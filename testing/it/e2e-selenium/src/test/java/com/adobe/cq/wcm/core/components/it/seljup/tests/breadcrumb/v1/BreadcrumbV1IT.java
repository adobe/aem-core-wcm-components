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

package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v1;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbItems;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v1.BreadcrumbItemsV1;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreadcrumbV1IT extends AuthorBaseUITest {

    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbV1IT.class);
    private List<String> testPages;
    private String proxyPath;
    private String cmpPath;
    private PageEditorPage editorPage;
    private BreadcrumbItems breadcrumbItems;
    private static String componentName = "breadcrumb";
    private static String rtBreadcrumb_v1 = "core/wcm/components/breadcrumb/v1/breadcrumb";


    private void createTestpages(int maxLevel) throws ClientException {
        String parentPage = rootPage;
        testPages = new ArrayList<String>();
        for(int i = 1; i <= maxLevel; i++) {
            String pageLabel = "testPage_L" + i;
            String pageTitle = "Test Page L" + i + " Title";
            String testPage = Commons.createPage(adminClient, pageLabel, pageTitle, parentPage, defaultPageTemplate,"core/wcm/tests/components/test-page-v2");
            testPages.add(testPage);
            parentPage = testPage;
        }
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        createTestpages(5);

        proxyPath = Commons.createProxyComponent(adminClient, rtBreadcrumb_v1, Commons.proxyPath, null, null);

        cmpPath = Commons.addComponent(adminClient, proxyPath,testPages.get(4) + Commons.relParentCompPath, componentName, null);

        Commons.checkProxiedClientLibrary(adminClient,"/core/wcm/components/breadcrumb/v1/breadcrumb/clientlibs/site.css");

        editorPage = new PageEditorPage(testPages.get(4));
        editorPage.open();

        breadcrumbItems = new BreadcrumbItemsV1();

    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPages.get(0), true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    /**
     * Test: Set the Hide Current flag
     */
    @Test
    @DisplayName("Test: Set the Hide Current flag")
    public void testHideCurrent() throws InterruptedException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should be active");
        Commons.switchToDefaultContext();

        Commons.openConfigureDialog(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        configDialog.setHideCurrent(true);
        Commons.saveConfigureDialog();

        editorPage.waitReady();
        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should not be active");
        Commons.switchToDefaultContext();

    }


    /**
     * Test: Set the Show Hidden flag
     */
    @Test
    @DisplayName("Test: Set the Show Hidden flag")
    public void testShowHidden() throws InterruptedException, ClientException {
        HashMap<String, String> data = new HashMap<String, String>();
       Commons.hidePage(adminClient, testPages.get(2));
        editorPage.refresh();


        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should not be visible");
        Commons.switchToDefaultContext();

        Commons.openConfigureDialog(testPages.get(4) + Commons.relParentCompPath + componentName);
        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        configDialog.setShowHidden(true);
        Commons.saveConfigureDialog();

        editorPage.waitReady();
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should be visible");
        Commons.switchToDefaultContext();
    }

    /**
     * Test: Change the start level
     */
    @Test
    @DisplayName("Test: Change the start level")
    public void changeStartLevel() throws InterruptedException, ClientException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        Commons.openConfigureDialog(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        assertTrue(configDialog.getStartLevelValue() == 2,"Start level should be 2");
        configDialog.setStartLevelValue("4");
        Commons.saveConfigureDialog();

        editorPage.waitReady();
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 3, "number of breadcrumb items should be 3");
        Commons.switchToDefaultContext();
    }


    /**
     * Test: Set the start level to lowest allowed value of 0.
     * This shouldn't render anything since level 0 is not a valid page.
     */
    @Test
    @DisplayName("Test: Set the start level to invalid value of 0.")
    public void setZeroStartLevel() throws InterruptedException, ClientException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        Commons.openConfigureDialog(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        assertTrue(configDialog.getStartLevelValue() == 2,"Start level should be 2");
        configDialog.setStartLevelValue("0");
        assertTrue(configDialog.checkInvalidStartLevel(), "Setting Start Level value to 0 is not allowed");
    }

    /**
     * Test: Set the start level to the highest possible value 100.
     * This shouldn't render anything since level 100 is higher the the current's page level.
     */
    @Test
    @DisplayName("Test: Set the start level to the highest possible value 100")
    public void set100StartLevel() throws InterruptedException, ClientException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
        Commons.openConfigureDialog(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        assertTrue(configDialog.getStartLevelValue() == 2,"Start level should be 2");
        configDialog.setStartLevelValue("100");
        Commons.saveConfigureDialog();

        editorPage.waitReady();
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 0, "number of breadcrumb items should be 0");
        assertTrue(breadcrumbItems.getActiveItems().size() == 0, "number of active breadcrumb items should be 0");
        Commons.switchToDefaultContext();
    }


}
