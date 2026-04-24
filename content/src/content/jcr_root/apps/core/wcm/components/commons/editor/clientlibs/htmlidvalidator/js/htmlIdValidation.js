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
    // feature toggle enabling opening the cf in the new editor
    var CT_SANITIZE_ENCODE_PATH = "CT_SITES-33116";

    /**
     * Sanitizes and encodes a page path for safe URL construction.
     * Removes path traversal attempts, validates the path format, and encodes it.
     *
     * @param {String} pagePath - The page path to sanitize
     * @returns {String|null} The sanitized and encoded path, or null if invalid
     */
    function sanitizeAndEncodePath(pagePath) {
        // Remove any path traversal attempts and normalize the path
        pagePath = pagePath.replace(/\.\./g, "").replace(/\/+/g, "/");

        // Validate that the path starts with / and doesn't contain dangerous patterns
        if (!/^\//.test(pagePath) || /[<>"|*?]/.test(pagePath)) {
            console.warn("Invalid page path detected: " + pagePath);
            return null;
        }
        // Use encodeURIComponent to safely encode the path for URL construction
        return encodeURIComponent(pagePath).replace(/%2F/g, "/");
    }

    function authoringViewUrlForPath(resolvedPath) {
        if (!resolvedPath) {
            return null;
        }
        var u;
        try {
            u = new URL(String(resolvedPath), window.location.href);
        } catch (err) {
            return null;
        }
        if (u.origin !== window.location.origin) {
            return null;
        }
        u.search = "";
        u.hash = "";
        if (!/\.html$/i.test(u.pathname)) {
            u.pathname = u.pathname + ".html";
        }
        u.searchParams.set("wcmmode", "disabled");
        return u.toString();
    }

    function countElementsWithIdInHtml(markup, elementId) {
        if (elementId === null || elementId === undefined) {
            return 0;
        }
        var expected = String(elementId);
        if (expected.length === 0) {
            return 0;
        }
        var root = (new window.DOMParser()).parseFromString(markup, "text/html");
        var nodes = root.querySelectorAll("[id]");
        var count = 0;
        for (var n = 0; n < nodes.length; n++) {
            if (nodes[n].id === expected) {
                count++;
            }
        }
        return count;
    }

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
            var pathToResolve = pagePath;

            // Use sanitization function if toggle is enabled
            if (window.Granite && window.Granite.Toggles && window.Granite.Toggles.isEnabled(CT_SANITIZE_ENCODE_PATH)) {
                var encodedPagePath = sanitizeAndEncodePath(pagePath);
                if (!encodedPagePath) {
                    return;
                }
                pathToResolve = encodedPagePath;
            }

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
            var viewUrl = authoringViewUrlForPath(pathToResolve);
            if (!viewUrl) {
                return;
            }
            var idCount = 0;
            /* Check if same ID already exist on the page */
            $.ajax({
                type: "GET",
                url: viewUrl,
                dataType: "html",
                async: false,
                success: function(data) {
                    if (data) {
                        idCount = countElementsWithIdInHtml(data, currentVal);
                    }
                }
            });
            if (idCount > 0) {
                return "This ID already exist on the page, please enter a unique ID.";
            }
        }
    });
})($, window, document);
