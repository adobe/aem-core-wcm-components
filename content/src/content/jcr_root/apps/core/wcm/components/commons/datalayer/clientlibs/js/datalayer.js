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

    var CHANGE_EVENT = "datalayer:change";
    var EVENT_EVENT = "datalayer:event";

    function DataLayerHandler(dataLayer) {
        this.dataLayer = dataLayer;
        this.dataLayer.state = {};
        // TODO remove _listeners from data layer (this is used for testing): replace this.dataLayer._listeners and that.dataLayer._listeners
        this.dataLayer._listeners = [];
        // this._listeners = [];
        this._init();
    }

    DataLayerHandler.prototype._init = function() {
        this._handleItemsBeforeScriptLoad(this.dataLayer);
        this._overridePush();
    };

    DataLayerHandler.prototype._handleItemsBeforeScriptLoad = function() {
        var that = this;
        this.dataLayer.forEach(function(item, idx) {
            // remove event listeners defined before the script load
            if (that._isListener(item)) {
                that.dataLayer.splice(idx, 1);
            }
            that._handleItem(item);
        });
    };

    // Augments the push function to also handle the item
    DataLayerHandler.prototype._overridePush = function() {
        var that = this;
        // restrict the override to the data layer object
        that.dataLayer.push = function() {
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

    DataLayerHandler.prototype._handleItem = function(item) {
        if (!item) {
            return;
        }
        if (this._isListener(item)) {
            if (item.on) {
                this._registerListener(item);
                this._triggerListener(item);
            } else if (item.off) {
                this._removeListener(item);
            }
        } else {
            if (item.data) {
                this._updateState(item);
                this._triggerListeners(item, CHANGE_EVENT);
            }
            if (item.eventName) {
                this._triggerListeners(item, EVENT_EVENT);
            }
        }
    };

    DataLayerHandler.prototype._isListener = function(item) {
        return (item.handler && (item.on || item.off));
    };

    DataLayerHandler.prototype._updateState = function(item) {
        this._deepMerge(this.dataLayer.state, item.data);
    };

    DataLayerHandler.prototype._triggerListeners = function(item, eventName) {
        this.dataLayer._listeners.forEach(function(listener) {
            if (listener.on === eventName || listener.on === item.eventName) {
                listener.handler(item);
            }
        });
    };

    DataLayerHandler.prototype._removeListener = function(listener) {
        var tmp = listener;
        tmp.on = listener.off;
        delete tmp.off;
        var idx = this._getListenerIndex(tmp);
        if (idx > -1) {
            this.dataLayer._listeners.splice(idx, 1);
            console.log("event listener unregistered on: ", tmp.on);
        }
    };

    DataLayerHandler.prototype._registerListener = function(listener) {
        if (this._getListenerIndex(listener) === -1) {
            this.dataLayer._listeners.push(listener);
            console.log("event listener registered on: ", listener.on);
        }
    };

    DataLayerHandler.prototype._getListenerIndex = function(listener) {
        var listenerFound = true;
        for (var i = 0; i <  this.dataLayer._listeners.length; i++) {
            var existingListener = this.dataLayer._listeners[i];
            if (Object.keys(existingListener).length !== Object.keys(listener).length) {
                listenerFound = false;
                break;
            }
            for (var j = 0; j < Object.keys(existingListener).length; j++) {
                var field = Object.keys(existingListener)[j];
                if (existingListener[field].toString() !== listener[field].toString()) {
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

    // trigger the listener on all previous items matching the listener
    DataLayerHandler.prototype._triggerListener = function(listener) {
        this.dataLayer.forEach(function(item) {
            if (listener.on === CHANGE_EVENT || listener.on === EVENT_EVENT || listener.on === item.eventName) {
                listener.handler(item);
            }
        });
    };

    DataLayerHandler.prototype._deepMerge = function(target, source) {
        var tmpSource = {};
        var that = this;
        if (this._isObject(target) && this._isObject(source)) {
            Object.keys(source).forEach(function(key) {
                if (that._isObject(source[key])) {
                    if (!target[key]) {
                        tmpSource[key] = {};
                        Object.assign(target, tmpSource);
                    }
                    that._deepMerge(target[key], source[key]);
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

    DataLayerHandler.prototype._isObject = function(item) {
        return (item && typeof item === "object" && !Array.isArray(item));
    };

    window.addEventListener("datalayer:prepopulated", function() {
        console.log("data layer prepopulated - let's initialize the data layer");
        window.dataLayer = window.dataLayer || [];
        new DataLayerHandler(window.dataLayer);
        var readyEvent = new CustomEvent("datalayer:ready");
        window.dispatchEvent(readyEvent);
        console.log("data layer script initialized");
    });

    /**
     * Triggered when there is change in the data layer state.
     *
     * @event datalayer:change
     * @type {object}
     * @property {object} data Data pushed that caused a change in the data layer state.
     */

    /**
     * Triggered when an event is pushed to the data layer.
     *
     * @event datalayer:event
     * @type {object}
     * @property {string} eventName Name of the committed event.
     * @property {object} info Additional information passed with the committed event.
     * @property {object} data Data that was pushed alongside the event.
     */

     /**
      * Triggered when an arbitrary event is pushed to the data layer.
      *
      * @event <eventName>
      * @type {object}
      * @property {string} eventName Name of the committed event.
      * @property {object} info Additional information passed with the committed event.
      * @property {object} data Data that was pushed alongside the event.
      */

})();
