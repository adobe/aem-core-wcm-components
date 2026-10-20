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
import java.util.regex.Pattern;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_TABS_V1;
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
        for (int i = 0; i < 3; i++) {
            addChildrenEditorItem("item" + i);
        }
        saveDialog();
    }

    /** The tabs JS only handles keyboard navigation outside the editor, so these tests use wcmmode=disabled. */
    private Locator disabledModeTabs() {
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");
        return page.locator(".cmp-tabs__tab");
    }

    private Locator tabItems() {
        return contentFrame().locator(".cmp-tabs__tab");
    }

    private Locator toolbarAction(String action) {
        return page.locator("#EditableToolbar button[data-action='" + action + "'][data-path='" + tabsPath + "']");
    }

    /** Selects the editable's overlay so that its toolbar is shown. */
    private void selectEditable() {
        if (toolbarAction("CONFIGURE").isVisible()) {
            return;
        }
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + tabsPath + "']");
        // the overlay is replaced when the editable refreshes after a dialog was saved
        for (int attempt = 0; ; attempt++) {
            try {
                overlay.scrollIntoViewIfNeeded();
                overlay.evaluate("element => Promise.all(element.getAnimations().map(animation => "
                    + "animation.finished.catch(() => {})))");
                BoundingBox bounds = overlay.boundingBox();
                overlay.click(new Locator.ClickOptions().setPosition(Math.max(1, bounds.width / 2), 2));
                break;
            } catch (com.microsoft.playwright.PlaywrightException e) {
                if (attempt >= 3 || !String.valueOf(e.getMessage()).contains("not attached")) {
                    throw e;
                }
            }
        }
        assertThat(toolbarAction("CONFIGURE")).isVisible();
    }

    @Override
    protected void openEditDialog(String componentPath) {
        clickSelectedToolbarAction(componentPath, "CONFIGURE");
        assertThat(dialog()).isVisible();
    }

    private void scrollToTop() {
        page.evaluate("() => window.scrollTo(0, 0)");
    }

    /** Exactly one tab is active and it is the given one, and so is its panel. */
    private void assertTabActive(int index, String title) {
        Locator active = page.locator(".cmp-tabs__tab--active");
        assertThat(active).hasCount(1);
        assertThat(active).containsText(title);
        assertThat(page.locator(".cmp-tabs__tabpanel--active")).hasCount(1);
        assertThat(page.locator(".cmp-tabs__tabpanel").nth(index)).hasClass(Pattern.compile(".*cmp-tabs__tabpanel--active.*"));
    }

    private void assertEditorTabActive(int index, String title) {
        Locator active = contentFrame().locator(".cmp-tabs__tab--active");
        assertThat(active).hasCount(1);
        assertThat(active).containsText(title);
        assertThat(contentFrame().locator(".cmp-tabs__tabpanel--active")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-tabs__tabpanel").nth(index))
            .hasClass(Pattern.compile(".*cmp-tabs__tabpanel--active.*"));
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
        dragAndDrop(items.nth(2).locator("button[handle='move']"), items.nth(0));
        saveDialog();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='items']").click();
        assertThat(itemInputs()).hasCount(3);
        // the drag does not always settle the same way in every browser, the Selenium test accepted both orders
        assertThat(itemInputs().nth(0)).hasValue(Pattern.compile("item2|item0"));
        assertThat(itemInputs().nth(1)).hasValue(Pattern.compile("item0|item2"));
        assertThat(itemInputs().nth(2)).hasValue("item1");
    }

    @Test
    public void testSetActiveItem() throws Exception {
        createTabs();
        addItems();
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        openSelectList("[data-cmp-tabs-v1-dialog-edit-hook='activeSelect']").locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        saveDialog();
        assertEditorTabActive(1, "item1");
        openEditDialog(tabsPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        openSelectList("[data-cmp-tabs-v1-dialog-edit-hook='activeSelect']").locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("Default")).click();
        saveDialog();
        assertEditorTabActive(0, "item0");
    }

    @Test
    public void testPanelSelectItems() throws Exception {
        createTabs();
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).hasCount(0);
        addItems();
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).isVisible();
        clickSelectedToolbarAction(tabsPath, "PANEL_SELECT");
        assertThat(page.locator(".cmp-panelselector")).isVisible();
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
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).hasCount(0);
        addItems();
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).isVisible();
        clickSelectedToolbarAction(tabsPath, "PANEL_SELECT");
        assertThat(page.locator(".cmp-panelselector")).isVisible();
        reorderPanelSelectorRow(0, 2);
        assertTabTitles("item1", "item2", "item0");
    }

    @Test
    public void testAllowedComponents() throws Exception {
        createTabs();
        // the toolbar's insert action adds a sibling, so the component also has to be allowed in the parent container
        addComponentToAllowedPolicy(Commons.RT_TEASER_V1);
        createComponentPolicy(RT_TABS_V1.substring(RT_TABS_V1.lastIndexOf("/")),
            Map.of("components", Commons.RT_TEASER_V1));
        openEditor(testPage);
        selectEditable();
        assertThat(toolbarAction("INSERT")).isVisible();
        toolbarAction("INSERT").click();
        assertThat(page.locator("coral-dialog.InsertComponentDialog")).isVisible();
        assertThat(insertDialogComponent("$='" + Commons.RT_TEASER_V1 + "'")).isVisible();
    }

    // The keyboard tests focus the tab by clicking it, like a user does, and then send the keys to whatever has focus.

    @Test
    public void testAccessibilityNavigateRight() throws Exception {
        createTabs();
        addItems();
        Locator tabs = disabledModeTabs();
        tabs.nth(0).click();
        assertTabActive(0, "item0");
        page.keyboard().press("ArrowRight");
        assertTabActive(1, "item1");
        page.keyboard().press("ArrowRight");
        assertTabActive(2, "item2");
    }

    @Test
    public void testAccessibilityNavigateLeft() throws Exception {
        createTabs();
        addItems();
        Locator tabs = disabledModeTabs();
        tabs.nth(2).click();
        assertTabActive(2, "item2");
        page.keyboard().press("ArrowLeft");
        assertTabActive(1, "item1");
        page.keyboard().press("ArrowLeft");
        assertTabActive(0, "item0");
    }

    @Test
    public void testAccessibilityNavigateEndStart() throws Exception {
        createTabs();
        addItems();
        Locator tabs = disabledModeTabs();
        tabs.nth(0).click();
        assertTabActive(0, "item0");
        page.keyboard().press("End");
        assertTabActive(2, "item2");
        page.keyboard().press("Home");
        assertTabActive(0, "item0");
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
        assertThat(page.locator("#" + TAB1)).isInViewport();
        assertThat(page.locator("#" + CONTENT1)).isInViewport();
        assertThat(page).hasURL(baseUrl + DEEP_LINK + "#" + TAB1);
    }

    @Test
    public void testDeepLink_clickingLinksReferencingTabsItems() {
        page.navigate(baseUrl + DEEP_LINK);
        Locator title1 = page.locator("#" + TAB1);
        Locator content1 = page.locator("#" + CONTENT1);
        Locator title1a = page.locator("#" + TAB1A);
        Locator content1a = page.locator("#" + CONTENT1A);
        Locator title2 = page.locator("#" + TAB2);
        Locator content2 = page.locator("#" + CONTENT2);
        Locator title3 = page.locator("#" + TAB3);
        Locator content3 = page.locator("#" + CONTENT3);

        // make sure tabs items are not displayed before clicking the links
        assertThat(title1).isInViewport();
        assertThat(content1).not().isInViewport();
        assertThat(title2).not().isInViewport();
        assertThat(content2).not().isInViewport();
        assertThat(title3).not().isInViewport();
        assertThat(content3).not().isInViewport();

        // clicking a link referencing a tabs item displays it and scrolls to it
        page.locator("#link-1").click();
        assertThat(content1).isVisible();
        assertThat(title1).isInViewport();
        assertThat(content1).isInViewport();

        // clicking a link referencing the first tabs item displays it and scrolls to it
        page.locator("#link-1a").click();
        assertThat(content1a).isVisible();
        assertThat(title1a).isInViewport();
        assertThat(content1a).isInViewport();

        // clicking a link referencing a nested tabs item expands all intermediary items and scrolls to it
        scrollToTop();
        page.locator("#link-2").click();
        assertThat(content2).isVisible();
        assertThat(title2).isInViewport();
        assertThat(content2).isInViewport();

        // clicking a link referencing a text element within a nested tabs item expands all intermediary items
        // and scrolls to the ID
        scrollToTop();
        page.locator("#link-3").click();
        assertThat(content3).isVisible();
        assertThat(content3).isInViewport();
        assertThat(title3).not().isInViewport();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + TAB1);
        assertThat(page.locator("#" + TAB1)).isVisible();
        assertThat(page.locator("#" + CONTENT1)).isVisible();
        assertThat(page.locator("#" + TAB1)).isInViewport();
        assertThat(page.locator("#" + CONTENT1)).isInViewport();
    }

    @Test
    public void testDeepLinkFromHash_nestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + TAB2);
        assertThat(page.locator("#" + TAB2)).isVisible();
        assertThat(page.locator("#" + CONTENT2)).isVisible();
        assertThat(page.locator("#" + TAB2)).isInViewport();
        assertThat(page.locator("#" + CONTENT2)).isInViewport();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK + "#" + CONTENT3);
        assertThat(page.locator("#" + CONTENT3)).isVisible();
        assertThat(page.locator("#" + CONTENT3)).isInViewport();
        assertThat(page.locator("#" + TAB3)).isVisible();
        assertThat(page.locator("#" + TAB3)).not().isInViewport();
    }
}
