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
 * Registers {@code globalThis.AuthoringutilsThumbnailFixtures} for page-image thumbnail HTML used in this file and in
 * {@code authoringMarkupUtilsTest.js}. Karma loads test modules in name order; keep this filename before
 * {@code authoringMarkupUtilsTest.js} so the fixtures exist when markup tests run.
 */
(function(global) {
    "use strict";

    var CORAL_OPEN = '<coral-fileupload class="cq-page-image-thumbnail">';
    var CORAL_CLOSE = "</coral-fileupload>";

    function imgTag(src, alt, extraAttr) {
        var al = alt !== undefined && alt !== null ? alt : "x";
        var ex = extraAttr ? " " + extraAttr : "";
        return '<img class="cq-page-image-thumbnail__image" src="' + src + '" alt="' + al + '"' + ex + ">";
    }

    function shell(inner) {
        return CORAL_OPEN + inner + CORAL_CLOSE;
    }

    global.AuthoringutilsThumbnailFixtures = {
        shell: shell,
        imgTag: imgTag,
        parseShellRoot: function(inner) {
            var doc = new global.DOMParser().parseFromString(shell(inner), "text/html");
            return doc.querySelector(".cq-page-image-thumbnail");
        },
        importMarkup: function(inner) {
            return "<div>" + shell(inner) + "</div>";
        }
    };
})(typeof globalThis !== "undefined" ? globalThis : window);

describe("AuthoringEditorUtils.image (core.wcm.components.commons.editor.authoringutils)", function() {
    let imageUtils;
    let F;

    beforeAll(function() {
        imageUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.image;
        F = globalThis.AuthoringutilsThumbnailFixtures;
    });

    describe("formatPlainTextForMarkup", function() {
        it("returns empty string for null and undefined", function() {
            expect(imageUtils.formatPlainTextForMarkup(null)).toBe("");
            expect(imageUtils.formatPlainTextForMarkup(undefined)).toBe("");
        });

        it("encodes markup characters for Coral innerHTML labels", function() {
            const html = imageUtils.formatPlainTextForMarkup("Label <extra> text");
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("extra")).not.toBe(-1);
        });

        it("passes through plain text unchanged", function() {
            expect(imageUtils.formatPlainTextForMarkup("Portrait")).toBe("Portrait");
        });
    });

    describe("isDamScene7PathEligible", function() {
        it("returns false for empty or missing path", function() {
            expect(imageUtils.isDamScene7PathEligible("")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible(null)).toBe(false);
            expect(imageUtils.isDamScene7PathEligible(undefined)).toBe(false);
        });

        it("returns false for non-repository URL schemes in metadata", function() {
            expect(imageUtils.isDamScene7PathEligible("javascript:alert(1)")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("data:text/html,<x>")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("vbscript:msgbox")).toBe(false);
        });

        it("returns false for mixed-case dangerous schemes after normalisation", function() {
            expect(imageUtils.isDamScene7PathEligible("JaVaScRiPt:alert(1)")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("DaTa:text/html,x")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("VbScRiPt:x")).toBe(false);
        });

        it("returns false when decoded path segments are not stable", function() {
            expect(imageUtils.isDamScene7PathEligible("../../etc/passwd")).toBe(false);
            expect(imageUtils.isDamScene7PathEligible("/content/../secret")).toBe(false);
        });

        it("returns true for relative repository path segments", function() {
            expect(imageUtils.isDamScene7PathEligible("/content/dam/demo")).toBe(true);
            expect(imageUtils.isDamScene7PathEligible("demo/asset")).toBe(true);
        });

        it("returns true only for same-origin absolute http(s) URLs", function() {
            const origin = globalThis.location.origin;
            expect(imageUtils.isDamScene7PathEligible(origin + "/path/to/asset")).toBe(true);
            expect(imageUtils.isDamScene7PathEligible("http://example.com/x")).toBe(false);
        });
    });

    /**
     * Image v3 editor (image.js) uses importParsedPageImageThumbnail in the page-thumbnail AJAX path when FT_SITES-41279 is on.
     */
    describe("importParsedPageImageThumbnail (Image v3 page thumbnail shell)", function() {
        it("returns null when markup has no thumbnail root", function() {
            expect(imageUtils.importParsedPageImageThumbnail("<p>no thumb</p>", globalThis.document)).toBe(null);
        });

        it("returns null when the thumbnail root is not a coral-fileupload", function() {
            const html =
                '<div class="cq-page-image-thumbnail"><img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt=""></div>';
            expect(imageUtils.importParsedPageImageThumbnail(html, globalThis.document)).toBe(null);
        });

        it("returns a fragment without script under the thumbnail root", function() {
            const html = F.importMarkup(F.imgTag("/content/dam/x.png", "ok") + "<script>void 0</script>");
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            expect(el).not.toBe(null);
            expect(el.querySelector("script")).toBe(null);
        });

        it("drops declarative handler attributes on the rebuilt thumbnail image", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'onclick="void 0"'));
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("drops mixed-case handler attribute names (onClick, ONMOUSEOVER)", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'onClick="void 0" ONMOUSEOVER="void 0"'));
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("onmouseover")).toBe(null);
            expect(img.getAttribute("onClick")).toBe(null);
        });

        it("drops ONFOCUS / OnBlur style mixed-case handler names", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'ONFOCUS="void 0" OnBlur="void 0"'));
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onfocus")).toBe(null);
            expect(img.getAttribute("onblur")).toBe(null);
        });

        it("uses a safe placeholder when img src is javascript:, data:, or mixed-case javascript:", function() {
            const j = imageUtils.importParsedPageImageThumbnail(F.shell(F.imgTag("javascript:alert(1)", "bad")), globalThis.document);
            expect(j.querySelector("img.cq-page-image-thumbnail__image")).toBe(null);
            expect(j.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);

            const d = imageUtils.importParsedPageImageThumbnail(
                F.shell(F.imgTag("data:text/html,&lt;x&gt;", "bad")),
                globalThis.document
            );
            expect(d.querySelector("img")).toBe(null);

            const m = imageUtils.importParsedPageImageThumbnail(
                F.shell(F.imgTag("JaVaScRiPt:alert(1)", "bad")),
                globalThis.document
            );
            expect(m.querySelector("img")).toBe(null);
            expect(m.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);
        });

        it("does not propagate injected anchor href or rogue img href into the shell", function() {
            const nested = imageUtils.importParsedPageImageThumbnail(
                F.shell(
                    "<div><div><a href=\"javascript:alert(1)\">x</a>" +
                        F.imgTag("/content/dam/safe.png", "s") +
                        "</div></div>"
                ),
                globalThis.document
            );
            expect(nested.querySelector("a")).toBe(null);
            expect(nested.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/safe.png");

            const rogueHref = imageUtils.importParsedPageImageThumbnail(
                F.shell(F.imgTag("/content/dam/x.png", "ok", 'href="javascript:alert(1)"')),
                globalThis.document
            );
            const img = rogueHref.querySelector("img.cq-page-image-thumbnail__image");
            expect(img.getAttribute("href")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("recurses through deeply nested markup for image and allowlisted buttons (replaceWith payload)", function() {
            const inner =
                "<div><div><div>" +
                F.imgTag("/content/dam/nested.png", "n") +
                "</div></div></div>" +
                '<div class="cq-FileUpload-thumbnail">' +
                '<div class="cq-FileUpload-thumbnail-img"></div>' +
                '<div><div><div>' +
                '<button type="button" class="cq-FileUpload-edit" data-cq-fileupload-filereference="/content/dam/ref">' +
                '<coral-button-label class="_coral-Button-label">Edit</coral-button-label>' +
                "</button></div></div></div></div>";
            const html = F.importMarkup(inner);
            const el = imageUtils.importParsedPageImageThumbnail(html, globalThis.document);
            expect(el.ownerDocument).toBe(globalThis.document);
            expect(el.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/nested.png");
            const edit = el.querySelector("button.cq-FileUpload-edit");
            expect(edit).not.toBe(null);
            expect(edit.dataset.cqFileuploadFilereference).toBe("/content/dam/ref");
        });

        it("collects edit, picker, and clear from a deeply nested cq-FileUpload-thumbnail subtree", function() {
            const inner =
                F.imgTag("/content/dam/x.png", "") +
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
                "</div></div></div></div></div>";
            const el = imageUtils.importParsedPageImageThumbnail(F.shell(inner), globalThis.document);
            expect(el.querySelector("button.cq-FileUpload-edit")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-picker")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-clear")).not.toBe(null);
        });
    });

    describe("Image v3 smart crop label encoding (formatSmartCropOptionLabel target)", function() {
        it("encodes crop names that contain script-like markup for Coral innerHTML", function() {
            const encoded = imageUtils.formatPlainTextForMarkup('SmartCrop<script>alert(1)</script>');
            expect(encoded.indexOf("<script>")).toBe(-1);
            expect(encoded.indexOf("SmartCrop")).not.toBe(-1);
        });

        it("encodes angle brackets for arbitrary label strings", function() {
            const html = imageUtils.formatPlainTextForMarkup("16:9 <extra>");
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("16:9")).not.toBe(-1);
        });
    });
});
