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
/** Uses {@code globalThis.AuthoringutilsThumbnailFixtures} from {@code authoringImageUtilsTest.js} (load order: name before this file in Karma). */
describe("AuthoringEditorUtils.markup (core.wcm.components.commons.editor.authoringutils)", function() {
    let markupUtils;
    let F;

    beforeAll(function() {
        markupUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.markup;
        F = globalThis.AuthoringutilsThumbnailFixtures;
    });

    describe("linkValueHasExcludedRepositoryPrefix", function() {
        it("returns false for typical repository paths and normal http(s) page URLs", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/x")).toBe(false);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("")).toBe(false);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/page.html")).toBe(false);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/page.html#frag")).toBe(false);
        });

        it("returns true for javascript, data, and vbscript schemes (any casing and trim)", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("javascript:void(0)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("data:text/plain,x")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("vbscript:x")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("data:text/html,<x>")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("VbScRiPt:msgbox")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("JavaScript:alert(1)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("DATA:image/png;base64,xx")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("  javascript:x  ")).toBe(true);
        });
    });

    describe("buildPageImageThumbnailShellForEditor", function() {
        it("returns null when the root is not a coral-fileupload", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<div class="cq-page-image-thumbnail"></div>',
                "text/html"
            );
            const root = doc.body.firstElementChild;
            expect(markupUtils.buildPageImageThumbnailShellForEditor(root, globalThis.document)).toBe(null);
        });

        it("keeps only allowlisted classes on the file upload root", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail cq-FileUpload extra-unknown is-filled" ' +
                    'data-thumbnail-config-path="/content/site/en"></coral-fileupload>',
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built).not.toBe(null);
            expect(built.classList.contains("extra-unknown")).toBe(false);
            expect(built.classList.contains("cq-page-image-thumbnail")).toBe(true);
            expect(built.dataset.thumbnailConfigPath).toBe("/content/site/en");
        });

        it("omits the preview image when src uses disallowed schemes", function() {
            const srcJs = F.parseShellRoot(F.imgTag("javascript:alert(1)", "x"));
            const builtJs = markupUtils.buildPageImageThumbnailShellForEditor(srcJs, globalThis.document);
            expect(builtJs.querySelector("img.cq-page-image-thumbnail__image")).toBe(null);
            expect(builtJs.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);

            const srcMixed = F.parseShellRoot(F.imgTag("JaVaScRiPt:alert(1)", "x"));
            const builtMixed = markupUtils.buildPageImageThumbnailShellForEditor(srcMixed, globalThis.document);
            expect(builtMixed.querySelector("img")).toBe(null);

            const srcData = F.parseShellRoot(F.imgTag("data:text/html,&lt;script&gt;", "x"));
            const builtData = markupUtils.buildPageImageThumbnailShellForEditor(srcData, globalThis.document);
            expect(builtData.querySelector("img")).toBe(null);
        });

        it("finds a deeply nested thumbnail image and copies only safe attributes", function() {
            const inner =
                "<div><div><div>" +
                F.imgTag("/content/dam/deep/nested.png", "deep", 'onClick="void 0" ONMOUSEOVER="void 0"') +
                "</div></div></div>";
            const src = F.parseShellRoot(inner);
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const img = built.querySelector("img.cq-page-image-thumbnail__image");
            expect(img).not.toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/deep/nested.png");
            expect(img.getAttribute("alt")).toBe("deep");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("onmouseover")).toBe(null);
        });

        it("recurses into nested thumbnail markup for allowlisted buttons", function() {
            const inner =
                F.imgTag("/content/dam/x.png", "") +
                '<div class="cq-FileUpload-thumbnail">' +
                '<div class="cq-FileUpload-thumbnail-img">' +
                '<img src="/content/dam/x.png">' +
                "</div>" +
                '<div><div>' +
                '<button type="button" class="cq-FileUpload-clear _coral-Button">' +
                '<coral-button-label class="_coral-Button-label">Clear</coral-button-label>' +
                "</button></div></div></div>";
            const src = F.parseShellRoot(inner);
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const clearBtn = built.querySelector("button.cq-FileUpload-clear");
            expect(clearBtn).not.toBe(null);
            expect(clearBtn.querySelector("coral-button-label").textContent).toBe("Clear");
        });

        it("walks a deep subtree for image, controls, and ignores injected link nodes", function() {
            const deepWrap =
                "<div><div><div><div><div><div><div><div><div><div>" +
                F.imgTag("/content/dam/deep.png", "d") +
                '<a href="javascript:alert(1)">bad</a>' +
                "</div></div></div></div></div></div></div></div></div></div>";
            const inner =
                deepWrap +
                '<div class="cq-FileUpload-thumbnail">' +
                '<div><div><div><div>' +
                '<button type="button" class="cq-FileUpload-edit _coral-Button" data-cq-fileupload-filereference="/content/dam/a">' +
                '<coral-button-label class="_coral-Button-label">E</coral-button-label>' +
                "</button>" +
                '<div><div><div>' +
                '<button type="button" class="cq-FileUpload-picker _coral-Button">' +
                '<coral-button-label class="_coral-Button-label">P</coral-button-label>' +
                "</button></div></div></div>" +
                '<button type="button" class="cq-FileUpload-clear _coral-Button">' +
                '<coral-button-label class="_coral-Button-label">C</coral-button-label>' +
                "</button>" +
                "</div></div></div></div></div>";
            const src = F.parseShellRoot(inner);
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built.querySelector("a")).toBe(null);
            expect(built.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/deep.png");
            expect(built.querySelectorAll("button.cq-FileUpload-edit").length).toBe(1);
            expect(built.querySelectorAll("button.cq-FileUpload-picker").length).toBe(1);
            expect(built.querySelectorAll("button.cq-FileUpload-clear").length).toBe(1);
        });

        it("does not copy a non-allowlisted href from the source thumbnail image (only src and alt)", function() {
            const src = F.parseShellRoot(F.imgTag("/content/dam/x.png", "ok", 'href="javascript:alert(1)"'));
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const img = built.querySelector("img.cq-page-image-thumbnail__image");
            expect(img).not.toBe(null);
            expect(img.getAttribute("href")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });
    });

    describe("sanitizeAuthoringEditorResponseMarkup", function() {
        it("returns inner markup of the first body child with event attributes removed", function() {
            const html =
                "<html><body><div><span onmouseover=\"x\"><img src=\"x\" onerror=\"alert(1)\"></span></div></body></html>";
            const out = markupUtils.sanitizeAuthoringEditorResponseMarkup(html);
            expect(out.indexOf("onerror")).toBe(-1);
            expect(out.indexOf("onmouseover")).toBe(-1);
            expect(out.indexOf("<img")).not.toBe(-1);
        });

        it("drops script tags from nested markup", function() {
            const html = "<div><script>z</script><p>ok</p></div>";
            const out = markupUtils.sanitizeAuthoringEditorResponseMarkup(html);
            expect(out.indexOf("<script>")).toBe(-1);
            expect(out.indexOf(">ok</p>")).not.toBe(-1);
        });

        it("removes href with disallowed schemes", function() {
            const html = "<div><a href=\"javascript:void(0)\">t</a></div>";
            const out = markupUtils.sanitizeAuthoringEditorResponseMarkup(html);
            expect(out.indexOf("javascript")).toBe(-1);
        });

        it("drops style, link, meta, base, and form elements", function() {
            const html =
                "<div><style>x</style><link rel=\"stylesheet\" href=\"/etc.clientlibs/a.css\">" +
                "<meta http-equiv=\"refresh\" content=\"0\">" +
                "<base href=\"http://example.com/\">" +
                "<form action=\"/search\"></form><p>ok</p></div>";
            const out = markupUtils.sanitizeAuthoringEditorResponseMarkup(html);
            expect(out.indexOf("<style")).toBe(-1);
            expect(out.indexOf("<link")).toBe(-1);
            expect(out.indexOf("<meta")).toBe(-1);
            expect(out.indexOf("<base")).toBe(-1);
            expect(out.indexOf("<form")).toBe(-1);
            expect(out.indexOf(">ok</p>")).not.toBe(-1);
        });

        it("returns empty string when body has no element child", function() {
            expect(markupUtils.sanitizeAuthoringEditorResponseMarkup("")).toBe("");
        });
    });
});
