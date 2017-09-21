/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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
(function () {
    'use strict';

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
        self : '.cmp-search',
        form : '.cmp-search__form',
        field : '.cmp-search__field',
        input : '.cmp-search__input',
        searchIcon : '.cmp-search__icon',
        loadingIndicator : '.cmp-search__loading-indicator',
        clear : '.cmp-search__clear',
        results : '.cmp-search__results',
        item : {
            self : '.cmp-search__item',
            focused : '.cmp-search__item--focused',
            mark : '.cmp-search__item-mark'
        }
    };

    var idCount = 0;

    function toggleShow(element, show) {
        if (element) {
            if (show !== false) {
                element.style.display = 'block';
                element.setAttribute('aria-hidden', false);
            } else {
                element.style.display = 'none';
                element.setAttribute('aria-hidden', true);
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
                    query.push(param.join('='));
                }
            }
        }
        return query.join('&');
    }

    function mark(node, regex) {
        if (!node || !regex) {
            return;
        }

        // text nodes
        if (node.nodeType == 3) {
            var nodeValue = node.nodeValue;
            var match = regex.exec(nodeValue);

            if (nodeValue && match) {
                var element = document.createElement('mark');
                element.className = 'cmp-search__item-mark';
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
        this._el = config.el;
        this._form = this._el.querySelector(selectors.form);
        this._action = this._form.getAttribute('action');
        this._input = this._el.querySelector(selectors.input);
        this._searchIcon = this._el.querySelector(selectors.searchIcon);
        this._loadingIndicator = this._el.querySelector(selectors.loadingIndicator);
        this._clear = this._el.querySelector(selectors.clear);
        this._results = this._el.querySelector(selectors.results);

        this._searchTermMinimumLength = parseFloat(this._el.dataset.searchTermMinimumLength);
        this._resultsSize = parseFloat(this._el.dataset.resultsSize);
        this._resultsOffset = 0;
        this._hasMoreResults = true;

        this._input.addEventListener('input', this._onInput.bind(this));
        this._input.addEventListener('focus', this._onInput.bind(this));
        this._input.addEventListener('keydown', this._onKeydown.bind(this));
        this._clear.addEventListener('click', this._onClearClick.bind(this));
        document.addEventListener('click', this._onDocumentClick.bind(this));
        this._results.addEventListener('scroll', this._onScroll.bind(this));

        this._makeAccessible();
    }

    Search.prototype._displayResults = function() {
        if (this._input.value.length === 0) {
            toggleShow(this._clear, false);
            this._cancelResults();
        } else if (this._input.value.length < this._searchTermMinimumLength) {
            toggleShow(this._clear, true);
        } else {
            this._updateResults();
            toggleShow(this._clear, true);
        }
    };

    Search.prototype._onScroll = function(event) {
        // fetch new results when the results to be scrolled down are less than the visible results
        if (this._results.scrollTop + 2 * this._results.clientHeight >= this._results.scrollHeight) {
            this._resultsOffset += this._resultsSize;
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
                    event.preventDefault();
                }
                break;
            case keyCodes.ENTER:
                if (!self._resultsOpen()) {
                    self._form.submit();
                } else {
                    var focused = self._results.querySelector(selectors.item.focused);
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
        this._input.value = '';
        toggleShow(this._clear, false);
        toggleShow(this._results, false);
    };

    Search.prototype._onDocumentClick = function(event) {
        var inputContainsTarget =  this._input.contains(event.target);
        var resultsContainTarget = this._results.contains(event.target);

        if (!(inputContainsTarget || resultsContainTarget)) {
            toggleShow(this._results, false);
        }
    };

    Search.prototype._resultsOpen = function() {
        return this._results.style.display !== 'none';
    };

    Search.prototype._makeAccessible = function() {
        var id = 'cmp-search-' + idCount;
        this._input.setAttribute('aria-owns', id);
        this._results.id = id;
        idCount++;
    };

    Search.prototype._markResults = function() {
        var nodeList = this._results.querySelectorAll(selectors.item.self);
        var escapedTerm = this._input.value.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
        var regex = new RegExp('(' + escapedTerm + ')', 'gi');

        for (var i = this._resultsOffset - 1; i < nodeList.length; ++i) {
            var result = nodeList[i];
            mark(result, regex);
        }
    };

    Search.prototype._stepResultFocus = function(reverse) {
        var results = this._results.querySelectorAll(selectors.item.self);
        var focused = this._results.querySelector(selectors.item.focused);
        var newFocused;
        var index = Array.prototype.indexOf.call(results, focused);
        var focusedCssClass = 'cmp-search__item--focused';

        if (results.length > 0) {

            if (!reverse) {
                // highlight the next result
                if (index < 0) {
                    results[0].classList.add(focusedCssClass);
                } else if (index + 1 < results.length) {
                    results[index].classList.remove(focusedCssClass);
                    results[index + 1].classList.add(focusedCssClass);
                }

                // if the last visible result is partially hidden, scroll up until it's completely visible
                newFocused = this._results.querySelector(selectors.item.focused);
                if (newFocused) {
                    var bottomHiddenHeight = newFocused.offsetTop + newFocused.offsetHeight - this._results.scrollTop - this._results.clientHeight;
                    if (bottomHiddenHeight > 0) {
                        this._results.scrollTop += bottomHiddenHeight;
                    } else {
                        this._onScroll();
                    }
                }

            } else {
                // highlight the previous result
                if (index >= 1) {
                    results[index].classList.remove(focusedCssClass);
                    results[index - 1].classList.add(focusedCssClass);
                }

                // if the first visible result is partially hidden, scroll down until it's completely visible
                newFocused = this._results.querySelector(selectors.item.focused);
                if (newFocused) {
                    var topHiddenHeight = this._results.scrollTop - newFocused.offsetTop;
                    if (topHiddenHeight > 0) {
                        this._results.scrollTop -= topHiddenHeight;
                    }
                }
            }
        }
    };

    Search.prototype._updateResults = function() {
        var self = this;
        if (self._hasMoreResults) {
            var request = new XMLHttpRequest();
            var url = self._action + "?" + serialize(self._form) + "&" + PARAM_RESULTS_OFFSET + "=" + self._resultsOffset;

            request.open('GET', url, true);
            request.onload = function() {
                // when the results are loaded: hide the loading indicator and display the search icon after a minimum period
                setTimeout(function() {
                    toggleShow(self._loadingIndicator, false);
                    toggleShow(self._searchIcon, true);
                }, LOADING_DISPLAY_DELAY);
                if (request.status >= 200 && request.status < 400) {
                    // success status
                    var data = request.responseText;
                    if (data && data.trim()) {
                        self._results.innerHTML = self._results.innerHTML + data;
                        self._markResults();
                        toggleShow(self._results, true);
                    } else {
                        self._hasMoreResults = false;
                    }
                    // the total number of results is not a multiple of the fetched results:
                    // -> we reached the end of the query
                    if (self._results.querySelectorAll(selectors.item.self).length % self._resultsSize > 0) {
                        self._hasMoreResults = false;
                    }
                } else {
                    // error status
                }
            };
            // when the results are loading: display the loading indicator and hide the search icon
            toggleShow(self._loadingIndicator, true);
            toggleShow(self._searchIcon, false);
            request.send();
        }
    };

    Search.prototype._cancelResults = function() {
        clearTimeout(this._timeout);
        this._results.scrollTop = 0;
        this._resultsOffset = 0;
        this._hasMoreResults = true;
        this._results.innerHTML = '';
    };

    var initSearch = function(search) {
        for (var i = 0; i < search.length; i++) {
            new Search({ el: search[i] });
        }
    };

    document.addEventListener('DOMContentLoaded', function() {
        var search = document.querySelectorAll(selectors.self);
        initSearch(search);
    });

    var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
    var body = document.querySelector('body');
    var observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            // needed for IE
            var nodesArray = [].slice.call(mutation.addedNodes);
            if (nodesArray.length > 0) {
                nodesArray.forEach(function (addedNode) {
                    if (addedNode.querySelectorAll) {
                        var search = [].slice.call(addedNode.querySelectorAll(selectors.self));
                        initSearch(search);
                    }
                });
            }
        });
    });

    observer.observe(body, {
        subtree      : true,
        childList    : true,
        characterData: true
    });

})();
