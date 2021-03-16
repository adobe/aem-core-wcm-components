/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
/* global CQ */
(function(ns) {
    "use strict";

    /**
     * Panel Container Utilities
     *
     * @namespace
     * @alias CQ.CoreComponents.panelcontainer.v1.utils
     * @type {{}}
     */
    CQ.CoreComponents.panelcontainer.v1.utils = {

        /**
         * Checks whether an [Editable]{@link Granite.author.Editable} is a Panel Container
         *
         * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} to check
         * @returns {Boolean} True if the Editable is a Panel Container, false otherwise
         */
        isPanelContainer: function(editable) {
            var panelContainerType = getPanelContainerType(editable);
            return (panelContainerType !== undefined);
        },

        /**
         * Returns the Panel Container definition associated with an [Editable]{@link Granite.author.Editable}
         *
         * @param {Granite.author.Editable} editable The Panel Container [Editable]{@link Granite.author.Editable}
         * @returns {Object} The Panel Container Type definition, undefined if none is associated
         */
        getPanelContainerType: function(editable) {
            return getPanelContainerType(editable);
        },

        /**
         * Returns the Panel Container HTML element associated with an [Editable]{@link Granite.author.Editable}
         *
         * @param {Granite.author.Editable} editable The Panel Container [Editable]{@link Granite.author.Editable}
         * @returns {HTMLElement} The HTML Element match for the Panel Container, undefined if none is associated
         */
        getPanelContainerHTMLElement: function(editable) {
            var container = getPanelContainerType(editable);
            var element;

            if (container) {
                element = editable.dom.filter(container.selector)[0] || editable.dom.find(container.selector)[0];
            }

            return element;
        },

        /**
         * Returns Panel Container [Editable]{@link Granite.author.Editable}'s child items
         *
         * @param {Granite.author.Editable} editable The Panel Container {@link Granite.author.Editable}
         * @returns {Array<Granite.author.editable>} The Panel Container child editables
         */
        getPanelContainerItems: function(editable) {
            var children = [];
            var container = getPanelContainerType(editable);
            if (container) {
                if (editable.isContainer()) {
                    children = editable.getChildren().filter(isDisplayable);
                }
            }
            return children;
        }
    };

    /**
     * Returns the Panel Container definition associated with an [Editable]{@link Granite.author.Editable}
     *
     * @param {Granite.author.Editable} editable The Panel Container [Editable]{@link Granite.author.Editable}
     * @returns {Object} The Panel Container Type definition, undefined if none is associated
     */
    function getPanelContainerType(editable) {
        var panelContainerType;
        var panelContainerTypes = CQ.CoreComponents.panelcontainer.v1.registry.getAll();

        if (editable && editable.dom) {
            for (var i = 0; i < panelContainerTypes.length; i++) {
                var container = panelContainerTypes[i];
                var selector = container.wrapperSelector || container.selector;

                var match = $(editable.dom[0]).is(selector);

                // look for a match at the editable DOM wrapper, if none is found, try its children.
                if (!match) {
                    var children = editable.dom[0].children;
                    for (var j = 0; j < children.length; j++) {
                        var child = children[j];
                        match = $(child).is(selector);
                        if (match) {
                            break;
                        }
                    }
                }
                if (match) {
                    panelContainerType = container;
                    break;
                }
            }
        }

        return panelContainerType;
    }

    /**
     * Test whether an [Editable]{@link Granite.author.Editable} is displayable in the panel popover.
     * Ignore [Inspectables]{@link Granite.author.Inspectable} and Placeholders.
     *
     * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} to test
     * @returns {Boolean} Whether the [Editable]{@link Granite.author.Editable} is displayed in the panel popover, or not
     */
    function isDisplayable(editable) {
        return (editable instanceof ns.Editable &&
        (editable.isContainer() || (editable.hasActionsAvailable() && !editable.isNewSection())));
    }

})(Granite.author);
