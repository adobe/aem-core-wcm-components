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

import com.adobe.cq.wcm.core.components.it.pw.SidePanelPw;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Playwright counterpart of the Selenium {@code Commons.openSidePanel / closeSidePanel / dragSidePanelImageToComponent},
 * {@code AssetFinder.setFiltersPath} and {@code ImageEditDialog.uploadImageFromSidePanel} helpers, plus the page
 * properties featured-image flow. Shared by the image and teaser tests.
 */
public final class ImageSidePanelHelper {

    public static final String TEST_ASSETS_PATH = "/content/dam/core-components";

    private static final String ASSET_FILTER_NAME = "assetfilter_image_path";

    private ImageSidePanelHelper() {
    }

    /** Opens the editor side panel (if closed) and makes sure its Assets tab is the active one. */
    public static void openSidePanel(Page page) {
        new SidePanelPw(page).open();
    }

    public static void closeSidePanel(Page page) {
        new SidePanelPw(page).close();
    }

    /** Restricts the asset finder to the given DAM folder, like {@code AssetFinder.setFiltersPath}. */
    public static void setAssetFilter(Page page, String path) {
        new SidePanelPw(page).filterByPath(ASSET_FILTER_NAME, path);
    }

    /** Drags an asset card onto a dialog file upload and waits until the dialog took it over as its fileReference. */
    public static void dragAssetToFileUpload(Page page, Locator dialog, Locator fileUpload, String assetPath) {
        new SidePanelPw(page).dragAssetToFileUpload(dialog, fileUpload, assetPath);
    }

    /** Drags an asset card from the asset finder onto the editable's overlay in the editor. */
    public static void dragSidePanelImageToComponent(Page page, String assetPath, String componentPath) {
        new SidePanelPw(page).dragAssetToComponent(assetPath, componentPath);
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
