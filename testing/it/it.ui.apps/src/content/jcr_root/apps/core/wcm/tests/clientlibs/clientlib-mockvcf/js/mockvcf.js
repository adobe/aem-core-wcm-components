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

/**
 * Local-development helper for Content Fragment Visualization.
 *
 * Redirects VCF API requests to the local mock Sling servlet at
 * {@code /bin/mock/cfvisualization}.
 *
 * This clientlib is part of the {@code testing/it/it.ui.apps} package, which
 * is deployed only to local AEM SDK instances — never to AEM Cloud Service.
 *
 * No API URL patterns are hardcoded. The mock reads server-generated URLs
 * from DOM data attributes — the Java model is the single source of truth.
 *
 * Strategy:
 *   1. DOM rewriting — rewrites {@code data-cmp-contentfragment-vcf-url}
 *      attributes for the site clientlib (vcf.js) on View as Published pages.
 *   2. jQuery ajaxPrefilter — intercepts editor AJAX requests from
 *      vcfRenderer.js and editDialog.js by matching known fragment IDs and
 *      the templates API base collected from the content frame DOM.
 *   3. State is collected eagerly during editor events and used lazily in
 *      the ajaxPrefilter, avoiding unreliable content frame access inside
 *      the prefilter callback.
 */
(function() {
    "use strict";

    var MOCK_BASE = "/bin/mock/cfvisualization";
    var VCF_URL_ATTR = "data-cmp-contentfragment-vcf-url";
    var VCF_TEMPLATES_API_ATTR = "data-cmp-contentfragment-vcf-templates-api";
    var VCF_FRAGMENT_ID_ATTR = "data-cmp-contentfragment-id";
    var VCF_SELECTOR = "[" + VCF_URL_ATTR + "]";

    // --- Shared state populated from the content frame DOM ---
    var knownFragmentIds = {};
    var templatesApiBase = null;

    /**
     * Builds a mock preview URL from a fragment ID and original query string.
     */
    function buildMockPreviewUrl(originalUrl, fragmentId) {
        var queryIdx = originalUrl.indexOf("?");
        var queryStr = queryIdx >= 0 ? originalUrl.substring(queryIdx + 1) : "";
        var mockUrl = MOCK_BASE + ".vcf.html?fragmentId=" + encodeURIComponent(fragmentId);
        if (queryStr) {
            mockUrl += "&" + queryStr;
        }
        return mockUrl;
    }

    /**
     * Builds a mock templates URL from the original URL.
     * Extracts the modelId from the path suffix after templatesApiBase.
     */
    function buildMockTemplatesUrl(originalUrl) {
        if (!templatesApiBase) {
            return null;
        }
        var suffix = originalUrl.substring(templatesApiBase.length).replace(/^\//, "");
        var modelId = suffix.split("/")[0];
        if (!modelId) {
            return null;
        }
        var queryIdx = originalUrl.indexOf("?");
        var queryStr = queryIdx >= 0 ? originalUrl.substring(queryIdx + 1) : "";
        var mockUrl = MOCK_BASE + ".templates.json?modelId=" + encodeURIComponent(modelId);
        if (queryStr) {
            mockUrl += "&" + queryStr;
        }
        return mockUrl;
    }

    /**
     * Collects fragment IDs and templates API base from VCF elements,
     * and rewrites their vcf-url attributes to mock URLs.
     */
    function collectAndRewrite(doc) {
        // collect templates API base from any element that has the attribute
        if (!templatesApiBase && doc.querySelectorAll) {
            var apiEls = doc.querySelectorAll("[" + VCF_TEMPLATES_API_ATTR + "]");
            for (var k = 0; k < apiEls.length; k++) {
                var api = apiEls[k].getAttribute(VCF_TEMPLATES_API_ATTR);
                if (api) {
                    templatesApiBase = api;
                    break;
                }
            }
        }

        // collect fragment IDs and rewrite VCF URLs
        var elements = doc.querySelectorAll ? doc.querySelectorAll(VCF_SELECTOR) : [];
        for (var i = 0; i < elements.length; i++) {
            var el = elements[i];
            var fragmentId = el.getAttribute(VCF_FRAGMENT_ID_ATTR);
            var url = el.getAttribute(VCF_URL_ATTR);

            if (fragmentId) {
                knownFragmentIds[fragmentId] = true;
            }

            if (url && fragmentId && url.indexOf(MOCK_BASE) !== 0) {
                el.setAttribute(VCF_URL_ATTR, buildMockPreviewUrl(url, fragmentId));
            }
        }
    }

    function observeAndRewrite(doc) {
        collectAndRewrite(doc);
        var obs = new MutationObserver(function(mutations) {
            for (var i = 0; i < mutations.length; i++) {
                var added = mutations[i].addedNodes;
                for (var j = 0; j < added.length; j++) {
                    var node = added[j];
                    if (node.nodeType !== 1) {
                        continue;
                    }
                    if (node.hasAttribute && node.hasAttribute(VCF_URL_ATTR)) {
                        var fid = node.getAttribute(VCF_FRAGMENT_ID_ATTR);
                        var url = node.getAttribute(VCF_URL_ATTR);
                        if (fid) {
                            knownFragmentIds[fid] = true;
                        }
                        if (!templatesApiBase) {
                            var api = node.getAttribute(VCF_TEMPLATES_API_ATTR);
                            if (api) {
                                templatesApiBase = api;
                            }
                        }
                        if (url && fid && url.indexOf(MOCK_BASE) !== 0) {
                            node.setAttribute(VCF_URL_ATTR, buildMockPreviewUrl(url, fid));
                        }
                    }
                    if (node.querySelectorAll) {
                        collectAndRewrite(node);
                    }
                }
            }
        });
        obs.observe(doc.documentElement, { childList: true, subtree: true });
    }

    // --- Site-side: rewrite in the current document (View as Published) ---
    if (document.readyState !== "loading") {
        observeAndRewrite(document);
    } else {
        document.addEventListener("DOMContentLoaded", function() {
            observeAndRewrite(document);
        });
    }

    // --- Editor-side: collect state, rewrite content frame, and install
    //     ajaxPrefilter on the content frame's jQuery ---
    function handleContentFrame() {
        try {
            var contentWindow = Granite.author.ContentFrame.contentWindow;
            if (contentWindow && contentWindow.document) {
                observeAndRewrite(contentWindow.document);
                installContentFramePrefilter(contentWindow);
            }
        } catch (e) {
            // not in editor context
        }
    }

    var contentFramePrefilterInstalled = false;

    function installContentFramePrefilter(contentWindow) {
        if (contentFramePrefilterInstalled) {
            return;
        }
        var contentJQuery = contentWindow.jQuery || contentWindow.Granite && contentWindow.Granite.$;
        if (!contentJQuery) {
            return;
        }
        contentJQuery.ajaxPrefilter(function(options) {
            if (!options.url) {
                return;
            }
            ensureStateCollected();
            for (var fid in knownFragmentIds) {
                if (knownFragmentIds.hasOwnProperty(fid) && options.url.indexOf(fid) !== -1) {
                    options.url = buildMockPreviewUrl(options.url, fid);
                    return;
                }
            }
            if (templatesApiBase && options.url.indexOf(templatesApiBase) === 0) {
                var mockUrl = buildMockTemplatesUrl(options.url);
                if (mockUrl) {
                    options.url = mockUrl;
                }
            }
        });
        contentFramePrefilterInstalled = true;
    }

    if (typeof Granite !== "undefined" && Granite.author) {
        var channel = jQuery(document);
        channel.on("cq-editor-loaded", handleContentFrame);
        channel.on("foundation-contentloaded", handleContentFrame);

        // Install the content frame prefilter as early as possible —
        // the iframe's load event fires before cq-editor-loaded, giving
        // us a chance to register the ajaxPrefilter before vcfRenderer.js
        // makes its AJAX calls.
        if (Granite.author.ContentFrame && Granite.author.ContentFrame.iframe) {
            Granite.author.ContentFrame.iframe.on("load", function() {
                try {
                    var cw = Granite.author.ContentFrame.contentWindow;
                    if (cw && cw.document) {
                        collectAndRewrite(cw.document);
                        installContentFramePrefilter(cw);
                    }
                } catch (e) {
                    // cross-origin or not ready
                }
            });
        }
    }

    /**
     * Attempts to rewrite a VCF API URL to a mock URL.
     * Returns the rewritten URL, or the original if no rewrite was needed.
     */
    function rewriteUrlIfNeeded(url) {
        if (typeof url !== "string") {
            return url;
        }
        // Check templates API first (more specific match)
        if (templatesApiBase && url.indexOf(templatesApiBase) === 0) {
            var mockUrl = buildMockTemplatesUrl(url);
            if (mockUrl) {
                return mockUrl;
            }
        }
        // Check preview URLs by fragment ID
        if (url.indexOf(MOCK_BASE) === -1) {
            for (var fid in knownFragmentIds) {
                if (knownFragmentIds.hasOwnProperty(fid) && url.indexOf(fid) !== -1) {
                    return buildMockPreviewUrl(url, fid);
                }
            }
        }
        return url;
    }

    // --- Editor-side: XHR interceptor ---
    // Granite's HTTP layer bypasses jQuery ajaxPrefilter, so patching
    // XMLHttpRequest.open is the only reliable way to intercept requests
    // from editDialog.js (templates API) and vcfRenderer.js (preview API).
    if (typeof Granite !== "undefined" && Granite.author) {
        var origXhrOpen = XMLHttpRequest.prototype.open;
        XMLHttpRequest.prototype.open = function(method, url) {
            arguments[1] = rewriteUrlIfNeeded(url);
            return origXhrOpen.apply(this, arguments);
        };
    }

    // --- Editor-side: jQuery ajaxPrefilter (belt-and-suspenders) ---
    if (typeof jQuery !== "undefined") {
        var contentFrameScanned = false;

        function ensureStateCollected() {
            if (contentFrameScanned) {
                return;
            }
            try {
                var contentDoc = Granite.author.ContentFrame.contentWindow.document;
                collectAndRewrite(contentDoc);
                contentFrameScanned = true;
            } catch (e) {
                // content frame not ready yet
            }
        }

        jQuery.ajaxPrefilter(function(options) {
            if (!options.url) {
                return;
            }
            ensureStateCollected();
            options.url = rewriteUrlIfNeeded(options.url);
        });
    }
})();
