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
         * Returns index of the container component item (accordion, tabs) that corresponds to the deep link in the URL fragment.
         *
         * @param {Object} component The Accordion or Tabs component.
         * @param {String} itemType The type of the item as defined in the component.
         * @returns {Number} the index within the items array if the item exists, -1 otherwise.
         */
        getDeepLinkItemIdx: function(component, itemType) {
            if (window.location.hash) {
                var deepLinkId = window.location.hash.substring(1);
                if (document.getElementById(deepLinkId) &&
                    deepLinkId && component &&
                    component._config && component._config.element && component._config.element.id &&
                    component._elements && component._elements[itemType] &&
                    deepLinkId.indexOf(component._config.element.id + "-item-") === 0) {
                    for (var i = 0; i < component._elements[itemType].length; i++) {
                        var item = component._elements[itemType][i];
                        if (item.id === deepLinkId) {
                            return i;
                        }
                    }
                }
                return -1;
            }
        },

        /**
         * Returns the item of the container component (accordion, tabs) that corresponds to the deep link in the URL fragment.
         *
         * @param {Object} component The Accordion or Tabs component.
         * @param {String} itemType The type of the item as defined in the component.
         * @returns {Object} the item if it exists, undefined otherwise.
         */
        getDeepLinkItem: function(component, itemType) {
            var idx = window.CQ.CoreComponents.container.utils.getDeepLinkItemIdx(component, itemType);
            if (component && component._elements && component._elements[itemType]) {
                return component._elements[itemType][idx];
            }
        }

    };
}());
