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
describe("Test editDialog VCF template retention for", function() {

    var channel;
    var COMPONENT_PATH = "/content/test-page/jcr:content/root/cf";
    var FRAGMENT_PATH = "/content/dam/test/my-fragment";
    var MODEL_PATH = "/conf/test/settings/dam/cfm/models/person";
    var TEMPLATES_RESPONSE = {
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

        var tabView = fixture.el.querySelector("coral-tabview");
        tabView.tabList = {
            items: {
                getAll: function() {
                    return [{ hidden: false }, { hidden: false }];
                }
            }
        };

        var vcfSelect = fixture.el.querySelector("[data-vcf-template-selector='true']");
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
    });

    afterEach(function() {
        fixture.cleanup();
        jQuery._getJSONHandler = null;
        jQuery._ajaxHandler = null;
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
            if (url.indexOf("jcr:content/data") >= 0) {
                resolve({ "cq:model": MODEL_PATH });
            } else if (url.indexOf(COMPONENT_PATH) >= 0) {
                if (options.componentData !== null) {
                    resolve(options.componentData);
                } else {
                    reject();
                }
            }
        };

        jQuery._ajaxHandler = function(ajaxOptions, resolve) {
            resolve(TEMPLATES_RESPONSE);
        };

        channel.trigger({ type: "foundation-contentloaded", target: fixture.el });

        setTimeout(function() {
            var vcfSelect = fixture.el.querySelector("[data-vcf-template-selector='true']");
            expect(vcfSelect.items._items.length).toBe(options.expectedItemCount);
            expect(vcfSelect.value).toBe(options.expectedTemplate);
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
        var ajaxCalled = false;

        jQuery._getJSONHandler = function(url, resolve) {
            if (url.indexOf(COMPONENT_PATH) >= 0) {
                resolve({});
            }
        };

        jQuery._ajaxHandler = function() {
            ajaxCalled = true;
        };

        channel.trigger({ type: "foundation-contentloaded", target: fixture.el });

        setTimeout(function() {
            var vcfSelect = fixture.el.querySelector("[data-vcf-template-selector='true']");
            expect(vcfSelect.items._items.length).toBe(0);
            expect(ajaxCalled).toBe(false);
            done();
        }, 100);
    });

});
