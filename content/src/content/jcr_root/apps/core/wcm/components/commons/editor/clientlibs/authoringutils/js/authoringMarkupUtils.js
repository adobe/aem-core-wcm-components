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

    var CORAL_FILEUPLOAD_CLASS_TOKENS = {
        "cq-page-image-thumbnail": true,
        "coral-Form-field": true,
        "cq-FileUpload": true,
        "_coral-FileUpload": true,
        "is-filled": true
    };

    var FILEUPLOAD_BUTTON_CLASS_TOKENS = {
        "cq-FileUpload-edit": true,
        "cq-FileUpload-clear": true,
        "cq-FileUpload-picker": true,
        "_coral-Button": true,
        "_coral-Button--primary": true,
        "_coral-Button--quiet": true
    };

    var THUMBNAIL_DATA_ATTRS = [
        "data-thumbnail-current-page-path",
        "data-thumbnail-component-path",
        "data-thumbnail-config-path"
    ];

    /** Element names removed entirely when normalizing parsed authoring datasource markup. */
    var AUTHORING_MARKUP_STRIPPED_ELEMENT_TAGS = {
        SCRIPT: true,
        IFRAME: true,
        OBJECT: true,
        EMBED: true,
        STYLE: true,
        LINK: true,
        META: true,
        BASE: true,
        FORM: true
    };

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

    /**
     * Drops ASCII C0 controls, DEL, and whitespace so scheme prefix checks cannot be bypassed with
     * characters the URL layer may normalise away (e.g. TAB inside {@code javascript:}).
     *
     * @param {String} str - raw attribute value
     * @returns {String} characters kept for scheme prefix checks
     */
    function stripAsciiControlsAndWhitespaceForSchemeCheck(str) {
        var out = "";
        var i;
        var ch;
        var c;
        for (i = 0; i < str.length; i++) {
            ch = str.charAt(i);
            c = str.charCodeAt(i);
            if (c <= 31 || c === 127) {
                continue;
            }
            if (/\s/.test(ch)) {
                continue;
            }
            out += ch;
        }
        return out;
    }

    /**
     * Whether a link-like attribute value uses a non-http(s) scheme prefix that authoring dialogs do not treat as repository paths.
     * Leading C0 control characters, DEL, and whitespace are stripped before the check so values cannot hide schemes from prefix matching.
     *
     * @param {*} value - attribute value (typically after DOM parsing, so entities are decoded)
     * @returns {Boolean} true when the normalised value starts with javascript, data, or vbscript
     */
    function linkValueHasExcludedRepositoryPrefix(value) {
        if (value === undefined || value === null) {
            return false;
        }
        var t = stripAsciiControlsAndWhitespaceForSchemeCheck(String(value)).toLowerCase();
        return (
            t.indexOf("javascript:") === 0 ||
            t.indexOf("data:") === 0 ||
            t.indexOf("vbscript:") === 0
        );
    }

    /**
     * Normalizes parsed authoring markup under a root element: drops disallowed subtrees (including
     * active content, document-influencing, and styling hooks) and clears event-handler and disallowed URL schemes on link-like attributes.
     *
     * @param {Element} rootElement - parsed subtree root (typically {@code document.body})
     */
    function sanitizeAuthoringMarkupSubtree(rootElement) {
        if (!rootElement || rootElement.nodeType !== 1) {
            return;
        }
        var all = rootElement.querySelectorAll("*");
        var list = [];
        var i;
        for (i = 0; i < all.length; i++) {
            list.push(all[i]);
        }
        list.push(rootElement);
        var removeEls = [];
        for (i = 0; i < list.length; i++) {
            var el = list[i];
            if (el.nodeType !== 1) {
                continue;
            }
            var tag = el.tagName;
            if (AUTHORING_MARKUP_STRIPPED_ELEMENT_TAGS[tag]) {
                removeEls.push(el);
                continue;
            }
            var attrs = el.attributes;
            var names = [];
            var j;
            for (j = 0; attrs && j < attrs.length; j++) {
                names.push(attrs[j].name);
            }
            for (j = 0; j < names.length; j++) {
                var name = names[j];
                var val = el.getAttribute(name);
                var nl = name.toLowerCase();
                if (/^on/i.test(name)) {
                    el.removeAttribute(name);
                    continue;
                }
                if (
                    (
                        nl === "href" ||
                        nl === "src" ||
                        nl === "action" ||
                        nl === "formaction" ||
                        nl === "xlink:href"
                    ) &&
                    linkValueHasExcludedRepositoryPrefix(val)
                ) {
                    el.removeAttribute(name);
                }
            }
        }
        for (i = 0; i < removeEls.length; i++) {
            var node = removeEls[i];
            if (node.parentNode) {
                node.parentNode.removeChild(node);
            }
        }
    }

    /**
     * Parses datasource HTML and returns the inner markup of the first body child, after subtree
     * normalization is applied to the parsed document body.
     *
     * @param {String} markup - HTML document string from a datasource response
     * @returns {String} normalized inner markup (empty string when the parsed body has no element child)
     */
    function sanitizeAuthoringEditorResponseMarkup(markup) {
        var doc = parseMarkupDocument(String(markup == null ? "" : markup));
        if (doc.body) {
            sanitizeAuthoringMarkupSubtree(doc.body);
        }
        var body = doc.body;
        if (!body || !body.firstElementChild) {
            return "";
        }
        return body.firstElementChild.innerHTML;
    }

    /**
     * Parses datasource HTML into a document whose body subtree is normalised the same way as for
     * {@code sanitizeAuthoringEditorResponseMarkup}, without collapsing to the first child inner string.
     *
     * @param {String} markup - HTML document string from a datasource response
     * @returns {Document} parsed document with a normalised body subtree
     */
    function parseAndNormalizeAuthoringDatasourceMarkup(markup) {
        var doc = parseMarkupDocument(String(markup == null ? "" : markup));
        if (doc.body) {
            sanitizeAuthoringMarkupSubtree(doc.body);
        }
        return doc;
    }

    function filterClassAttribute(raw, allowedTokens) {
        if (!raw || typeof raw !== "string") {
            return "";
        }
        var out = [];
        var parts = raw.trim().split(/\s+/);
        var i;
        for (i = 0; i < parts.length; i++) {
            if (parts[i] && allowedTokens[parts[i]]) {
                out.push(parts[i]);
            }
        }
        return out.join(" ");
    }

    function copyPlainDataAttribute(source, name, dest) {
        if (!source.hasAttribute(name)) {
            return;
        }
        var v = source.getAttribute(name);
        if (v === null || typeof v !== "string") {
            return;
        }
        if (/[<>"]/.test(v)) {
            return;
        }
        dest.setAttribute(name, v);
    }

    /**
     * Builds a coral-fileupload page-image thumbnail shell in targetDocument, copying only the structure and attributes
     * supported by the core page image thumbnail HTL (allowlist). Unknown nodes and attributes from the parsed response are ignored.
     *
     * @param {Element} parsedSourceRoot - element with class cq-page-image-thumbnail (expected coral-fileupload from authoring)
     * @param {Document} targetDocument - document that will own the returned subtree
     * @returns {Element|null} coral-fileupload root or null when the source cannot be mapped
     */
    function buildPageImageThumbnailShellForEditor(parsedSourceRoot, targetDocument) {
        if (!parsedSourceRoot || !targetDocument || parsedSourceRoot.nodeType !== 1) {
            return null;
        }
        if (parsedSourceRoot.tagName.toLowerCase() !== "coral-fileupload") {
            return null;
        }
        if (!parsedSourceRoot.classList || !parsedSourceRoot.classList.contains("cq-page-image-thumbnail")) {
            return null;
        }

        var root = targetDocument.createElement("coral-fileupload");
        var cls = filterClassAttribute(parsedSourceRoot.getAttribute("class"), CORAL_FILEUPLOAD_CLASS_TOKENS);
        if (cls) {
            root.setAttribute("class", cls);
        }
        var a;
        for (a = 0; a < THUMBNAIL_DATA_ATTRS.length; a++) {
            copyPlainDataAttribute(parsedSourceRoot, THUMBNAIL_DATA_ATTRS[a], root);
        }

        var wrap = targetDocument.createElement("div");
        wrap.setAttribute("class", "cq-FileUpload-thumbnail");
        var inner = targetDocument.createElement("div");
        inner.setAttribute("class", "cq-FileUpload-thumbnail-img");

        var srcImg = parsedSourceRoot.querySelector("img.cq-page-image-thumbnail__image");
        var srcVal = srcImg && srcImg.getAttribute("src");
        if (srcVal && !linkValueHasExcludedRepositoryPrefix(srcVal) && !/[<>"]/.test(srcVal)) {
            var img = targetDocument.createElement("img");
            img.setAttribute("class", "cq-page-image-thumbnail__image");
            img.setAttribute("src", srcVal);
            var altV = srcImg.getAttribute("alt");
            if (altV !== null && !/[<>"]/.test(altV)) {
                img.setAttribute("alt", altV);
            }
            inner.appendChild(img);
        } else {
            var icon = targetDocument.createElement("coral-icon");
            icon.setAttribute("icon", "image");
            icon.setAttribute("class", "_coral-Icon _coral-Icon--sizeS");
            icon.setAttribute("role", "img");
            icon.setAttribute("size", "S");
            inner.appendChild(icon);
        }

        wrap.appendChild(inner);

        var sourceThumb = parsedSourceRoot.querySelector(":scope > .cq-FileUpload-thumbnail");
        if (!sourceThumb) {
            sourceThumb = parsedSourceRoot.querySelector(".cq-FileUpload-thumbnail");
        }
        if (sourceThumb) {
            var sourceButtons = sourceThumb.querySelectorAll("button[type=\"button\"]");
            var b;
            for (b = 0; b < sourceButtons.length; b++) {
                var sb = sourceButtons[b];
                if (
                    !sb.classList.contains("cq-FileUpload-edit") &&
                    !sb.classList.contains("cq-FileUpload-clear") &&
                    !sb.classList.contains("cq-FileUpload-picker")
                ) {
                    continue;
                }
                var btn = targetDocument.createElement("button");
                btn.setAttribute("type", "button");
                btn.setAttribute("disabled", "");
                var bcls = filterClassAttribute(sb.getAttribute("class"), FILEUPLOAD_BUTTON_CLASS_TOKENS);
                if (bcls) {
                    btn.setAttribute("class", bcls);
                }
                if (sb.classList.contains("cq-FileUpload-edit")) {
                    var ref = sb.getAttribute("data-cq-fileupload-filereference");
                    if (ref && typeof ref === "string" && !/[<>"]/.test(ref)) {
                        btn.setAttribute("data-cq-fileupload-filereference", ref);
                    }
                }
                var srcLbl = sb.querySelector("coral-button-label");
                var cbl = targetDocument.createElement("coral-button-label");
                cbl.setAttribute("class", "_coral-Button-label");
                if (srcLbl && srcLbl.textContent) {
                    cbl.textContent = srcLbl.textContent;
                }
                btn.appendChild(cbl);
                wrap.appendChild(btn);
            }
        }

        root.appendChild(wrap);
        return root;
    }

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.AuthoringEditorUtils = window.CQ.CoreComponents.AuthoringEditorUtils || {};
    window.CQ.CoreComponents.AuthoringEditorUtils.markup = {
        parseMarkupDocument: parseMarkupDocument,
        innerHtmlFromFirstBodyChild: innerHtmlFromFirstBodyChild,
        adoptNodeForDocument: adoptNodeForDocument,
        linkValueHasExcludedRepositoryPrefix: linkValueHasExcludedRepositoryPrefix,
        buildPageImageThumbnailShellForEditor: buildPageImageThumbnailShellForEditor,
        sanitizeAuthoringEditorResponseMarkup: sanitizeAuthoringEditorResponseMarkup,
        parseAndNormalizeAuthoringDatasourceMarkup: parseAndNormalizeAuthoringDatasourceMarkup
    };

})(window);
