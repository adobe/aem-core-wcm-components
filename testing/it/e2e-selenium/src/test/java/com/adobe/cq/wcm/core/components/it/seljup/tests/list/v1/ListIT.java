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

package com.adobe.cq.wcm.core.components.it.seljup.tests.list.v1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.ListEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v1.List;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group4")
public class ListIT extends AuthorBaseUITest {
    private static String searchValue = "Victor Sullivan";
    private static String tag1 = "ellie";
    private static String tag2 = "joel";
    private static String description = "This is a child page";

    private String compPath;
    private String parentPath;
    private String testPage;
    private String page1Path;
    private String page2Path;
    private String page21Path;
    private String page22Path;
    private String page3Path;
    private String page4Path;
    private String page5Path;
    private EditorPage editorPage;
    private List list;
    private String tag1Path;
    private String tag2Path;

    protected String textRT;
    protected String listRT;

    protected void setComponentResources() {
        textRT = Commons.RT_TEXT_V1;
        listRT = Commons.RT_LIST_V1;
    }


    protected void setup() throws ClientException {
        // add 2 tags
        tag1Path = Commons.addTag(adminClient, tag1);
        tag2Path = Commons.addTag(adminClient, tag2);
        // create a separate parent page
        parentPath = authorClient.createPage("parent_page", "parent_page", rootPage, defaultPageTemplate).getSlingPath();
        // add page 1
        page1Path = authorClient.createPage("page_1", "page_1", parentPath, defaultPageTemplate).getSlingPath();
        // set tag on the page
        String[] tags = new String[]{tag1};
        Commons.setTagsToPage(authorClient, page1Path, tags, 200);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:description", description);
        Commons.editNodeProperties(authorClient, page1Path + "/jcr:content", data);
        // add page 2
        page2Path = authorClient.createPage("page_2", "page_2", parentPath, defaultPageTemplate).getSlingPath();
        // add a text component
        String text1Path = Commons.addComponentWithRetry(authorClient, textRT, page2Path + Commons.relParentCompPath, "text");
        //set some text in the text component
        data.clear();
        data.put("text", searchValue);
        Commons.editNodeProperties(authorClient, text1Path, data);
        // create subpage for page 2
        page21Path = authorClient.createPage("sub_2_1", "sub_2_1", page2Path, defaultPageTemplate).getSlingPath();
        // create second sub page for page 2
        page22Path = authorClient.createPage("sub_2_2", "sub_2_2", page2Path, defaultPageTemplate).getSlingPath();
        // add page 3
        page3Path = authorClient.createPage("page_3", "page_3", parentPath, defaultPageTemplate).getSlingPath();
        // set 2 tags on the page
        tags = new String[]{tag1, tag2};
        Commons.setTagsToPage(authorClient, page3Path, tags, 200);
        // create page 4
        page4Path = authorClient.createPage("page_4", "page_4", parentPath, defaultPageTemplate).getSlingPath();
        // create a sub page for page 4
        String page41Path = authorClient.createPage("sub_4_1", "sub_4_1", page4Path, defaultPageTemplate).getSlingPath();
        // add a text component
        String text2Path = Commons.addComponentWithRetry(authorClient, textRT, page41Path + Commons.relParentCompPath, "text");
        //set some text in the text component
        data.clear();
        data.put("text", searchValue);
        Commons.editNodeProperties(authorClient, text2Path, data);
        // create page 5
        page5Path = authorClient.createPage("page_5", "page_5", parentPath, defaultPageTemplate).getSlingPath();
        // set tag on the page
        tags = new String[]{tag2};
        Commons.setTagsToPage(authorClient, page5Path, tags, 200);

        // create the test page containing the list component, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the component to test page
        compPath = Commons.addComponentWithRetry(authorClient, listRT, testPage + Commons.relParentCompPath, "list");

        // open test page in page editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        list = new List();
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(parentPath, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        adminClient.deletePath("/content/cq:tags/default/" + tag1Path, HttpStatus.SC_OK);
        adminClient.deletePath("/content/cq:tags/default/" + tag2Path, HttpStatus.SC_OK);
    }

    /**
     * Test: Build a list using direct child pages
     */
    @Test
    @DisplayName("Test: Build a list using direct child pages")
    public void testCreateListDirectChildren() throws ClientException, TimeoutException, InterruptedException {
        // create 3 direct sub pages
        String subpage1Path = authorClient.createPage("direct_1", "direct_1", testPage, defaultPageTemplate).getSlingPath();
        String subpage2Path = authorClient.createPage("direct_2", "direct_2", testPage, defaultPageTemplate).getSlingPath();
        String subpage3Path = authorClient.createPage("direct_2", "direct_3", testPage, defaultPageTemplate).getSlingPath();

        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        // default setting is to build list using 'child pages', empty 'parent page' and 'child depth' = 1,
        // so we only need to save
        Commons.saveConfigureDialog();

        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("direct_1"), "direct_1 should be present in list");
        assertTrue(list.isPagePresentInList("direct_2"), "direct_2 should be present in list");
        assertTrue(list.isPagePresentInList("direct_3"), "direct_3 should be present in list");
    }

    /**
     * Test: Build a list using child pages from a different location
     */
    @Test
    @DisplayName("Test: Build a list using child pages from a different location")
    public void testCreateListChildren() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        // set parent page
        list.getEditDialog().setParentPage(parentPath);
        // close the dialog
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");

        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_2"), "page_2 should be present in list");
        assertTrue(list.isPagePresentInList("page_3"), "page_3 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
        assertTrue(list.isPagePresentInList("page_5"), "page_5 should be present in list");
    }

    /**
     * Test: Build a list using child pages and sub child pages
     */
    @Test
    @DisplayName("Test: Build a list using child pages and sub child pages")
    public void testListSubChildren() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");
        // close the dialog
        Commons.saveConfigureDialog();

        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_2"), "page_2 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_2"), "sub_2_2 should be present in list");
        assertTrue(list.isPagePresentInList("page_3"), "page_3 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
        assertTrue(list.isPagePresentInList("sub_4_1"), "sub_4_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_5"), "page_5 should be present in list");
    }

    /**
     * Test: Build a fixed list
     */
    @Test
    @DisplayName("Test: Build a fixed list")
    public void testCreateFixedList() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // select Fixed List
        editDialog.selectFromList("static");

        // add items
        editDialog.addFixedListOptions(page1Path);
        editDialog.addFixedListOptions(page21Path);
        editDialog.addFixedListOptions(page4Path);

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
    }

    /**
     * Test: Build a list using search
     */
    @Test
    @DisplayName("Test: Build a list using search")
    public void testCreateListBySearch() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();

        // set the content path
        editDialog.selectFromList("search");
        // set the search query
        editDialog.enterSearchQuery(searchValue);
        // set search location
        editDialog.setSearchLocation(parentPath);
        // close the dialog
        Commons.saveConfigureDialog();

        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_2"), "page_2 should be present in list");
        assertTrue(list.isPagePresentInList("sub_4_1"), "sub_4_1 should be present in list");
    }

    /**
     * Test: Build a list matching any tags defined
     */
    @Test
    @DisplayName("Test: Build a list matching any tags defined")
    public void testCreateListAnyTagsMatching() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set the content path
        editDialog.selectFromList("tags");
        // set parent page
        editDialog.setTageSearchRoot(parentPath);
        // search for 2 tags
        editDialog.selectInTags("default/" + tag1);
        editDialog.selectInTags("default/" + tag2);

        // set the content path
        editDialog.setTagsMatch("any");
        // close the dialog
        Commons.saveConfigureDialog();

        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_3"), "page_3 should be present in list");
        assertTrue(list.isPagePresentInList("page_5"), "page_5 should be present in list");
    }

    /**
     * Test: Build a list matching all tags defined
     */
    @Test
    @DisplayName("Test: Build a list matching all tags defined")
    public void testCreateListAllTagsMatching() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set the content path
        editDialog.selectFromList("tags");
        // set parent page
        editDialog.setTageSearchRoot(parentPath);
        // search for 2 tags
        editDialog.selectInTags("default/" + tag1);
        editDialog.selectInTags("default/" + tag2);

        // set the content path
        editDialog.setTagsMatch("all");
        // close the dialog
        Commons.saveConfigureDialog();

        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_3"), "page_3 should be present in list");
    }

    /**
     * Test: order list by title
     */
    @Test
    @DisplayName("Test: order list by title")
    public void testOrderByTitle() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");

        // set order by title
        editDialog.setOrderBy("title");

        // set sort order to ascending
        editDialog.setSortOrder("asc");

        // close the dialog
        Commons.saveConfigureDialog();

        // check if they are listed in the right order
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInListAtPosition(0, "page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(1, "page_2"), "page_2 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(2, "page_3"), "page_3 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(3, "page_4"), "page_4 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(4, "page_5"), "page_5 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(5, "sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(6, "sub_2_2"), "sub_2_2 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(7, "sub_4_1"), "sub_4_1 should be present in list");
    }

    /**
     * Test: change ordering of a list to descending
     */
    @Test
    @DisplayName("Test: change ordering of a list to descending")
    public void testChangeOrderingTitle() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");

        // set order by title
        editDialog.setOrderBy("title");

        // set sort order to ascending
        editDialog.setSortOrder("desc");

        // close the dialog
        Commons.saveConfigureDialog();

        // check if they are listed in the right order
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInListAtPosition(0, "sub_4_1"), "sub_4_1 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(1, "sub_2_2"), "sub_2_2 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(2, "sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(3, "page_5"), "page_5 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(4, "page_4"), "page_4 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(5, "page_3"), "page_3 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(6, "page_2"), "page_2 should be present in list");
        assertTrue(list.isPagePresentInListAtPosition(7, "page_1"), "page_1 should be present in list");
    }

    /**
     * Test: set max item
     */
    @Test
    @DisplayName("Test: set max item")
    public void testSetMaxItems() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");
        // close the dialog
        Commons.saveConfigureDialog();

        // by default there should be 8
        Commons.switchContext("ContentFrame");
        assertTrue(list.getListLength() == 8, "By default there should be 8 items");
        Commons.switchToDefaultContext();

        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        // set max Items to 4
        editDialog.setMaxItems("4");
        // close the dialog
        Commons.saveConfigureDialog();

        // now it should only render 4 entries
        Commons.switchContext("ContentFrame");
        assertTrue(list.getListLength() == 4, "After setting max items tp 4 there should be 4 items");
        Commons.switchToDefaultContext();
    }

    /**
     * Test: order list by last modified date
     */
    @Test
    @DisplayName("Test: order list by last modified date")
    public void testOrderByLastModifiedDate() throws ClientException, TimeoutException, InterruptedException {
        // modify page 5
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "Modified Page 5");
        Commons.editNodeProperties(authorClient, page5Path + "/jcr:content", data);

        // modify page 1
        data.put("jcr:title", "Modified Page 1");
        Commons.editNodeProperties(authorClient, page1Path + "/jcr:content", data);

        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");

        // set order by modified
        editDialog.setOrderBy("modified");
        // set sort order to ascending
        editDialog.setSortOrder("asc");

        // close the dialog
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        // page 5 should be at 7th place
        assertTrue(list.isPagePresentInListAtPosition(6, "Modified Page 5"), "page 5 should be at 7th place");
        // page 1 should be at 8th place
        assertTrue(list.isPagePresentInListAtPosition(7, "Modified Page 1"), "page 1 should be at 7th place");
    }

    /**
     * Test: order list by last modified date
     */
    @Test
    @DisplayName("Test: order list by last modified date")
    public void testChangeOrderingDate() throws ClientException, TimeoutException, InterruptedException {
        // modify page 3
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "Modified Page 3");
        Commons.editNodeProperties(authorClient, page3Path + "/jcr:content", data);

        // modify page 2
        data.put("jcr:title", "Modified Page 2");
        Commons.editNodeProperties(authorClient, page2Path + "/jcr:content", data);

        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        // set depth to 2
        editDialog.setChildDepth("2");

        // set order by modified
        editDialog.setOrderBy("modified");
        // set sort order to ascending
        editDialog.setSortOrder("desc");

        // close the dialog
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        // page 5 should be at 7th place
        assertTrue(list.isPagePresentInListAtPosition(0, "Modified Page 2"), "page 2 should be at 7th place");
        // page 1 should be at 8th place
        assertTrue(list.isPagePresentInListAtPosition(1, "Modified Page 3"), "page 3 should be at 7th place");
    }

    /**
     * Test: item settings - link items option
     */
    @Test
    @DisplayName("Test: item settings - link items option")
        public void testLinkItemsForList() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        editDialog.openSettings();
        editDialog.clickLinkItems();

        // close the dialog
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(list.isPageLinkPresent("page_1"),"Page link should be present");
    }

    /**
     * Test: item settings - show description
     */
    @Test
    @DisplayName("Test: item settings - show description")
    public void testShowDescriptionForList() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        editDialog.openSettings();
        editDialog.clickShowDescription();

        // close the dialog
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentWithDescription("This is a child page"),"Page should be present with description This is a child page");
    }

    /**
     * Test: item settings - show date
     */
    @Test
    @DisplayName("Test: item settings - show date")
    public void testShowDateForList() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();
        // set parent page
        editDialog.setParentPage(parentPath);
        editDialog.openSettings();
        editDialog.clickShowModificationDate();

        // close the dialog
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String text = sdf.format(date);
        String currentDate = text.substring(0, 10);
        assertTrue(list.isPagePresentWithDate(currentDate),"Page should be present with date " + currentDate);
    }

}
