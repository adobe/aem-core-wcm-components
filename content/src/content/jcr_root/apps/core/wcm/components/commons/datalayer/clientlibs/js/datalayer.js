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

    var CHANGE_EVENT = "datalayer:change";

    // Initializes the data layer
    function init() {
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.state = {};
        window.dataLayer._listeners = [];
        populateDataLayer();
        handleEventsBeforeScriptLoad(window.dataLayer);
        overridePush();
        populateDataLayerAfterOverride();
        console.log("data layer script initialized");
    }

    // Augments the push function (only for the data layer object) to also handle the event
    function overridePush() {
        window.dataLayer.push = function() {
            var pushArguments = arguments;
            var filteredArguments = arguments;
            Object.keys(pushArguments).forEach(function(key) {
                var event = pushArguments[key];
                handleEvent(event);
                // filter out event listeners
                if (event.handler) {
                    delete filteredArguments[key];
                }
            });
            if (filteredArguments[0]) {
                return Array.prototype.push.apply(this, filteredArguments);
            }
        };
    }

    function handleEventsBeforeScriptLoad(dataLayer) {
        dataLayer.forEach(function(event, idx) {
            // remove event listeners that were defined before the script load.
            if (event.handler) {
                dataLayer.splice(idx, 1);
            }
            handleEvent(event);
        });
    }

    function handleEvent(event) {
        if (!event) {
            return;
        }
        if (event.data) {
            updateState(window.dataLayer.state, event.data);
            triggerListeners(event);
        } else if (event.handler) {
            registerListener(event);
            triggerListener(event);
        }
    }

    // trigger the listener on all previous events matching the listener
    function triggerListener(listener) {
        window.dataLayer.forEach(function(event) {
            if (listener.on === CHANGE_EVENT || listener.on === event.type) {
                listener.handler(event);
            }
        });
    }

    function registerListener(listener) {
        // add the listener to datalayer._listeners
        window.dataLayer._listeners.push(listener);
        console.log("register an event listener: " + listener.on);
    }

    function triggerListeners(event) {
        // loop over all the listeners
        // when a match is found, execute the handler
        Object.keys(window.dataLayer._listeners).forEach(function(key) {
            var listener = window.dataLayer._listeners[key];
            if (listener.on === CHANGE_EVENT || listener.on === event.type) {
                listener.handler(event);
            }
        });
    }

    function updateState(state, object) {
        deepMerge(state, object);
    }

    function deepMerge(target, source) {
        var tmpSource = {};
        if (isObject(target) && isObject(source)) {
            Object.keys(source).forEach(function(key) {
                if (isObject(source[key])) {
                    if (!target[key]) {
                        tmpSource[key] = {};
                        Object.assign(target, tmpSource);
                    }
                    deepMerge(target[key], source[key]);
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
    }

    function isObject(item) {
        return (item && typeof item === "object" && !Array.isArray(item));
    }

    function populateDataLayer() {
        window.dataLayer.push({
            "type": "carousel clicked",
            "data": {
                "component": {
                    "carousel": {
                        "carousel3": {
                            "id": "/content/mysite/en/home/jcr:content/root/carousel3",
                            "items": {}
                        }
                    }
                }
            }
        });

        window.dataLayer.push({
            "type": "tab viewed",
            "data": {
                "component": {
                    "tab": {
                        "tab2": {
                            "id": "/content/mysite/en/home/jcr:content/root/tab2",
                            "items": {}
                        }
                    }
                }
            }
        });

        window.dataLayer.push({
            "type": "page loaded",
            "data": {
                "page": {
                    "id": "/content/mysite/en/products/crossfit",
                    "siteLanguage": "en-us",
                    "siteCountry": "US",
                    "pageType": "product detail",
                    "pageName": "pdp - crossfit zoom",
                    "pageCategory": "womens > shoes > athletic"
                }
            }
        });

        window.dataLayer.push({
            "on": "datalayer:change",
            "handler": function(event) {
                // the type
                console.log(event.type);
                // the data that changed
                console.log(event.data);
                // the state
                console.log(window.dataLayer.state);
            }
        });
    }

    function populateDataLayerAfterOverride() {
        window.dataLayer.push({
            "type": "page updated",
            "data": {
                "page": {
                    "new prop": "I'm new",
                    "id": "NEW/content/mysite/en/products/crossfit",
                    "siteLanguage": "en-us",
                    "siteCountry": "US",
                    "pageType": "product detail",
                    "pageName": "pdp - crossfit zoom",
                    "pageCategory": "womens > shoes > athletic"
                }
            }
        });

        window.dataLayer.push({
            "type": "component updated",
            "data": {
                "component": {
                    "image": {
                        "image4": {
                            "id": "/content/mysite/en/home/jcr:content/root/image4",
                            "items": {}
                        }
                    }
                }
            }
        });

        window.dataLayer.push({
            "type": "removed",
            "data": {
                "component": {
                    "image": {
                        "image5": {
                            "id": "/content/mysite/en/home/jcr:content/root/image4",
                            "items": undefined
                        }
                    }
                }
            }
        });

        window.dataLayer.push({
            "on": "removed",
            "handler": function(event) {
                // the type
                console.log(event.type);
                // the data that changed
                console.log(event.data);
                // the state
                console.log(window.dataLayer.state);
            }
        });

        window.dataLayer.push({
            "type": "removed",
            "data": {
                "component": {
                    "image": {
                        "image4": {
                            "id": "/content/mysite/en/home/jcr:content/root/image4",
                            "items": undefined
                        }
                    }
                }
            }
        });

    }

    init();

})();

