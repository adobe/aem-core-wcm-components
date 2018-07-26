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
/* global CQ */
(function() {
    "use strict";

    /**
     * Panel Container Utilities
     *
     * @namespace
     * @alias CQ.CoreComponents.panelcontainer.utils
     * @type {{}}
     */
    CQ.CoreComponents.panelcontainer.utils = {

        /**
         * Checks whether an [Editable]{@link Granite.author.Editable} is a panel container
         *
         * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} to check
         * @returns {Boolean} True if the Editable is a panel container, false otherwise
         */
        isPanelContainer: function(editable) {
            var panelContainerType = getPanelContainerType(editable);
            return (panelContainerType !== undefined);
        },

        /**
         * Returns the panel container definition associated with an [Editable]{@link Granite.author.Editable}
         *
         * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} to check
         * @returns {Object} The Panel Container Type definition, undefined if none is associated
         */
        getPanelContainerType: function(editable) {
            return getPanelContainerType(editable);
        }
    };

    function getPanelContainerType(editable) {
        var panelContainerType;
        var panelContainerTypes = CQ.CoreComponents.panelcontainer.registry.getAll();

        if (editable && editable.dom) {
            for (var i = 0; i < panelContainerTypes.length; i++) {
                var container = panelContainerTypes[i];
                var match = editable.dom.find(container.selector).addBack(editable.dom).length > 0;
                if (match) {
                    panelContainerType = container;
                    break;
                }
            }
        }

        return panelContainerType;
    }

})();
