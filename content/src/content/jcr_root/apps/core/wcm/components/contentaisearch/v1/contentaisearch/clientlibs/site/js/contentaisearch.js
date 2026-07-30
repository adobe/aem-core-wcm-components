/*******************************************************************************
 * Copyright 2026 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function() {
    "use strict";

    var NS = "cmp";
    var IS = "contentaisearch";
    var LOADING_DISPLAY_DELAY = 300;
    var REVEAL_WORD_INTERVAL_MS = 12;

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]',
        item: {
            self: "[data-" + NS + "-hook-" + IS + '="item"]',
            title: "[data-" + NS + "-hook-" + IS + '="itemTitle"]',
            description: "[data-" + NS + "-hook-" + IS + '="itemDescription"]',
            image: "[data-" + NS + "-hook-" + IS + '="itemImage"]',
            imagePlaceholder: "[data-" + NS + "-hook-" + IS + '="itemImagePlaceholder"]'
        },
        source: {
            link: "[data-" + NS + "-hook-" + IS + '="sourceLink"]',
            text: "[data-" + NS + "-hook-" + IS + '="sourceText"]'
        }
    };

    function toggleShow(element, show) {
        if (element) {
            if (show !== false) {
                element.style.display = "block";
                element.removeAttribute("hidden");
            } else {
                element.style.display = "none";
                element.setAttribute("hidden", "hidden");
            }
        }
    }

    function ContentAISearch(element) {
        this._element = element;
        this._cacheElements();
        this._resourcePath = this._resolveResourcePath();
        this._genSearchErrorFallback = this._element.getAttribute("data-cmp-gensearch-error-fallback") || "RESULTS_ONLY";
        this._genSearchEnabled = this._resolveInitialGenSearchEnabled();
        this._resultsLayout = this._element.getAttribute("data-cmp-results-layout") === "list" ? "list" : "card";
        this._i18n = this._parseI18n();
        this._revealTimer = null;
        this._currentQuery = "";
        this._allResults = [];
        this._hasMore = false;
        this._sourceCursors = {};
        // Monotonic per-endpoint request counters, plus which query (if any)
        // is currently in flight for that endpoint. Replaces comparing
        // against _currentQuery alone: two requests for the identical query
        // text (e.g. pressing Enter twice) are otherwise indistinguishable
        // from each other by a string comparison, so neither looks "stale"
        // to the other no matter which one resolves last.
        this._resultsRequestId = 0;
        this._genSearchRequestId = 0;
        this._pendingResultsQuery = null;
        this._pendingGenSearchQuery = null;

        this._applyLayoutClass();
        this._syncLayoutButtons();

        if (this._elements.input) {
            this._elements.input.addEventListener("input", this._onInput.bind(this));
        }
        if (this._elements.clear) {
            this._elements.clear.addEventListener("click", this._onClearClick.bind(this));
        }
        if (this._elements.toggle) {
            this._elements.toggle.addEventListener("change", this._onToggleChange.bind(this));
        }
        if (this._elements.retry) {
            this._elements.retry.addEventListener("click", this._onRetry.bind(this));
        }
        if (this._elements.layoutCard) {
            this._elements.layoutCard.addEventListener("click", this._onLayoutCard.bind(this));
        }
        if (this._elements.layoutList) {
            this._elements.layoutList.addEventListener("click", this._onLayoutList.bind(this));
        }
        if (this._elements.loadMore) {
            this._elements.loadMore.addEventListener("click", this._onLoadMore.bind(this));
        }
        if (this._elements.form) {
            this._elements.form.addEventListener("submit", this._onFormSubmit.bind(this));
        }
    }

    ContentAISearch.prototype._parseI18n = function() {
        var raw = this._element.getAttribute("data-i18n-messages");
        if (!raw) {
            return {};
        }
        try {
            return JSON.parse(raw);
        } catch (e) {
            return {};
        }
    };

    ContentAISearch.prototype._msg = function(key, fallback) {
        return this._i18n[key] || fallback || key;
    };

    ContentAISearch.prototype._cacheElements = function() {
        this._elements = {};
        var hooks = this._element.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");
        for (var i = 0; i < hooks.length; i++) {
            var hook = hooks[i];
            var key = hook.dataset[NS + "Hook" + IS.charAt(0).toUpperCase() + IS.slice(1)];
            this._elements[key] = hook;
        }
    };

    ContentAISearch.prototype._resolveResourcePath = function() {
        return this._element.getAttribute("data-cmp-resource-path");
    };

    ContentAISearch.prototype._resolveInitialGenSearchEnabled = function() {
        var toggleVisible = this._element.getAttribute("data-cmp-gensearch-toggle-visible");
        var enabledDefault = this._element.getAttribute("data-cmp-gensearch-enabled-default");
        if (toggleVisible === "false") {
            return enabledDefault === "true";
        }
        return this._elements.toggle ? this._elements.toggle.checked : false;
    };

    ContentAISearch.prototype._applyLayoutClass = function() {
        this._element.classList.remove("cmp-contentaisearch--card", "cmp-contentaisearch--list");
        this._element.classList.add(this._resultsLayout === "list" ? "cmp-contentaisearch--list" : "cmp-contentaisearch--card");
    };

    ContentAISearch.prototype._syncLayoutButtons = function() {
        var isList = this._resultsLayout === "list";
        if (this._elements.layoutCard) {
            this._elements.layoutCard.setAttribute("aria-pressed", isList ? "false" : "true");
        }
        if (this._elements.layoutList) {
            this._elements.layoutList.setAttribute("aria-pressed", isList ? "true" : "false");
        }
    };

    ContentAISearch.prototype._getActiveItemTemplate = function() {
        return this._resultsLayout === "list" ? this._elements.itemTemplateList : this._elements.itemTemplateCard;
    };

    ContentAISearch.prototype._onLayoutCard = function() {
        if (this._resultsLayout === "card") {
            return;
        }
        this._resultsLayout = "card";
        this._applyLayoutClass();
        this._syncLayoutButtons();
        this._renderResults();
    };

    ContentAISearch.prototype._onLayoutList = function() {
        if (this._resultsLayout === "list") {
            return;
        }
        this._resultsLayout = "list";
        this._applyLayoutClass();
        this._syncLayoutButtons();
        this._renderResults();
    };

    // Search runs only on an explicit trigger - form submit (Enter, or a
    // mobile keyboard's "Go"/"Search" action, both of which fire the form's
    // submit event same as a real submit button would) or the AI-answer
    // toggle changing - never on typing itself. A debounced search-as-you-
    // type fires a fresh, often-incomplete query on every brief pause,
    // which both re-renders the results/summary repeatedly while the user
    // is still typing (a visible flicker) and shows the loading indicators
    // for every one of those in-between queries, giving the impression a
    // search is running when the user hasn't asked for one yet.
    ContentAISearch.prototype._onInput = function() {
        this._syncClearButton();
    };

    ContentAISearch.prototype._onFormSubmit = function(event) {
        event.preventDefault();
        this._runQuery();
    };

    ContentAISearch.prototype._syncClearButton = function() {
        var hasValue = this._elements.input && this._elements.input.value.length > 0;
        toggleShow(this._elements.clear, hasValue);
    };

    ContentAISearch.prototype._onClearClick = function() {
        if (this._elements.input) {
            this._elements.input.value = "";
        }
        toggleShow(this._elements.clear, false);
        toggleShow(this._elements.loadingIndicator, false);
        toggleShow(this._elements.icon, true);
        this._clearResults();
    };

    // Only ever touches the gensearch/summary state - toggling the
    // AI-answer switch has nothing to do with the plain results list, so
    // unlike the old behavior (which always re-ran _runQuery, re-firing a
    // full .search.json call along with it) this no longer re-fetches
    // results that haven't changed.
    ContentAISearch.prototype._onToggleChange = function() {
        this._genSearchEnabled = this._elements.toggle.checked;
        if (!this._genSearchEnabled) {
            // Invalidate any gensearch request still in flight so a
            // late-arriving answer can't reopen the summary the user just
            // explicitly turned off.
            this._genSearchRequestId++;
            this._pendingGenSearchQuery = null;
            if (this._revealTimer) {
                clearTimeout(this._revealTimer);
                this._revealTimer = null;
            }
            toggleShow(this._elements.summary, false);
            toggleShow(this._elements.error, false);
            this._setSummaryLoading(false);
            return;
        }
        var query = this._elements.input.value;
        if (!query || query !== this._currentQuery) {
            // Nothing has been submitted yet, or the field has unsubmitted
            // edits - wait for an explicit submit rather than searching just
            // because the toggle moved.
            return;
        }
        if (this._pendingGenSearchQuery === query) {
            return;
        }
        this._runGenSearch(query);
    };

    ContentAISearch.prototype._onLoadMore = function() {
        this._runLoadMore();
    };

    ContentAISearch.prototype._onRetry = function() {
        var query = this._elements.input.value;
        if (this._pendingGenSearchQuery === query) {
            // Already retrying this exact query - ignore a repeated click
            // instead of firing a second, redundant request.
            return;
        }
        this._runGenSearch(query);
    };

    ContentAISearch.prototype._runQuery = function() {
        var query = this._elements.input.value;
        if (!query) {
            this._currentQuery = query;
            this._clearResults();
            return;
        }
        if (query === this._currentQuery &&
            (this._pendingResultsQuery === query || this._pendingGenSearchQuery === query)) {
            // The identical query is already in flight (e.g. Enter pressed
            // twice, or mashed while waiting) - ignore the duplicate submit
            // rather than firing a second round-trip. The in-flight request
            // still resolves normally; submitting again once it's done
            // fires a fresh one as usual.
            return;
        }
        this._currentQuery = query;
        this._runResultsSearch(query);
        if (this._genSearchEnabled) {
            this._runGenSearch(query);
        } else {
            toggleShow(this._elements.summary, false);
            this._setSummaryLoading(false);
        }
    };

    ContentAISearch.prototype._clearResults = function() {
        this._currentQuery = "";
        this._allResults = [];
        this._hasMore = false;
        this._sourceCursors = {};
        // Invalidate anything still in flight - its response, if any,
        // resolves against a request ID nothing compares equal to anymore.
        this._resultsRequestId++;
        this._genSearchRequestId++;
        this._pendingResultsQuery = null;
        this._pendingGenSearchQuery = null;
        if (this._revealTimer) {
            clearTimeout(this._revealTimer);
            this._revealTimer = null;
        }
        if (this._elements.sources) {
            this._elements.sources.style.visibility = "";
        }
        if (this._elements.results) {
            this._elements.results.innerHTML = "";
        }
        toggleShow(this._elements.resultsSection, false);
        toggleShow(this._elements.loadMore, false);
        toggleShow(this._elements.summary, false);
        toggleShow(this._elements.summaryLoading, false);
        toggleShow(this._elements.error, false);
        toggleShow(this._elements.loadingIndicator, false);
        toggleShow(this._elements.icon, true);
    };

    ContentAISearch.prototype._setFieldLoading = function(loading) {
        toggleShow(this._elements.loadingIndicator, loading);
        toggleShow(this._elements.icon, !loading);
    };

    ContentAISearch.prototype._setSummaryLoading = function(show) {
        toggleShow(this._elements.summaryLoading, show);
        if (this._elements.summaryLoading) {
            if (show) {
                this._elements.summaryLoading.setAttribute("aria-busy", "true");
            } else {
                this._elements.summaryLoading.removeAttribute("aria-busy");
            }
        }
    };

    ContentAISearch.prototype._hideSummaryLoading = function(startTime, callback) {
        var elapsed = Date.now() - startTime;
        var delay = Math.max(0, LOADING_DISPLAY_DELAY - elapsed);
        var self = this;
        setTimeout(function() {
            self._setSummaryLoading(false);
            if (callback) {
                callback();
            }
        }, delay);
    };

    ContentAISearch.prototype._runResultsSearch = function(query) {
        var self = this;
        var searchStart = Date.now();
        // Shared with _runLoadMore - a fresh search and a load-more append
        // both write to _allResults/_hasMore/_sourceCursors, so whichever of
        // the two is issued more recently needs to invalidate the other one's
        // still-in-flight response, not just responses to a different query.
        var requestId = ++this._resultsRequestId;
        this._pendingResultsQuery = query;
        this._setFieldLoading(true);
        var url = this._resourcePath + ".search.json?q=" + encodeURIComponent(query);
        this._fetchJson(url)
            .then(function(data) {
                if (requestId !== self._resultsRequestId) {
                    return;
                }
                self._storeResults(data);
                self._renderResults();
            })
            .catch(function() {
                if (requestId !== self._resultsRequestId) {
                    return;
                }
                self._allResults = [];
                self._hasMore = false;
                self._sourceCursors = {};
                if (self._elements.results) {
                    self._elements.results.innerHTML = "";
                }
                toggleShow(self._elements.resultsSection, false);
                toggleShow(self._elements.loadMore, false);
            })
            .then(function() {
                if (requestId !== self._resultsRequestId) {
                    return;
                }
                self._pendingResultsQuery = null;
                var elapsed = Date.now() - searchStart;
                var delay = Math.max(0, LOADING_DISPLAY_DELAY - elapsed);
                setTimeout(function() {
                    self._setFieldLoading(false);
                }, delay);
            });
    };

    ContentAISearch.prototype._storeResults = function(data, append) {
        var results = (data && data.results) || [];
        if (!append) {
            this._allResults = results;
        } else {
            var existingIds = {};
            var self = this;
            this._allResults.forEach(function(result) {
                if (result && result.id) {
                    existingIds[result.id] = true;
                }
            });
            results.forEach(function(result) {
                if (result && result.id && !existingIds[result.id]) {
                    self._allResults.push(result);
                    existingIds[result.id] = true;
                }
            });
            this._allResults.sort(function(a, b) {
                return (b.score || 0) - (a.score || 0);
            });
        }
        this._hasMore = !!(data && data.hasMore);
        this._sourceCursors = (data && data.sourceCursors) || {};
    };

    ContentAISearch.prototype._runLoadMore = function() {
        if (!this._hasMore || !this._currentQuery) {
            return;
        }
        var self = this;
        var query = this._currentQuery;
        // Shares _resultsRequestId with _runResultsSearch - if a fresh
        // search is issued while this append is still in flight (or vice
        // versa), whichever fired later wins and the other's response is
        // ignored, instead of racing to write _allResults/_sourceCursors.
        var requestId = ++this._resultsRequestId;
        if (this._elements.loadMore) {
            this._elements.loadMore.disabled = true;
        }
        var url = this._resourcePath + ".search.json?q=" + encodeURIComponent(query) +
            "&cursors=" + encodeURIComponent(JSON.stringify(this._sourceCursors));
        this._fetchJson(url)
            .then(function(data) {
                if (requestId !== self._resultsRequestId) {
                    return;
                }
                self._storeResults(data, true);
                self._renderResults();
            })
            .catch(function() {
                // A failed page fetch shouldn't leave "Load More" stuck
                // disabled forever - keep the results that are already
                // shown and let the user try again.
            })
            .then(function() {
                if (requestId !== self._resultsRequestId) {
                    return;
                }
                if (self._elements.loadMore) {
                    self._elements.loadMore.disabled = false;
                }
            });
    };

    ContentAISearch.prototype._runGenSearch = function(query) {
        var self = this;
        var genSearchStart = Date.now();
        var requestId = ++this._genSearchRequestId;
        this._pendingGenSearchQuery = query;
        toggleShow(this._elements.error, false);
        toggleShow(this._elements.summary, false);
        this._setSummaryLoading(true);
        if (this._elements.retry) {
            toggleShow(this._elements.retry, true);
        }
        this._fetchJson(this._resourcePath + ".gensearch.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                if (requestId !== self._genSearchRequestId) {
                    return;
                }
                self._hideSummaryLoading(genSearchStart, function() {
                    if (requestId !== self._genSearchRequestId) {
                        return;
                    }
                    self._pendingGenSearchQuery = null;
                    self._renderSummary(data, requestId);
                });
            })
            .catch(function() {
                if (requestId !== self._genSearchRequestId) {
                    return;
                }
                self._hideSummaryLoading(genSearchStart, function() {
                    if (requestId !== self._genSearchRequestId) {
                        return;
                    }
                    self._pendingGenSearchQuery = null;
                    self._handleGenSearchError();
                });
            });
    };

    ContentAISearch.prototype._handleGenSearchError = function() {
        if (this._genSearchErrorFallback === "SHOW_ERROR") {
            if (this._elements.retry) {
                toggleShow(this._elements.retry, true);
            }
            toggleShow(this._elements.error, true);
            return;
        }
        if (this._genSearchErrorFallback === "SHOW_ERROR_MESSAGE") {
            if (this._elements.retry) {
                toggleShow(this._elements.retry, false);
            }
            toggleShow(this._elements.error, true);
            return;
        }
        toggleShow(this._elements.error, false);
    };

    ContentAISearch.prototype._fetchJson = function(url) {
        return fetch(url).then(function(response) {
            if (!response.ok) {
                throw new Error("Request to " + url + " failed with status " + response.status);
            }
            return response.json();
        });
    };

    ContentAISearch.prototype._getItemMetadata = function(item) {
        return (item && item.data && item.data.metadata) || {};
    };

    // Content AI's own acquisition indexing convention stores the crawled page's
    // address as "source" (metadata.source, duplicated at data.source) - "url" is
    // only ever present for content sources that map a differently-named field.
    // Every resolver below checks metadata.url first for that case, then falls
    // back to the acquisition convention.
    function resolveMetadataUrl(metadata, fallbackUrl) {
        var m = metadata || {};
        return m.url || m.source || fallbackUrl || "";
    }

    ContentAISearch.prototype._resolveItemLabel = function(item) {
        if (!item) {
            return "";
        }
        var data = item.data || {};
        var metadata = data.metadata || {};
        if (metadata.title) {
            return metadata.title;
        }
        if (data.title) {
            return data.title;
        }
        if (data.name) {
            return data.name;
        }
        if (metadata.description) {
            return metadata.description;
        }
        if (data.text) {
            var headingMatch = String(data.text).match(/^#\s+(.+)$/m);
            if (headingMatch) {
                return headingMatch[1].trim();
            }
        }
        var url = resolveMetadataUrl(metadata, data.source);
        if (url) {
            return this._labelFromUrl(url);
        }
        return item.id || "";
    };

    ContentAISearch.prototype._resolveItemDescription = function(item) {
        var metadata = this._getItemMetadata(item);
        if (metadata.description) {
            return metadata.description;
        }
        var data = (item && item.data) || {};
        if (data.text) {
            var text = String(data.text).replace(/^#\s+.+\n+/m, "").trim();
            text = text.replace(/\[([^\]]+)\]\([^)]+\)/g, "$1");
            text = text.replace(/[*_`#]/g, "");
            return text;
        }
        return "";
    };

    ContentAISearch.prototype._resolveItemImage = function(item) {
        var metadata = this._getItemMetadata(item);
        var image = metadata.image;
        if (image && this._isSafeUrl(image)) {
            return image;
        }
        return "";
    };

    ContentAISearch.prototype._resolveHitLabel = function(hit) {
        if (!hit) {
            return "";
        }
        var metadata = hit.metadata || {};
        if (metadata.title) {
            return metadata.title;
        }
        var url = resolveMetadataUrl(metadata, hit.source);
        if (url) {
            return this._labelFromUrl(url);
        }
        return hit.id || "";
    };

    // Only ever used as a last-resort fallback, once metadata.title/data.title/
    // data.name have all already come up empty - so there's no risk of this
    // clobbering an authored title, just turning a bare URL into something
    // readable: strip a trailing page extension, turn dashes/underscores into
    // spaces, and title-case the result (e.g. "ski-touring-mont-blanc.html"
    // becomes "Ski Touring Mont Blanc" instead of "ski touring mont blanc.html").
    ContentAISearch.prototype._labelFromUrl = function(url) {
        try {
            var parsed = new URL(url, window.location.origin);
            var segments = parsed.pathname.split("/").filter(Boolean);
            if (segments.length) {
                var slug = decodeURIComponent(segments[segments.length - 1])
                    .replace(/\.(html?|php|aspx?)$/i, "")
                    .replace(/[-_]+/g, " ")
                    .trim();
                if (slug) {
                    return slug.replace(/\S+/g, function(word) {
                        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
                    });
                }
            }
        } catch (e) {
            // fall through
        }
        return url;
    };

    ContentAISearch.prototype._populateItemNode = function(root, item) {
        var metadata = this._getItemMetadata(item);
        var url = resolveMetadataUrl(metadata, item && item.data && item.data.source);
        var title = this._resolveItemLabel(item);
        var description = this._resolveItemDescription(item);
        var image = this._resolveItemImage(item);
        var itemRoot = root.querySelector(selectors.item.self);
        var titleNode = root.querySelector(selectors.item.title);
        var descriptionNode = root.querySelector(selectors.item.description);
        var imageNode = root.querySelector(selectors.item.image);
        var placeholderNode = root.querySelector(selectors.item.imagePlaceholder);

        if (titleNode) {
            titleNode.textContent = title;
        }
        if (descriptionNode) {
            if (description) {
                descriptionNode.textContent = description;
                toggleShow(descriptionNode, true);
            } else {
                descriptionNode.textContent = "";
                toggleShow(descriptionNode, false);
            }
        }
        if (imageNode && placeholderNode) {
            if (image) {
                imageNode.setAttribute("src", image);
                toggleShow(imageNode, true);
                toggleShow(placeholderNode, false);
            } else {
                imageNode.removeAttribute("src");
                toggleShow(imageNode, false);
                toggleShow(placeholderNode, true);
            }
        }
        if (itemRoot) {
            if (url && this._isSafeUrl(url)) {
                itemRoot.setAttribute("href", url);
            } else {
                itemRoot.removeAttribute("href");
                if (itemRoot.tagName === "A") {
                    var article = document.createElement(itemRoot.classList.contains("cmp-contentaisearch__row") ? "div" : "article");
                    article.className = itemRoot.className;
                    while (itemRoot.firstChild) {
                        article.appendChild(itemRoot.firstChild);
                    }
                    itemRoot.parentNode.replaceChild(article, itemRoot);
                }
            }
        }
    };

    ContentAISearch.prototype._generateResultItems = function(results) {
        var self = this;
        var html = "";
        var template = this._getActiveItemTemplate();
        if (!template) {
            return html;
        }
        results.forEach(function(item) {
            var el = document.createElement("div");
            el.innerHTML = template.innerHTML;
            self._populateItemNode(el, item);
            html += el.innerHTML;
        });
        return html;
    };

    ContentAISearch.prototype._generateSourceItems = function(hits) {
        var self = this;
        var html = "";
        if (!this._elements.sourceTemplate) {
            return html;
        }
        hits.forEach(function(hit) {
            var url = resolveMetadataUrl(hit.metadata, hit.source);
            var label = self._resolveHitLabel(hit);
            var el = document.createElement("div");
            el.innerHTML = self._elements.sourceTemplate.innerHTML;
            var linkNode = el.querySelector(selectors.source.link);
            var textNode = el.querySelector(selectors.source.text);
            if (url && self._isSafeUrl(url) && linkNode) {
                linkNode.setAttribute("href", url);
                linkNode.textContent = label;
                toggleShow(linkNode, true);
                toggleShow(textNode, false);
            } else if (textNode) {
                textNode.textContent = label;
                toggleShow(textNode, true);
                toggleShow(linkNode, false);
            }
            html += el.innerHTML;
        });
        return html;
    };

    ContentAISearch.prototype._renderResults = function() {
        if (!this._allResults.length) {
            this._elements.results.innerHTML = "";
            toggleShow(this._elements.resultsSection, false);
            toggleShow(this._elements.loadMore, false);
            return;
        }

        this._elements.results.innerHTML = this._generateResultItems(this._allResults);
        // The whole list is replaced in one synchronous write, so there's no
        // display:none/block toggle for a CSS *animation* to key off of the
        // way the summary card's show/hide has - remove-then-re-add the
        // class instead (with a forced reflow in between) so the animation
        // restarts on every render, including a Load More append.
        this._elements.results.classList.remove("cmp-contentaisearch__results--refresh");
        void this._elements.results.offsetWidth;
        this._elements.results.classList.add("cmp-contentaisearch__results--refresh");

        toggleShow(this._elements.resultsSection, true);
        toggleShow(this._elements.loadMore, this._hasMore);
    };

    // Reveals the answer word by word rather than painting it in one go. The
    // full answer is already in hand (this runs once the blocking gensearch
    // response has arrived) - a genuine reduction in time-to-first-word
    // would need the API's SSE streaming endpoint relayed through a servlet
    // fan-out across every configured content source, a larger change; this
    // is the interim, purely cosmetic improvement. Every tick re-renders
    // through _renderMarkdownSummary (never a raw, unstyled Text node), so
    // paragraph/list spacing is already in place from the first word instead
    // of appearing all at once on the last one.
    //
    // A newer query starting its own _renderSummary call cancels this one's
    // timer (below), but that alone isn't enough: a still-ticking reveal for
    // an older query can otherwise run to completion - and sit there fully
    // rendered - before a slower-arriving newer response replaces it, the
    // same stale-response race _runResultsSearch/_runGenSearch already guard
    // against at the network level. Each tick re-checks requestId against
    // _genSearchRequestId (not query text - two requests for the identical
    // query, e.g. a double Enter, are otherwise indistinguishable) so a
    // reveal superseded by a newer request stops silently instead of
    // finishing or restarting.
    ContentAISearch.prototype._renderSummary = function(data, requestId) {
        var self = this;
        var fullText = data.result || "";
        var hits = data.hits || [];
        var tokens = fullText.split(/(\s+)/);
        var idx = 0;

        if (this._revealTimer) {
            clearTimeout(this._revealTimer);
            this._revealTimer = null;
        }

        if (this._elements.sources) {
            // Sources are generated up front (same as the summary text
            // itself) but held back until the reveal finishes, rather than
            // showing them - momentarily out of sync - before the answer
            // they support has even finished appearing.
            this._elements.sources.innerHTML = this._generateSourceItems(hits);
            this._elements.sources.style.visibility = "hidden";
        }
        toggleShow(this._elements.summary, true);

        function tick() {
            if (requestId !== self._genSearchRequestId) {
                self._revealTimer = null;
                return;
            }
            idx++;
            self._elements.summaryText.innerHTML = self._renderMarkdownSummary(tokens.slice(0, idx).join(""));
            if (idx < tokens.length) {
                self._revealTimer = setTimeout(tick, REVEAL_WORD_INTERVAL_MS);
            } else {
                self._revealTimer = null;
                if (self._elements.sources) {
                    self._elements.sources.style.visibility = "";
                }
            }
        }

        tick();
    };

    function escapeHtml(str) {
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    // Content AI returns the generative answer as Markdown (bold, links, bullet lists).
    // Renders a minimal, safe subset of it: text is HTML-escaped first, then only
    // **bold**, [text](url) links (http/https only, re-validated via _isSafeUrl),
    // and "- " bullet lists are turned into markup; anything else stays plain text.
    ContentAISearch.prototype._renderMarkdownInline = function(text) {
        var self = this;
        var html = escapeHtml(text);
        html = html.replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>");
        html = html.replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, function(match, label, url) {
            return self._isSafeUrl(url) ? '<a href="' + url + '">' + label + "</a>" : label;
        });
        return html;
    };

    ContentAISearch.prototype._renderMarkdownSummary = function(text) {
        var lines = String(text || "").split(/\r?\n/);
        var html = "";
        var listOpen = false;
        var i;
        var trimmed;
        for (i = 0; i < lines.length; i++) {
            trimmed = lines[i].replace(/^\s+/, "");
            if (/^[-*]\s+/.test(trimmed)) {
                if (!listOpen) {
                    html += "<ul>";
                    listOpen = true;
                }
                html += "<li>" + this._renderMarkdownInline(trimmed.replace(/^[-*]\s+/, "")) + "</li>";
            } else {
                if (listOpen) {
                    html += "</ul>";
                    listOpen = false;
                }
                if (trimmed.length > 0) {
                    html += "<p>" + this._renderMarkdownInline(trimmed) + "</p>";
                }
            }
        }
        if (listOpen) {
            html += "</ul>";
        }
        return html;
    };

    function stripAsciiControlsAndWhitespaceForSchemeCheck(str) {
        var out = "";
        var i;
        var ch;
        var c;
        for (i = 0; i < str.length; i++) {
            ch = str.charAt(i);
            c = str.charCodeAt(i);
            if (c <= 31 || c === 127) {
                continue;
            }
            if (/\s/.test(ch)) {
                continue;
            }
            out += ch;
        }
        return out;
    }

    ContentAISearch.prototype._isSafeUrl = function(url) {
        if (!url) {
            return false;
        }
        var sanitized = stripAsciiControlsAndWhitespaceForSchemeCheck(String(url));
        if (/^https?:\/\//i.test(sanitized)) {
            return true;
        }
        return !/^[a-z][a-z0-9+.-]*:/i.test(sanitized);
    };

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new ContentAISearch(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
})();
