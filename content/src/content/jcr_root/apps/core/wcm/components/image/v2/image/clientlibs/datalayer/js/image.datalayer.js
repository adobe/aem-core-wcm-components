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

    function init(imageElt) {
        imageElt.addEventListener("click", addClickToDataLayer);
        var imageData = getImageData(imageElt);
        dataLayer.push({
            data: {
                component: {
                    image: imageData
                }
            }
        });
    }

    function addClickToDataLayer(event) {
        var imageElt = event.currentTarget;
        var imageData = getImageData(imageElt);
        dataLayer.push({
            event: 'image clicked',
            info: {
                path: imageData[Object.keys(imageData)[0]].path
            }
        });
    }

    function getImageData(imageElt) {
        var dataLayerJson = imageElt.getAttribute("data-cmp-image-data-layer");
        return JSON.parse(dataLayerJson);
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll('.cmp-image');
        for (var i = 0; i < elements.length; i++) {
            init(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());
