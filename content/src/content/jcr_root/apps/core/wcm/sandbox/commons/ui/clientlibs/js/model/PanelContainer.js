(function($, ns, channel, window, undefined) {
    "use strict";

    // TODO: draft

    ns.PanelContainer = ns.util.createClass({

        config: {},

        activeIndex: 0,
        data: {},

        constructor: function PanelContainer(config) { // NB: Use only config object parameter; other form is deprecated
            this.config = config;

            this._readData();
        },

        /**
         * Navigates to the provided index
         *
         * @returns {Object} The panel container items data
         */
        navigate: function(index) {
            // internally messages the relevant UI widget to do the work.
            // updates the active index.
        },

        /**
         * Get the index of the currently active panel
         *
         * @returns {Object} The panel container items data
         */
        getActiveIndex: function() {
            return this.activeIndex;
        },

        /**
         * Get the items data for this panel container
         *
         * @returns {Object} The panel container items data
         */
        getItems: function() {
            return this.data.items;
        },

        /**
         * Path to the component represented by this panel container
         *
         * @returns {String} The path of this panel container
         */
        getPath: function() {
            return this.config.path;
        },

        /**
         * Persists item updates to an endpoint, returns a deferred for handling
         *
         * @param {Array} order IDs of the items in order
         * @param {Array} deleted IDs of the deleted items
         * @returns {$.Deferred} The deferred for callback handling
         */
        update: function(order, deleted) {
            var myDeferred = {};
            // should read the data once complete.
            return myDeferred;
        },

        /**
         * Reads model json data from component path
         *
         * @returns {Object} The panel container items data
         */
        _readData: function() {
            // do the data read
            var data = {};
            return data;
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
