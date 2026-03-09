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
    var ATTR_FRAGMENT_ID = "data-cmp-contentfragment-id";
    var ATTR_VCF_TEMPLATE = "data-cmp-contentfragment-vcf-template";
    var ATTR_VARIATION = "data-cmp-contentfragment-variation";
    var VCF_API_BASE = "/adobe/experimental/previewtemplates-expires-20260301/sites/cf/fragments";
    var LOADING_ATTR = "data-vcf-loading";

    var _observer = null;

    function loadVisualContentFragment(element) {
        var fragmentId = element.getAttribute(ATTR_FRAGMENT_ID);
        var templateId = element.getAttribute(ATTR_VCF_TEMPLATE);
        if (!fragmentId || element.getAttribute(LOADING_ATTR)) {
            return;
        }
        element.setAttribute(LOADING_ATTR, "true");

        var url = VCF_API_BASE + "/" + fragmentId + "/preview";
        var params = [];
        if (templateId) {
            params.push("templateId=" + encodeURIComponent(templateId));
        }
        var variation = element.getAttribute(ATTR_VARIATION);
        if (variation && variation !== "master") {
            params.push("variation=" + encodeURIComponent(variation));
        }
        if (params.length > 0) {
            url += "?" + params.join("&");
        }

        $.ajax({
            url: url,
            type: "GET",
            dataType: "html"
        }).then(function(html) {
            var target = element;
            if (!target.ownerDocument || !target.ownerDocument.contains(target)) {
                var contentDoc = getContentFrameDocument();
                if (contentDoc) {
                    target = contentDoc.querySelector(
                        VCF_SELECTOR + "[" + ATTR_FRAGMENT_ID + "=\"" + fragmentId + "\"]"
                    );
                }
            }
            if (target) {
                target.innerHTML = html;
            }
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
