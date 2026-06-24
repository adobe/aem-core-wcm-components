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
 * Covers Content Fragment List v1 editor {@code contentfragmentlist.js} (CT_SITES-41317) wired through
 * {@code globalThis.__CONTENTFRAGMENTLIST_V1_EDITOR_TEST_API}. Karma loads {@code authoringutils} before that script.
 */
function contentfragmentlistEditorTestToggleOn() {
    globalThis.Granite.Toggles.isEnabled = function() {
        return true;
    };
}

function contentfragmentlistEditorTestToggleOff() {
    globalThis.Granite.Toggles.isEnabled = function(key) {
        return key !== "CT_SITES-41317";
    };
}

describe("Content Fragment List v1 editor contentfragmentlist.js (Karma-loaded)", function() {
    let api;
    let togglesIsEnabled;

    beforeAll(function() {
        api = globalThis.__CONTENTFRAGMENTLIST_V1_EDITOR_TEST_API;
        togglesIsEnabled = globalThis.Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        globalThis.Granite.Toggles.isEnabled = togglesIsEnabled;
    });

    describe("__CONTENTFRAGMENTLIST_V1_EDITOR_TEST_API", function() {
        it("exposes resolve, toggle, and datasource path helpers", function() {
            expect(api).toBeDefined();
            expect(typeof api.resolveElementNamesContainerInnerHtmlForEditor).toBe("function");
            expect(typeof api.isContentFragmentListV1EditorMarkupHelpersEnabled).toBe("function");
            expect(typeof api.getAuthoringMarkupUtils).toBe("function");
            expect(typeof api.isSameOriginDatasourcePath).toBe("function");
        });
    });

    describe("isContentFragmentListV1EditorMarkupHelpersEnabled", function() {
        it("treats missing Granite.Toggles as enabled", function() {
            const saved = globalThis.Granite.Toggles;
            globalThis.Granite.Toggles = undefined;
            expect(api.isContentFragmentListV1EditorMarkupHelpersEnabled()).toBe(true);
            globalThis.Granite.Toggles = saved;
        });

        it("returns false when CT_SITES-41317 is explicitly disabled", function() {
            contentfragmentlistEditorTestToggleOff();
            expect(api.isContentFragmentListV1EditorMarkupHelpersEnabled()).toBe(false);
        });

        it("returns true when CT_SITES-41317 is enabled", function() {
            contentfragmentlistEditorTestToggleOn();
            expect(api.isContentFragmentListV1EditorMarkupHelpersEnabled()).toBe(true);
        });
    });

    describe("resolveElementNamesContainerInnerHtmlForEditor", function() {
        it("normalises markup when helpers are enabled", function() {
            contentfragmentlistEditorTestToggleOn();
            const doc =
                "<div><div data-granite-coral-multifield-name=\"./elementNames\"><img src=\"x\" onerror=\"alert(1)\"></div></div>";
            const inner = api.resolveElementNamesContainerInnerHtmlForEditor(doc);
            expect(inner.indexOf("onerror")).toBe(-1);
        });

        it("keeps legacy extraction when helpers are disabled", function() {
            contentfragmentlistEditorTestToggleOff();
            const doc =
                "<div><div data-granite-coral-multifield-name=\"./elementNames\"><img src=\"x\" onerror=\"alert(1)\"></div></div>";
            const inner = api.resolveElementNamesContainerInnerHtmlForEditor(doc);
            expect(inner.indexOf("onerror")).not.toBe(-1);
        });
    });

    describe("isSameOriginDatasourcePath", function() {
        it("returns false for nullish or empty values", function() {
            expect(api.isSameOriginDatasourcePath(null)).toBe(false);
            expect(api.isSameOriginDatasourcePath(undefined)).toBe(false);
            expect(api.isSameOriginDatasourcePath("")).toBe(false);
            expect(api.isSameOriginDatasourcePath("   ")).toBe(false);
        });

        it("returns true for repository-relative paths on the current origin", function() {
            expect(api.isSameOriginDatasourcePath("/content/dam/models")).toBe(true);
            expect(api.isSameOriginDatasourcePath("/conf/test/settings/dam/cfm/models/items")).toBe(true);
        });

        it("returns true for absolute URLs on the current page origin", function() {
            const originPath = globalThis.location.origin + "/libs/datasource.html";
            expect(api.isSameOriginDatasourcePath(originPath)).toBe(true);
        });

        it("returns false for disallowed schemes", function() {
            expect(api.isSameOriginDatasourcePath("javascript:alert(1)")).toBe(false);
            expect(api.isSameOriginDatasourcePath("data:text/html,x")).toBe(false);
            expect(api.isSameOriginDatasourcePath("vbscript:msgbox")).toBe(false);
        });

        it("returns false when scheme is split with whitespace before prefix checks", function() {
            expect(api.isSameOriginDatasourcePath("java\tscript:alert(1)")).toBe(false);
        });

        it("returns false for other origins", function() {
            expect(api.isSameOriginDatasourcePath("https://example.com/libs/datasource.html")).toBe(false);
        });
    });
});
