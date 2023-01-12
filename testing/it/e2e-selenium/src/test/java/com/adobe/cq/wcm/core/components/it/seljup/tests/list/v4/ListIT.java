/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.list.v4;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v4.List;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.v4.ListEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import org.apache.sling.testing.clients.ClientException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group4")
public class ListIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.list.v3.ListIT {

    protected void setComponentResources() {
        textRT = Commons.RT_TEXT_V2;
        listRT = Commons.RT_LIST_V4;
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @Override
    protected @NotNull List createList() {
        return new List();
    }

    @Override
    public List getList() {
        return (List) super.getList();
    }

    /**
     * Test: Build a fixed list.
     * We override this method to accommodate edit dialog changes.
     */
    @Test
    @DisplayName("Test: Build a fixed list")
    public void testCreateFixedList() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = getList().getEditDialog();
        // select Fixed List
        editDialog.selectFromList("static");

        // add items
        editDialog.addStaticListPage(page1Path);
        editDialog.addStaticListPage(page21Path);
        editDialog.addStaticListPage(page4Path);

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
    }

    /**
     * Test: Build a static list with pages.
     */
    @Test
    @DisplayName("Test: Build a static list with pages")
    public void testCreateStaticListWithPages() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = getList().getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select static List
        editDialog.selectFromList("static");

        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addStaticListPage(page1Path);
        editDialog.addStaticListPage(page21Path);
        editDialog.addStaticListPage(page4Path);

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
    }

    /**
     * Test: Build a static list with pages and external link
     */
    @Test
    @DisplayName("Test: Build a static list with pages and external link")
    public void testCreateStaticListWithPagesAndExternalLink() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = getList().getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select static List
        editDialog.selectFromList("static");

        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addStaticListPage(page1Path);
        editDialog.addStaticListPage(page21Path);

        assertFalse(editDialog.isExternalLinksMode());

        editDialog.addStaticListLink("http://www.adobe.com", "Adobe");

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        assertTrue(editDialog.isExternalLinksMode());

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPresentInList("Adobe"), "Adobe should be present in list");
    }

    /**
     * Test: Build a static list with pages and external link and check edit dialog interaction
     */
    @Test
    @DisplayName("Test: Build a static list with pages and external link and check edit dialog interaction")
    public void testCreateStaticListWithPagesAndExternalLinkCheckInteraction() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = getList().getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select static List
        editDialog.selectFromList("static");
        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addStaticListPage(page1Path);
        assertFalse(editDialog.isExternalLinksMode());
        editDialog.addStaticListLink("http://www.adobe.com", "Adobe");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(editDialog.isExternalLinksMode());

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages and links are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPresentInList("Adobe"), "Adobe should be present in list");

        // reopen edit dialog
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, compPath);
        editDialog = getList().getEditDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertTrue(editDialog.isExternalLinksMode());

        // change to children list
        editDialog.selectFromList("children");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(editDialog.isMaxItemsDisplayed());
        assertFalse(editDialog.isExternalLinksMode());

        // change to static list
        editDialog.selectFromList("static");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertTrue(editDialog.isExternalLinksMode());

        // remove external link
        editDialog.removeLastStaticListLink();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertFalse(editDialog.isExternalLinksMode());
    }
}
