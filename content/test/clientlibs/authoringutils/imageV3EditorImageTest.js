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
 * Covers Image v3 editor {@code image.js} markup-helper surface (FT_SITES-41279) wired through
 * {@code globalThis.__IMAGE_V3_EDITOR_TEST_API}. Depends on {@code authoringImageUtilsTest.js} loading first for
 * {@code AuthoringutilsThumbnailFixtures} (Karma name order).
 */
function imageV3EditorImageTestFtOn() {
    globalThis.Granite.Toggles.isEnabled = function() {
        return true;
    };
}

function imageV3EditorImageTestFtOff() {
    globalThis.Granite.Toggles.isEnabled = function(key) {
        return key !== "FT_SITES-41279";
    };
}

describe("Image v3 editor image.js (Karma-loaded)", function() {
    let api;
    let imageUtils;
    let F;
    let togglesIsEnabled;

    beforeAll(function() {
        api = globalThis.__IMAGE_V3_EDITOR_TEST_API;
        imageUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.image;
        F = globalThis.AuthoringutilsThumbnailFixtures;
        togglesIsEnabled = globalThis.Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        globalThis.Granite.Toggles.isEnabled = togglesIsEnabled;
        globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
    });

    describe("__IMAGE_V3_EDITOR_TEST_API", function() {
        it("exposes helpers used by the dialog markup refresh", function() {
            expect(api).toBeDefined();
            expect(typeof api.formatSmartCropOptionLabel).toBe("function");
            expect(typeof api.isDamScene7FileEligible).toBe("function");
            expect(typeof api.isImageV3AuthoringMarkupHelpersEnabled).toBe("function");
            expect(typeof api.getImageAuthoringUtils).toBe("function");
            expect(typeof api.getAuthoringPathUtils).toBe("function");
            expect(typeof api.importPageImageThumbnailFromMarkup).toBe("function");
        });

        it("getAuthoringPathUtils returns path helpers from commons when present", function() {
            const pathUtils = api.getAuthoringPathUtils();
            expect(pathUtils).toBeTruthy();
            expect(typeof pathUtils.matchesRepoPathAttributePattern).toBe("function");
        });

        it("getImageAuthoringUtils returns AuthoringEditorUtils.image when loaded", function() {
            expect(api.getImageAuthoringUtils()).toBe(globalThis.CQ.CoreComponents.AuthoringEditorUtils.image);
        });
    });

    describe("isImageV3AuthoringMarkupHelpersEnabled (Granite toggle)", function() {
        it("treats missing Granite.Toggles as enabled", function() {
            const saved = globalThis.Granite.Toggles;
            globalThis.Granite.Toggles = undefined;
            expect(api.isImageV3AuthoringMarkupHelpersEnabled()).toBe(true);
            globalThis.Granite.Toggles = saved;
        });

        it("returns false when FT_SITES-41279 is explicitly disabled", function() {
            globalThis.Granite.Toggles.isEnabled = function(key) {
                return key !== "FT_SITES-41279";
            };
            expect(api.isImageV3AuthoringMarkupHelpersEnabled()).toBe(false);
        });

        it("returns true when FT_SITES-41279 is enabled", function() {
            imageV3EditorImageTestFtOn();
            expect(api.isImageV3AuthoringMarkupHelpersEnabled()).toBe(true);
        });
    });

    describe("formatSmartCropOptionLabel", function() {
        it("keeps legacy string behaviour when FT is off", function() {
            imageV3EditorImageTestFtOff();
            expect(api.formatSmartCropOptionLabel("a<b>c")).toBe("a<b>c");
            expect(api.formatSmartCropOptionLabel(null)).toBe("");
            expect(api.formatSmartCropOptionLabel(undefined)).toBe("");
        });

        it("delegates to AuthoringEditorUtils.image when FT is on", function() {
            imageV3EditorImageTestFtOn();
            const html = api.formatSmartCropOptionLabel("x < y");
            expect(html.indexOf("<")).toBe(-1);
        });

        it("encodes crop names that contain script-like markup when FT is on", function() {
            imageV3EditorImageTestFtOn();
            const encoded = api.formatSmartCropOptionLabel('SmartCrop<script>alert(1)</script>');
            expect(encoded.indexOf("<script>")).toBe(-1);
            expect(encoded.indexOf("SmartCrop")).not.toBe(-1);
        });

        it("encodes angle brackets for arbitrary label strings when FT is on", function() {
            imageV3EditorImageTestFtOn();
            const html = api.formatSmartCropOptionLabel("16:9 <extra>");
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("16:9")).not.toBe(-1);
        });

        it("falls back when FT is on but image utils are missing", function() {
            imageV3EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = undefined;
            expect(api.formatSmartCropOptionLabel("plain")).toBe("plain");
            expect(api.formatSmartCropOptionLabel(null)).toBe("");
        });

        it("falls back when FT is on but formatPlainTextForMarkup is not a function", function() {
            imageV3EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = { isDamScene7PathEligible: imageUtils.isDamScene7PathEligible };
            expect(api.formatSmartCropOptionLabel("x")).toBe("x");
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });
    });

    describe("isDamScene7FileEligible", function() {
        it("is permissive when FT is off", function() {
            imageV3EditorImageTestFtOff();
            expect(api.isDamScene7FileEligible("javascript:alert(1)")).toBe(true);
            expect(api.isDamScene7FileEligible("data:text/html,x")).toBe(true);
        });

        it("delegates when FT is on", function() {
            imageV3EditorImageTestFtOn();
            expect(api.isDamScene7FileEligible("/content/dam/x")).toBe(true);
            expect(api.isDamScene7FileEligible("javascript:alert(1)")).toBe(false);
            expect(api.isDamScene7FileEligible("data:text/html,<x>")).toBe(false);
            expect(api.isDamScene7FileEligible("JaVaScRiPt:x")).toBe(false);
        });

        it("rejects unstable path segments when FT is on", function() {
            imageV3EditorImageTestFtOn();
            expect(api.isDamScene7FileEligible("../../etc/passwd")).toBe(false);
        });

        it("returns true when FT is on but image utils are missing", function() {
            imageV3EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = undefined;
            expect(api.isDamScene7FileEligible("javascript:x")).toBe(true);
        });

        it("returns true when FT is on but isDamScene7PathEligible is not a function", function() {
            imageV3EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = { formatPlainTextForMarkup: imageUtils.formatPlainTextForMarkup };
            expect(api.isDamScene7FileEligible("/content/dam/x")).toBe(true);
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });
    });

    describe("importPageImageThumbnailFromMarkup (updateImageThumbnail toggle-on path)", function() {
        it("returns null when image utils are missing", function() {
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = undefined;
            expect(api.importPageImageThumbnailFromMarkup(F.shell(F.imgTag("/x", "a")), globalThis.document)).toBe(null);
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });

        it("returns null when importParsedPageImageThumbnail is not a function", function() {
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = {};
            expect(api.importPageImageThumbnailFromMarkup(F.shell(F.imgTag("/x", "a")), globalThis.document)).toBe(null);
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });

        it("returns null when markup has no thumbnail root", function() {
            expect(api.importPageImageThumbnailFromMarkup("<p>no thumb</p>", globalThis.document)).toBe(null);
        });

        it("returns null when the thumbnail root is not a coral-fileupload", function() {
            const html =
                '<div class="cq-page-image-thumbnail"><img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt=""></div>';
            expect(api.importPageImageThumbnailFromMarkup(html, globalThis.document)).toBe(null);
        });

        it("returns a fragment without script under the thumbnail root", function() {
            const html = F.importMarkup(F.imgTag("/content/dam/x.png", "ok") + "<script>void 0</script>");
            const el = api.importPageImageThumbnailFromMarkup(html, globalThis.document);
            expect(el).not.toBe(null);
            expect(el.querySelector("script")).toBe(null);
        });

        it("drops declarative handler attributes on the rebuilt thumbnail image", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'onclick="void 0"'));
            const el = api.importPageImageThumbnailFromMarkup(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("drops mixed-case handler attribute names (onClick, ONMOUSEOVER)", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'onClick="void 0" ONMOUSEOVER="void 0"'));
            const el = api.importPageImageThumbnailFromMarkup(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("onmouseover")).toBe(null);
            expect(img.getAttribute("onClick")).toBe(null);
        });

        it("drops ONFOCUS / OnBlur style mixed-case handler names", function() {
            const html = F.shell(F.imgTag("/content/dam/x.png", "a", 'ONFOCUS="void 0" OnBlur="void 0"'));
            const el = api.importPageImageThumbnailFromMarkup(html, globalThis.document);
            const img = el.querySelector("img");
            expect(img.getAttribute("onfocus")).toBe(null);
            expect(img.getAttribute("onblur")).toBe(null);
        });

        it("uses a safe placeholder when img src is javascript:, data:, or mixed-case javascript:", function() {
            const j = api.importPageImageThumbnailFromMarkup(F.shell(F.imgTag("javascript:alert(1)", "bad")), globalThis.document);
            expect(j.querySelector("img.cq-page-image-thumbnail__image")).toBe(null);
            expect(j.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);

            const d = api.importPageImageThumbnailFromMarkup(
                F.shell(F.imgTag("data:text/html,&lt;x&gt;", "bad")),
                globalThis.document
            );
            expect(d.querySelector("img")).toBe(null);

            const m = api.importPageImageThumbnailFromMarkup(
                F.shell(F.imgTag("JaVaScRiPt:alert(1)", "bad")),
                globalThis.document
            );
            expect(m.querySelector("img")).toBe(null);
            expect(m.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);
        });

        it("does not propagate injected anchor href or rogue img href into the shell", function() {
            const nested = api.importPageImageThumbnailFromMarkup(
                F.shell(
                    "<div><div><a href=\"javascript:alert(1)\">x</a>" +
                        F.imgTag("/content/dam/safe.png", "s") +
                        "</div></div>"
                ),
                globalThis.document
            );
            expect(nested.querySelector("a")).toBe(null);
            expect(nested.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/safe.png");

            const rogueHref = api.importPageImageThumbnailFromMarkup(
                F.shell(F.imgTag("/content/dam/x.png", "ok", 'href="javascript:alert(1)"')),
                globalThis.document
            );
            const img = rogueHref.querySelector("img.cq-page-image-thumbnail__image");
            expect(img.getAttribute("href")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });

        it("recurses through deeply nested markup for image and allowlisted buttons", function() {
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
            const el = api.importPageImageThumbnailFromMarkup(html, globalThis.document);
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
            const el = api.importPageImageThumbnailFromMarkup(F.shell(inner), globalThis.document);
            expect(el.querySelector("button.cq-FileUpload-edit")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-picker")).not.toBe(null);
            expect(el.querySelector("button.cq-FileUpload-clear")).not.toBe(null);
        });
    });
});
