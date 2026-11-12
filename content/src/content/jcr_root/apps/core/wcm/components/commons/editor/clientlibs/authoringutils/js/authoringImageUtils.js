/*******************************************************************************
 * Copyright 2026 Adobe
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
(function(window) {
    "use strict";

    function getMarkupUtils() {
        return window.CQ && window.CQ.CoreComponents && window.CQ.CoreComponents.AuthoringEditorUtils && window.CQ.CoreComponents.AuthoringEditorUtils.markup;
    }

    /**
     * Formats a display string for Coral select item labels that use HTML content.
     *
     * @param {*} value - crop label or similar
     * @returns {String}
     */
    function formatPlainTextForMarkup(value) {
        if (value === undefined || value === null) {
            return "";
        }
        var div = window.document.createElement("div");
        div.textContent = String(value);
        return div.innerHTML;
    }

    /**
     * Whether a DAM metadata path segment can be used when composing same-origin image service URLs.
     *
     * @param {*} path - metadata value
     * @returns {Boolean}
     */
    function isDamScene7PathEligible(path) {
        if (path === undefined || path === null) {
            return false;
        }
        var str = String(path).trim();
        if (str.length === 0) {
            return false;
        }
        var markupApi = getMarkupUtils();
        if (markupApi && typeof markupApi.linkValueHasExcludedRepositoryPrefix === "function") {
            if (markupApi.linkValueHasExcludedRepositoryPrefix(str)) {
                return false;
            }
        } else {
            var schemeProbe = str.toLowerCase();
            if (
                schemeProbe.indexOf("javascript:") === 0 ||
                schemeProbe.indexOf("data:") === 0 ||
                schemeProbe.indexOf("vbscript:") === 0
            ) {
                return false;
            }
        }
        var lowerStr = str.toLowerCase();
        var decoded;
        try {
            decoded = decodeURIComponent(str.split("+").join(" "));
        } catch (e) {
            return false;
        }
        if (decoded.indexOf("..") !== -1) {
            return false;
        }
        var isAbsoluteHttp =
            lowerStr.indexOf("https://") === 0 ||
            lowerStr.indexOf("http://") === 0;
        if (isAbsoluteHttp) {
            try {
                return new URL(str).origin === window.location.origin;
            } catch (ex) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses a page-image thumbnail HTML fragment and returns a shell element for insertion (delegates markup allowlist rules).
     *
     * @param {String} markup - HTML response from the thumbnail endpoint
     * @param {Document} targetDocument - document receiving the clone
     * @returns {Element|null}
     */
    function importParsedPageImageThumbnail(markup, targetDocument) {
        if (markup === undefined || markup === null || !targetDocument) {
            return null;
        }
        var str = String(markup);
        var parsed = new window.DOMParser().parseFromString(str, "text/html");
        var thumb = parsed.querySelector(".cq-page-image-thumbnail");
        if (!thumb) {
            return null;
        }
        var markupApi = getMarkupUtils();
        if (!markupApi || typeof markupApi.buildPageImageThumbnailShellForEditor !== "function") {
            return null;
        }
        return markupApi.buildPageImageThumbnailShellForEditor(thumb, targetDocument);
    }

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.AuthoringEditorUtils = window.CQ.CoreComponents.AuthoringEditorUtils || {};
    window.CQ.CoreComponents.AuthoringEditorUtils.image = {
        formatPlainTextForMarkup: formatPlainTextForMarkup,
        isDamScene7PathEligible: isDamScene7PathEligible,
        importParsedPageImageThumbnail: importParsedPageImageThumbnail
    };

})(window);
