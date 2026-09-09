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
    var TAB_COOKIE_NAME = "cmp-contentaisearch-tab";
    var TAB_RESULTS = "search-results";
    var TAB_AI = "ai-mode";

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

    // eslint-disable-next-line no-unused-vars
    function getCookie(name) {
        var match = document.cookie.match(new RegExp("(?:^|; )" + name + "=([^;]*)"));
        return match ? decodeURIComponent(match[1]) : null;
    }

    function setCookie(name, value) {
        var oneYearSeconds = 365 * 24 * 60 * 60;
        document.cookie = name + "=" + encodeURIComponent(value) +
            "; path=/; max-age=" + oneYearSeconds + "; SameSite=Lax; Secure";
    }

    function ContentAISearch(element) {
        this._element = element;
        this._cacheElements();
        this._resourcePath = this._resolveResourcePath();
        // Sightly renders boolean-typed attributes as bare (present when true, omitted
        // when false) rather than as the literal string "true" - hasAttribute is the
        // correct read here, matching the convention used elsewhere in this codebase
        // (e.g. data-cmp-data-layer-enabled), not a getAttribute() === "true" string
        // comparison, which would always evaluate to false against real HTL output.
        this._aiSearchModeEnabled = this._element.hasAttribute("data-cmp-ai-search-mode-enabled");
        this._resultsLayout = this._element.getAttribute("data-cmp-results-layout") === "list" ? "list" : "card";
        this._currentQuery = "";
        this._allResults = [];
        this._hasMore = false;
        this._sourceCursors = {};
        this._resultsRequestId = 0;
        this._genSearchRequestId = 0;
        this._pendingResultsQuery = null;
        this._pendingGenSearchQuery = null;
        this._activeTab = TAB_RESULTS;

        this._applyLayoutClass();
        this._syncLayoutButtons();
        if (this._aiSearchModeEnabled) {
            this._syncActiveTabFromDom();
        }

        if (this._elements.input) {
            this._elements.input.addEventListener("input", this._onInput.bind(this));
        }
        if (this._elements.clear) {
            this._elements.clear.addEventListener("click", this._onClearClick.bind(this));
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
        if (this._elements.tabSearchResults) {
            this._elements.tabSearchResults.addEventListener("click", this._onTabResultsClick.bind(this));
            this._elements.tabSearchResults.addEventListener("keydown", this._onTabKeydown.bind(this));
        }
        if (this._elements.tabAiMode) {
            this._elements.tabAiMode.addEventListener("click", this._onTabAiClick.bind(this));
            this._elements.tabAiMode.addEventListener("keydown", this._onTabKeydown.bind(this));
        }
    }

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

    // Reads whatever the HTL's synchronous pre-paint script already applied to the
    // DOM (aria-selected on the AI Mode tab) rather than re-reading the cookie here -
    // a single source of truth for "what's the initial tab" instead of two places
    // that could disagree.
    ContentAISearch.prototype._syncActiveTabFromDom = function() {
        if (this._elements.tabAiMode && this._elements.tabAiMode.getAttribute("aria-selected") === "true") {
            this._activeTab = TAB_AI;
        }
    };

    ContentAISearch.prototype._onTabResultsClick = function() {
        this._activateTab(TAB_RESULTS, true);
    };

    ContentAISearch.prototype._onTabAiClick = function() {
        this._activateTab(TAB_AI, true);
    };

    ContentAISearch.prototype._onTabKeydown = function(event) {
        if (event.key !== "ArrowLeft" && event.key !== "ArrowRight") {
            return;
        }
        event.preventDefault();
        var next = this._activeTab === TAB_RESULTS ? TAB_AI : TAB_RESULTS;
        this._activateTab(next, true);
        var nextElement = next === TAB_AI ? this._elements.tabAiMode : this._elements.tabSearchResults;
        if (nextElement) {
            nextElement.focus();
        }
    };

    ContentAISearch.prototype._activateTab = function(tab, persist) {
        if (tab === this._activeTab) {
            return;
        }
        this._activeTab = tab;
        var showAi = tab === TAB_AI;

        if (this._elements.tabAiMode) {
            this._elements.tabAiMode.setAttribute("aria-selected", showAi ? "true" : "false");
            this._elements.tabAiMode.setAttribute("tabindex", showAi ? "0" : "-1");
        }
        if (this._elements.tabSearchResults) {
            this._elements.tabSearchResults.setAttribute("aria-selected", showAi ? "false" : "true");
            this._elements.tabSearchResults.setAttribute("tabindex", showAi ? "-1" : "0");
        }
        toggleShow(this._elements.panelAiMode, showAi);
        toggleShow(this._elements.panelSearchResults, !showAi);

        if (persist) {
            setCookie(TAB_COOKIE_NAME, tab);
        }
    };

    ContentAISearch.prototype._onInput = function() {
        this._syncClearButton();
        // Backspacing a query down to empty (without submitting or hitting the
        // explicit clear button) should also clear stale results/summary - otherwise
        // an emptied field is left showing results for whatever was last submitted.
        if (!this._elements.input.value) {
            this._clearResults();
        }
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
        this._clearResults();
    };

    ContentAISearch.prototype._onLoadMore = function() {
        this._runLoadMore();
    };

    ContentAISearch.prototype._onRetry = function() {
        var query = this._elements.input.value;
        if (this._pendingGenSearchQuery === query) {
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
            return;
        }
        this._currentQuery = query;
        this._runResultsSearch(query);
        if (this._aiSearchModeEnabled) {
            this._runGenSearch(query);
        }
    };

    ContentAISearch.prototype._clearResults = function() {
        this._currentQuery = "";
        this._allResults = [];
        this._hasMore = false;
        this._sourceCursors = {};
        this._resultsRequestId++;
        this._genSearchRequestId++;
        this._pendingResultsQuery = null;
        this._pendingGenSearchQuery = null;
        if (this._elements.results) {
            this._elements.results.innerHTML = "";
        }
        toggleShow(this._elements.resultsSection, false);
        toggleShow(this._elements.loadMore, false);
        toggleShow(this._elements.summary, false);
        toggleShow(this._elements.summaryLoading, false);
        toggleShow(this._elements.error, false);
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
        var requestId = ++this._resultsRequestId;
        this._pendingResultsQuery = query;
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
                // A failed page fetch shouldn't leave "Load More" stuck disabled forever.
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
                    toggleShow(self._elements.error, true);
                });
            });
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
                // Not toggleShow(): it sets an inline style="display: block", which
                // (being more specific than a stylesheet class rule) would permanently
                // clobber the __card-description/__row-description CSS's
                // "display: -webkit-box", silently breaking the line-clamp truncation.
                // Just clearing "hidden" lets that stylesheet display value apply.
                descriptionNode.removeAttribute("hidden");
            } else {
                descriptionNode.textContent = "";
                descriptionNode.setAttribute("hidden", "hidden");
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
        this._elements.results.classList.remove("cmp-contentaisearch__results--refresh");
        void this._elements.results.offsetWidth;
        this._elements.results.classList.add("cmp-contentaisearch__results--refresh");

        toggleShow(this._elements.resultsSection, true);
        toggleShow(this._elements.loadMore, this._hasMore);
    };

    ContentAISearch.prototype._renderSummary = function(data, requestId) {
        var fullText = data.result || "";
        var hits = data.hits || [];

        if (requestId !== this._genSearchRequestId) {
            return;
        }

        if (this._elements.sources) {
            this._elements.sources.innerHTML = this._generateSourceItems(hits);
        }
        this._elements.summaryText.innerHTML = this._renderMarkdownSummary(fullText);
        toggleShow(this._elements.summary, true);
    };

    function escapeHtml(str) {
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

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
