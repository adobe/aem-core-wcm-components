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
package com.adobe.cq.wcm.core.components.it.pw.image;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.BoundingBox;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright counterpart of the Selenium {@code Commons.openSidePanel / closeSidePanel / dragSidePanelImageToComponent},
 * {@code AssetFinder.setFiltersPath} and {@code ImageEditDialog.uploadImageFromSidePanel} helpers, plus the page
 * properties featured-image flow. Shared by the image and teaser tests.
 */
public final class ImageSidePanelHelper {

    public static final String TEST_ASSETS_PATH = "/content/dam/core-components";

    private static final String SIDE_PANEL = "#SidePanel";
    private static final String TOGGLE_BUTTON = "button.toggle-sidepanel";
    private static final String ASSET_FILTER = "foundation-autocomplete[name='assetfilter_image_path']";

    private ImageSidePanelHelper() {
    }

    private static boolean isSidePanelOpen(Page page) {
        Locator panel = page.locator(SIDE_PANEL);
        return panel.count() > 0 && (Boolean) panel.evaluate(
            "el => { const r = el.getBoundingClientRect(); return r.width > 0 && r.right > 10 && r.left < window.innerWidth; }");
    }

    /** Opens the editor side panel (if closed) and makes sure its Assets tab is the active one. */
    public static void openSidePanel(Page page) {
        if (!isSidePanelOpen(page)) {
            page.locator(TOGGLE_BUTTON).first().click();
            page.waitForFunction("() => { const el = document.querySelector('" + SIDE_PANEL + "'); if (!el) return false;"
                + " const r = el.getBoundingClientRect(); return r.width > 0 && r.right > 10; }");
        }
        Locator filter = page.locator(ASSET_FILTER);
        try {
            filter.waitFor(new Locator.WaitForOptions().setTimeout(3_000));
        } catch (TimeoutError e) {
            // another content tab (e.g. Components) was remembered as active
            page.locator(SIDE_PANEL + " coral-tab:has-text('Assets')").first().click();
        }
        assertThat(filter).isVisible();
    }

    public static void closeSidePanel(Page page) {
        if (isSidePanelOpen(page)) {
            page.locator(TOGGLE_BUTTON).first().click();
            page.waitForFunction("() => { const el = document.querySelector('" + SIDE_PANEL + "'); if (!el) return true;"
                + " const r = el.getBoundingClientRect(); return r.width === 0 || r.right <= 10; }");
        }
    }

    /** Restricts the asset finder to the given DAM folder, like {@code AssetFinder.setFiltersPath}. */
    public static void setAssetFilter(Page page, String path) {
        Locator autocomplete = page.locator(ASSET_FILTER);
        autocomplete.locator("input[is='coral-textfield']").first().fill(path);
        Locator suggestion = autocomplete.locator("[is='coral-buttonlist-item'][value='" + path + "']");
        assertThat(suggestion).isVisible();
        suggestion.click();
    }

    private static Locator card(Page page, String assetPath) {
        return page.locator("coral-card.cq-draggable[data-path=\"" + assetPath + "\"]");
    }

    /**
     * Drags an asset card from the asset finder onto the given drop target (a dialog file upload) and waits until the
     * dialog took the asset over as its {@code fileReference}.
     */
    public static void dragAssetToFileUpload(Page page, Locator dialog, Locator fileUpload, String assetPath) {
        Locator card = card(page, assetPath);
        assertThat(card).isVisible();
        Locator reference = dialog.locator("input[name='./fileReference']");
        for (int attempt = 1; ; attempt++) {
            card.scrollIntoViewIfNeeded();
            fileUpload.scrollIntoViewIfNeeded();
            drag(page, card, fileUpload);
            try {
                assertThat(reference).hasValue(assetPath,
                    new com.microsoft.playwright.assertions.LocatorAssertions.HasValueOptions().setTimeout(5_000));
                return;
            } catch (AssertionError e) {
                if (attempt == 3) {
                    throw e;
                }
            }
        }
    }

    /** Drags an asset card from the asset finder onto the editable's overlay in the editor. */
    public static void dragSidePanelImageToComponent(Page page, String assetPath, String componentPath) {
        Locator card = card(page, assetPath);
        assertThat(card).isVisible();
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + componentPath + "']");
        card.scrollIntoViewIfNeeded();
        overlay.scrollIntoViewIfNeeded();
        drag(page, card, overlay);
    }

    /** Coral/jQuery UI drags do not react to Playwright's atomic dragTo, so the gesture is replayed with the mouse. */
    private static void drag(Page page, Locator source, Locator target) {
        BoundingBox from = source.boundingBox();
        BoundingBox to = target.boundingBox();
        double startX = from.x + from.width / 2;
        double startY = from.y + from.height / 2;
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

    private static void openProperties(Page page, String baseUrl, String pagePath) {
        page.navigate(baseUrl + "/mnt/overlay/wcm/core/content/sites/properties.html?item=" + pagePath);
        page.locator("coral-tab[data-foundation-tracking-event*='images']").click();
    }

    private static void saveAndClose(Page page) {
        page.locator("#shell-propertiespage-saveclose").click();
        page.waitForURL(url -> !url.contains("properties.html"));
    }

    /**
     * Sets the featured image of a page through the page properties dialog (Images tab, asset picker).
     *
     * @param assetName   name of the DAM node below {@link #TEST_ASSETS_PATH}
     * @param altFromDam  whether to switch on "inherit alt text from DAM"
     */
    public static void setPageFeaturedImage(Page page, String baseUrl, String pagePath, String assetName,
                                            boolean altFromDam) {
        openProperties(page, baseUrl, pagePath);
        page.locator(".cq-FileUpload-picker").click();
        Locator picker = page.locator("coral-dialog:visible").last();
        assertThat(picker).isVisible();
        Locator folder = picker.locator("[data-foundation-collection-item-id='" + TEST_ASSETS_PATH + "']");
        assertThat(folder).isVisible();
        folder.click();
        Locator asset = picker.locator("[data-foundation-collection-item-id='" + TEST_ASSETS_PATH + "/" + assetName + "']");
        assertThat(asset).isVisible();
        // Cloud Service renders a selection checkbox, 6.5/LTS a column view thumbnail
        Locator checkbox = asset.locator("coral-checkbox");
        if (checkbox.count() > 0 && checkbox.first().isVisible()) {
            checkbox.first().click();
        } else {
            asset.locator("coral-columnview-item-thumbnail").click();
        }
        picker.locator("button.granite-pickerdialog-submit[is='coral-button']").click();
        assertThat(page.locator("input[name='./cq:featuredimage/fileReference']"))
            .hasValue(TEST_ASSETS_PATH + "/" + assetName);
        if (altFromDam) {
            page.locator(".cq-siteadmin-admin-properties coral-checkbox[name='./cq:featuredimage/altValueFromDAM']"
                + " input[type='checkbox']").check();
        }
        saveAndClose(page);
    }

    /** Sets the alt text of the page's featured image through the page properties dialog. */
    public static void setPageFeaturedImageAlt(Page page, String baseUrl, String pagePath, String alt) {
        openProperties(page, baseUrl, pagePath);
        page.locator("[name='./cq:featuredimage/alt']").fill(alt);
        saveAndClose(page);
    }
}
