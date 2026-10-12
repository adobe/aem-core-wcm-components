/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.adobe.cq.wcm.core.components.it.pw.download;

import java.util.Arrays;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Helpers to use the page editor side panel (assets tab) through the UI. */
public final class SidePanelPw {

    private static final String PANEL = "#SidePanel";
    private static final String TOGGLE = "button.toggle-sidepanel, button.js-editor-SidePanel-toggle";

    private final Page page;

    public SidePanelPw(Page page) {
        this.page = page;
    }

    private boolean isOpen() {
        return (Boolean) page.locator(PANEL).evaluate(
            "el => { const r = el.getBoundingClientRect(); return r.width > 0 && r.right > 0 && r.left < window.innerWidth; }");
    }

    public void open() {
        assertThat(page.locator(PANEL)).isAttached();
        if (!isOpen()) {
            page.locator(TOGGLE).first().click();
            assertThat(page.locator(PANEL + " coral-card, " + PANEL + " [name^='assetfilter_']").first()).isVisible();
        }
    }

    /** Filters the assets list by a folder path using the given path autocomplete of the asset filter. */
    public void filterByPath(String filterName, String path) {
        Locator autocomplete = page.locator(PANEL + " foundation-autocomplete[name='" + filterName + "']").first();
        Locator input = autocomplete.locator("input[is='coral-textfield']").first();
        input.fill(path);
        Locator suggestion = autocomplete.locator("coral-overlay button[value='" + path + "']").first();
        assertThat(suggestion).isVisible();
        suggestion.click();
    }

    /** Selects the asset type in the side panel type selector, e.g. "Documents". */
    public void selectType(String selectName, String value) {
        Locator button = page.locator(PANEL + " coral-select[name='" + selectName + "'] > button").first();
        button.click();
        Locator list = page.locator("[id='" + button.getAttribute("aria-controls") + "']");
        assertThat(list).isVisible();
        list.locator("coral-selectlist-item[value='" + value + "']").click();
        page.waitForFunction("([s, v]) => { const e = document.querySelector(s); return !!e && e.value === v; }",
            Arrays.asList(PANEL + " coral-select[name='" + selectName + "']", value));
    }

    public Locator card(String assetPath) {
        return page.locator(PANEL + " coral-card.cq-draggable[data-path=\"" + assetPath + "\"]");
    }

    /** Drags the asset card onto the target using native mouse events, as the editor drag and drop ignores dragTo. */
    public void dragAssetTo(String assetPath, Locator target) {
        Locator card = card(assetPath);
        assertThat(card).isVisible();
        card.scrollIntoViewIfNeeded();
        BoundingBox from = card.boundingBox();
        BoundingBox to = target.boundingBox();
        double startX = from.x + from.width / 2;
        double startY = from.y + from.height / 2;
        double endX = to.x + to.width / 2;
        double endY = to.y + to.height / 2;
        page.mouse().move(startX, startY);
        page.mouse().down();
        page.mouse().move(startX + 5, startY + 5);
        for (int step = 1; step <= 20; step++) {
            page.mouse().move(startX + (endX - startX) * step / 20.0, startY + (endY - startY) * step / 20.0);
        }
        page.mouse().up();
    }
}
