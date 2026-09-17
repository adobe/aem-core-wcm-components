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

/*
 * Jest harness for v2's contentaisearch.js, covering the tab-switching and
 * cookie-persistence behavior that's new in v2 (request-ID/in-flight-guard
 * concurrency logic is already covered by contentaisearch.test.js against v1's
 * script, and v2 reuses that same logic verbatim for the shared search/gensearch
 * fetch paths - not re-tested here).
 */

"use strict";

const fs = require("fs");
const path = require("path");

const SCRIPT_PATH = path.join(
    __dirname,
    "../../../src/content/jcr_root/apps/core/wcm/components/contentaisearch/v2/contentaisearch/clientlibs/site/js/contentaisearch.js"
);

if (!fs.existsSync(SCRIPT_PATH)) {
    throw new Error("contentaisearch.js (v2) not found at " + SCRIPT_PATH + " - update SCRIPT_PATH in this test file.");
}

const RESOURCE_PATH = "/content/wknd/us/en/search/jcr:content/root/container/contentaisearch";

function renderComponentHtml(options) {
    const opts = Object.assign({ aiSearchModeEnabled: true }, options);
    const tabsMarkup = opts.aiSearchModeEnabled
        ? '<div role="tablist">' +
          '<button type="button" data-cmp-hook-contentaisearch="tabSearchResults" aria-selected="true"></button>' +
          '<button type="button" data-cmp-hook-contentaisearch="tabAiMode" aria-selected="false" tabindex="-1"></button>' +
          "</div>" +
          '<div data-cmp-hook-contentaisearch="panelAiMode" hidden>' +
          '<div data-cmp-hook-contentaisearch="summaryLoading" hidden></div>' +
          '<div data-cmp-hook-contentaisearch="summary" hidden>' +
          '<p data-cmp-hook-contentaisearch="summaryText"></p>' +
          '<ul data-cmp-hook-contentaisearch="sources"></ul>' +
          "</div>" +
          '<div data-cmp-hook-contentaisearch="error" hidden>' +
          '<p data-cmp-hook-contentaisearch="errorMessage"></p>' +
          '<button type="button" data-cmp-hook-contentaisearch="retry"></button>' +
          "</div>" +
          "</div>" +
          '<div data-cmp-hook-contentaisearch="panelSearchResults">'
        : "";
    return (
        '<section data-cmp-is="contentaisearch"' +
        ' data-cmp-resource-path="' + RESOURCE_PATH + '"' +
        // aiSearchModeEnabled is a Sightly boolean-typed attribute: real HTL output is a
        // bare attribute (present) when true and omits it entirely when false - never the
        // literal string "false". Mirroring that here is what makes this fixture actually
        // catch a regression to the old (buggy) getAttribute() === "true" read.
        (opts.aiSearchModeEnabled ? " data-cmp-ai-search-mode-enabled" : "") +
        ' data-cmp-gensearch-error-retry-visible="true"' +
        ' data-cmp-results-layout="card">' +
        '<form data-cmp-hook-contentaisearch="form">' +
        '<i data-cmp-hook-contentaisearch="icon"></i>' +
        '<input data-cmp-hook-contentaisearch="input" type="text">' +
        '<button type="button" data-cmp-hook-contentaisearch="clear" hidden></button>' +
        "</form>" +
        tabsMarkup +
        '<div data-cmp-hook-contentaisearch="resultsSection" hidden>' +
        '<button type="button" data-cmp-hook-contentaisearch="layoutCard"></button>' +
        '<button type="button" data-cmp-hook-contentaisearch="layoutList"></button>' +
        '<ul data-cmp-hook-contentaisearch="results"></ul>' +
        '<button type="button" data-cmp-hook-contentaisearch="loadMore" hidden></button>' +
        "</div>" +
        (opts.aiSearchModeEnabled ? "</div>" : "") +
        "</section>"
    );
}

function loadFreshScript() {
    jest.resetModules();
    require(SCRIPT_PATH);
    document.dispatchEvent(new Event("DOMContentLoaded", { bubbles: true, cancelable: true }));
}

describe("contentaisearch.js v2 - tab switching", () => {
    beforeEach(() => {
        document.body.innerHTML = "";
        document.cookie = "cmp-contentaisearch-tab=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
        global.fetch = jest.fn(() => new Promise(() => {}));
    });

    test("clicking AI Mode tab shows the AI panel and hides the results panel", () => {
        document.body.innerHTML = renderComponentHtml({});
        loadFreshScript();
        const tabAi = document.querySelector('[data-cmp-hook-contentaisearch="tabAiMode"]');
        const panelAi = document.querySelector('[data-cmp-hook-contentaisearch="panelAiMode"]');
        const panelResults = document.querySelector('[data-cmp-hook-contentaisearch="panelSearchResults"]');

        tabAi.click();

        expect(panelAi.hasAttribute("hidden")).toBe(false);
        expect(panelResults.hasAttribute("hidden")).toBe(true);
        expect(tabAi.getAttribute("aria-selected")).toBe("true");
    });

    test("clicking AI Mode tab writes the cookie", () => {
        document.body.innerHTML = renderComponentHtml({});
        loadFreshScript();
        document.querySelector('[data-cmp-hook-contentaisearch="tabAiMode"]').click();

        expect(document.cookie).toContain("cmp-contentaisearch-tab=ai-mode");
    });

    test("clicking Search Results tab after AI Mode writes the cookie back", () => {
        document.body.innerHTML = renderComponentHtml({});
        loadFreshScript();
        document.querySelector('[data-cmp-hook-contentaisearch="tabAiMode"]').click();
        document.querySelector('[data-cmp-hook-contentaisearch="tabSearchResults"]').click();

        expect(document.cookie).toContain("cmp-contentaisearch-tab=search-results");
    });

    test("ArrowRight on the Search Results tab moves focus and activates AI Mode", () => {
        document.body.innerHTML = renderComponentHtml({});
        loadFreshScript();
        const tabResults = document.querySelector('[data-cmp-hook-contentaisearch="tabSearchResults"]');
        const tabAi = document.querySelector('[data-cmp-hook-contentaisearch="tabAiMode"]');
        tabResults.focus();

        tabResults.dispatchEvent(new KeyboardEvent("keydown", { key: "ArrowRight", bubbles: true }));

        expect(tabAi.getAttribute("aria-selected")).toBe("true");
        expect(document.activeElement).toBe(tabAi);
    });

    test("submitting a query with AI Search Mode disabled never calls .gensearch.json", () => {
        document.body.innerHTML = renderComponentHtml({ aiSearchModeEnabled: false });
        loadFreshScript();
        const form = document.querySelector('[data-cmp-hook-contentaisearch="form"]');
        const input = document.querySelector('[data-cmp-hook-contentaisearch="input"]');
        input.value = "surfing";

        form.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));

        const calledUrls = global.fetch.mock.calls.map((call) => call[0]);
        expect(calledUrls.some((url) => url.indexOf(".gensearch.json") !== -1)).toBe(false);
        expect(calledUrls.some((url) => url.indexOf(".search.json") !== -1)).toBe(true);
    });

    test("picks up the pre-paint script's already-applied DOM state as its initial active tab", () => {
        // Simulate what the HTL's synchronous pre-paint script does before the JS
        // component initializes: it already flips aria-selected on the tabs and
        // hidden on the panels based on the cookie. The JS must read that state
        // (via _syncActiveTabFromDom) into its own _activeTab, rather than assuming
        // the default (Search Results) - otherwise a subsequent tab interaction
        // would be computed from the wrong starting point.
        const html = renderComponentHtml({}).replace(
            'data-cmp-hook-contentaisearch="tabAiMode" aria-selected="false" tabindex="-1"',
            'data-cmp-hook-contentaisearch="tabAiMode" aria-selected="true"'
        ).replace(
            'data-cmp-hook-contentaisearch="tabSearchResults" aria-selected="true"',
            'data-cmp-hook-contentaisearch="tabSearchResults" aria-selected="false" tabindex="-1"'
        ).replace(
            'data-cmp-hook-contentaisearch="panelAiMode" hidden',
            'data-cmp-hook-contentaisearch="panelAiMode"'
        ).replace(
            'data-cmp-hook-contentaisearch="panelSearchResults">',
            'data-cmp-hook-contentaisearch="panelSearchResults" hidden>'
        );
        document.body.innerHTML = html;
        loadFreshScript();

        const panelAi = document.querySelector('[data-cmp-hook-contentaisearch="panelAiMode"]');
        const panelResults = document.querySelector('[data-cmp-hook-contentaisearch="panelSearchResults"]');

        // Sanity check: the pre-paint state as rendered is still what we expect
        // before any JS-driven interaction.
        expect(panelAi.hasAttribute("hidden")).toBe(false);
        expect(panelResults.hasAttribute("hidden")).toBe(true);

        // The real assertion: if the JS's internal _activeTab wasn't synced from the
        // DOM (i.e. it still thinks Search Results is active, the default), clicking
        // the Search Results tab would be a no-op (tab === this._activeTab short-
        // circuits _activateTab) and the AI panel would incorrectly stay visible.
        document.querySelector('[data-cmp-hook-contentaisearch="tabSearchResults"]').click();

        expect(panelResults.hasAttribute("hidden")).toBe(false);
        expect(panelAi.hasAttribute("hidden")).toBe(true);
        expect(document.cookie).toContain("cmp-contentaisearch-tab=search-results");
    });

    test("submitting a query with AI Search Mode enabled calls both endpoints in parallel", () => {
        document.body.innerHTML = renderComponentHtml({});
        loadFreshScript();
        const form = document.querySelector('[data-cmp-hook-contentaisearch="form"]');
        const input = document.querySelector('[data-cmp-hook-contentaisearch="input"]');
        input.value = "surfing";

        form.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));

        const calledUrls = global.fetch.mock.calls.map((call) => call[0]);
        expect(calledUrls.some((url) => url.indexOf(".search.json") !== -1)).toBe(true);
        expect(calledUrls.some((url) => url.indexOf(".gensearch.json") !== -1)).toBe(true);
    });

    test("backspacing the query down to empty clears already-rendered results and summary", async () => {
        document.body.innerHTML = renderComponentHtml({});
        global.fetch = jest.fn((url) => {
            const data = url.indexOf(".gensearch.json") !== -1
                ? { result: "Adventures include hiking and skiing.", hits: [] }
                : { results: [{ id: "1", data: { title: "Adventure" } }], hasMore: false, sourceCursors: {} };
            return Promise.resolve({ ok: true, status: 200, json: () => Promise.resolve(data) });
        });
        loadFreshScript();
        const form = document.querySelector('[data-cmp-hook-contentaisearch="form"]');
        const input = document.querySelector('[data-cmp-hook-contentaisearch="input"]');
        const resultsSection = document.querySelector('[data-cmp-hook-contentaisearch="resultsSection"]');
        const summary = document.querySelector('[data-cmp-hook-contentaisearch="summary"]');

        input.value = "adventure";
        form.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
        // Let the fetch .then() chains settle, including _hideSummaryLoading's
        // minimum-visible-time setTimeout.
        jest.useFakeTimers();
        await jest.runAllTimersAsync();
        jest.useRealTimers();

        expect(resultsSection.hasAttribute("hidden")).toBe(false);
        expect(summary.hasAttribute("hidden")).toBe(false);

        // Simulate the user backspacing the field to empty - no submit, no clear button.
        input.value = "";
        input.dispatchEvent(new Event("input", { bubbles: true }));

        expect(resultsSection.hasAttribute("hidden")).toBe(true);
        expect(summary.hasAttribute("hidden")).toBe(true);
        expect(document.querySelector('[data-cmp-hook-contentaisearch="results"]').innerHTML).toBe("");
    });
});
