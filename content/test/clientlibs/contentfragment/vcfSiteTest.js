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
describe("Test site vcf.js for", function() {

    const VCF_SCRIPT = "/base/src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment/clientlibs/site/js/vcf.js";
    const PREVIEW_URL = "/content/dam/test/cf-preview";

    let originalFetch;

    function appendVcfElement(options) {
        const el = document.createElement("div");
        el.className = "cmp-contentfragment cmp-contentfragment--vcf";
        el.setAttribute("data-cmp-contentfragment-vcf-url", options.url || PREVIEW_URL);
        if (options.withAuth) {
            el.setAttribute("data-cmp-contentfragment-vcf-auth", "");
        }
        document.body.appendChild(el);
        return el;
    }

    function loadVcfScript() {
        return new Promise(function(resolve, reject) {
            const s = document.createElement("script");
            s.src = VCF_SCRIPT + "?t=" + Date.now();
            s.onload = function() { resolve(); };
            s.onerror = function() { reject(new Error("failed to load vcf.js")); };
            document.head.appendChild(s);
        });
    }

    function flushPromises() {
        return new Promise(function(resolve) {
            setTimeout(resolve, 0);
        });
    }

    beforeEach(function() {
        originalFetch = window.fetch;
    });

    afterEach(function() {
        window.fetch = originalFetch;
        document.body.innerHTML = "";
        try {
            const keys = Object.keys(sessionStorage);
            for (let i = 0; i < keys.length; i++) {
                if (keys[i].indexOf("adobeid_ims_access_token/") === 0) {
                    sessionStorage.removeItem(keys[i]);
                }
            }
        } catch (e) {
            // ignore
        }
    });

    it("fetches preview HTML and renders it inside shadow DOM", async function() {
        window.fetch = jasmine.createSpy("fetch").and.returnValue(Promise.resolve({
            ok: true,
            text: function() {
                return Promise.resolve("<div class=\"vcf-preview\">Rendered</div>");
            }
        }));

        const el = appendVcfElement({});
        await loadVcfScript();
        await flushPromises();

        expect(window.fetch.calls.mostRecent().args[0]).toBe(PREVIEW_URL);
        expect(window.fetch.calls.mostRecent().args[1].headers).toEqual({});
        expect(el.shadowRoot).toBeTruthy();
        const body = el.shadowRoot.querySelector("body");
        expect(body.style.display).toBe("");
        expect(body.innerHTML).toBe("<div class=\"vcf-preview\">Rendered</div>");
    });

    it("sends Authorization when auth attribute is set and IMS token exists", async function() {
        sessionStorage.setItem(
            "adobeid_ims_access_token/unit-test",
            JSON.stringify({ tokenValue: "unit-test-token" })
        );

        window.fetch = jasmine.createSpy("fetch").and.returnValue(Promise.resolve({
            ok: true,
            text: function() {
                return Promise.resolve("<span>ok</span>");
            }
        }));

        appendVcfElement({ withAuth: true });
        await loadVcfScript();
        await flushPromises();

        expect(window.fetch.calls.mostRecent().args[0]).toBe(PREVIEW_URL);
        expect(window.fetch.calls.mostRecent().args[1].headers.Authorization).toBe("Bearer unit-test-token");
    });

    it("does not send Authorization when auth attribute is set but no IMS token", async function() {
        window.fetch = jasmine.createSpy("fetch").and.returnValue(Promise.resolve({
            ok: true,
            text: function() {
                return Promise.resolve("<span>ok</span>");
            }
        }));

        appendVcfElement({ withAuth: true });
        await loadVcfScript();
        await flushPromises();

        expect(window.fetch.calls.mostRecent().args[0]).toBe(PREVIEW_URL);
        expect(window.fetch.calls.mostRecent().args[1].headers).toEqual({});
    });

    it("leaves shadow body hidden when the response is not OK", async function() {
        window.fetch = jasmine.createSpy("fetch").and.returnValue(Promise.resolve({
            ok: false,
            status: 502,
            text: function() {
                return Promise.resolve("");
            }
        }));

        const el = appendVcfElement({});
        await loadVcfScript();
        await flushPromises();

        const body = el.shadowRoot.querySelector("body");
        expect(body.style.display).toBe("none");
        expect(body.innerHTML).toBe("");
    });

    it("does not fetch when the element already has a shadow root", async function() {
        window.fetch = jasmine.createSpy("fetch");

        const el = appendVcfElement({});
        el.attachShadow({ mode: "open" });

        await loadVcfScript();

        expect(window.fetch).not.toHaveBeenCalled();
    });

});
