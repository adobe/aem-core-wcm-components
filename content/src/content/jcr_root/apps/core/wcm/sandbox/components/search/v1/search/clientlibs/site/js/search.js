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

    var DELAY = 300;
    var LIMIT = 10;

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
        input : '.cmp-search__input',
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
        this._clear = this._el.querySelector(selectors.clear);
        this._results = this._el.querySelector(selectors.results);

        this._input.addEventListener('input', this._onInput.bind(this));
        this._input.addEventListener('focus', this._onInput.bind(this));
        this._input.addEventListener('keydown', this._onKeydown.bind(this));
        this._clear.addEventListener('click', this._onClearClick.bind(this));
        document.addEventListener('click', this._onDocumentClick.bind(this));

        this._makeAccessible();
    }

    Search.prototype._onInput = function(event) {
        var self = this;

        clearTimeout(self._timeout);

        this._timeout = setTimeout(function() {
            if (self._input.value.length === 0) {
                toggleShow(self._clear, false);
                self._cancelResults();
            } else {
                toggleShow(self._clear, true);
                self._updateResults();
            }
        }, DELAY);
    };

    Search.prototype._onKeydown = function(event) {
        var self = this;

        switch (event.keyCode) {
            case keyCodes.TAB:
                self._cancelResults();
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
                    self._stepResultFocus(true);
                }
                break;
            case keyCodes.ARROW_DOWN:
                if (self._resultsOpen()) {
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

        for (var i = 0; i < nodeList.length; ++i) {
            var result = nodeList[i];
            mark(result, regex);
        }
    };

    Search.prototype._stepResultFocus = function(reverse) {
        var results = this._results.querySelectorAll(selectors.item.self);
        var focused = this._results.querySelector(selectors.item.focused);
        var index = Array.prototype.indexOf.call(results, focused);
        var focusedCssClass = 'cmp-search__item--focused';

        if (results.length > 0) {
            if (focused) {
                focused.classList.remove(focusedCssClass);
            }

            if (!reverse) {
                if (index === (results.length - 1) || index < 0) {
                    results[0].classList.add(focusedCssClass);
                } else {
                    results[index + 1].classList.add(focusedCssClass);
                }
            } else {
                if (index <= 0) {
                    results[results.length - 1].classList.add(focusedCssClass);
                } else {
                    results[index - 1].classList.add(focusedCssClass);
                }
            }
        }
    };

    Search.prototype._applyLimit = function() {
        var nodeList = this._results.querySelectorAll(selectors.item.self);
        for (var i = LIMIT; i < nodeList.length; i++) {
            nodeList[i].parentNode.removeChild(nodeList[i]);
        }
    };

    Search.prototype._updateResults = function() {
        var self = this;
        var request = new XMLHttpRequest();
        var url = self._action + "?" + serialize(self._form);

        request.open('GET', url, true);
        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                // success status
                var data = request.responseText;
                self._results.innerHTML = data;
                self._applyLimit();
                self._markResults();
                toggleShow(self._results, true);
            } else {
                // error status
            }
        };
        request.send();
    };

    Search.prototype._cancelResults = function() {
        clearTimeout(this._timeout);
        toggleShow(this._results, false);
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
