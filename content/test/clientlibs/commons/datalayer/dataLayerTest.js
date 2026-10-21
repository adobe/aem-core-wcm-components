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

function loadScript(src) {
    return new Promise(function(resolve, reject) {
        const s = document.createElement("script");
        s.src = src;
        s.onload = function() { resolve(s); };
        s.onerror = function() { reject(new Error("failed to load " + src)); };
        document.head.appendChild(s);
    });
}

// loads the scripts one after the other, preserving their order, and resolves with the script elements
function loadScripts(srcs) {
    return srcs.reduce(function(previous, src) {
        return previous.then(function(elements) {
            return loadScript(src).then(function(element) {
                return elements.concat(element);
            });
        });
    }, Promise.resolve([]));
}

function createComponent(id, data, clickable) {
    const el = document.createElement("div");
    el.id = id;
    el.dataset.cmpDataLayer = JSON.stringify({ [id]: data });
    if (clickable) {
        el.dataset.cmpClickable = "";
    }
    return el;
}

const ACDL = "/base/node_modules/@adobe/adobe-client-data-layer/dist/adobe-client-data-layer.min.js";
const POLYFILL = "/base/src/scripts/datalayer/v1/polyfill.js";
const DATALAYER = "/base/src/scripts/datalayer/v1/datalayer.js";

function describeDataLayerIntegration(description, scripts) {
    describe(description, function() {

        const TEASER_ID = "teaser-1234";
        const BUTTON_ID = "button-5678";
        const PAGE_ID = "page-abcd";

        let events;
        let scriptElements;

        beforeAll(async function() {
            events = [];
            delete window.adobeDataLayer;

            document.body.dataset.cmpDataLayerEnabled = "";
            const page = createComponent(PAGE_ID, { "@type": "core/wcm/components/page/v3/page", "dc:title": "Page" });
            const teaser = createComponent(TEASER_ID, { "@type": "core/wcm/components/teaser/v2/teaser", "dc:title": "Teaser" });
            const button = createComponent(BUTTON_ID, {
                "@type": "core/wcm/components/button/v2/button",
                "dc:title": "Button",
                "xdm:linkURL": "/content/foo.html"
            }, true);
            page.appendChild(teaser);
            document.body.appendChild(page);
            document.body.appendChild(button);

            scriptElements = await loadScripts(scripts);
            window.adobeDataLayer.push(function(dl) {
                dl.addEventListener("cmp:click", function(event) {
                    events.push(event);
                });
            });
        });

        afterAll(function() {
            [PAGE_ID, BUTTON_ID].forEach(function(id) {
                const el = document.getElementById(id);
                if (el) {
                    el.remove();
                }
            });
            scriptElements.forEach(function(el) {
                el.remove();
            });
            delete document.body.dataset.cmpDataLayerEnabled;
            delete window.adobeDataLayer;
        });

        it("bundles Adobe Client Data Layer 3.x", function() {
            expect(window.adobeDataLayer.version).toMatch(/^3\./);
            expect(typeof window.adobeDataLayer.getState).toBe("function");
            expect(typeof window.adobeDataLayer.addEventListener).toBe("function");
        });

        it("adds the components to the data layer state", function() {
            const state = window.adobeDataLayer.getState();
            expect(state.component[PAGE_ID]["dc:title"]).toBe("Page");
            expect(state.component[TEASER_ID]["dc:title"]).toBe("Teaser");
            expect(state.component[TEASER_ID].parentId).toBe(PAGE_ID);
            expect(state.component[BUTTON_ID]["xdm:linkURL"]).toBe("/content/foo.html");
            expect(window.adobeDataLayer.getState("component." + BUTTON_ID + ".dc:title")).toBe("Button");
        });

        it("returns a copy of the state", function() {
            const state = window.adobeDataLayer.getState();
            state.component[TEASER_ID]["dc:title"] = "Changed";
            expect(window.adobeDataLayer.getState().component[TEASER_ID]["dc:title"]).toBe("Teaser");
        });

        it("pushes the cmp:loaded event", function() {
            const loaded = window.adobeDataLayer.filter(function(item) {
                return item?.event === "cmp:loaded";
            });
            expect(loaded.length).toBe(1);
        });

        it("pushes a cmp:click event with the component path on click", function() {
            document.getElementById(BUTTON_ID).click();
            expect(events.length).toBe(1);
            expect(events[0].event).toBe("cmp:click");
            expect(events[0].eventInfo.path).toBe("component." + BUTTON_ID);
            expect(window.adobeDataLayer.getState(events[0].eventInfo.path)["dc:title"]).toBe("Button");
        });

        it("merges updates into existing component data", function() {
            window.adobeDataLayer.push({ component: { [TEASER_ID]: { "xdm:text": "Description" } } });
            const teaser = window.adobeDataLayer.getState("component." + TEASER_ID);
            expect(teaser["dc:title"]).toBe("Teaser");
            expect(teaser["xdm:text"]).toBe("Description");
        });
    });
}

describe("Test data layer integration (core.wcm.components.commons.datalayer.v1)", function() {
    // same order as rendered on the page: the core.wcm.components.commons.datalayer.acdl dependency first,
    // then the assets of the core.wcm.components.commons.datalayer.v1 clientlib (see clientlib.config.js)
    describeDataLayerIntegration("when the Adobe Client Data Layer is loaded first", [ACDL, POLYFILL, DATALAYER]);

    // the Adobe Client Data Layer may also be loaded after the components (e.g. async includes)
    describeDataLayerIntegration("when the Adobe Client Data Layer is loaded last", [POLYFILL, DATALAYER, ACDL]);
});
