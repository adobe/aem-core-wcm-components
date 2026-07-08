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

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]'
    };

    function toggleShow(element, show) {
        if (element) {
            if (show) {
                element.removeAttribute("hidden");
            } else {
                element.setAttribute("hidden", "hidden");
            }
        }
    }

    function ContentAISearch(element) {
        this._element = element;
        this._cacheElements();
        this._resourcePath = this._resolveResourcePath();
        this._genSearchEnabled = this._elements.toggle ? this._elements.toggle.checked : false;
        this._timeout = null;

        this._elements.input.addEventListener("input", this._onInput.bind(this));
        if (this._elements.toggle) {
            this._elements.toggle.addEventListener("change", this._onToggleChange.bind(this));
        }
        if (this._elements.retry) {
            this._elements.retry.addEventListener("click", this._onRetry.bind(this));
        }
        if (this._elements.form) {
            this._elements.form.addEventListener("submit", function(event) {
                event.preventDefault();
            });
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
        // The component's own JCR resource path, rendered by the HTL as data-cmp-resource-path.
        // Used to build the .search.json / .gensearch.json selector URLs against the servlets
        // bound to this component's resource type — correct even with multiple instances of
        // this component on the same page, since each instance's root element carries its own path.
        return this._element.getAttribute("data-cmp-resource-path");
    };

    ContentAISearch.prototype._onInput = function() {
        var self = this;
        clearTimeout(this._timeout);
        this._timeout = setTimeout(function() {
            self._runQuery();
        }, DELAY);
    };

    ContentAISearch.prototype._onToggleChange = function() {
        this._genSearchEnabled = this._elements.toggle.checked;
        if (!this._genSearchEnabled) {
            toggleShow(this._elements.summary, false);
            toggleShow(this._elements.error, false);
        }
        this._runQuery();
    };

    ContentAISearch.prototype._onRetry = function() {
        this._runGenSearch(this._elements.input.value);
    };

    ContentAISearch.prototype._runQuery = function() {
        var query = this._elements.input.value;
        if (!query) {
            this._clearResults();
            return;
        }
        this._runResultsSearch(query);
        if (this._genSearchEnabled) {
            this._runGenSearch(query);
        }
    };

    ContentAISearch.prototype._clearResults = function() {
        this._elements.results.innerHTML = "";
        toggleShow(this._elements.summary, false);
        toggleShow(this._elements.error, false);
    };

    ContentAISearch.prototype._runResultsSearch = function(query) {
        var self = this;
        toggleShow(this._elements.loadingIndicator, true);
        this._fetchJson(this._resourcePath + ".search.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                self._renderResults(data);
            })
            .catch(function() {
                self._elements.results.innerHTML = "";
            })
            .then(function() {
                toggleShow(self._elements.loadingIndicator, false);
            });
    };

    ContentAISearch.prototype._runGenSearch = function(query) {
        var self = this;
        toggleShow(this._elements.error, false);
        toggleShow(this._elements.summary, false);
        this._fetchJson(this._resourcePath + ".gensearch.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                self._renderSummary(data);
            })
            .catch(function() {
                toggleShow(self._elements.error, true);
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

    ContentAISearch.prototype._renderResults = function(data) {
        var results = (data && data.results) || [];
        var html = "";
        for (var i = 0; i < results.length; i++) {
            var item = results[i];
            var title = (item.data && (item.data.title || item.data.name)) || item.id;
            html += "<li class=\"cmp-contentaisearch__item\">" + this._escapeHtml(title) + "</li>";
        }
        this._elements.results.innerHTML = html;
    };

    ContentAISearch.prototype._renderSummary = function(data) {
        this._elements.summaryText.textContent = data.result || "";
        var hits = data.hits || [];
        var sourcesHtml = "";
        for (var i = 0; i < hits.length; i++) {
            var hit = hits[i];
            var url = hit.metadata && hit.metadata.url;
            var label = (hit.metadata && hit.metadata.title) || hit.id;
            if (url && this._isSafeUrl(url)) {
                sourcesHtml += "<li><a href=\"" + this._escapeAttribute(url) + "\">" + this._escapeHtml(label) + "</a></li>";
            } else {
                sourcesHtml += "<li>" + this._escapeHtml(label) + "</li>";
            }
        }
        this._elements.sources.innerHTML = sourcesHtml;
        toggleShow(this._elements.summary, true);
    };

    ContentAISearch.prototype._escapeHtml = function(text) {
        var div = document.createElement("div");
        div.textContent = String(text == null ? "" : text);
        return div.innerHTML;
    };

    ContentAISearch.prototype._escapeAttribute = function(text) {
        return this._escapeHtml(text).replace(/"/g, "&quot;");
    };

    /**
     * Strips ASCII C0 controls, DEL, and whitespace so the scheme-prefix checks below run against a single
     * normalised token (characters the URL/DOM layer may otherwise strip away, e.g. a TAB spliced into a
     * scheme name), rather than just trimming the ends as String.trim() does.
     *
     * @param {String} str - raw URL value
     * @returns {String} characters kept for scheme prefix checks
     */
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
        // Strip interior/leading/trailing ASCII controls, DEL, and whitespace before scheme-testing: the URL
        // layer normalises these away (e.g. "java\tscript:alert(1)" becomes "javascript:alert(1)" once
        // assigned to href), so leaving them in place would let a crafted value slip past the checks below.
        var sanitized = stripAsciiControlsAndWhitespaceForSchemeCheck(String(url));
        // Allow http(s) absolute URLs and root-relative/relative paths; reject javascript:, data:, vbscript:, etc.
        if (/^https?:\/\//i.test(sanitized)) {
            return true;
        }
        // Relative or root-relative path (no scheme). Reject anything containing a colon before the first slash (a scheme).
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
