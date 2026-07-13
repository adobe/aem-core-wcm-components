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
    var DELAY = 300;
    var LOADING_DISPLAY_DELAY = 300;

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
        this._timeout = null;
        this._currentQuery = "";
        this._allResults = [];
        this._hasMore = false;
        this._sourceCursors = {};

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
            this._elements.form.addEventListener("submit", function(event) {
                event.preventDefault();
            });
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

    ContentAISearch.prototype._onInput = function() {
        var self = this;
        this._syncClearButton();
        clearTimeout(this._timeout);
        this._timeout = setTimeout(function() {
            self._runQuery();
        }, DELAY);
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

    ContentAISearch.prototype._onToggleChange = function() {
        this._genSearchEnabled = this._elements.toggle.checked;
        if (!this._genSearchEnabled) {
            toggleShow(this._elements.summary, false);
            toggleShow(this._elements.error, false);
            this._setSummaryLoading(false);
        }
        this._runQuery();
    };

    ContentAISearch.prototype._onLoadMore = function() {
        this._runLoadMore();
    };

    ContentAISearch.prototype._onRetry = function() {
        this._runGenSearch(this._elements.input.value);
    };

    ContentAISearch.prototype._runQuery = function() {
        var query = this._elements.input.value;
        this._currentQuery = query;
        if (!query) {
            this._clearResults();
            return;
        }
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
        this._setFieldLoading(true);
        var url = this._resourcePath + ".search.json?q=" + encodeURIComponent(query);
        this._fetchJson(url)
            .then(function(data) {
                self._storeResults(data);
                self._renderResults();
            })
            .catch(function() {
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
        if (this._elements.loadMore) {
            this._elements.loadMore.disabled = true;
        }
        var url = this._resourcePath + ".search.json?q=" + encodeURIComponent(this._currentQuery) +
            "&cursors=" + encodeURIComponent(JSON.stringify(this._sourceCursors));
        this._fetchJson(url)
            .then(function(data) {
                self._storeResults(data, true);
                self._renderResults();
            })
            .then(function() {
                if (self._elements.loadMore) {
                    self._elements.loadMore.disabled = false;
                }
            });
    };

    ContentAISearch.prototype._runGenSearch = function(query) {
        var self = this;
        var genSearchStart = Date.now();
        toggleShow(this._elements.error, false);
        toggleShow(this._elements.summary, false);
        this._setSummaryLoading(true);
        if (this._elements.retry) {
            toggleShow(this._elements.retry, true);
        }
        this._fetchJson(this._resourcePath + ".gensearch.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                self._hideSummaryLoading(genSearchStart, function() {
                    self._renderSummary(data);
                });
            })
            .catch(function() {
                self._hideSummaryLoading(genSearchStart, function() {
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
        if (metadata.url) {
            return this._labelFromUrl(metadata.url);
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
        if (metadata.url) {
            return this._labelFromUrl(metadata.url);
        }
        return hit.id || "";
    };

    ContentAISearch.prototype._labelFromUrl = function(url) {
        try {
            var parsed = new URL(url, window.location.origin);
            var segments = parsed.pathname.split("/").filter(Boolean);
            if (segments.length) {
                return decodeURIComponent(segments[segments.length - 1]).replace(/[-_]/g, " ");
            }
        } catch (e) {
            // fall through
        }
        return url;
    };

    ContentAISearch.prototype._populateItemNode = function(root, item) {
        var metadata = this._getItemMetadata(item);
        var url = metadata.url;
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
            var url = hit.metadata && hit.metadata.url;
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

        toggleShow(this._elements.resultsSection, true);
        toggleShow(this._elements.loadMore, this._hasMore);
    };

    ContentAISearch.prototype._renderSummary = function(data) {
        this._elements.summaryText.textContent = data.result || "";
        var hits = data.hits || [];
        this._elements.sources.innerHTML = this._generateSourceItems(hits);
        toggleShow(this._elements.summary, true);
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
