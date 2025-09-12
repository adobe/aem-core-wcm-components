/***************************************************************************
 *  Copyright 2016 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 **************************************************************************/
(function($, window, document) {
    "use strict";
    /* Adapting window object to foundation-registry */
    var registry = $(window).adaptTo("foundation-registry");

    /* Validator for TextField - Validation for duplicate HTML ID authored through dialog */
    registry.register("foundation.validation.validator", {
        selector: "[data-validation=html-unique-id-validator]",
        validate: function(el) {
            var compPath = $(el.closest("form")).attr("action");

            if (!compPath) {
                return;
            }

            if (compPath.indexOf("://") > -1) {
                try {
                    var urlObj = new URL(compPath, window.location.origin);
                    if (urlObj.origin !== window.location.origin) {
                        console.log("Different origin detected: " + urlObj.origin + " from window origin " + window.location.origin + " generated from compPath: " + compPath);
                        return;
                    }
                } catch (e) {
                    return;
                }
            }

            var pagePath = compPath.split("/_jcr_content")[0];

            // Remove any path traversal attempts and normalize the path
            pagePath = pagePath.replace(/\.\./g, '').replace(/\/+/g, '/');

            // Validate that the path starts with / and doesn't contain dangerous patterns
            if (!/^\//.test(pagePath) || /[<>"|*?]/.test(pagePath)) {
                console.warn("Invalid page path detected: " + pagePath);
                return;
            }
            // Use encodeURIComponent to safely encode the path for URL construction
            var encodedPagePath = encodeURIComponent(pagePath).replace(/%2F/g, '/');

            var preConfiguredVal;
            /* Get the pre configured value if any */
            $.ajax({
                type: "GET",
                url: compPath + ".json",
                dataType: "json",
                async: false,
                success: function(data) {
                    if (data) {
                        preConfiguredVal = data.id;
                    }
                }
            });
            var element = $(el);
            var currentVal = element.val();
            /* Handle empty values or dialog re-submission */
            if (!currentVal || currentVal === preConfiguredVal) {
                return;
            }
            var url = encodedPagePath + ".html?wcmmode=disabled";
            var idCount = 0;
            /* Check if same ID already exist on the page */
            $.ajax({
                type: "GET",
                url: url,
                dataType: "html",
                async: false,
                success: function(data) {
                    var idList;
                    if (data) {
                        idList = $(data).find("[id='" + currentVal + "']");
                        if (idList) {
                            idCount = idList.length;
                        }
                    }
                }
            });
            if (idCount > 0) {
                return "This ID already exist on the page, please enter a unique ID.";
            }
        }
    });
})($, window, document);
