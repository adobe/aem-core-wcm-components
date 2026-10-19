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
 * Standalone Jest harness for contentaisearch.js, targeting the request-ID/
 * in-flight-guard concurrency logic specifically - the riskiest code in the
 * component and, until this harness, entirely untested (the project's main
 * JS suite is Karma/Jasmine for editor-side scripts; this component's
 * site-facing script had no coverage at all). Kept deliberately separate
 * from content/package.json and karma.conf.js - not wired into the Maven
 * build or CI, run locally on demand (npm install && npm test).
 *
 * The script is a self-executing IIFE with no exports: it scans the DOM for
 * "[data-cmp-is='contentaisearch']" and wires up a ContentAISearch instance
 * per match as soon as it loads. So each test builds the markup fresh, resets
 * the module registry, and re-requires the script so it (re)discovers the
 * current DOM.
 */

"use strict";

const fs = require("fs");
const path = require("path");

const SCRIPT_PATH = path.join(
    __dirname,
    "../../../src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/js/contentaisearch.js"
);

// Fail fast and clearly if the relative path above ever drifts instead of
// letting Jest's own "Cannot find module" error obscure why.
if (!fs.existsSync(SCRIPT_PATH)) {
    throw new Error("contentaisearch.js not found at " + SCRIPT_PATH + " - update SCRIPT_PATH in this test file.");
}

const RESOURCE_PATH = "/content/wknd/us/en/search/jcr:content/root/container/contentaisearch";

function renderComponentHtml(options) {
    const opts = Object.assign(
        {
            genSearchToggleVisible: false,
            genSearchEnabledDefault: true
        },
        options
    );
    const toggleMarkup = opts.genSearchToggleVisible
        ? '<input type="checkbox" data-cmp-hook-contentaisearch="toggle"' +
          (opts.genSearchEnabledDefault ? " checked" : "") +
          ">"
        : "";
    return (
        '<section data-cmp-is="contentaisearch"' +
        ' data-cmp-resource-path="' + RESOURCE_PATH + '"' +
        ' data-cmp-gensearch-enabled-default="' + String(opts.genSearchEnabledDefault) + '"' +
        ' data-cmp-gensearch-toggle-visible="' + String(opts.genSearchToggleVisible) + '"' +
        ' data-cmp-gensearch-error-fallback="RESULTS_ONLY"' +
        ' data-cmp-results-layout="card">' +
        '<form data-cmp-hook-contentaisearch="form">' +
        '<i data-cmp-hook-contentaisearch="icon"></i>' +
        '<span data-cmp-hook-contentaisearch="loadingIndicator" hidden></span>' +
        '<input data-cmp-hook-contentaisearch="input" type="text">' +
        '<button type="button" data-cmp-hook-contentaisearch="clear" hidden></button>' +
        toggleMarkup +
        "</form>" +
        '<div data-cmp-hook-contentaisearch="summaryLoading" hidden></div>' +
        '<div data-cmp-hook-contentaisearch="summary" hidden>' +
        '<p data-cmp-hook-contentaisearch="summaryText"></p>' +
        '<ul data-cmp-hook-contentaisearch="sources"></ul>' +
        "</div>" +
        '<div data-cmp-hook-contentaisearch="error" hidden>' +
        '<p data-cmp-hook-contentaisearch="errorMessage"></p>' +
        '<button type="button" data-cmp-hook-contentaisearch="retry"></button>' +
        "</div>" +
        '<div data-cmp-hook-contentaisearch="resultsSection" hidden>' +
        '<button type="button" data-cmp-hook-contentaisearch="layoutCard"></button>' +
        '<button type="button" data-cmp-hook-contentaisearch="layoutList"></button>' +
        '<ul data-cmp-hook-contentaisearch="results"></ul>' +
        '<button type="button" data-cmp-hook-contentaisearch="loadMore" hidden></button>' +
        "</div>" +
        '<script data-cmp-hook-contentaisearch="itemTemplateCard" type="x-template">' +
        '<li><a data-cmp-hook-contentaisearch="item" href="#">' +
        '<img data-cmp-hook-contentaisearch="itemImage" hidden>' +
        '<div data-cmp-hook-contentaisearch="itemImagePlaceholder"></div>' +
        '<h3 data-cmp-hook-contentaisearch="itemTitle"></h3>' +
        '<p data-cmp-hook-contentaisearch="itemDescription" hidden></p>' +
        "</a></li>" +
        "</script>" +
        '<script data-cmp-hook-contentaisearch="itemTemplateList" type="x-template">' +
        '<li><a data-cmp-hook-contentaisearch="item" href="#">' +
        '<img data-cmp-hook-contentaisearch="itemImage" hidden>' +
        '<div data-cmp-hook-contentaisearch="itemImagePlaceholder"></div>' +
        '<h3 data-cmp-hook-contentaisearch="itemTitle"></h3>' +
        '<p data-cmp-hook-contentaisearch="itemDescription" hidden></p>' +
        "</a></li>" +
        "</script>" +
        '<script data-cmp-hook-contentaisearch="sourceTemplate" type="x-template">' +
        '<li><a data-cmp-hook-contentaisearch="sourceLink" href="#" hidden></a>' +
        '<span data-cmp-hook-contentaisearch="sourceText" hidden></span></li>' +
        "</script>" +
        "</section>"
    );
}

function loadComponent(options) {
    document.body.innerHTML = renderComponentHtml(options);
    jest.resetModules();
    require(SCRIPT_PATH);
    return {
        root: document.querySelector('[data-cmp-is="contentaisearch"]'),
        input: document.querySelector('[data-cmp-hook-contentaisearch="input"]'),
        form: document.querySelector('[data-cmp-hook-contentaisearch="form"]'),
        toggle: document.querySelector('[data-cmp-hook-contentaisearch="toggle"]'),
        loadMore: document.querySelector('[data-cmp-hook-contentaisearch="loadMore"]'),
        summary: document.querySelector('[data-cmp-hook-contentaisearch="summary"]'),
        results: document.querySelector('[data-cmp-hook-contentaisearch="results"]')
    };
}

function submit(form, input, query) {
    input.value = query;
    form.dispatchEvent(new Event("submit", { bubbles: true, cancelable: true }));
}

// --- Controllable fetch mock -----------------------------------------------

let pendingFetches;

function installFetchMock() {
    pendingFetches = [];
    global.fetch = jest.fn(function(url) {
        return new Promise(function(resolve, reject) {
            pendingFetches.push({ url: String(url), resolve: resolve, reject: reject });
        });
    });
}

function pendingFetchesMatching(urlSubstring) {
    return pendingFetches.filter(function(entry) {
        return entry.url.indexOf(urlSubstring) !== -1;
    });
}

function resolveFetch(urlSubstring, data) {
    const index = pendingFetches.findIndex(function(entry) {
        return entry.url.indexOf(urlSubstring) !== -1;
    });
    if (index === -1) {
        throw new Error(
            'No pending fetch matching "' + urlSubstring + '". Pending: ' +
            pendingFetches.map(function(entry) { return entry.url; }).join(", ")
        );
    }
    const entry = pendingFetches.splice(index, 1)[0];
    entry.resolve({
        ok: true,
        status: 200,
        json: function() { return Promise.resolve(data); }
    });
}

async function flush() {
    await jest.runAllTimersAsync();
}

beforeEach(function() {
    jest.useFakeTimers();
    installFetchMock();
});

afterEach(function() {
    jest.useRealTimers();
    document.body.innerHTML = "";
});

describe("contentaisearch.js request handling", function() {
    test("a normal submit fires exactly one search + one gensearch request and renders the answer", async function() {
        const ui = loadComponent({});

        submit(ui.form, ui.input, "adventure");

        expect(global.fetch).toHaveBeenCalledTimes(2);
        expect(pendingFetchesMatching(".search.json").length).toBe(1);
        expect(pendingFetchesMatching(".gensearch.json").length).toBe(1);

        resolveFetch(".search.json", { results: [], hasMore: false, sourceCursors: {} });
        resolveFetch(".gensearch.json", { result: "Adventures include hiking and skiing.", hits: [] });
        await flush();

        expect(ui.summary.hasAttribute("hidden")).toBe(false);
        expect(ui.summary.querySelector('[data-cmp-hook-contentaisearch="summaryText"]').textContent)
            .toContain("Adventures include hiking and skiing.");
    });

    test("submitting the identical query twice while the first is still in flight does not fire a second request pair", async function() {
        const ui = loadComponent({});

        submit(ui.form, ui.input, "adventure");
        expect(global.fetch).toHaveBeenCalledTimes(2);

        // Same query, submitted again before either request has resolved.
        submit(ui.form, ui.input, "adventure");
        expect(global.fetch).toHaveBeenCalledTimes(2);

        resolveFetch(".search.json", { results: [], hasMore: false, sourceCursors: {} });
        resolveFetch(".gensearch.json", { result: "Answer one.", hits: [] });
        await flush();

        // Once the first pair has fully settled, a fresh submit of the SAME
        // query is a genuinely new request, not a duplicate.
        submit(ui.form, ui.input, "adventure");
        expect(global.fetch).toHaveBeenCalledTimes(4);

        // A different query is never treated as a duplicate, even while one
        // is still in flight.
        resolveFetch(".search.json", { results: [], hasMore: false, sourceCursors: {} });
        resolveFetch(".gensearch.json", { result: "Answer two.", hits: [] });
        await flush();
        submit(ui.form, ui.input, "surfing");
        expect(global.fetch).toHaveBeenCalledTimes(6);
    });

    test("turning the AI-answer toggle off invalidates an in-flight gensearch response instead of letting it reopen the summary", async function() {
        const ui = loadComponent({ genSearchToggleVisible: true, genSearchEnabledDefault: true });

        submit(ui.form, ui.input, "adventure");
        resolveFetch(".search.json", { results: [], hasMore: false, sourceCursors: {} });
        await flush();

        // gensearch is still in flight at this point.
        expect(pendingFetchesMatching(".gensearch.json").length).toBe(1);

        ui.toggle.checked = false;
        ui.toggle.dispatchEvent(new Event("change", { bubbles: true }));
        expect(ui.summary.hasAttribute("hidden")).toBe(true);

        // The in-flight request now resolves - late, after the user already
        // turned answers off.
        resolveFetch(".gensearch.json", { result: "A late answer.", hits: [] });
        await flush();

        expect(ui.summary.hasAttribute("hidden")).toBe(true);
        expect(ui.summary.querySelector('[data-cmp-hook-contentaisearch="summaryText"]').textContent)
            .not.toContain("A late answer.");
    });

    test("Load More failing does not permanently disable the button", async function() {
        const ui = loadComponent({});

        submit(ui.form, ui.input, "adventure");
        resolveFetch(".search.json", {
            results: [{ id: "1", score: 1, data: {} }],
            hasMore: true,
            sourceCursors: { "my-source": "cursor-1" }
        });
        resolveFetch(".gensearch.json", { result: "", hits: [] });
        await flush();

        expect(ui.loadMore.hasAttribute("hidden")).toBe(false);

        ui.loadMore.dispatchEvent(new Event("click", { bubbles: true }));
        expect(ui.loadMore.disabled).toBe(true);

        const loadMoreCall = pendingFetchesMatching("cursors=");
        expect(loadMoreCall.length).toBe(1);
        loadMoreCall[0].reject(new Error("network down"));
        await flush();

        expect(ui.loadMore.disabled).toBe(false);
    });

    test("a fresh search issued while a Load More append is still in flight wins - the stale append is ignored", async function() {
        const ui = loadComponent({});

        submit(ui.form, ui.input, "adventure");
        resolveFetch(".search.json", {
            results: [{ id: "1", score: 1, data: {} }],
            hasMore: true,
            sourceCursors: { "my-source": "cursor-1" }
        });
        resolveFetch(".gensearch.json", { result: "", hits: [] });
        await flush();

        ui.loadMore.dispatchEvent(new Event("click", { bubbles: true }));
        expect(pendingFetchesMatching("cursors=").length).toBe(1);

        // A fresh submit for a different query while that append is still
        // pending - this should "win": its own request supersedes the
        // load-more append, which is now stale.
        submit(ui.form, ui.input, "surfing");
        const freshSearch = pendingFetchesMatching(".search.json").filter(function(entry) {
            return entry.url.indexOf("cursors=") === -1;
        });
        expect(freshSearch.length).toBe(1);

        resolveFetch(".gensearch.json", { result: "", hits: [] });
        resolveFetch("q=surfing", { results: [{ id: "2", score: 1, data: {} }], hasMore: false, sourceCursors: {} });
        await flush();

        // The stale load-more response for "adventure" arrives last.
        resolveFetch("cursors=", {
            results: [{ id: "3", score: 1, data: {} }],
            hasMore: false,
            sourceCursors: {}
        });
        await flush();

        // Only "surfing"'s single result should be present - the late,
        // superseded load-more append must not have been applied.
        expect(ui.results.querySelectorAll("li").length).toBe(1);
    });
});

describe("contentaisearch.js result images, dedup and top-up", function() {
    function result(id, score, url, extra) {
        return {
            id: id,
            score: score,
            data: Object.assign({ title: "T" + id, metadata: Object.assign({ url: url }, extra) })
        };
    }

    test("drops duplicate pages, tops up to the page size and falls back to metadata.meta.image", async function() {
        const ui = loadComponent({});

        submit(ui.form, ui.input, "surf");
        resolveFetch(".search.json", {
            results: [
                result("1", 0.9, "https://x.test/a.html", { meta: { image: "https://x.test/a.jpg" } }),
                result("2", 0.8, "https://x.test/a.html", { image: "https://x.test/a.jpg" }),
                result("3", 0.7, "https://x.test/b.html", { image: "https://x.test/b.jpg" })
            ],
            hasMore: true,
            sourceCursors: { s: "c1" }
        });
        resolveFetch(".gensearch.json", { result: "", hits: [] });
        await flush();

        // The raw page had 3 results; 2 survive dedup, so one top-up request is issued.
        expect(pendingFetchesMatching("cursors=").length).toBe(1);
        resolveFetch("cursors=", {
            results: [result("4", 0.6, "https://x.test/c.html", { image: "https://x.test/c.jpg" })],
            hasMore: false,
            sourceCursors: {}
        });
        await flush();

        const images = Array.from(ui.results.querySelectorAll("img")).map(function(img) {
            return img.getAttribute("src");
        });
        expect(images).toEqual(["https://x.test/a.jpg", "https://x.test/b.jpg", "https://x.test/c.jpg"]);
    });
});
