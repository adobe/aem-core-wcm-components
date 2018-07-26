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
/* global Map, CQ */
(function() {
    "use strict";

    var registry = new Map();

    /**
     * Panel Container Registry
     *
     * @namespace
     * @alias CQ.CoreComponents.panelcontainer.registry
     * @type {{}}
     */
    CQ.CoreComponents.panelcontainer.registry = {
        /**
         * Registers a panel container definition by name to the registry
         *
         * @param {Object} panelContainer The panel container definition
         */
        register: function(panelContainer) {
            if (panelContainer !== null && typeof panelContainer === "object" && panelContainer.name) {
                registry.set(panelContainer.name, panelContainer);
            }
        },

        /**
         * Returns a panel container definition by name from the registry
         *
         * @param {String} name The panel container name
         * @returns {Object} The found panel container definition, undefined otherwise
         */
        get: function(name) {
            return registry.get(name);
        },

        /**
         * Returns all registered panel container definitions
         *
         * @returns {Array} An Array of registered panel container definitions
         */
        getAll: function() {
            var values = [];
            registry.forEach(function(value, key, map) {
                values.push(value);
            });
            return values;
        }
    };

})();
