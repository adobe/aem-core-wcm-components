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
describe("AuthoringEditorUtils.markup (core.wcm.components.commons.editor.authoringutils)", function() {
    let markupUtils;

    beforeAll(function() {
        markupUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.markup;
    });

    describe("linkValueHasExcludedRepositoryPrefix", function() {
        it("returns false for typical repository paths", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/x")).toBe(false);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("")).toBe(false);
        });

        it("returns true for URL values outside repository http(s) conventions", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("javascript:void(0)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("data:text/plain,x")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("vbscript:x")).toBe(true);
        });

        it("treats scheme checks case-insensitively after lowercasing", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("JavaScript:alert(1)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("DATA:image/png;base64,xx")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("  javascript:x  ")).toBe(true);
        });

        it("treats href- or src-like attribute values the same for dangerous schemes (javascript, data, vbscript)", function() {
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("javascript:void(0)")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("data:text/html,<x>")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("VbScRiPt:msgbox")).toBe(true);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/page.html")).toBe(false);
            expect(markupUtils.linkValueHasExcludedRepositoryPrefix("/content/dam/page.html#frag")).toBe(false);
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

        it("omits the preview image when src uses a disallowed scheme (javascript / data)", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="javascript:alert(1)" alt="x">' +
                    "</coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built.querySelector("img.cq-page-image-thumbnail__image")).toBe(null);
            expect(built.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);
        });

        it("omits the preview image when src uses a mixed-case javascript: scheme", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="JaVaScRiPt:alert(1)" alt="x">' +
                    "</coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built.querySelector("img")).toBe(null);
            expect(built.querySelector("coral-icon[icon=\"image\"]")).not.toBe(null);
        });

        it("omits the preview image when src uses data: scheme", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="data:text/html,&lt;script&gt;" alt="x">' +
                    "</coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built.querySelector("img")).toBe(null);
        });

        it("finds a deeply nested thumbnail image and copies only safe attributes", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<div><div><div>' +
                    '<img class="cq-page-image-thumbnail__image" src="/content/dam/deep/nested.png" alt="deep" ' +
                    'onClick="void 0" ONMOUSEOVER="void 0">' +
                    "</div></div></div></coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const img = built.querySelector("img.cq-page-image-thumbnail__image");
            expect(img).not.toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/deep/nested.png");
            expect(img.getAttribute("alt")).toBe("deep");
            expect(img.getAttribute("onclick")).toBe(null);
            expect(img.getAttribute("onmouseover")).toBe(null);
        });

        it("recurses into nested thumbnail markup for allowlisted buttons", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="">' +
                    '<div class="cq-FileUpload-thumbnail">' +
                    '<div class="cq-FileUpload-thumbnail-img">' +
                    '<img src="/content/dam/x.png">' +
                    "</div>" +
                    '<div><div>' +
                    '<button type="button" class="cq-FileUpload-clear _coral-Button">' +
                    '<coral-button-label class="_coral-Button-label">Clear</coral-button-label>' +
                    "</button></div></div></div></coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const clearBtn = built.querySelector("button.cq-FileUpload-clear");
            expect(clearBtn).not.toBe(null);
            expect(clearBtn.querySelector("coral-button-label").textContent).toBe("Clear");
        });

        it("walks a deep subtree for image, controls, and ignores injected link nodes", function() {
            const deepWrap =
                "<div><div><div><div><div><div><div><div><div><div>" +
                '<img class="cq-page-image-thumbnail__image" src="/content/dam/deep.png" alt="d">' +
                '<a href="javascript:alert(1)">bad</a>' +
                "</div></div></div></div></div></div></div></div></div></div>";
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
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
                    "</div></div></div></div></div></coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            expect(built.querySelector("a")).toBe(null);
            expect(built.querySelector("img.cq-page-image-thumbnail__image").getAttribute("src")).toBe("/content/dam/deep.png");
            expect(built.querySelectorAll("button.cq-FileUpload-edit").length).toBe(1);
            expect(built.querySelectorAll("button.cq-FileUpload-picker").length).toBe(1);
            expect(built.querySelectorAll("button.cq-FileUpload-clear").length).toBe(1);
        });

        it("does not copy a non-allowlisted href from the source thumbnail image (only src and alt)", function() {
            const doc = new globalThis.DOMParser().parseFromString(
                '<coral-fileupload class="cq-page-image-thumbnail">' +
                    '<img class="cq-page-image-thumbnail__image" src="/content/dam/x.png" alt="ok" ' +
                    'href="javascript:alert(1)">' +
                    "</coral-fileupload>",
                "text/html"
            );
            const src = doc.querySelector(".cq-page-image-thumbnail");
            const built = markupUtils.buildPageImageThumbnailShellForEditor(src, globalThis.document);
            const img = built.querySelector("img.cq-page-image-thumbnail__image");
            expect(img).not.toBe(null);
            expect(img.getAttribute("href")).toBe(null);
            expect(img.getAttribute("src")).toBe("/content/dam/x.png");
        });
    });
});
