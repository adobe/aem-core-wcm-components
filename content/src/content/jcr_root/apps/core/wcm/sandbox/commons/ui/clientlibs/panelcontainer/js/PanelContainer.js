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
(function($, ns, channel, window, undefined) {
    "use strict";

    var DATA_ENDPOINT_SUFFIX = ".model.json";

    CQ.CoreComponents.PanelContainer = ns.util.createClass({

        _config: {},
        _data: {},

        constructor: function PanelContainer(config) {
            this._config = config;
            this.getItems();
        },

        /**
         * Navigates to the panel at the provided index. Posts a message to the content
         * frame and lets the UI widget related to this container do the navigation.
         *
         * @param {Number} index Index of the panel to navigate to
         */
        navigate: function(index) {
            Granite.author.ContentFrame.postMessage(this._config.type, { panel: index });
        },

        /**
         * Gets the index of the currently active panel
         *
         * @returns {Object} The panel container items data
         */
        getActiveIndex: function() {
            var that = this;
            var activeIndex = 0;
            if (that._config.el && that._config.panelContainer) {
                var items = $(this._config.el).find(that._config.panelContainer.itemSelector);
                items.each(function(index) {
                    if (items[index].is(that._config.panelContainer.itemActiveSelector)) {
                        activeIndex = index;
                    }
                });
            }
            return activeIndex;
        },

        /**
         * Gets the items data for this panel container
         *
         * @returns {Promise.<Array.<*>>} A promise for handling completion, with items as resolved values
         */
        getItems: function() {
            var that = this;
            var deferred = $.Deferred();

            if (that._data.items) {
                // data is already cached, don't re-fetch
                deferred.resolve(that._data.items);
            }

            $.ajax({
                url: that._config.path + DATA_ENDPOINT_SUFFIX
            }).done(function(data) {
                if (data) {
                    that._data = data;
                }
                deferred.resolve(that._data.items);
            }).fail(function() {
                deferred.resolve([]);
            });

            return deferred.promise();
        },

        /**
         * Returns the path to the component represented by this panel container
         *
         * @returns {String} The path of this panel container
         */
        getPath: function() {
            return this._config.path;
        },

        /**
         * Persists item updates to an endpoint, returns a deferred for handling
         *
         * @param {Array} ordered IDs of the items in order
         * @param {Array} deleted IDs of the deleted items
         * @returns {Promise} The promise for completion handling
         */
        update: function(ordered, deleted) {
            var deferred = $.Deferred;
            return deferred.promise();
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
