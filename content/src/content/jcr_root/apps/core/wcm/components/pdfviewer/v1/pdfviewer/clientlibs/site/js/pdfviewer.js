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

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]'
    };

    function initSDK() {
        if (!window.adobe_dc_view_sdk) {
            var dcv = document.createElement("script");
            dcv.src = "https://documentcloud.adobe.com/view-sdk/main.js";
            document.body.appendChild(dcv);
        }
    }

    function previewPdf(component) {
        initSDK();
        // prevents multiple initialization
        component.removeAttribute("data-" + NS + "-is");

        // manage the preview
        if (component.dataset && component.id) {
            document.addEventListener("adobe_dc_view_sdk.ready", function(){
                var adobeDCView = new AdobeDC.View({
                    clientId: component.dataset.cmpClientId,
                    divId: component.id + '-content',
                    reportSuiteId: component.dataset.cmpReportSuiteId
                });
                adobeDCView.previewFile({
                    content:{location: {url: component.dataset.cmpDocumentPath}},
                    metaData:{fileName: component.dataset.cmpDocumentFileName}
                }, component.dataset.cmpViewerConfigJson);
            });
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
