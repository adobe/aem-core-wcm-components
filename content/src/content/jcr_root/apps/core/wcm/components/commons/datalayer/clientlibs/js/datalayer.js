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

    // Initializes the data layer
    function init() {
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.state = {};
        populateDataLayer();
        handleEvents(window.dataLayer);
        overridePush();
        populateDataLayerAfterOverride();
        console.log("data layer script initialized");
    }

    // Augments the push function (only for the data layer object) to also handle the event
    function overridePush() {
        window.dataLayer.push = function() {
            var pushArguments = arguments;
            Object.keys(pushArguments).forEach(function(key) {
                handleEvent(pushArguments[key]);
            });
            return Array.prototype.push.apply(this, pushArguments);
        };
    }

    function handleEvents(dataLayer) {
        dataLayer.forEach(function(event) {
            handleEvent(event);
        });
    }

    function handleEvent(event) {
        if (!event && !event.type) {
            return;
        }
        switch (event.type) {
            case "updated":
                updateState(window.dataLayer.state, event.object);
                break;
            case "listenerdefined":
                registerListener(event);
                break;
            case "removed":
                removeFromState(window.dataLayer.state, event.object);
                break;
            default:
                return;
        }
    }

    function removeFromState(state, object) {
        deepRemove(state, object);
    }

    function deepRemove(target, source) {
        if (isObject(target) && isObject(source)) {
            Object.keys(source).forEach(function(key) {
                if (target[key]) {
                    if (isObject(source[key]) && Object.keys(source[key]).length > 0) {
                        deepRemove(target[key], source[key]);
                    } else {
                        delete target[key];
                    }
                }
            });
        }
    }

    function updateState(state, object) {
        deepMerge(state, object);
    }

    function registerListener(event) {
        console.log("register an event listener: " + event.object.listener.type);
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
                    tmpSource[key] = source[key];
                    Object.assign(target, tmpSource);
                }
            });
        }
    }

    function isObject(item) {
        return (item && typeof item === "object" && !Array.isArray(item));
    }

    function populateDataLayer() {
        window.dataLayer.push({
            "type": "updated",
            "object": {
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
            "type": "updated",
            "object": {
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
            "type": "updated",
            "object": {
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
            "type": "listenerdefined",
            "object": {
                "listener": {
                    "type": "clicked",
                    "target": {},
                    "handler": function(component) {
                        console.log("clicked on component: " + component);
                    }
                }
            }
        });
    }

    function populateDataLayerAfterOverride() {
        window.dataLayer.push({
            "type": "updated",
            "object": {
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
            "type": "updated",
            "object": {
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
            "object": {
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

    }

    init();

})();

