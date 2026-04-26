/*******************************************************************************
 * Copyright 2019 Adobe
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
(function($, ns, channel) {
    "use strict";

    var VCF_SELECTOR = ".cmp-contentfragment--vcf";
    var ATTR_VCF_URL = "data-cmp-contentfragment-vcf-url";
    var LOADING_ATTR = "data-vcf-loading";
    var ATTR_LOAD_FAILED = "data-cmp-contentfragment-vcf-load-failed";
    var ATTR_NO_PREVIEW = "data-cmp-contentfragment-vcf-no-preview";

    var _observer = null;

    function i18n(message) {
        if (typeof window !== "undefined" && window.Granite && window.Granite.I18n) {
            return window.Granite.I18n.get(message);
        }
        return message;
    }

    /**
     * Builds placeholder markup for the author preview; uses DOM so translated strings are escaped.
     */
    function buildVcfPlaceholderOuterHtml(modifier, role, surfaceCss, accentCss, titleMessage, detailMessage) {
        var root = document.createElement("div");
        root.className = "cmp-contentfragment__vcf-placeholder cmp-contentfragment__vcf-placeholder--" + modifier;
        root.setAttribute("role", role);
        root.style.cssText = [
            "box-sizing:border-box",
            "padding:20px 24px",
            surfaceCss,
            "color:#505050",
            "font-family:Adobe Clean,Helvetica,sans-serif",
            "font-size:14px",
            "line-height:1.45"
        ].join(";");

        var titleEl = document.createElement("strong");
        titleEl.style.cssText = [
            "display:block",
            "margin-bottom:8px",
            accentCss,
            "font-size:13px",
            "text-transform:uppercase",
            "letter-spacing:.06em"
        ].join(";");
        titleEl.textContent = i18n(titleMessage);

        var detailEl = document.createElement("span");
        detailEl.textContent = i18n(detailMessage);

        root.appendChild(titleEl);
        root.appendChild(detailEl);
        return root.outerHTML;
    }

    function buildLoadFailedPlaceholderHtml() {
        return buildVcfPlaceholderOuterHtml(
            "error",
            "alert",
            "border:2px dashed #d7373f;border-radius:8px;background:#fff4f4",
            "color:#c9252d",
            "Visualization could not be loaded",
            "Check that preview services are available and the fragment configuration is valid."
        );
    }

    function buildNoPreviewPlaceholderHtml() {
        return buildVcfPlaceholderOuterHtml(
            "unavailable",
            "status",
            "border:2px dashed #b0b0b0;border-radius:8px;background:#f5f5f5",
            "color:#6e6e6e",
            "Visualization preview unavailable",
            "A preview URL is not available for this content fragment in the editor."
        );
    }

    function applyNoPreviewPlaceholder(element) {
        if (!element || element.getAttribute(ATTR_NO_PREVIEW) === "true") {
            return;
        }
        if (element.getAttribute(ATTR_VCF_URL)) {
            return;
        }
        element.setAttribute(ATTR_NO_PREVIEW, "true");
        element.innerHTML = buildNoPreviewPlaceholderHtml();
    }

    function resolveVcfTargetElement(element, url) {
        var target = element;
        if (!target.ownerDocument || !target.ownerDocument.contains(target)) {
            var contentDoc = getContentFrameDocument();
            if (contentDoc) {
                var found = contentDoc.querySelector(
                    VCF_SELECTOR + "[" + ATTR_VCF_URL + "=\"" + url + "\"]"
                );
                if (found) {
                    target = found;
                }
            }
        }
        return target;
    }

    function loadVisualContentFragment(element) {
        var url = element.getAttribute(ATTR_VCF_URL);
        if (!url) {
            applyNoPreviewPlaceholder(element);
            return;
        }
        if (element.getAttribute(ATTR_LOAD_FAILED) === "true" || element.getAttribute(LOADING_ATTR)) {
            return;
        }
        element.setAttribute(LOADING_ATTR, "true");

        $.ajax({
            url: url,
            type: "GET",
            dataType: "html"
        }).then(function(html) {
            var target = resolveVcfTargetElement(element, url);
            if (target) {
                if (typeof html === "string" && html.trim().length === 0) {
                    target.setAttribute(ATTR_LOAD_FAILED, "true");
                    target.innerHTML = buildLoadFailedPlaceholderHtml();
                } else {
                    target.removeAttribute(ATTR_LOAD_FAILED);
                    target.removeAttribute(ATTR_NO_PREVIEW);
                    target.innerHTML = html;
                }
                target.removeAttribute(LOADING_ATTR);
            }
            element.removeAttribute(LOADING_ATTR);
        }, function() {
            var target = resolveVcfTargetElement(element, url);
            if (target) {
                target.setAttribute(ATTR_LOAD_FAILED, "true");
                target.innerHTML = buildLoadFailedPlaceholderHtml();
                target.removeAttribute(LOADING_ATTR);
            }
            element.removeAttribute(LOADING_ATTR);
        });
    }

    function renderVisualContentFragments(container) {
        var elements = container.querySelectorAll
            ? container.querySelectorAll(VCF_SELECTOR)
            : [];
        for (var i = 0; i < elements.length; i++) {
            loadVisualContentFragment(elements[i]);
        }
    }

    function getContentFrameDocument() {
        if (ns && ns.ContentFrame && ns.ContentFrame.contentWindow) {
            return ns.ContentFrame.contentWindow.document;
        }
        return null;
    }

    function renderContentFrameVCFs() {
        var doc = getContentFrameDocument();
        if (doc) {
            renderVisualContentFragments(doc);
        }
    }

    /**
     * Watches the content frame DOM for VCF elements being inserted (e.g. after
     * a component refresh triggered by dialog save) and renders them immediately.
     */
    function setupContentFrameObserver() {
        if (_observer) {
            _observer.disconnect();
        }
        var doc = getContentFrameDocument();
        if (!doc || !doc.body) {
            return;
        }
        _observer = new MutationObserver(function(mutations) {
            for (var i = 0; i < mutations.length; i++) {
                var added = mutations[i].addedNodes;
                for (var j = 0; j < added.length; j++) {
                    var node = added[j];
                    if (node.nodeType !== 1) {
                        continue;
                    }
                    if (node.matches && node.matches(VCF_SELECTOR)) {
                        loadVisualContentFragment(node);
                    } else if (node.querySelectorAll) {
                        var vcfs = node.querySelectorAll(VCF_SELECTOR);
                        for (var k = 0; k < vcfs.length; k++) {
                            loadVisualContentFragment(vcfs[k]);
                        }
                    }
                }
            }
        });
        _observer.observe(doc.body, { childList: true, subtree: true });
    }

    channel.on("cq-editor-loaded", function() {
        renderContentFrameVCFs();
        setupContentFrameObserver();
    });

    channel.on("foundation-contentloaded", function(e) {
        renderVisualContentFragments(e.target);
        renderContentFrameVCFs();
        setupContentFrameObserver();
    });

    channel.on("cq-contentfragment-edit cq-contentfragment-insert", function() {
        renderContentFrameVCFs();
        setupContentFrameObserver();
    });

})(jQuery, Granite.author, jQuery(document));
