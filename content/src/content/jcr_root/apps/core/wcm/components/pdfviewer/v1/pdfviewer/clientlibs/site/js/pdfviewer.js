/*******************************************************************************
 * Copyright 2020 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function() {
    "use strict";

    var NS = "cmp";
    var IS = "pdfviewer";
    var SDK_URL = "https://acrobatservices.adobe.com/view-sdk/viewer.js";
    var SDK_READY_EVENT = "adobe_dc_view_sdk.ready";

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]',
        sdkScript: 'script[src="' + SDK_URL + '"]'
    };

    function initSDK() {
        var sdkIncluded = document.querySelectorAll(selectors.sdkScript).length > 0;
        if (!window.adobe_dc_view_sdk && !sdkIncluded) {
            var dcv = document.createElement("script");
            dcv.type = "text/javascript";
            dcv.src = SDK_URL;
            document.body.appendChild(dcv);
        }
    }

    function previewPdf(component) {
        // prevents multiple initialization
        component.removeAttribute("data-" + NS + "-is");

        // add the view sdk to the page
        initSDK();

        // manage the preview
        if (component.dataset && component.id) {
            if (window.AdobeDC && window.AdobeDC.View) {
                dcView(component);
            } else {
                document.addEventListener(SDK_READY_EVENT, function() {
                    dcView(component);
                });
            }
        }
    }

    function dcView(component) {
        console.log("Initializing " + component.id);
        var element = document.getElementById(component.id + "-content");

        const observer = new IntersectionObserver(entries => {
            entries.forEach(entry => {
                if (entry.intersectionRatio > 0) {
                    // Element is in view
                    console.log(element.id + " is in view");
                    // Call mountViewer when div element is in view
                    mountViewer(component);
                } else {
                    // Element is out of view
                    console.log(element.id + " is out of view");
                    // Call unmountViewer when div element is out of view
                    unmountViewer(component);
                }
            });
        });

        observer.observe(element);
    }

    function mountViewer(component) {
        var iframe = document.getElementById(component.id + "-content").getElementsByTagName("iframe");
        if (iframe && iframe[0]) {
            console.log("Viewer already mounted " + component.id);
            return;
        }
        console.log("Mounting " + component.id);
        var adobeDCView = new window.AdobeDC.View({
            clientId: component.dataset.cmpClientId,
            divId: component.id + "-content",
            reportSuiteId: component.dataset.cmpReportSuiteId
        });
        adobeDCView.previewFile({
            content: { location: { url: component.dataset.cmpDocumentPath } },
            metaData: { fileName: component.dataset.cmpDocumentFileName }
        }, JSON.parse(component.dataset.cmpViewerConfigJson));
    }

    function unmountViewer(component) {
        var iframe = document.getElementById(component.id + "-content").getElementsByTagName("iframe");
        if (iframe && iframe[0]) {
            console.log("Unmounting " + component.id);
            iframe[0].parentNode.removeChild(iframe[0]);
        }
    }

    /**
     * Document ready handler and DOM mutation observers. Initializes Accordion components as necessary.
     *
     * @private
     */
    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            previewPdf(elements[i]);
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body = document.querySelector("body");
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                previewPdf(element);
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });

    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
}());
