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

    var dataLayer = window.dataLayer = window.dataLayer || [];

    function addComponentToDataLayer(component) {
        dataLayer.push({
            data: {
                component: getComponentObject(component)
            }
        });
    }

    function attachClickEventListener(element) {
        element.addEventListener("click", addClickToDataLayer);
    }

    function getComponentObject(element) {
        var parentData;
        var component = getComponentData(element);
        var componentID = Object.keys(component)[0];
        var parentElement = element.parentNode.closest("[data-cmp-data-layer], body");

        if (parentElement) {

            if (parentElement.tagName === "BODY") {
                parentData = dataLayer.find(function(element) {
                    return element.data !== undefined && element.data.page !== undefined;
                });

                if (parentData !== undefined) {
                    component[componentID].parentId = Object.keys(parentData.data.page)[0];
                }
            } else {
                parentData = getComponentData(parentElement);
                component[componentID].parentId = Object.keys(parentData)[0];
            }
        }

        return component;
    }

    function addClickToDataLayer(event) {
        var element = event.currentTarget;
        var componentId = getClickId(element);

        dataLayer.push({
            event: "cmp:click",
            info: {
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

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());
