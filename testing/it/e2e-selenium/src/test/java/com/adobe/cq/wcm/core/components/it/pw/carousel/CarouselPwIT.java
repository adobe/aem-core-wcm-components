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
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_CAROUSEL_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEASER_V1;
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
        openEditDialog(carouselPath);
        Locator add = dialog().locator("[data-cmp-hook-childreneditor='add']");
        for (int index = 0; index < 3; index++) {
            add.click();
            insertableComponent("='/libs/wcm/foundation/components/responsivegrid'").click();
            dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").last().fill("item" + index);
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
        createItems();
        Locator toolbar = page.locator("#EditableToolbar");
        assertThat(toolbar.locator("button[data-action='PANEL_SELECT'][data-path='" + carouselPath + "']")).hasCount(0);
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
        page.keyboard().press("Escape");
    }

    @Test
    public void testAccessibilityNavigateRight() throws Exception {
        createItems();
        Locator buttons = disabledModeIndicators();
        buttons.first().click();
        buttons.first().press("ArrowRight");
        assertThat(buttons.nth(1)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
        buttons.nth(1).press("ArrowRight");
        assertThat(buttons.nth(2)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
    }

    @Test
    public void testAccessibilityNavigateLeft() throws Exception {
        createItems();
        Locator indicators = disabledModeIndicators();
        indicators.nth(2).click();
        indicators.nth(2).press("ArrowLeft");
        assertThat(indicators.nth(1)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
        indicators.nth(1).press("ArrowLeft");
        assertThat(indicators.first()).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
    }

    @Test
    public void testAccessibilityNavigateEndStart() throws Exception {
        createItems();
        Locator indicators = disabledModeIndicators();
        indicators.first().click();
        indicators.first().press("End");
        assertThat(indicators.nth(2)).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
        indicators.nth(2).press("Home");
        assertThat(indicators.first()).hasClass(java.util.regex.Pattern.compile(".*cmp-carousel__indicator--active.*"));
    }

    @Test
    public void testAllowedComponents() throws Exception {
        // the policy has to exist before the test page is created, otherwise the editor keeps the default policy
        String policyPath = createComponentPolicy("/carousel-v1",
            java.util.Collections.singletonMap("components", RT_TEASER_V1));
        addCarousel("carousel-v1");
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
        page.locator("#" + ITEM3 + "-tab").click();
        assertThat(page.locator("#text-3")).isVisible();
        assertEquals("#" + ITEM3 + "-tabpanel", page.evaluate("() => window.location.hash"));
    }

    @Test
    public void testDeepLink_clickingLinksReferencingCarouselItems() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        page.locator("#link-1").click();
        assertThat(page.locator("#text-2")).isVisible();
        page.locator("#link-1a").click();
        assertThat(page.locator("#text-1")).isVisible();
        page.locator("#link-2").click();
        assertThat(page.locator("#text-3")).isVisible();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingCarouselItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM2 + "-tabpanel");
        assertThat(page.locator("#" + ITEM2 + "-tab")).isVisible();
        assertThat(page.locator("#text-2")).isVisible();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedTabsItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#text-3");
        assertThat(page.locator("#" + ITEM3 + "-tab")).isVisible();
        assertThat(page.locator("#text-3")).isVisible();
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
