/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

    /* eslint no-console: "off" */
    /* eslint no-unused-vars: "off" */

    /**
     * @typedef {String} DataLayerEvents
     **/

    /**
     * Enumeration of data layer events.
     *
     * @enum {DataLayerEvents}
     * @readonly
     */
    var events = {
        /** Represents an event triggered for any change in the data layer state */
        CHANGE: "datalayer:change",
        /** Represents an event triggered for any event push to the data layer */
        EVENT: "datalayer:event",
        /** Represents an event triggered when the data layer has initialized */
        READY: "datalayer:ready"
    };

    /**
     * @typedef {String} ListenerScope
     **/

    /**
     * Enumeration of listener scopes.
     *
     * @enum {ListenerScope}
     * @readonly
     */
    var listenerScope = {
        /** Past events only */
        PAST: "past",
        /** Future events only */
        FUTURE: "future",
        /** All events, past and future */
        ALL: "all"
    };

    /**
     * @typedef  {Object} ListenerOnConfig
     * @property {String} on Name of the event to bind to.
     * @property {String} [selector] Object key in the state to bind to.
     * @property {ListenerScope} [scope] Scope of the listener.
     * @property {Function} handler Handler to execute when the bound event is triggered.
     */

    /**
     * @typedef  {Object} ListenerOffConfig
     * @property {String} off Name of the event to unbind.
     * @property {Function} [handler] Handler for a previously attached event to unbind.
     */

    /**
     * @typedef {Object} DataConfig
     * @property {Object} data Data to be updated in the state.
     */

    /**
     * @typedef {Object} EventConfig
     * @property {String} eventName Name of the event.
     * @property {Object} [info] Additional information to pass to the event handler.
     * @property {DataConfig.data} [data] Data to be updated in the state.
     */

    /**
     * @typedef {DataConfig | EventConfig | ListenerOnConfig | ListenerOffConfig} ItemConfig
     */

    /**
     * @class DataLayer
     * @classdesc Data layer controller that augments the passed data layer array and handles eventing.
     * @param {Array} dataLayer The data layer array.
     */
    function DataLayer(dataLayer) {
        var that = this;

        that.dataLayer = dataLayer;
        that.state = {};

        /**
         * Returns a deep copy of the data layer state.
         *
         * @returns {Object} The deep copied state object.
         */
        that.dataLayer.getState = function() {
            // use deep copying technique of JSON stringify and parsing the state.
            return JSON.parse(JSON.stringify(that.state));
        };

        // TODO remove _listeners from data layer (this is used for testing): replace this.dataLayer._listeners and that.dataLayer._listeners
        that.dataLayer._listeners = [];
        // this._listeners = [];
        that._init();
    }

    /**
     * Initializes the data layer.
     *
     * @private
     */
    DataLayer.prototype._init = function() {
        this._handleItemsBeforeScriptLoad(this.dataLayer);
        this._overridePush();
    };

    /**
     * Handles the items that were pushed before the data layer script loaded.
     *
     * @private
     */
    DataLayer.prototype._handleItemsBeforeScriptLoad = function() {
        var that = this;
        this.dataLayer.forEach(function(item, idx) {
            // remove event listeners defined before the script load
            if (that._isListener(item)) {
                that.dataLayer.splice(idx, 1);
            }
            that._handleItem(item);
        });
    };

    /**
     * Overrides the push function of DataLayer.dataLayer to handle item pushes.
     *
     * @private
     */
    DataLayer.prototype._overridePush = function() {
        var that = this;

        /**
         * Pushes one or more items to the data layer.
         *
         * @param {...ItemConfig} var_args The items to add to the data layer.
         * @returns {Number} The length of the data layer following push.
         */
        that.dataLayer.push = function(var_args) { /* eslint-disable-line camelcase */
            var pushArguments = arguments;
            var filteredArguments = arguments;

            Object.keys(pushArguments).forEach(function(key) {
                var item = pushArguments[key];
                that._handleItem(item);

                // filter out event listeners
                if (that._isListener(item)) {
                    delete filteredArguments[key];
                }
            });

            if (filteredArguments[0]) {
                return Array.prototype.push.apply(this, filteredArguments);
            }
        };
    };

    /**
     * Handles an item pushed to the data layer.
     *
     * @param {ItemConfig} item The item configuration.
     * @private
     */
    DataLayer.prototype._handleItem = function(item) {
        if (!item) {
            return;
        }
        if (this._isListener(item)) {
            if (item.on) {
                this._registerListener(item);
                this._triggerListener(item);
            } else if (item.off) {
                this._unregisterListener(item);
            }
        } else {
            if (item.data) {
                this._updateState(item);
                this._triggerListeners(item, events.CHANGE);
            }
            if (item.eventName) {
                this._triggerListeners(item, events.EVENT);
            }
        }
    };

    /**
     * Updates the state with the passed data configuration.
     *
     * @param {DataConfig} item The data configuration.
     * @private
     */
    DataLayer.prototype._updateState = function(item) {
        DataLayer.utils.deepMerge(this.state, item.data);
    };

    DataLayer.prototype._triggerListeners = function(item, eventName) {
        this.dataLayer._listeners.forEach(function(listener) {
            if (listener.on === eventName || listener.on === item.eventName) {
                listener.handler(item);
            }
        });
    };

    DataLayer.prototype._triggerListener = function(listener) {
        this.dataLayer.forEach(function(item) {
            if (listener.on === events.CHANGE || listener.on === events.EVENT || listener.on === item.eventName) {
                listener.handler(item);
            }
        });
    };

    /**
     * Registers a listener based on a listener on configuration.
     *
     * @param {ListenerOnConfig} item The listener on configuration.
     * @private
     */
    DataLayer.prototype._registerListener = function(item) {
        if (this._getListenerIndex(item) === -1) {
            this.dataLayer._listeners.push(item);
            console.log("event listener registered on: ", item.on);
        }
    };

    /**
     * Unregisters a listener based on a listener off configuration.
     *
     * @param {ListenerOffConfig} item The listener off configuration.
     * @private
     */
    DataLayer.prototype._unregisterListener = function(item) {
        var tmp = item;
        tmp.on = item.off;
        delete tmp.off;
        var idx = this._getListenerIndex(tmp);
        if (idx > -1) {
            this.dataLayer._listeners.splice(idx, 1);
            console.log("event listener unregistered on: ", tmp.on);
        }
    };

    /**
     * Gets the index of a listener based on a listener on configuration.
     *
     * @param {ListenerOnConfig} item The listener on configuration.
     * @returns {Number} The index of the listener.
     * @private
     */
    DataLayer.prototype._getListenerIndex = function(item) {
        var listenerFound = true;
        for (var i = 0; i <  this.dataLayer._listeners.length; i++) {
            var existingListener = this.dataLayer._listeners[i];
            if (Object.keys(existingListener).length !== Object.keys(item).length) {
                listenerFound = false;
                break;
            }
            for (var j = 0; j < Object.keys(existingListener).length; j++) {
                var field = Object.keys(existingListener)[j];
                if (existingListener[field].toString() !== item[field].toString()) {
                    listenerFound = false;
                    break;
                }
            }
            if (listenerFound) {
                return i;
            }
        }
        return -1;
    };

    /**
     * Determines whether the passed item is a listener configuration.
     *
     * @param {ItemConfig} item The listener on/off configuration.
     * @returns {Boolean} true if the item is a listener on/off configuration, false otherwise.
     * @private
     */
    DataLayer.prototype._isListener = function(item) {
        return !!(item.handler && (item.on || item.off));
    };

    /**
     * Data Layer utilities.
     *
     * @type {Object}
     */
    DataLayer.utils = {};

    /**
     * Deep merges a source and target object.
     *
     * @param {Object} target The target object.
     * @param {Object} source The source object.
     * @static
     */
    DataLayer.utils.deepMerge = function(target, source) {
        var tmpSource = {};
        var that = this;
        if (that.isObject(target) && that.isObject(source)) {
            Object.keys(source).forEach(function(key) {
                if (that.isObject(source[key])) {
                    if (!target[key]) {
                        tmpSource[key] = {};
                        Object.assign(target, tmpSource);
                    }
                    that.deepMerge(target[key], source[key]);
                } else {
                    if (source[key] === undefined) {
                        delete target[key];
                    } else {
                        tmpSource[key] = source[key];
                        Object.assign(target, tmpSource);
                    }
                }
            });
        }
    };

    /**
     * Checks whether the passed object is an object.
     *
     * @param {Object} obj The object that will be checked.
     * @returns {Boolean} true if it is an object, false otherwise.
     * @static
     */
    DataLayer.utils.isObject = function(obj) {
        return (obj && typeof obj === "object" && !Array.isArray(obj));
    };

    window.addEventListener("datalayer:prepopulated", function() {
        console.log("data layer prepopulated - let's initialize the data layer");
        window.dataLayer = window.dataLayer || [];
        new DataLayer(window.dataLayer);
        var readyEvent = new CustomEvent(events.READY);
        window.dispatchEvent(readyEvent);
        console.log("data layer script initialized");
    });

    /**
     * Triggered when there is change in the data layer state.
     *
     * @event DataLayerEvents.CHANGE
     * @type {Object}
     * @property {Object} data Data pushed that caused a change in the data layer state.
     */

    /**
     * Triggered when an event is pushed to the data layer.
     *
     * @event DataLayerEvents.EVENT
     * @type {Object}
     * @property {String} eventName Name of the committed event.
     * @property {Object} info Additional information passed with the committed event.
     * @property {Object} data Data that was pushed alongside the event.
     */

    /**
     * Triggered when an arbitrary event is pushed to the data layer.
     *
     * @event <eventName>
     * @type {Object}
     * @property {String} eventName Name of the committed event.
     * @property {Object} info Additional information passed with the committed event.
     * @property {Object} data Data that was pushed alongside the event.
     */

    /**
     * Triggered when the data layer has initialized.
     *
     * @event DataLayerEvents.READY
     * @type {Object}
     */

})();
