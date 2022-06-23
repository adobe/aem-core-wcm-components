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


package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.util.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb.BreadcrumbEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb.BaseBreadcrumbItems;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb.v2.BreadcrumbList;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreadcrumbTests {

    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbTests.class);
    private List<String> testPages;
    private String cmpPath;
    private EditorPage editorPage;
    private BaseBreadcrumbItems breadcrumbItems;
    private static String componentName = "breadcrumb";

    private void createTestpages(CQClient client,String rootPage, String defaultPageTemplate, int maxLevel) throws ClientException {
        String parentPage = rootPage;
        testPages = new ArrayList<String>();
        for(int i = 1; i <= maxLevel; i++) {
            String pageTitle = "testPage_L" + i;
            String testPage = client.createPage(pageTitle, pageTitle, parentPage, defaultPageTemplate).getSlingPath();
            testPages.add(testPage);
            parentPage = testPage;
        }
    }

    public void setup(CQClient client, String breadcrumbRT, String rootPage,
                      String defaultPageTemplate, String clientlib, BaseBreadcrumbItems breadcrumbItems) throws ClientException {
        createTestpages(client,rootPage, defaultPageTemplate, 5);

        cmpPath = Commons.addComponentWithRetry(client,breadcrumbRT,testPages.get(4) + Commons.relParentCompPath, componentName, null,
            RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
            HttpServletResponse.SC_OK, HttpServletResponse.SC_CREATED);

        Commons.checkProxiedClientLibrary(client, clientlib);

        editorPage = new PageEditorPage(testPages.get(4));
        editorPage.open();

        this.breadcrumbItems = breadcrumbItems;
    }

    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPages.get(0), true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    private void openConfiguration(String compPath) throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + compPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(compPath);
        EditableToolbarAssertion editableToolbarAssertion = new EditableToolbarAssertion(editableToolbar,
            "editable toolbar of none style selector enabled component - %s button is not displayed while it should");
        editableToolbarAssertion.assertConfigureButton(true);
        editableToolbar.clickConfigure();
    }

    public void testHideCurrent() throws TimeoutException, InterruptedException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should be active");
        Commons.switchToDefaultContext();

        openConfiguration(cmpPath);
        BreadcrumbEditDialog editDialog = new BreadcrumbEditDialog();
        editDialog.setHideCurrent(true);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemActive("testPage_L5"), "testPage_L5 should not be active");
        Commons.switchToDefaultContext();
    }

    public void testShowHidden(CQClient client) throws ClientException, TimeoutException, InterruptedException {
        HashMap<String, String> data = new HashMap<String, String>();
        Commons.hidePage(client, testPages.get(2));
        editorPage.refresh();

        Commons.switchContext("ContentFrame");
        assertTrue(!breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should not be visible");
        Commons.switchToDefaultContext();

        openConfiguration(cmpPath);
        BreadcrumbEditDialog editDialog = new BreadcrumbEditDialog();
        editDialog.setShowHidden(true);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.isItemPresent("testPage_L3"), "testPage_L3 should be visible");
        Commons.switchToDefaultContext();
    }

    public void testChangeStartLevel() throws InterruptedException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        openConfiguration(cmpPath);

        BreadcrumbEditDialog editDialog = new BreadcrumbEditDialog();
        assertTrue(editDialog.getStartLevelValue() == 2,"Start level should be 2");
        editDialog.setStartLevelValue("4");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 3, "number of breadcrumb items should be 3");
        Commons.switchToDefaultContext();
    }

    public void testSetZeroStartLevel() throws InterruptedException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        Commons.switchToDefaultContext();
        openConfiguration(cmpPath);

        BreadcrumbEditDialog editDialog = new BreadcrumbEditDialog();
        assertTrue(editDialog.getStartLevelValue() == 2,"Start level should be 2");
        editDialog.setStartLevelValue("0");
        assertTrue(editDialog.checkInvalidStartLevel(), "Setting Start Level value to 0 is not allowed");
    }

    public void testSet100StartLevel() throws InterruptedException, TimeoutException {
        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
        openConfiguration(cmpPath);

        BreadcrumbEditDialog editDialog = new BreadcrumbEditDialog();
        assertTrue(editDialog.getStartLevelValue() == 2,"Start level should be 2");
        editDialog.setStartLevelValue("100");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(breadcrumbItems.getItems().size() == 0, "number of breadcrumb items should be 0");
        assertTrue(breadcrumbItems.getActiveItems().size() == 0, "number of active breadcrumb items should be 0");
        Commons.switchToDefaultContext();
    }

    public void testStructureData() {
        Commons.switchContext("ContentFrame");
        BreadcrumbList list = new BreadcrumbList();
        assertTrue(list.isExisting() , "Breadcrumb list should be present");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
    }

}
