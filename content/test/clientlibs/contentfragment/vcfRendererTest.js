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
        Granite.author.ContentFrame.contentWindow = null;
    });

    it("foundation-contentloaded searches the content frame document", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        vcfElement.dataset.cmpContentfragmentPath = "/content/dam/test/fragment";
        vcfElement.dataset.cmpContentfragmentVcfTemplate = "person";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        let getJSONCalled = false;
        jQuery._getJSONHandler = function(url, resolve) {
            if (url === "/content/dam/test/fragment/jcr:content.json") {
                getJSONCalled = true;
                resolve({"jcr:uuid": FRAGMENT_UUID});
            }
        };

        jQuery._ajaxHandler = function(options, resolve) {
            expect(options.url).toContain("/preview");
            expect(options.url).toContain(encodeURIComponent(FRAGMENT_UUID));
            expect(options.url).toContain("templateId=person");
            resolve("<div>VCF Preview Content</div>");

            setTimeout(function() {
                expect(vcfElement.innerHTML).toBe("<div>VCF Preview Content</div>");
                expect(getJSONCalled).toBeTrue();
                done();
            }, 0);
        };

        channel.trigger({type: "foundation-contentloaded", target: document.body});
    });

    it("cq-editor-loaded searches the content frame document", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        vcfElement.dataset.cmpContentfragmentPath = "/content/dam/test/fragment2";
        vcfElement.dataset.cmpContentfragmentVcfTemplate = "card";
        vcfElement.dataset.cmpContentfragmentVariation = "summary";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._getJSONHandler = function(url, resolve) {
            resolve({"jcr:uuid": FRAGMENT_UUID});
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

    it("skips elements without fragment path", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._getJSONHandler = function() {
            fail("getJSON should not be called for elements without fragment path");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            expect(vcfElement.innerHTML).toBe("");
            done();
        }, 50);
    });

    it("handles missing content frame gracefully", function(done) {
        Granite.author.ContentFrame.contentWindow = null;

        jQuery._getJSONHandler = function() {
            fail("getJSON should not be called when content frame is unavailable");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            done();
        }, 50);
    });

    it("handles failed fragment ID resolution", function(done) {
        const contentFrameDoc = document.createElement("div");
        const vcfElement = document.createElement("div");
        vcfElement.className = "cmp-contentfragment cmp-contentfragment--vcf";
        vcfElement.dataset.cmpContentfragmentPath = "/content/dam/test/missing";
        contentFrameDoc.appendChild(vcfElement);

        Granite.author.ContentFrame.contentWindow = {
            document: contentFrameDoc
        };

        jQuery._getJSONHandler = function(url, resolve, reject) {
            reject();
        };

        jQuery._ajaxHandler = function() {
            fail("ajax should not be called when fragment ID resolution fails");
        };

        channel.trigger("cq-editor-loaded");

        setTimeout(function() {
            expect(vcfElement.innerHTML).toBe("");
            done();
        }, 50);
    });

});
