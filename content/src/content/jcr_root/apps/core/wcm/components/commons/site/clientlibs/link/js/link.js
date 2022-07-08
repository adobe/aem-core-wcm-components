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

    var linkAccessibilityClass = "cmp-link__screen-reader-only";
    var selectors = {
        linkAccessibility: "." + linkAccessibilityClass,
        linkAccessibilityEnabled: "[data-cmp-link-accessibility-enabled]",
        linkAccessibilityText: "[data-cmp-link-accessibility-text]"
    };

    function getLinkAccessibilityText() {
        var linkAccessibilityEnabled = document.querySelectorAll(selectors.linkAccessibilityEnabled);
        if (!linkAccessibilityEnabled[0]) {
            return;
        }
        var linkAccessibilityTextElements = document.querySelectorAll(selectors.linkAccessibilityText);
        if (!linkAccessibilityTextElements[0]) {
            return;
        }
        return linkAccessibilityTextElements[0].dataset.cmpLinkAccessibilityText;
    }

    function onDocumentReady() {
        var linkAccessibilityText = getLinkAccessibilityText();
        if (linkAccessibilityText) {
            var linkAccessibilityHtml = "<span class='" + linkAccessibilityClass + "'>(" + linkAccessibilityText + ")</span>";
            document.querySelectorAll("a[target='_blank']").forEach(function(link) {
                if (!link.querySelector(selectors.linkAccessibility)) {
                    link.insertAdjacentHTML("beforeend",  linkAccessibilityHtml);
                }
            });
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}());
