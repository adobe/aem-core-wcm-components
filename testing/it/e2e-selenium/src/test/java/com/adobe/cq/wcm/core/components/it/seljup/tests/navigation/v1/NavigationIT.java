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

package com.adobe.cq.wcm.core.components.it.seljup.tests.navigation.v1;

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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.navigation.NavigationEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.navigation.v1.Navigation;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class NavigationIT extends AuthorBaseUITest {

    protected String page1path;
    protected String compPath;
    protected EditorPage editorPage;
    protected Navigation navigation;
    protected String navigationRT;

    private void setupResources() {
        navigationRT = Commons.RT_NAVIGATION_V1;
    }

    protected void setup() throws ClientException {
        // level 1
        page1path = authorClient.createPage("page_1", "page_1", rootPage, defaultPageTemplate).getSlingPath();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "Page 1");
        Commons.editNodeProperties(authorClient, page1path, data);

        // level 2
        String page11path = authorClient.createPage("page_1_1", "page_1_1", page1path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "Page 1.1");
        data.put("./jcr:content/sling:vanityPath", "/page_1_1_vanity");
        Commons.editNodeProperties(authorClient, page11path, data);

        // level 2 1
        String page111path = authorClient.createPage("page_1_1_1", "page_1_1_1", page11path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "Page 1.1.1");
        Commons.editNodeProperties(authorClient, page111path, data);

        // level 2 2
        String page112path = authorClient.createPage("page_1_1_2", "page_1_1_2", page11path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/hideInNav", "true");
        Commons.editNodeProperties(authorClient, page112path, data);

        // level 2 3
        String page113path = authorClient.createPage("page_1_1_3", "page_1_1_3", page11path, defaultPageTemplate).getSlingPath();
        data.clear();
        data.put("_charset_", "UTF-8");
        data.put("./jcr:content/navTitle", "Page 1.1.3");
        Commons.editNodeProperties(authorClient, page113path, data);

        // add the component to test page
        compPath = Commons.addComponentWithRetry(authorClient, navigationRT, page11path + Commons.relParentCompPath, "navigation");

        //open test page in page editor
        editorPage = new PageEditorPage(page11path);
        editorPage.open();

        navigation = new Navigation();
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(page1path, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Test default configuration
     */
    @Test
    @DisplayName("Test default configuration")
    public void testDefaultConfiguration() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        NavigationEditDialog editDialog = navigation.getEditDialog();

        assertTrue(editDialog.isCollectAllPagesChecked(), "Collect All Page should be checked by default");
        //TODO: In Spectrum UI Structure Depth input is visible even though Collect All Pages is checked.
        // Enable this when that issue gets resolved
        //assertTrue(!editDialog.isStructureDepthVisible(), "Structure depth input should not be visible");
        editDialog.setNavigationRoot(page1path);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(navigation.navigationItemsCount() == 3, "Total navigation items should be 3");
        assertTrue(navigation.isActiveItemContainValue("0", "Page 1.1"), "active Level 0 item should be Page 1.1");
        assertTrue(navigation.isLinkItemPresentContainsValue("/page_1_1.html"), "Link item should be present for page_1_1.html");
        assertTrue(navigation.isItemPresentContainValue("1", "Page 1.1.1"), "Page 1.1.1 item should be present at Level 1");
        assertTrue(!navigation.isItemPresentContainValue("1", "Page 1.1.2"), "Page 1.1.2 item should not be present at Level 1");
        assertTrue(navigation.isItemPresentContainValue("1", "Page 1.1.3"), "Page 1.1.3 item should be present at Level 1");
    }

    /**
     * Include Navigation Root
     */
    @Test
    @DisplayName("Include Navigation Root")
    public void testIncludeNavigationRoot() throws InterruptedException, TimeoutException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        NavigationEditDialog editDialog = navigation.getEditDialog();

        assertTrue(editDialog.isCollectAllPagesChecked(), "Collect All Page should be checked by default");
        //TODO: In Spectrum UI Structure Depth input is visible even though Collect All Pages is checked.
        // Enable this when that issue gets resolved
        //assertTrue(!editDialog.isStructureDepthVisible(), "Structure depth input should not be visible");
        editDialog.setNavigationRoot(page1path);
        editDialog.setStructureStart("0");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(navigation.navigationItemsCount() == 4, "Total navigation items should be 4");
        assertTrue(navigation.isActiveItemContainValue("0","Page 1"), "active Level 0 item should be Page 1");
        assertTrue(navigation.isActiveItemContainValue("1","Page 1.1"), "active Level 1 item should be Page 1.1");
        assertTrue(navigation.isLinkItemPresentContainsValue("/page_1_1.html"), "Link item should be present for page_1_1.html");

        assertTrue(navigation.isItemPresentContainValue("2", "Page 1.1.1"), "Page 1.1.1 item should be present at Level 2");
        assertTrue(!navigation.isItemPresentContainValue("2", "Page 1.1.2"), "Page 1.1.2 item should not be present at Level 2");
        assertTrue(navigation.isItemPresentContainValue("2", "Page 1.1.3"), "Page 1.1.3 item should be present at Level 2");

    }

    /**
     * Change max depth level
     */
    @Test
    @DisplayName("Change max depth level")
    public void testChangeStructureDepthLevel() throws TimeoutException, InterruptedException {
        // open the configuration dialog
        Commons.openEditDialog(editorPage, compPath);
        NavigationEditDialog editDialog = navigation.getEditDialog();
        editDialog.setNavigationRoot(page1path);
        editDialog.clickCollectAllPages();
        assertTrue(editDialog.isStructureDepthVisible(), "Structure depth input should be visible");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(navigation.navigationItemsCount() == 1, "Total navigation items should be 1");
        assertTrue(navigation.isActiveItemContainValue("0","Page 1.1"), "active Level 0 item should be Page 1.1");
        assertTrue(!navigation.isItemPresentContainValue("1", "Page 1.1.1"), "Page 1.1.1 item should not be present at Level 1");
        assertTrue(!navigation.isItemPresentContainValue("1", "Page 1.1.2"), "Page 1.1.2 item should not be present at Level 1");
        assertTrue(!navigation.isItemPresentContainValue("1", "Page 1.1.3"), "Page 1.1.3 item should not be present at Level 1");
    }
}
