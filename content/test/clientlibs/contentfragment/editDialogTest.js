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
 * Helpers for {@code Granite.Toggles} during editDialog markup-helper test suites
 * ({@code globalThis.__CONTENTFRAGMENT_V1_DIALOG_TEST_API}).
 */
function editDialogTest_graniteAllOn() {
    globalThis.Granite.Toggles.isEnabled = function() {
        return true;
    };
}

function editDialogTest_graniteCt41323Off() {
    globalThis.Granite.Toggles.isEnabled = function(key) {
        return key !== "CT_SITES-41323";
    };
}

describe("Test editDialog VCF template retention for", function() {

    let channel;
    const COMPONENT_PATH = "/content/test-page/jcr:content/root/cf";
    const FRAGMENT_PATH = "/content/dam/test/my-fragment";
    const MODEL_PATH = "/conf/test/settings/dam/cfm/models/person";
    const TEMPLATES_RESPONSE = {
        items: [
            { id: "hero-banner", name: "Hero Banner" },
            { id: "card", name: "Card" },
            { id: "teaser", name: "Teaser" }
        ]
    };

    beforeAll(function() {
        fixture.setBase("test/fixtures/contentfragment");
        channel = jQuery(document);
    });

    beforeEach(function() {
        this.result = fixture.load("editDialogTest.html");

        const tabView = fixture.el.querySelector("coral-tabview");
        tabView.tabList = {
            items: {
                getAll: function() {
                    return [{ hidden: false }, { hidden: false }];
                }
            }
        };

        const vcfSelect = fixture.el.querySelector("[data-vcf-template-selector='true']");
        vcfSelect._selectedValue = "";
        vcfSelect.items = {
            _items: [],
            clear: function() {
                this._items = [];
                vcfSelect._selectedValue = "";
            },
            add: function(item) {
                this._items.push(item);
                if (item.selected) {
                    vcfSelect._selectedValue = item.value;
                }
            }
        };
        Object.defineProperty(vcfSelect, "value", {
            get: function() { return this._selectedValue; },
            set: function(v) { this._selectedValue = v; },
            configurable: true
        });

        jQuery._getHandler = function(url, deliver) {
            if (typeof url === "string" && url.includes(".html")) {
                deliver("<span data-cmp-contentfragment-vcf-templates-api=\"/mock/vcf-templates-api\"></span>");
            } else {
                deliver("");
            }
        };
    });

    afterEach(function() {
        fixture.cleanup();
        jQuery._getJSONHandler = null;
        jQuery._ajaxHandler = null;
        jQuery._getHandler = null;
    });

    /**
     * Sets up mock handlers for the component resource and model resolution,
     * triggers dialog initialization, and asserts VCF template state.
     *
     * @param {Object} options
     * @param {Object|null} options.componentData - data returned by the component JSON fetch, or null to reject
     * @param {String} options.expectedTemplate - expected vcfTemplate value after init
     * @param {Number} options.expectedItemCount - expected number of template items
     * @param {Function} done - Jasmine async callback
     */
    function initDialogAndAssert(options, done) {
        jQuery._getJSONHandler = function(url, resolve, reject) {
            if (url.includes("jcr:content/data")) {
                resolve({ "cq:model": MODEL_PATH });
            } else if (url.includes(COMPONENT_PATH)) {
                if (options.componentData === null) {
                    reject();
                } else {
                    resolve(options.componentData);
                }
            }
        };

        jQuery._ajaxHandler = function(ajaxOptions, resolve) {
            resolve(TEMPLATES_RESPONSE);
        };

        channel.trigger({ type: "foundation-contentloaded", target: fixture.el });

        setTimeout(function() {
            const vcfSelectAfter = fixture.el.querySelector("[data-vcf-template-selector='true']");
            expect(vcfSelectAfter.items._items.length).toBe(options.expectedItemCount);
            expect(vcfSelectAfter.value).toBe(options.expectedTemplate);
            done();
        }, 100);
    }

    it("previously saved template is restored on dialog re-open", function(done) {
        initDialogAndAssert({
            componentData: { vcfTemplate: "hero-banner", fragmentPath: FRAGMENT_PATH, displayMode: "vcf" },
            expectedTemplate: "hero-banner",
            expectedItemCount: 3
        }, done);
    });

    it("no template pre-selected when component has no stored value", function(done) {
        initDialogAndAssert({
            componentData: { fragmentPath: FRAGMENT_PATH, displayMode: "vcf" },
            expectedTemplate: "",
            expectedItemCount: 3
        }, done);
    });

    it("template is loaded even when component JSON fetch fails", function(done) {
        initDialogAndAssert({
            componentData: null,
            expectedTemplate: "",
            expectedItemCount: 3
        }, done);
    });

    it("templates are not loaded when no fragment is selected", function(done) {
        fixture.el.querySelector("[name='./fragmentPath']").value = "";
        let ajaxCalled = false;

        jQuery._getJSONHandler = function(url, resolve) {
            if (url.includes(COMPONENT_PATH)) {
                resolve({});
            }
        };

        jQuery._ajaxHandler = function() {
            ajaxCalled = true;
        };

        channel.trigger({ type: "foundation-contentloaded", target: fixture.el });

        setTimeout(function() {
            const vcfSelectAfter = fixture.el.querySelector("[data-vcf-template-selector='true']");
            expect(vcfSelectAfter.items._items.length).toBe(0);
            expect(ajaxCalled).toBe(false);
            done();
        }, 100);
    });

});

/**
 * Covers Content Fragment v1 editor {@code editDialog.js} (CT_SITES-41323) wired through
 * {@code globalThis.__CONTENTFRAGMENT_V1_DIALOG_TEST_API}. Karma loads {@code authoringutils} before {@code editDialog.js}.
 */
describe("Content Fragment v1 editor editDialog.js (Karma-loaded)", function() {
    let api;
    let togglesIsEnabled;

    beforeAll(function() {
        api = globalThis.__CONTENTFRAGMENT_V1_DIALOG_TEST_API;
        togglesIsEnabled = globalThis.Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        globalThis.Granite.Toggles.isEnabled = togglesIsEnabled;
    });

    describe("__CONTENTFRAGMENT_V1_DIALOG_TEST_API", function() {
        it("exposes resolve and toggle helpers", function() {
            expect(api).toBeDefined();
            expect(typeof api.resolveElementNamesContainerInnerHtml).toBe("function");
            expect(typeof api.isContentFragmentV1DialogAuthoringMarkupHelpersEnabled).toBe("function");
            expect(typeof api.getAuthoringMarkupUtils).toBe("function");
        });
    });

    describe("isContentFragmentV1DialogAuthoringMarkupHelpersEnabled", function() {
        it("treats missing Granite.Toggles as enabled", function() {
            const saved = globalThis.Granite.Toggles;
            globalThis.Granite.Toggles = undefined;
            expect(api.isContentFragmentV1DialogAuthoringMarkupHelpersEnabled()).toBe(true);
            globalThis.Granite.Toggles = saved;
        });

        it("returns false when CT_SITES-41323 is explicitly disabled", function() {
            editDialogTest_graniteCt41323Off();
            expect(api.isContentFragmentV1DialogAuthoringMarkupHelpersEnabled()).toBe(false);
        });

        it("returns true when CT_SITES-41323 is enabled", function() {
            editDialogTest_graniteAllOn();
            expect(api.isContentFragmentV1DialogAuthoringMarkupHelpersEnabled()).toBe(true);
        });
    });

    describe("resolveElementNamesContainerInnerHtml", function() {
        it("normalizes markup when helpers are enabled", function() {
            editDialogTest_graniteAllOn();
            const doc =
                "<div><div data-element-names-container=\"true\"><img src=\"x\" onerror=\"alert(1)\"></div></div>";
            const inner = api.resolveElementNamesContainerInnerHtml(doc);
            expect(inner.indexOf("onerror")).toBe(-1);
        });

        it("keeps legacy parsing when helpers are disabled", function() {
            editDialogTest_graniteCt41323Off();
            const doc =
                "<div><div data-element-names-container=\"true\"><img src=\"x\" onerror=\"alert(1)\"></div></div>";
            const inner = api.resolveElementNamesContainerInnerHtml(doc);
            expect(inner.indexOf("onerror")).not.toBe(-1);
        });
    });
});
