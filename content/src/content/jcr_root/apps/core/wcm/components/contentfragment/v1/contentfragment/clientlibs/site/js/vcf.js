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
                body.innerHTML = html;
                body.style.display = "";
            })
            .catch(function() {
                // silently fail — component remains empty
            });
    }

    documentReady(function() {
        var vcfElements = document.querySelectorAll(VCF_SELECTOR);
        for (var i = 0; i < vcfElements.length; i++) {
            renderVcf(vcfElements[i]);
        }
    });

})();
