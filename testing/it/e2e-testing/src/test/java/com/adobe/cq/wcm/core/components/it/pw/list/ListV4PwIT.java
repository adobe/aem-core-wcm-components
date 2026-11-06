/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.list;

import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_LIST_V4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group4")
public class ListV4PwIT extends ListV3PwIT {
    @Override
    protected String listResourceType() {
        return RT_LIST_V4;
    }

    private void assertRendered(String... titles) {
        page.navigate(baseUrl + testPage + ".html");
        for (String title : titles) {
            assertThat(page.locator(".cmp-list")).containsText(title);
        }
    }

    private void openListDialog(String componentPath) {
        openEditor(testPage);
        openEditDialog(componentPath);
    }

    @Override
    @Test
    public void testCreateFixedList() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        createListPages();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./listFrom']", "static");
        addStaticItem(listPages[1], null);
        addStaticItem(page21Path, null);
        addStaticItem(listPages[4], null);
        saveDialog();
        assertRendered("page_1", "sub_2_1", "page_4");
    }

    @Test
    public void testCreateStaticListWithPages() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        createListPages();
        openEditDialog(componentPath);
        assertThat(dialog().locator("input[name='./maxItems']")).isVisible();
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
        addStaticItem(listPages[1], null);
        addStaticItem(page21Path, null);
        addStaticItem(listPages[4], null);
        saveDialog();
        assertRendered("page_1", "sub_2_1", "page_4");
    }

    @Test
    public void testCreateStaticListWithPagesAndExternalLink() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        createListPages();
        openEditDialog(componentPath);
        assertThat(dialog().locator("input[name='./maxItems']")).isVisible();
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
        addStaticItem(listPages[1], null);
        addStaticItem(page21Path, null);
        addStaticItem("http://www.adobe.com", "Adobe");
        saveDialog();
        assertRendered("page_1", "sub_2_1", "Adobe");
    }

    @Test
    public void testCreateStaticListWithPagesAndExternalLinkCheckInteraction() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        createListPages();
        openEditDialog(componentPath);
        assertThat(dialog().locator("input[name='./maxItems']")).isVisible();
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
        addStaticItem(listPages[1], null);
        addStaticItem("http://www.adobe.com", "Adobe");
        saveDialog();
        assertRendered("page_1", "Adobe");

        openListDialog(componentPath);
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
        selectInCoralSelect("[name='./listFrom']", "children");
        assertThat(dialog().locator("input[name='./maxItems']")).isVisible();
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
    }

    @Test
    public void testConvertV3StaticListContentToV4Content() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        createListPages();
        String page1Path = listPages[1];
        String page4Path = listPages[4];

        // v3 content format: multivalue "pages" property
        ArrayList<NameValuePair> properties = new ArrayList<>();
        properties.add(new BasicNameValuePair("listFrom", "static"));
        properties.add(new BasicNameValuePair("pages", page1Path));
        properties.add(new BasicNameValuePair("pages", page21Path));
        properties.add(new BasicNameValuePair("pages", page4Path));
        authorClient.setPropertiesString(componentPath, properties, 200);

        JsonNode json = authorClient.doGetJson(componentPath, -1);
        assertTrue(json.has("pages"));
        assertFalse(json.has("static"));
        assertRendered("page_1", "sub_2_1", "page_4");

        // opening and saving the dialog converts the v3 configuration to v4
        openListDialog(componentPath);
        saveDialog();
        assertRendered("page_1", "sub_2_1", "page_4");

        json = authorClient.doGetJson(componentPath, -1);
        assertFalse(json.has("pages"));
        assertTrue(json.has("static"));
        JsonNode staticNode = json.get("static");
        assertTrue(staticNode.isObject());
        String[] urls = {page1Path, page21Path, page4Path};
        String[] texts = {"page_1", "sub_2_1", "page_4"};
        for (int i = 0; i < urls.length; i++) {
            JsonNode item = staticNode.get("item" + i);
            assertNotNull(item, "item" + i + " should exist");
            assertEquals(urls[i], item.get("linkURL").asText());
            assertEquals(texts[i], item.get("linkText").asText());
        }
    }

    private void addStaticItem(String link, String text) {
        Locator multifield = dialog().locator("coral-multifield[data-granite-coral-multifield-name='./static']");
        multifield.locator("button[coral-multifield-add]").last().click();
        Locator item = multifield.locator("coral-multifield-item").last();
        Locator autocomplete = item.locator("foundation-autocomplete").first();
        Locator linkText = item.locator("input[name$='linkText']");
        if (text != null) {
            typeInAutocomplete(autocomplete, link);
            linkText.fill(text);
        } else {
            // pick the page suggestion: typing alone does not fire the change event that fills in the
            // (required) link text from the page title on all AEM versions
            autocomplete.locator("input[is='coral-textfield']").first().fill(link);
            autocomplete.locator("coral-overlay button[is='coral-buttonlist-item'][value='" + link + "']").click();
            assertThat(autocomplete).hasJSProperty("value", link);
            assertThat(linkText).hasValue(link.substring(link.lastIndexOf('/') + 1));
        }
    }
}
