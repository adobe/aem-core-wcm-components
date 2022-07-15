/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.tabs.v1;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.cq.testing.selenium.utils.KeyboardShortCuts;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.PanelSelector;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs.TabsEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs.v1.Tabs;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("group3")
public class TabsIT extends AuthorBaseUITest {

    private static String pageVar = "tabs_page";
    private static String pageDescription = "tabs page description";
    private static String componentName = "tabs";
    private static final String clientlibs = Commons.CLIENTLIBS_TABS_V1;

    private static final String deepLinkPagePath = "/content/core-components/deep-link/tabs/v1.html";
    private static final String itemTitleId1 = "tabs-4e44202276-item-7db3b7c485-tab";
    private static final String itemContentId1 = "text-1";
    private static final String itemTitleId1a = "tabs-4e44202276-item-0bbe8ef9f2-tab";
    private static final String itemContentId1a = "text-1a";
    private static final String itemTitleId2 = "tabs-5ec1f408ee-item-2c2b4d5083-tab";
    private static final String itemContentId2 = "text-2";
    private static final String itemTitleId3 = "tabs-fac5c2a775-item-343a3d8a51-tab";
    private static final String itemContentId3 = "text-3";

    private String policyPath;
    private String proxyPath;
    private String testPage;
    private EditorPage editorPage;
    private String cmpPath;
    private Tabs tabs;

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create clientlib page policy
     * 3. assign clientlib page policy
     * 4. create the proxy component
     * 5. set cq:isContainer property true
     * 6. add the proxy component to the page
     * 7. open the test page in the editor
     * 8. Get Tabs component
     *
     * @throws ClientException
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        proxyPath = Commons.RT_TABS_V1;
        // 1.
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // 2.
        policyPath = createPagePolicy(new HashMap<String, String>() {{
            put("clientlibs", clientlibs);
        }});

        // 6.
        cmpPath = Commons.addComponentWithRetry(authorClient, proxyPath, testPage + Commons.relParentCompPath, componentName);

        // 7.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        //8.
        tabs = new Tabs();
    }

    /**
     * After Test Case
     *
     * 1. delete the test proxy component
     * 2. delete the test page
     *
     * @throws ClientException
     * @throws InterruptedException
     */

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // 2.
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);

    }

    /**
     * Create three items via the children editor
     *
     * 1. open the edit dialog
     * 2. add item via the children editor
     * 3. save the edit dialog
     * 4. Check if items are added
     *
     * @throws InterruptedException
     */
    private ElementsCollection createItems() throws InterruptedException {
        //1.
        TabsEditDialog editDialog = tabs.openEditDialog(cmpPath);
        editDialog.openItemsTab();

        //2.
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        childrenEditor.clickAddButton();
        InsertComponentDialog insertComponentDialog = editDialog.getInsertComponentDialog();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item0");
        childrenEditor.clickAddButton();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item1");
        childrenEditor.clickAddButton();
        insertComponentDialog.selectComponent("/libs/wcm/foundation/components/responsivegrid");
        childrenEditor.getInputItems().last().sendKeys("item2");

        //3.
        Commons.saveConfigureDialog();

        //4.
        tabs.openEditDialog(cmpPath);
        editDialog.openItemsTab();
        ElementsCollection items = childrenEditor.getInputItems();
        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getValue().equals("item0"), "First input item should be item0");
        assertTrue(items.get(1).getValue().equals("item1"), "Second input item should be item1");
        assertTrue(items.get(2).getValue().equals("item2"), "Third input item should be item2");
        Commons.saveConfigureDialog();

        return items;
    }

    /**
     * Create and title a single tabs item
     *
     * @param component component path
     * @param parentPath parent component path
     * @param itemName name of the item to be set
     * 1. add a component to the tabs
     * 2. open the edit dialog
     * 3. name the tabs item
     * 4. save the edit dialog
     *
     * @throws ClientException
     * @throws InterruptedException
     */
    private String addTabsItem(String component, String parentPath,  String itemName) throws ClientException, InterruptedException {

        //1.
        String cmpPath = Commons.addComponentWithRetry(authorClient, component, parentPath + "/", null);

        //2.
        TabsEditDialog editDialog = tabs.openEditDialog(parentPath);
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editDialog.openItemsTab();

        //3.
        childrenEditor.getInputItems().last().sendKeys(itemName);

        //4.
        Commons.saveConfigureDialog();

        return cmpPath;
    }

    /**
     * Test: Edit Dialog: Add child items
     */
    @Test
    @DisplayName("Test: Edit Dialog: Add child items")
    public void testAddItems() throws InterruptedException {
        createItems();
    }

    /**
     * Test: Edit Dialog : Remove items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. remove the first item and save the edit dialog
     * 4. open the edit dialog
     * 5. verify that the first item has been removed
     * 6. save the edit dialog
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Edit Dialog : Remove items")
    public void testRemoveItem() throws InterruptedException {
        //1.
        createItems();

        //2.
        TabsEditDialog editDialog = tabs.openEditDialog(cmpPath);
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        editDialog.openItemsTab();

        //3.
        childrenEditor.removeFirstItem();
        Commons.saveConfigureDialog();

        //4.
        tabs.openEditDialog(cmpPath);
        editDialog.openItemsTab();
        ElementsCollection items = childrenEditor.getInputItems();

        //5.
        assertTrue(items.size() == 2, "Number to items added should be 2");
        assertTrue(items.get(0).getValue().equals("item1"), "First input item should be item1");
        assertTrue(items.get(1).getValue().equals("item2"), "Second input item should be item2");

        //6.
        Commons.saveConfigureDialog();
    }

    /**
     * Test: Edit Dialog : Reorder items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. move the last item before the first one
     * 4. save the edit dialog
     * 5. open the edit dialog
     * 6. verify the new order
     * 7. save the edit dialog
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Edit Dialog : Re-order children")
    public void testReorderItem() throws InterruptedException {
        //1.
        createItems();

        //2.
        TabsEditDialog editDialog = tabs.openEditDialog(cmpPath);
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        editDialog.openItemsTab();

        //3.
        childrenEditor.moveItems(2,0);

        //4.
        Commons.saveConfigureDialog();

        //5.
        tabs.openEditDialog(cmpPath);
        editDialog.openItemsTab();

        //6.
        ElementsCollection items = childrenEditor.getInputItems();

        assertTrue(items.size() == 3, "Number to items added should be 3");
        //In chrome browser re-order is not working as expected
        assertTrue(items.get(0).getValue().equals("item2") || items.get(0).getValue().equals("item0"), "First input item should be item2 or item0");
        assertTrue(items.get(1).getValue().equals("item0") || items.get(1).getValue().equals("item2"), "Second input item should be item0 or item2");
        assertTrue(items.get(2).getValue().equals("item1"), "Second input item should be item1");

        //7.
        Commons.saveConfigureDialog();
    }

    /**
     * Test: Edit Dialog : Re-order children
     */
    @Test
    @DisplayName("Test: Edit Dialog : Re-order children")
    public void testSetActiveItem() throws InterruptedException {
        createItems();

        TabsEditDialog editDialog = tabs.openEditDialog(cmpPath);

        // switch to properties tab
        TabsEditDialog.EditDialogProperties editDialogProperties = editDialog.openPropertiesTab();

        // select second item as active
        editDialogProperties.setItemActive("item1");

        // save the edit dialog
        Commons.saveConfigureDialog();

        // check the second tab is active
        Commons.switchContext("ContentFrame");
        assertTrue(tabs.isTabActive("item1"),"item1 tab should be active");
        assertTrue(tabs.isTabPanelActive(1),"Second panel should be active");

        Commons.switchToDefaultContext();

        // open the edit dialog
        tabs.openEditDialog(cmpPath);
        // switch to properties tab
        editDialogProperties = editDialog.openPropertiesTab();
        // select second item as active
        editDialogProperties.setItemActive("Default");

        // save the edit dialog
        Commons.saveConfigureDialog();

        // check the first tab is active
        Commons.switchContext("ContentFrame");
        assertTrue(tabs.isTabActive("item0"),"item0 tab should be active");
        assertTrue(tabs.isTabPanelActive(0),"First panel should be active");
    }

    /**
     * Test: Panel Select: Check items
     */
    @Test
    @DisplayName("Test: Panel Select: Check items")
    public void testPanelSelectItems() throws InterruptedException {
        // open the toolbar
        Commons.openEditableToolbar(cmpPath);
        // verify that initially no panel select action is available
        assertTrue(!Commons.isPanelSelectPresent(), "Panel Select button should not be present");
        // create new items with titles
        createItems();
        // open the toolbar
        Commons.openEditableToolbar(cmpPath);
        // verify the panel select action is available
        assertTrue(Commons.isPanelSelectPresent(), "Panel Select button should be present");
        // open the panel selector and verify it's open
        Commons.openPanelSelect();
        PanelSelector panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible(), "Panel selector should be visible");

        // verify that 3 items are available in the panel selector and the correct titles are visible
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        ElementsCollection panelSelectorItems = panelSelector.getItems();
        assertTrue(panelSelectorItems.size() == 3, "Number to items in panel selector should be 3");
        assertTrue(panelSelectorItems.get(0).getText().contains("item0"), "First panel select item should be item0");
        assertTrue(panelSelectorItems.get(1).getText().contains("item1"), "Second panel select item should be item1");
        assertTrue(panelSelectorItems.get(2).getText().contains("item2"), "Third panel select item should be item2");

        Commons.switchContext("ContentFrame");
        // verify initial Tabs DOM item order is as expected
        ElementsCollection tabItems = tabs.getTabItems();
        assertTrue(tabItems.size() == 3, "Number to items added should be 3");
        assertTrue(tabItems.get(0).getText().contains("item0"), "First tab item should be item0");
        assertTrue(tabItems.get(1).getText().contains("item1"), "Second tab item should be item1");
        assertTrue(tabItems.get(2).getText().contains("item2"), "Third tab item should be item2");
    }

    /**
     * Test: Panel Select: Reordering items
     */
    @Test
    @DisplayName("Test: Panel Select: Reordering items")
    public void testPanelSelectReorder() throws InterruptedException {
        // open the toolbar
        Commons.openEditableToolbar(cmpPath);
        // verify that initially no panel select action is available
        assertTrue(!Commons.isPanelSelectPresent(), "Panel Select button should not be present");
        // create new items with titles
        createItems();
        // open the toolbar
        Commons.openEditableToolbar(cmpPath);
        // verify the panel select action is available
        assertTrue(Commons.isPanelSelectPresent(), "Panel Select button should be present");
        // open the panel selector and verify it's open
        Commons.openPanelSelect();
        PanelSelector panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible(), "Panel selector should be visible");

        // reorder: move first element to last position
        panelSelector.reorderItems(0, 2);

        Commons.switchContext("ContentFrame");

        //wait for the reordering to reflect
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        // verify new Tabs DOM item order is as expected
        ElementsCollection tabItems = tabs.getTabItems();
        assertTrue(tabItems.size() == 3, "Number to items added should be 3");
        assertTrue(tabItems.get(0).getText().contains("item1"), "First tab item should be item1");
        assertTrue(tabItems.get(1).getText().contains("item2"), "Second tab item should be item2");
        assertTrue(tabItems.get(2).getText().contains("item0"), "Third tab item should be item0");
    }

    /**
     * Test: Allowed components
     */
    @Test
    @DisplayName("Test: Allowed components")
    public void testAllowedComponents() throws ClientException, InterruptedException, TimeoutException {
        String teaserProxyPath = Commons.RT_TEASER_V1;
        policyPath = createComponentPolicy(proxyPath.substring(proxyPath.lastIndexOf("/")), new HashMap<String, String>() {{
            put("components", teaserProxyPath);
        }});

        String testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        String compPath = Commons.addComponentWithRetry(authorClient, proxyPath, testPage + Commons.relParentCompPath, "tabs");

        // open test page in page editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        String component = "[data-type='Editable'][data-path='" + compPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);

        //2.
        EditableToolbarAssertion editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is displayed while it should not");

        editableToolbarAssertion.assertInsertButton(true);

        editableToolbar.getInsertButton().click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(Commons.isComponentPresentInInsertDialog(teaserProxyPath), "teaser component should be present in insert dialog");
    }

    /**
     * Test: Accessibility : Navigate Right
     */
    @Test
    @DisplayName("Test: Accessibility : Navigate Right")
    public void testAccessibilityNavigateRight() throws InterruptedException {
        createItems();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        tabs.clickTab(0);
        assertTrue(tabs.isTabActive("item0"),"item0 tab should be active");
        assertTrue(tabs.isTabPanelActive(0),"First panel should be active");
        KeyboardShortCuts.keyRight();
        assertTrue(tabs.isTabActive("item1"),"item1 tab should be active");
        assertTrue(tabs.isTabPanelActive(1),"Second panel should be active");
        KeyboardShortCuts.keyRight();
        assertTrue(tabs.isTabActive("item2"),"item2 tab should be active");
        assertTrue(tabs.isTabPanelActive(2),"Third panel should be active");
    }

    /**
     * Test: Accessibility : Navigate Left
     */
    @Test
    @DisplayName("Test: Accessibility : Navigate Left")
    public void testAccessibilityNavigateLeft() throws InterruptedException {
        createItems();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        tabs.clickTab(2);
        assertTrue(tabs.isTabActive("item2"),"item2 tab should be active");
        assertTrue(tabs.isTabPanelActive(2),"Third panel should be active");
        KeyboardShortCuts.keyLeft();
        assertTrue(tabs.isTabActive("item1"),"item1 tab should be active");
        assertTrue(tabs.isTabPanelActive(1),"Second panel should be active");
        KeyboardShortCuts.keyLeft();
        assertTrue(tabs.isTabActive("item0"),"item0 tab should be active");
        assertTrue(tabs.isTabPanelActive(0),"First panel should be active");
    }

    /**
     * Test: Keys : Navigate end / start
     */
    @Test
    @DisplayName("Test: Accessibility : Navigate Left")
    public void testAccessibilityNavigateEndStart() throws InterruptedException {
        createItems();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        tabs.clickTab(0);
        assertTrue(tabs.isTabActive("item0"),"item0 tab should be active");
        assertTrue(tabs.isTabPanelActive(0),"First panel should be active");
        KeyboardShortCuts.keyEnd();
        assertTrue(tabs.isTabActive("item2"),"item2 tab should be active");
        assertTrue(tabs.isTabPanelActive(2),"Third panel should be active");
        KeyboardShortCuts.keyStart();
        assertTrue(tabs.isTabActive("item0"),"item2 tab should be active");
        assertTrue(tabs.isTabPanelActive(0),"First panel should be active");
    }

    /**
     * Test: Nested tabs
     */
    @Test
    @DisplayName("Test: Nested tabs")
    public void testNestedTabs() throws ClientException, InterruptedException {
        String tab1Path = addTabsItem(proxyPath, testPage + Commons.relParentCompPath + componentName,  "Tab 1");
        String tab2Path = addTabsItem(proxyPath, testPage + Commons.relParentCompPath + componentName,  "Tab 2");
        String tab11Path = addTabsItem(proxyPath, tab1Path,  "Tab 1.1");
        String tab12Path = addTabsItem(proxyPath, tab1Path,  "Tab 1.2");
        String tab111Path = addTabsItem(proxyPath, tab11Path,  "Tab 1.1.1");

        // verify new Tabs DOM item order is as expected
        Commons.switchContext("ContentFrame");
        ElementsCollection tabItems = tabs.getTabItems();
        assertTrue(tabItems.size() == 5, "Number to items added should be 4");
        assertTrue(tabItems.get(0).getText().contains("Tab 1"), "First tab item should be Tab 1");
        assertTrue(tabItems.get(1).getText().contains("Tab 2"), "Second tab item should be Tab 2");
        assertTrue(tabItems.get(2).getText().contains("Tab 1.1"), "Third tab item should be Tab 1.1");
        assertTrue(tabItems.get(3).getText().contains("Tab 1.2"), "Fourth tab item should be Tab 1.2");
        assertTrue(tabItems.get(4).getText().contains("Tab 1.1.1"), "Fifth tab item should be Tab 1.1.1");
    }

    @Test
    @DisplayName("Test: Deep Link: clicking tabs items")
    public void testDeepLink_clickingTabsItem() throws MalformedURLException {
        Commons.SimplePage page = new Commons.SimplePage(deepLinkPagePath);
        page.open();

        // clicking a tabs item expands it and modifies the URL fragment
        SelenideElement itemTitle = Selenide.$("#" + itemTitleId1);
        itemTitle.click();
        String fragment = Commons.getUrlFragment();
        SelenideElement itemContent = Selenide.$("#" + itemContentId1);
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
        assertEquals(itemTitleId1, fragment, "The URL fragment should be updated");
    }

    @Test
    @DisplayName("Test: Deep Link: clicking links referencing tabs items")
    public void testDeepLink_clickingLinksReferencingTabsItems() {
        Commons.SimplePage page = new Commons.SimplePage(deepLinkPagePath);
        page.open();
        SelenideElement itemTitle1 = Selenide.$("#" + itemTitleId1);
        SelenideElement itemContent1 = Selenide.$("#" + itemContentId1);
        SelenideElement itemTitle1a = Selenide.$("#" + itemTitleId1a);
        SelenideElement itemContent1a = Selenide.$("#" + itemContentId1a);
        SelenideElement itemTitle2 = Selenide.$("#" + itemTitleId2);
        SelenideElement itemContent2 = Selenide.$("#" + itemContentId2);
        SelenideElement itemTitle3 = Selenide.$("#" + itemTitleId3);
        SelenideElement itemContent3 = Selenide.$("#" + itemContentId3);

        // make sure tabs items are not displayed before clicking the links
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle1));
        assertFalse(Commons.isElementVisibleAndInViewport(itemContent1));
        assertFalse(Commons.isElementVisibleAndInViewport(itemTitle2));
        assertFalse(Commons.isElementVisibleAndInViewport(itemContent2));
        assertFalse(Commons.isElementVisibleAndInViewport(itemTitle3));
        assertFalse(Commons.isElementVisibleAndInViewport(itemContent3));

        // clicking a link referencing a tabs item displays it and scrolls to it
        Selenide.$("#link-1").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle1));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent1));

        // clicking a link referencing the first tabs item displays it and scrolls to it
        Selenide.$("#link-1a").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle1a));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent1a));

        // clicking a link referencing a nested tabs item expands all intermediary items and scrolls to it
        Commons.scrollToTop();
        Selenide.$("#link-2").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle2));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent2));

        // clicking a link referencing a text element within a nested tabs item expands all intermediary items
        // and scrolls to the ID
        Commons.scrollToTop();
        Selenide.$("#link-3").click();
        assertFalse(Commons.isElementVisibleAndInViewport(itemTitle3));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent3));
    }

    @Test
    @DisplayName("Test: Deep Link: URL fragment referencing a tabs item")
    public void testDeepLink_UrlFragmentReferencingTabsItem() {
        String pagePath = deepLinkPagePath + "#" + itemTitleId1;
        Commons.SimplePage page = new Commons.SimplePage(pagePath);
        page.open();
        SelenideElement itemTitle = Selenide.$("#" + itemTitleId1);
        SelenideElement itemContent = Selenide.$("#" + itemContentId1);
        // when the URL fragment references a tabs item, the tabs item is expanded and scrolled to
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
    }

    @Test
    @DisplayName("Test: Deep Link: URL fragment referencing a nested tabs item")
    public void testDeepLinkFromHash_nestedTabsItem() {
        String pagePath = deepLinkPagePath + "#" + itemTitleId2;
        Commons.SimplePage page = new Commons.SimplePage(pagePath);
        page.open();
        SelenideElement itemTitle = Selenide.$("#" + itemTitleId2);
        SelenideElement itemContent = Selenide.$("#" + itemContentId2);
        // when the URL fragment references a nested tabs item, all intermediary tabs items are expanded and
        // the last item is scrolled to
        assertTrue(Commons.isElementVisibleAndInViewport(itemTitle));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
    }

    @Test
    @DisplayName("Test: Deep Link: URL fragment referencing a text element within a nested tabs item")
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        String pagePath = deepLinkPagePath + "#" + itemContentId3;
        Commons.SimplePage page = new Commons.SimplePage(pagePath);
        page.open();
        SelenideElement itemTitle = Selenide.$("#" + itemTitleId3);
        SelenideElement itemContent = Selenide.$("#" + itemContentId3);
        // when the URL fragment references an element ID that is part of a nested tabs item, all intermediary
        // tabs items are expanded and the element ID is scrolled to
        assertFalse(Commons.isElementVisibleAndInViewport(itemTitle));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
    }

}
