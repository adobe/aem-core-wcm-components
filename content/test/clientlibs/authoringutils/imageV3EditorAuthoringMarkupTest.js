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
 * Image v3 editor (core.wcm.components.image.v3.editor / image.js) gates markup helpers behind Granite FT_SITES-41279.
 * When the toggle is on, the editor delegates to AuthoringEditorUtils.image / .markup from this clientlib.
 * Production logic stays in image.js; this file only locks the public authoringutils contract those call sites rely on.
 */
describe("Image v3 editor — authoringutils contract (FT_SITES-41279)", function() {
    let imageUtils;
    let markupUtils;

    beforeAll(function() {
        imageUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.image;
        markupUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.markup;
    });

    describe("Smart crop labels (editor formatSmartCropOptionLabel -> formatPlainTextForMarkup)", function() {
        it("encodes markup so Coral select innerHTML cannot inject tags from crop names", function() {
            const encoded = imageUtils.formatPlainTextForMarkup('SmartCrop<script>alert(1)</script>');
            expect(encoded.indexOf("<script>")).toBe(-1);
            expect(encoded.indexOf("SmartCrop")).not.toBe(-1);
        });

        it("encodes angle brackets for arbitrary label strings", function() {
            const html = imageUtils.formatPlainTextForMarkup("16:9 <extra>");
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("16:9")).not.toBe(-1);
        });

        it("returns empty string for null and undefined", function() {
            expect(imageUtils.formatPlainTextForMarkup(null)).toBe("");
            expect(imageUtils.formatPlainTextForMarkup(undefined)).toBe("");
        });
    });

    describe("Dynamic Media path gate (editor isDamScene7FileEligible -> isDamScene7PathEligible)", function() {
        it("rejects javascript:, data:, and vbscript: values", function() {
            expect(imageUtils.isDamScene7PathEligible("javascript:alert(1)")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("data:text/html,<x>")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("vbscript:msgbox")).toBe(false);
        });

        it("rejects mixed-case dangerous schemes after normalisation", function() {
            expect(imageUtils.isDamScene7PathEligible("JaVaScRiPt:alert(1)")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("DaTa:text/html,x")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("VbScRiPt:x")).toBe(false);
        });

        it("accepts typical repository paths used when composing /is/image/ requests", function() {
            expect(imageUtils.isDamScene7PathEligible("/content/dam/demo")).toBe(true);
            expect(imageUtils.isDamScene7PathEligible("demo/asset")).toBe(true);
        });
    });

    describe("linkValueHasExcludedRepositoryPrefix (src/href-style scheme guard for thumbnail build)", function() {
        it("flags javascript:, data:, and vbscript: the same for link-like attribute payloads", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("javascript:void(0)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("data:text/plain,x")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("VbScRiPt:x")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/page.html")).toBe(false);
        });

        it("treats scheme checks case-insensitively after lowercasing", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("JavaScript:alert(1)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("DATA:image/png;base64,xx")).toBe(true);
        });
    });

    describe("Page image thumbnail import (editor updateImageThumbnail toggle-on -> importParsedPageImageThumbnail)", function() {
        it("returns null when markup has no thumbnail root", function() {
            expect(imageUtils.importParsedPageImageThumbnail("<p>no thumb</p>", globalThis.document)).toBe(null);
        });

        it("returns null when the thumbnail root is not a coral-fileupload", function() {
            const html =
                '<div class="cq-page-image-thumbnail"><img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt=""></div>';
            expect(imageUtils.importParsedPageImageThumbnail(html, globalThis.document)).toBe(null);
        });

        it("returns a fragment without script under the thumbnail root", function() {
            const html =
                '<div><coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="ok">' +
                "<script>void 0</script>" +
                "</coral-fileupload></div>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            expect(el).not.toBe(null);
            expect(el.querySelector("script")).toBe(null);
        });

        it("drops declarative handler attributes on the rebuilt thumbnail image", function() {
            const html =
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="a" onclick="void 0">' +
                "</coral-fileupload>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("drops mixed-case handler attribute names (onClick, ONMOUSEOVER)", function() {
            const html =
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="a" onClick="void 0" ONMOUSEOVER="void 0">' +
                "</coral-fileupload>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("onmouseover")).toBe(null);
            expect(img.getAttribute("onClick")).toBe(null);
        });

        it("drops ONFOCUS / OnBlur style mixed-case handler names", function() {
            const html =
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="a" ONFOCUS="void 0" OnBlur="void 0">' +
                "</coral-fileupload>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onfocus")).toBe(null);
            expect(img.getAttribute("onblur")).toBe(null);
        });

        it("uses a safe placeholder when img src is javascript:, data:, or mixed-case javascript:", function() {
            const j = imageUtils.importParsedPageImageThumbnail(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="javascript:alert(1)" alt="bad">' +
                    "</coral-fileupload>",
                globalThis.document
            );
            expect(j.querySelector("img.cq-page-image-thumbnail__image")).toBe(null);
            expect(j.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);

            const d = imageUtils.importParsedPageImageThumbnail(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="data:text/html,&lt;x&gt;" alt="bad">' +
                    "</coral-fileupload>",
                globalThis.document
            );
            expect(d.querySelector("img")).toBe(null);

            const m = imageUtils.importParsedPageImageThumbnail(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="JaVaScRiPt:alert(1)" alt="bad">' +
                    "</coral-fileupload>",
                globalThis.document
            );
            expect(m.querySelector("img")).toBe(null);
            expect(m.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);
        });

        it("does not propagate injected anchor href or rogue img href into the shell", function() {
            const nested = imageUtils.importParsedPageImageThumbnail(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<div><div><a href="javascript:alert(1)">x</a>' +
                    '<img class="cq-page-image-thumbnail__image" src="/content/dam/safe.png" alt="s">' +
                    "</div></div></coral-fileupload>",
                globalThis.document
            );
            expect(nested.querySelector("a")).toBe(null);
            expect(nested.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/safe.png");

            const rogueHref = imageUtils.importParsedPageImageThumbnail(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="ok" href="javascript:alert(1)">' +
                    "</coral-fileupload>",
                globalThis.document
            );
            const img = rogueHref.querySelector("img.cq-page-image-thumbnail__image");
            expect(img.getAttribute("href")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("recurses through deeply nested markup for image and allowlisted buttons (replaceWith payload)", function() {
            const html =
                '<div><coral-fileupload class="cq-page-image-thumbnail">' +
                '<div><div><div>' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/nested.png" alt="n">' +
                "</div></div></div>" +
                '<div class="cq-FileUpload-thumbnail">' +
                '<div class="cq-FileUpload-thumbnail-img"></div>' +
                '<div><div><div>' +
                '<button type="button" class="cq-FileUpload-edit" data-cq-fileupload-filereference="/content/dam/ref">' +
                '<coral-button-label class="_coral-Button-label">Edit</coral-button-label>' +
                "</button></div></div></div></div>" +
                "</coral-fileupload></div>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            expect(el.ownerDocument).toBe(globalThis.document);
            expect(el.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/nested.png");
            const edit = el.querySelector("button.cq-FileUpload-edit");
            expect(edit).not.toBe(null);
            expect(edit.dataset.cqFileuploadFilereference).toBe("/content/dam/ref");
        });

        it("collects edit, picker, and clear from a deeply nested cq-FileUpload-thumbnail subtree", function() {
            const html =
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="">' +
                '<div class="cq-FileUpload-thumbnail">' +
                '<div><div><div><div>' +
                '<button type="button" class="cq-FileUpload-edit" data-cq-fileupload-filereference="/content/dam/e">' +
                '<coral-button-label class="_coral-Button-label">E</coral-button-label></button>' +
                '<div><div><div>' +
                '<button type="button" class="cq-FileUpload-picker">' +
                '<coral-button-label class="_coral-Button-label">P</coral-button-label></button>' +
                "</div></div></div>" +
                '<button type="button" class="cq-FileUpload-clear">' +
                '<coral-button-label class="_coral-Button-label">C</coral-button-label></button>' +
                "</div></div></div></div></div></coral-fileupload>";
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            expect(el.querySelector("button.cq-FileUpload-edit")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-picker")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-clear")).not.toBe(null);
        });
    });
});
