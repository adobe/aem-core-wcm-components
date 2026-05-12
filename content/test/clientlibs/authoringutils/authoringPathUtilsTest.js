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
describe("AuthoringEditorUtils.path (core.wcm.components.commons.editor.authoringutils)", function() {
    var pathUtils;

    beforeAll(function() {
        pathUtils = window.CQ.CoreComponents.AuthoringEditorUtils.path;
    });

    describe("isRepoPathAttributeValue", function() {
        it("rejects missing, empty, and non-string values", function() {
            expect(pathUtils.isRepoPathAttributeValue(undefined)).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue(null)).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue("")).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue("   ")).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue(123)).toBe(false);
        });

        it("rejects values that are not absolute repository paths", function() {
            expect(pathUtils.isRepoPathAttributeValue("content/dam/x")).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue("undefined")).toBe(false);
        });

        it("rejects path traversal and angle brackets", function() {
            expect(pathUtils.isRepoPathAttributeValue("/content/../etc")).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue("/content/x%2f%2e%2e%2fetc")).toBe(false);
            expect(pathUtils.isRepoPathAttributeValue('/content/x"><script>')).toBe(false);
        });

        it("allows typical content and apps paths", function() {
            expect(pathUtils.isRepoPathAttributeValue("/content/mysite/en/page")).toBe(true);
            expect(pathUtils.isRepoPathAttributeValue("/apps/core/wcm/components/image/v1/image")).toBe(true);
        });
    });
});
