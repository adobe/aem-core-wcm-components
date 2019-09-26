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
            "event": "carousel clicked",
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
            "event": "tab viewed",
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
            "event": "page loaded",
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
                console.log("event listener triggered on: ", event.event);
            }
        });

    }

    function populateDataLayerAfter() {
        window.dataLayer.push({
            "event": "page updated",
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
            "event": "component updated",
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
            "event": "removed",
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
                console.log("event listener triggered on: ", event.event);
            }
        });

        window.dataLayer.push({
            "event": "removed",
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

    // =========================================================================================

    //                                     BASIC USE CASES

    // =========================================================================================

    function basicUseCases() {

        // ====================================  Add data ======================================

        // DONE
        // Add the page data (automatically triggers the datalayer:change event)
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

        // DONE
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

        // DONE
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


        // DONE
        // Add an event (without data)
        window.dataLayer.push({
            "event": "page loaded"
        });

        // TODO
        // Add an event with a reference to the data
        window.dataLayer.push({
            "event": "click",
            "id": ["component", "/content/my-site/en/about-us/jcr:content/root/responsivegrid/teaser"]
        });

        // DONE
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

        // DONE
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

        // DONE
        window.dataLayer.push({
            "on": "page loaded",
            "handler": function(event) {
                var pageUrl = window.dataLayer.get("page.id");
                console.log(pageUrl);
            }
        });

        // TODO
        window.dataLayer.push({
            "on": "click",
            "listen": "future", // could also be called 'scope'. Possible values: past, future, all, once
            "selector": "path='component' && type='my-site/components/teaser'",
            "handler": function(event) {
                var clickTarget = event.id;
                console.log(clickTarget);
            }
        });

        // TODO
        window.dataLayer.push({
            "on": "change",
            "get": "user.userName",
            "listen": "once",
            "handler": function(userName) {
                console.log(userName);
            }
        });

        // DONE
        window.dataLayer.push({
            "on": "datalayer:change",
            "handler": function(event) {
                // the event name
                console.log(event.event);
                // the data that changed
                console.log(event.data);
                // the state
                console.log(window.dataLayer.state);
            }
        });

        // DONE
        // Unregister event listener
        window.dataLayer.push({
            "off": "datalayer:change",
            "handler": function(event) {
                // the event name
                console.log(event.event);
                // the data that changed
                console.log(event.data);
                // the state
                console.log(window.dataLayer.state);
            }
        });

        // TODO: is it still needed?
        window.dataLayer.push({
            "event": "tab viewed",
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
