/*******************************************************************************
 * Copyright 2022 Adobe
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

    var dataLayerEnabled;
    var dataLayer;

    /**
     * Adds Click Event Listener to the main <div> of the Text Components
     */
    function addClickEventListenerToTextComponents() {
        var componentMainDivs = document.getElementsByClassName("cmp-text");

        for (var i = 0; i < componentMainDivs.length; i++) {
            componentMainDivs[i].addEventListener("click", addClickedLinkToDataLayer);
        }
    }

    /**
     * Adds clicked link contained by the Text Component to Data Layer
     *
     * @private
     * @param {Object} event The Click event
     */
    function addClickedLinkToDataLayer(event) {
        var element = event.currentTarget;
        var componentId = getDataLayerId(element);

        if (event.target.tagName === "A") {
            dataLayer.push({
                event: "cmp:click",
                eventInfo: {
                    path: "component." + componentId,
                    link: event.target.getAttribute("href")
                }
            });
        }

    }

    /**
     * Parses the dataLayer string and returns the ID
     *
     * @private
     * @param {HTMLElement} item, the Text item
     * @returns {String} dataLayerId or undefined
     */
    function getDataLayerId(item) {
        if (item) {
            if (item.dataset.cmpDataLayer) {
                return Object.keys(JSON.parse(item.dataset.cmpDataLayer))[0];
            } else {
                return item.id;
            }
        }
        return null;
    }

    function onDocumentReady() {
        dataLayerEnabled = document.body.hasAttribute("data-cmp-data-layer-enabled");
        dataLayer = (dataLayerEnabled) ? window.adobeDataLayer = window.adobeDataLayer || [] : undefined;

        if (dataLayerEnabled) {
            addClickEventListenerToTextComponents();
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
})();
