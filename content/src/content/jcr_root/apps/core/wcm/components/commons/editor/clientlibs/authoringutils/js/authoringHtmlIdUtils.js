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
     * Returns the repository page path segment that precedes authored dialog form resource paths.
     * Values without a {@code /_jcr_content} or {@code /jcr:content} segment are not treated as component form actions.
     *
     * @param {String} compPath - form {@code action} (typically a component edit URL)
     * @returns {String|null} page path prefix, or null when the action does not follow the usual pattern
     */
    function extractAuthoringPagePathFromComponentFormAction(compPath) {
        if (compPath === undefined || compPath === null) {
            return null;
        }
        var s = String(compPath).trim();
        if (s.length === 0) {
            return null;
        }
        var idx = s.indexOf("/_jcr_content");
        if (idx === -1) {
            idx = s.indexOf("/jcr:content");
        }
        if (idx === -1) {
            return null;
        }
        return s.substring(0, idx);
    }

    /**
     * Builds a GET URL for the authored page HTML preview on the same origin (wcmmode=disabled).
     *
     * @param {String} resolvedPath - page path or encoded path string
     * @returns {String|null} resolved preview URL, or null when the path is invalid or not same-origin
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
     * @returns {Number} number of matching elements
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

    /**
     * Counts elements whose {@code id} equals the given string in fetched authoring HTML, after stripping
     * event-handler attributes ({@code on*}) from the parsed body subtree. Element nodes are not removed,
     * so semantics stay aligned with {@code countElementsWithIdInHtml} for ids on elements such as {@code form}.
     *
     * @param {String} markup - full HTML document string
     * @param {*} elementId - author-entered id value
     * @returns {Number} number of matching elements after handler attributes are cleared
     */
    function countElementsWithIdInAuthoringFetchedHtml(markup, elementId) {
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
        var doc;
        var Markup =
            window.CQ &&
            window.CQ.CoreComponents &&
            window.CQ.CoreComponents.AuthoringEditorUtils &&
            window.CQ.CoreComponents.AuthoringEditorUtils.markup;
        if (Markup && typeof Markup.parseAuthoringMarkupStripEventHandlersOnly === "function") {
            doc = Markup.parseAuthoringMarkupStripEventHandlersOnly(markup);
        } else {
            doc = new window.DOMParser().parseFromString(String(markup), "text/html");
        }
        var nodes = doc.querySelectorAll("[id]");
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
        countElementsWithIdInHtml: countElementsWithIdInHtml,
        extractAuthoringPagePathFromComponentFormAction: extractAuthoringPagePathFromComponentFormAction,
        countElementsWithIdInAuthoringFetchedHtml: countElementsWithIdInAuthoringFetchedHtml
    };

})(window);
