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

package com.adobe.cq.wcm.core.components.it.seljup.tests.list.v3;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.list.ListEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group4")
public class ListIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.list.v2.ListIT {

    protected void setComponentResources() {
        textRT = Commons.RT_TEXT_V2;
        listRT = Commons.RT_LIST_V3;
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    /**
     * Test: Build a mixed list with pages.
     */
    @Test
    @DisplayName("Test: Build a mixed list with pages")
    public void testCreateMixedListWithPages() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select Fixed List
        editDialog.selectFromList("mixed");

        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addMixedListPage(page1Path);
        editDialog.addMixedListPage(page21Path);
        editDialog.addMixedListPage(page4Path);

        // close the dialog
        Commons.saveConfigureDialog();
        // check if the correct pages are listed
        Commons.switchContext("ContentFrame");
        assertTrue(list.isPagePresentInList("page_1"), "page_1 should be present in list");
        assertTrue(list.isPagePresentInList("sub_2_1"), "sub_2_1 should be present in list");
        assertTrue(list.isPagePresentInList("page_4"), "page_4 should be present in list");
    }

    /**
     * Test: Build a mixed list with pages and external link
     */
    @Test
    @DisplayName("Test: Build a mixed list with pages and external link")
    public void testCreateMixedListWithPagesAndExternalLink() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select Fixed List
        editDialog.selectFromList("mixed");

        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addMixedListPage(page1Path);
        editDialog.addMixedListPage(page21Path);

        assertFalse(editDialog.isExternalLinksMode());

        editDialog.addMixedListLink("http://www.adobe.com", "Adobe");

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
     * Test: Build a mixed list with pages and external link and check edit dialog interaction
     */
    @Test
    @DisplayName("Test: Build a mixed list with pages and external link and check edit dialog interaction")
    public void testCreateMixedListWithPagesAndExternalLinkCheckInteraction() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        ListEditDialog editDialog = list.getEditDialog();

        assertTrue(editDialog.isMaxItemsDisplayed());

        // select Fixed List
        editDialog.selectFromList("mixed");
        assertFalse(editDialog.isMaxItemsDisplayed());

        // add items
        editDialog.addMixedListPage(page1Path);
        assertFalse(editDialog.isExternalLinksMode());
        editDialog.addMixedListLink("http://www.adobe.com", "Adobe");
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
        editDialog = list.getEditDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertTrue(editDialog.isExternalLinksMode());

        // change to fixed list
        editDialog.selectFromList("static");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(editDialog.isMaxItemsDisplayed());
        assertFalse(editDialog.isExternalLinksMode());

        // change to mixed list
        editDialog.selectFromList("mixed");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertTrue(editDialog.isExternalLinksMode());

        // remove external link
        editDialog.removeLastMixedListLink();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(editDialog.isMaxItemsDisplayed());
        assertFalse(editDialog.isExternalLinksMode());
    }
}
