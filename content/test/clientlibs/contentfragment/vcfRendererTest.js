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
describe("Test VCF renderer for", function() {

    let channel;
    const FRAGMENT_UUID = "test-uuid-1234";

    beforeAll(function() {
        fixture.setBase("test/fixtures/contentfragment");
        channel = jQuery(document);
    });

    beforeEach(function() {
        this.result = fixture.load("vcfRendererTest.html");
    });

    afterEach(function() {
        fixture.cleanup();
        jQuery._getJSONHandler = null;
        jQuery._ajaxHandler = null;
        jQuery._getHandler = null;
        Granite.author.ContentFrame.contentWindow = null;
    });

    it("foundation-contentloaded searches the content frame document", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        const previewUrl = "/cf/preview?" + FRAGMENT_UUID + "&templateId=person";
        vcfElement.dataset.cmpContentfragmentVcfUrl = previewUrl;
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._ajaxHandler = function(options, resolve) {
            expect(options.url).toContain("/preview");
            expect(options.url).toContain(FRAGMENT_UUID);
            expect(options.url).toContain("templateId=person");
            resolve("<div>VCF Preview Content</div>");

            setTimeout(function() {
                expect(vcfElement.innerHTML).toBe("<div>VCF Preview Content</div>");
                done();
            }, 0);
        };

        channel.trigger({type: "foundation-contentloaded", target: document.body});
    });

    it("cq-editor-loaded searches the content frame document", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        const previewUrl = "/cf/preview?templateId=card&variation=summary";
        vcfElement.dataset.cmpContentfragmentVcfUrl = previewUrl;
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._ajaxHandler = function(options, resolve) {
            expect(options.url).toContain("templateId=card");
            expect(options.url).toContain("variation=summary");
            resolve("<div>Card Preview</div>");

            setTimeout(function() {
                expect(vcfElement.innerHTML).toBe("<div>Card Preview</div>");
                done();
            }, 0);
        };

        channel.trigger("cq-editor-loaded");
    });

    it("skips elements without VCF preview URL", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._ajaxHandler = function() {
            fail("ajax should not be called for elements without fragment id");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            expect(vcfElement.innerHTML).toBe("");
            done();
        }, 50);
    });

    it("handles missing content frame gracefully", function(done) {
        Granite.author.ContentFrame.contentWindow = null;

        jQuery._ajaxHandler = function() {
            fail("ajax should not be called when content frame is unavailable");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            done();
        }, 50);
    });

    it("does not load element already being loaded", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        vcfElement.dataset.cmpContentfragmentVcfUrl = "/cf/preview?" + FRAGMENT_UUID;
        vcfElement.dataset.vcfLoading = "true";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._ajaxHandler = function() {
            fail("ajax should not be called for elements already being loaded");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            expect(vcfElement.innerHTML).toBe("");
            done();
        }, 50);
    });

});
