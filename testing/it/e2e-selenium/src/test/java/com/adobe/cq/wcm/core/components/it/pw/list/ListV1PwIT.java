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
package com.adobe.cq.wcm.core.components.it.pw.list;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import java.util.HashMap;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEXT_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group4")
public class ListV1PwIT extends ComponentPwBaseTest {

    private static final String LIST = ".cmp-list";
    private static final String PARENT = "coral-dialog[open] foundation-autocomplete[name='./parentPage'] input";

    protected String listResourceType() {
        return com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_LIST_V1;
    }

    private void configure(String source) {
        selectInCoralSelect("[name='./listFrom']", source);
    }

    private void selectTag(String tagPath) {
        Locator tags = dialog().locator("foundation-autocomplete[name='./tags'] input");
        tags.fill(tagPath);
        Locator suggestion = page.locator("coral-overlay coral-buttonlist button[value='" + tagPath + "']");
        assertThat(suggestion).isVisible();
        suggestion.click();
    }

    private void setTagSearchRoot(String path) {
        selectAutocomplete("[name='./tagsSearchRoot']", path);
    }

    private void setParent(String path) {
        dialog().locator(PARENT).fill(path);
    }

    private void setDepth(String depth) {
        dialog().locator("[name='./childDepth']").fill(depth);
    }

    private void saveAndAssertVisible(String... titles) {
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        for (String title : titles) {
            assertThat(page.locator(LIST)).containsText(title);
        }
    }

    @Test
    public void testCreateListDirectChildren() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        authorClient.createPage("direct_1", "direct_1", testPage, defaultPageTemplate);
        authorClient.createPage("direct_2", "direct_2", testPage, defaultPageTemplate);
        authorClient.createPage("direct_3", "direct_3", testPage, defaultPageTemplate);

        openEditDialog(componentPath);
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(LIST)).containsText("direct_1");
        assertThat(page.locator(LIST)).containsText("direct_2");
        assertThat(page.locator(LIST)).containsText("direct_3");
    }

    @Test
    public void testCreateListChildren() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        saveAndAssertVisible("page_1", "page_2", "page_3", "page_4", "page_5");
    }

    @Test
    public void testListSubChildren() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        saveAndAssertVisible("page_1", "page_2", "sub_2_1", "sub_2_2", "page_3", "page_4", "sub_4_1", "page_5");
    }

    @Test
    public void testCreateFixedList() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        String page1 = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        String page2 = authorClient.createPage("page_2", "page_2", rootPage, defaultPageTemplate).getSlingPath();
        String page3 = authorClient.createPage("page_3", "page_3", rootPage, defaultPageTemplate).getSlingPath();
        openEditDialog(componentPath);
        configure("static");
        addStaticPage(page1);
        addStaticPage(page2);
        addStaticPage(page3);
        saveAndAssertVisible("page_1", "page_2", "page_3");
    }

    @Test
    public void testCreateListBySearch() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        configure("search");
        dialog().locator("[name='./query']").fill("Victor Sullivan");
        dialog().locator("[name='./searchRoot'] input").fill(testPage + "-parent");
        saveAndAssertVisible("page_2", "sub_4_1");
    }

    @Test
    public void testCreateListAnyTagsMatching() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createTaggedListPages();
        openEditDialog(componentPath);
        configure("tags");
        setTagSearchRoot(testPage + "-parent");
        selectTag("default/ellie");
        selectTag("default/joel");
        selectInCoralSelect("[name='./tagsMatch']", "any");
        saveAndAssertVisible("page_1", "page_3", "page_5");
    }

    @Test
    public void testCreateListAllTagsMatching() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createTaggedListPages();
        openEditDialog(componentPath);
        configure("tags");
        setTagSearchRoot(testPage + "-parent");
        selectTag("default/ellie");
        selectTag("default/joel");
        selectInCoralSelect("[name='./tagsMatch']", "all");
        saveAndAssertVisible("page_3");
    }

    @Test
    public void testOrderByTitle() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        selectInCoralSelect("[name='./orderBy']", "title");
        selectInCoralSelect("[name='./sortOrder']", "asc");
        saveAndAssertVisible("page_1", "page_2", "page_3", "page_4", "page_5");
    }

    @Test
    public void testChangeOrderingTitle() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        selectInCoralSelect("[name='./orderBy']", "title");
        selectInCoralSelect("[name='./sortOrder']", "desc");
        saveAndAssertVisible("sub_4_1", "sub_2_2", "sub_2_1", "page_5");
    }

    @Test
    public void testSetMaxItems() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(LIST + " > li")).hasCount(8);
        openEditDialog(componentPath);
        dialog().locator("[name='./maxItems']").fill("4");
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(LIST + " > li")).hasCount(4);
    }

    @Test
    public void testOrderByLastModifiedDate() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        selectInCoralSelect("[name='./orderBy']", "modified");
        selectInCoralSelect("[name='./sortOrder']", "asc");
        saveAndAssertVisible("Modified Page 5", "Modified Page 1");
    }

    @Test
    public void testChangeOrderingDate() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        setDepth("2");
        selectInCoralSelect("[name='./orderBy']", "modified");
        selectInCoralSelect("[name='./sortOrder']", "desc");
        saveAndAssertVisible("Modified Page 2", "Modified Page 3");
    }

    @Test
    public void testLinkItemsForList() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        checkCoralCheckbox("./linkItems");
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(LIST + " a")).hasCount(5);
    }

    @Test
    public void testShowDescriptionForList() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        checkCoralCheckbox("./showDescription");
        saveAndAssertVisible("This is a child page");
    }

    @Test
    public void testShowDateForList() throws Exception {
        String componentPath = addStandaloneComponent(listResourceType(), "list");
        createListPages();
        openEditDialog(componentPath);
        setParent(testPage + "-parent");
        checkCoralCheckbox("./showModificationDate");
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(LIST)).containsText("-");
    }

    private void createListPages() throws Exception {
        String parentName = testPage.substring(testPage.lastIndexOf('/') + 1) + "-parent";
        String parent = authorClient.createPage(parentName, parentName, rootPage, defaultPageTemplate).getSlingPath();
        String tag1 = Commons.addTag(authorClient, "ellie");
        String tag2 = Commons.addTag(authorClient, "joel");
        for (int i = 1; i <= 5; i++) {
            String path = authorClient.createPage("page_" + i, "page_" + i, parent, defaultPageTemplate).getSlingPath();
            if (i == 1) {
                Commons.setTagsToPage(authorClient, path, new String[]{tag1}, 200);
                HashMap<String, String> properties = new HashMap<>();
                properties.put("jcr:description", "This is a child page");
                Commons.editNodeProperties(authorClient, path + "/jcr:content", properties);
            } else if (i == 3) {
                Commons.setTagsToPage(authorClient, path, new String[]{tag1, tag2}, 200);
            } else if (i == 5) {
                Commons.setTagsToPage(authorClient, path, new String[]{tag2}, 200);
            }
            if (i == 2 || i == 4) {
                String child = authorClient.createPage("sub_" + i + "_1", "sub_" + i + "_1", path, defaultPageTemplate)
                    .getSlingPath();
                String textPath = Commons.addComponentWithRetry(authorClient, RT_TEXT_V1,
                    child + Commons.relParentCompPath, "text");
                HashMap<String, String> properties = new HashMap<>();
                properties.put("text", "Victor Sullivan");
                Commons.editNodeProperties(authorClient, textPath, properties);
            }
        }
    }

    private void createTaggedListPages() throws Exception {
        createListPages();
    }

    private void addStaticPage(String path) {
        Locator staticItems = dialog().locator("foundation-autocomplete[name='./static'] input");
        staticItems.fill(path);
        staticItems.press("Enter");
    }
}
