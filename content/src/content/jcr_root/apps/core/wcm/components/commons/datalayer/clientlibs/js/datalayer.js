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
        populateDataLayer();
        window.dataLayer.state = getComputeState(window.dataLayer);
        overridePush();
        populateDataLayerAfterOverride();
        console.log("data layer script initialized");
    }

    // Returns the computed state by iterating over all the events stored in the data layer array
    function getComputeState(dataLayer) {
        var state = {};
        dataLayer.forEach(function(event) {
            updateState(state, event);
        });
        return state;
    }


    // Augments the push function (only for the data layer object) to also handle the event
    function overridePush() {
        window.dataLayer.push = function() {
            var event = arguments[0];
            handleEvent(event);
            return Array.prototype.push.apply(this, arguments);
        };
    }

    function handleEvent(event) {
        if (!event) {
            return;
        }
        if (event.type === "updated") {
            updateState(window.dataLayer.state, event.object);
        } else if (event.type === "listenerdefined") {
            registerListener(event);
        }
    }

    function registerListener(event) {
        
    }

    function updateState(state, event) {
        deepMerge(state, event.object);
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

    }

    init();

})();

