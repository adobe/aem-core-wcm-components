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

    var dataLayerEnabled = document.body.hasAttribute("data-cmp-data-layer-enabled");
    var dataLayer = (dataLayerEnabled)? window.adobeDataLayer = window.adobeDataLayer || [] : undefined;

    function addComponentToDataLayer(component) {
        dataLayer.push({
            component: getComponentObject(component)
        });
    }

    function attachClickEventListener(element) {
        element.addEventListener("click", addClickToDataLayer);
    }

    function getComponentObject(element) {
        var component = getComponentData(element);
        var componentID = Object.keys(component)[0];
        var parentElement = element.parentNode.closest("[data-cmp-data-layer], body");

        if (parentElement) {
            component[componentID].parentId = parentElement.id;
        }

        return component;
    }

    function addClickToDataLayer(event) {
        var element = event.currentTarget;
        var componentId = getClickId(element);

        dataLayer.push({
            event: "cmp:click",
            eventInfo: {
                path: "component." + componentId
            }
        });
    }

    function getComponentData(element) {
        var dataLayerJson = element.dataset.cmpDataLayer;
        return JSON.parse(dataLayerJson);
    }

    function getClickId(element) {
        if (element.dataset.cmpDataLayer) {
            return Object.keys(JSON.parse(element.dataset.cmpDataLayer))[0];
        }

        var componentElement = element.closest("[data-cmp-data-layer]");

        return Object.keys(JSON.parse(componentElement.dataset.cmpDataLayer))[0];
    }

    function onDocumentReady() {
        var components = document.querySelectorAll("[data-cmp-data-layer]");
        var clickableElements = document.querySelectorAll("[data-cmp-clickable]");

        components.forEach(function(component) {
            addComponentToDataLayer(component);
        });

        clickableElements.forEach(function(element) {
            attachClickEventListener(element);
        });

        dataLayer.push({
            event: "cmp:loaded"
        });
    }

    if (dataLayerEnabled) {
        if (document.readyState !== "loading") {
            onDocumentReady();
        } else {
            document.addEventListener("DOMContentLoaded", onDocumentReady);
        }
    }

}());
