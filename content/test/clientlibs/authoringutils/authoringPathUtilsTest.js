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

    describe("matchesRepoPathAttributePattern", function() {
        it("returns false for missing, empty, or non-string values", function() {
            expect(pathUtils.matchesRepoPathAttributePattern(undefined)).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern(null)).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern("")).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern("   ")).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern(123)).toBe(false);
        });

        it("returns false when the value is not an absolute repository path", function() {
            expect(pathUtils.matchesRepoPathAttributePattern("content/dam/x")).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern("undefined")).toBe(false);
        });

        it("returns false when the path shape is not stable after decoding", function() {
            expect(pathUtils.matchesRepoPathAttributePattern("/content/../etc")).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern("/content/x%2f%2e%2e%2fetc")).toBe(false);
            expect(pathUtils.matchesRepoPathAttributePattern('/content/x"><img')).toBe(false);
        });

        it("returns true for typical /content and /apps paths", function() {
            expect(pathUtils.matchesRepoPathAttributePattern("/content/mysite/en/page")).toBe(true);
            expect(pathUtils.matchesRepoPathAttributePattern("/apps/core/wcm/components/image/v1/image")).toBe(true);
        });
    });
});
