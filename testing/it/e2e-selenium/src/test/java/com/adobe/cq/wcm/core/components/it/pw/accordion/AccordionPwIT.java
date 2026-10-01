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
package com.adobe.cq.wcm.core.components.it.pw.accordion;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_ACCORDION_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEASER_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group2")
public class AccordionPwIT extends ComponentPwBaseTest {

    private static final String DEEP_LINK_PAGE = "/content/core-components/deep-link/accordion/v1.html";
    private static final String ITEM1 = "accordion-24628d01df-item-5209084c68";
    private static final String ITEM1A = "accordion-24628d01df-item-5c22487585";
    private static final String ITEM2 = "accordion-90566600bc-item-42c74c71b7";
    private static final String ITEM3 = "accordion-83cc77b83d-item-fec7e9d490";
    private String accordionPath;

    private void addAccordion() throws Exception {
        accordionPath = addStandaloneComponent(RT_ACCORDION_V1, "accordion");
        createPagePolicy(java.util.Collections.singletonMap("clientlibs", "core.wcm.components.accordion.v1"));
    }

    private void createItems() throws Exception {
        addAccordion();
        for (int index = 0; index < 3; index++) {
            addPanel(accordionPath, "item" + index);
        }
        reloadEditor();
    }

    private String addPanel(String parentPath, String name) throws Exception {
        String path = Commons.addComponentWithRetry(authorClient,
            "/libs/wcm/foundation/components/responsivegrid", parentPath, name);
        HashMap<String, String> properties = new HashMap<>();
        properties.put("_charset_", "UTF-8");
        properties.put("./jcr:title", name);
        Commons.editNodeProperties(authorClient, path, properties);
        return path;
    }

    private Locator accordion() {
        return contentFrame().locator(".cmp-accordion");
    }

    private Locator panels() {
        return accordion().locator("[data-cmp-hook-accordion='item']");
    }

    private Locator panelButtons() {
        return accordion().locator("[data-cmp-hook-accordion='button']");
    }

    @Test
    public void testAddItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        assertThat(dialog().locator(".cmp-childreneditor coral-multifield-item")).hasCount(3);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(0)).hasValue("item0");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(1)).hasValue("item1");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(2)).hasValue("item2");
    }

    @Test
    public void testRemoveItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator(".cmp-childreneditor coral-multifield-item button[handle='remove']").first().click();
        saveDialog();
        openEditDialog(accordionPath);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']")).hasCount(2);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").first()).hasValue("item1");
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(1)).hasValue("item2");
    }

    @Test
    public void testReorderItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        Locator rows = dialog().locator(".cmp-childreneditor coral-multifield-item");
        rows.nth(2).dragTo(rows.nth(0));
        saveDialog();
        openEditDialog(accordionPath);
        assertThat(dialog().locator("[data-cmp-hook-childreneditor='itemTitle']").nth(2)).hasValue("item1");
    }

    @Test
    public void testSetExpandedItems() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        Locator select = dialog().locator("[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect'] button");
        select.click();
        page.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        saveDialog();

        assertThat(panels().nth(1)).hasAttribute("data-cmp-expanded", "");
        openEditDialog(accordionPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        dialog().locator("[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect'] button").click();
        page.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item2")).click();
        saveDialog();

        assertThat(panels().nth(1)).hasAttribute("data-cmp-expanded", "");
        assertThat(panels().nth(2)).hasAttribute("data-cmp-expanded", "");
    }

    @Test
    public void testSingleItemExpansion() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator("coral-tab[data-foundation-tracking-event*='properties']").click();
        dialog().locator("[data-cmp-accordion-v1-dialog-edit-hook='singleExpansion'] input").check();
        Locator select = dialog().locator("[data-cmp-accordion-v1-dialog-edit-hook='expandedSelectSingle'] button");
        select.click();
        page.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        saveDialog();
        panelButtons().nth(0).click();
        panelButtons().nth(2).click();

        assertThat(panels().nth(2)).hasAttribute("data-cmp-expanded", "");
        assertThat(panels().nth(0)).not().hasAttribute("data-cmp-expanded", "");
    }

    @Test
    public void testPanelSelectItems() throws Exception {
        createItems();
        Locator toolbar = page.locator("#EditableToolbar");
        assertThat(toolbar.locator("button[data-action='PANEL_SELECT'][data-path='" + accordionPath + "']")).hasCount(0);
        openPanelSelector();
        Locator panelRows = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        assertThat(panelRows).hasCount(3);
        assertThat(panelRows.nth(0)).containsText("item0");
        assertThat(panelRows.nth(1)).containsText("item1");
        assertThat(panelRows.nth(2)).containsText("item2");
        assertThat(panelButtons()).hasCount(3);
        assertThat(panelButtons().nth(0)).containsText("item0");
        assertThat(panelButtons().nth(1)).containsText("item1");
        assertThat(panelButtons().nth(2)).containsText("item2");
        page.keyboard().press("Escape");
    }

    @Test
    public void testPanelSelectReorder() throws Exception {
        createItems();
        openPanelSelector();
        Locator rows = page.locator(".cmp-panelselector__table [is='coral-table-row']");
        rows.nth(0).locator("button[coral-table-roworder='true']").dragTo(rows.nth(2));
        assertThat(panelButtons().nth(0)).containsText("item1");
        assertThat(panelButtons().nth(1)).containsText("item2");
        assertThat(panelButtons().nth(2)).containsText("item0");
    }

    @Test
    public void testNested() throws Exception {
        createItems();
        String nested = Commons.addComponentWithRetry(authorClient, RT_ACCORDION_V1,
            accordionPath + "/item0", "nestedAccordion");
        addPanel(nested, "nested-item");
        reloadEditor();

        assertThat(contentFrame().locator(".cmp-accordion .cmp-accordion")).hasCount(1);
        assertThat(contentFrame().locator(".cmp-accordion .cmp-accordion__item")).hasCount(1);
    }

    @Test
    public void testOpenConfigDialog() throws Exception {
        createItems();
        String nestedPath = Commons.addComponentWithRetry(authorClient, RT_ACCORDION_V1,
            accordionPath + "/item0", "nestedAccordion");
        addPanel(nestedPath, "nested-item");
        String textPath = Commons.addComponentWithRetry(authorClient, RT_TEXT_V2,
            accordionPath + "/item1", "text");
        reloadEditor();

        openEditDialog(nestedPath);
        assertThat(dialog().locator(".cq-dialog-header")).containsText("Accordion");
        saveDialog();
        openEditDialog(textPath);
        assertThat(dialog().locator(".cq-dialog-header")).containsText("Text");
        saveDialog();
    }

    @Test
    public void testAllowedComponents() throws Exception {
        addAccordion();
        String policyPath = createComponentPolicy("/accordion-v1",
            java.util.Collections.singletonMap("components", RT_TEASER_V1));
        openEditDialog(accordionPath);
        saveDialog();
        page.locator("#OverlayWrapper [data-path='" + accordionPath + "']").click();
        Locator insert = page.locator("#EditableToolbar button[data-action='INSERT'][data-path='" + accordionPath + "']");
        assertThat(insert).isVisible();
        insert.click();
        assertThat(page.locator("coral-dialog:visible")).containsText("Teaser");
        adminClient.deletePath(policyPath, 200);
    }

    @Test
    public void testDeepLink_clickingAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        Locator button = page.locator("#" + ITEM1 + "-button");
        button.click();
        assertThat(page.locator("#text-1")).isVisible();
        assertEquals("#" + ITEM1, page.evaluate("() => window.location.hash"));
        button.click();
        assertThat(page.locator("#text-1")).isHidden();
        assertEquals("", page.evaluate("() => window.location.hash"));
    }

    @Test
    public void testDeepLink_clickingLinksReferencingAccordionItems() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        page.locator("#link-1").click();
        assertThat(page.locator("#text-1")).isVisible();
        page.locator("#link-1a").click();
        assertThat(page.locator("#text-1a")).isVisible();
        page.locator("#link-2").click();
        assertThat(page.locator("#text-2")).isVisible();
        page.locator("#link-3").click();
        assertThat(page.locator("#text-3")).isVisible();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM1);
        assertThat(page.locator("#" + ITEM1 + "-button")).isVisible();
        assertThat(page.locator("#text-1")).isVisible();
    }

    @Test
    public void testDeepLinkFromHash_nestedAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM2);
        assertThat(page.locator("#" + ITEM2)).isVisible();
        assertThat(page.locator("#text-2")).isVisible();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#text-3");
        assertThat(page.locator("#" + ITEM3)).isVisible();
        assertThat(page.locator("#text-3")).isVisible();
    }

    private void openPanelSelector() {
        page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + accordionPath + "']").click();
        Locator panelSelect = page.locator(
            "#EditableToolbar button[data-action='PANEL_SELECT'][data-path='" + accordionPath + "']");
        assertThat(panelSelect).isVisible();
        panelSelect.click();
        assertThat(page.locator(".cmp-panelselector")).isVisible();
    }
}
