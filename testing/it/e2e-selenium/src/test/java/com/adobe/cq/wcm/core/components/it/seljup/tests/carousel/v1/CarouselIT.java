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

package com.adobe.cq.wcm.core.components.it.seljup.tests.carousel.v1;

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
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.cq.testing.selenium.utils.KeyboardShortCuts;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.carousel.CarouselEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.carousel.v1.Carousel;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.PanelSelector;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.CLIENTLIBS_CAROUSEL_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CAROUSEL_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEASER_V1;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("group2")
public class CarouselIT extends AuthorBaseUITest {

    private static final String clientlibs = CLIENTLIBS_CAROUSEL_V1;
    private static String componentName = "carousel";

    private static final String deepLinkPagePath = "/content/core-components/deep-link/carousel/v1.html";
    private static final String itemId1 = "carousel-2a81de66e5-item-858ba740ca";
    private static final String itemContentId1 = "text-1";
    private static final String itemId2 = "carousel-2a81de66e5-item-48148b56a1";
    private static final String itemContentId2 = "text-2";
    private static final String itemId3 = "carousel-2a81de66e5-item-5fb9d15664";
    private static final String itemContentId3 = "text-3";

    protected Carousel carousel;
    protected EditorPage editorPage;
    protected String cmpPath;
    protected String testPage;

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create and assign clientlib page policy
     * 3. add the proxy component to the page
     * 4. open the test page in the editor
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // 1.
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // 2.
        createPagePolicy(new HashMap<String,String>(){{put("clientlibs",clientlibs); }});

        // 3.
        cmpPath = Commons.addComponentWithRetry(authorClient, RT_CAROUSEL_V1,testPage + Commons.relParentCompPath, componentName);

        // 4.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        carousel = new Carousel();

    }

    /**
     * After Test Case
     *
     * 1. delete the test page
     * 2. delete the clientlib page policy
     * 3. reassign the default policy
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {

        // 1.
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }


    /**
     * Create three items via the children editor
     *
     * 1. open the edit dialog
     * 2. add item via the children editor
     * 3. save the edit dialog
     */
    private ElementsCollection createItems() throws  InterruptedException {
        CarouselEditDialog editDialog = carousel.openEditDialog(cmpPath);
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
        Commons.saveConfigureDialog();

        carousel.openEditDialog(cmpPath);
        ElementsCollection items = childrenEditor.getInputItems();
        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getValue().equals("item0"), "First input item should be item0");
        assertTrue(items.get(1).getValue().equals("item1"), "Second input item should be item1");
        assertTrue(items.get(2).getValue().equals("item2"), "Third input item should be item2");
        Commons.saveConfigureDialog();

        return items;
    }


    /**
     * Test: Edit Dialog: Add child items
     */
    @Test
    @DisplayName("Test: Edit Dialog: Add child items")
    public void testAddItem() throws InterruptedException {
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
     * 6. verify the expanded items select
     * 7. save the edit dialog
     */
    @Test
    @DisplayName("Test: Edit Dialog : Remove items")
    public void testRemoveItem() throws InterruptedException {
        createItems();
        CarouselEditDialog editDialog = carousel.openEditDialog(cmpPath);
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        childrenEditor.removeFirstItem();
        Commons.saveConfigureDialog();

        carousel.openEditDialog(cmpPath);
        ElementsCollection items = childrenEditor.getInputItems();

        assertTrue(items.size() == 2, "Number to items added should be 2");
        assertTrue(items.get(0).getValue().equals("item1"), "First input item should be item1");
        assertTrue(items.get(1).getValue().equals("item2"), "Second input item should be item2");

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
     * 7. verify the expanded items select
     * 8. save the edit dialog
     */
    @Test
    @DisplayName("Test: Edit Dialog : Reorder items")
    public void testReorderItem() throws InterruptedException {
        createItems();
        CarouselEditDialog editDialog = carousel.openEditDialog(cmpPath);
        ChildrenEditor childrenEditor = editDialog.getChildrenEditor();
        childrenEditor.moveItems(2,0);
        Commons.saveConfigureDialog();

        carousel.openEditDialog(cmpPath);
        ElementsCollection items = childrenEditor.getInputItems();

        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getValue().equals("item2") || items.get(0).getValue().equals("item0"), "First input item should be item2 or item0");
        assertTrue(items.get(1).getValue().equals("item0") || items.get(1).getValue().equals("item2"), "Second input item should be item0 or item2  ");
        assertTrue(items.get(2).getValue().equals("item1"), "Second input item should be item1");

        Commons.saveConfigureDialog();
    }

    @Test
    @DisplayName("Test: Autoplay group toggle")
    public void testAutoplayGroup() throws InterruptedException {
        createItems();
        CarouselEditDialog editDialog = carousel.openEditDialog(cmpPath);
        editDialog.openEditDialogProperties();

        CoralCheckbox autoplay = editDialog.getAutoplay();
        assertTrue(autoplay.isChecked() == false, "Autoplay should be unchecked");
        assertTrue(editDialog.getAutoplayGroup().isDisplayed() == false, "Autoplay Group should not be visible");

        autoplay.setSelected(true);

        assertTrue(editDialog.getAutoplayGroup().isDisplayed() == true, "Autoplay Group should be visible");

        autoplay.setSelected(false);

        assertTrue(editDialog.getAutoplayGroup().isDisplayed() == false, "Autoplay Group should not be visible");

    }


    /**
     * Test: Panel Select
     */
    @Test
    @DisplayName("Test: Panel Select")
    public void testPanelSelect() throws InterruptedException {
        String component = "[data-type='Editable'][data-path='" + cmpPath +"']";
        WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        Commons.openEditableToolbar(cmpPath);
        assertTrue(!Commons.isPanelSelectPresent(), "Panel Select should not be present");
        createItems();
        Commons.openEditableToolbar(cmpPath);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(Commons.isPanelSelectPresent(), "Panel Select should be present");
        Commons.openPanelSelect();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        PanelSelector panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible(), "Panel selector should be visible");

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        ElementsCollection items = panelSelector.getItems();

        assertTrue(items.size() == 3, "Number to items added should be 3");
        assertTrue(items.get(0).getText().contains("item0"), "First panel select items should be item0");
        assertTrue(items.get(1).getText().contains("item1"), "Second panel select item should be item1");
        assertTrue(items.get(2).getText().contains("item2"), "Second panel select item should be item2");

        Commons.switchContext("ContentFrame");
        assertTrue(carousel.getIndicators().get(0).getText().contains("item0"),"First indicator item should be item0");
        Commons.switchToDefaultContext();

        webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(panelSelector.getCssSelector())));

        //4.
        panelSelector.reorderItems(0, 2);

        Commons.switchContext("ContentFrame");
        //wait for the reordering to reflect
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(carousel.getIndicators().get(2).getText().contains("item0"),"Third indicator item should be item0 after re-order");
        Commons.switchToDefaultContext();

        carousel.getCQOverlay().openPlaceholder(testPage);
        panelSelector = new PanelSelector();
        assertTrue(panelSelector.isVisible() == false, "Panel selector should not be visible");
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
        carousel.getIndicators().get(0).click();
        assertTrue(carousel.isIndicatorActive(0),"first element should be active");
        KeyboardShortCuts.keyRight();
        assertTrue(carousel.isIndicatorActive(1),"Second element should be active");
        KeyboardShortCuts.keyRight();
        assertTrue(carousel.isIndicatorActive(2),"Third element should be active");
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
        carousel.getIndicators().get(2).click();
        assertTrue(carousel.isIndicatorActive(2),"Third element should be active");
        KeyboardShortCuts.keyLeft();
        assertTrue(carousel.isIndicatorActive(1),"Second element should be active");
        KeyboardShortCuts.keyLeft();
        assertTrue(carousel.isIndicatorActive(0),"First element should be active");
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
        carousel.getIndicators().get(0).click();
        assertTrue(carousel.isIndicatorActive(0),"First element should be active");
        KeyboardShortCuts.keyEnd();
        assertTrue(carousel.isIndicatorActive(2),"Third element should be active");
        KeyboardShortCuts.keyStart();
        assertTrue(carousel.isIndicatorActive(0),"First element should be active");
    }

    /**
     * Test: Allowed components
     */
    @Test
    @DisplayName("Test: Allowed components")
    public void testAllowedComponents() throws ClientException, InterruptedException, TimeoutException {
        String teaserProxyPath = RT_TEASER_V1;
        String policyPath = createComponentPolicy("/carousel-v1", new HashMap<String, String>() {{ put("components", teaserProxyPath); }} );

        String testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        String compPath = Commons.addComponentWithRetry(authorClient, RT_CAROUSEL_V1, testPage + Commons.relParentCompPath, "carousel-v1");

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

        deleteComponentPolicy("/carousel-v1", policyPath);
    }

    @Test
    @DisplayName("Test: Deep Link: clicking carousel items")
    public void testDeepLink_clickingCarouselItem() throws MalformedURLException {
        Commons.SimplePage page = new Commons.SimplePage(deepLinkPagePath);
        page.open();

        // clicking a carousel item expands it and modifies the URL fragment
        SelenideElement itemButton = Selenide.$("#" + itemId3 + "-tab");
        itemButton.click();
        String fragment = Commons.getUrlFragment();
        SelenideElement itemContent = Selenide.$("#" + itemContentId3);
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
        assertEquals(itemId3 + "-tabpanel", fragment, "The URL fragment should be updated");
    }

    @Test
    @DisplayName("Test: Deep Link: clicking links referencing carousel items")
    public void testDeepLink_clickingLinksReferencingCarouselItems() {
        Commons.SimplePage page = new Commons.SimplePage(deepLinkPagePath);
        page.open();
        SelenideElement itemButton1 = Selenide.$("#" + itemId1 + "-tab");
        SelenideElement itemContent1 = Selenide.$("#" + itemContentId1);
        SelenideElement itemButton2 = Selenide.$("#" + itemId2 + "-tab");
        SelenideElement itemContent2 = Selenide.$("#" + itemContentId2);
        SelenideElement itemButton3 = Selenide.$("#" + itemId3 + "-tab");
        SelenideElement itemContent3 = Selenide.$("#" + itemContentId3);

        // make sure carousel items are not displayed before clicking the links
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton1));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent1));
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton2));
        assertFalse(Commons.isElementVisibleAndInViewport(itemContent2));
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton3));
        assertFalse(Commons.isElementVisibleAndInViewport(itemContent3));

        // clicking a link referencing a carousel item displays it and scrolls to it
        Selenide.$("#link-1").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton2));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent2));

        // clicking a link referencing the first carousel item displays it and scrolls to it
        Selenide.$("#link-1a").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton1));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent1));

        // clicking a link referencing a text element within a carousel item expands the item
        // and scrolls to the ID
        Commons.scrollToTop();
        Selenide.$("#link-2").click();
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton3));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent3));
    }

    @Test
    @DisplayName("Test: Deep Link: URL fragment referencing a carousel item")
    public void testDeepLink_UrlFragmentReferencingCarouselItem() {
        String pagePath = deepLinkPagePath + "#" + itemId2 + "-tabpanel";
        Commons.SimplePage page = new Commons.SimplePage(pagePath);
        page.open();
        SelenideElement itemButton = Selenide.$("#" + itemId2 + "-tab");
        SelenideElement itemContent = Selenide.$("#" + itemContentId2);
        // when the URL fragment references a carousel item, the carousel item is expanded and scrolled to
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
    }

    @Test
    @DisplayName("Test: Deep Link: URL fragment referencing a text element within a carousel item")
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        String pagePath = deepLinkPagePath + "#" + itemContentId3;
        Commons.SimplePage page = new Commons.SimplePage(pagePath);
        page.open();
        SelenideElement itemButton = Selenide.$("#" + itemId3 + "-tab");
        SelenideElement itemContent = Selenide.$("#" + itemContentId3);
        // when the URL fragment references an element ID that is part of a carousel item,
        // the carousel item is expanded and the element ID is scrolled to
        assertTrue(Commons.isElementVisibleAndInViewport(itemButton));
        assertTrue(Commons.isElementVisibleAndInViewport(itemContent));
    }

}
