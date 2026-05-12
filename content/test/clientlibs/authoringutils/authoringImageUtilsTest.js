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
    let imageUtils;

    beforeAll(function() {
        imageUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.image;
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
});
