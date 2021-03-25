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

package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v2;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v2.BreadcrumbItemsV2;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v2.BreadcrumbList;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import com.adobe.qe.selenium.pagewidgets.cq.EditableToolbar;
import com.codeborne.selenide.WebDriverRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreadcrumbV2IT extends AuthorBaseUITest {

    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbV2IT.class);
    private List<String> testPages;
    private String proxyPath;
    private String cmpPath;
    private PageEditorPage editorPage;
    private com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbItems breadcrumbItems;
    private static String componentName = "breadcrumb";
    private static String rtBreadcrumb_v1 = "core/wcm/components/breadcrumb/v2/breadcrumb";


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

    private void openConfiguration(String compPath) throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + compPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        EditableToolbarAssertion editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is not displayed while it should");
        editableToolbarAssertion.assertConfigureButton(true);
        editableToolbar.clickConfigure();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        createTestpages(5);

        proxyPath = Commons.createProxyComponent(adminClient, rtBreadcrumb_v1, Commons.proxyPath, null, null);

        cmpPath = Commons.addComponent(adminClient, proxyPath,testPages.get(4) + Commons.relParentCompPath, componentName, null);

        Commons.checkProxiedClientLibrary(adminClient,"/core/wcm/components/breadcrumb/v2/breadcrumb/clientlibs/site.css");

        editorPage = new PageEditorPage(testPages.get(4));
        editorPage.open();

        breadcrumbItems = new BreadcrumbItemsV2();

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
    public void testHideCurrent() throws InterruptedException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should be active");
        Commons.switchToDefaultContext();

        openConfiguration(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        configDialog.setHideCurrent(true);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should not be active");
        Commons.switchToDefaultContext();

    }


    /**
     * Test: Set the Show Hidden flag
     */
    @Test
    @DisplayName("Test: Set the Show Hidden flag")
    public void testShowHidden() throws InterruptedException, ClientException, TimeoutException {
        HashMap<String, String> data = new HashMap<String, String>();
        Commons.hidePage(adminClient, testPages.get(2));
        editorPage.refresh();


        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should not be visible");
        Commons.switchToDefaultContext();

        openConfiguration(testPages.get(4) + Commons.relParentCompPath + componentName);
        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        configDialog.setShowHidden(true);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should be visible");
        Commons.switchToDefaultContext();
    }

    /**
     * Test: Change the start level
     */
    @Test
    @DisplayName("Test: Change the start level")
    public void changeStartLevel() throws InterruptedException, ClientException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        openConfiguration(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        assertTrue(configDialog.getStartLevelValue() == 2,"Start level should be 2");
        configDialog.setStartLevelValue("4");
        Commons.saveConfigureDialog();

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
    public void setZeroStartLevel() throws InterruptedException, ClientException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        openConfiguration(testPages.get(4) + Commons.relParentCompPath + componentName);

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
    public void set100StartLevel() throws InterruptedException, ClientException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
        openConfiguration(testPages.get(4) + Commons.relParentCompPath + componentName);

        BreadcrumbConfigDialog configDialog = new BreadcrumbConfigDialog();
        assertTrue(configDialog.getStartLevelValue() == 2,"Start level should be 2");
        configDialog.setStartLevelValue("100");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 0, "number of breadcrumb items should be 0");
        assertTrue(breadcrumbItems.getActiveItems().size() == 0, "number of active breadcrumb items should be 0");
        Commons.switchToDefaultContext();
    }


    /**
     * Test: structure data (schema.org)
     */
    @Test
    @DisplayName("Test: structure data (schema.org)")
    public void testStructureData() {
        Commons.switchContext("ContentFrame");
        BreadcrumbList list = new BreadcrumbList();
        assertTrue(list.isExisting() , "Breadcrumb list should be present");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
    }


}
