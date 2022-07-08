/*******************************************************************************
 * Copyright 2020 Adobe
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

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.container = window.CQ.CoreComponents.container || {};
    window.CQ.CoreComponents.container.utils = {};

    /**
     * Utilities for Container Components (accordion, tabs)
     *
     * @namespace
     * @alias CQ.CoreComponents.container.utils
     * @type {{}}
     */
    window.CQ.CoreComponents.container.utils = {

        /**
         * Removes the hash from the URL.
         */
        removeUrlHash: function() {
            history.replaceState(undefined, undefined, " ");
        },

        /**
         * Updates the URL hash with the panel ID without scrolling to it.
         *
         * @param {Object} component The container component (e.g. Accordion, Carousel, Tabs).
         * @param {String} itemType The type of the item as defined in the component.
         * @param {Number} index The index of the container item
         */
        updateUrlHash: function(component, itemType, index) {
            if (component && component._elements && component._elements[itemType] &&
                component._elements[itemType][index] && component._elements[itemType][index].id) {
                var ID = component._elements[itemType][index].id;
                history.replaceState(undefined, undefined, "#" + ID);
            }
        },

        /**
         * Returns the index of the component item (accordion, carousel, tabs) that:
         * - either corresponds to the deep link in the URL fragment
         * - or contains the element that corresponds to the deep link in the URL fragment
         *
         * @param {Object} component The container component (Accordion, Carousel or Tabs).
         * @param {String} itemType The type of the item as defined in the component.
         * @param {String} itemContentType The type of the item content as defined in the component.
         * @returns {Number} the index within the items array if the item exists, -1 otherwise.
         */
        getDeepLinkItemIdx: function(component, itemType, itemContentType) {
            if (window.location.hash) {
                var deepLinkId = window.location.hash.substring(1);
                if (deepLinkId && document.getElementById(deepLinkId) &&
                    component && component._config && component._config.element &&
                    component._elements[itemType] &&
                    component._config.element.querySelector("[id='" + deepLinkId + "']")) {
                    for (var i = 0; i < component._elements[itemType].length; i++) {
                        var item = component._elements[itemType][i];
                        var itemContentContainsId = false;
                        if (component._elements[itemContentType]) {
                            var itemContent = component._elements[itemContentType][i];
                            itemContentContainsId = itemContent && itemContent.querySelector("[id='" + deepLinkId + "']");
                        }
                        if (item.id === deepLinkId || itemContentContainsId) {
                            return i;
                        }
                    }
                }
                return -1;
            }
            return -1;
        },

        /**
         * Returns the item of the container component (accordion, carousel, tabs) that:
         * - either corresponds to the deep link in the URL fragment
         * - or contains the element that corresponds to the deep link in the URL fragment
         *
         * @param {Object} component The Accordion or Tabs component.
         * @param {String} itemType The type of the item as defined in the component.
         * @param {String} itemContentType The type of the item content as defined in the component.
         * @returns {Object} the item if it exists, undefined otherwise.
         */
        getDeepLinkItem: function(component, itemType, itemContentType) {
            var idx = window.CQ.CoreComponents.container.utils.getDeepLinkItemIdx(component, itemType, itemContentType);
            if (component && component._elements && component._elements[itemType]) {
                return component._elements[itemType][idx];
            }
        },

        /**
         * Scrolls the browser on page reload (if URI contains URI fragment) to the item of the container component (accordion, tabs)
           that corresponds to the deep link in the URI fragment.
         * This method fixes the issue existent with Chrome and related browsers, which are not scrolling on page reload (if URI contains URI fragment)
           to the element that corresponds to the deep link in the URI fragment.
         * Small setTimeout is needed, otherwise the scrolling will not work on Chrome.
         */
        scrollToAnchor: function() {
            setTimeout(function() {
                if (window.location.hash) {
                    var id = decodeURIComponent(window.location.hash.substring(1));
                    var anchorElement = document.getElementById(id);
                    if (anchorElement && anchorElement.offsetTop) {
                        anchorElement.scrollIntoView();
                    }
                }
            }, 100);
        }
    };
}());
