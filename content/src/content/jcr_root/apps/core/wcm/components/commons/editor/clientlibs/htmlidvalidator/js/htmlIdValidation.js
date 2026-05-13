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
    var CT_SITES_41942 = "CT_SITES-41942";

    /**
     * Granite feature toggle CT_SITES-41942 controls how the page HTML preview URL is derived from the dialog
     * form action and whether fetched markup is normalised before counting duplicate ids. When Granite reports
     * the toggle as disabled, behaviour matches earlier validators.
     *
     * @returns {Boolean} true when preview URL resolution and fetched markup handling follow the updated rules
     */
    function isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled() {
        if (!window.Granite || !window.Granite.Toggles || typeof window.Granite.Toggles.isEnabled !== "function") {
            return true;
        }
        return window.Granite.Toggles.isEnabled(CT_SITES_41942) !== false;
    }

    /**
     * Returns the commons html id helper namespace object.
     *
     * @returns {Object|null} null when commons helpers are not available
     */
    function getAuthoringHtmlIdUtils() {
        return window.CQ &&
            window.CQ.CoreComponents &&
            window.CQ.CoreComponents.AuthoringEditorUtils &&
            window.CQ.CoreComponents.AuthoringEditorUtils.htmlId
            ? window.CQ.CoreComponents.AuthoringEditorUtils.htmlId
            : null;
    }

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

    var htmlUniqueIdFieldValidator = {
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

            var htmlIdUtils = getAuthoringHtmlIdUtils();
            var pagePath;
            if (
                isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled() &&
                htmlIdUtils &&
                typeof htmlIdUtils.extractAuthoringPagePathFromComponentFormAction === "function"
            ) {
                pagePath = htmlIdUtils.extractAuthoringPagePathFromComponentFormAction(compPath);
                if (!pagePath) {
                    return;
                }
            } else {
                pagePath = compPath.split("/_jcr_content")[0];
            }

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
            var viewUrl = null;
            if (htmlIdUtils && typeof htmlIdUtils.authoringPageViewUrl === "function") {
                viewUrl = htmlIdUtils.authoringPageViewUrl(pathToResolve);
            }
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
                    if (data && htmlIdUtils) {
                        if (
                            isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled() &&
                            typeof htmlIdUtils.countElementsWithIdInAuthoringFetchedHtml === "function"
                        ) {
                            idCount = htmlIdUtils.countElementsWithIdInAuthoringFetchedHtml(data, currentVal);
                        } else if (typeof htmlIdUtils.countElementsWithIdInHtml === "function") {
                            idCount = htmlIdUtils.countElementsWithIdInHtml(data, currentVal);
                        }
                    }
                }
            });
            if (idCount > 0) {
                return "This ID already exist on the page, please enter a unique ID.";
            }
        }
    };

    registry.register("foundation.validation.validator", htmlUniqueIdFieldValidator);

    var htmlIdValidatorTestApiHost = typeof globalThis !== "undefined" ? globalThis : window;
    /* Karma (mocks.js) sets __HTML_ID_VALIDATOR_EDITOR_TEST_API on the global object; AEM runtime leaves it undefined. */
    if (htmlIdValidatorTestApiHost.__HTML_ID_VALIDATOR_EDITOR_TEST_API) {
        htmlIdValidatorTestApiHost.__HTML_ID_VALIDATOR_EDITOR_TEST_API.isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled =
            isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled;
        htmlIdValidatorTestApiHost.__HTML_ID_VALIDATOR_EDITOR_TEST_API.getAuthoringHtmlIdUtils = getAuthoringHtmlIdUtils;
        htmlIdValidatorTestApiHost.__HTML_ID_VALIDATOR_EDITOR_TEST_API.getHtmlUniqueIdFieldValidator = function() {
            return htmlUniqueIdFieldValidator;
        };
    }
})($, window, document);
