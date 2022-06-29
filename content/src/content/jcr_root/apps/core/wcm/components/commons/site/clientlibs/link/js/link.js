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
(function(Granite) {
    "use strict";

    function onDocumentReady() {
        var linkAccessibilityClass = "cmp-link__screen-reader-only";
        var linkAccessibilitySelector = "." + linkAccessibilityClass;
        var linkAccessibilityHtml = "<span class='" + linkAccessibilityClass + "'>opens in a new tab</span>";
        document.querySelectorAll("a[target='_blank']").forEach(function(link) {
            if (!link.querySelector(linkAccessibilitySelector)) {
                link.insertAdjacentHTML("beforeend",  linkAccessibilityHtml);
            }
        });
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

}(Granite));
