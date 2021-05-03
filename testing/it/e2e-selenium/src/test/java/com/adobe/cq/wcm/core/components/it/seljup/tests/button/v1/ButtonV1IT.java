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

package com.adobe.cq.wcm.core.components.it.seljup.tests.button.v1;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.button.v1.Button;
import com.adobe.cq.wcm.core.components.it.seljup.components.button.ButtonEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.codeborne.selenide.WebDriverRunner;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class ButtonV1IT extends AuthorBaseUITest {

    private String testPage;
    private String proxyComponentPath;
    private PageEditorPage editorPage;
    private Button button;
    private String cmpPath;
    private String componentName = "button";

    private ButtonEditDialog getButtonEditDialog() throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        return editorPage.openEditableToolbar(cmpPath).clickConfigure().adaptTo(ButtonEditDialog.class);
    }

    @BeforeEach
    public void setupBefore() throws Exception {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();
        proxyComponentPath = Commons.creatProxyComponent(adminClient, Commons.rtButton_v1, "Proxy Button", "button");
        addPathtoComponentPolicy(responsiveGridPath, proxyComponentPath);
        // 5.
        cmpPath = Commons.addComponent(adminClient, proxyComponentPath,testPage + Commons.relParentCompPath, componentName, null);
        editorPage = new PageEditorPage(testPage);
        button = new Button();
        editorPage.open();
    }

    @AfterEach
    public void cleanup() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Test: Set button text
     *
     * 1. open the edit dialog
     * 2. set the button text
     * 3. close the edit dialog
     * 4. verify the button is rendered with the correct text
     */
    @Test
    @DisplayName("Test: Set button text")
    void SetText() throws TimeoutException, InterruptedException {
        final String testTitle = "test button";
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        ButtonEditDialog buttonEditDialog = getButtonEditDialog();
        buttonEditDialog.getTitleField().setValue(testTitle);
        buttonEditDialog.clickPrimary();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(button.isVisible(), "Button should be visible in content frame");
        assertTrue(button.getTitle().trim().equals(testTitle), "Button Text should have been updated");
    }

    /**
     * Test: Set button link
     *
     * 1. open the edit dialog
     * 2. set the button link
     * 3. close the edit dialog
     * 4. verify the button is an anchor tag with the correct href attribute
     */
    @Test
    @DisplayName("Test: Set button link")
    void SetLink() throws TimeoutException, InterruptedException {
        String link = "https://www.adobe.com";
        ButtonEditDialog buttonEditDialog = getButtonEditDialog();
        buttonEditDialog.setLinkField(link);
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        buttonEditDialog.clickPrimary();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(button.checkLinkPresent(link),"Button with link " + link + " should be present");
    }

    /**
     * Test: Set button icon
     *
     * 1. open the edit dialog
     * 2. set the button icon identifier
     * 3. close the edit dialog
     * 4. verify the button has an icon rendered with the correct icon identifier as modifier
     */
    @Test
    @DisplayName("Test: Set button icon")
    void SetIcon() throws InterruptedException, TimeoutException {
        String icon = "email";
        ButtonEditDialog buttonEditDialog = getButtonEditDialog();
        buttonEditDialog.getIcon().setValue(icon);
        buttonEditDialog.clickPrimary();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(CoreComponentConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(button.iconPresent(icon),"Icon " + icon + " should be present");
    }
}
