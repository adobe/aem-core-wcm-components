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
    });
});
