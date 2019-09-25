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

    function DataLayerHandler(dataLayer) {
        this.dataLayer = dataLayer;
        this.dataLayer.state = {};
        this._listeners = [];
        this._init();
    }

    DataLayerHandler.prototype._init = function() {
        this._handleEventsBeforeScriptLoad(this.dataLayer);
        this._overridePush();
    };

    // Augments the push function (only for the data layer object) to also handle the event
    DataLayerHandler.prototype._overridePush = function() {
        var that = this;
        that.dataLayer.push = function() {
            var pushArguments = arguments;
            var filteredArguments = arguments;
            Object.keys(pushArguments).forEach(function(key) {
                var event = pushArguments[key];
                that._handleEvent(event);
                // filter out event listeners
                if (event.handler) {
                    delete filteredArguments[key];
                }
            });
            if (filteredArguments[0]) {
                return Array.prototype.push.apply(this, filteredArguments);
            }
        };
    };

    DataLayerHandler.prototype._handleEventsBeforeScriptLoad = function() {
        var that = this;
        this.dataLayer.forEach(function(event, idx) {
            // remove event listeners that were defined before the script load.
            if (event.handler) {
                that.dataLayer.splice(idx, 1);
            }
            that._handleEvent(event);
        });
    };

    DataLayerHandler.prototype._handleEvent = function(event) {
        if (!event) {
            return;
        }
        if (event.data) {
            this._updateState(window.dataLayer.state, event.data);
            this._triggerListeners(event);
        } else if (event.handler) {
            this._registerListener(event);
            this._triggerListener(event);
        }
    };

    // trigger the listener on all previous events matching the listener
    DataLayerHandler.prototype._triggerListener = function(listener) {
        this.dataLayer.forEach(function(event) {
            if (listener.on === CHANGE_EVENT || listener.on === event.type) {
                listener.handler(event);
            }
        });
    };

    DataLayerHandler.prototype._registerListener = function(listener) {
        // add the listener to datalayer._listeners
        this._listeners.push(listener);
        console.log("register an event listener: " + listener.on);
    };

    DataLayerHandler.prototype._triggerListeners = function(event) {
        // loop over all the listeners
        // when a match is found, execute the handler
        var that = this;
        Object.keys(this._listeners).forEach(function(key) {
            var listener = that._listeners[key];
            if (listener.on === CHANGE_EVENT || listener.on === event.type) {
                listener.handler(event);
            }
        });
    };

    DataLayerHandler.prototype._updateState = function(state, object) {
        this._deepMerge(state, object);
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

})();

