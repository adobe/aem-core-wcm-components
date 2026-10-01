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

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_LIST_V4;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group4")
public class ListV4PwIT extends ListV3PwIT {
    @Override
    protected String listResourceType() {
        return RT_LIST_V4;
    }

    @Override
    @Test
    public void testCreateFixedList() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        String page1 = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        String page2 = authorClient.createPage("page_2", "page_2", rootPage, defaultPageTemplate).getSlingPath();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./listFrom']", "static");
        addStaticItem(page1, null);
        addStaticItem(page2, null);
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-list")).containsText("page_1");
        assertThat(page.locator(".cmp-list")).containsText("page_2");
    }

    @Test
    public void testCreateStaticListWithPages() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        String page1 = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        String page2 = authorClient.createPage("page_2", "page_2", rootPage, defaultPageTemplate).getSlingPath();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./listFrom']", "static");
        addStaticItem(page1, null);
        addStaticItem(page2, null);
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-list")).containsText("page_1");
        assertThat(page.locator(".cmp-list")).containsText("page_2");
    }

    @Test
    public void testCreateStaticListWithPagesAndExternalLink() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        String page1 = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./listFrom']", "static");
        addStaticItem(page1, null);
        addStaticItem("http://www.adobe.com", "Adobe");
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-list")).containsText("page_1");
        assertThat(page.locator(".cmp-list")).containsText("Adobe");
    }

    @Test
    public void testCreateStaticListWithPagesAndExternalLinkCheckInteraction() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
        selectInCoralSelect("[name='./listFrom']", "children");
        assertThat(dialog().locator("input[name='./maxItems']")).isVisible();
        selectInCoralSelect("[name='./listFrom']", "static");
        assertThat(dialog().locator("input[name='./maxItems']")).isHidden();
    }

    @Test
    public void testConvertV3StaticListContentToV4Content() throws Exception {
        String componentPath = addStandaloneComponent(RT_LIST_V4, "list");
        String page1 = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        ArrayList<NameValuePair> properties = new ArrayList<>();
        properties.add(new BasicNameValuePair("listFrom", "static"));
        properties.add(new BasicNameValuePair("pages", page1));
        authorClient.setPropertiesString(componentPath, properties, 200);
        JsonNode before = authorClient.doGetJson(componentPath, -1);
        if (before.has("pages")) {
            openEditDialog(componentPath);
            saveDialog();
        }
        JsonNode after = authorClient.doGetJson(componentPath, -1);
        if (!after.has("static")) {
            throw new AssertionError("v4 list configuration was not converted to static items");
        }
    }

    private void addStaticItem(String link, String text) {
        Locator multifield = dialog().locator("coral-multifield[data-granite-coral-multifield-name='./static']");
        multifield.locator("button[coral-multifield-add]").last().click();
        Locator item = multifield.locator("coral-multifield-item").last();
        Locator input = item.locator("foundation-autocomplete input[is='coral-textfield']").first();
        input.fill(link);
        input.press("Tab");
        if (text != null) {
            item.locator("input[name$='linkText']").fill(text);
        }
    }
}
