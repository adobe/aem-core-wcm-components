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
 * Covers commons {@code htmlIdValidation.js} editor helpers wired for Karma and
 * {@code AuthoringEditorUtils.htmlId} (authoringutils loads before the validator script).
 */
function htmlIdValidatorEditorTestFtOn() {
    globalThis.Granite.Toggles.isEnabled = function() {
        return true;
    };
}

function htmlIdValidatorEditorTestFtOff() {
    globalThis.Granite.Toggles.isEnabled = function(key) {
        return key !== "CT_SITES-41942";
    };
}

describe("HTML ID validator editor htmlIdValidation.js (Karma-loaded)", function() {
    let api;
    let htmlId;
    let togglesIsEnabled;

    beforeAll(function() {
        api = globalThis.__HTML_ID_VALIDATOR_EDITOR_TEST_API;
        htmlId = globalThis.CQ.CoreComponents.AuthoringEditorUtils.htmlId;
        togglesIsEnabled = globalThis.Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        globalThis.Granite.Toggles.isEnabled = togglesIsEnabled;
    });

    describe("__HTML_ID_VALIDATOR_EDITOR_TEST_API", function() {
        it("exposes preview helper accessors and the field validator", function() {
            expect(api).toBeDefined();
            expect(typeof api.isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled).toBe("function");
            expect(typeof api.getAuthoringHtmlIdUtils).toBe("function");
            expect(typeof api.getHtmlUniqueIdFieldValidator).toBe("function");
            expect(api.getHtmlUniqueIdFieldValidator().selector).toBe("[data-validation=html-unique-id-validator]");
        });
    });

    describe("isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled (Granite toggle)", function() {
        it("treats missing Granite.Toggles as enabled", function() {
            const saved = globalThis.Granite.Toggles;
            globalThis.Granite.Toggles = undefined;
            expect(api.isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled()).toBe(true);
            globalThis.Granite.Toggles = saved;
        });

        it("returns false when CT_SITES-41942 is explicitly disabled", function() {
            htmlIdValidatorEditorTestFtOff();
            expect(api.isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled()).toBe(false);
        });

        it("returns true when CT_SITES-41942 is enabled", function() {
            htmlIdValidatorEditorTestFtOn();
            expect(api.isHtmlIdValidatorAuthoringPagePreviewHelpersEnabled()).toBe(true);
        });
    });

    describe("AuthoringEditorUtils.htmlId.extractAuthoringPagePathFromComponentFormAction", function() {
        it("returns null when the action has no authored content segment", function() {
            expect(htmlId.extractAuthoringPagePathFromComponentFormAction("/content/x/payload/res")).toBe(null);
        });

        it("returns the page prefix for _jcr_content actions", function() {
            expect(
                htmlId.extractAuthoringPagePathFromComponentFormAction("/content/site/en/page/_jcr_content/root/c")
            ).toBe("/content/site/en/page");
        });

        it("returns the page prefix for jcr:content actions", function() {
            expect(
                htmlId.extractAuthoringPagePathFromComponentFormAction("/content/site/en/page/jcr:content/root/c")
            ).toBe("/content/site/en/page");
        });
    });

    describe("AuthoringEditorUtils.htmlId.countElementsWithIdInAuthoringFetchedHtml", function() {
        it("counts matching ids after normalising active markup in the document", function() {
            const doc =
                "<!DOCTYPE html><html><body><div id=\"keep\"></div><img src=\"x\" onerror=\"void(0)\"></body></html>";
            expect(htmlId.countElementsWithIdInAuthoringFetchedHtml(doc, "keep")).toBe(1);
        });

        it("returns zero for empty markup or id", function() {
            expect(htmlId.countElementsWithIdInAuthoringFetchedHtml("", "a")).toBe(0);
            expect(htmlId.countElementsWithIdInAuthoringFetchedHtml("<html><body></body></html>", "")).toBe(0);
            expect(htmlId.countElementsWithIdInAuthoringFetchedHtml("<html><body></body></html>", null)).toBe(0);
        });
    });

    describe("getAuthoringHtmlIdUtils", function() {
        it("returns the commons htmlId helper object when loaded", function() {
            expect(api.getAuthoringHtmlIdUtils()).toBe(htmlId);
        });
    });
});
