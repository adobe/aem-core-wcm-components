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
(function() {
    "use strict";

    var ATTR_VCF_URL = "data-cmp-contentfragment-vcf-url";
    var ATTR_VCF_AUTH = "data-cmp-contentfragment-vcf-auth";
    var VCF_SELECTOR = ".cmp-contentfragment--vcf[" + ATTR_VCF_URL + "]";

    function i18n(message) {
        if (typeof window !== "undefined" && window.Granite && window.Granite.I18n) {
            return window.Granite.I18n.get(message);
        }
        return message;
    }

    /**
     * Builds placeholder markup for the VCF shadow root; uses DOM so translated strings are escaped.
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
            "The content fragment visualization could not be displayed."
        );
    }

    function documentReady(fn) {
        if (document.readyState !== "loading") {
            fn();
        } else {
            document.addEventListener("DOMContentLoaded", fn);
        }
    }

    /**
     * Retrieves the IMS access token from sessionStorage (available on AEM author
     * tier when the user is logged in via the Unified Shell).
     * Returns null on publish tier or when no token is available.
     */
    function getImsAccessToken() {
        try {
            var prefix = "adobeid_ims_access_token/";
            var keys = Object.keys(sessionStorage);
            for (var i = 0; i < keys.length; i++) {
                if (keys[i].indexOf(prefix) === 0) {
                    var entry = JSON.parse(sessionStorage.getItem(keys[i]));
                    if (entry && entry.tokenValue) {
                        return entry.tokenValue;
                    }
                }
            }
        } catch (e) {
            // publish tier or restricted access — no token available
        }
        return null;
    }

    /**
     * Fetches VCF preview HTML and renders it inside a shadow DOM on the given element.
     */
    function renderVcf(element) {
        var url = element.getAttribute(ATTR_VCF_URL);
        if (!url || element.shadowRoot) {
            return;
        }

        var headers = {};
        if (element.hasAttribute(ATTR_VCF_AUTH)) {
            var token = getImsAccessToken();
            if (token) {
                headers["Authorization"] = "Bearer " + token;
            }
        }

        var shadow = element.attachShadow({ mode: "open" });
        var body = document.createElement("body");
        body.style.display = "none";
        shadow.appendChild(body);

        fetch(url, { headers: headers })
            .then(function(response) {
                return response.ok ? response.text() : Promise.reject(response.status);
            })
            .then(function(html) {
                if (typeof html === "string" && html.trim().length === 0) {
                    body.innerHTML = buildLoadFailedPlaceholderHtml();
                } else {
                    body.innerHTML = html;
                }
                body.style.display = "";
            })
            .catch(function() {
                body.innerHTML = buildLoadFailedPlaceholderHtml();
                body.style.display = "";
            });
    }

    documentReady(function() {
        var vcfElements = document.querySelectorAll(VCF_SELECTOR);
        for (var i = 0; i < vcfElements.length; i++) {
            renderVcf(vcfElements[i]);
        }
    });

})();
