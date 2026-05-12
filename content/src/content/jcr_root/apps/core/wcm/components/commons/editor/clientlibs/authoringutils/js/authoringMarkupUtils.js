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
     * Parses an HTML document string into a Document instance.
     *
     * @param {String} markup - HTML document string from a datasource response
     * @returns {Document} parsed document
     */
    function parseMarkupDocument(markup) {
        return new window.DOMParser().parseFromString(markup, "text/html");
    }

    /**
     * Inner HTML of the first element child of the parsed document body (mirrors jQuery(html)[0].innerHTML for one root).
     *
     * @param {String} markup - HTML document string
     * @returns {String}
     */
    function innerHtmlFromFirstBodyChild(markup) {
        var doc = parseMarkupDocument(markup);
        var body = doc.body;
        if (!body || !body.firstElementChild) {
            return "";
        }
        return body.firstElementChild.innerHTML;
    }

    /**
     * Ensures a node can be inserted into targetDocument (uses importNode when the node comes from another document).
     *
     * @param {Node} node - element or fragment from parsing or another document
     * @param {Document} targetDocument - typically document
     * @returns {Node}
     */
    function adoptNodeForDocument(node, targetDocument) {
        if (!node || !targetDocument) {
            return node;
        }
        if (node.ownerDocument !== targetDocument) {
            return targetDocument.importNode(node, true);
        }
        return node;
    }

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.AuthoringEditorUtils = window.CQ.CoreComponents.AuthoringEditorUtils || {};
    window.CQ.CoreComponents.AuthoringEditorUtils.markup = {
        parseMarkupDocument: parseMarkupDocument,
        innerHtmlFromFirstBodyChild: innerHtmlFromFirstBodyChild,
        adoptNodeForDocument: adoptNodeForDocument
    };

})(window);
