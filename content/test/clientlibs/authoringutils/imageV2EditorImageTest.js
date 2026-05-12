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
 * Covers Image v2 editor {@code image.js} (FT_SITES-41320) and related {@code AuthoringEditorUtils.image} checks
 * wired through {@code globalThis.__IMAGE_V2_EDITOR_TEST_API}. Karma loads {@code image/v2/.../image.js} after {@code authoringutils}.
 */
function imageV2EditorImageTestFtOn() {
    globalThis.Granite.Toggles.isEnabled = function() {
        return true;
    };
}

function imageV2EditorImageTestFtOff() {
    globalThis.Granite.Toggles.isEnabled = function(key) {
        return key !== "FT_SITES-41320";
    };
}

describe("Image v2 editor image.js (Karma-loaded)", function() {
    let api;
    let imageUtils;
    let togglesIsEnabled;

    beforeAll(function() {
        api = globalThis.__IMAGE_V2_EDITOR_TEST_API;
        imageUtils = globalThis.CQ.CoreComponents.AuthoringEditorUtils.image;
        togglesIsEnabled = globalThis.Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        globalThis.Granite.Toggles.isEnabled = togglesIsEnabled;
        globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
    });

    describe("__IMAGE_V2_EDITOR_TEST_API", function() {
        it("exposes helpers used by the smart crop and Dynamic Media dialog paths", function() {
            expect(api).toBeDefined();
            expect(typeof api.formatSmartCropOptionLabel).toBe("function");
            expect(typeof api.isDamScene7FileEligible).toBe("function");
            expect(typeof api.isImageV2AuthoringMarkupHelpersEnabled).toBe("function");
            expect(typeof api.getImageAuthoringUtils).toBe("function");
        });

        it("getImageAuthoringUtils returns AuthoringEditorUtils.image when loaded", function() {
            expect(api.getImageAuthoringUtils()).toBe(globalThis.CQ.CoreComponents.AuthoringEditorUtils.image);
        });
    });

    describe("isImageV2AuthoringMarkupHelpersEnabled (Granite toggle)", function() {
        it("treats missing Granite.Toggles as enabled", function() {
            const saved = globalThis.Granite.Toggles;
            globalThis.Granite.Toggles = undefined;
            expect(api.isImageV2AuthoringMarkupHelpersEnabled()).toBe(true);
            globalThis.Granite.Toggles = saved;
        });

        it("returns false when FT_SITES-41320 is explicitly disabled", function() {
            globalThis.Granite.Toggles.isEnabled = function(key) {
                return key !== "FT_SITES-41320";
            };
            expect(api.isImageV2AuthoringMarkupHelpersEnabled()).toBe(false);
        });

        it("returns true when FT_SITES-41320 is enabled", function() {
            imageV2EditorImageTestFtOn();
            expect(api.isImageV2AuthoringMarkupHelpersEnabled()).toBe(true);
        });
    });

    describe("formatSmartCropOptionLabel", function() {
        it("keeps legacy string behaviour when FT is off", function() {
            imageV2EditorImageTestFtOff();
            expect(api.formatSmartCropOptionLabel("a<b>c")).toBe("a<b>c");
            expect(api.formatSmartCropOptionLabel(null)).toBe("");
            expect(api.formatSmartCropOptionLabel(undefined)).toBe("");
        });

        it("delegates to AuthoringEditorUtils.image when FT is on", function() {
            imageV2EditorImageTestFtOn();
            const html = api.formatSmartCropOptionLabel("x < y");
            expect(html.indexOf("<")).toBe(-1);
        });

        it("encodes crop names that contain script-like markup when FT is on", function() {
            imageV2EditorImageTestFtOn();
            const encoded = api.formatSmartCropOptionLabel('SmartCrop<script>alert(1)</script>');
            expect(encoded.indexOf("<script>")).toBe(-1);
            expect(encoded.indexOf("SmartCrop")).not.toBe(-1);
        });

        it("encodes angle brackets for arbitrary label strings when FT is on", function() {
            imageV2EditorImageTestFtOn();
            const html = api.formatSmartCropOptionLabel("16:9 <extra>");
            expect(html.indexOf("<")).toBe(-1);
            expect(html.indexOf("16:9")).not.toBe(-1);
        });

        it("falls back when FT is on but image utils are missing", function() {
            imageV2EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = undefined;
            expect(api.formatSmartCropOptionLabel("plain")).toBe("plain");
            expect(api.formatSmartCropOptionLabel(null)).toBe("");
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });

        it("falls back when FT is on but formatPlainTextForMarkup is not a function", function() {
            imageV2EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = { isDamScene7PathEligible: imageUtils.isDamScene7PathEligible };
            expect(api.formatSmartCropOptionLabel("x")).toBe("x");
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });
    });

    describe("isDamScene7FileEligible", function() {
        it("is permissive when FT is off", function() {
            imageV2EditorImageTestFtOff();
            expect(api.isDamScene7FileEligible("javascript:alert(1)")).toBe(true);
            expect(api.isDamScene7FileEligible("data:text/html,x")).toBe(true);
        });

        it("delegates when FT is on", function() {
            imageV2EditorImageTestFtOn();
            expect(api.isDamScene7FileEligible("/content/dam/x")).toBe(true);
            expect(api.isDamScene7FileEligible("javascript:alert(1)")).toBe(false);
            expect(api.isDamScene7FileEligible("data:text/html,<x>")).toBe(false);
            expect(api.isDamScene7FileEligible("JaVaScRiPt:x")).toBe(false);
        });

        it("rejects unstable path segments when FT is on", function() {
            imageV2EditorImageTestFtOn();
            expect(api.isDamScene7FileEligible("../../etc/passwd")).toBe(false);
        });

        it("returns true when FT is on but image utils are missing", function() {
            imageV2EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = undefined;
            expect(api.isDamScene7FileEligible("javascript:x")).toBe(true);
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });

        it("returns true when FT is on but isDamScene7PathEligible is not a function", function() {
            imageV2EditorImageTestFtOn();
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = { formatPlainTextForMarkup: imageUtils.formatPlainTextForMarkup };
            expect(api.isDamScene7FileEligible("/content/dam/x")).toBe(true);
            globalThis.CQ.CoreComponents.AuthoringEditorUtils.image = imageUtils;
        });
    });

    describe("AuthoringEditorUtils.image path rules (metadata-style references)", function() {
        it("returns false when a value walks outside a stable repository path", function() {
            expect(imageUtils.isDamScene7PathEligible("../../content/usergenerated/demo/payload/asset.json?")).toBe(false);
        });
    });
});
