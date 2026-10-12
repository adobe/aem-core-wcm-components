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
package com.adobe.cq.wcm.core.components.it.pw.carousel;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.sling.testing.clients.util.FormEntityBuilder;
import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_CAROUSEL_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_TEASER_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group2")
public class CarouselPwIT extends ComponentPwBaseTest {

    private static final String DEEP_LINK_PAGE = "/content/core-components/deep-link/carousel/v1.html";
    private static final String ITEM1 = "carousel-2a81de66e5-item-858ba740ca";
    private static final String ITEM2 = "carousel-2a81de66e5-item-48148b56a1";
    private static final String ITEM3 = "carousel-2a81de66e5-item-5fb9d15664";
    private String carouselPath;

    private void addCarousel() throws Exception {
        addCarousel("carousel");
    }

    private void addCarousel(String name) throws Exception {
        carouselPath = addStandaloneComponent(RT_CAROUSEL_V1, name);
        createPagePolicy(java.util.Collections.singletonMap("clientlibs",
            Commons.CLIENTLIBS_CAROUSEL_V1));
        // the editor loaded before the policy existed, so the component clientlibs are only applied after a reload
        reloadEditor();
    }

    private void createItems() throws Exception {
        addCarousel();
        addItems();
    }

    private void addItems() {
        openEditDialog(carouselPath);
        for (int index = 0; index < 3; index++) {
            addChildrenEditorItem("item" + index);
        }
        saveDialog();
    }

    private Locator carousel() {
        return contentFrame().locator(".cmp-carousel");
    }

    /** The carousel JS only drives the indicators outside the editor, so keyboard tests run with wcmmode=disabled. */
    private Locator disabledModeIndicators() {
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");
        return page.locator(".cmp-carousel__indicator");
    }

    private Locator indicators() {
        return carousel().locator(".cmp-carousel__indicator");
    }

    private Locator items() {
        return carousel().locator(".cmp-carousel__item");
    }

    /** Outside the editor: exactly the given indicator and its item are active. */
    private void assertActive(int index) {
        java.util.regex.Pattern indicatorActive = java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*");
        java.util.regex.Pattern itemActive = java.util.regex.Pattern.compile(".*cmp-carousel__item--active.*");
        assertThat(page.locator(".cmp-carousel__indicator--active")).hasCount(1);
        assertThat(page.locator(".cmp-carousel__indicator").nth(index)).hasClass(indicatorActive);
        assertThat(page.locator(".cmp-carousel__item--active")).hasCount(1);
        assertThat(page.locator(".cmp-carousel__item").nth(index)).hasClass(itemActive);
    }

    private Locator toolbarAction(String action) {
        return page.locator("#EditableToolbar button[data-action='" + action + "'][data-path='" + carouselPath + "']");
    }

    /** Selects the editable's overlay so that its toolbar is shown. */
    private void selectEditable() {
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + carouselPath + "']");
        overlay.scrollIntoViewIfNeeded();
        overlay.evaluate("element => Promise.all(element.getAnimations().map(animation => "
            + "animation.finished.catch(() => {})))");
        BoundingBox bounds = overlay.boundingBox();
        overlay.click(new Locator.ClickOptions().setPosition(Math.max(1, bounds.width / 2), 2));
        assertThat(toolbarAction("CONFIGURE")).isVisible();
    }

    private void scrollToTop() {
        page.evaluate("() => window.scrollTo(0, 0)");
    }

    @Test
    public void testAddItem() throws Exception {
        createItems();
        openEditDialog(carouselPath);
        assertThat(dialog().locator(".cmp-childreneditor coral-multifield-item")).hasCount(3);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(0)).hasValue("item0");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(1)).hasValue("item1");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(2)).hasValue("item2");
    }

    @Test
    public void testRemoveItem() throws Exception {
        createItems();
        openEditDialog(carouselPath);
        dialog().locator(".cmp-childreneditor coral-multifield-item button[handle='remove']").first().click();
        saveDialog();
        openEditDialog(carouselPath);

        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']")).hasCount(2);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").first()).hasValue("item1");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(1)).hasValue("item2");
    }

    @Test
    public void testReorderItem() throws Exception {
        createItems();
        openEditDialog(carouselPath);
        Locator rows = dialog().locator(".cmp-childreneditor coral-multifield-item");
        dragAndDrop(rows.nth(2).locator("button[handle='move']"), rows.nth(0));
        saveDialog();
        openEditDialog(carouselPath);

        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']")).hasCount(3);
        // the drag does not always settle the same way in every browser, the Selenium test accepted both orders
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(0))
            .hasValue(java.util.regex.Pattern.compile("item2|item0"));
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(1))
            .hasValue(java.util.regex.Pattern.compile("item0|item2"));
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(2)).hasValue("item1");
    }

    @Test
    public void testAutoplayGroup() throws Exception {
        createItems();
        openEditDialog(carouselPath);
        dialog().locator(".cmp-carousel__editor coral-tab").nth(1).click();
        Locator autoplay = dialog().locator("[data-cmp-carousel-v1-dialog-hook='autoplay'] input[type='checkbox']");
        Locator group = dialog().locator("[data-cmp-carousel-v1-dialog-hook='autoplayGroup']");
        assertThat(autoplay).not().isChecked();
        assertThat(group).isHidden();
        autoplay.check();
        assertThat(group).isVisible();
        autoplay.uncheck();
        assertThat(group).isHidden();
    }

    @Test
    public void testPanelSelect() throws Exception {
        addCarousel();
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).hasCount(0);
        addItems();
        selectEditable();
        assertThat(toolbarAction("PANEL_SELECT")).isVisible();
        openPanelSelector();
        Locator rows = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        assertThat(rows).hasCount(3);
        assertThat(rows.nth(0)).containsText("item0");
        assertThat(rows.nth(1)).containsText("item1");
        assertThat(rows.nth(2)).containsText("item2");
        assertThat(indicators()).hasCount(3);
        assertThat(indicators().nth(0)).containsText("item0");
        assertThat(indicators().nth(1)).containsText("item1");
        assertThat(indicators().nth(2)).containsText("item2");

        reorderPanelSelectorRow(0, 2);
        assertThat(indicators()).hasCount(3);
        assertThat(indicators().nth(0)).containsText("item1");
        assertThat(indicators().nth(1)).containsText("item2");
        assertThat(indicators().nth(2)).containsText("item0");

        // an out of area click closes the panel selector
        page.locator(".cq-Overlay[data-path='" + testPage + "/jcr:content/root/responsivegrid/*']").click();
        assertThat(page.locator(".cmp-panelselector")).isHidden();
    }

    // The keyboard tests focus the indicator by clicking it, like a user does, and then send the keys to whatever has focus.

    @Test
    public void testAccessibilityNavigateRight() throws Exception {
        createItems();
        Locator buttons = disabledModeIndicators();
        buttons.nth(0).click();
        assertActive(0);
        page.keyboard().press("ArrowRight");
        assertActive(1);
        page.keyboard().press("ArrowRight");
        assertActive(2);
    }

    @Test
    public void testAccessibilityNavigateLeft() throws Exception {
        createItems();
        Locator indicators = disabledModeIndicators();
        indicators.nth(2).click();
        assertActive(2);
        page.keyboard().press("ArrowLeft");
        assertActive(1);
        page.keyboard().press("ArrowLeft");
        assertActive(0);
    }

    @Test
    public void testAccessibilityNavigateEndStart() throws Exception {
        createItems();
        Locator indicators = disabledModeIndicators();
        indicators.nth(0).click();
        assertActive(0);
        page.keyboard().press("End");
        assertActive(2);
        page.keyboard().press("Home");
        assertActive(0);
    }

    @Test
    public void testAllowedComponents() throws Exception {
        // the policy has to exist before the test page is created, otherwise the editor keeps the default policy
        String policyPath = createComponentPolicy("/carousel-v1",
            java.util.Collections.singletonMap("components", RT_TEASER_V1));
        addCarousel("carousel-v1");

        // the toolbar's insert action offers the allowed teaser
        selectEditable();
        assertThat(toolbarAction("INSERT")).isVisible();
        toolbarAction("INSERT").click();
        assertThat(insertableComponent("$='" + RT_TEASER_V1 + "'")).isVisible();
        reloadEditor();

        // the policy restricts what can be added as a panel, which the children editor offers through its picker
        openEditDialog(carouselPath);
        dialog().locator("[data-cmp-hook-childreneditor='add']").click();
        assertThat(insertableComponent("$='" + RT_TEASER_V1 + "'")).isVisible();
        assertThat(insertableComponent("$='/responsivegrid'")).hasCount(0);
        adminClient.deletePath(policyPath, 200);
    }

    @Test
    public void testDeepLink_clickingCarouselItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        Locator button = page.locator("#" + ITEM3 + "-tab");
        button.click();
        assertThat(page.locator("#text-3")).isVisible();
        assertThat(button).isInViewport();
        assertThat(page.locator("#text-3")).isInViewport();
        assertEquals("#" + ITEM3 + "-tabpanel", page.evaluate("() => window.location.hash"));
    }

    @Test
    public void testDeepLink_clickingLinksReferencingCarouselItems() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        Locator button1 = page.locator("#" + ITEM1 + "-tab");
        Locator content1 = page.locator("#text-1");
        Locator button2 = page.locator("#" + ITEM2 + "-tab");
        Locator content2 = page.locator("#text-2");
        Locator button3 = page.locator("#" + ITEM3 + "-tab");
        Locator content3 = page.locator("#text-3");

        // only the first carousel item is displayed before clicking the links
        assertThat(button1).isInViewport();
        assertThat(content1).isInViewport();
        assertThat(button2).isInViewport();
        assertThat(content2).not().isInViewport();
        assertThat(button3).isInViewport();
        assertThat(content3).not().isInViewport();

        // clicking a link referencing a carousel item displays it and scrolls to it
        page.locator("#link-1").click();
        assertThat(content2).isVisible();
        assertThat(button2).isInViewport();
        assertThat(content2).isInViewport();

        // clicking a link referencing the first carousel item displays it and scrolls to it
        page.locator("#link-1a").click();
        assertThat(content1).isVisible();
        assertThat(button1).isInViewport();
        assertThat(content1).isInViewport();

        // clicking a link referencing a text element within a carousel item expands the item
        // and scrolls to the ID
        scrollToTop();
        page.locator("#link-2").click();
        assertThat(content3).isVisible();
        assertThat(button3).isInViewport();
        assertThat(content3).isInViewport();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingCarouselItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM2 + "-tabpanel");
        assertThat(page.locator("#" + ITEM2 + "-tab")).isVisible();
        assertThat(page.locator("#text-2")).isVisible();
        assertThat(page.locator("#" + ITEM2 + "-tab")).isInViewport();
        assertThat(page.locator("#text-2")).isInViewport();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#text-3");
        assertThat(page.locator("#" + ITEM3 + "-tab")).isVisible();
        assertThat(page.locator("#text-3")).isVisible();
        assertThat(page.locator("#" + ITEM3 + "-tab")).isInViewport();
        assertThat(page.locator("#text-3")).isInViewport();
    }

    @Test
    public void testDefaultActiveItem() throws Exception {
        createItems();
        assertThat(items().first()).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__item--active.*"));
        assertThat(indicators().first()).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
        openEditDialog(carouselPath);
        dialog().locator(".cmp-carousel__editor coral-tab").nth(1).click();
        Locator activeList = openSelectList("[data-cmp-carousel-v1-dialog-edit-hook='activeSelect']");        activeList.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        closeOverlays();
        saveDialog();

        assertThat(items().nth(1)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__item--active.*"));
        assertThat(indicators().nth(1)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
        openEditDialog(carouselPath);
        dialog().locator(".cmp-carousel__editor coral-tab").nth(1).click();
        activeList = openSelectList("[data-cmp-carousel-v1-dialog-edit-hook='activeSelect']");
        activeList.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item2")).click();
        closeOverlays();
        saveDialog();

        assertThat(items().nth(2)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__item--active.*"));
        assertThat(indicators().nth(2)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
    }

    private void openPanelSelector() {
        clickToolbarAction(carouselPath, "PANEL_SELECT");
        assertThat(page.locator(".cmp-panelselector")).isVisible();
    }
}
