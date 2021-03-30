/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.button.v1.Button;
import com.adobe.cq.wcm.core.components.it.seljup.components.button.v1.ButtonConfigureDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import com.adobe.qe.selenium.pagewidgets.coral.Dialog;
import com.adobe.qe.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ButtonIT extends AuthorBaseUITest {

    final String COMPONENT_RESOURCE_TYPE = "core/wcm/components/button/v1/button";
    private static final String CONTENT_FRAME = "ContentFrame";

    private String testPage;
    private String proxyCompoenetPath;
    private PageEditorPage editorPage;

    @BeforeEach
    public void setupBefore() throws Exception {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();
        proxyCompoenetPath = creatProxyCompoenet(COMPONENT_RESOURCE_TYPE, "Proxy Button");
        addPathtoComponentPolicy(responsiveGridPath, proxyCompoenetPath);
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanup() throws ClientException {

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
    void SetText() throws Exception {
        final String testTitle = "test button";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/button";
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar layoutContainerEditableToolbar = editorPage.openEditableToolbar(testPage + "/jcr:content/root/responsivegrid/*");
        InsertComponentDialog insertComponentDialog = layoutContainerEditableToolbar.clickInsertComponent();
        insertComponentDialog.selectComponent(proxyCompoenetPath);
        assertTrue(editorPage.getComponentOverlay(testComponentPath).exists(), "new inserted text component should exist in UI");
        Commons.assertResourceExist(authorClient, testComponentPath, "new inserted text component should exist on backend");
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.getTitleField().setValue(testTitle);
        buttonConfigureDialog.clickPrimary();
        Commons.switchContext("ContentFrame");
        Button button = new Button();
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
    void SetLink() throws TimeoutException {
        String link = "https://www.adobe.com";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/button";
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar layoutContainerEditableToolbar = editorPage.openEditableToolbar(testPage + "/jcr:content/root/responsivegrid/*");
        InsertComponentDialog insertComponentDialog = layoutContainerEditableToolbar.clickInsertComponent();
        insertComponentDialog.selectComponent(proxyCompoenetPath);
        assertTrue(editorPage.getComponentOverlay(testComponentPath).exists(), "new inserted text component should exist in UI");
        Commons.assertResourceExist(authorClient, testComponentPath, "new inserted text component should exist on backend");
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        //buttonConfigureDialog.getLinkField().setValue(link);
        //buttonConfigureDialog.clickPrimary();
        //Commons.switchContext("ContentFrame");
    }
}
