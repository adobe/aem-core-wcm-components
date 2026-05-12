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

    var EDITOR_THUMBNAIL_DOM_EXCLUDED_TAGS = {
        script: true,
        iframe: true,
        object: true,
        embed: true,
        link: true,
        meta: true,
        base: true,
        form: true
    };

    function linkValueHasExcludedRepositoryPrefix(value) {
        if (value === undefined || value === null) {
            return false;
        }
        var t = String(value).trim().toLowerCase();
        return (
            t.indexOf("javascript:") === 0 ||
            t.indexOf("data:") === 0 ||
            t.indexOf("vbscript:") === 0
        );
    }

    function compactAuthoringDomAttributesOnElement(el) {
        if (!el || el.nodeType !== 1 || !el.attributes) {
            return;
        }
        var removeNames = [];
        var i;
        var a;
        var name;
        for (i = 0; i < el.attributes.length; i++) {
            a = el.attributes[i];
            name = a.name.toLowerCase();
            if (name.indexOf("on") === 0) {
                removeNames.push(a.name);
                continue;
            }
            if (name === "src" || name === "href" || name === "xlink:href") {
                if (linkValueHasExcludedRepositoryPrefix(a.value)) {
                    removeNames.push(a.name);
                }
            }
        }
        for (i = 0; i < removeNames.length; i++) {
            el.removeAttribute(removeNames[i]);
        }
    }

    function compactAuthoringDomAttributesOnSubtree(root) {
        if (!root) {
            return;
        }
        var list = [root];
        var nodes = root.querySelectorAll("*");
        for (var i = 0; i < nodes.length; i++) {
            list.push(nodes[i]);
        }
        for (var j = 0; j < list.length; j++) {
            compactAuthoringDomAttributesOnElement(list[j]);
        }
    }

    function pruneExcludedChildTagsUnderThumbnail(root) {
        if (!root || !root.getElementsByTagName) {
            return;
        }
        var toRemove = [];
        var all = root.getElementsByTagName("*");
        var k;
        var el;
        var tag;
        for (k = 0; k < all.length; k++) {
            el = all[k];
            tag = el.tagName ? el.tagName.toLowerCase() : "";
            if (EDITOR_THUMBNAIL_DOM_EXCLUDED_TAGS[tag]) {
                toRemove.push(el);
            }
        }
        for (k = 0; k < toRemove.length; k++) {
            el = toRemove[k];
            if (el.parentNode) {
                el.parentNode.removeChild(el);
            }
        }
    }

    function prepareThumbnailFragmentForEditor(root) {
        pruneExcludedChildTagsUnderThumbnail(root);
        compactAuthoringDomAttributesOnSubtree(root);
    }

    /**
     * Parses a page-image thumbnail HTML fragment and returns a clone for insertion into the active document after editor-side compaction.
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
        prepareThumbnailFragmentForEditor(thumb);
        return targetDocument.importNode(thumb, true);
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
