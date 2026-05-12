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
describe("AuthoringEditorUtils.image (core.wcm.components.commons.editor.authoringutils)", function() {
    var imageUtils;

    beforeAll(function() {
        imageUtils = window.CQ.CoreComponents.AuthoringEditorUtils.image;
    });

    describe("formatPlainTextForMarkup", function() {
        it("returns empty string for null and undefined", function() {
            expect(imageUtils.formatPlainTextForMarkup(null)).toBe("");
            expect(imageUtils.formatPlainTextForMarkup(undefined)).toBe("");
        });

        it("escapes angle brackets for display in HTML content", function() {
            var html = imageUtils.formatPlainTextForMarkup('<img src=x onerror=test>');
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("img")).not.toBe(-1);
        });

        it("passes through plain text unchanged as HTML entities", function() {
            expect(imageUtils.formatPlainTextForMarkup("Portrait")).toBe("Portrait");
        });
    });

    describe("isDamScene7PathEligible", function() {
        it("rejects empty or missing path", function() {
            expect(imageUtils.isDamScene7PathEligible("")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible(null)).toBe(false);
            expect(imageUtils.isDamScene7PathEligible(undefined)).toBe(false);
        });

        it("rejects javascript, data, and vbscript schemes", function() {
            expect(imageUtils.isDamScene7PathEligible("javascript:alert(1)")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("data:text/html,<x>")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("vbscript:msgbox")).toBe(false);
        });

        it("rejects path traversal after decoding", function() {
            expect(imageUtils.isDamScene7PathEligible("../../etc/passwd")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("/content/../secret")).toBe(false);
        });

        it("allows relative repository paths", function() {
            expect(imageUtils.isDamScene7PathEligible("/content/dam/demo")).toBe(true);
            expect(imageUtils.isDamScene7PathEligible("demo/asset")).toBe(true);
        });

        it("allows only absolute http(s) URLs on the same origin", function() {
            var origin = window.location.origin;
            expect(imageUtils.isDamScene7PathEligible(origin + "/path/to/asset")).toBe(true);
            expect(imageUtils.isDamScene7PathEligible("http://example.com/x")).toBe(false);
        });
    });

    describe("sanitizedPageImageThumbnailFromMarkup", function() {
        it("returns null when markup has no thumbnail root", function() {
            expect(imageUtils.sanitizedPageImageThumbnailFromMarkup("<p>no thumb</p>", document)).toBe(null);
        });

        it("returns an element with dangerous tags removed", function() {
            var html =
                '<div><coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="ok">' +
                "<script>bad()</script>" +
                "</coral-fileupload></div>";
            var el = imageUtils.sanitizedPageImageThumbnailFromMarkup(html, document);
            expect(el).not.toBe(null);
            expect(el.querySelector("script")).toBe(null);
        });

        it("strips inline handlers from thumbnail markup", function() {
            var html =
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="a" onerror="bad()">' +
                "</coral-fileupload>";
            var el = imageUtils.sanitizedPageImageThumbnailFromMarkup(html, document);
            var img = el.querySelector("img");
            expect(img.getAttribute("onerror")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });
    });
});
