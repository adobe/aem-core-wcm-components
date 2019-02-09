/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Element.matches()
 * https://developer.mozilla.org/enUS/docs/Web/API/Element/matches#Polyfill
 */
if (!Element.prototype.matches) {
    Element.prototype.matches = Element.prototype.msMatchesSelector || Element.prototype.webkitMatchesSelector;
}

// eslint-disable-next-line valid-jsdoc
/**
 * Element.closest()
 * https://developer.mozilla.org/enUS/docs/Web/API/Element/closest#Polyfill
 */
if (!Element.prototype.closest) {
    Element.prototype.closest = function(s) {
        "use strict";
        var el = this;
        if (!document.documentElement.contains(el)) {
            return null;
        }
        do {
            if (el.matches(s)) {
                return el;
            }
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };
}

/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
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
    var IS = "tabs";

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
        active: {
            tab: "cmp-tabs__tab--active",
            tabpanel: "cmp-tabs__tabpanel--active"
        }
    };

    /**
     * Tabs Configuration
     *
     * @typedef {Object} TabsConfig Represents a Tabs configuration
     * @property {HTMLElement} element The HTMLElement representing the Tabs
     * @property {Object} options The Tabs options
     */

    /**
     * Tabs
     *
     * @class Tabs
     * @classdesc An interactive Tabs component for navigating a list of tabs
     * @param {TabsConfig} config The Tabs configuration
     */
    function Tabs(config) {
        var that = this;

        if (config && config.element) {
            init(config);
        }

        /**
         * Initializes the Tabs
         *
         * @private
         * @param {TabsConfig} config The Tabs configuration
         */
        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            cacheElements(config.element);
            that._active = getActiveIndex(that._elements["tab"]);

            if (that._elements.tabpanel) {
                refreshActive();
                bindEvents();
            }

            if (window.Granite && window.Granite.author && window.Granite.author.MessageChannel) {
                /*
                 * Editor message handling:
                 * - subscribe to "cmp.panelcontainer" message requests sent by the editor frame
                 * - check that the message data panel container type is correct and that the id (path) matches this specific Tabs component
                 * - if so, route the "navigate" operation to enact a navigation of the Tabs based on index data
                 */
                new window.Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
                    if (message.data && message.data.type === "cmp-tabs" && message.data.id === that._elements.self.dataset["cmpPanelcontainerId"]) {
                        if (message.data.operation === "navigate") {
                            navigate(message.data.index);
                        }
                    }
                });
            }
        }

        /**
         * Returns the index of the active tab, if no tab is active returns 0
         *
         * @param {Array} tabs Tab elements
         * @returns {Number} Index of the active tab, 0 if none is active
         */
        function getActiveIndex(tabs) {
            if (tabs) {
                for (var i = 0; i < tabs.length; i++) {
                    if (tabs[i].classList.contains(selectors.active.tab)) {
                        return i;
                    }
                }
            }
            return 0;
        }

        /**
         * Caches the Tabs elements as defined via the {@code data-tabs-hook="ELEMENT_NAME"} markup API
         *
         * @private
         * @param {HTMLElement} wrapper The Tabs wrapper element
         */
        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            var hooks = that._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

            for (var i = 0; i < hooks.length; i++) {
                var hook = hooks[i];
                if (hook.closest("." + NS + "-" + IS) === that._elements.self) { // only process own tab elements
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
         * Binds Tabs event handling
         *
         * @private
         */
        function bindEvents() {
            var tabs = that._elements["tab"];
            if (tabs) {
                for (var i = 0; i < tabs.length; i++) {
                    (function(index) {
                        tabs[i].addEventListener("click", function(event) {
                            navigateAndFocusTab(index);
                        });
                        tabs[i].addEventListener("keydown", function(event) {
                            onKeyDown(event);
                        });
                    })(i);
                }
            }
        }

        /**
         * Handles tab keydown events
         *
         * @private
         * @param {Object} event The keydown event
         */
        function onKeyDown(event) {
            var index = that._active;
            var lastIndex = that._elements["tab"].length - 1;

            switch (event.keyCode) {
                case keyCodes.ARROW_LEFT:
                case keyCodes.ARROW_UP:
                    event.preventDefault();
                    if (index > 0) {
                        navigateAndFocusTab(index - 1);
                    }
                    break;
                case keyCodes.ARROW_RIGHT:
                case keyCodes.ARROW_DOWN:
                    event.preventDefault();
                    if (index < lastIndex) {
                        navigateAndFocusTab(index + 1);
                    }
                    break;
                case keyCodes.HOME:
                    event.preventDefault();
                    navigateAndFocusTab(0);
                    break;
                case keyCodes.END:
                    event.preventDefault();
                    navigateAndFocusTab(lastIndex);
                    break;
                default:
                    return;
            }
        }

        /**
         * Refreshes the tab markup based on the current {@code Tabs#_active} index
         *
         * @private
         */
        function refreshActive() {
            var tabpanels = that._elements["tabpanel"];
            var tabs = that._elements["tab"];

            if (tabpanels) {
                if (Array.isArray(tabpanels)) {
                    for (var i = 0; i < tabpanels.length; i++) {
                        if (i === parseInt(that._active)) {
                            tabpanels[i].classList.add(selectors.active.tabpanel);
                            tabpanels[i].removeAttribute("aria-hidden");
                            tabs[i].classList.add(selectors.active.tab);
                            tabs[i].setAttribute("aria-selected", true);
                            tabs[i].setAttribute("tabindex", "0");
                        } else {
                            tabpanels[i].classList.remove(selectors.active.tabpanel);
                            tabpanels[i].setAttribute("aria-hidden", true);
                            tabs[i].classList.remove(selectors.active.tab);
                            tabs[i].setAttribute("aria-selected", false);
                            tabs[i].setAttribute("tabindex", "-1");
                        }
                    }
                } else {
                    // only one tab
                    tabpanels.classList.add(selectors.active.tabpanel);
                    tabs.classList.add(selectors.active.tab);
                }
            }
        }

        /**
         * Focuses the element and prevents scrolling the element into view
         *
         * @param {HTMLElement} element Element to focus
         */
        function focusWithoutScroll(element) {
            var x = window.scrollX || window.pageXOffset;
            var y = window.scrollY || window.pageYOffset;
            element.focus();
            window.scrollTo(x, y);
        }

        /**
         * Navigates to the tab at the provided index
         *
         * @private
         * @param {Number} index The index of the tab to navigate to
         */
        function navigate(index) {
            that._active = index;
            refreshActive();
        }

        /**
         * Navigates to the item at the provided index and ensures the active tab gains focus
         *
         * @private
         * @param {Number} index The index of the item to navigate to
         */
        function navigateAndFocusTab(index) {
            navigate(index);
            focusWithoutScroll(that._elements["tab"][index]);
        }
    }

    /**
     * Reads options data from the Tabs wrapper element, defined via {@code data-cmp-*} data attributes
     *
     * @private
     * @param {HTMLElement} element The Tabs element to read options data from
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
     * Document ready handler and DOM mutation observers. Initializes Tabs components as necessary.
     *
     * @private
     */
    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Tabs({ element: elements[i], options: readData(elements[i]) });
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
                                new Tabs({ element: element, options: readData(element) });
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

/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
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
    var IS = "carousel";

    var keyCodes = {
        SPACE: 32,
        END: 35,
        HOME: 36,
        ARROW_LEFT: 37,
        ARROW_UP: 38,
        ARROW_RIGHT: 39,
        ARROW_DOWN: 40
    };

    var selectors = {
        self: "[data-" +  NS + '-is="' + IS + '"]'
    };

    var properties = {
        /**
         * Determines whether the Carousel will automatically transition between slides
         *
         * @memberof Carousel
         * @type {Boolean}
         * @default false
         */
        "autoplay": {
            "default": false,
            "transform": function(value) {
                return !(value === null || typeof value === "undefined");
            }
        },
        /**
         * Duration (in milliseconds) before automatically transitioning to the next slide
         *
         * @memberof Carousel
         * @type {Number}
         * @default 5000
         */
        "delay": {
            "default": 5000,
            "transform": function(value) {
                value = parseFloat(value);
                return !isNaN(value) ? value : null;
            }
        },
        /**
         * Determines whether automatic pause on hovering the carousel is disabled
         *
         * @memberof Carousel
         * @type {Boolean}
         * @default false
         */
        "autopauseDisabled": {
            "default": false,
            "transform": function(value) {
                return !(value === null || typeof value === "undefined");
            }
        }
    };

    /**
     * Carousel Configuration
     *
     * @typedef {Object} CarouselConfig Represents a Carousel configuration
     * @property {HTMLElement} element The HTMLElement representing the Carousel
     * @property {Object} options The Carousel options
     */

    /**
     * Carousel
     *
     * @class Carousel
     * @classdesc An interactive Carousel component for navigating a list of generic items
     * @param {CarouselConfig} config The Carousel configuration
     */
    function Carousel(config) {
        var that = this;

        if (config && config.element) {
            init(config);
        }

        /**
         * Initializes the Carousel
         *
         * @private
         * @param {CarouselConfig} config The Carousel configuration
         */
        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            setupProperties(config.options);
            cacheElements(config.element);

            that._active = 0;
            that._paused = false;

            if (that._elements.item) {
                refreshActive();
                bindEvents();
                resetAutoplayInterval();
                refreshPlayPauseActions();
            }

            if (window.Granite && window.Granite.author && window.Granite.author.MessageChannel) {
                /*
                 * Editor message handling:
                 * - subscribe to "cmp.panelcontainer" message requests sent by the editor frame
                 * - check that the message data panel container type is correct and that the id (path) matches this specific Carousel component
                 * - if so, route the "navigate" operation to enact a navigation of the Carousel based on index data
                 */
                new window.Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
                    if (message.data && message.data.type === "cmp-carousel" && message.data.id === that._elements.self.dataset["cmpPanelcontainerId"]) {
                        if (message.data.operation === "navigate") {
                            navigate(message.data.index);
                        }
                    }
                });
            }
        }

        /**
         * Caches the Carousel elements as defined via the {@code data-carousel-hook="ELEMENT_NAME"} markup API
         *
         * @private
         * @param {HTMLElement} wrapper The Carousel wrapper element
         */
        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            var hooks = that._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

            for (var i = 0; i < hooks.length; i++) {
                var hook = hooks[i];
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

        /**
         * Sets up properties for the Carousel based on the passed options.
         *
         * @private
         * @param {Object} options The Carousel options
         */
        function setupProperties(options) {
            that._properties = {};

            for (var key in properties) {
                if (properties.hasOwnProperty(key)) {
                    var property = properties[key];
                    var value = null;

                    if (options && options[key] != null) {
                        value = options[key];

                        // transform the provided option
                        if (property && typeof property.transform === "function") {
                            value = property.transform(value);
                        }
                    }

                    if (value === null) {
                        // value still null, take the property default
                        value = properties[key]["default"];
                    }

                    that._properties[key] = value;
                }
            }
        }

        /**
         * Binds Carousel event handling
         *
         * @private
         */
        function bindEvents() {
            if (that._elements["previous"]) {
                that._elements["previous"].addEventListener("click", function() {
                    navigate(getPreviousIndex());
                });
            }

            if (that._elements["next"]) {
                that._elements["next"].addEventListener("click", function() {
                    navigate(getNextIndex());
                });
            }

            var indicators = that._elements["indicator"];
            if (indicators) {
                for (var i = 0; i < indicators.length; i++) {
                    (function(index) {
                        indicators[i].addEventListener("click", function(event) {
                            navigateAndFocusIndicator(index);
                        });
                    })(i);
                }
            }

            if (that._elements["pause"]) {
                if (that._properties.autoplay) {
                    that._elements["pause"].addEventListener("click", onPauseClick);
                }
            }

            if (that._elements["play"]) {
                if (that._properties.autoplay) {
                    that._elements["play"].addEventListener("click", onPlayClick);
                }
            }

            that._elements.self.addEventListener("keydown", onKeyDown);

            if (!that._properties.autopauseDisabled) {
                that._elements.self.addEventListener("mouseenter", onMouseEnter);
                that._elements.self.addEventListener("mouseleave", onMouseLeave);
            }
        }

        /**
         * Handles carousel keydown events
         *
         * @private
         * @param {Object} event The keydown event
         */
        function onKeyDown(event) {
            var index = that._active;
            var lastIndex = that._elements["indicator"].length - 1;

            switch (event.keyCode) {
                case keyCodes.ARROW_LEFT:
                case keyCodes.ARROW_UP:
                    event.preventDefault();
                    if (index > 0) {
                        navigateAndFocusIndicator(index - 1);
                    }
                    break;
                case keyCodes.ARROW_RIGHT:
                case keyCodes.ARROW_DOWN:
                    event.preventDefault();
                    if (index < lastIndex) {
                        navigateAndFocusIndicator(index + 1);
                    }
                    break;
                case keyCodes.HOME:
                    event.preventDefault();
                    navigateAndFocusIndicator(0);
                    break;
                case keyCodes.END:
                    event.preventDefault();
                    navigateAndFocusIndicator(lastIndex);
                    break;
                case keyCodes.SPACE:
                    if (that._properties.autoplay && (event.target !== that._elements["previous"] && event.target !== that._elements["next"])) {
                        event.preventDefault();
                        if (!that._paused) {
                            pause();
                        } else {
                            play();
                        }
                    }
                    if (event.target === that._elements["pause"]) {
                        that._elements["play"].focus();
                    }
                    if (event.target === that._elements["play"]) {
                        that._elements["pause"].focus();
                    }
                    break;
                default:
                    return;
            }
        }

        /**
         * Handles carousel mouseenter events
         *
         * @private
         * @param {Object} event The mouseenter event
         */
        function onMouseEnter(event) {
            clearAutoplayInterval();
        }

        /**
         * Handles carousel mouseleave events
         *
         * @private
         * @param {Object} event The mouseleave event
         */
        function onMouseLeave(event) {
            resetAutoplayInterval();
        }

        /**
         * Handles pause element click events
         *
         * @private
         * @param {Object} event The click event
         */
        function onPauseClick(event) {
            pause();
            that._elements["play"].focus();
        }

        /**
         * Handles play element click events
         *
         * @private
         * @param {Object} event The click event
         */
        function onPlayClick() {
            play();
            that._elements["pause"].focus();
        }

        /**
         * Pauses the playing of the Carousel. Sets {@code Carousel#_paused} marker.
         * Only relevant when autoplay is enabled
         *
         * @private
         */
        function pause() {
            that._paused = true;
            clearAutoplayInterval();
            refreshPlayPauseActions();
        }

        /**
         * Enables the playing of the Carousel. Sets {@code Carousel#_paused} marker.
         * Only relevant when autoplay is enabled
         *
         * @private
         */
        function play() {
            that._paused = false;

            // If the Carousel is hovered, don't begin auto transitioning until the next mouse leave event
            var hovered = false;
            if (that._elements.self.parentElement) {
                hovered = that._elements.self.parentElement.querySelector(":hover") === that._elements.self;
            }
            if (that._properties.autopauseDisabled || !hovered) {
                resetAutoplayInterval();
            }

            refreshPlayPauseActions();
        }

        /**
         * Refreshes the play/pause action markup based on the {@code Carousel#_paused} state
         *
         * @private
         */
        function refreshPlayPauseActions() {
            setActionDisabled(that._elements["pause"], that._paused);
            setActionDisabled(that._elements["play"], !that._paused);
        }

        /**
         * Refreshes the item markup based on the current {@code Carousel#_active} index
         *
         * @private
         */
        function refreshActive() {
            var items = that._elements["item"];
            var indicators = that._elements["indicator"];

            if (items) {
                if (Array.isArray(items)) {
                    for (var i = 0; i < items.length; i++) {
                        if (i === parseInt(that._active)) {
                            items[i].classList.add("cmp-carousel__item--active");
                            items[i].removeAttribute("aria-hidden");
                            indicators[i].classList.add("cmp-carousel__indicator--active");
                            indicators[i].setAttribute("aria-selected", true);
                            indicators[i].setAttribute("tabindex", "0");
                        } else {
                            items[i].classList.remove("cmp-carousel__item--active");
                            items[i].setAttribute("aria-hidden", true);
                            indicators[i].classList.remove("cmp-carousel__indicator--active");
                            indicators[i].setAttribute("aria-selected", false);
                            indicators[i].setAttribute("tabindex", "-1");
                        }
                    }
                } else {
                    // only one item
                    items.classList.add("cmp-carousel__item--active");
                    indicators.classList.add("cmp-carousel__indicator--active");
                }
            }
        }

        /**
         * Focuses the element and prevents scrolling the element into view
         *
         * @param {HTMLElement} element Element to focus
         */
        function focusWithoutScroll(element) {
            var x = window.scrollX || window.pageXOffset;
            var y = window.scrollY || window.pageYOffset;
            element.focus();
            window.scrollTo(x, y);
        }

        /**
         * Retrieves the next active index, with looping
         *
         * @private
         * @returns {Number} Index of the next carousel item
         */
        function getNextIndex() {
            return that._active === (that._elements["item"].length - 1) ? 0 : that._active + 1;
        }

        /**
         * Retrieves the previous active index, with looping
         *
         * @private
         * @returns {Number} Index of the previous carousel item
         */
        function getPreviousIndex() {
            return that._active === 0 ? (that._elements["item"].length - 1) : that._active - 1;
        }

        /**
         * Navigates to the item at the provided index
         *
         * @private
         * @param {Number} index The index of the item to navigate to
         */
        function navigate(index) {
            if (index < 0 || index > (that._elements["item"].length - 1)) {
                return;
            }

            that._active = index;
            refreshActive();

            // reset the autoplay transition interval following navigation, if not already hovering the carousel
            if (that._elements.self.parentElement) {
                if (that._elements.self.parentElement.querySelector(":hover") !== that._elements.self) {
                    resetAutoplayInterval();
                }
            }
        }

        /**
         * Navigates to the item at the provided index and ensures the active indicator gains focus
         *
         * @private
         * @param {Number} index The index of the item to navigate to
         */
        function navigateAndFocusIndicator(index) {
            navigate(index);
            focusWithoutScroll(that._elements["indicator"][index]);
        }

        /**
         * Starts/resets automatic slide transition interval
         *
         * @private
         */
        function resetAutoplayInterval() {
            if (that._paused || !that._properties.autoplay) {
                return;
            }
            clearAutoplayInterval();
            that._autoplayIntervalId = window.setInterval(function() {
                if (document.visibilityState && document.hidden) {
                    return;
                }
                var indicators = that._elements["indicators"];
                if (indicators !== document.activeElement && indicators.contains(document.activeElement)) {
                    // if an indicator has focus, ensure we switch focus following navigation
                    navigateAndFocusIndicator(getNextIndex());
                } else {
                    navigate(getNextIndex());
                }
            }, that._properties.delay);
        }

        /**
         * Clears/pauses automatic slide transition interval
         *
         * @private
         */
        function clearAutoplayInterval() {
            window.clearInterval(that._autoplayIntervalId);
            that._autoplayIntervalId = null;
        }

        /**
         * Sets the disabled state for an action and toggles the appropriate CSS classes
         *
         * @private
         * @param {HTMLElement} action Action to disable
         * @param {Boolean} [disable] {@code true} to disable, {@code false} to enable
         */
        function setActionDisabled(action, disable) {
            if (!action) {
                return;
            }
            if (disable !== false) {
                action.disabled = true;
                action.classList.add("cmp-carousel__action--disabled");
            } else {
                action.disabled = false;
                action.classList.remove("cmp-carousel__action--disabled");
            }
        }
    }

    /**
     * Reads options data from the Carousel wrapper element, defined via {@code data-cmp-*} data attributes
     *
     * @private
     * @param {HTMLElement} element The Carousel element to read options data from
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
     * Document ready handler and DOM mutation observers. Initializes Carousel components as necessary.
     *
     * @private
     */
    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Carousel({ element: elements[i], options: readData(elements[i]) });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body             = document.querySelector("body");
        var observer         = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new Carousel({ element: element, options: readData(element) });
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
if (window.Element && !Element.prototype.closest) {
    // eslint valid-jsdoc: "off"
    Element.prototype.closest =
        function(s) {
            "use strict";
            var matches = (this.document || this.ownerDocument).querySelectorAll(s);
            var el      = this;
            var i;
            do {
                i = matches.length;
                while (--i >= 0 && matches.item(i) !== el) {
                    // continue
                }
            } while ((i < 0) && (el = el.parentElement));
            return el;
        };
}

if (window.Element && !Element.prototype.matches) {
    Element.prototype.matches =
        Element.prototype.matchesSelector ||
        Element.prototype.mozMatchesSelector ||
        Element.prototype.msMatchesSelector ||
        Element.prototype.oMatchesSelector ||
        Element.prototype.webkitMatchesSelector ||
        function(s) {
            "use strict";
            var matches = (this.document || this.ownerDocument).querySelectorAll(s);
            var i       = matches.length;
            while (--i >= 0 && matches.item(i) !== this) {
                // continue
            }
            return i > -1;
        };
}

if (!Object.assign) {
    Object.assign = function(target, varArgs) { // .length of function is 2
        "use strict";
        if (target === null) {
            throw new TypeError("Cannot convert undefined or null to object");
        }

        var to = Object(target);

        for (var index = 1; index < arguments.length; index++) {
            var nextSource = arguments[index];

            if (nextSource !== null) {
                for (var nextKey in nextSource) {
                    if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
                        to[nextKey] = nextSource[nextKey];
                    }
                }
            }
        }
        return to;
    };
}

(function(arr) {
    "use strict";
    arr.forEach(function(item) {
        if (item.hasOwnProperty("remove")) {
            return;
        }
        Object.defineProperty(item, "remove", {
            configurable: true,
            enumerable: true,
            writable: true,
            value: function remove() {
                this.parentNode.removeChild(this);
            }
        });
    });
})([Element.prototype, CharacterData.prototype, DocumentType.prototype]);

/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
    var IS = "image";

    var EMPTY_PIXEL = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
    var LAZY_THRESHOLD = 0;
    var SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]',
        image: '[data-cmp-hook-image="image"]',
        map: '[data-cmp-hook-image="map"]',
        area: '[data-cmp-hook-image="area"]'
    };

    var lazyLoader = {
        "cssClass": "cmp-image__image--is-loading",
        "style": {
            "height": 0,
            "padding-bottom": "" // will be replaced with % ratio
        }
    };

    var properties = {
        /**
         * An array of alternative image widths (in pixels).
         * Used to replace a {.width} variable in the src property with an optimal width if a URI template is provided.
         *
         * @memberof Image
         * @type {Number[]}
         * @default []
         */
        "widths": {
            "default": [],
            "transform": function(value) {
                var widths = [];
                value.split(",").forEach(function(item) {
                    item = parseFloat(item);
                    if (!isNaN(item)) {
                        widths.push(item);
                    }
                });
                return widths;
            }
        },
        /**
         * Indicates whether the image should be rendered lazily.
         *
         * @memberof Image
         * @type {Boolean}
         * @default false
         */
        "lazy": {
            "default": false,
            "transform": function(value) {
                return !(value === null || typeof value === "undefined");
            }
        },
        /**
         * The image source.
         *
         * Can be a simple image source, or a URI template representation that
         * can be variable expanded - useful for building an image configuration with an alternative width.
         * e.g. '/path/image.coreimg{.width}.jpeg/1506620954214.jpeg'
         *
         * @memberof Image
         * @type {String}
         */
        "src": {
        }
    };

    var devicePixelRatio = window.devicePixelRatio || 1;

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

    function Image(config) {
        var that = this;

        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            setupProperties(config.options);
            cacheElements(config.element);

            if (!that._elements.noscript) {
                return;
            }

            that._elements.container = that._elements.link ? that._elements.link : that._elements.self;

            unwrapNoScript();

            if (that._properties.lazy) {
                addLazyLoader();
            }

            if (that._elements.map) {
                that._elements.image.addEventListener("load", onLoad);
            }

            window.addEventListener("scroll", that.update);
            window.addEventListener("resize", onWindowResize);
            window.addEventListener("update", that.update);
            that._elements.image.addEventListener("cmp-image-redraw", that.update);
            that.update();
        }

        function loadImage() {
            var hasWidths = that._properties.widths && that._properties.widths.length > 0;
            var replacement = hasWidths ? "." + getOptimalWidth() : "";
            var url = that._properties.src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, replacement);

            if (that._elements.image.getAttribute("src") !== url) {
                that._elements.image.setAttribute("src", url);
                if (!hasWidths) {
                    window.removeEventListener("scroll", that.update);
                }
            }

            if (that._lazyLoaderShowing) {
                that._elements.image.addEventListener("load", removeLazyLoader);
            }
        }

        function getOptimalWidth() {
            var container = that._elements.self;
            var containerWidth = container.clientWidth;
            while (containerWidth === 0 && container.parentNode) {
                container = container.parentNode;
                containerWidth = container.clientWidth;
            }
            var optimalWidth = containerWidth * devicePixelRatio;
            var len = that._properties.widths.length;
            var key = 0;

            while ((key < len - 1) && (that._properties.widths[key] < optimalWidth)) {
                key++;
            }

            return that._properties.widths[key].toString();
        }

        function addLazyLoader() {
            var width = that._elements.image.getAttribute("width");
            var height = that._elements.image.getAttribute("height");

            if (width && height) {
                var ratio = (height / width) * 100;
                var styles = lazyLoader.style;

                styles["padding-bottom"] = ratio + "%";

                for (var s in styles) {
                    if (styles.hasOwnProperty(s)) {
                        that._elements.image.style[s] = styles[s];
                    }
                }
            }
            that._elements.image.setAttribute("src", EMPTY_PIXEL);
            that._elements.image.classList.add(lazyLoader.cssClass);
            that._lazyLoaderShowing = true;
        }

        function unwrapNoScript() {
            var markup = decodeNoscript(that._elements.noscript.textContent.trim());
            var parser = new DOMParser();

            // temporary document avoids requesting the image before removing its src
            var temporaryDocument = parser.parseFromString(markup, "text/html");
            var imageElement = temporaryDocument.querySelector(selectors.image);
            imageElement.removeAttribute("src");
            that._elements.container.insertBefore(imageElement, that._elements.noscript);

            var mapElement = temporaryDocument.querySelector(selectors.map);
            if (mapElement) {
                that._elements.container.insertBefore(mapElement, that._elements.noscript);
            }

            that._elements.noscript.parentNode.removeChild(that._elements.noscript);
            if (that._elements.container.matches(selectors.image)) {
                that._elements.image = that._elements.container;
            } else {
                that._elements.image = that._elements.container.querySelector(selectors.image);
            }

            that._elements.map = that._elements.container.querySelector(selectors.map);
            that._elements.areas = that._elements.container.querySelectorAll(selectors.area);
        }

        function removeLazyLoader() {
            that._elements.image.classList.remove(lazyLoader.cssClass);
            for (var property in lazyLoader.style) {
                if (lazyLoader.style.hasOwnProperty(property)) {
                    that._elements.image.style[property] = "";
                }
            }
            that._elements.image.removeEventListener("load", removeLazyLoader);
            that._lazyLoaderShowing = false;
        }

        function isLazyVisible() {
            if (that._elements.container.offsetParent === null) {
                return false;
            }

            var wt = window.pageYOffset;
            var wb = wt + document.documentElement.clientHeight;
            var et = that._elements.container.getBoundingClientRect().top + wt;
            var eb = et + that._elements.container.clientHeight;

            return eb >= wt - LAZY_THRESHOLD && et <= wb + LAZY_THRESHOLD;
        }

        function resizeAreas() {
            if (that._elements.areas && that._elements.areas.length > 0) {
                for (var i = 0; i < that._elements.areas.length; i++) {
                    var width = that._elements.image.width;
                    var height = that._elements.image.height;

                    if (width && height) {
                        var relcoords = that._elements.areas[i].dataset.cmpRelcoords;
                        if (relcoords) {
                            var relativeCoordinates = relcoords.split(",");
                            var coordinates = new Array(relativeCoordinates.length);

                            for (var j = 0; j < coordinates.length; j++) {
                                if (j % 2 === 0) {
                                    coordinates[j] = parseInt(relativeCoordinates[j] * width);
                                } else {
                                    coordinates[j] = parseInt(relativeCoordinates[j] * height);
                                }
                            }

                            that._elements.areas[i].coords = coordinates;
                        }
                    }
                }
            }
        }

        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            var hooks = that._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

            for (var i = 0; i < hooks.length; i++) {
                var hook = hooks[i];
                var capitalized = IS;
                capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
                var key = hook.dataset[NS + "Hook" + capitalized];
                that._elements[key] = hook;
            }
        }

        function setupProperties(options) {
            that._properties = {};

            for (var key in properties) {
                if (properties.hasOwnProperty(key)) {
                    var property = properties[key];
                    if (options && options[key] != null) {
                        if (property && typeof property.transform === "function") {
                            that._properties[key] = property.transform(options[key]);
                        } else {
                            that._properties[key] = options[key];
                        }
                    } else {
                        that._properties[key] = properties[key]["default"];
                    }
                }
            }
        }

        function onWindowResize() {
            that.update();
            resizeAreas();
        }

        function onLoad() {
            resizeAreas();
        }

        that.update = function() {
            if (that._properties.lazy) {
                if (isLazyVisible()) {
                    loadImage();
                }
            } else {
                loadImage();
            }
        };

        if (config && config.element) {
            init(config);
        }
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Image({ element: elements[i], options: readData(elements[i]) });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body             = document.querySelector("body");
        var observer         = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new Image({ element: element, options: readData(element) });
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

    /*
        on drag & drop of the component into a parsys, noscript's content will be escaped multiple times by the editor which creates
        the DOM for editing; the HTML parser cannot be used here due to the multiple escaping
     */
    function decodeNoscript(text) {
        text = text.replace(/&(amp;)*lt;/g, "<");
        text = text.replace(/&(amp;)*gt;/g, ">");
        return text;
    }

})();

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
                    event.preventDefault();
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
    };

    Search.prototype._onDocumentClick = function(event) {
        var inputContainsTarget =  this._elements.input.contains(event.target);
        var resultsContainTarget = this._elements.results.contains(event.target);

        if (!(inputContainsTarget || resultsContainTarget)) {
            toggleShow(this._elements.results, false);
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
                } else if (index + 1 < results.length) {
                    results[index].classList.remove(focusedCssClass);
                    results[index + 1].classList.add(focusedCssClass);
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
                    results[index - 1].classList.add(focusedCssClass);
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
            if (properties.hasOwnProperty(key)) {
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

/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
    var IS = "formText";
    var IS_DASH = "form-text";

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]'
    };

    var properties = {
        /**
         * A validation message to display if there is a type mismatch between the user input and expected input.
         *
         * @type {String}
         */
        constraintMessage: {
        },
        /**
         * A validation message to display if no input is supplied, but input is expected for the field.
         *
         * @type {String}
         */
        requiredMessage: {
        }
    };

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

    function FormText(config) {
        if (config.element) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");
        }

        this._cacheElements(config.element);
        this._setupProperties(config.options);

        this._elements.input.addEventListener("invalid", this._onInvalid.bind(this));
        this._elements.input.addEventListener("input", this._onInput.bind(this));
    }

    FormText.prototype._onInvalid = function(event) {
        event.target.setCustomValidity("");
        if (event.target.validity.typeMismatch) {
            if (this._properties.constraintMessage) {
                event.target.setCustomValidity(this._properties.constraintMessage);
            }
        } else if (event.target.validity.valueMissing) {
            if (this._properties.requiredMessage) {
                event.target.setCustomValidity(this._properties.requiredMessage);
            }
        }
    };

    FormText.prototype._onInput = function(event) {
        event.target.setCustomValidity("");
    };

    FormText.prototype._cacheElements = function(wrapper) {
        this._elements = {};
        this._elements.self = wrapper;
        var hooks = this._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS_DASH + "]");
        for (var i = 0; i < hooks.length; i++) {
            var hook = hooks[i];
            var capitalized = IS;
            capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
            var key = hook.dataset[NS + "Hook" + capitalized];
            this._elements[key] = hook;
        }
    };

    FormText.prototype._setupProperties = function(options) {
        this._properties = {};

        for (var key in properties) {
            if (properties.hasOwnProperty(key)) {
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
            new FormText({ element: elements[i], options: readData(elements[i]) });
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
                                new FormText({ element: element, options: readData(element) });
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

})();

/* AUTO-GENERATED. DO NOT MODIFY. */
/*

  The MIT License (MIT)

  Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

  Permission is hereby granted, free of charge, to any person
  obtaining a copy of this software and associated documentation files
  (the "Software"), to deal in the Software without restriction,
  including without limitation the rights to use, copy, modify, merge,
  publish, distribute, sublicense, and/or sell copies of the Software,
  and to permit persons to whom the Software is furnished to do so,
  subject to the following conditions:

  The above copyright notice and this permission notice shall be
  included in all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
  BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
  ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.


 Style HTML
---------------

  Written by Nochum Sossonko, (nsossonko@hotmail.com)

  Based on code initially developed by: Einar Lielmanis, <einar@beautifier.io>
    https://beautifier.io/

  Usage:
    style_html(html_source);

    style_html(html_source, options);

  The options are:
    indent_inner_html (default false)  â€” indent <head> and <body> sections,
    indent_size (default 4)          â€” indentation size,
    indent_char (default space)      â€” character to indent with,
    wrap_line_length (default 250)            -  maximum amount of characters per line (0 = disable)
    brace_style (default "collapse") - "collapse" | "expand" | "end-expand" | "none"
            put braces on the same line as control statements (default), or put braces on own line (Allman / ANSI style), or just put end braces on own line, or attempt to keep them where they are.
    inline (defaults to inline tags) - list of tags to be considered inline tags
    unformatted (defaults to inline tags) - list of tags, that shouldn't be reformatted
    content_unformatted (defaults to ["pre", "textarea"] tags) - list of tags, whose content shouldn't be reformatted
    indent_scripts (default normal)  - "keep"|"separate"|"normal"
    preserve_newlines (default true) - whether existing line breaks before elements should be preserved
                                        Only works before elements, not inside tags or for text.
    max_preserve_newlines (default unlimited) - maximum number of line breaks to be preserved in one chunk
    indent_handlebars (default false) - format and indent {{#foo}} and {{/foo}}
    end_with_newline (false)          - end with a newline
    extra_liners (default [head,body,/html]) -List of tags that should have an extra newline before them.

    e.g.

    style_html(html_source, {
      'indent_inner_html': false,
      'indent_size': 2,
      'indent_char': ' ',
      'wrap_line_length': 78,
      'brace_style': 'expand',
      'preserve_newlines': true,
      'max_preserve_newlines': 5,
      'indent_handlebars': false,
      'extra_liners': ['/html']
    });
*/

(function() {

    /* GENERATED_BUILD_OUTPUT */
    var legacy_beautify_html =
            /******/ (function(modules) { // webpackBootstrap
        /******/ 	// The module cache
        /******/ 	var installedModules = {};
        /******/
        /******/ 	// The require function
        /******/ 	function __webpack_require__(moduleId) {
            /******/
            /******/ 		// Check if module is in cache
            /******/ 		if(installedModules[moduleId]) {
                /******/ 			return installedModules[moduleId].exports;
                /******/ 		}
            /******/ 		// Create a new module (and put it into the cache)
            /******/ 		var module = installedModules[moduleId] = {
                /******/ 			i: moduleId,
                /******/ 			l: false,
                /******/ 			exports: {}
                /******/ 		};
            /******/
            /******/ 		// Execute the module function
            /******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
            /******/
            /******/ 		// Flag the module as loaded
            /******/ 		module.l = true;
            /******/
            /******/ 		// Return the exports of the module
            /******/ 		return module.exports;
            /******/ 	}
        /******/
        /******/
        /******/ 	// expose the modules object (__webpack_modules__)
        /******/ 	__webpack_require__.m = modules;
        /******/
        /******/ 	// expose the module cache
        /******/ 	__webpack_require__.c = installedModules;
        /******/
        /******/ 	// define getter function for harmony exports
        /******/ 	__webpack_require__.d = function(exports, name, getter) {
            /******/ 		if(!__webpack_require__.o(exports, name)) {
                /******/ 			Object.defineProperty(exports, name, { enumerable: true, get: getter });
                /******/ 		}
            /******/ 	};
        /******/
        /******/ 	// define __esModule on exports
        /******/ 	__webpack_require__.r = function(exports) {
            /******/ 		if(typeof Symbol !== 'undefined' && Symbol.toStringTag) {
                /******/ 			Object.defineProperty(exports, Symbol.toStringTag, { value: 'Module' });
                /******/ 		}
            /******/ 		Object.defineProperty(exports, '__esModule', { value: true });
            /******/ 	};
        /******/
        /******/ 	// create a fake namespace object
        /******/ 	// mode & 1: value is a module id, require it
        /******/ 	// mode & 2: merge all properties of value into the ns
        /******/ 	// mode & 4: return value when already ns object
        /******/ 	// mode & 8|1: behave like require
        /******/ 	__webpack_require__.t = function(value, mode) {
            /******/ 		if(mode & 1) value = __webpack_require__(value);
            /******/ 		if(mode & 8) return value;
            /******/ 		if((mode & 4) && typeof value === 'object' && value && value.__esModule) return value;
            /******/ 		var ns = Object.create(null);
            /******/ 		__webpack_require__.r(ns);
            /******/ 		Object.defineProperty(ns, 'default', { enumerable: true, value: value });
            /******/ 		if(mode & 2 && typeof value != 'string') for(var key in value) __webpack_require__.d(ns, key, function(key) { return value[key]; }.bind(null, key));
            /******/ 		return ns;
            /******/ 	};
        /******/
        /******/ 	// getDefaultExport function for compatibility with non-harmony modules
        /******/ 	__webpack_require__.n = function(module) {
            /******/ 		var getter = module && module.__esModule ?
                /******/ 			function getDefault() { return module['default']; } :
                /******/ 			function getModuleExports() { return module; };
            /******/ 		__webpack_require__.d(getter, 'a', getter);
            /******/ 		return getter;
            /******/ 	};
        /******/
        /******/ 	// Object.prototype.hasOwnProperty.call
        /******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
        /******/
        /******/ 	// __webpack_public_path__
        /******/ 	__webpack_require__.p = "";
        /******/
        /******/
        /******/ 	// Load entry module and return exports
        /******/ 	return __webpack_require__(__webpack_require__.s = 15);
        /******/ })
    /************************************************************************/
    /******/ ([
        /* 0 */,
        /* 1 */,
        /* 2 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function OutputLine(parent) {
                this.__parent = parent;
                this.__character_count = 0;
                // use indent_count as a marker for this.__lines that have preserved indentation
                this.__indent_count = -1;
                this.__alignment_count = 0;

                this.__items = [];
            }

            OutputLine.prototype.item = function(index) {
                if (index < 0) {
                    return this.__items[this.__items.length + index];
                } else {
                    return this.__items[index];
                }
            };

            OutputLine.prototype.has_match = function(pattern) {
                for (var lastCheckedOutput = this.__items.length - 1; lastCheckedOutput >= 0; lastCheckedOutput--) {
                    if (this.__items[lastCheckedOutput].match(pattern)) {
                        return true;
                    }
                }
                return false;
            };

            OutputLine.prototype.set_indent = function(indent, alignment) {
                this.__indent_count = indent || 0;
                this.__alignment_count = alignment || 0;
                this.__character_count = this.__parent.baseIndentLength + this.__alignment_count + this.__indent_count * this.__parent.indent_length;
            };

            OutputLine.prototype.get_character_count = function() {
                return this.__character_count;
            };

            OutputLine.prototype.is_empty = function() {
                return this.__items.length === 0;
            };

            OutputLine.prototype.last = function() {
                if (!this.is_empty()) {
                    return this.__items[this.__items.length - 1];
                } else {
                    return null;
                }
            };

            OutputLine.prototype.push = function(item) {
                this.__items.push(item);
                this.__character_count += item.length;
            };

            OutputLine.prototype.push_raw = function(item) {
                this.push(item);
                var last_newline_index = item.lastIndexOf('\n');
                if (last_newline_index !== -1) {
                    this.__character_count = item.length - last_newline_index;
                }
            };

            OutputLine.prototype.pop = function() {
                var item = null;
                if (!this.is_empty()) {
                    item = this.__items.pop();
                    this.__character_count -= item.length;
                }
                return item;
            };

            OutputLine.prototype.remove_indent = function() {
                if (this.__indent_count > 0) {
                    this.__indent_count -= 1;
                    this.__character_count -= this.__parent.indent_length;
                }
            };

            OutputLine.prototype.trim = function() {
                while (this.last() === ' ') {
                    this.__items.pop();
                    this.__character_count -= 1;
                }
            };

            OutputLine.prototype.toString = function() {
                var result = '';
                if (!this.is_empty()) {
                    if (this.__indent_count >= 0) {
                        result = this.__parent.get_indent_string(this.__indent_count);
                    }
                    if (this.__alignment_count >= 0) {
                        result += this.__parent.get_alignment_string(this.__alignment_count);
                    }
                    result += this.__items.join('');
                }
                return result;
            };

            function IndentCache(base_string, level_string) {
                this.__cache = [base_string];
                this.__level_string = level_string;
            }

            IndentCache.prototype.__ensure_cache = function(level) {
                while (level >= this.__cache.length) {
                    this.__cache.push(this.__cache[this.__cache.length - 1] + this.__level_string);
                }
            };

            IndentCache.prototype.get_level_string = function(level) {
                this.__ensure_cache(level);
                return this.__cache[level];
            };


            function Output(options, baseIndentString) {
                var indent_string = options.indent_char;
                if (options.indent_size > 1) {
                    indent_string = new Array(options.indent_size + 1).join(options.indent_char);
                }

                // Set to null to continue support for auto detection of base indent level.
                baseIndentString = baseIndentString || '';
                if (options.indent_level > 0) {
                    baseIndentString = new Array(options.indent_level + 1).join(indent_string);
                }

                this.__indent_cache = new IndentCache(baseIndentString, indent_string);
                this.__alignment_cache = new IndentCache('', ' ');
                this.baseIndentLength = baseIndentString.length;
                this.indent_length = indent_string.length;
                this.raw = false;
                this._end_with_newline = options.end_with_newline;

                this.__lines = [];
                this.previous_line = null;
                this.current_line = null;
                this.space_before_token = false;
                // initialize
                this.__add_outputline();
            }

            Output.prototype.__add_outputline = function() {
                this.previous_line = this.current_line;
                this.current_line = new OutputLine(this);
                this.__lines.push(this.current_line);
            };

            Output.prototype.get_line_number = function() {
                return this.__lines.length;
            };

            Output.prototype.get_indent_string = function(level) {
                return this.__indent_cache.get_level_string(level);
            };

            Output.prototype.get_alignment_string = function(level) {
                return this.__alignment_cache.get_level_string(level);
            };

            Output.prototype.is_empty = function() {
                return !this.previous_line && this.current_line.is_empty();
            };

            Output.prototype.add_new_line = function(force_newline) {
                // never newline at the start of file
                // otherwise, newline only if we didn't just add one or we're forced
                if (this.is_empty() ||
                    (!force_newline && this.just_added_newline())) {
                    return false;
                }

                // if raw output is enabled, don't print additional newlines,
                // but still return True as though you had
                if (!this.raw) {
                    this.__add_outputline();
                }
                return true;
            };

            Output.prototype.get_code = function(eol) {
                var sweet_code = this.__lines.join('\n').replace(/[\r\n\t ]+$/, '');

                if (this._end_with_newline) {
                    sweet_code += '\n';
                }

                if (eol !== '\n') {
                    sweet_code = sweet_code.replace(/[\n]/g, eol);
                }

                return sweet_code;
            };

            Output.prototype.set_indent = function(indent, alignment) {
                indent = indent || 0;
                alignment = alignment || 0;

                // Never indent your first output indent at the start of the file
                if (this.__lines.length > 1) {
                    this.current_line.set_indent(indent, alignment);
                    return true;
                }
                this.current_line.set_indent();
                return false;
            };

            Output.prototype.add_raw_token = function(token) {
                for (var x = 0; x < token.newlines; x++) {
                    this.__add_outputline();
                }
                this.current_line.push(token.whitespace_before);
                this.current_line.push_raw(token.text);
                this.space_before_token = false;
            };

            Output.prototype.add_token = function(printable_token) {
                this.add_space_before_token();
                this.current_line.push(printable_token);
            };

            Output.prototype.add_space_before_token = function() {
                if (this.space_before_token && !this.just_added_newline()) {
                    this.current_line.push(' ');
                }
                this.space_before_token = false;
            };

            Output.prototype.remove_indent = function(index) {
                var output_length = this.__lines.length;
                while (index < output_length) {
                    this.__lines[index].remove_indent();
                    index++;
                }
            };

            Output.prototype.trim = function(eat_newlines) {
                eat_newlines = (eat_newlines === undefined) ? false : eat_newlines;

                this.current_line.trim(this.indent_string, this.baseIndentString);

                while (eat_newlines && this.__lines.length > 1 &&
                this.current_line.is_empty()) {
                    this.__lines.pop();
                    this.current_line = this.__lines[this.__lines.length - 1];
                    this.current_line.trim();
                }

                this.previous_line = this.__lines.length > 1 ?
                    this.__lines[this.__lines.length - 2] : null;
            };

            Output.prototype.just_added_newline = function() {
                return this.current_line.is_empty();
            };

            Output.prototype.just_added_blankline = function() {
                return this.is_empty() ||
                    (this.current_line.is_empty() && this.previous_line.is_empty());
            };

            Output.prototype.ensure_empty_line_above = function(starts_with, ends_with) {
                var index = this.__lines.length - 2;
                while (index >= 0) {
                    var potentialEmptyLine = this.__lines[index];
                    if (potentialEmptyLine.is_empty()) {
                        break;
                    } else if (potentialEmptyLine.item(0).indexOf(starts_with) !== 0 &&
                        potentialEmptyLine.item(-1) !== ends_with) {
                        this.__lines.splice(index + 1, 0, new OutputLine(this));
                        this.previous_line = this.__lines[this.__lines.length - 2];
                        break;
                    }
                    index--;
                }
            };

            module.exports.Output = Output;


            /***/ }),
        /* 3 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function Token(type, text, newlines, whitespace_before) {
                this.type = type;
                this.text = text;

                // comments_before are
                // comments that have a new line before them
                // and may or may not have a newline after
                // this is a set of comments before
                this.comments_before = null; /* inline comment*/


                // this.comments_after =  new TokenStream(); // no new line before and newline after
                this.newlines = newlines || 0;
                this.whitespace_before = whitespace_before || '';
                this.parent = null;
                this.next = null;
                this.previous = null;
                this.opened = null;
                this.closed = null;
                this.directives = null;
            }


            module.exports.Token = Token;


            /***/ }),
        /* 4 */,
        /* 5 */,
        /* 6 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function Options(options, merge_child_field) {
                this.raw_options = _mergeOpts(options, merge_child_field);

                // Support passing the source text back with no change
                this.disabled = this._get_boolean('disabled');

                this.eol = this._get_characters('eol', 'auto');
                this.end_with_newline = this._get_boolean('end_with_newline');
                this.indent_size = this._get_number('indent_size', 4);
                this.indent_char = this._get_characters('indent_char', ' ');
                this.indent_level = this._get_number('indent_level');

                this.preserve_newlines = this._get_boolean('preserve_newlines', true);
                this.max_preserve_newlines = this._get_number('max_preserve_newlines', 32786);
                if (!this.preserve_newlines) {
                    this.max_preserve_newlines = 0;
                }

                this.indent_with_tabs = this._get_boolean('indent_with_tabs');
                if (this.indent_with_tabs) {
                    this.indent_char = '\t';
                    this.indent_size = 1;
                }

                // Backwards compat with 1.3.x
                this.wrap_line_length = this._get_number('wrap_line_length', this._get_number('max_char'));

            }

            Options.prototype._get_array = function(name, default_value) {
                var option_value = this.raw_options[name];
                var result = default_value || [];
                if (typeof option_value === 'object') {
                    if (option_value !== null && typeof option_value.concat === 'function') {
                        result = option_value.concat();
                    }
                } else if (typeof option_value === 'string') {
                    result = option_value.split(/[^a-zA-Z0-9_\/\-]+/);
                }
                return result;
            };

            Options.prototype._get_boolean = function(name, default_value) {
                var option_value = this.raw_options[name];
                var result = option_value === undefined ? !!default_value : !!option_value;
                return result;
            };

            Options.prototype._get_characters = function(name, default_value) {
                var option_value = this.raw_options[name];
                var result = default_value || '';
                if (typeof option_value === 'string') {
                    result = option_value.replace(/\\r/, '\r').replace(/\\n/, '\n').replace(/\\t/, '\t');
                }
                return result;
            };

            Options.prototype._get_number = function(name, default_value) {
                var option_value = this.raw_options[name];
                default_value = parseInt(default_value, 10);
                if (isNaN(default_value)) {
                    default_value = 0;
                }
                var result = parseInt(option_value, 10);
                if (isNaN(result)) {
                    result = default_value;
                }
                return result;
            };

            Options.prototype._get_selection = function(name, selection_list, default_value) {
                var result = this._get_selection_list(name, selection_list, default_value);
                if (result.length !== 1) {
                    throw new Error(
                        "Invalid Option Value: The option '" + name + "' can only be one of the following values:\n" +
                        selection_list + "\nYou passed in: '" + this.raw_options[name] + "'");
                }

                return result[0];
            };


            Options.prototype._get_selection_list = function(name, selection_list, default_value) {
                if (!selection_list || selection_list.length === 0) {
                    throw new Error("Selection list cannot be empty.");
                }

                default_value = default_value || [selection_list[0]];
                if (!this._is_valid_selection(default_value, selection_list)) {
                    throw new Error("Invalid Default Value!");
                }

                var result = this._get_array(name, default_value);
                if (!this._is_valid_selection(result, selection_list)) {
                    throw new Error(
                        "Invalid Option Value: The option '" + name + "' can contain only the following values:\n" +
                        selection_list + "\nYou passed in: '" + this.raw_options[name] + "'");
                }

                return result;
            };

            Options.prototype._is_valid_selection = function(result, selection_list) {
                return result.length && selection_list.length &&
                    !result.some(function(item) { return selection_list.indexOf(item) === -1; });
            };


// merges child options up with the parent options object
// Example: obj = {a: 1, b: {a: 2}}
//          mergeOpts(obj, 'b')
//
//          Returns: {a: 2}
            function _mergeOpts(allOptions, childFieldName) {
                var finalOpts = {};
                allOptions = _normalizeOpts(allOptions);
                var name;

                for (name in allOptions) {
                    if (name !== childFieldName) {
                        finalOpts[name] = allOptions[name];
                    }
                }

                //merge in the per type settings for the childFieldName
                if (childFieldName && allOptions[childFieldName]) {
                    for (name in allOptions[childFieldName]) {
                        finalOpts[name] = allOptions[childFieldName][name];
                    }
                }
                return finalOpts;
            }

            function _normalizeOpts(options) {
                var convertedOpts = {};
                var key;

                for (key in options) {
                    var newKey = key.replace(/-/g, "_");
                    convertedOpts[newKey] = options[key];
                }
                return convertedOpts;
            }

            module.exports.Options = Options;
            module.exports.normalizeOpts = _normalizeOpts;
            module.exports.mergeOpts = _mergeOpts;


            /***/ }),
        /* 7 */,
        /* 8 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function InputScanner(input_string) {
                this.__input = input_string || '';
                this.__input_length = this.__input.length;
                this.__position = 0;
            }

            InputScanner.prototype.restart = function() {
                this.__position = 0;
            };

            InputScanner.prototype.back = function() {
                if (this.__position > 0) {
                    this.__position -= 1;
                }
            };

            InputScanner.prototype.hasNext = function() {
                return this.__position < this.__input_length;
            };

            InputScanner.prototype.next = function() {
                var val = null;
                if (this.hasNext()) {
                    val = this.__input.charAt(this.__position);
                    this.__position += 1;
                }
                return val;
            };

            InputScanner.prototype.peek = function(index) {
                var val = null;
                index = index || 0;
                index += this.__position;
                if (index >= 0 && index < this.__input_length) {
                    val = this.__input.charAt(index);
                }
                return val;
            };

            InputScanner.prototype.test = function(pattern, index) {
                index = index || 0;
                index += this.__position;
                pattern.lastIndex = index;

                if (index >= 0 && index < this.__input_length) {
                    var pattern_match = pattern.exec(this.__input);
                    return pattern_match && pattern_match.index === index;
                } else {
                    return false;
                }
            };

            InputScanner.prototype.testChar = function(pattern, index) {
                // test one character regex match
                var val = this.peek(index);
                return val !== null && pattern.test(val);
            };

            InputScanner.prototype.match = function(pattern) {
                pattern.lastIndex = this.__position;
                var pattern_match = pattern.exec(this.__input);
                if (pattern_match && pattern_match.index === this.__position) {
                    this.__position += pattern_match[0].length;
                } else {
                    pattern_match = null;
                }
                return pattern_match;
            };

            InputScanner.prototype.read = function(pattern) {
                var val = '';
                var match = this.match(pattern);
                if (match) {
                    val = match[0];
                }
                return val;
            };

            InputScanner.prototype.readUntil = function(pattern, include_match) {
                var val = '';
                var match_index = this.__position;
                pattern.lastIndex = this.__position;
                var pattern_match = pattern.exec(this.__input);
                if (pattern_match) {
                    if (include_match) {
                        match_index = pattern_match.index + pattern_match[0].length;
                    } else {
                        match_index = pattern_match.index;
                    }
                } else {
                    match_index = this.__input_length;
                }

                val = this.__input.substring(this.__position, match_index);
                this.__position = match_index;
                return val;
            };

            InputScanner.prototype.readUntilAfter = function(pattern) {
                return this.readUntil(pattern, true);
            };

            /* css beautifier legacy helpers */
            InputScanner.prototype.peekUntilAfter = function(pattern) {
                var start = this.__position;
                var val = this.readUntilAfter(pattern);
                this.__position = start;
                return val;
            };

            InputScanner.prototype.lookBack = function(testVal) {
                var start = this.__position - 1;
                return start >= testVal.length && this.__input.substring(start - testVal.length, start)
                    .toLowerCase() === testVal;
            };


            module.exports.InputScanner = InputScanner;


            /***/ }),
        /* 9 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            var InputScanner = __webpack_require__(8).InputScanner;
            var Token = __webpack_require__(3).Token;
            var TokenStream = __webpack_require__(10).TokenStream;

            var TOKEN = {
                START: 'TK_START',
                RAW: 'TK_RAW',
                EOF: 'TK_EOF'
            };

            var Tokenizer = function(input_string, options) {
                this._input = new InputScanner(input_string);
                this._options = options || {};
                this.__tokens = null;
                this.__newline_count = 0;
                this.__whitespace_before_token = '';

                this._whitespace_pattern = /[\n\r\t ]+/g;
                this._newline_pattern = /([^\n\r]*)(\r\n|[\n\r])?/g;
            };

            Tokenizer.prototype.tokenize = function() {
                this._input.restart();
                this.__tokens = new TokenStream();

                this._reset();

                var current;
                var previous = new Token(TOKEN.START, '');
                var open_token = null;
                var open_stack = [];
                var comments = new TokenStream();

                while (previous.type !== TOKEN.EOF) {
                    current = this._get_next_token(previous, open_token);
                    while (this._is_comment(current)) {
                        comments.add(current);
                        current = this._get_next_token(previous, open_token);
                    }

                    if (!comments.isEmpty()) {
                        current.comments_before = comments;
                        comments = new TokenStream();
                    }

                    current.parent = open_token;

                    if (this._is_opening(current)) {
                        open_stack.push(open_token);
                        open_token = current;
                    } else if (open_token && this._is_closing(current, open_token)) {
                        current.opened = open_token;
                        open_token.closed = current;
                        open_token = open_stack.pop();
                        current.parent = open_token;
                    }

                    current.previous = previous;
                    previous.next = current;

                    this.__tokens.add(current);
                    previous = current;
                }

                return this.__tokens;
            };


            Tokenizer.prototype._is_first_token = function() {
                return this.__tokens.isEmpty();
            };

            Tokenizer.prototype._reset = function() {};

            Tokenizer.prototype._get_next_token = function(previous_token, open_token) { // jshint unused:false
                this._readWhitespace();
                var resulting_string = this._input.read(/.+/g);
                if (resulting_string) {
                    return this._create_token(TOKEN.RAW, resulting_string);
                } else {
                    return this._create_token(TOKEN.EOF, '');
                }
            };

            Tokenizer.prototype._is_comment = function(current_token) { // jshint unused:false
                return false;
            };

            Tokenizer.prototype._is_opening = function(current_token) { // jshint unused:false
                return false;
            };

            Tokenizer.prototype._is_closing = function(current_token, open_token) { // jshint unused:false
                return false;
            };

            Tokenizer.prototype._create_token = function(type, text) {
                var token = new Token(type, text, this.__newline_count, this.__whitespace_before_token);
                this.__newline_count = 0;
                this.__whitespace_before_token = '';
                return token;
            };

            Tokenizer.prototype._readWhitespace = function() {
                var resulting_string = this._input.read(this._whitespace_pattern);
                if (resulting_string === ' ') {
                    this.__whitespace_before_token = resulting_string;
                } else if (resulting_string !== '') {
                    this._newline_pattern.lastIndex = 0;
                    var nextMatch = this._newline_pattern.exec(resulting_string);
                    while (nextMatch[2]) {
                        this.__newline_count += 1;
                        nextMatch = this._newline_pattern.exec(resulting_string);
                    }
                    this.__whitespace_before_token = nextMatch[1];
                }
            };



            module.exports.Tokenizer = Tokenizer;
            module.exports.TOKEN = TOKEN;


            /***/ }),
        /* 10 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function TokenStream(parent_token) {
                // private
                this.__tokens = [];
                this.__tokens_length = this.__tokens.length;
                this.__position = 0;
                this.__parent_token = parent_token;
            }

            TokenStream.prototype.restart = function() {
                this.__position = 0;
            };

            TokenStream.prototype.isEmpty = function() {
                return this.__tokens_length === 0;
            };

            TokenStream.prototype.hasNext = function() {
                return this.__position < this.__tokens_length;
            };

            TokenStream.prototype.next = function() {
                var val = null;
                if (this.hasNext()) {
                    val = this.__tokens[this.__position];
                    this.__position += 1;
                }
                return val;
            };

            TokenStream.prototype.peek = function(index) {
                var val = null;
                index = index || 0;
                index += this.__position;
                if (index >= 0 && index < this.__tokens_length) {
                    val = this.__tokens[index];
                }
                return val;
            };

            TokenStream.prototype.add = function(token) {
                if (this.__parent_token) {
                    token.parent = this.__parent_token;
                }
                this.__tokens.push(token);
                this.__tokens_length += 1;
            };

            module.exports.TokenStream = TokenStream;


            /***/ }),
        /* 11 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            function Directives(start_block_pattern, end_block_pattern) {
                start_block_pattern = typeof start_block_pattern === 'string' ? start_block_pattern : start_block_pattern.source;
                end_block_pattern = typeof end_block_pattern === 'string' ? end_block_pattern : end_block_pattern.source;
                this.__directives_block_pattern = new RegExp(start_block_pattern + / beautify( \w+[:]\w+)+ /.source + end_block_pattern, 'g');
                this.__directive_pattern = / (\w+)[:](\w+)/g;

                this.__directives_end_ignore_pattern = new RegExp('(?:[\\s\\S]*?)((?:' + start_block_pattern + /\sbeautify\signore:end\s/.source + end_block_pattern + ')|$)', 'g');
            }

            Directives.prototype.get_directives = function(text) {
                if (!text.match(this.__directives_block_pattern)) {
                    return null;
                }

                var directives = {};
                this.__directive_pattern.lastIndex = 0;
                var directive_match = this.__directive_pattern.exec(text);

                while (directive_match) {
                    directives[directive_match[1]] = directive_match[2];
                    directive_match = this.__directive_pattern.exec(text);
                }

                return directives;
            };

            Directives.prototype.readIgnored = function(input) {
                return input.read(this.__directives_end_ignore_pattern);
            };


            module.exports.Directives = Directives;


            /***/ }),
        /* 12 */,
        /* 13 */,
        /* 14 */,
        /* 15 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            var Beautifier = __webpack_require__(16).Beautifier;

            function style_html(html_source, options, js_beautify, css_beautify) {
                var beautifier = new Beautifier(html_source, options, js_beautify, css_beautify);
                return beautifier.beautify();
            }

            module.exports = style_html;


            /***/ }),
        /* 16 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            var Options = __webpack_require__(17).Options;
            var Output = __webpack_require__(2).Output;
            var Tokenizer = __webpack_require__(18).Tokenizer;
            var TOKEN = __webpack_require__(18).TOKEN;

            var lineBreak = /\r\n|[\r\n]/;
            var allLineBreaks = /\r\n|[\r\n]/g;

            var Printer = function(options, base_indent_string) { //handles input/output and some other printing functions

                this.indent_level = 0;
                this.alignment_size = 0;
                this.wrap_line_length = options.wrap_line_length;
                this.max_preserve_newlines = options.max_preserve_newlines;
                this.preserve_newlines = options.preserve_newlines;

                this._output = new Output(options, base_indent_string);

            };

            Printer.prototype.current_line_has_match = function(pattern) {
                return this._output.current_line.has_match(pattern);
            };

            Printer.prototype.set_space_before_token = function(value) {
                this._output.space_before_token = value;
            };

            Printer.prototype.add_raw_token = function(token) {
                this._output.add_raw_token(token);
            };

            Printer.prototype.print_preserved_newlines = function(raw_token) {
                var newlines = 0;
                if (raw_token.type !== TOKEN.TEXT && raw_token.previous.type !== TOKEN.TEXT) {
                    newlines = raw_token.newlines ? 1 : 0;
                }

                if (this.preserve_newlines) {
                    newlines = raw_token.newlines < this.max_preserve_newlines + 1 ? raw_token.newlines : this.max_preserve_newlines + 1;
                }
                for (var n = 0; n < newlines; n++) {
                    this.print_newline(n > 0);
                }

                return newlines !== 0;
            };

            Printer.prototype.traverse_whitespace = function(raw_token) {
                if (raw_token.whitespace_before || raw_token.newlines) {
                    if (!this.print_preserved_newlines(raw_token)) {
                        this._output.space_before_token = true;
                        this.print_space_or_wrap(raw_token.text);
                    }
                    return true;
                }
                return false;
            };

// Append a space to the given content (string array) or, if we are
// at the wrap_line_length, append a newline/indentation.
// return true if a newline was added, false if a space was added
            Printer.prototype.print_space_or_wrap = function(text) {
                if (this.wrap_line_length) {
                    if (this._output.current_line.get_character_count() + text.length + 1 >= this.wrap_line_length) { //insert a line when the wrap_line_length is reached
                        return this._output.add_new_line();
                    }
                }
                return false;
            };

            Printer.prototype.print_newline = function(force) {
                this._output.add_new_line(force);
            };

            Printer.prototype.print_token = function(text) {
                if (text) {
                    if (this._output.current_line.is_empty()) {
                        this._output.set_indent(this.indent_level, this.alignment_size);
                    }

                    this._output.add_token(text);
                }
            };

            Printer.prototype.print_raw_text = function(text) {
                this._output.current_line.push_raw(text);
            };

            Printer.prototype.indent = function() {
                this.indent_level++;
            };

            Printer.prototype.unindent = function() {
                if (this.indent_level > 0) {
                    this.indent_level--;
                }
            };

            Printer.prototype.get_full_indent = function(level) {
                level = this.indent_level + (level || 0);
                if (level < 1) {
                    return '';
                }

                return this._output.get_indent_string(level);
            };


            var uses_beautifier = function(tag_check, start_token) {
                var raw_token = start_token.next;
                if (!start_token.closed) {
                    return false;
                }

                while (raw_token.type !== TOKEN.EOF && raw_token.closed !== start_token) {
                    if (raw_token.type === TOKEN.ATTRIBUTE && raw_token.text === 'type') {
                        // For script and style tags that have a type attribute, only enable custom beautifiers for matching values
                        var peekEquals = raw_token.next ? raw_token.next : raw_token;
                        var peekValue = peekEquals.next ? peekEquals.next : peekEquals;
                        if (peekEquals.type === TOKEN.EQUALS && peekValue.type === TOKEN.VALUE) {
                            return (tag_check === 'style' && peekValue.text.search('text/css') > -1) ||
                                (tag_check === 'script' && peekValue.text.search(/(text|application|dojo)\/(x-)?(javascript|ecmascript|jscript|livescript|(ld\+)?json|method|aspect)/) > -1);
                        }
                        return false;
                    }
                    raw_token = raw_token.next;
                }

                return true;
            };

            function in_array(what, arr) {
                return arr.indexOf(what) !== -1;
            }

            function TagFrame(parent, parser_token, indent_level) {
                this.parent = parent || null;
                this.tag = parser_token ? parser_token.tag_name : '';
                this.indent_level = indent_level || 0;
                this.parser_token = parser_token || null;
            }

            function TagStack(printer) {
                this._printer = printer;
                this._current_frame = null;
            }

            TagStack.prototype.get_parser_token = function() {
                return this._current_frame ? this._current_frame.parser_token : null;
            };

            TagStack.prototype.record_tag = function(parser_token) { //function to record a tag and its parent in this.tags Object
                var new_frame = new TagFrame(this._current_frame, parser_token, this._printer.indent_level);
                this._current_frame = new_frame;
            };

            TagStack.prototype._try_pop_frame = function(frame) { //function to retrieve the opening tag to the corresponding closer
                var parser_token = null;

                if (frame) {
                    parser_token = frame.parser_token;
                    this._printer.indent_level = frame.indent_level;
                    this._current_frame = frame.parent;
                }

                return parser_token;
            };

            TagStack.prototype._get_frame = function(tag_list, stop_list) { //function to retrieve the opening tag to the corresponding closer
                var frame = this._current_frame;

                while (frame) { //till we reach '' (the initial value);
                    if (tag_list.indexOf(frame.tag) !== -1) { //if this is it use it
                        break;
                    } else if (stop_list && stop_list.indexOf(frame.tag) !== -1) {
                        frame = null;
                        break;
                    }
                    frame = frame.parent;
                }

                return frame;
            };

            TagStack.prototype.try_pop = function(tag, stop_list) { //function to retrieve the opening tag to the corresponding closer
                var frame = this._get_frame([tag], stop_list);
                return this._try_pop_frame(frame);
            };

            TagStack.prototype.indent_to_tag = function(tag_list) {
                var frame = this._get_frame(tag_list);
                if (frame) {
                    this._printer.indent_level = frame.indent_level;
                }
            };

            function Beautifier(source_text, options, js_beautify, css_beautify) {
                //Wrapper function to invoke all the necessary constructors and deal with the output.
                this._source_text = source_text || '';
                options = options || {};
                this._js_beautify = js_beautify;
                this._css_beautify = css_beautify;
                this._tag_stack = null;

                // Allow the setting of language/file-type specific options
                // with inheritance of overall settings
                var optionHtml = new Options(options, 'html');

                this._options = optionHtml;

                this._is_wrap_attributes_force = this._options.wrap_attributes.substr(0, 'force'.length) === 'force';
                this._is_wrap_attributes_force_expand_multiline = (this._options.wrap_attributes === 'force-expand-multiline');
                this._is_wrap_attributes_force_aligned = (this._options.wrap_attributes === 'force-aligned');
                this._is_wrap_attributes_aligned_multiple = (this._options.wrap_attributes === 'aligned-multiple');
                this._is_wrap_attributes_preserve = this._options.wrap_attributes.substr(0, 'preserve'.length) === 'preserve';
                this._is_wrap_attributes_preserve_aligned = (this._options.wrap_attributes === 'preserve-aligned');
            }

            Beautifier.prototype.beautify = function() {

                // if disabled, return the input unchanged.
                if (this._options.disabled) {
                    return this._source_text;
                }

                var source_text = this._source_text;
                var eol = this._options.eol;
                if (this._options.eol === 'auto') {
                    eol = '\n';
                    if (source_text && lineBreak.test(source_text)) {
                        eol = source_text.match(lineBreak)[0];
                    }
                }

                // HACK: newline parsing inconsistent. This brute force normalizes the input.
                source_text = source_text.replace(allLineBreaks, '\n');
                var baseIndentString = '';

                // Including commented out text would change existing html beautifier behavior to autodetect base indent.
                // baseIndentString = source_text.match(/^[\t ]*/)[0];

                var last_token = {
                    text: '',
                    type: ''
                };

                var last_tag_token = new TagOpenParserToken();

                var printer = new Printer(this._options, baseIndentString);
                var tokens = new Tokenizer(source_text, this._options).tokenize();

                this._tag_stack = new TagStack(printer);

                var parser_token = null;
                var raw_token = tokens.next();
                while (raw_token.type !== TOKEN.EOF) {

                    if (raw_token.type === TOKEN.TAG_OPEN || raw_token.type === TOKEN.COMMENT) {
                        parser_token = this._handle_tag_open(printer, raw_token, last_tag_token, last_token);
                        last_tag_token = parser_token;
                    } else if ((raw_token.type === TOKEN.ATTRIBUTE || raw_token.type === TOKEN.EQUALS || raw_token.type === TOKEN.VALUE) ||
                        (raw_token.type === TOKEN.TEXT && !last_tag_token.tag_complete)) {
                        parser_token = this._handle_inside_tag(printer, raw_token, last_tag_token, tokens);
                    } else if (raw_token.type === TOKEN.TAG_CLOSE) {
                        parser_token = this._handle_tag_close(printer, raw_token, last_tag_token);
                    } else if (raw_token.type === TOKEN.TEXT) {
                        parser_token = this._handle_text(printer, raw_token, last_tag_token);
                    } else {
                        // This should never happen, but if it does. Print the raw token
                        printer.add_raw_token(raw_token);
                    }

                    last_token = parser_token;

                    raw_token = tokens.next();
                }
                var sweet_code = printer._output.get_code(eol);

                return sweet_code;
            };

            Beautifier.prototype._handle_tag_close = function(printer, raw_token, last_tag_token) {
                var parser_token = { text: raw_token.text, type: raw_token.type };
                printer.alignment_size = 0;
                last_tag_token.tag_complete = true;

                printer.set_space_before_token(raw_token.newlines || raw_token.whitespace_before !== '');
                if (last_tag_token.is_unformatted) {
                    printer.add_raw_token(raw_token);
                } else {
                    if (last_tag_token.tag_start_char === '<') {
                        printer.set_space_before_token(raw_token.text[0] === '/'); // space before />, no space before >
                        if (this._is_wrap_attributes_force_expand_multiline && last_tag_token.has_wrapped_attrs) {
                            printer.print_newline(false);
                        }
                    }
                    printer.print_token(raw_token.text);
                }

                if (last_tag_token.indent_content &&
                    !(last_tag_token.is_unformatted || last_tag_token.is_content_unformatted)) {
                    printer.indent();

                    // only indent once per opened tag
                    last_tag_token.indent_content = false;
                }
                return parser_token;
            };

            Beautifier.prototype._handle_inside_tag = function(printer, raw_token, last_tag_token, tokens) {
                var parser_token = { text: raw_token.text, type: raw_token.type };
                printer.set_space_before_token(raw_token.newlines || raw_token.whitespace_before !== '');
                if (last_tag_token.is_unformatted) {
                    printer.add_raw_token(raw_token);
                } else if (last_tag_token.tag_start_char === '{' && raw_token.type === TOKEN.TEXT) {
                    // For the insides of handlebars allow newlines or a single space between open and contents
                    if (printer.print_preserved_newlines(raw_token)) {
                        printer.print_raw_text(raw_token.whitespace_before + raw_token.text);
                    } else {
                        printer.print_token(raw_token.text);
                    }
                } else {
                    if (raw_token.type === TOKEN.ATTRIBUTE) {
                        printer.set_space_before_token(true);
                        last_tag_token.attr_count += 1;
                    } else if (raw_token.type === TOKEN.EQUALS) { //no space before =
                        printer.set_space_before_token(false);
                    } else if (raw_token.type === TOKEN.VALUE && raw_token.previous.type === TOKEN.EQUALS) { //no space before value
                        printer.set_space_before_token(false);
                    }

                    if (printer._output.space_before_token && last_tag_token.tag_start_char === '<') {
                        // Allow the current attribute to wrap
                        // Set wrapped to true if the line is wrapped
                        var wrapped = printer.print_space_or_wrap(raw_token.text);
                        if (raw_token.type === TOKEN.ATTRIBUTE) {
                            if (this._is_wrap_attributes_preserve || this._is_wrap_attributes_preserve_aligned) {
                                printer.traverse_whitespace(raw_token);
                                wrapped = wrapped || raw_token.newlines !== 0;
                            }
                            // Save whether we have wrapped any attributes
                            last_tag_token.has_wrapped_attrs = last_tag_token.has_wrapped_attrs || wrapped;

                            if (this._is_wrap_attributes_force) {
                                var force_attr_wrap = last_tag_token.attr_count > 1;
                                if (this._is_wrap_attributes_force_expand_multiline && last_tag_token.attr_count === 1) {
                                    var is_only_attribute = true;
                                    var peek_index = 0;
                                    var peek_token;
                                    do {
                                        peek_token = tokens.peek(peek_index);
                                        if (peek_token.type === TOKEN.ATTRIBUTE) {
                                            is_only_attribute = false;
                                            break;
                                        }
                                        peek_index += 1;
                                    } while (peek_index < 4 && peek_token.type !== TOKEN.EOF && peek_token.type !== TOKEN.TAG_CLOSE);

                                    force_attr_wrap = !is_only_attribute;
                                }

                                if (force_attr_wrap) {
                                    printer.print_newline(false);
                                    last_tag_token.has_wrapped_attrs = true;
                                }
                            }
                        }
                    }
                    printer.print_token(raw_token.text);
                }
                return parser_token;
            };

            Beautifier.prototype._handle_text = function(printer, raw_token, last_tag_token) {
                var parser_token = { text: raw_token.text, type: 'TK_CONTENT' };
                if (last_tag_token.custom_beautifier) { //check if we need to format javascript
                    this._print_custom_beatifier_text(printer, raw_token, last_tag_token);
                } else if (last_tag_token.is_unformatted || last_tag_token.is_content_unformatted) {
                    printer.add_raw_token(raw_token);
                } else {
                    printer.traverse_whitespace(raw_token);
                    printer.print_token(raw_token.text);
                }
                return parser_token;
            };

            Beautifier.prototype._print_custom_beatifier_text = function(printer, raw_token, last_tag_token) {
                if (raw_token.text !== '') {
                    printer.print_newline(false);
                    var text = raw_token.text,
                        _beautifier,
                        script_indent_level = 1;
                    if (last_tag_token.tag_name === 'script') {
                        _beautifier = typeof this._js_beautify === 'function' && this._js_beautify;
                    } else if (last_tag_token.tag_name === 'style') {
                        _beautifier = typeof this._css_beautify === 'function' && this._css_beautify;
                    }

                    if (this._options.indent_scripts === "keep") {
                        script_indent_level = 0;
                    } else if (this._options.indent_scripts === "separate") {
                        script_indent_level = -printer.indent_level;
                    }

                    var indentation = printer.get_full_indent(script_indent_level);

                    // if there is at least one empty line at the end of this text, strip it
                    // we'll be adding one back after the text but before the containing tag.
                    text = text.replace(/\n[ \t]*$/, '');

                    if (_beautifier) {

                        // call the Beautifier if avaliable
                        var Child_options = function() {
                            this.eol = '\n';
                        };
                        Child_options.prototype = this._options.raw_options;
                        var child_options = new Child_options();
                        text = _beautifier(indentation + text, child_options);
                    } else {
                        // simply indent the string otherwise
                        var white = text.match(/^\s*/)[0];
                        var _level = white.match(/[^\n\r]*$/)[0].split(this._options.indent_string).length - 1;
                        var reindent = this._get_full_indent(script_indent_level - _level);
                        text = (indentation + text.trim())
                            .replace(/\r\n|\r|\n/g, '\n' + reindent);
                    }
                    if (text) {
                        printer.print_raw_text(text);
                        printer.print_newline(true);
                    }
                }
            };

            Beautifier.prototype._handle_tag_open = function(printer, raw_token, last_tag_token, last_token) {
                var parser_token = this._get_tag_open_token(raw_token);

                if ((last_tag_token.is_unformatted || last_tag_token.is_content_unformatted) &&
                    raw_token.type === TOKEN.TAG_OPEN && raw_token.text.indexOf('</') === 0) {
                    // End element tags for unformatted or content_unformatted elements
                    // are printed raw to keep any newlines inside them exactly the same.
                    printer.add_raw_token(raw_token);
                } else {
                    printer.traverse_whitespace(raw_token);
                    this._set_tag_position(printer, raw_token, parser_token, last_tag_token, last_token);
                    printer.print_token(raw_token.text);
                }

                //indent attributes an auto, forced, aligned or forced-align line-wrap
                if (this._is_wrap_attributes_force_aligned || this._is_wrap_attributes_aligned_multiple || this._is_wrap_attributes_preserve_aligned) {
                    parser_token.alignment_size = raw_token.text.length + 1;
                }


                if (!parser_token.tag_complete && !parser_token.is_unformatted) {
                    printer.alignment_size = parser_token.alignment_size;
                }

                return parser_token;
            };

            var TagOpenParserToken = function(parent, raw_token) {
                this.parent = parent || null;
                this.text = '';
                this.type = 'TK_TAG_OPEN';
                this.tag_name = '';
                this.is_inline_element = false;
                this.is_unformatted = false;
                this.is_content_unformatted = false;
                this.is_empty_element = false;
                this.is_start_tag = false;
                this.is_end_tag = false;
                this.indent_content = false;
                this.multiline_content = false;
                this.custom_beautifier = false;
                this.start_tag_token = null;
                this.attr_count = 0;
                this.has_wrapped_attrs = false;
                this.alignment_size = 0;
                this.tag_complete = false;
                this.tag_start_char = '';
                this.tag_check = '';

                if (!raw_token) {
                    this.tag_complete = true;
                } else {
                    var tag_check_match;

                    this.tag_start_char = raw_token.text[0];
                    this.text = raw_token.text;

                    if (this.tag_start_char === '<') {
                        tag_check_match = raw_token.text.match(/^<([^\s>]*)/);
                        this.tag_check = tag_check_match ? tag_check_match[1] : '';
                    } else {
                        tag_check_match = raw_token.text.match(/^{{\#?([^\s}]+)/);
                        this.tag_check = tag_check_match ? tag_check_match[1] : '';
                    }
                    this.tag_check = this.tag_check.toLowerCase();

                    if (raw_token.type === TOKEN.COMMENT) {
                        this.tag_complete = true;
                    }

                    this.is_start_tag = this.tag_check.charAt(0) !== '/';
                    this.tag_name = !this.is_start_tag ? this.tag_check.substr(1) : this.tag_check;
                    this.is_end_tag = !this.is_start_tag ||
                        (raw_token.closed && raw_token.closed.text === '/>');

                    // handlebars tags that don't start with # or ^ are single_tags, and so also start and end.
                    this.is_end_tag = this.is_end_tag ||
                        (this.tag_start_char === '{' && (this.text.length < 3 || (/[^#\^]/.test(this.text.charAt(2)))));
                }
            };

            Beautifier.prototype._get_tag_open_token = function(raw_token) { //function to get a full tag and parse its type
                var parser_token = new TagOpenParserToken(this._tag_stack.get_parser_token(), raw_token);

                parser_token.alignment_size = this._options.wrap_attributes_indent_size;

                parser_token.is_end_tag = parser_token.is_end_tag ||
                    in_array(parser_token.tag_check, this._options.void_elements);

                parser_token.is_empty_element = parser_token.tag_complete ||
                    (parser_token.is_start_tag && parser_token.is_end_tag);

                parser_token.is_unformatted = !parser_token.tag_complete && in_array(parser_token.tag_check, this._options.unformatted);
                parser_token.is_content_unformatted = !parser_token.is_empty_element && in_array(parser_token.tag_check, this._options.content_unformatted);
                parser_token.is_inline_element = in_array(parser_token.tag_name, this._options.inline) || parser_token.tag_start_char === '{';

                return parser_token;
            };

            Beautifier.prototype._set_tag_position = function(printer, raw_token, parser_token, last_tag_token, last_token) {

                if (!parser_token.is_empty_element) {
                    if (parser_token.is_end_tag) { //this tag is a double tag so check for tag-ending
                        parser_token.start_tag_token = this._tag_stack.try_pop(parser_token.tag_name); //remove it and all ancestors
                    } else { // it's a start-tag
                        // check if this tag is starting an element that has optional end element
                        // and do an ending needed
                        this._do_optional_end_element(parser_token);

                        this._tag_stack.record_tag(parser_token); //push it on the tag stack

                        if ((parser_token.tag_name === 'script' || parser_token.tag_name === 'style') &&
                            !(parser_token.is_unformatted || parser_token.is_content_unformatted)) {
                            parser_token.custom_beautifier = uses_beautifier(parser_token.tag_check, raw_token);
                        }
                    }
                }

                if (in_array(parser_token.tag_check, this._options.extra_liners)) { //check if this double needs an extra line
                    printer.print_newline(false);
                    if (!printer._output.just_added_blankline()) {
                        printer.print_newline(true);
                    }
                }

                if (parser_token.is_empty_element) { //if this tag name is a single tag type (either in the list or has a closing /)

                    // if you hit an else case, reset the indent level if you are inside an:
                    // 'if', 'unless', or 'each' block.
                    if (parser_token.tag_start_char === '{' && parser_token.tag_check === 'else') {
                        this._tag_stack.indent_to_tag(['if', 'unless', 'each']);
                        parser_token.indent_content = true;
                        // Don't add a newline if opening {{#if}} tag is on the current line
                        var foundIfOnCurrentLine = printer.current_line_has_match(/{{#if/);
                        if (!foundIfOnCurrentLine) {
                            printer.print_newline(false);
                        }
                    }

                    // Don't add a newline before elements that should remain where they are.
                    if (parser_token.tag_name === '!--' && last_token.type === TOKEN.TAG_CLOSE &&
                        last_tag_token.is_end_tag && parser_token.text.indexOf('\n') === -1) {
                        //Do nothing. Leave comments on same line.
                    } else if (!parser_token.is_inline_element && !parser_token.is_unformatted) {
                        printer.print_newline(false);
                    }
                } else if (parser_token.is_unformatted || parser_token.is_content_unformatted) {
                    if (!parser_token.is_inline_element && !parser_token.is_unformatted) {
                        printer.print_newline(false);
                    }
                } else if (parser_token.is_end_tag) { //this tag is a double tag so check for tag-ending
                    if ((parser_token.start_tag_token && parser_token.start_tag_token.multiline_content) ||
                        !(parser_token.is_inline_element ||
                            (last_tag_token.is_inline_element) ||
                            (last_token.type === TOKEN.TAG_CLOSE &&
                                parser_token.start_tag_token === last_tag_token) ||
                            (last_token.type === 'TK_CONTENT')
                        )) {
                        printer.print_newline(false);
                    }
                } else { // it's a start-tag
                    parser_token.indent_content = !parser_token.custom_beautifier;

                    if (parser_token.tag_start_char === '<') {
                        if (parser_token.tag_name === 'html') {
                            parser_token.indent_content = this._options.indent_inner_html;
                        } else if (parser_token.tag_name === 'head') {
                            parser_token.indent_content = this._options.indent_head_inner_html;
                        } else if (parser_token.tag_name === 'body') {
                            parser_token.indent_content = this._options.indent_body_inner_html;
                        }
                    }

                    if (!parser_token.is_inline_element && last_token.type !== 'TK_CONTENT') {
                        if (parser_token.parent) {
                            parser_token.parent.multiline_content = true;
                        }
                        printer.print_newline(false);
                    }
                }
            };

//To be used for <p> tag special case:
//var p_closers = ['address', 'article', 'aside', 'blockquote', 'details', 'div', 'dl', 'fieldset', 'figcaption', 'figure', 'footer', 'form', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'header', 'hr', 'main', 'nav', 'ol', 'p', 'pre', 'section', 'table', 'ul'];

            Beautifier.prototype._do_optional_end_element = function(parser_token) {
                // NOTE: cases of "if there is no more content in the parent element"
                // are handled automatically by the beautifier.
                // It assumes parent or ancestor close tag closes all children.
                // https://www.w3.org/TR/html5/syntax.html#optional-tags
                if (parser_token.is_empty_element || !parser_token.is_start_tag || !parser_token.parent) {
                    return;

                } else if (parser_token.tag_name === 'body') {
                    // A head elementâ€™s end tag may be omitted if the head element is not immediately followed by a space character or a comment.
                    this._tag_stack.try_pop('head');

                    //} else if (parser_token.tag_name === 'body') {
                    // DONE: A body elementâ€™s end tag may be omitted if the body element is not immediately followed by a comment.

                } else if (parser_token.tag_name === 'li') {
                    // An li elementâ€™s end tag may be omitted if the li element is immediately followed by another li element or if there is no more content in the parent element.
                    this._tag_stack.try_pop('li', ['ol', 'ul']);

                } else if (parser_token.tag_name === 'dd' || parser_token.tag_name === 'dt') {
                    // A dd elementâ€™s end tag may be omitted if the dd element is immediately followed by another dd element or a dt element, or if there is no more content in the parent element.
                    // A dt elementâ€™s end tag may be omitted if the dt element is immediately followed by another dt element or a dd element.
                    this._tag_stack.try_pop('dt', ['dl']);
                    this._tag_stack.try_pop('dd', ['dl']);

                    //} else if (p_closers.indexOf(parser_token.tag_name) !== -1) {
                    //TODO: THIS IS A BUG FARM. We are not putting this into 1.8.0 as it is likely to blow up.
                    //A p elementâ€™s end tag may be omitted if the p element is immediately followed by an address, article, aside, blockquote, details, div, dl, fieldset, figcaption, figure, footer, form, h1, h2, h3, h4, h5, h6, header, hr, main, nav, ol, p, pre, section, table, or ul element, or if there is no more content in the parent element and the parent element is an HTML element that is not an a, audio, del, ins, map, noscript, or video element, or an autonomous custom element.
                    //this._tag_stack.try_pop('p', ['body']);

                } else if (parser_token.tag_name === 'rp' || parser_token.tag_name === 'rt') {
                    // An rt elementâ€™s end tag may be omitted if the rt element is immediately followed by an rt or rp element, or if there is no more content in the parent element.
                    // An rp elementâ€™s end tag may be omitted if the rp element is immediately followed by an rt or rp element, or if there is no more content in the parent element.
                    this._tag_stack.try_pop('rt', ['ruby', 'rtc']);
                    this._tag_stack.try_pop('rp', ['ruby', 'rtc']);

                } else if (parser_token.tag_name === 'optgroup') {
                    // An optgroup elementâ€™s end tag may be omitted if the optgroup element is immediately followed by another optgroup element, or if there is no more content in the parent element.
                    // An option elementâ€™s end tag may be omitted if the option element is immediately followed by another option element, or if it is immediately followed by an optgroup element, or if there is no more content in the parent element.
                    this._tag_stack.try_pop('optgroup', ['select']);
                    //this._tag_stack.try_pop('option', ['select']);

                } else if (parser_token.tag_name === 'option') {
                    // An option elementâ€™s end tag may be omitted if the option element is immediately followed by another option element, or if it is immediately followed by an optgroup element, or if there is no more content in the parent element.
                    this._tag_stack.try_pop('option', ['select', 'datalist', 'optgroup']);

                } else if (parser_token.tag_name === 'colgroup') {
                    // DONE: A colgroup elementâ€™s end tag may be omitted if the colgroup element is not immediately followed by a space character or a comment.
                    // A caption element's end tag may be ommitted if a colgroup, thead, tfoot, tbody, or tr element is started.
                    this._tag_stack.try_pop('caption', ['table']);

                } else if (parser_token.tag_name === 'thead') {
                    // A colgroup element's end tag may be ommitted if a thead, tfoot, tbody, or tr element is started.
                    // A caption element's end tag may be ommitted if a colgroup, thead, tfoot, tbody, or tr element is started.
                    this._tag_stack.try_pop('caption', ['table']);
                    this._tag_stack.try_pop('colgroup', ['table']);

                    //} else if (parser_token.tag_name === 'caption') {
                    // DONE: A caption elementâ€™s end tag may be omitted if the caption element is not immediately followed by a space character or a comment.

                } else if (parser_token.tag_name === 'tbody' || parser_token.tag_name === 'tfoot') {
                    // A thead elementâ€™s end tag may be omitted if the thead element is immediately followed by a tbody or tfoot element.
                    // A tbody elementâ€™s end tag may be omitted if the tbody element is immediately followed by a tbody or tfoot element, or if there is no more content in the parent element.
                    // A colgroup element's end tag may be ommitted if a thead, tfoot, tbody, or tr element is started.
                    // A caption element's end tag may be ommitted if a colgroup, thead, tfoot, tbody, or tr element is started.
                    this._tag_stack.try_pop('caption', ['table']);
                    this._tag_stack.try_pop('colgroup', ['table']);
                    this._tag_stack.try_pop('thead', ['table']);
                    this._tag_stack.try_pop('tbody', ['table']);

                    //} else if (parser_token.tag_name === 'tfoot') {
                    // DONE: A tfoot elementâ€™s end tag may be omitted if there is no more content in the parent element.

                } else if (parser_token.tag_name === 'tr') {
                    // A tr elementâ€™s end tag may be omitted if the tr element is immediately followed by another tr element, or if there is no more content in the parent element.
                    // A colgroup element's end tag may be ommitted if a thead, tfoot, tbody, or tr element is started.
                    // A caption element's end tag may be ommitted if a colgroup, thead, tfoot, tbody, or tr element is started.
                    this._tag_stack.try_pop('caption', ['table']);
                    this._tag_stack.try_pop('colgroup', ['table']);
                    this._tag_stack.try_pop('tr', ['table', 'thead', 'tbody', 'tfoot']);

                } else if (parser_token.tag_name === 'th' || parser_token.tag_name === 'td') {
                    // A td elementâ€™s end tag may be omitted if the td element is immediately followed by a td or th element, or if there is no more content in the parent element.
                    // A th elementâ€™s end tag may be omitted if the th element is immediately followed by a td or th element, or if there is no more content in the parent element.
                    this._tag_stack.try_pop('td', ['tr']);
                    this._tag_stack.try_pop('th', ['tr']);
                }

                // Start element omission not handled currently
                // A head elementâ€™s start tag may be omitted if the element is empty, or if the first thing inside the head element is an element.
                // A tbody elementâ€™s start tag may be omitted if the first thing inside the tbody element is a tr element, and if the element is not immediately preceded by a tbody, thead, or tfoot element whose end tag has been omitted. (It canâ€™t be omitted if the element is empty.)
                // A colgroup elementâ€™s start tag may be omitted if the first thing inside the colgroup element is a col element, and if the element is not immediately preceded by another colgroup element whose end tag has been omitted. (It canâ€™t be omitted if the element is empty.)

                // Fix up the parent of the parser token
                parser_token.parent = this._tag_stack.get_parser_token();

            };

            module.exports.Beautifier = Beautifier;


            /***/ }),
        /* 17 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            var BaseOptions = __webpack_require__(6).Options;

            function Options(options) {
                BaseOptions.call(this, options, 'html');

                this.indent_inner_html = this._get_boolean('indent_inner_html');
                this.indent_body_inner_html = this._get_boolean('indent_body_inner_html', true);
                this.indent_head_inner_html = this._get_boolean('indent_head_inner_html', true);

                this.indent_handlebars = this._get_boolean('indent_handlebars', true);
                this.wrap_attributes = this._get_selection('wrap_attributes',
                    ['auto', 'force', 'force-aligned', 'force-expand-multiline', 'aligned-multiple', 'preserve', 'preserve-aligned']);
                this.wrap_attributes_indent_size = this._get_number('wrap_attributes_indent_size', this.indent_size);
                this.extra_liners = this._get_array('extra_liners', ['head', 'body', '/html']);

                this.inline = this._get_array('inline', [
                    // https://www.w3.org/TR/html5/dom.html#phrasing-content
                    'a', 'abbr', 'area', 'audio', 'b', 'bdi', 'bdo', 'br', 'button', 'canvas', 'cite',
                    'code', 'data', 'datalist', 'del', 'dfn', 'em', 'embed', 'i', 'iframe', 'img',
                    'input', 'ins', 'kbd', 'keygen', 'label', 'map', 'mark', 'math', 'meter', 'noscript',
                    'object', 'output', 'progress', 'q', 'ruby', 's', 'samp', /* 'script', */ 'select', 'small',
                    'span', 'strong', 'sub', 'sup', 'svg', 'template', 'textarea', 'time', 'u', 'var',
                    'video', 'wbr', 'text',
                    // prexisting - not sure of full effect of removing, leaving in
                    'acronym', 'address', 'big', 'dt', 'ins', 'strike', 'tt'
                ]);
                this.void_elements = this._get_array('void_elements', [
                    // HTLM void elements - aka self-closing tags - aka singletons
                    // https://www.w3.org/html/wg/drafts/html/master/syntax.html#void-elements
                    'area', 'base', 'br', 'col', 'embed', 'hr', 'img', 'input', 'keygen',
                    'link', 'menuitem', 'meta', 'param', 'source', 'track', 'wbr',
                    // NOTE: Optional tags are too complex for a simple list
                    // they are hard coded in _do_optional_end_element

                    // Doctype and xml elements
                    '!doctype', '?xml',
                    // ?php and ?= tags
                    '?php', '?=',
                    // other tags that were in this list, keeping just in case
                    'basefont', 'isindex'
                ]);
                this.unformatted = this._get_array('unformatted', []);
                this.content_unformatted = this._get_array('content_unformatted', [
                    'pre', 'textarea'
                ]);
                this.indent_scripts = this._get_selection('indent_scripts', ['normal', 'keep', 'separate']);
            }
            Options.prototype = new BaseOptions();



            module.exports.Options = Options;


            /***/ }),
        /* 18 */
        /***/ (function(module, exports, __webpack_require__) {

            "use strict";
            /*jshint node:true */
            /*

              The MIT License (MIT)

              Copyright (c) 2007-2018 Einar Lielmanis, Liam Newman, and contributors.

              Permission is hereby granted, free of charge, to any person
              obtaining a copy of this software and associated documentation files
              (the "Software"), to deal in the Software without restriction,
              including without limitation the rights to use, copy, modify, merge,
              publish, distribute, sublicense, and/or sell copies of the Software,
              and to permit persons to whom the Software is furnished to do so,
              subject to the following conditions:

              The above copyright notice and this permission notice shall be
              included in all copies or substantial portions of the Software.

              THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
              EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
              MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
              NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
              BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
              ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
              CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
              SOFTWARE.
            */



            var BaseTokenizer = __webpack_require__(9).Tokenizer;
            var BASETOKEN = __webpack_require__(9).TOKEN;
            var Directives = __webpack_require__(11).Directives;

            var TOKEN = {
                TAG_OPEN: 'TK_TAG_OPEN',
                TAG_CLOSE: 'TK_TAG_CLOSE',
                ATTRIBUTE: 'TK_ATTRIBUTE',
                EQUALS: 'TK_EQUALS',
                VALUE: 'TK_VALUE',
                COMMENT: 'TK_COMMENT',
                TEXT: 'TK_TEXT',
                UNKNOWN: 'TK_UNKNOWN',
                START: BASETOKEN.START,
                RAW: BASETOKEN.RAW,
                EOF: BASETOKEN.EOF
            };

            var directives_core = new Directives(/<\!--/, /-->/);

            var Tokenizer = function(input_string, options) {
                BaseTokenizer.call(this, input_string, options);
                this._current_tag_name = '';

                // Words end at whitespace or when a tag starts
                // if we are indenting handlebars, they are considered tags
                this._word_pattern = this._options.indent_handlebars ? /[\n\r\t <]|{{/g : /[\n\r\t <]/g;
            };
            Tokenizer.prototype = new BaseTokenizer();

            Tokenizer.prototype._is_comment = function(current_token) { // jshint unused:false
                return false; //current_token.type === TOKEN.COMMENT || current_token.type === TOKEN.UNKNOWN;
            };

            Tokenizer.prototype._is_opening = function(current_token) {
                return current_token.type === TOKEN.TAG_OPEN;
            };

            Tokenizer.prototype._is_closing = function(current_token, open_token) {
                return current_token.type === TOKEN.TAG_CLOSE &&
                    (open_token && (
                        ((current_token.text === '>' || current_token.text === '/>') && open_token.text[0] === '<') ||
                        (current_token.text === '}}' && open_token.text[0] === '{' && open_token.text[1] === '{')));
            };

            Tokenizer.prototype._reset = function() {
                this._current_tag_name = '';
            };

            Tokenizer.prototype._get_next_token = function(previous_token, open_token) { // jshint unused:false
                this._readWhitespace();
                var token = null;
                var c = this._input.peek();

                if (c === null) {
                    return this._create_token(TOKEN.EOF, '');
                }

                token = token || this._read_attribute(c, previous_token, open_token);
                token = token || this._read_raw_content(previous_token, open_token);
                token = token || this._read_comment(c);
                token = token || this._read_open(c, open_token);
                token = token || this._read_close(c, open_token);
                token = token || this._read_content_word();
                token = token || this._create_token(TOKEN.UNKNOWN, this._input.next());

                return token;
            };

            Tokenizer.prototype._read_comment = function(c) { // jshint unused:false
                var token = null;
                if (c === '<' || c === '{') {
                    var peek1 = this._input.peek(1);
                    var peek2 = this._input.peek(2);
                    if ((c === '<' && (peek1 === '!' || peek1 === '?' || peek1 === '%')) ||
                        this._options.indent_handlebars && c === '{' && peek1 === '{' && peek2 === '!') {
                        //if we're in a comment, do something special
                        // We treat all comments as literals, even more than preformatted tags
                        // we just look for the appropriate close tag

                        // this is will have very poor perf, but will work for now.
                        var comment = '',
                            delimiter = '>',
                            matched = false;

                        var input_char = this._input.next();

                        while (input_char) {
                            comment += input_char;

                            // only need to check for the delimiter if the last chars match
                            if (comment.charAt(comment.length - 1) === delimiter.charAt(delimiter.length - 1) &&
                                comment.indexOf(delimiter) !== -1) {
                                break;
                            }

                            // only need to search for custom delimiter for the first few characters
                            if (!matched) {
                                matched = comment.length > 10;
                                if (comment.indexOf('<![if') === 0) { //peek for <![if conditional comment
                                    delimiter = '<![endif]>';
                                    matched = true;
                                } else if (comment.indexOf('<![cdata[') === 0) { //if it's a <[cdata[ comment...
                                    delimiter = ']]>';
                                    matched = true;
                                } else if (comment.indexOf('<![') === 0) { // some other ![ comment? ...
                                    delimiter = ']>';
                                    matched = true;
                                } else if (comment.indexOf('<!--') === 0) { // <!-- comment ...
                                    delimiter = '-->';
                                    matched = true;
                                } else if (comment.indexOf('{{!--') === 0) { // {{!-- handlebars comment
                                    delimiter = '--}}';
                                    matched = true;
                                } else if (comment.indexOf('{{!') === 0) { // {{! handlebars comment
                                    if (comment.length === 5 && comment.indexOf('{{!--') === -1) {
                                        delimiter = '}}';
                                        matched = true;
                                    }
                                } else if (comment.indexOf('<?') === 0) { // {{! handlebars comment
                                    delimiter = '?>';
                                    matched = true;
                                } else if (comment.indexOf('<%') === 0) { // {{! handlebars comment
                                    delimiter = '%>';
                                    matched = true;
                                }
                            }

                            input_char = this._input.next();
                        }

                        var directives = directives_core.get_directives(comment);
                        if (directives && directives.ignore === 'start') {
                            comment += directives_core.readIgnored(this._input);
                        }
                        token = this._create_token(TOKEN.COMMENT, comment);
                        token.directives = directives;
                    }
                }

                return token;
            };

            Tokenizer.prototype._read_open = function(c, open_token) {
                var resulting_string = null;
                var token = null;
                if (!open_token) {
                    if (c === '<') {
                        resulting_string = this._input.read(/<(?:[^\n\r\t >{][^\n\r\t >{/]*)?/g);
                        token = this._create_token(TOKEN.TAG_OPEN, resulting_string);
                    } else if (this._options.indent_handlebars && c === '{' && this._input.peek(1) === '{') {
                        resulting_string = this._input.readUntil(/[\n\r\t }]/g);
                        token = this._create_token(TOKEN.TAG_OPEN, resulting_string);
                    }
                }
                return token;
            };

            Tokenizer.prototype._read_close = function(c, open_token) {
                var resulting_string = null;
                var token = null;
                if (open_token) {
                    if (open_token.text[0] === '<' && (c === '>' || (c === '/' && this._input.peek(1) === '>'))) {
                        resulting_string = this._input.next();
                        if (c === '/') { //  for close tag "/>"
                            resulting_string += this._input.next();
                        }
                        token = this._create_token(TOKEN.TAG_CLOSE, resulting_string);
                    } else if (open_token.text[0] === '{' && c === '}' && this._input.peek(1) === '}') {
                        this._input.next();
                        this._input.next();
                        token = this._create_token(TOKEN.TAG_CLOSE, '}}');
                    }
                }

                return token;
            };

            Tokenizer.prototype._read_attribute = function(c, previous_token, open_token) {
                var token = null;
                var resulting_string = '';
                if (open_token && open_token.text[0] === '<') {

                    if (c === '=') {
                        token = this._create_token(TOKEN.EQUALS, this._input.next());
                    } else if (c === '"' || c === "'") {
                        var content = this._input.next();
                        var input_string = '';
                        var string_pattern = new RegExp(c + '|{{', 'g');
                        while (this._input.hasNext()) {
                            input_string = this._input.readUntilAfter(string_pattern);
                            content += input_string;
                            if (input_string[input_string.length - 1] === '"' || input_string[input_string.length - 1] === "'") {
                                break;
                            } else if (this._input.hasNext()) {
                                content += this._input.readUntilAfter(/}}/g);
                            }
                        }

                        token = this._create_token(TOKEN.VALUE, content);
                    } else {
                        if (c === '{' && this._input.peek(1) === '{') {
                            resulting_string = this._input.readUntilAfter(/}}/g);
                        } else {
                            resulting_string = this._input.readUntil(/[\n\r\t =\/>]/g);
                        }

                        if (resulting_string) {
                            if (previous_token.type === TOKEN.EQUALS) {
                                token = this._create_token(TOKEN.VALUE, resulting_string);
                            } else {
                                token = this._create_token(TOKEN.ATTRIBUTE, resulting_string);
                            }
                        }
                    }
                }
                return token;
            };

            Tokenizer.prototype._is_content_unformatted = function(tag_name) {
                // void_elements have no content and so cannot have unformatted content
                // script and style tags should always be read as unformatted content
                // finally content_unformatted and unformatted element contents are unformatted
                return this._options.void_elements.indexOf(tag_name) === -1 &&
                    (tag_name === 'script' || tag_name === 'style' ||
                        this._options.content_unformatted.indexOf(tag_name) !== -1 ||
                        this._options.unformatted.indexOf(tag_name) !== -1);
            };


            Tokenizer.prototype._read_raw_content = function(previous_token, open_token) { // jshint unused:false
                var resulting_string = '';
                if (open_token && open_token.text[0] === '{') {
                    resulting_string = this._input.readUntil(/}}/g);
                } else if (previous_token.type === TOKEN.TAG_CLOSE && (previous_token.opened.text[0] === '<')) {
                    var tag_name = previous_token.opened.text.substr(1).toLowerCase();
                    if (this._is_content_unformatted(tag_name)) {
                        resulting_string = this._input.readUntil(new RegExp('</' + tag_name + '[\\n\\r\\t ]*?>', 'ig'));
                    }
                }

                if (resulting_string) {
                    return this._create_token(TOKEN.TEXT, resulting_string);
                }

                return null;
            };

            Tokenizer.prototype._read_content_word = function() {
                // if we get here and we see handlebars treat them as plain text
                var resulting_string = this._input.readUntil(this._word_pattern);
                if (resulting_string) {
                    return this._create_token(TOKEN.TEXT, resulting_string);
                }
            };

            module.exports.Tokenizer = Tokenizer;
            module.exports.TOKEN = TOKEN;


            /***/ })
        /******/ ]);
    var style_html = legacy_beautify_html;
    /* Footer */
    if (typeof define === "function" && define.amd) {
        // Add support for AMD ( https://github.com/amdjs/amdjs-api/wiki/AMD#defineamd-property- )
        define(["require", "./beautify", "./beautify-css"], function(requireamd) {
            var js_beautify = requireamd("./beautify");
            var css_beautify = requireamd("./beautify-css");

            return {
                html_beautify: function(html_source, options) {
                    return style_html(html_source, options, js_beautify.js_beautify, css_beautify.css_beautify);
                }
            };
        });
    } else if (typeof exports !== "undefined") {
        // Add support for CommonJS. Just put this file somewhere on your require.paths
        // and you will be able to `var html_beautify = require("beautify").html_beautify`.
        var js_beautify = require('./beautify.js');
        var css_beautify = require('./beautify-css.js');

        exports.html_beautify = function(html_source, options) {
            return style_html(html_source, options, js_beautify.js_beautify, css_beautify.css_beautify);
        };
    } else if (typeof window !== "undefined") {
        // If we're running a web page and don't have either of the above, add our one global
        window.html_beautify = function(html_source, options) {
            return style_html(html_source, options, window.js_beautify, window.css_beautify);
        };
    } else if (typeof global !== "undefined") {
        // If we don't even have window, try global.
        global.html_beautify = function(html_source, options) {
            return style_html(html_source, options, global.js_beautify, global.css_beautify);
        };
    }

}());
/**
 * @license
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @fileoverview
 * some functions for browser-side pretty printing of code contained in html.
 *
 * <p>
 * For a fairly comprehensive set of languages see the
 * <a href="https://github.com/google/code-prettify#for-which-languages-does-it-work">README</a>
 * file that came with this source.  At a minimum, the lexer should work on a
 * number of languages including C and friends, Java, Python, Bash, SQL, HTML,
 * XML, CSS, Javascript, and Makefiles.  It works passably on Ruby, PHP and Awk
 * and a subset of Perl, but, because of commenting conventions, doesn't work on
 * Smalltalk, Lisp-like, or CAML-like languages without an explicit lang class.
 * <p>
 * Usage: <ol>
 * <li> include this source file in an html page via
 *   {@code <script type="text/javascript" src="/path/to/prettify.js"></script>}
 * <li> define style rules.  See the example page for examples.
 * <li> mark the {@code <pre>} and {@code <code>} tags in your source with
 *    {@code class=prettyprint.}
 *    You can also use the (html deprecated) {@code <xmp>} tag, but the pretty
 *    printer needs to do more substantial DOM manipulations to support that, so
 *    some css styles may not be preserved.
 * </ol>
 * That's it.  I wanted to keep the API as simple as possible, so there's no
 * need to specify which language the code is in, but if you wish, you can add
 * another class to the {@code <pre>} or {@code <code>} element to specify the
 * language, as in {@code <pre class="prettyprint lang-java">}.  Any class that
 * starts with "lang-" followed by a file extension, specifies the file type.
 * See the "lang-*.js" files in this directory for code that implements
 * per-language file handlers.
 * <p>
 * Change log:<br>
 * cbeust, 2006/08/22
 * <blockquote>
 *   Java annotations (start with "@") are now captured as literals ("lit")
 * </blockquote>
 * @requires console
 */

// JSLint declarations
/*global console, document, navigator, setTimeout, window, define */

/**
 * @typedef {!Array.<number|string>}
 * Alternating indices and the decorations that should be inserted there.
 * The indices are monotonically increasing.
 */
var DecorationsT;

/**
 * @typedef {!{
 *   sourceNode: !Element,
 *   pre: !(number|boolean),
 *   langExtension: ?string,
 *   numberLines: ?(number|boolean),
 *   sourceCode: ?string,
 *   spans: ?(Array.<number|Node>),
 *   basePos: ?number,
 *   decorations: ?DecorationsT
 * }}
 * <dl>
 *  <dt>sourceNode<dd>the element containing the source
 *  <dt>sourceCode<dd>source as plain text
 *  <dt>pre<dd>truthy if white-space in text nodes
 *     should be considered significant.
 *  <dt>spans<dd> alternating span start indices into source
 *     and the text node or element (e.g. {@code <BR>}) corresponding to that
 *     span.
 *  <dt>decorations<dd>an array of style classes preceded
 *     by the position at which they start in job.sourceCode in order
 *  <dt>basePos<dd>integer position of this.sourceCode in the larger chunk of
 *     source.
 * </dl>
 */
var JobT;

/**
 * @typedef {!{
 *   sourceCode: string,
 *   spans: !(Array.<number|Node>)
 * }}
 * <dl>
 *  <dt>sourceCode<dd>source as plain text
 *  <dt>spans<dd> alternating span start indices into source
 *     and the text node or element (e.g. {@code <BR>}) corresponding to that
 *     span.
 * </dl>
 */
var SourceSpansT;

/** @define {boolean} */
var IN_GLOBAL_SCOPE = true;


/**
 * {@type !{
 *   'createSimpleLexer': function (Array, Array): (function (JobT)),
 *   'registerLangHandler': function (function (JobT), Array.<string>),
 *   'PR_ATTRIB_NAME': string,
 *   'PR_ATTRIB_NAME': string,
 *   'PR_ATTRIB_VALUE': string,
 *   'PR_COMMENT': string,
 *   'PR_DECLARATION': string,
 *   'PR_KEYWORD': string,
 *   'PR_LITERAL': string,
 *   'PR_NOCODE': string,
 *   'PR_PLAIN': string,
 *   'PR_PUNCTUATION': string,
 *   'PR_SOURCE': string,
 *   'PR_STRING': string,
 *   'PR_TAG': string,
 *   'PR_TYPE': string,
 *   'prettyPrintOne': function (string, string, number|boolean),
 *   'prettyPrint': function (?function, ?(HTMLElement|HTMLDocument))
 * }}
 * @const
 */
var PR;

/**
 * Split {@code prettyPrint} into multiple timeouts so as not to interfere with
 * UI events.
 * If set to {@code false}, {@code prettyPrint()} is synchronous.
 */
var PR_SHOULD_USE_CONTINUATION = true
if (typeof window !== 'undefined') {
    window['PR_SHOULD_USE_CONTINUATION'] = PR_SHOULD_USE_CONTINUATION;
}

/**
 * Pretty print a chunk of code.
 * @param {string} sourceCodeHtml The HTML to pretty print.
 * @param {string} opt_langExtension The language name to use.
 *     Typically, a filename extension like 'cpp' or 'java'.
 * @param {number|boolean} opt_numberLines True to number lines,
 *     or the 1-indexed number of the first line in sourceCodeHtml.
 * @return {string} code as html, but prettier
 */
var prettyPrintOne;
/**
 * Find all the {@code <pre>} and {@code <code>} tags in the DOM with
 * {@code class=prettyprint} and prettify them.
 *
 * @param {Function} opt_whenDone called when prettifying is done.
 * @param {HTMLElement|HTMLDocument} opt_root an element or document
 *   containing all the elements to pretty print.
 *   Defaults to {@code document.body}.
 */
var prettyPrint;


(function () {
    var win = (typeof window !== 'undefined') ? window : {};
    // Keyword lists for various languages.
    // We use things that coerce to strings to make them compact when minified
    // and to defeat aggressive optimizers that fold large string constants.
    var FLOW_CONTROL_KEYWORDS = ["break,continue,do,else,for,if,return,while"];
    var C_KEYWORDS = [FLOW_CONTROL_KEYWORDS,"auto,case,char,const,default," +
    "double,enum,extern,float,goto,inline,int,long,register,restrict,short,signed," +
    "sizeof,static,struct,switch,typedef,union,unsigned,void,volatile"];
    var COMMON_KEYWORDS = [C_KEYWORDS,"catch,class,delete,false,import," +
    "new,operator,private,protected,public,this,throw,true,try,typeof"];
    var CPP_KEYWORDS = [COMMON_KEYWORDS,"alignas,alignof,align_union,asm,axiom,bool," +
    "concept,concept_map,const_cast,constexpr,decltype,delegate," +
    "dynamic_cast,explicit,export,friend,generic,late_check," +
    "mutable,namespace,noexcept,noreturn,nullptr,property,reinterpret_cast,static_assert," +
    "static_cast,template,typeid,typename,using,virtual,where"];
    var JAVA_KEYWORDS = [COMMON_KEYWORDS,
        "abstract,assert,boolean,byte,extends,finally,final,implements,import," +
        "instanceof,interface,null,native,package,strictfp,super,synchronized," +
        "throws,transient"];
    var CSHARP_KEYWORDS = [COMMON_KEYWORDS,
        "abstract,add,alias,as,ascending,async,await,base,bool,by,byte,checked,decimal,delegate,descending," +
        "dynamic,event,finally,fixed,foreach,from,get,global,group,implicit,in,interface," +
        "internal,into,is,join,let,lock,null,object,out,override,orderby,params," +
        "partial,readonly,ref,remove,sbyte,sealed,select,set,stackalloc,string,select,uint,ulong," +
        "unchecked,unsafe,ushort,value,var,virtual,where,yield"];
    var COFFEE_KEYWORDS = "all,and,by,catch,class,else,extends,false,finally," +
        "for,if,in,is,isnt,loop,new,no,not,null,of,off,on,or,return,super,then," +
        "throw,true,try,unless,until,when,while,yes";
    var JSCRIPT_KEYWORDS = [COMMON_KEYWORDS,
        "abstract,async,await,constructor,debugger,enum,eval,export,from,function," +
        "get,import,implements,instanceof,interface,let,null,of,set,undefined," +
        "var,with,yield,Infinity,NaN"];
    var PERL_KEYWORDS = "caller,delete,die,do,dump,elsif,eval,exit,foreach,for," +
        "goto,if,import,last,local,my,next,no,our,print,package,redo,require," +
        "sub,undef,unless,until,use,wantarray,while,BEGIN,END";
    var PYTHON_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "and,as,assert,class,def,del," +
    "elif,except,exec,finally,from,global,import,in,is,lambda," +
    "nonlocal,not,or,pass,print,raise,try,with,yield," +
    "False,True,None"];
    var RUBY_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "alias,and,begin,case,class," +
    "def,defined,elsif,end,ensure,false,in,module,next,nil,not,or,redo," +
    "rescue,retry,self,super,then,true,undef,unless,until,when,yield," +
    "BEGIN,END"];
    var SH_KEYWORDS = [FLOW_CONTROL_KEYWORDS, "case,done,elif,esac,eval,fi," +
    "function,in,local,set,then,until"];
    var ALL_KEYWORDS = [
        CPP_KEYWORDS, CSHARP_KEYWORDS, JAVA_KEYWORDS, JSCRIPT_KEYWORDS,
        PERL_KEYWORDS, PYTHON_KEYWORDS, RUBY_KEYWORDS, SH_KEYWORDS];
    var C_TYPES = /^(DIR|FILE|array|vector|(de|priority_)?queue|(forward_)?list|stack|(const_)?(reverse_)?iterator|(unordered_)?(multi)?(set|map)|bitset|u?(int|float)\d*)\b/;

    // token style names.  correspond to css classes
    /**
     * token style for a string literal
     * @const
     */
    var PR_STRING = 'str';
    /**
     * token style for a keyword
     * @const
     */
    var PR_KEYWORD = 'kwd';
    /**
     * token style for a comment
     * @const
     */
    var PR_COMMENT = 'com';
    /**
     * token style for a type
     * @const
     */
    var PR_TYPE = 'typ';
    /**
     * token style for a literal value.  e.g. 1, null, true.
     * @const
     */
    var PR_LITERAL = 'lit';
    /**
     * token style for a punctuation string.
     * @const
     */
    var PR_PUNCTUATION = 'pun';
    /**
     * token style for plain text.
     * @const
     */
    var PR_PLAIN = 'pln';

    /**
     * token style for an sgml tag.
     * @const
     */
    var PR_TAG = 'tag';
    /**
     * token style for a markup declaration such as a DOCTYPE.
     * @const
     */
    var PR_DECLARATION = 'dec';
    /**
     * token style for embedded source.
     * @const
     */
    var PR_SOURCE = 'src';
    /**
     * token style for an sgml attribute name.
     * @const
     */
    var PR_ATTRIB_NAME = 'atn';
    /**
     * token style for an sgml attribute value.
     * @const
     */
    var PR_ATTRIB_VALUE = 'atv';

    /**
     * A class that indicates a section of markup that is not code, e.g. to allow
     * embedding of line numbers within code listings.
     * @const
     */
    var PR_NOCODE = 'nocode';


    // Regex pattern below is automatically generated by regexpPrecederPatterns.pl
    // Do not modify, your changes will be erased.

    // CAVEAT: this does not properly handle the case where a regular
    // expression immediately follows another since a regular expression may
    // have flags for case-sensitivity and the like.  Having regexp tokens
    // adjacent is not valid in any language I'm aware of, so I'm punting.
    // TODO: maybe style special characters inside a regexp as punctuation.

    /**
     * A set of tokens that can precede a regular expression literal in
     * javascript
     * http://web.archive.org/web/20070717142515/http://www.mozilla.org/js/language/js20/rationale/syntax.html
     * has the full list, but I've removed ones that might be problematic when
     * seen in languages that don't support regular expression literals.
     *
     * Specifically, I've removed any keywords that can't precede a regexp
     * literal in a syntactically legal javascript program, and I've removed the
     * "in" keyword since it's not a keyword in many languages, and might be used
     * as a count of inches.
     *
     * The link above does not accurately describe EcmaScript rules since
     * it fails to distinguish between (a=++/b/i) and (a++/b/i) but it works
     * very well in practice.
     *
     * @private
     * @const
     */
    var REGEXP_PRECEDER_PATTERN = '(?:^^\\.?|[+-]|[!=]=?=?|\\#|%=?|&&?=?|\\(|\\*=?|[+\\-]=|->|\\/=?|::?|<<?=?|>>?>?=?|,|;|\\?|@|\\[|~|{|\\^\\^?=?|\\|\\|?=?|break|case|continue|delete|do|else|finally|instanceof|return|throw|try|typeof)\\s*';


    /**
     * Given a group of {@link RegExp}s, returns a {@code RegExp} that globally
     * matches the union of the sets of strings matched by the input RegExp.
     * Since it matches globally, if the input strings have a start-of-input
     * anchor (/^.../), it is ignored for the purposes of unioning.
     * @param {Array.<RegExp>} regexs non multiline, non-global regexs.
     * @return {RegExp} a global regex.
     */
    function combinePrefixPatterns(regexs) {
        var capturedGroupIndex = 0;

        var needToFoldCase = false;
        var ignoreCase = false;
        for (var i = 0, n = regexs.length; i < n; ++i) {
            var regex = regexs[i];
            if (regex.ignoreCase) {
                ignoreCase = true;
            } else if (/[a-z]/i.test(regex.source.replace(
                /\\u[0-9a-f]{4}|\\x[0-9a-f]{2}|\\[^ux]/gi, ''))) {
                needToFoldCase = true;
                ignoreCase = false;
                break;
            }
        }

        var escapeCharToCodeUnit = {
            'b': 8,
            't': 9,
            'n': 0xa,
            'v': 0xb,
            'f': 0xc,
            'r': 0xd
        };

        function decodeEscape(charsetPart) {
            var cc0 = charsetPart.charCodeAt(0);
            if (cc0 !== 92 /* \\ */) {
                return cc0;
            }
            var c1 = charsetPart.charAt(1);
            cc0 = escapeCharToCodeUnit[c1];
            if (cc0) {
                return cc0;
            } else if ('0' <= c1 && c1 <= '7') {
                return parseInt(charsetPart.substring(1), 8);
            } else if (c1 === 'u' || c1 === 'x') {
                return parseInt(charsetPart.substring(2), 16);
            } else {
                return charsetPart.charCodeAt(1);
            }
        }

        function encodeEscape(charCode) {
            if (charCode < 0x20) {
                return (charCode < 0x10 ? '\\x0' : '\\x') + charCode.toString(16);
            }
            var ch = String.fromCharCode(charCode);
            return (ch === '\\' || ch === '-' || ch === ']' || ch === '^')
                ? "\\" + ch : ch;
        }

        function caseFoldCharset(charSet) {
            var charsetParts = charSet.substring(1, charSet.length - 1).match(
                new RegExp(
                    '\\\\u[0-9A-Fa-f]{4}'
                    + '|\\\\x[0-9A-Fa-f]{2}'
                    + '|\\\\[0-3][0-7]{0,2}'
                    + '|\\\\[0-7]{1,2}'
                    + '|\\\\[\\s\\S]'
                    + '|-'
                    + '|[^-\\\\]',
                    'g'));
            var ranges = [];
            var inverse = charsetParts[0] === '^';

            var out = ['['];
            if (inverse) { out.push('^'); }

            for (var i = inverse ? 1 : 0, n = charsetParts.length; i < n; ++i) {
                var p = charsetParts[i];
                if (/\\[bdsw]/i.test(p)) {  // Don't muck with named groups.
                    out.push(p);
                } else {
                    var start = decodeEscape(p);
                    var end;
                    if (i + 2 < n && '-' === charsetParts[i + 1]) {
                        end = decodeEscape(charsetParts[i + 2]);
                        i += 2;
                    } else {
                        end = start;
                    }
                    ranges.push([start, end]);
                    // If the range might intersect letters, then expand it.
                    // This case handling is too simplistic.
                    // It does not deal with non-latin case folding.
                    // It works for latin source code identifiers though.
                    if (!(end < 65 || start > 122)) {
                        if (!(end < 65 || start > 90)) {
                            ranges.push([Math.max(65, start) | 32, Math.min(end, 90) | 32]);
                        }
                        if (!(end < 97 || start > 122)) {
                            ranges.push([Math.max(97, start) & ~32, Math.min(end, 122) & ~32]);
                        }
                    }
                }
            }

            // [[1, 10], [3, 4], [8, 12], [14, 14], [16, 16], [17, 17]]
            // -> [[1, 12], [14, 14], [16, 17]]
            ranges.sort(function (a, b) { return (a[0] - b[0]) || (b[1]  - a[1]); });
            var consolidatedRanges = [];
            var lastRange = [];
            for (var i = 0; i < ranges.length; ++i) {
                var range = ranges[i];
                if (range[0] <= lastRange[1] + 1) {
                    lastRange[1] = Math.max(lastRange[1], range[1]);
                } else {
                    consolidatedRanges.push(lastRange = range);
                }
            }

            for (var i = 0; i < consolidatedRanges.length; ++i) {
                var range = consolidatedRanges[i];
                out.push(encodeEscape(range[0]));
                if (range[1] > range[0]) {
                    if (range[1] + 1 > range[0]) { out.push('-'); }
                    out.push(encodeEscape(range[1]));
                }
            }
            out.push(']');
            return out.join('');
        }

        function allowAnywhereFoldCaseAndRenumberGroups(regex) {
            // Split into character sets, escape sequences, punctuation strings
            // like ('(', '(?:', ')', '^'), and runs of characters that do not
            // include any of the above.
            var parts = regex.source.match(
                new RegExp(
                    '(?:'
                    + '\\[(?:[^\\x5C\\x5D]|\\\\[\\s\\S])*\\]'  // a character set
                    + '|\\\\u[A-Fa-f0-9]{4}'  // a unicode escape
                    + '|\\\\x[A-Fa-f0-9]{2}'  // a hex escape
                    + '|\\\\[0-9]+'  // a back-reference or octal escape
                    + '|\\\\[^ux0-9]'  // other escape sequence
                    + '|\\(\\?[:!=]'  // start of a non-capturing group
                    + '|[\\(\\)\\^]'  // start/end of a group, or line start
                    + '|[^\\x5B\\x5C\\(\\)\\^]+'  // run of other characters
                    + ')',
                    'g'));
            var n = parts.length;

            // Maps captured group numbers to the number they will occupy in
            // the output or to -1 if that has not been determined, or to
            // undefined if they need not be capturing in the output.
            var capturedGroups = [];

            // Walk over and identify back references to build the capturedGroups
            // mapping.
            for (var i = 0, groupIndex = 0; i < n; ++i) {
                var p = parts[i];
                if (p === '(') {
                    // groups are 1-indexed, so max group index is count of '('
                    ++groupIndex;
                } else if ('\\' === p.charAt(0)) {
                    var decimalValue = +p.substring(1);
                    if (decimalValue) {
                        if (decimalValue <= groupIndex) {
                            capturedGroups[decimalValue] = -1;
                        } else {
                            // Replace with an unambiguous escape sequence so that
                            // an octal escape sequence does not turn into a backreference
                            // to a capturing group from an earlier regex.
                            parts[i] = encodeEscape(decimalValue);
                        }
                    }
                }
            }

            // Renumber groups and reduce capturing groups to non-capturing groups
            // where possible.
            for (var i = 1; i < capturedGroups.length; ++i) {
                if (-1 === capturedGroups[i]) {
                    capturedGroups[i] = ++capturedGroupIndex;
                }
            }
            for (var i = 0, groupIndex = 0; i < n; ++i) {
                var p = parts[i];
                if (p === '(') {
                    ++groupIndex;
                    if (!capturedGroups[groupIndex]) {
                        parts[i] = '(?:';
                    }
                } else if ('\\' === p.charAt(0)) {
                    var decimalValue = +p.substring(1);
                    if (decimalValue && decimalValue <= groupIndex) {
                        parts[i] = '\\' + capturedGroups[decimalValue];
                    }
                }
            }

            // Remove any prefix anchors so that the output will match anywhere.
            // ^^ really does mean an anchored match though.
            for (var i = 0; i < n; ++i) {
                if ('^' === parts[i] && '^' !== parts[i + 1]) { parts[i] = ''; }
            }

            // Expand letters to groups to handle mixing of case-sensitive and
            // case-insensitive patterns if necessary.
            if (regex.ignoreCase && needToFoldCase) {
                for (var i = 0; i < n; ++i) {
                    var p = parts[i];
                    var ch0 = p.charAt(0);
                    if (p.length >= 2 && ch0 === '[') {
                        parts[i] = caseFoldCharset(p);
                    } else if (ch0 !== '\\') {
                        // TODO: handle letters in numeric escapes.
                        parts[i] = p.replace(
                            /[a-zA-Z]/g,
                            function (ch) {
                                var cc = ch.charCodeAt(0);
                                return '[' + String.fromCharCode(cc & ~32, cc | 32) + ']';
                            });
                    }
                }
            }

            return parts.join('');
        }

        var rewritten = [];
        for (var i = 0, n = regexs.length; i < n; ++i) {
            var regex = regexs[i];
            if (regex.global || regex.multiline) { throw new Error('' + regex); }
            rewritten.push(
                '(?:' + allowAnywhereFoldCaseAndRenumberGroups(regex) + ')');
        }

        return new RegExp(rewritten.join('|'), ignoreCase ? 'gi' : 'g');
    }


    /**
     * Split markup into a string of source code and an array mapping ranges in
     * that string to the text nodes in which they appear.
     *
     * <p>
     * The HTML DOM structure:</p>
     * <pre>
     * (Element   "p"
     *   (Element "b"
     *     (Text  "print "))       ; #1
     *   (Text    "'Hello '")      ; #2
     *   (Element "br")            ; #3
     *   (Text    "  + 'World';")) ; #4
     * </pre>
     * <p>
     * corresponds to the HTML
     * {@code <p><b>print </b>'Hello '<br>  + 'World';</p>}.</p>
     *
     * <p>
     * It will produce the output:</p>
     * <pre>
     * {
     *   sourceCode: "print 'Hello '\n  + 'World';",
     *   //                     1          2
     *   //           012345678901234 5678901234567
     *   spans: [0, #1, 6, #2, 14, #3, 15, #4]
     * }
     * </pre>
     * <p>
     * where #1 is a reference to the {@code "print "} text node above, and so
     * on for the other text nodes.
     * </p>
     *
     * <p>
     * The {@code} spans array is an array of pairs.  Even elements are the start
     * indices of substrings, and odd elements are the text nodes (or BR elements)
     * that contain the text for those substrings.
     * Substrings continue until the next index or the end of the source.
     * </p>
     *
     * @param {Node} node an HTML DOM subtree containing source-code.
     * @param {boolean|number} isPreformatted truthy if white-space in
     *    text nodes should be considered significant.
     * @return {SourceSpansT} source code and the nodes in which they occur.
     */
    function extractSourceSpans(node, isPreformatted) {
        var nocode = /(?:^|\s)nocode(?:\s|$)/;

        var chunks = [];
        var length = 0;
        var spans = [];
        var k = 0;

        function walk(node) {
            var type = node.nodeType;
            if (type == 1) {  // Element
                if (nocode.test(node.className)) { return; }
                for (var child = node.firstChild; child; child = child.nextSibling) {
                    walk(child);
                }
                var nodeName = node.nodeName.toLowerCase();
                if ('br' === nodeName || 'li' === nodeName) {
                    chunks[k] = '\n';
                    spans[k << 1] = length++;
                    spans[(k++ << 1) | 1] = node;
                }
            } else if (type == 3 || type == 4) {  // Text
                var text = node.nodeValue;
                if (text.length) {
                    if (!isPreformatted) {
                        text = text.replace(/[ \t\r\n]+/g, ' ');
                    } else {
                        text = text.replace(/\r\n?/g, '\n');  // Normalize newlines.
                    }
                    // TODO: handle tabs here?
                    chunks[k] = text;
                    spans[k << 1] = length;
                    length += text.length;
                    spans[(k++ << 1) | 1] = node;
                }
            }
        }

        walk(node);

        return {
            sourceCode: chunks.join('').replace(/\n$/, ''),
            spans: spans
        };
    }


    /**
     * Apply the given language handler to sourceCode and add the resulting
     * decorations to out.
     * @param {!Element} sourceNode
     * @param {number} basePos the index of sourceCode within the chunk of source
     *    whose decorations are already present on out.
     * @param {string} sourceCode
     * @param {function(JobT)} langHandler
     * @param {DecorationsT} out
     */
    function appendDecorations(
        sourceNode, basePos, sourceCode, langHandler, out) {
        if (!sourceCode) { return; }
        /** @type {JobT} */
        var job = {
            sourceNode: sourceNode,
            pre: 1,
            langExtension: null,
            numberLines: null,
            sourceCode: sourceCode,
            spans: null,
            basePos: basePos,
            decorations: null
        };
        langHandler(job);
        out.push.apply(out, job.decorations);
    }

    var notWs = /\S/;

    /**
     * Given an element, if it contains only one child element and any text nodes
     * it contains contain only space characters, return the sole child element.
     * Otherwise returns undefined.
     * <p>
     * This is meant to return the CODE element in {@code <pre><code ...>} when
     * there is a single child element that contains all the non-space textual
     * content, but not to return anything where there are multiple child elements
     * as in {@code <pre><code>...</code><code>...</code></pre>} or when there
     * is textual content.
     */
    function childContentWrapper(element) {
        var wrapper = undefined;
        for (var c = element.firstChild; c; c = c.nextSibling) {
            var type = c.nodeType;
            wrapper = (type === 1)  // Element Node
                ? (wrapper ? element : c)
                : (type === 3)  // Text Node
                    ? (notWs.test(c.nodeValue) ? element : wrapper)
                    : wrapper;
        }
        return wrapper === element ? undefined : wrapper;
    }

    /** Given triples of [style, pattern, context] returns a lexing function,
     * The lexing function interprets the patterns to find token boundaries and
     * returns a decoration list of the form
     * [index_0, style_0, index_1, style_1, ..., index_n, style_n]
     * where index_n is an index into the sourceCode, and style_n is a style
     * constant like PR_PLAIN.  index_n-1 <= index_n, and style_n-1 applies to
     * all characters in sourceCode[index_n-1:index_n].
     *
     * The stylePatterns is a list whose elements have the form
     * [style : string, pattern : RegExp, DEPRECATED, shortcut : string].
     *
     * Style is a style constant like PR_PLAIN, or can be a string of the
     * form 'lang-FOO', where FOO is a language extension describing the
     * language of the portion of the token in $1 after pattern executes.
     * E.g., if style is 'lang-lisp', and group 1 contains the text
     * '(hello (world))', then that portion of the token will be passed to the
     * registered lisp handler for formatting.
     * The text before and after group 1 will be restyled using this decorator
     * so decorators should take care that this doesn't result in infinite
     * recursion.  For example, the HTML lexer rule for SCRIPT elements looks
     * something like ['lang-js', /<[s]cript>(.+?)<\/script>/].  This may match
     * '<script>foo()<\/script>', which would cause the current decorator to
     * be called with '<script>' which would not match the same rule since
     * group 1 must not be empty, so it would be instead styled as PR_TAG by
     * the generic tag rule.  The handler registered for the 'js' extension would
     * then be called with 'foo()', and finally, the current decorator would
     * be called with '<\/script>' which would not match the original rule and
     * so the generic tag rule would identify it as a tag.
     *
     * Pattern must only match prefixes, and if it matches a prefix, then that
     * match is considered a token with the same style.
     *
     * Context is applied to the last non-whitespace, non-comment token
     * recognized.
     *
     * Shortcut is an optional string of characters, any of which, if the first
     * character, gurantee that this pattern and only this pattern matches.
     *
     * @param {Array} shortcutStylePatterns patterns that always start with
     *   a known character.  Must have a shortcut string.
     * @param {Array} fallthroughStylePatterns patterns that will be tried in
     *   order if the shortcut ones fail.  May have shortcuts.
     *
     * @return {function (JobT)} a function that takes an undecorated job and
     *   attaches a list of decorations.
     */
    function createSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns) {
        var shortcuts = {};
        var tokenizer;
        (function () {
            var allPatterns = shortcutStylePatterns.concat(fallthroughStylePatterns);
            var allRegexs = [];
            var regexKeys = {};
            for (var i = 0, n = allPatterns.length; i < n; ++i) {
                var patternParts = allPatterns[i];
                var shortcutChars = patternParts[3];
                if (shortcutChars) {
                    for (var c = shortcutChars.length; --c >= 0;) {
                        shortcuts[shortcutChars.charAt(c)] = patternParts;
                    }
                }
                var regex = patternParts[1];
                var k = '' + regex;
                if (!regexKeys.hasOwnProperty(k)) {
                    allRegexs.push(regex);
                    regexKeys[k] = null;
                }
            }
            allRegexs.push(/[\0-\uffff]/);
            tokenizer = combinePrefixPatterns(allRegexs);
        })();

        var nPatterns = fallthroughStylePatterns.length;

        /**
         * Lexes job.sourceCode and attaches an output array job.decorations of
         * style classes preceded by the position at which they start in
         * job.sourceCode in order.
         *
         * @type{function (JobT)}
         */
        var decorate = function (job) {
            var sourceCode = job.sourceCode, basePos = job.basePos;
            var sourceNode = job.sourceNode;
            /** Even entries are positions in source in ascending order.  Odd enties
             * are style markers (e.g., PR_COMMENT) that run from that position until
             * the end.
             * @type {DecorationsT}
             */
            var decorations = [basePos, PR_PLAIN];
            var pos = 0;  // index into sourceCode
            var tokens = sourceCode.match(tokenizer) || [];
            var styleCache = {};

            for (var ti = 0, nTokens = tokens.length; ti < nTokens; ++ti) {
                var token = tokens[ti];
                var style = styleCache[token];
                var match = void 0;

                var isEmbedded;
                if (typeof style === 'string') {
                    isEmbedded = false;
                } else {
                    var patternParts = shortcuts[token.charAt(0)];
                    if (patternParts) {
                        match = token.match(patternParts[1]);
                        style = patternParts[0];
                    } else {
                        for (var i = 0; i < nPatterns; ++i) {
                            patternParts = fallthroughStylePatterns[i];
                            match = token.match(patternParts[1]);
                            if (match) {
                                style = patternParts[0];
                                break;
                            }
                        }

                        if (!match) {  // make sure that we make progress
                            style = PR_PLAIN;
                        }
                    }

                    isEmbedded = style.length >= 5 && 'lang-' === style.substring(0, 5);
                    if (isEmbedded && !(match && typeof match[1] === 'string')) {
                        isEmbedded = false;
                        style = PR_SOURCE;
                    }

                    if (!isEmbedded) { styleCache[token] = style; }
                }

                var tokenStart = pos;
                pos += token.length;

                if (!isEmbedded) {
                    decorations.push(basePos + tokenStart, style);
                } else {  // Treat group 1 as an embedded block of source code.
                    var embeddedSource = match[1];
                    var embeddedSourceStart = token.indexOf(embeddedSource);
                    var embeddedSourceEnd = embeddedSourceStart + embeddedSource.length;
                    if (match[2]) {
                        // If embeddedSource can be blank, then it would match at the
                        // beginning which would cause us to infinitely recurse on the
                        // entire token, so we catch the right context in match[2].
                        embeddedSourceEnd = token.length - match[2].length;
                        embeddedSourceStart = embeddedSourceEnd - embeddedSource.length;
                    }
                    var lang = style.substring(5);
                    // Decorate the left of the embedded source
                    appendDecorations(
                        sourceNode,
                        basePos + tokenStart,
                        token.substring(0, embeddedSourceStart),
                        decorate, decorations);
                    // Decorate the embedded source
                    appendDecorations(
                        sourceNode,
                        basePos + tokenStart + embeddedSourceStart,
                        embeddedSource,
                        langHandlerForExtension(lang, embeddedSource),
                        decorations);
                    // Decorate the right of the embedded section
                    appendDecorations(
                        sourceNode,
                        basePos + tokenStart + embeddedSourceEnd,
                        token.substring(embeddedSourceEnd),
                        decorate, decorations);
                }
            }
            job.decorations = decorations;
        };
        return decorate;
    }

    /** returns a function that produces a list of decorations from source text.
     *
     * This code treats ", ', and ` as string delimiters, and \ as a string
     * escape.  It does not recognize perl's qq() style strings.
     * It has no special handling for double delimiter escapes as in basic, or
     * the tripled delimiters used in python, but should work on those regardless
     * although in those cases a single string literal may be broken up into
     * multiple adjacent string literals.
     *
     * It recognizes C, C++, and shell style comments.
     *
     * @param {Object} options a set of optional parameters.
     * @return {function (JobT)} a function that examines the source code
     *     in the input job and builds a decoration list which it attaches to
     *     the job.
     */
    function sourceDecorator(options) {
        var shortcutStylePatterns = [], fallthroughStylePatterns = [];
        if (options['tripleQuotedStrings']) {
            // '''multi-line-string''', 'single-line-string', and double-quoted
            shortcutStylePatterns.push(
                [PR_STRING,  /^(?:\'\'\'(?:[^\'\\]|\\[\s\S]|\'{1,2}(?=[^\']))*(?:\'\'\'|$)|\"\"\"(?:[^\"\\]|\\[\s\S]|\"{1,2}(?=[^\"]))*(?:\"\"\"|$)|\'(?:[^\\\']|\\[\s\S])*(?:\'|$)|\"(?:[^\\\"]|\\[\s\S])*(?:\"|$))/,
                    null, '\'"']);
        } else if (options['multiLineStrings']) {
            // 'multi-line-string', "multi-line-string"
            shortcutStylePatterns.push(
                [PR_STRING,  /^(?:\'(?:[^\\\']|\\[\s\S])*(?:\'|$)|\"(?:[^\\\"]|\\[\s\S])*(?:\"|$)|\`(?:[^\\\`]|\\[\s\S])*(?:\`|$))/,
                    null, '\'"`']);
        } else {
            // 'single-line-string', "single-line-string"
            shortcutStylePatterns.push(
                [PR_STRING,
                    /^(?:\'(?:[^\\\'\r\n]|\\.)*(?:\'|$)|\"(?:[^\\\"\r\n]|\\.)*(?:\"|$))/,
                    null, '"\'']);
        }
        if (options['verbatimStrings']) {
            // verbatim-string-literal production from the C# grammar.  See issue 93.
            fallthroughStylePatterns.push(
                [PR_STRING, /^@\"(?:[^\"]|\"\")*(?:\"|$)/, null]);
        }
        var hc = options['hashComments'];
        if (hc) {
            if (options['cStyleComments']) {
                if (hc > 1) {  // multiline hash comments
                    shortcutStylePatterns.push(
                        [PR_COMMENT, /^#(?:##(?:[^#]|#(?!##))*(?:###|$)|.*)/, null, '#']);
                } else {
                    // Stop C preprocessor declarations at an unclosed open comment
                    shortcutStylePatterns.push(
                        [PR_COMMENT, /^#(?:(?:define|e(?:l|nd)if|else|error|ifn?def|include|line|pragma|undef|warning)\b|[^\r\n]*)/,
                            null, '#']);
                }
                // #include <stdio.h>
                fallthroughStylePatterns.push(
                    [PR_STRING,
                        /^<(?:(?:(?:\.\.\/)*|\/?)(?:[\w-]+(?:\/[\w-]+)+)?[\w-]+\.h(?:h|pp|\+\+)?|[a-z]\w*)>/,
                        null]);
            } else {
                shortcutStylePatterns.push([PR_COMMENT, /^#[^\r\n]*/, null, '#']);
            }
        }
        if (options['cStyleComments']) {
            fallthroughStylePatterns.push([PR_COMMENT, /^\/\/[^\r\n]*/, null]);
            fallthroughStylePatterns.push(
                [PR_COMMENT, /^\/\*[\s\S]*?(?:\*\/|$)/, null]);
        }
        var regexLiterals = options['regexLiterals'];
        if (regexLiterals) {
            /**
             * @const
             */
            var regexExcls = regexLiterals > 1
                ? ''  // Multiline regex literals
                : '\n\r';
            /**
             * @const
             */
            var regexAny = regexExcls ? '.' : '[\\S\\s]';
            /**
             * @const
             */
            var REGEX_LITERAL = (
                // A regular expression literal starts with a slash that is
                // not followed by * or / so that it is not confused with
                // comments.
                '/(?=[^/*' + regexExcls + '])'
                // and then contains any number of raw characters,
                + '(?:[^/\\x5B\\x5C' + regexExcls + ']'
                // escape sequences (\x5C),
                +    '|\\x5C' + regexAny
                // or non-nesting character sets (\x5B\x5D);
                +    '|\\x5B(?:[^\\x5C\\x5D' + regexExcls + ']'
                +             '|\\x5C' + regexAny + ')*(?:\\x5D|$))+'
                // finally closed by a /.
                + '/');
            fallthroughStylePatterns.push(
                ['lang-regex',
                    RegExp('^' + REGEXP_PRECEDER_PATTERN + '(' + REGEX_LITERAL + ')')
                ]);
        }

        var types = options['types'];
        if (types) {
            fallthroughStylePatterns.push([PR_TYPE, types]);
        }

        var keywords = ("" + options['keywords']).replace(/^ | $/g, '');
        if (keywords.length) {
            fallthroughStylePatterns.push(
                [PR_KEYWORD,
                    new RegExp('^(?:' + keywords.replace(/[\s,]+/g, '|') + ')\\b'),
                    null]);
        }

        shortcutStylePatterns.push([PR_PLAIN,       /^\s+/, null, ' \r\n\t\xA0']);

        var punctuation =
                // The Bash man page says

                // A word is a sequence of characters considered as a single
                // unit by GRUB. Words are separated by metacharacters,
                // which are the following plus space, tab, and newline: { }
                // | & $ ; < >
                // ...

                // A word beginning with # causes that word and all remaining
                // characters on that line to be ignored.

                // which means that only a '#' after /(?:^|[{}|&$;<>\s])/ starts a
                // comment but empirically
                // $ echo {#}
                // {#}
                // $ echo \$#
                // $#
                // $ echo }#
                // }#

                // so /(?:^|[|&;<>\s])/ is more appropriate.

                // http://gcc.gnu.org/onlinedocs/gcc-2.95.3/cpp_1.html#SEC3
                // suggests that this definition is compatible with a
                // default mode that tries to use a single token definition
                // to recognize both bash/python style comments and C
                // preprocessor directives.

                // This definition of punctuation does not include # in the list of
                // follow-on exclusions, so # will not be broken before if preceeded
                // by a punctuation character.  We could try to exclude # after
                // [|&;<>] but that doesn't seem to cause many major problems.
                // If that does turn out to be a problem, we should change the below
                // when hc is truthy to include # in the run of punctuation characters
                // only when not followint [|&;<>].
                '^.[^\\s\\w.$@\'"`/\\\\]*';
        if (options['regexLiterals']) {
            punctuation += '(?!\s*\/)';
        }

        fallthroughStylePatterns.push(
            // TODO(mikesamuel): recognize non-latin letters and numerals in idents
            [PR_LITERAL,     /^@[a-z_$][a-z_$@0-9]*/i, null],
            [PR_TYPE,        /^(?:[@_]?[A-Z]+[a-z][A-Za-z_$@0-9]*|\w+_t\b)/, null],
            [PR_PLAIN,       /^[a-z_$][a-z_$@0-9]*/i, null],
            [PR_LITERAL,
                new RegExp(
                    '^(?:'
                    // A hex number
                    + '0x[a-f0-9]+'
                    // or an octal or decimal number,
                    + '|(?:\\d(?:_\\d+)*\\d*(?:\\.\\d*)?|\\.\\d\\+)'
                    // possibly in scientific notation
                    + '(?:e[+\\-]?\\d+)?'
                    + ')'
                    // with an optional modifier like UL for unsigned long
                    + '[a-z]*', 'i'),
                null, '0123456789'],
            // Don't treat escaped quotes in bash as starting strings.
            // See issue 144.
            [PR_PLAIN,       /^\\[\s\S]?/, null],
            [PR_PUNCTUATION, new RegExp(punctuation), null]);

        return createSimpleLexer(shortcutStylePatterns, fallthroughStylePatterns);
    }

    var decorateSource = sourceDecorator({
        'keywords': ALL_KEYWORDS,
        'hashComments': true,
        'cStyleComments': true,
        'multiLineStrings': true,
        'regexLiterals': true
    });

    /**
     * Given a DOM subtree, wraps it in a list, and puts each line into its own
     * list item.
     *
     * @param {Node} node modified in place.  Its content is pulled into an
     *     HTMLOListElement, and each line is moved into a separate list item.
     *     This requires cloning elements, so the input might not have unique
     *     IDs after numbering.
     * @param {number|null|boolean} startLineNum
     *     If truthy, coerced to an integer which is the 1-indexed line number
     *     of the first line of code.  The number of the first line will be
     *     attached to the list.
     * @param {boolean} isPreformatted true iff white-space in text nodes should
     *     be treated as significant.
     */
    function numberLines(node, startLineNum, isPreformatted) {
        var nocode = /(?:^|\s)nocode(?:\s|$)/;
        var lineBreak = /\r\n?|\n/;

        var document = node.ownerDocument;

        var li = document.createElement('li');
        while (node.firstChild) {
            li.appendChild(node.firstChild);
        }
        // An array of lines.  We split below, so this is initialized to one
        // un-split line.
        var listItems = [li];

        function walk(node) {
            var type = node.nodeType;
            if (type == 1 && !nocode.test(node.className)) {  // Element
                if ('br' === node.nodeName.toLowerCase()) {
                    breakAfter(node);
                    // Discard the <BR> since it is now flush against a </LI>.
                    if (node.parentNode) {
                        node.parentNode.removeChild(node);
                    }
                } else {
                    for (var child = node.firstChild; child; child = child.nextSibling) {
                        walk(child);
                    }
                }
            } else if ((type == 3 || type == 4) && isPreformatted) {  // Text
                var text = node.nodeValue;
                var match = text.match(lineBreak);
                if (match) {
                    var firstLine = text.substring(0, match.index);
                    node.nodeValue = firstLine;
                    var tail = text.substring(match.index + match[0].length);
                    if (tail) {
                        var parent = node.parentNode;
                        parent.insertBefore(
                            document.createTextNode(tail), node.nextSibling);
                    }
                    breakAfter(node);
                    if (!firstLine) {
                        // Don't leave blank text nodes in the DOM.
                        node.parentNode.removeChild(node);
                    }
                }
            }
        }

        // Split a line after the given node.
        function breakAfter(lineEndNode) {
            // If there's nothing to the right, then we can skip ending the line
            // here, and move root-wards since splitting just before an end-tag
            // would require us to create a bunch of empty copies.
            while (!lineEndNode.nextSibling) {
                lineEndNode = lineEndNode.parentNode;
                if (!lineEndNode) { return; }
            }

            function breakLeftOf(limit, copy) {
                // Clone shallowly if this node needs to be on both sides of the break.
                var rightSide = copy ? limit.cloneNode(false) : limit;
                var parent = limit.parentNode;
                if (parent) {
                    // We clone the parent chain.
                    // This helps us resurrect important styling elements that cross lines.
                    // E.g. in <i>Foo<br>Bar</i>
                    // should be rewritten to <li><i>Foo</i></li><li><i>Bar</i></li>.
                    var parentClone = breakLeftOf(parent, 1);
                    // Move the clone and everything to the right of the original
                    // onto the cloned parent.
                    var next = limit.nextSibling;
                    parentClone.appendChild(rightSide);
                    for (var sibling = next; sibling; sibling = next) {
                        next = sibling.nextSibling;
                        parentClone.appendChild(sibling);
                    }
                }
                return rightSide;
            }

            var copiedListItem = breakLeftOf(lineEndNode.nextSibling, 0);

            // Walk the parent chain until we reach an unattached LI.
            for (var parent;
                // Check nodeType since IE invents document fragments.
                 (parent = copiedListItem.parentNode) && parent.nodeType === 1;) {
                copiedListItem = parent;
            }
            // Put it on the list of lines for later processing.
            listItems.push(copiedListItem);
        }

        // Split lines while there are lines left to split.
        for (var i = 0;  // Number of lines that have been split so far.
             i < listItems.length;  // length updated by breakAfter calls.
             ++i) {
            walk(listItems[i]);
        }

        // Make sure numeric indices show correctly.
        if (startLineNum === (startLineNum|0)) {
            listItems[0].setAttribute('value', startLineNum);
        }

        var ol = document.createElement('ol');
        ol.className = 'linenums';
        var offset = Math.max(0, ((startLineNum - 1 /* zero index */)) | 0) || 0;
        for (var i = 0, n = listItems.length; i < n; ++i) {
            li = listItems[i];
            // Stick a class on the LIs so that stylesheets can
            // color odd/even rows, or any other row pattern that
            // is co-prime with 10.
            li.className = 'L' + ((i + offset) % 10);
            if (!li.firstChild) {
                li.appendChild(document.createTextNode('\xA0'));
            }
            ol.appendChild(li);
        }

        node.appendChild(ol);
    }


    /**
     * Breaks {@code job.sourceCode} around style boundaries in
     * {@code job.decorations} and modifies {@code job.sourceNode} in place.
     * @param {JobT} job
     * @private
     */
    function recombineTagsAndDecorations(job) {
        var isIE8OrEarlier = /\bMSIE\s(\d+)/.exec(navigator.userAgent);
        isIE8OrEarlier = isIE8OrEarlier && +isIE8OrEarlier[1] <= 8;
        var newlineRe = /\n/g;

        var source = job.sourceCode;
        var sourceLength = source.length;
        // Index into source after the last code-unit recombined.
        var sourceIndex = 0;

        var spans = job.spans;
        var nSpans = spans.length;
        // Index into spans after the last span which ends at or before sourceIndex.
        var spanIndex = 0;

        var decorations = job.decorations;
        var nDecorations = decorations.length;
        // Index into decorations after the last decoration which ends at or before
        // sourceIndex.
        var decorationIndex = 0;

        // Remove all zero-length decorations.
        decorations[nDecorations] = sourceLength;
        var decPos, i;
        for (i = decPos = 0; i < nDecorations;) {
            if (decorations[i] !== decorations[i + 2]) {
                decorations[decPos++] = decorations[i++];
                decorations[decPos++] = decorations[i++];
            } else {
                i += 2;
            }
        }
        nDecorations = decPos;

        // Simplify decorations.
        for (i = decPos = 0; i < nDecorations;) {
            var startPos = decorations[i];
            // Conflate all adjacent decorations that use the same style.
            var startDec = decorations[i + 1];
            var end = i + 2;
            while (end + 2 <= nDecorations && decorations[end + 1] === startDec) {
                end += 2;
            }
            decorations[decPos++] = startPos;
            decorations[decPos++] = startDec;
            i = end;
        }

        nDecorations = decorations.length = decPos;

        var sourceNode = job.sourceNode;
        var oldDisplay = "";
        if (sourceNode) {
            oldDisplay = sourceNode.style.display;
            sourceNode.style.display = 'none';
        }
        try {
            var decoration = null;
            while (spanIndex < nSpans) {
                var spanStart = spans[spanIndex];
                var spanEnd = /** @type{number} */ (spans[spanIndex + 2])
                    || sourceLength;

                var decEnd = decorations[decorationIndex + 2] || sourceLength;

                var end = Math.min(spanEnd, decEnd);

                var textNode = /** @type{Node} */ (spans[spanIndex + 1]);
                var styledText;
                if (textNode.nodeType !== 1  // Don't muck with <BR>s or <LI>s
                    // Don't introduce spans around empty text nodes.
                    && (styledText = source.substring(sourceIndex, end))) {
                    // This may seem bizarre, and it is.  Emitting LF on IE causes the
                    // code to display with spaces instead of line breaks.
                    // Emitting Windows standard issue linebreaks (CRLF) causes a blank
                    // space to appear at the beginning of every line but the first.
                    // Emitting an old Mac OS 9 line separator makes everything spiffy.
                    if (isIE8OrEarlier) {
                        styledText = styledText.replace(newlineRe, '\r');
                    }
                    textNode.nodeValue = styledText;
                    var document = textNode.ownerDocument;
                    var span = document.createElement('span');
                    span.className = decorations[decorationIndex + 1];
                    var parentNode = textNode.parentNode;
                    parentNode.replaceChild(span, textNode);
                    span.appendChild(textNode);
                    if (sourceIndex < spanEnd) {  // Split off a text node.
                        spans[spanIndex + 1] = textNode
                            // TODO: Possibly optimize by using '' if there's no flicker.
                            = document.createTextNode(source.substring(end, spanEnd));
                        parentNode.insertBefore(textNode, span.nextSibling);
                    }
                }

                sourceIndex = end;

                if (sourceIndex >= spanEnd) {
                    spanIndex += 2;
                }
                if (sourceIndex >= decEnd) {
                    decorationIndex += 2;
                }
            }
        } finally {
            if (sourceNode) {
                sourceNode.style.display = oldDisplay;
            }
        }
    }


    /** Maps language-specific file extensions to handlers. */
    var langHandlerRegistry = {};
    /** Register a language handler for the given file extensions.
     * @param {function (JobT)} handler a function from source code to a list
     *      of decorations.  Takes a single argument job which describes the
     *      state of the computation and attaches the decorations to it.
     * @param {Array.<string>} fileExtensions
     */
    function registerLangHandler(handler, fileExtensions) {
        for (var i = fileExtensions.length; --i >= 0;) {
            var ext = fileExtensions[i];
            if (!langHandlerRegistry.hasOwnProperty(ext)) {
                langHandlerRegistry[ext] = handler;
            } else if (win['console']) {
                console['warn']('cannot override language handler %s', ext);
            }
        }
    }
    function langHandlerForExtension(extension, source) {
        if (!(extension && langHandlerRegistry.hasOwnProperty(extension))) {
            // Treat it as markup if the first non whitespace character is a < and
            // the last non-whitespace character is a >.
            extension = /^\s*</.test(source)
                ? 'default-markup'
                : 'default-code';
        }
        return langHandlerRegistry[extension];
    }
    registerLangHandler(decorateSource, ['default-code']);
    registerLangHandler(
        createSimpleLexer(
            [],
            [
                [PR_PLAIN,       /^[^<?]+/],
                [PR_DECLARATION, /^<!\w[^>]*(?:>|$)/],
                [PR_COMMENT,     /^<\!--[\s\S]*?(?:-\->|$)/],
                // Unescaped content in an unknown language
                ['lang-',        /^<\?([\s\S]+?)(?:\?>|$)/],
                ['lang-',        /^<%([\s\S]+?)(?:%>|$)/],
                [PR_PUNCTUATION, /^(?:<[%?]|[%?]>)/],
                ['lang-',        /^<xmp\b[^>]*>([\s\S]+?)<\/xmp\b[^>]*>/i],
                // Unescaped content in javascript.  (Or possibly vbscript).
                ['lang-js',      /^<script\b[^>]*>([\s\S]*?)(<\/script\b[^>]*>)/i],
                // Contains unescaped stylesheet content
                ['lang-css',     /^<style\b[^>]*>([\s\S]*?)(<\/style\b[^>]*>)/i],
                ['lang-in.tag',  /^(<\/?[a-z][^<>]*>)/i]
            ]),
        ['default-markup', 'htm', 'html', 'mxml', 'xhtml', 'xml', 'xsl']);
    registerLangHandler(
        createSimpleLexer(
            [
                [PR_PLAIN,        /^[\s]+/, null, ' \t\r\n'],
                [PR_ATTRIB_VALUE, /^(?:\"[^\"]*\"?|\'[^\']*\'?)/, null, '\"\'']
            ],
            [
                [PR_TAG,          /^^<\/?[a-z](?:[\w.:-]*\w)?|\/?>$/i],
                [PR_ATTRIB_NAME,  /^(?!style[\s=]|on)[a-z](?:[\w:-]*\w)?/i],
                ['lang-uq.val',   /^=\s*([^>\'\"\s]*(?:[^>\'\"\s\/]|\/(?=\s)))/],
                [PR_PUNCTUATION,  /^[=<>\/]+/],
                ['lang-js',       /^on\w+\s*=\s*\"([^\"]+)\"/i],
                ['lang-js',       /^on\w+\s*=\s*\'([^\']+)\'/i],
                ['lang-js',       /^on\w+\s*=\s*([^\"\'>\s]+)/i],
                ['lang-css',      /^style\s*=\s*\"([^\"]+)\"/i],
                ['lang-css',      /^style\s*=\s*\'([^\']+)\'/i],
                ['lang-css',      /^style\s*=\s*([^\"\'>\s]+)/i]
            ]),
        ['in.tag']);
    registerLangHandler(
        createSimpleLexer([], [[PR_ATTRIB_VALUE, /^[\s\S]+/]]), ['uq.val']);
    registerLangHandler(sourceDecorator({
        'keywords': CPP_KEYWORDS,
        'hashComments': true,
        'cStyleComments': true,
        'types': C_TYPES
    }), ['c', 'cc', 'cpp', 'cxx', 'cyc', 'm']);
    registerLangHandler(sourceDecorator({
        'keywords': 'null,true,false'
    }), ['json']);
    registerLangHandler(sourceDecorator({
        'keywords': CSHARP_KEYWORDS,
        'hashComments': true,
        'cStyleComments': true,
        'verbatimStrings': true,
        'types': C_TYPES
    }), ['cs']);
    registerLangHandler(sourceDecorator({
        'keywords': JAVA_KEYWORDS,
        'cStyleComments': true
    }), ['java']);
    registerLangHandler(sourceDecorator({
        'keywords': SH_KEYWORDS,
        'hashComments': true,
        'multiLineStrings': true
    }), ['bash', 'bsh', 'csh', 'sh']);
    registerLangHandler(sourceDecorator({
        'keywords': PYTHON_KEYWORDS,
        'hashComments': true,
        'multiLineStrings': true,
        'tripleQuotedStrings': true
    }), ['cv', 'py', 'python']);
    registerLangHandler(sourceDecorator({
        'keywords': PERL_KEYWORDS,
        'hashComments': true,
        'multiLineStrings': true,
        'regexLiterals': 2  // multiline regex literals
    }), ['perl', 'pl', 'pm']);
    registerLangHandler(sourceDecorator({
        'keywords': RUBY_KEYWORDS,
        'hashComments': true,
        'multiLineStrings': true,
        'regexLiterals': true
    }), ['rb', 'ruby']);
    registerLangHandler(sourceDecorator({
        'keywords': JSCRIPT_KEYWORDS,
        'cStyleComments': true,
        'regexLiterals': true
    }), ['javascript', 'js', 'ts', 'typescript']);
    registerLangHandler(sourceDecorator({
        'keywords': COFFEE_KEYWORDS,
        'hashComments': 3,  // ### style block comments
        'cStyleComments': true,
        'multilineStrings': true,
        'tripleQuotedStrings': true,
        'regexLiterals': true
    }), ['coffee']);
    registerLangHandler(
        createSimpleLexer([], [[PR_STRING, /^[\s\S]+/]]), ['regex']);

    /** @param {JobT} job */
    function applyDecorator(job) {
        var opt_langExtension = job.langExtension;

        try {
            // Extract tags, and convert the source code to plain text.
            var sourceAndSpans = extractSourceSpans(job.sourceNode, job.pre);
            /** Plain text. @type {string} */
            var source = sourceAndSpans.sourceCode;
            job.sourceCode = source;
            job.spans = sourceAndSpans.spans;
            job.basePos = 0;

            // Apply the appropriate language handler
            langHandlerForExtension(opt_langExtension, source)(job);

            // Integrate the decorations and tags back into the source code,
            // modifying the sourceNode in place.
            recombineTagsAndDecorations(job);
        } catch (e) {
            if (win['console']) {
                console['log'](e && e['stack'] || e);
            }
        }
    }

    /**
     * Pretty print a chunk of code.
     * @param sourceCodeHtml {string} The HTML to pretty print.
     * @param opt_langExtension {string} The language name to use.
     *     Typically, a filename extension like 'cpp' or 'java'.
     * @param opt_numberLines {number|boolean} True to number lines,
     *     or the 1-indexed number of the first line in sourceCodeHtml.
     */
    function $prettyPrintOne(sourceCodeHtml, opt_langExtension, opt_numberLines) {
        /** @type{number|boolean} */
        var nl = opt_numberLines || false;
        /** @type{string|null} */
        var langExtension = opt_langExtension || null;
        /** @type{!Element} */
        var container = document.createElement('div');
        // This could cause images to load and onload listeners to fire.
        // E.g. <img onerror="alert(1337)" src="nosuchimage.png">.
        // We assume that the inner HTML is from a trusted source.
        // The pre-tag is required for IE8 which strips newlines from innerHTML
        // when it is injected into a <pre> tag.
        // http://stackoverflow.com/questions/451486/pre-tag-loses-line-breaks-when-setting-innerhtml-in-ie
        // http://stackoverflow.com/questions/195363/inserting-a-newline-into-a-pre-tag-ie-javascript
        container.innerHTML = '<pre>' + sourceCodeHtml + '</pre>';
        container = /** @type{!Element} */(container.firstChild);
        if (nl) {
            numberLines(container, nl, true);
        }

        /** @type{JobT} */
        var job = {
            langExtension: langExtension,
            numberLines: nl,
            sourceNode: container,
            pre: 1,
            sourceCode: null,
            basePos: null,
            spans: null,
            decorations: null
        };
        applyDecorator(job);
        return container.innerHTML;
    }

    /**
     * Find all the {@code <pre>} and {@code <code>} tags in the DOM with
     * {@code class=prettyprint} and prettify them.
     *
     * @param {Function} opt_whenDone called when prettifying is done.
     * @param {HTMLElement|HTMLDocument} opt_root an element or document
     *   containing all the elements to pretty print.
     *   Defaults to {@code document.body}.
     */
    function $prettyPrint(opt_whenDone, opt_root) {
        var root = opt_root || document.body;
        var doc = root.ownerDocument || document;
        function byTagName(tn) { return root.getElementsByTagName(tn); }
        // fetch a list of nodes to rewrite
        var codeSegments = [byTagName('pre'), byTagName('code'), byTagName('xmp')];
        var elements = [];
        for (var i = 0; i < codeSegments.length; ++i) {
            for (var j = 0, n = codeSegments[i].length; j < n; ++j) {
                elements.push(codeSegments[i][j]);
            }
        }
        codeSegments = null;

        var clock = Date;
        if (!clock['now']) {
            clock = { 'now': function () { return +(new Date); } };
        }

        // The loop is broken into a series of continuations to make sure that we
        // don't make the browser unresponsive when rewriting a large page.
        var k = 0;

        var langExtensionRe = /\blang(?:uage)?-([\w.]+)(?!\S)/;
        var prettyPrintRe = /\bprettyprint\b/;
        var prettyPrintedRe = /\bprettyprinted\b/;
        var preformattedTagNameRe = /pre|xmp/i;
        var codeRe = /^code$/i;
        var preCodeXmpRe = /^(?:pre|code|xmp)$/i;
        var EMPTY = {};

        function doWork() {
            var endTime = (win['PR_SHOULD_USE_CONTINUATION'] ?
                clock['now']() + 250 /* ms */ :
                Infinity);
            for (; k < elements.length && clock['now']() < endTime; k++) {
                var cs = elements[k];

                // Look for a preceding comment like
                // <?prettify lang="..." linenums="..."?>
                var attrs = EMPTY;
                {
                    for (var preceder = cs; (preceder = preceder.previousSibling);) {
                        var nt = preceder.nodeType;
                        // <?foo?> is parsed by HTML 5 to a comment node (8)
                        // like <!--?foo?-->, but in XML is a processing instruction
                        var value = (nt === 7 || nt === 8) && preceder.nodeValue;
                        if (value
                            ? !/^\??prettify\b/.test(value)
                            : (nt !== 3 || /\S/.test(preceder.nodeValue))) {
                            // Skip over white-space text nodes but not others.
                            break;
                        }
                        if (value) {
                            attrs = {};
                            value.replace(
                                /\b(\w+)=([\w:.%+-]+)/g,
                                function (_, name, value) { attrs[name] = value; });
                            break;
                        }
                    }
                }

                var className = cs.className;
                if ((attrs !== EMPTY || prettyPrintRe.test(className))
                    // Don't redo this if we've already done it.
                    // This allows recalling pretty print to just prettyprint elements
                    // that have been added to the page since last call.
                    && !prettyPrintedRe.test(className)) {

                    // make sure this is not nested in an already prettified element
                    var nested = false;
                    for (var p = cs.parentNode; p; p = p.parentNode) {
                        var tn = p.tagName;
                        if (preCodeXmpRe.test(tn)
                            && p.className && prettyPrintRe.test(p.className)) {
                            nested = true;
                            break;
                        }
                    }
                    if (!nested) {
                        // Mark done.  If we fail to prettyprint for whatever reason,
                        // we shouldn't try again.
                        cs.className += ' prettyprinted';

                        // If the classes includes a language extensions, use it.
                        // Language extensions can be specified like
                        //     <pre class="prettyprint lang-cpp">
                        // the language extension "cpp" is used to find a language handler
                        // as passed to PR.registerLangHandler.
                        // HTML5 recommends that a language be specified using "language-"
                        // as the prefix instead.  Google Code Prettify supports both.
                        // http://dev.w3.org/html5/spec-author-view/the-code-element.html
                        var langExtension = attrs['lang'];
                        if (!langExtension) {
                            langExtension = className.match(langExtensionRe);
                            // Support <pre class="prettyprint"><code class="language-c">
                            var wrapper;
                            if (!langExtension && (wrapper = childContentWrapper(cs))
                                && codeRe.test(wrapper.tagName)) {
                                langExtension = wrapper.className.match(langExtensionRe);
                            }

                            if (langExtension) { langExtension = langExtension[1]; }
                        }

                        var preformatted;
                        if (preformattedTagNameRe.test(cs.tagName)) {
                            preformatted = 1;
                        } else {
                            var currentStyle = cs['currentStyle'];
                            var defaultView = doc.defaultView;
                            var whitespace = (
                                currentStyle
                                    ? currentStyle['whiteSpace']
                                    : (defaultView
                                    && defaultView.getComputedStyle)
                                    ? defaultView.getComputedStyle(cs, null)
                                        .getPropertyValue('white-space')
                                    : 0);
                            preformatted = whitespace
                                && 'pre' === whitespace.substring(0, 3);
                        }

                        // Look for a class like linenums or linenums:<n> where <n> is the
                        // 1-indexed number of the first line.
                        var lineNums = attrs['linenums'];
                        if (!(lineNums = lineNums === 'true' || +lineNums)) {
                            lineNums = className.match(/\blinenums\b(?::(\d+))?/);
                            lineNums =
                                lineNums
                                    ? lineNums[1] && lineNums[1].length
                                    ? +lineNums[1] : true
                                    : false;
                        }
                        if (lineNums) { numberLines(cs, lineNums, preformatted); }

                        // do the pretty printing
                        var prettyPrintingJob = {
                            langExtension: langExtension,
                            sourceNode: cs,
                            numberLines: lineNums,
                            pre: preformatted,
                            sourceCode: null,
                            basePos: null,
                            spans: null,
                            decorations: null
                        };
                        applyDecorator(prettyPrintingJob);
                    }
                }
            }
            if (k < elements.length) {
                // finish up in a continuation
                win.setTimeout(doWork, 250);
            } else if ('function' === typeof opt_whenDone) {
                opt_whenDone();
            }
        }

        doWork();
    }

    /**
     * Contains functions for creating and registering new language handlers.
     * @type {Object}
     */
    var PR = win['PR'] = {
        'createSimpleLexer': createSimpleLexer,
        'registerLangHandler': registerLangHandler,
        'sourceDecorator': sourceDecorator,
        'PR_ATTRIB_NAME': PR_ATTRIB_NAME,
        'PR_ATTRIB_VALUE': PR_ATTRIB_VALUE,
        'PR_COMMENT': PR_COMMENT,
        'PR_DECLARATION': PR_DECLARATION,
        'PR_KEYWORD': PR_KEYWORD,
        'PR_LITERAL': PR_LITERAL,
        'PR_NOCODE': PR_NOCODE,
        'PR_PLAIN': PR_PLAIN,
        'PR_PUNCTUATION': PR_PUNCTUATION,
        'PR_SOURCE': PR_SOURCE,
        'PR_STRING': PR_STRING,
        'PR_TAG': PR_TAG,
        'PR_TYPE': PR_TYPE,
        'prettyPrintOne':
            IN_GLOBAL_SCOPE
                ? (win['prettyPrintOne'] = $prettyPrintOne)
                : (prettyPrintOne = $prettyPrintOne),
        'prettyPrint':
            IN_GLOBAL_SCOPE
                ? (win['prettyPrint'] = $prettyPrint)
                : (prettyPrint = $prettyPrint)
    };

    // Make PR available via the Asynchronous Module Definition (AMD) API.
    // Per https://github.com/amdjs/amdjs-api/wiki/AMD:
    // The Asynchronous Module Definition (AMD) API specifies a
    // mechanism for defining modules such that the module and its
    // dependencies can be asynchronously loaded.
    // ...
    // To allow a clear indicator that a global define function (as
    // needed for script src browser loading) conforms to the AMD API,
    // any global define function SHOULD have a property called "amd"
    // whose value is an object. This helps avoid conflict with any
    // other existing JavaScript code that could have defined a define()
    // function that does not conform to the AMD API.
    var define = win['define'];
    if (typeof define === "function" && define['amd']) {
        define("google-code-prettify", [], function () {
            return PR;
        });
    }
})();
/**
 * @licence MIT, originally licensed by https://github.com/taylorhakes/promise-polyfill
 */
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory() :
	typeof define === 'function' && define.amd ? define(factory) :
	(factory());
}(this, (function () { 'use strict';

/**
 * @this {Promise}
 */
function finallyConstructor(callback) {
  var constructor = this.constructor;
  return this.then(
    function(value) {
      return constructor.resolve(callback()).then(function() {
        return value;
      });
    },
    function(reason) {
      return constructor.resolve(callback()).then(function() {
        return constructor.reject(reason);
      });
    }
  );
}

// Store setTimeout reference so promise-polyfill will be unaffected by
// other code modifying setTimeout (like sinon.useFakeTimers())
var setTimeoutFunc = setTimeout;

function noop() {}

// Polyfill for Function.prototype.bind
function bind(fn, thisArg) {
  return function() {
    fn.apply(thisArg, arguments);
  };
}

/**
 * @constructor
 * @param {Function} fn
 */
function Promise(fn) {
  if (!(this instanceof Promise))
    throw new TypeError('Promises must be constructed via new');
  if (typeof fn !== 'function') throw new TypeError('not a function');
  /** @type {!number} */
  this._state = 0;
  /** @type {!boolean} */
  this._handled = false;
  /** @type {Promise|undefined} */
  this._value = undefined;
  /** @type {!Array<!Function>} */
  this._deferreds = [];

  doResolve(fn, this);
}

function handle(self, deferred) {
  while (self._state === 3) {
    self = self._value;
  }
  if (self._state === 0) {
    self._deferreds.push(deferred);
    return;
  }
  self._handled = true;
  Promise._immediateFn(function() {
    var cb = self._state === 1 ? deferred.onFulfilled : deferred.onRejected;
    if (cb === null) {
      (self._state === 1 ? resolve : reject)(deferred.promise, self._value);
      return;
    }
    var ret;
    try {
      ret = cb(self._value);
    } catch (e) {
      reject(deferred.promise, e);
      return;
    }
    resolve(deferred.promise, ret);
  });
}

function resolve(self, newValue) {
  try {
    // Promise Resolution Procedure: https://github.com/promises-aplus/promises-spec#the-promise-resolution-procedure
    if (newValue === self)
      throw new TypeError('A promise cannot be resolved with itself.');
    if (
      newValue &&
      (typeof newValue === 'object' || typeof newValue === 'function')
    ) {
      var then = newValue.then;
      if (newValue instanceof Promise) {
        self._state = 3;
        self._value = newValue;
        finale(self);
        return;
      } else if (typeof then === 'function') {
        doResolve(bind(then, newValue), self);
        return;
      }
    }
    self._state = 1;
    self._value = newValue;
    finale(self);
  } catch (e) {
    reject(self, e);
  }
}

function reject(self, newValue) {
  self._state = 2;
  self._value = newValue;
  finale(self);
}

function finale(self) {
  if (self._state === 2 && self._deferreds.length === 0) {
    Promise._immediateFn(function() {
      if (!self._handled) {
        Promise._unhandledRejectionFn(self._value);
      }
    });
  }

  for (var i = 0, len = self._deferreds.length; i < len; i++) {
    handle(self, self._deferreds[i]);
  }
  self._deferreds = null;
}

/**
 * @constructor
 */
function Handler(onFulfilled, onRejected, promise) {
  this.onFulfilled = typeof onFulfilled === 'function' ? onFulfilled : null;
  this.onRejected = typeof onRejected === 'function' ? onRejected : null;
  this.promise = promise;
}

/**
 * Take a potentially misbehaving resolver function and make sure
 * onFulfilled and onRejected are only called once.
 *
 * Makes no guarantees about asynchrony.
 */
function doResolve(fn, self) {
  var done = false;
  try {
    fn(
      function(value) {
        if (done) return;
        done = true;
        resolve(self, value);
      },
      function(reason) {
        if (done) return;
        done = true;
        reject(self, reason);
      }
    );
  } catch (ex) {
    if (done) return;
    done = true;
    reject(self, ex);
  }
}

Promise.prototype['catch'] = function(onRejected) {
  return this.then(null, onRejected);
};

Promise.prototype.then = function(onFulfilled, onRejected) {
  // @ts-ignore
  var prom = new this.constructor(noop);

  handle(this, new Handler(onFulfilled, onRejected, prom));
  return prom;
};

Promise.prototype['finally'] = finallyConstructor;

Promise.all = function(arr) {
  return new Promise(function(resolve, reject) {
    if (!arr || typeof arr.length === 'undefined')
      throw new TypeError('Promise.all accepts an array');
    var args = Array.prototype.slice.call(arr);
    if (args.length === 0) return resolve([]);
    var remaining = args.length;

    function res(i, val) {
      try {
        if (val && (typeof val === 'object' || typeof val === 'function')) {
          var then = val.then;
          if (typeof then === 'function') {
            then.call(
              val,
              function(val) {
                res(i, val);
              },
              reject
            );
            return;
          }
        }
        args[i] = val;
        if (--remaining === 0) {
          resolve(args);
        }
      } catch (ex) {
        reject(ex);
      }
    }

    for (var i = 0; i < args.length; i++) {
      res(i, args[i]);
    }
  });
};

Promise.resolve = function(value) {
  if (value && typeof value === 'object' && value.constructor === Promise) {
    return value;
  }

  return new Promise(function(resolve) {
    resolve(value);
  });
};

Promise.reject = function(value) {
  return new Promise(function(resolve, reject) {
    reject(value);
  });
};

Promise.race = function(values) {
  return new Promise(function(resolve, reject) {
    for (var i = 0, len = values.length; i < len; i++) {
      values[i].then(resolve, reject);
    }
  });
};

// Use polyfill for setImmediate for performance gains
Promise._immediateFn =
  (typeof setImmediate === 'function' &&
    function(fn) {
      setImmediate(fn);
    }) ||
  function(fn) {
    setTimeoutFunc(fn, 0);
  };

Promise._unhandledRejectionFn = function _unhandledRejectionFn(err) {
  if (typeof console !== 'undefined' && console) {
    console.warn('Possible Unhandled Promise Rejection:', err); // eslint-disable-line no-console
  }
};

/** @suppress {undefinedVars} */
var globalNS = (function() {
  // the only reliable means to get the global object is
  // `Function('return this')()`
  // However, this causes CSP violations in Chrome apps.
  if (typeof self !== 'undefined') {
    return self;
  }
  if (typeof window !== 'undefined') {
    return window;
  }
  if (typeof global !== 'undefined') {
    return global;
  }
  throw new Error('unable to locate global object');
})();

if (!('Promise' in globalNS)) {
  globalNS['Promise'] = Promise;
} else if (!globalNS.Promise.prototype['finally']) {
  globalNS.Promise.prototype['finally'] = finallyConstructor;
}

})));

/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
(function() {
    "use strict";

    window.CmpExamples = window.CmpExamples || {};
})();

/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
(function() {
    "use strict";

    var animationDuration = 220;
    var delay = 2000;

    var states = {
        'NOTICE': 'notice',
        'SUCCESS': 'success',
        'ERROR': 'error',
        'WARNING': 'warning'
    };

    // font awesome icon key to state map
    var stateIconMap = {
        'notice': 'info-circle',
        'success': 'smile',
        'error': 'frown-open',
        'warning': 'exclamation-triangle'
    };

    window.CmpExamples.Notification = window.CmpExamples.Notification || {};

    window.CmpExamples.Notification.show = function(text, state) {
        state = (!state) ? states.NOTICE : state;
        state = state.toLowerCase();

        var notification = document.createElement('div');
        var stateCssClass = 'cmp-examples-notification--' + state;
        notification.classList.add('cmp-examples-notification');
        notification.classList.add(stateCssClass);
        notification.innerHTML = '<i class="cmp-examples-notification__icon fas fa-' + stateIconMap[state] + '"></i><span class="cmp-examples-notification__text">' + text + '</span>';
        document.body.appendChild(notification);

        window.setTimeout(function() {
            notification.classList.add('cmp-examples-notification--out');
            window.setTimeout(function() {
                notification.parentNode.removeChild(notification);
            }, animationDuration);
        }, delay);
    };

    window.CmpExamples.Notification.state = states;

})();

/*
 *  Copyright 2018 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
(function() {
    "use strict";

    var selectors = {
        self: '.cmp-examples-structure__aside',
        showMenu: '[data-cmp-examples-is="showMenu"]'
    };

    var cssClasses = {
        open: 'cmp-examples-structure__aside--open'
    };

    ready(function() {
        var showMenuActions = document.querySelectorAll(selectors.showMenu);
        var aside = document.querySelector(selectors.self);

        if (aside) {
            for (var i = 0; i < showMenuActions.length; ++i) {
                showMenuActions[i].addEventListener('click', function(event) {
                    event.stopPropagation();
                    aside.classList.add(cssClasses.open);
                });
            }

            document.body.addEventListener('click', function (event) {
                aside.classList.remove(cssClasses.open);
            }, false);

            aside.addEventListener('click', function (event) {
                event.stopPropagation();
            }, false);
        }
    });

    function ready(fn) {
        if (document.attachEvent ? document.readyState === "complete" : document.readyState !== "loading"){
            fn();
        } else {
            document.addEventListener('DOMContentLoaded', fn);
        }
    }

})();

/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
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
/*global PR, html_beautify */
(function(Promise, Prettify, htmlBeautify) {
    "use strict";

    var selectors = {
        self: '[data-cmp-examples-is="demo"]',
        info: '[data-cmp-examples-hook-demo="info"]',
        json: '[data-cmp-examples-hook-demo="json"]',
        jsonLink: '[data-cmp-examples-hook-demo="jsonLink"]',
        markup: '[data-cmp-examples-hook-demo="markup"]',
        hideCode: '[data-cmp-examples-hook-demo="hideCode"]',
        showCode: '[data-cmp-examples-hook-demo="showCode"]',
        copyCode: '[data-cmp-examples-hook-demo="copyCode"]',
        activePre: '.cmp-tabs__tabpanel--active pre'
    };

    function escapeHtml(html) {
        var text = document.createTextNode(html);
        var p = document.createElement('p');
        p.appendChild(text);
        return p.innerHTML;
    }

    // From https://developers.google.com/web/fundamentals/primers/promises#promisifying_xmlhttprequest
    // Code samples are licensed under the Apache 2.0 License
    function get(url) {
        // Return a new promise.
        return new Promise(function(resolve, reject) {
            // Do the usual XHR stuff
            var req = new XMLHttpRequest();
            req.open('GET', url);

            req.onload = function() {
                // This is called even on 404 etc
                // so check the status
                if (req.status == 200) {
                    // Resolve the promise with the response text
                    resolve(req.response);
                }
                else {
                    // Otherwise reject with the status text
                    // which will hopefully be a meaningful error
                    reject(Error(req.statusText));
                }
            };

            // Handle network errors
            req.onerror = function() {
                reject(Error("Network Error"));
            };

            // Make the request
            req.send();
        });
    }

    // From https://developers.google.com/web/fundamentals/primers/promises#promisifying_xmlhttprequest
    // Code samples are licensed under the Apache 2.0 License
    function getJSON(url) {
        return get(url).then(JSON.parse);
    }

    document.addEventListener('DOMContentLoaded', function() {
        var deferreds = [];

        var demos = document.querySelectorAll(selectors.self);
        demos = [].slice.call(demos);
        demos.forEach(function(demo) {
            var hideCode = demo.querySelector(selectors.hideCode);
            var showCode = demo.querySelector(selectors.showCode);
            var copyCode = demo.querySelector(selectors.copyCode);
            var info = demo.querySelector(selectors.info);
            var json = demo.querySelector(selectors.json);
            var jsonLink = demo.querySelector(selectors.jsonLink);
            var jsonSrc = "";
            var markup = demo.querySelector(selectors.markup);

            if (jsonLink) {
                jsonSrc = jsonLink.href;

                // a link to the model JSON is presented initially in the markup so that the content
                // can be scraped when exporting a static version of the library
                if (jsonLink.parentNode) {
                    jsonLink.parentNode.removeChild(jsonLink);
                }
            }

            if (json) {
                deferreds.push(getJSON(jsonSrc).then(function(data) {
                    json.innerText = JSON.stringify(data, null, 2);
                }));
            }

            if (markup) {
                markup.innerHTML = escapeHtml(htmlBeautify(markup.innerHTML, { 'preserve_newlines': false, 'indent_size': 2 }));
            }

            if (hideCode) {
                hideCode.addEventListener('click', function() {
                    info.classList.remove('cmp-examples-demo__info--open');
                    hideCode.disabled = true;
                    showCode.disabled = false;
                    copyCode.disabled = true;
                });
            }

            if (showCode) {
                showCode.addEventListener('click', function() {
                    info.classList.add('cmp-examples-demo__info--open');
                    hideCode.disabled = false;
                    showCode.disabled = true;
                    copyCode.disabled = false;
                });
            }

            if (copyCode) {
                copyCode.addEventListener('click', function(event) {
                    var activePre = demo.querySelector(selectors.activePre);

                    if (activePre) {
                        var tempTextarea = document.createElement('textarea');
                        tempTextarea.value = activePre.innerText;
                        document.body.appendChild(tempTextarea);
                        tempTextarea.select();

                        window.CmpExamples.Notification.show('Copied to clipboard', window.CmpExamples.Notification.state.SUCCESS);

                        try {
                            document.execCommand('copy');
                        } catch(error) {
                            window.CmpExamples.Notification.show('Unable to copy to clipboard', window.CmpExamples.Notification.state.ERROR);
                        }

                        document.body.removeChild(tempTextarea);
                    }
                });
            }
        });

        // Prettify once all JSON requests have completed
        Promise.all(deferreds).then(function() {
            Prettify.prettyPrint();
        });
    });
}(Promise, PR, html_beautify));

