/*******************************************************************************
 * Copyright 2016 Adobe
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

    function addClickToDataLayer(event) {
        var imageElt = event.currentTarget;
        var imageId = imageElt.getAttribute("data-id");

        dataLayer.push({
            event: "image clicked",
            info: {
                id: imageId
            }
        });
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll(".cmp-image");

        for (var i = 0; i < elements.length; i++) {
            elements[i].addEventListener("click", addClickToDataLayer);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
})();
