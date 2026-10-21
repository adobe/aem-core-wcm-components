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

package com.adobe.cq.wcm.core.components.it.pw;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.microsoft.playwright.JSHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.LoadState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Helpers to use the page editor side panel (assets tab) through the UI. */
public final class SidePanelPw {

    private static final String PANEL = "#SidePanel";
    private static final String TOGGLE = "#Content button.toggle-sidepanel";

    private final Page page;

    public SidePanelPw(Page page) {
        this.page = page;
    }

    private boolean isOpen() {
        return (Boolean) page.locator(PANEL).evaluate("el => el.classList.contains('sidepanel-opened')");
    }

    /** Opens the side panel (if closed) and makes sure its Assets tab is the active one. */
    public void open() {
        assertThat(page.locator(PANEL)).isAttached();
        if (!isOpen()) {
            page.locator(TOGGLE).first().click();
            assertThat(page.locator(PANEL)).hasClass(Pattern.compile("sidepanel-opened"));
            waitForPanelAnimation();
        }
        Locator filters = page.locator(PANEL + " [name^='assetfilter_']").first();
        try {
            filters.waitFor(new Locator.WaitForOptions().setTimeout(3_000));
        } catch (TimeoutError e) {
            // another content tab (e.g. Components) was remembered as active
            page.locator(PANEL + " coral-tab[data-foundation-tracking-event*='assets' i]").first().click();
        }
        assertThat(filters).isVisible();
    }

    public void close() {
        if (isOpen()) {
            page.locator(TOGGLE).first().click();
            assertThat(page.locator(PANEL)).hasClass(Pattern.compile("sidepanel-closed"));
            waitForPanelAnimation();
        }
    }

    /** The panel slides in and out and the editor content is resized, so wait until its position is stable. */
    private void waitForPanelAnimation() {
        page.locator(PANEL).evaluate("el => new Promise(resolve => { let last = null; let same = 0;"
            + " const tick = () => { const x = el.getBoundingClientRect().left; same = x === last ? same + 1 : 0;"
            + " last = x; if (same >= 10) { resolve(true); } else { requestAnimationFrame(tick); } }; tick(); })");
    }

    /** Filters the assets list by a folder path using the given path autocomplete of the asset filter. */
    public void filterByPath(String filterName, String path) {
        Locator autocomplete = page.locator(PANEL + " foundation-autocomplete[name='" + filterName + "']").first();
        Locator input = autocomplete.locator("input[is='coral-textfield']").first();
        input.fill(path);
        Locator suggestion = autocomplete.locator("[is='coral-buttonlist-item'][value='" + path + "'], coral-overlay button[value='"
            + path + "']").first();
        assertThat(suggestion).isVisible();
        suggestion.click();
        assertThat(suggestion).isHidden();
        // the path field keeps the focus after picking a suggestion and only commits on blur, which reloads (and
        // scrolls) the asset list; commit it now, otherwise the mouse-down of a later drag triggers the reload and
        // the drag picks up whichever card ends up under the pointer
        input.blur();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /**
     * Drags the asset card onto a dialog file upload and waits until the dialog took the asset over as its
     * {@code fileReference}, retrying the gesture as it is occasionally ignored.
     */
    public void dragAssetToFileUpload(Locator dialog, Locator fileUpload, String assetPath) {
        Locator reference = dialog.locator("input[name='./fileReference']");
        for (int attempt = 1; ; attempt++) {
            fileUpload.scrollIntoViewIfNeeded();
            dragAssetTo(assetPath, fileUpload);
            try {
                assertThat(reference).hasValue(assetPath, new LocatorAssertions.HasValueOptions().setTimeout(5_000));
                return;
            } catch (AssertionError e) {
                if (attempt == 3) {
                    throw e;
                }
            }
        }
    }

    /** Drags the asset card onto the editable's overlay in the editor. */
    public void dragAssetToComponent(String assetPath, String componentPath) {
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + componentPath + "']");
        overlay.scrollIntoViewIfNeeded();
        dragAssetTo(assetPath, overlay);
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
        // the asset list is reloaded after the path filter is applied
        assertThat(card).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(15_000));
        // the card list can still shift (e.g. thumbnails loading) or be re-rendered (detaching the card) after the card
        // is visible, so re-query the card on every animation frame, scroll it into view when it does not hit-test,
        // and only press once its centre is stable across two frames and hit-tests to this card, so the drag cannot
        // pick up a neighbouring asset
        JSHandle point = page.waitForFunction("p => {"
            + " const card = document.querySelector('" + PANEL + " coral-card.cq-draggable[data-path=\"' + p + '\"]');"
            + " if (!card) { return false; }"
            + " const r = card.getBoundingClientRect();"
            + " const x = r.x + r.width / 2, y = r.y + r.height / 2;"
            + " const hit = document.elementFromPoint(x, y);"
            + " if (!hit || hit.closest('coral-card') !== card) {"
            + " window.__pwDragPoint = null; card.scrollIntoView({ block: 'center', inline: 'nearest' }); return false; }"
            + " const key = x + ',' + y + ',' + p, stable = window.__pwDragPoint === key;"
            + " window.__pwDragPoint = key;"
            + " return stable ? { x, y } : false;"
            + " }", assetPath, new Page.WaitForFunctionOptions().setTimeout(15_000));
        double startX = ((Number) point.evaluate("p => p.x")).doubleValue();
        double startY = ((Number) point.evaluate("p => p.y")).doubleValue();
        BoundingBox to = target.boundingBox();
        double endX = to.x + to.width / 2;
        double endY = to.y + to.height / 2;
        page.mouse().move(startX, startY);
        page.mouse().down();
        // the drag has to start with a small movement, otherwise the draggable does not enter its drag mode
        page.mouse().move(startX + 5, startY + 5);
        page.waitForTimeout(100);
        for (int step = 1; step <= 20; step++) {
            page.mouse().move(startX + (endX - startX) * step / 20.0, startY + (endY - startY) * step / 20.0);
            page.waitForTimeout(30);
        }
        page.waitForTimeout(200);
        page.mouse().up();
    }
}
