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
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.tabs;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TABS_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group3")
public class TabsV1PwIT extends ComponentPwBaseTest {

    private static final String DEEP_LINK = "/content/core-components/deep-link/tabs/v1.html";
    private static final String TAB1 = "tabs-4e44202276-item-7db3b7c485-tab";
    private static final String CONTENT1 = "text-1";
    private static final String TAB1A = "tabs-4e44202276-item-0bbe8ef9f2-tab";
    private static final String CONTENT1A = "text-1a";
    private static final String TAB2 = "tabs-5ec1f408ee-item-2c2b4d5083-tab";
    private static final String CONTENT2 = "text-2";
    private static final String TAB3 = "tabs-fac5c2a775-item-343a3d8a51-tab";
    private static final String CONTENT3 = "text-3";

    private String tabsPath;

    private void createTabs() throws Exception {
        createPagePolicy(Map.of("clientlibs", Commons.CLIENTLIBS_TABS_V1));
        tabsPath = addStandaloneComponent(RT_TABS_V1, "tabs");
    }

    private Locator itemInputs() {
        return dialog().locator("[data-cmp-hook-childreneditor='itemTitle']");
    }

    private void addItems() throws Exception {
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        Locator add = dialog().locator("[data-cmp-hook-childreneditor='add']");
        for (int i = 0; i < 3; i++) {
            add.click();
            page.locator("[data-foundation-collection-item-id='/libs/wcm/foundation/components/responsivegrid']").click();
            itemInputs().last().fill("item" + i);
        }
        saveDialog();
    }

    private Locator tabItems() {
        return contentFrame().locator(".cmp-tabs__tab");
    }

    private void assertTabTitles(String... titles) {
        assertThat(tabItems()).hasCount(titles.length);
        for (int i = 0; i < titles.length; i++) {
            assertThat(tabItems().nth(i)).containsText(titles[i]);
        }
    }

    @Test
    public void testAddItems() throws Exception {
        createTabs();
        addItems();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        assertThat(itemInputs()).hasCount(3);
        assertThat(itemInputs().nth(0)).hasValue("item0");
        assertThat(itemInputs().nth(1)).hasValue("item1");
        assertThat(itemInputs().nth(2)).hasValue("item2");
    }

    @Test
    public void testRemoveItem() throws Exception {
        createTabs();
        addItems();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        dialog().locator(".cmp-childreneditor coral-multifield-item button[handle='remove']").first().click();
        saveDialog();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        assertThat(itemInputs()).hasCount(2);
        assertThat(itemInputs().nth(0)).hasValue("item1");
        assertThat(itemInputs().nth(1)).hasValue("item2");
    }

    @Test
    public void testReorderItem() throws Exception {
        createTabs();
        addItems();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        Locator items = dialog().locator(".cmp-childreneditor coral-multifield-item");
        items.nth(2).locator("button[handle='move']").dragTo(items.nth(0));
        saveDialog();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        assertThat(itemInputs()).hasCount(3);
        assertThat(itemInputs().nth(2)).hasValue("item1");
    }

    @Test
    public void testSetActiveItem() throws Exception {
        createTabs();
        addItems();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        dialog().locator("[data-cmp-tabs-v1-dialog-edit-hook='activeSelect'] button").click();
        page.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        saveDialog();
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item1");
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        dialog().locator("[data-cmp-tabs-v1-dialog-edit-hook='activeSelect'] button").click();
        page.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("Default")).click();
        saveDialog();
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item0");
    }

    @Test
    public void testPanelSelectItems() throws Exception {
        createTabs();
        page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']").click();
        assertThat(page.locator(".cq-editable-action[data-action='PANEL_SELECT']")).hasCount(0);
        addItems();
        page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']").click();
        page.locator(".cq-editable-action[data-action='PANEL_SELECT']").click();
        Locator panelItems = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        assertThat(panelItems).hasCount(3);
        assertThat(panelItems.nth(0)).containsText("item0");
        assertThat(panelItems.nth(1)).containsText("item1");
        assertThat(panelItems.nth(2)).containsText("item2");
        assertTabTitles("item0", "item1", "item2");
    }

    @Test
    public void testPanelSelectReorder() throws Exception {
        createTabs();
        page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']").click();
        assertThat(page.locator(".cq-editable-action[data-action='PANEL_SELECT']")).hasCount(0);
        addItems();
        page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']").click();
        page.locator(".cq-editable-action[data-action='PANEL_SELECT']").click();
        Locator selectorItems = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        Locator dragHandle = selectorItems.nth(0).locator("button[coral-table-roworder='true']");
        BoundingBox dragBounds = dragHandle.boundingBox();
        BoundingBox targetBounds = selectorItems.nth(2).boundingBox();
        page.mouse().move(dragBounds.x + dragBounds.width / 2, dragBounds.y + dragBounds.height / 2);
        page.mouse().down();
        page.mouse().move(targetBounds.x + targetBounds.width / 2,
            targetBounds.y + targetBounds.height + 1,
            new com.microsoft.playwright.Mouse.MoveOptions().setSteps(8));
        page.mouse().up();
        assertThat(selectorItems.nth(0)).containsText("item1");
        assertThat(selectorItems.nth(1)).containsText("item2");
        assertThat(selectorItems.nth(2)).containsText("item0");
        assertTabTitles("item1", "item2", "item0");
    }

    @Test
    public void testAllowedComponents() throws Exception {
        createTabs();
        createComponentPolicy(RT_TABS_V1.substring(RT_TABS_V1.lastIndexOf("/")),
            Map.of("components", Commons.RT_TEASER_V1));
        openEditor(testPage);
        Locator editable = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']");
        editable.click();
        page.locator(".cq-editable-action[data-action='INSERT']").click();
        assertThat(page.locator("coral-dialog:visible")).containsText("Teaser");
    }

    @Test
    public void testAccessibilityNavigateRight() throws Exception {
        createTabs();
        addItems();
        tabItems().first().click();
        tabItems().first().press("ArrowRight");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item1");
        tabItems().nth(1).press("ArrowRight");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item2");
    }

    @Test
    public void testAccessibilityNavigateLeft() throws Exception {
        createTabs();
        addItems();
        tabItems().nth(2).click();
        tabItems().nth(2).press("ArrowLeft");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item1");
        tabItems().nth(1).press("ArrowLeft");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item0");
    }

    @Test
    public void testAccessibilityNavigateEndStart() throws Exception {
        createTabs();
        addItems();
        tabItems().first().click();
        tabItems().first().press("End");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item2");
        tabItems().nth(2).press("Home");
        assertThat(contentFrame().locator(".cmp-tabs__tab--active")).containsText("item0");
    }

    @Test
    public void testNestedTabs() throws Exception {
        createTabs();
        String tab1 = addNestedTabsChild(tabsPath, "Tab 1");
        addNestedTabsChild(tabsPath, "Tab 2");
        String tab11 = addNestedTabsChild(tab1, "Tab 1.1");
        addNestedTabsChild(tab1, "Tab 1.2");
        addNestedTabsChild(tab11, "Tab 1.1.1");
        assertTabTitles("Tab 1", "Tab 2", "Tab 1.1", "Tab 1.2", "Tab 1.1.1");
    }

    private String addNestedTabsChild(String parentPath, String title) throws Exception {
        String child = Commons.addComponentWithRetry(authorClient, RT_TABS_V1, parentPath + "/", null);
        openEditDialog(parentPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        Locator titles = dialog().locator("[data-cmp-hook-childreneditor='itemTitle']");
        titles.last().fill(title);
        saveDialog();
        return child;
    }

    @Test
    public void testDeepLink_clickingTabsItem() {
        page.navigate(baseUrl + DEEP_LINK);
        page.locator("#" + TAB1).click();
        assertThat(page.locator("#" + CONTENT1)).isVisible();
        assertThat(page).hasURL(baseUrl + DEEP_LINK + "#" + TAB1);
    }

    @Test
    public void testDeepLink_clickingLinksReferencingTabsItems() {
        page.navigate(baseUrl + DEEP_LINK);
        page.locator("#link-1").click();
        assertThat(page.locator("#" + CONTENT1)).isVisible();
        page.locator("#link-1a").click();
        assertThat(page.locator("#" + CONTENT1A)).isVisible();
        page.locator("#link-2").click();
        assertThat(page.locator("#" + CONTENT2)).isVisible();
        page.locator("#link-3").click();
        assertThat(page.locator("#" + CONTENT3)).isVisible();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + TAB1);
        assertThat(page.locator("#" + TAB1)).isVisible();
        assertThat(page.locator("#" + CONTENT1)).isVisible();
    }

    @Test
    public void testDeepLinkFromHash_nestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + TAB2);
        assertThat(page.locator("#" + TAB2)).isVisible();
        assertThat(page.locator("#" + CONTENT2)).isVisible();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + CONTENT3);
        assertThat(page.locator("#" + CONTENT3)).isVisible();
        assertThat(page.locator("#" + TAB3)).isVisible();
    }
}
