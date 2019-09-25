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

    window.dataLayer = window.dataLayer || [];


    function populateDataLayerBefore() {
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

    function populateDataLayerAfter() {
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


    function basicUseCases() {

        // ====================================  Add data ======================================

        // Add the page data
        window.dataLayer.push({
            "data": {
                "page": {
                    "id": "/content/my-site/en/about-us",
                    "pageName": "About Us",
                    "siteLanguage": "en-us",
                    "siteCountry": "US"
                }
            }
        });

        // Add a component
        window.dataLayer.push({
            "data": {
                "component": {
                    "tab": {
                        "tab2": {
                            "id": "/content/mysite/en/home/jcr:content/root/tab2",
                            "title": "the ocean",
                            "items": {}
                        }
                    }
                }
            }
        });

        // Remove data
        window.dataLayer.push({
            "data": {
                "component": {
                    "image": {
                        "image5": undefined
                    }
                }
            }
        });

        // ====================================  Add event ======================================


        // Add an event (without data)
        window.dataLayer.push({
            "event": "page loaded"
        });

        // Add an event with a reference to the data
        window.dataLayer.push({
            "event": "click",
            "id": ["component", "/content/my-site/en/about-us/jcr:content/root/responsivegrid/teaser"]
        });

        // Add an event with its data
        window.dataLayer.push({
            "event": "image viewed",
            "data": {
                "component": {
                    "image": {
                        "image5": {
                            "id": "/content/mysite/en/home/jcr:content/root/image5",
                            "fileReference": "/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg"
                        }
                    }
                }
            }
        });

        // Add an event and remove data
        window.dataLayer.push({
            "event": "removed",
            "data": {
                "component": {
                    "image": {
                        "image5": undefined
                    }
                }
            }
        });

        // ====================================  Add event listener ======================================

        window.dataLayer.push({
            "on": "page loaded",
            "handler": function(event) {
                var pageUrl = window.dataLayer.get("page.id");
                console.log(pageUrl);
            }
        });

        window.dataLayer.push({
            "on": "click",
            "listen": "future",
            "selector": "path='component' && type='my-site/components/teaser'",
            "handler": function(event) {
                var clickTarget = event.id;
                console.log(clickTarget);
            }
        });

        window.dataLayer.push({
            "on": "change",
            "get": "user.userName",
            "listen": "once",
            "handler": function(userName) {
                console.log(userName);
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

        // Unregister event listener
        window.dataLayer.push({
            "off": "change",
            "get": "user.userName",
            "listen": "once",
            "handler": function(userName) {
                console.log(userName);
            }
        });

        // TODO: is it still needed?
        window.dataLayer.push({
            "type": "tab viewed",
            "eventData": {
                "prop1": "the component id",
                "prop2": "the component id",
                "prop3": "the component id"
            }
        });

    }

    populateDataLayerBefore();

    window.addEventListener("datalayer:ready", function() {
        console.log("data layer ready - let's populate some more events.");
        populateDataLayerAfter();
    });

    var event = new CustomEvent("datalayer:prepopulated");
    window.dispatchEvent(event);

})();
