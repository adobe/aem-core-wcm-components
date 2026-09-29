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
describe("Panel Selector getTitle", function() {

    const PN_PANEL_TITLE = "cq:panelTitle";
    let getTitle;
    let savedIsEnabled;

    beforeAll(function() {
        getTitle = CQ.CoreComponents.PanelSelector.prototype.getTitle;
    });

    beforeEach(function() {
        savedIsEnabled = Granite.Toggles.isEnabled;
    });

    afterEach(function() {
        Granite.Toggles.isEnabled = savedIsEnabled;
    });

    it("encodes the authored panel title before building the selector markup", function() {
        // CT_SITES-49051 gates the sanitization; force it enabled (mock default is off).
        Granite.Toggles.isEnabled = function() {
            return true;
        };

        const item = {};
        item[PN_PANEL_TITLE] = "</span><img src=\"x\" onerror=\"window.__panelHandlerFired = true\">";

        const title = getTitle({ displayName: "Accordion" }, item, 1);

        expect(title).not.toContain("<img");
        expect(title).not.toContain("<span class='foundation-layout-util-subtletext'></span>");
        expect(title).toContain("&lt;");

        // Rendering the title as markup must not create an executable image element.
        window.__panelHandlerFired = false;
        const host = document.createElement("div");
        host.innerHTML = title;
        expect(host.querySelector("img")).toBeNull();
        expect(window.__panelHandlerFired).toBe(false);
        delete window.__panelHandlerFired;
    });

    it("returns the raw authored title when sanitization is disabled", function() {
        Granite.Toggles.isEnabled = function(key) {
            return key !== "CT_SITES-49051";
        };

        const item = {};
        item[PN_PANEL_TITLE] = "<b>My Panel</b>";

        const title = getTitle({ displayName: "Accordion" }, item, 1);

        expect(title).toContain("<b>My Panel</b>");
    });

});
