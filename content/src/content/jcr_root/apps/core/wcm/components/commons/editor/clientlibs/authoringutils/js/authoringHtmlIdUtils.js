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
     * Builds a GET URL for the authored page HTML preview on the same origin (wcmmode=disabled).
     *
     * @param {String} resolvedPath - page path or encoded path string
     * @returns {String|null}
     */
    function authoringPageViewUrl(resolvedPath) {
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

    /**
     * Counts elements whose id attribute equals the given string.
     *
     * @param {String} markup - full HTML document string
     * @param {*} elementId - author-entered id value
     * @returns {Number}
     */
    function countElementsWithIdInHtml(markup, elementId) {
        if (elementId === null || elementId === undefined) {
            return 0;
        }
        var expected = String(elementId);
        if (expected.length === 0) {
            return 0;
        }
        if (!markup) {
            return 0;
        }
        var root = new window.DOMParser().parseFromString(markup, "text/html");
        var nodes = root.querySelectorAll("[id]");
        var count = 0;
        for (var n = 0; n < nodes.length; n++) {
            if (nodes[n].id === expected) {
                count++;
            }
        }
        return count;
    }

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.AuthoringEditorUtils = window.CQ.CoreComponents.AuthoringEditorUtils || {};
    window.CQ.CoreComponents.AuthoringEditorUtils.htmlId = {
        authoringPageViewUrl: authoringPageViewUrl,
        countElementsWithIdInHtml: countElementsWithIdInHtml
    };

})(window);
