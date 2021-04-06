package com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.wcm.core.components.it.seljup.assertion.EditableToolbarAssertion;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbItems;
import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v2.BreadcrumbList;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.tests.breadcrumb.v2.BreadcrumbV2IT;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import com.adobe.qe.selenium.pagewidgets.cq.EditableToolbar;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BreadcrumbTests {



    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbTests.class);
    private List<String> testPages;
    private String proxyPath;
    private String cmpPath;
    private PageEditorPage editorPage;
    private BreadcrumbItems breadcrumbItems;
    private static String componentName = "breadcrumb";

    private void createTestpages(CQClient client,String rootPage, String defaultPageTemplate, int maxLevel) throws ClientException {
        String parentPage = rootPage;
        testPages = new ArrayList<String>();
        for(int i = 1; i <= maxLevel; i++) {
            String pageLabel = "testPage_L" + i;
            String pageTitle = "Test Page L" + i + " Title";
            String testPage = Commons.createPage(client, pageLabel, pageTitle, parentPage, defaultPageTemplate,"core/wcm/tests/components/test-page-v2");
            testPages.add(testPage);
            parentPage = testPage;
        }
    }

    public void setup(CQClient client, String rtBreadcrumb,
                             String rootPage, String defaultPageTemplate, String clientlib, BreadcrumbItems breadcrumbItems) throws ClientException {
        createTestpages(client,rootPage, defaultPageTemplate, 5);

        proxyPath = Commons.createProxyComponent(client, rtBreadcrumb, Commons.proxyPath, null, null);

        cmpPath = Commons.addComponent(client, proxyPath,testPages.get(4) + Commons.relParentCompPath, componentName, null);

        Commons.checkProxiedClientLibrary(client, clientlib);

        editorPage = new PageEditorPage(testPages.get(4));
        editorPage.open();

        this.breadcrumbItems = breadcrumbItems;
    }

    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPages.get(0), true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        Commons.deleteProxyComponent(client, proxyPath);
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

    public void testHideCurrent() throws TimeoutException {
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

    public void testShowHidden(CQClient client) throws InterruptedException, ClientException, TimeoutException {
        HashMap<String, String> data = new HashMap<String, String>();
        Commons.hidePage(client, testPages.get(2));
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

    public void testStructureData() {
        Commons.switchContext("ContentFrame");
        BreadcrumbList list = new BreadcrumbList();
        assertTrue(list.isExisting() , "Breadcrumb list should be present");
        assertTrue(breadcrumbItems.getItems().size() == 5, "number of breadcrumb items should be 5");
        assertTrue(breadcrumbItems.getActiveItems().size() == 1, "number of active breadcrumb items should be 1");
        Commons.switchToDefaultContext();
    }

}
