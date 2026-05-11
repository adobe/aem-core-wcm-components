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
     * Whether dam:scene7File (or equivalent path segment) is acceptable for building image service URLs on this host.
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
        var lowerStr = str.toLowerCase();
        if (
            lowerStr.indexOf("javascript:") === 0 ||
            lowerStr.indexOf("data:") === 0 ||
            lowerStr.indexOf("vbscript:") === 0
        ) {
            return false;
        }
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

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.AuthoringEditorUtils = window.CQ.CoreComponents.AuthoringEditorUtils || {};
    window.CQ.CoreComponents.AuthoringEditorUtils.image = {
        formatPlainTextForMarkup: formatPlainTextForMarkup,
        isDamScene7PathEligible: isDamScene7PathEligible
    };

})(window);
