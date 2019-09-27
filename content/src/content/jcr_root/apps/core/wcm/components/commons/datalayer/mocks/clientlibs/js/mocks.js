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

        // TODO
        // Add the page data (automatically triggers the datalayer:change event)
        window.dataLayer.push({
            "page": {
                "id": "/content/my-site/en/about-us",
                "pageName": "About Us",
                "siteLanguage": "en-us",
                "siteCountry": "US"
            }
        });

        // TODO
        // Add a component
        window.dataLayer.push({
            "component": {
                "tab": {
                    "tab2": {
                        "id": "/content/mysite/en/home/jcr:content/root/tab2",
                        "title": "the ocean",
                        "items": {}
                    }
                }
            }
        });

        // TODO
        // Remove data
        window.dataLayer.push({
            "component": {
                "image": {
                    "image5": undefined
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
        // Add an event with its data
        window.dataLayer.push({
            "event": "image viewed",
            "component": {
                "image": {
                    "image5": {
                        "id": "/content/mysite/en/home/jcr:content/root/image5",
                        "fileReference": "/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg"
                    }
                }
            }
        });

        // TODO
        // Add an event and remove data
        window.dataLayer.push({
            "event": "removed",
            "component": {
                "image": {
                    "image5": undefined
                }
            }
        });

        // TODO
        // Add event and reference the object by ID
        window.dataLayer.push({
            "event": "image viewed",
            "data": {
                "id": "/content/mysite/en/home/jcr:content/root/image5"
            }
        });

        // TODO
        // Add an event with its data
        window.dataLayer.push({
            "event": "image viewed",
            "data": {
                // any data (is not persisted in the state)
            },
            "component": {
                "image": {
                    "image5": {
                        "id": "/content/mysite/en/home/jcr:content/root/image5",
                        "fileReference": "/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg"
                    }
                }
            }
        });

        // ====================================  Add event listener ======================================

        // DONE
        // Register event listener: retrieving the object that was referenced by ID in the event
        window.dataLayer.push({
            "on": "image viewed",
            "handler": function(event) {
                var image = window.dataLayer.get(event.data.id); // TODO: implement dataLayer.get()
            }
        });

        // DONE
        // Register event listener listening on state change
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

        // TODO
        // Register event listener listening on state change
        window.dataLayer.push({
            "on": "datalayer:change",
            "scope": "future", // Possible values: past, future, all, once
            "selector": "user.userName",
            "handler": function(userName) {
                console.log(userName);
            }
        });

        // Register event listener listening on all events
        window.dataLayer.push({
            "on": "datalayer:event",
            "handler": function(event) {
                console.log(event);
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

    }

    populateDataLayerBefore();

    window.addEventListener("datalayer:ready", function() {
        console.log("data layer ready - let's populate some more events.");
        populateDataLayerAfter();
    });

    var event = new CustomEvent("datalayer:prepopulated");
    window.dispatchEvent(event);

})();
