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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.BoundingBox;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_ACCORDION_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_TEASER_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_TEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group2")
public class AccordionPwIT extends ComponentPwBaseTest {

    private static final String DEEP_LINK_PAGE = "/content/core-components/deep-link/accordion/v1.html";
    private static final String ITEM1 = "accordion-24628d01df-item-5209084c68";
    private static final String ITEM1A = "accordion-24628d01df-item-5c22487585";
    private static final String ITEM2 = "accordion-90566600bc-item-42c74c71b7";
    private static final String ITEM3 = "accordion-83cc77b83d-item-fec7e9d490";

    private static final String ITEMS_TAB = "coral-tab[data-foundation-tracking-event*='items']";
    private static final String PROPERTIES_TAB = "coral-tab[data-foundation-tracking-event*='properties']";
    private static final String EXPANDED_SELECT = "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect']";
    private static final String EXPANDED_SELECT_SINGLE = "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelectSingle']";
    private static final String SINGLE_EXPANSION = "[data-cmp-accordion-v1-dialog-edit-hook='singleExpansion']";
    private static final String EXPANDED_ITEM = "[data-cmp-hook-accordion='item'][data-cmp-expanded]";

    private String accordionPath;

    private void addAccordion() throws Exception {
        addAccordion("accordion");
    }

    private void addAccordion(String name) throws Exception {
        accordionPath = addStandaloneComponent(RT_ACCORDION_V1, name);
        createPagePolicy(java.util.Collections.singletonMap("clientlibs", "core.wcm.components.accordion.v1"));
        // the editor loaded before the policy existed, so the component clientlibs are only applied after a reload
        reloadEditor();
    }

    private java.util.List<String> createItems() throws Exception {
        addAccordion();
        return addItems(accordionPath, "item0", "item1", "item2");
    }

    private java.util.List<String> addItems(String componentPath, String... titles) {
        openEditDialog(componentPath);
        dialog().locator(ITEMS_TAB).click();
        for (String title : titles) {
            addChildrenEditorItem(title);
        }
        saveDialog();
        return itemNames(componentPath);
    }

    /** The children editor generates the item node names, so they have to be read back from the repository. */
    private java.util.List<String> itemNames(String componentPath) {
        java.util.List<String> names = new java.util.ArrayList<>();
        try {
            com.fasterxml.jackson.databind.JsonNode node = authorClient.doGetJson(componentPath, 1, 200);
            java.util.Iterator<String> fields = node.fieldNames();
            while (fields.hasNext()) {
                String field = fields.next();
                if (!field.contains(":") && node.get(field).isObject()) {
                    names.add(field);
                }
            }
        } catch (org.apache.sling.testing.clients.ClientException e) {
            throw new IllegalStateException("Could not read the accordion items of " + componentPath, e);
        }
        return names;
    }

    /**
     * Adds a child component to the given accordion and titles it through the edit dialog of the parent.
     * The component is created over HTTP (as the Selenium test did), the title is typed into its children editor row.
     */
    private String addChildItem(String resourceType, String parentPath, String title) throws Exception {
        String child = Commons.addComponentWithRetry(authorClient, resourceType, parentPath + "/", null);
        int expected = itemNames(parentPath).size();
        openEditDialog(parentPath);
        dialog().locator(ITEMS_TAB).click();
        Locator titles = itemInputs();
        assertThat(titles).hasCount(expected);
        titles.last().fill(title);
        assertThat(titles.last()).hasValue(title);
        saveDialog();
        return child;
    }

    /** Collapsed panels hide their content, so nested editables are only selectable once the item is expanded. */
    private void expandItems(String... titles) {
        openEditDialog(accordionPath);
        dialog().locator(PROPERTIES_TAB).click();
        for (String title : titles) {
            // Coral 3 (6.5/LTS) closes the multi-select popover after each pick, so it is reopened per item.
            Locator list = openSelectList(EXPANDED_SELECT);
            list.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText(title)).click();
        }
        closeOverlays();
        saveDialog();
    }

    private Locator itemInputs() {
        return dialog().locator("[data-cmp-hook-childreneditor='itemTitle']");
    }

    private List<String> itemInputValues() {
        Locator inputs = itemInputs();
        List<String> values = new ArrayList<>();
        for (int i = 0; i < inputs.count(); i++) {
            values.add(inputs.nth(i).inputValue());
        }
        return values;
    }

    /** Opens the properties tab of the open dialog and checks the expanded items select lists the given items in order. */
    private void verifyExpandedItemsSelect(List<String> items) {
        dialog().locator(PROPERTIES_TAB).click();
        Locator entries = openSelectList(EXPANDED_SELECT).locator("coral-selectlist-item");
        assertThat(entries).hasCount(items.size());
        for (int i = 0; i < items.size(); i++) {
            assertThat(entries.nth(i)).containsText(items.get(i));
        }
        closeOverlays();
        saveDialog();
    }

    /** Checks that exactly the given items (in DOM order) are rendered expanded in the editor's content frame. */
    private void verifyExpandedItems(String... titles) {
        Locator expanded = accordion().locator(EXPANDED_ITEM);
        assertThat(expanded).hasCount(titles.length);
        for (int i = 0; i < titles.length; i++) {
            assertThat(expanded.nth(i)).containsText(titles[i]);
        }
    }

    /** Waits until the coral element carries (or lacks) the disabled attribute. */
    private void assertDisabledAttribute(String selector, boolean disabled) {
        page.waitForFunction("([s, expected]) => { const e = document.querySelector(s);"
            + " return !!e && e.hasAttribute('disabled') === expected; }",
            Arrays.asList(CONFIG_DIALOG + " " + selector, disabled));
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

    private Locator panelSelectorRows() {
        return page.locator(".cmp-panelselector__table [is='coral-table-row']");
    }

    private Locator toolbarAction(String componentPath, String action) {
        return page.locator("#EditableToolbar button[data-action='" + action + "'][data-path='" + componentPath + "']");
    }

    /** Selects the editable's overlay so that its toolbar is shown. */
    private void selectEditable(String componentPath) {
        if (toolbarAction(componentPath, "CONFIGURE").isVisible()) {
            return;
        }
        Locator overlay = page.locator("#OverlayWrapper [data-type='Editable'][data-path='" + componentPath + "']");
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
        assertThat(toolbarAction(componentPath, "CONFIGURE")).isVisible();
    }

    @Override
    protected void openEditDialog(String componentPath) {
        clickSelectedToolbarAction(componentPath, "CONFIGURE");
        assertThat(dialog()).isVisible();
    }

    private void scrollToTop() {
        page.evaluate("() => window.scrollTo(0, 0)");
    }

    @Test
    public void testAddItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        assertThat(dialog().locator(".cmp-childreneditor coral-multifield-item")).hasCount(3);
        assertThat(itemInputs().nth(0)).hasValue("item0");
        assertThat(itemInputs().nth(1)).hasValue("item1");
        assertThat(itemInputs().nth(2)).hasValue("item2");
        verifyExpandedItemsSelect(Arrays.asList("item0", "item1", "item2"));
    }

    @Test
    public void testRemoveItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator(".cmp-childreneditor coral-multifield-item button[handle='remove']").first().click();
        saveDialog();
        openEditDialog(accordionPath);
        assertThat(itemInputs()).hasCount(2);
        assertThat(itemInputs().first()).hasValue("item1");
        assertThat(itemInputs().nth(1)).hasValue("item2");
        verifyExpandedItemsSelect(itemInputValues());
    }

    @Test
    public void testReorderItem() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        Locator rows = dialog().locator(".cmp-childreneditor coral-multifield-item");
        dragAndDrop(rows.nth(2).locator("button[handle='move']"), rows.nth(0));
        saveDialog();
        openEditDialog(accordionPath);
        assertThat(itemInputs()).hasCount(3);
        // the drag does not always settle the same way in every browser, the Selenium test accepted both orders
        assertThat(itemInputs().nth(0)).hasValue(Pattern.compile("item2|item0"));
        assertThat(itemInputs().nth(1)).hasValue(Pattern.compile("item0|item2"));
        assertThat(itemInputs().nth(2)).hasValue("item1");
        verifyExpandedItemsSelect(itemInputValues());
    }

    @Test
    public void testSetExpandedItems() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator(PROPERTIES_TAB).click();
        openSelectList(EXPANDED_SELECT)
            .locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        closeOverlays();
        saveDialog();

        verifyExpandedItems("item1");
        assertThat(panels().nth(1)).hasAttribute("data-cmp-expanded", "");
        openEditDialog(accordionPath);
        dialog().locator(PROPERTIES_TAB).click();
        openSelectList(EXPANDED_SELECT)
            .locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item2")).click();
        closeOverlays();
        saveDialog();

        verifyExpandedItems("item1", "item2");
        assertThat(panels().nth(1)).hasAttribute("data-cmp-expanded", "");
        assertThat(panels().nth(2)).hasAttribute("data-cmp-expanded", "");
    }

    @Test
    public void testSingleItemExpansion() throws Exception {
        createItems();
        openEditDialog(accordionPath);
        dialog().locator(PROPERTIES_TAB).click();
        Locator singleExpansion = dialog().locator(SINGLE_EXPANSION + " input[type='checkbox']");
        Locator expandedSelect = dialog().locator(EXPANDED_SELECT);
        Locator expandedSelectSingle = dialog().locator(EXPANDED_SELECT_SINGLE);

        assertThat(singleExpansion).not().isChecked();
        assertThat(expandedSelect).isVisible();
        assertDisabledAttribute(EXPANDED_SELECT, false);
        assertThat(expandedSelectSingle).isHidden();
        assertDisabledAttribute(EXPANDED_SELECT_SINGLE, true);

        singleExpansion.check();

        assertThat(singleExpansion).isChecked();
        assertThat(expandedSelect).isHidden();
        assertDisabledAttribute(EXPANDED_SELECT, true);
        assertThat(expandedSelectSingle).isVisible();
        assertDisabledAttribute(EXPANDED_SELECT_SINGLE, false);

        saveDialog();

        // no item is expanded until one is chosen
        verifyExpandedItems();

        openEditDialog(accordionPath);
        dialog().locator(PROPERTIES_TAB).click();
        openSelectList(EXPANDED_SELECT_SINGLE)
            .locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText("item1")).click();
        closeOverlays();
        saveDialog();

        // the chosen item renders expanded
        verifyExpandedItems("item1");
        assertThat(panels().nth(1)).hasAttribute("data-cmp-expanded", "");

        // the editor overlays swallow clicks on the panel buttons, so the expansion is exercised outside the editor
        page.navigate(baseUrl + testPage + ".html?wcmmode=disabled");
        Locator buttons = page.locator("[data-cmp-hook-accordion='button']");
        Locator items = page.locator("[data-cmp-hook-accordion='item']");
        buttons.nth(0).click();
        buttons.nth(2).click();

        assertThat(items.nth(2)).hasAttribute("data-cmp-expanded", "");
        assertThat(items.nth(0)).not().hasAttribute("data-cmp-expanded", "");
        assertThat(items.nth(1)).not().hasAttribute("data-cmp-expanded", "");
    }

    @Test
    public void testPanelSelectItems() throws Exception {
        addAccordion();
        selectEditable(accordionPath);
        assertThat(toolbarAction(accordionPath, "PANEL_SELECT")).hasCount(0);
        addItems(accordionPath, "item0", "item1", "item2");
        selectEditable(accordionPath);
        assertThat(toolbarAction(accordionPath, "PANEL_SELECT")).isVisible();
        openPanelSelector();
        Locator panelRows = panelSelectorRows();
        assertThat(panelRows).hasCount(3);
        assertThat(panelRows.nth(0)).containsText("item0");
        assertThat(panelRows.nth(1)).containsText("item1");
        assertThat(panelRows.nth(2)).containsText("item2");
        assertThat(panels()).hasCount(3);
        assertThat(panelButtons()).hasCount(3);
        assertThat(panelButtons().nth(0)).containsText("item0");
        assertThat(panelButtons().nth(1)).containsText("item1");
        assertThat(panelButtons().nth(2)).containsText("item2");

        // an out of area click closes the panel selector
        page.locator(".cq-Overlay[data-path='" + testPage + "/jcr:content/root/responsivegrid/*']").click();
        assertThat(page.locator(".cmp-panelselector")).isHidden();
    }

    @Test
    public void testPanelSelectReorder() throws Exception {
        createItems();
        selectEditable(accordionPath);
        assertThat(toolbarAction(accordionPath, "PANEL_SELECT")).isVisible();
        openPanelSelector();
        Locator rows = panelSelectorRows();
        assertThat(rows).hasCount(3);
        reorderPanelSelectorRow(0, 2);
        assertThat(panels()).hasCount(3);
        assertThat(panelButtons().nth(0)).containsText("item1");
        assertThat(panelButtons().nth(1)).containsText("item2");
        assertThat(panelButtons().nth(2)).containsText("item0");
    }

    @Test
    public void testNested() throws Exception {
        addAccordion();
        addChildItem(RT_ACCORDION_V1, accordionPath, "Accordion 1.1");
        String accordion2Path = addChildItem(RT_ACCORDION_V1, accordionPath, "Accordion 1.2");
        // the nested accordion is only selectable once its item is expanded
        expandItems("Accordion 1.2");
        addChildItem(RT_ACCORDION_V1, accordion2Path, "Accordion 2.1");
        addChildItem(RT_ACCORDION_V1, accordion2Path, "Accordion 2.2");
        reloadEditor();

        assertThat(panels()).hasCount(4);
        String[] titles = {"Accordion 1.1", "Accordion 1.2", "Accordion 2.1", "Accordion 2.2"};
        for (int i = 0; i < titles.length; i++) {
            assertThat(panels().nth(i).locator("[data-cmp-hook-accordion='button']").first()).containsText(titles[i]);
        }
    }

    @Test
    public void testOpenConfigDialog() throws Exception {
        addAccordion();
        String accordion1Path = addChildItem(RT_ACCORDION_V1, accordionPath, "Accordion 1");
        openPanelSelector();
        addChildItem(RT_ACCORDION_V1, accordion1Path, "Accordion 1.2");
        String textPath = addChildItem(RT_TEXT_V2, accordionPath, "Text 1");

        openEditDialog(accordion1Path);
        assertThat(dialog().locator(".cq-dialog-header")).hasText(Pattern.compile("^\\s*Accordion\\s*$"));
        saveDialog();

        openPanelSelector();
        panelSelectorRows().nth(1).click();
        openEditDialog(textPath);
        assertThat(dialog().locator(".cq-dialog-header")).hasText(Pattern.compile("^\\s*Text\\s*$"));
        saveDialog();
    }

    @Test
    public void testAllowedComponents() throws Exception {
        // the policy has to exist before the test page is created, otherwise the editor keeps the default policy
        String policyPath = createComponentPolicy("/accordion-v1",
            java.util.Collections.singletonMap("components", RT_TEASER_V1));
        // the toolbar's insert action adds a sibling, so the component also has to be allowed in the parent container
        addComponentToAllowedPolicy(RT_TEASER_V1);
        addAccordion("accordion-v1");

        // the toolbar's insert action offers the allowed teaser
        selectEditable(accordionPath);
        assertThat(toolbarAction(accordionPath, "INSERT")).isVisible();
        toolbarAction(accordionPath, "INSERT").click();
        assertThat(page.locator("coral-dialog.InsertComponentDialog")).isVisible();
        assertThat(insertDialogComponent("$='" + RT_TEASER_V1 + "'")).isVisible();
        reloadEditor();

        // the policy restricts what can be added as a panel, which the children editor offers through its picker
        openEditDialog(accordionPath);
        dialog().locator(ITEMS_TAB).click();
        dialog().locator("[data-cmp-hook-childreneditor='add']").click();
        assertThat(insertableComponent("$='" + RT_TEASER_V1 + "'")).isVisible();
        assertThat(insertableComponent("$='/responsivegrid'")).hasCount(0);
        adminClient.deletePath(policyPath, 200);
    }

    @Test
    public void testDeepLink_clickingAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        Locator button = page.locator("#" + ITEM1 + "-button");
        Locator content = page.locator("#text-1");

        // clicking a closed accordion item expands it and modifies the URL fragment
        button.click();
        assertThat(button).isInViewport();
        assertThat(content).isVisible();
        assertThat(content).isInViewport();
        assertEquals("#" + ITEM1, page.evaluate("() => window.location.hash"));

        // clicking an expanded accordion item closes it and removes the URL fragment
        button.click();
        assertThat(button).isInViewport();
        assertThat(content).isHidden();
        assertThat(content).not().isInViewport();
        assertEquals("", page.evaluate("() => window.location.hash"));
    }

    @Test
    public void testDeepLink_clickingLinksReferencingAccordionItems() {
        page.navigate(baseUrl + DEEP_LINK_PAGE);
        Locator title1 = page.locator("#" + ITEM1);
        Locator content1 = page.locator("#text-1");
        Locator title1a = page.locator("#" + ITEM1A);
        Locator content1a = page.locator("#text-1a");
        Locator title2 = page.locator("#" + ITEM2);
        Locator content2 = page.locator("#text-2");
        Locator title3 = page.locator("#" + ITEM3);
        Locator content3 = page.locator("#text-3");

        // make sure accordion items are closed before clicking the links
        assertThat(title1).isInViewport();
        assertThat(content1).not().isInViewport();
        assertThat(title2).not().isInViewport();
        assertThat(content2).not().isInViewport();
        assertThat(title3).not().isInViewport();
        assertThat(content3).not().isInViewport();

        // clicking a link referencing an accordion item expands it and scrolls to it
        page.locator("#link-1").click();
        assertThat(content1).isVisible();
        assertThat(title1).isInViewport();
        assertThat(content1).isInViewport();

        // clicking a link referencing the first accordion item expands it and scrolls to it
        page.locator("#link-1a").click();
        assertThat(content1a).isVisible();
        assertThat(title1a).isInViewport();
        assertThat(content1a).isInViewport();

        // clicking a link referencing a nested accordion item expands all intermediary items and scrolls to it
        scrollToTop();
        page.locator("#link-2").click();
        assertThat(content2).isVisible();
        assertThat(title2).isInViewport();
        assertThat(content2).isInViewport();

        // clicking a link referencing a text element within a nested accordion item expands all intermediary items
        // and scrolls to the ID
        scrollToTop();
        page.locator("#link-3").click();
        assertThat(content3).isVisible();
        assertThat(content3).isInViewport();
        assertThat(page.locator("#" + ITEM3 + "-button")).not().isInViewport();
    }

    @Test
    public void testDeepLink_UrlFragmentReferencingAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM1);
        assertThat(page.locator("#" + ITEM1 + "-button")).isVisible();
        assertThat(page.locator("#text-1")).isVisible();
        assertThat(page.locator("#" + ITEM1)).isInViewport();
        assertThat(page.locator("#text-1")).isInViewport();
    }

    @Test
    public void testDeepLinkFromHash_nestedAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#" + ITEM2);
        assertThat(page.locator("#" + ITEM2)).isVisible();
        assertThat(page.locator("#text-2")).isVisible();
        assertThat(page.locator("#" + ITEM2)).isInViewport();
        assertThat(page.locator("#text-2")).isInViewport();
    }

    @Test
    public void testDeepLinkFromHash_IdInNestedAccordionItem() {
        page.navigate(baseUrl + DEEP_LINK_PAGE + "#text-3");
        assertThat(page.locator("#" + ITEM3)).isVisible();
        assertThat(page.locator("#text-3")).isVisible();
        assertThat(page.locator("#text-3")).isInViewport();
        assertThat(page.locator("#" + ITEM3 + "-button")).not().isInViewport();
    }

    private void openPanelSelector() {
        clickSelectedToolbarAction(accordionPath, "PANEL_SELECT");
        assertThat(page.locator(".cmp-panelselector")).isVisible();
    }
}
