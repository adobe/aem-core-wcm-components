/*******************************************************************************
 * Copyright 2017 Adobe
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
    var IS = "search";

    var DELAY = 300; // time before fetching new results when the user is typing a search string
    var LOADING_DISPLAY_DELAY = 300; // minimum time during which the loading indicator is displayed
    var PARAM_RESULTS_OFFSET = "resultsOffset";

    var keyCodes = {
        TAB: 9,
        ENTER: 13,
        ESCAPE: 27,
        ARROW_UP: 38,
        ARROW_DOWN: 40
    };

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]',
        item: {
            self: "[data-" + NS + "-hook-" + IS + '="item"]',
            title: "[data-" + NS + "-hook-" + IS + '="itemTitle"]',
            focused: "." + NS + "-search__item--is-focused"
        }
    };

    var properties = {
        /**
         * The minimum required length of the search term before results are fetched.
         *
         * @memberof Search
         * @type {Number}
         * @default 3
         */
        minLength: {
            "default": 3,
            transform: function(value) {
                value = parseFloat(value);
                return isNaN(value) ? null : value;
            }
        },
        /**
         * The maximal number of results fetched by a search request.
         *
         * @memberof Search
         * @type {Number}
         * @default 10
         */
        resultsSize: {
            "default": 10,
            transform: function(value) {
                value = parseFloat(value);
                return isNaN(value) ? null : value;
            }
        }
    };

    var idCount = 0;

    function readData(element) {
        var data = element.dataset;
        var options = [];
        var capitalized = IS;
        capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
        var reserved = ["is", "hook" + capitalized];

        for (var key in data) {
            if (Object.prototype.hasOwnProperty.call(data, key)) {
                var value = data[key];

                if (key.indexOf(NS) === 0) {
                    key = key.slice(NS.length);
                    key = key.charAt(0).toLowerCase() + key.substring(1);

                    if (reserved.indexOf(key) === -1) {
                        options[key] = value;
                    }
                }
            }
        }

        return options;
    }

    function toggleShow(element, show) {
        if (element) {
            if (show !== false) {
                element.style.display = "block";
                element.setAttribute("aria-hidden", false);
            } else {
                element.style.display = "none";
                element.setAttribute("aria-hidden", true);
            }
        }
    }

    function serialize(form) {
        var query = [];
        if (form && form.elements) {
            for (var i = 0; i < form.elements.length; i++) {
                var node = form.elements[i];
                if (!node.disabled && node.name) {
                    var param = [node.name, encodeURIComponent(node.value)];
                    query.push(param.join("="));
                }
            }
        }
        return query.join("&");
    }

    function mark(node, regex) {
        if (!node || !regex) {
            return;
        }

        // text nodes
        if (node.nodeType === 3) {
            var nodeValue = node.nodeValue;
            var match = regex.exec(nodeValue);

            if (nodeValue && match) {
                var element = document.createElement("mark");
                element.className = NS + "-search__item-mark";
                element.appendChild(document.createTextNode(match[0]));

                var after = node.splitText(match.index);
                after.nodeValue = after.nodeValue.substring(match[0].length);
                node.parentNode.insertBefore(element, after);
            }
        } else if (node.hasChildNodes()) {
            for (var i = 0; i < node.childNodes.length; i++) {
                // recurse
                mark(node.childNodes[i], regex);
            }
        }
    }

    function Search(config) {
        if (config.element) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");
        }

        this._cacheElements(config.element);
        this._setupProperties(config.options);

        this._action = this._elements.form.getAttribute("action");
        this._resultsOffset = 0;
        this._hasMoreResults = true;

        this._elements.input.addEventListener("input", this._onInput.bind(this));
        this._elements.input.addEventListener("focus", this._onInput.bind(this));
        this._elements.input.addEventListener("keydown", this._onKeydown.bind(this));
        this._elements.clear.addEventListener("click", this._onClearClick.bind(this));
        document.addEventListener("click", this._onDocumentClick.bind(this));
        this._elements.results.addEventListener("scroll", this._onScroll.bind(this));

        this._makeAccessible();
    }

    Search.prototype._displayResults = function() {
        if (this._elements.input.value.length === 0) {
            toggleShow(this._elements.clear, false);
            this._cancelResults();
        } else if (this._elements.input.value.length < this._properties.minLength) {
            toggleShow(this._elements.clear, true);
        } else {
            this._updateResults();
            toggleShow(this._elements.clear, true);
        }
    };

    Search.prototype._onScroll = function(event) {
        // fetch new results when the results to be scrolled down are less than the visible results
        if (this._elements.results.scrollTop + 2 * this._elements.results.clientHeight >= this._elements.results.scrollHeight) {
            this._resultsOffset += this._properties.resultsSize;
            this._displayResults();
        }
    };

    Search.prototype._onInput = function(event) {
        var self = this;
        self._cancelResults();
        // start searching when the search term reaches the minimum length
        this._timeout = setTimeout(function() {
            self._displayResults();
        }, DELAY);
    };

    Search.prototype._onKeydown = function(event) {
        var self = this;

        switch (event.keyCode) {
            case keyCodes.TAB:
                if (self._resultsOpen()) {
                    toggleShow(self._elements.results, false);
                    self._elements.input.setAttribute("aria-expanded", "false");
                }
                break;
            case keyCodes.ENTER:
                event.preventDefault();
                if (self._resultsOpen()) {
                    var focused = self._elements.results.querySelector(selectors.item.focused);
                    if (focused) {
                        focused.click();
                    }
                }
                break;
            case keyCodes.ESCAPE:
                self._cancelResults();
                break;
            case keyCodes.ARROW_UP:
                if (self._resultsOpen()) {
                    event.preventDefault();
                    self._stepResultFocus(true);
                }
                break;
            case keyCodes.ARROW_DOWN:
                if (self._resultsOpen()) {
                    event.preventDefault();
                    self._stepResultFocus();
                } else {
                    // test the input and if necessary fetch and display the results
                    self._onInput();
                }
                break;
            default:
                return;
        }
    };

    Search.prototype._onClearClick = function(event) {
        event.preventDefault();
        this._elements.input.value = "";
        toggleShow(this._elements.clear, false);
        toggleShow(this._elements.results, false);
        this._elements.input.setAttribute("aria-expanded", "false");
    };

    Search.prototype._onDocumentClick = function(event) {
        var inputContainsTarget =  this._elements.input.contains(event.target);
        var resultsContainTarget = this._elements.results.contains(event.target);

        if (!(inputContainsTarget || resultsContainTarget)) {
            toggleShow(this._elements.results, false);
            this._elements.input.setAttribute("aria-expanded", "false");
        }
    };

    Search.prototype._resultsOpen = function() {
        return this._elements.results.style.display !== "none";
    };

    Search.prototype._makeAccessible = function() {
        var id = NS + "-search-results-" + idCount;
        this._elements.input.setAttribute("aria-owns", id);
        this._elements.results.id = id;
        idCount++;
    };

    Search.prototype._generateItems = function(data, results) {
        var self = this;

        data.forEach(function(item) {
            var el = document.createElement("span");
            el.innerHTML = self._elements.itemTemplate.innerHTML;
            el.querySelectorAll(selectors.item.title)[0].appendChild(document.createTextNode(item.title));
            el.querySelectorAll(selectors.item.self)[0].setAttribute("href", item.url);
            results.innerHTML += el.innerHTML;
        });
    };

    Search.prototype._markResults = function() {
        var nodeList = this._elements.results.querySelectorAll(selectors.item.self);
        var escapedTerm = this._elements.input.value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
        var regex = new RegExp("(" + escapedTerm + ")", "gi");

        for (var i = this._resultsOffset - 1; i < nodeList.length; ++i) {
            var result = nodeList[i];
            mark(result, regex);
        }
    };

    Search.prototype._stepResultFocus = function(reverse) {
        var results = this._elements.results.querySelectorAll(selectors.item.self);
        var focused = this._elements.results.querySelector(selectors.item.focused);
        var newFocused;
        var index = Array.prototype.indexOf.call(results, focused);
        var focusedCssClass = NS + "-search__item--is-focused";

        if (results.length > 0) {

            if (!reverse) {
                // highlight the next result
                if (index < 0) {
                    results[0].classList.add(focusedCssClass);
                    results[0].setAttribute("aria-selected", "true");
                } else if (index + 1 < results.length) {
                    results[index].classList.remove(focusedCssClass);
                    results[index].setAttribute("aria-selected", "false");
                    results[index + 1].classList.add(focusedCssClass);
                    results[index + 1].setAttribute("aria-selected", "true");
                }

                // if the last visible result is partially hidden, scroll up until it's completely visible
                newFocused = this._elements.results.querySelector(selectors.item.focused);
                if (newFocused) {
                    var bottomHiddenHeight = newFocused.offsetTop + newFocused.offsetHeight - this._elements.results.scrollTop - this._elements.results.clientHeight;
                    if (bottomHiddenHeight > 0) {
                        this._elements.results.scrollTop += bottomHiddenHeight;
                    } else {
                        this._onScroll();
                    }
                }

            } else {
                // highlight the previous result
                if (index >= 1) {
                    results[index].classList.remove(focusedCssClass);
                    results[index].setAttribute("aria-selected", "false");
                    results[index - 1].classList.add(focusedCssClass);
                    results[index - 1].setAttribute("aria-selected", "true");
                }

                // if the first visible result is partially hidden, scroll down until it's completely visible
                newFocused = this._elements.results.querySelector(selectors.item.focused);
                if (newFocused) {
                    var topHiddenHeight = this._elements.results.scrollTop - newFocused.offsetTop;
                    if (topHiddenHeight > 0) {
                        this._elements.results.scrollTop -= topHiddenHeight;
                    }
                }
            }
        }
    };

    Search.prototype._updateResults = function() {
        var self = this;
        if (self._hasMoreResults) {
            var request = new XMLHttpRequest();
            var url = self._action + "?" + serialize(self._elements.form) + "&" + PARAM_RESULTS_OFFSET + "=" + self._resultsOffset;

            request.open("GET", url, true);
            request.onload = function() {
                // when the results are loaded: hide the loading indicator and display the search icon after a minimum period
                setTimeout(function() {
                    toggleShow(self._elements.loadingIndicator, false);
                    toggleShow(self._elements.icon, true);
                }, LOADING_DISPLAY_DELAY);
                if (request.status >= 200 && request.status < 400) {
                    // success status
                    var data = JSON.parse(request.responseText);
                    if (data.length > 0) {
                        self._generateItems(data, self._elements.results);
                        self._markResults();
                        toggleShow(self._elements.results, true);
                        self._elements.input.setAttribute("aria-expanded", "true");
                    } else {
                        self._hasMoreResults = false;
                    }
                    // the total number of results is not a multiple of the fetched results:
                    // -> we reached the end of the query
                    if (self._elements.results.querySelectorAll(selectors.item.self).length % self._properties.resultsSize > 0) {
                        self._hasMoreResults = false;
                    }
                } else {
                    // error status
                }
            };
            // when the results are loading: display the loading indicator and hide the search icon
            toggleShow(self._elements.loadingIndicator, true);
            toggleShow(self._elements.icon, false);
            request.send();
        }
    };

    Search.prototype._cancelResults = function() {
        clearTimeout(this._timeout);
        this._elements.results.scrollTop = 0;
        this._resultsOffset = 0;
        this._hasMoreResults = true;
        this._elements.results.innerHTML = "";
        this._elements.input.setAttribute("aria-expanded", "false");
    };

    Search.prototype._cacheElements = function(wrapper) {
        this._elements = {};
        this._elements.self = wrapper;
        var hooks = this._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

        for (var i = 0; i < hooks.length; i++) {
            var hook = hooks[i];
            var capitalized = IS;
            capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
            var key = hook.dataset[NS + "Hook" + capitalized];
            this._elements[key] = hook;
        }
    };

    Search.prototype._setupProperties = function(options) {
        this._properties = {};

        for (var key in properties) {
            if (Object.prototype.hasOwnProperty.call(properties, key)) {
                var property = properties[key];
                if (options && options[key] != null) {
                    if (property && typeof property.transform === "function") {
                        this._properties[key] = property.transform(options[key]);
                    } else {
                        this._properties[key] = options[key];
                    }
                } else {
                    this._properties[key] = properties[key]["default"];
                }
            }
        }
    };

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Search({ element: elements[i], options: readData(elements[i]) });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body = document.querySelector("body");
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new Search({ element: element, options: readData(element) });
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

})();
