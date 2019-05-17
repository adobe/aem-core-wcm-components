/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
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
    var IS = "accordion";

    var keyCodes = {
        END: 35,
        HOME: 36,
        ARROW_LEFT: 37,
        ARROW_UP: 38,
        ARROW_RIGHT: 39,
        ARROW_DOWN: 40
    };

    var selectors = {
        self: "[data-" +  NS + '-is="' + IS + '"]',
        expanded: {
            initial: "initially-expanded",
            item: "cmp-accordion__item--expanded",
            itempanel: "cmp-accordion__itempanel--expanded"
        }
    };

    /**
     * Accordion Configuration
     * @typedef {Object} AccordionConfig Represents an Accordion configuration
     * @property {HTMLElement} element The HTMLElement representing the Accordion
     * @property {Object} options The Accordion options
     */

    /**
     * Accordion
     * @class Accordion
     * @classdesc An interactive Accordion component for navigating a list of accordion items
     * @param {AccordionConfig} config The Accordion configuration
     */
    function Accordion(config) {
        var that = this;

        if (config && config.element) {
            init(config);
        }

        /**
         * Initializes the Accordion
         * @private
         * @param {AccordionConfig} config The Accordion configuration
         */
        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            cacheElements(config.element);
            that._elementItems = Array.isArray(that._elements["item"]) ? that._elements["item"] : [that._elements["item"]];
            that._elementItempanels = Array.isArray(that._elements["itempanel"]) ? that._elements["itempanel"] : [that._elements["itempanel"]];
            that._toggle = getInitiallyExpandedAccordionItem(that._elementItems);

            if (that._elements.itempanel) {
                refreshAccordion();
                bindEvents();
            }

            if (window.Granite && window.Granite.author && window.Granite.author.MessageChannel) {
                if (that._elementItems) {
                    if (that._elementItems.length === 1) {
                        /*
                         * if there is only 1 accordion item, always expand it in author
                         * mode, even if it is not author to be initially expanded.
                         */
                        handleAuthoring(0);
                    }
                }

                /*
                 * Editor message handling:
                 * - subscribe to "cmp.panelcontainer" message requests sent by the editor frame
                 * - check that the message data panel container type is correct and that the id (path) matches this specific Accordion component
                 * - if so, route the "navigate" operation to enact a navigation of the accordion items based on index data
                 */
                new window.Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
                    if (message.data && message.data.type === "cmp-accordion" && message.data.id === that._elements.self.dataset["cmpPanelcontainerId"]) {
                        if (message.data.operation === "navigate") {
                            handleAuthoring(message.data.index);
                        }
                    }
                });
            }
        }

        /**
         * Returns the index of the initially expanded item, if no item is expanded returns 0
         * @param {Array} accordionItems Accordion items
         * @returns {Number} Index of the expanded item, 0 if none are expanded
         */
        function getInitiallyExpandedAccordionItem(accordionItems) {
            if (accordionItems) {
                for (var i = 0; i < accordionItems.length; i++) {
                    if (accordionItems[i].classList.contains(selectors.expanded.initial)) {
                        // removes the 'initially-expanded' class since it is no longer needed.
                        accordionItems[i].classList.remove(selectors.expanded.initial);
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * Caches the Accordion elements as defined via the {@code data-accordion-hook="ELEMENT_NAME"} markup API
         * @private
         * @param {HTMLElement} wrapper The Accordion wrapper element
         */
        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            var hooks = that._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

            for (var i = 0; i < hooks.length; i++) {
                var hook = hooks[i];
                if (hook.closest("." + NS + "-" + IS) === that._elements.self) { // only process own accordion elements
                    var capitalized = IS;
                    capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
                    var key = hook.dataset[NS + "Hook" + capitalized];
                    if (that._elements[key]) {
                        if (!Array.isArray(that._elements[key])) {
                            var tmp = that._elements[key];
                            that._elements[key] = [tmp];
                        }
                        that._elements[key].push(hook);
                    } else {
                        that._elements[key] = hook;
                    }
                }
            }
        }

        /**
         * Binds Accordion event handling
         * @private
         */
        function bindEvents() {
            var items = that._elementItems;
            if (items) {
                for (var i = 0; i < items.length; i++) {
                    (function(index) {
                        items[i].addEventListener("click", function(event) {
                            navigateAndFocusAccordionItem(index);
                        });
                        items[i].addEventListener("keydown", function(event) {
                            onKeyDown(event);
                        });
                    })(i);
                }
            }
        }

        /**
         * Handles tab keydown events
         * @private
         * @param {Object} event The keydown event
         */
        function onKeyDown(event) {
            var index = that._toggle;
            var lastIndex = that._elementItems.length - 1;

            switch (event.keyCode) {
                case keyCodes.ARROW_LEFT:
                case keyCodes.ARROW_UP:
                    event.preventDefault();
                    if (index > 0) {
                        navigateAndFocusAccordionItem(index - 1);
                    }
                    break;
                case keyCodes.ARROW_RIGHT:
                case keyCodes.ARROW_DOWN:
                    event.preventDefault();
                    if (index < lastIndex) {
                        navigateAndFocusAccordionItem(index + 1);
                    }
                    break;
                case keyCodes.HOME:
                    event.preventDefault();
                    navigateAndFocusAccordionItem(0);
                    break;
                case keyCodes.END:
                    event.preventDefault();
                    navigateAndFocusAccordionItem(lastIndex);
                    break;
                default:
                    return;
            }
        }

        /**
         * Refreshes the accordion item based on the current {@code Accordion#_toggle} index
         * @private
         */
        function refreshAccordion() {
            if (that._toggle >= 0 && that._elementItempanels && that._elementItems) {
                if (!that._elementItempanels[that._toggle].classList.contains(selectors.expanded.itempanel)) {
                    expandAccordionItem(that._elementItempanels[that._toggle], that._elementItems[that._toggle]);
                } else {
                    collapseAccordionItem(that._elementItempanels[that._toggle], that._elementItems[that._toggle]);
                }
            }
        }

        /**
         * Collapses the provided accordion item.
         * @private
         * @param {Object} itempanel Provided accordion item's itempanel
         * @param {Object} item Provided accordion item's item
         */
        function collapseAccordionItem(itempanel, item) {
            if (itempanel && item) {
                itempanel.classList.remove(selectors.expanded.itempanel);
                itempanel.setAttribute("aria-hidden", true);
                item.classList.remove(selectors.expanded.item);
                item.setAttribute("aria-selected", false);
                item.setAttribute("tabindex", "-1");
            }
        }

        /**
         * Expands the provided accordion item.
         * @private
         * @param {Object} itempanel Provided accordion item's itempanel
         * @param {Object} item Provided accordion item's item
         */
        function expandAccordionItem(itempanel, item) {
            if (itempanel && item) {
                itempanel.classList.add(selectors.expanded.itempanel);
                itempanel.removeAttribute("aria-hidden");
                item.classList.add(selectors.expanded.item);
                item.setAttribute("aria-selected", true);
                item.setAttribute("tabindex", "0");
            }
        }

        /**
         * Focuses the element and prevents scrolling the element into view
         * @param {HTMLElement} element Element to focus
         */
        function focusWithoutScroll(element) {
            var x = window.scrollX || window.pageXOffset;
            var y = window.scrollY || window.pageYOffset;
            element.focus();
            window.scrollTo(x, y);
        }

        /**
         * Handles collapse/expand for the authoring UI. Collapses all accordion items and expands the provided index accordion item.
         * @private
         * @param {Number} index The index of the accordion item to expand
         */
        function handleAuthoring(index) {
            if (that._elementItempanels && that._elementItems) {
                for (var i = 0; i < that._elementItems.length; i++) {
                    collapseAccordionItem(that._elementItempanels[i], that._elementItems[i]);
                }
            }

            that._toggle = index;
            refreshAccordion();
        }

        /**
         * Navigates to the accordion item at the provided index and ensures the expanded/collapsed accordion item gains focus
         * @private
         * @param {Number} index The index of the item to navigate to
         */
        function navigateAndFocusAccordionItem(index) {
            that._toggle = index;
            refreshAccordion();
            focusWithoutScroll(that._elementItems[index]);
        }
    }

    /**
     * Reads options data from the Accordion wrapper element, defined via {@code data-cmp-*} data attributes
     * @private
     * @param {HTMLElement} element The Accordion element to read options data from
     * @returns {Object} The options read from the component data attributes
     */
    function readData(element) {
        var data = element.dataset;
        var options = [];
        var capitalized = IS;
        capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
        var reserved = ["is", "hook" + capitalized];

        for (var key in data) {
            if (data.hasOwnProperty(key)) {
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

    /**
     * Document ready handler and DOM mutation observers. Initializes Accordion components as necessary.
     * @private
     */
    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Accordion({ element: elements[i], options: readData(elements[i]) });
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
                                new Accordion({ element: element, options: readData(element) });
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
        document.addEventListener("DOMContentLoaded", onDocumentReady());
    }
}());
