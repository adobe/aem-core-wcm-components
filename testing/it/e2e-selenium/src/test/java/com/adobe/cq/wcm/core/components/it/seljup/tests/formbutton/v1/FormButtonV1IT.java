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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formbutton.v1;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormButton.v1.FormButtonV1;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormButton.FormButton;
import com.adobe.cq.wcm.core.components.it.seljup.components.button.v1.ButtonConfigureDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import com.adobe.qe.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.WebDriverRunner;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormButtonV1IT extends AuthorBaseUITest {

    final String COMPONENT_RESOURCE_TYPE = "core/wcm/components/form/button/v1/button";

    protected String testPage;
    protected String proxyCompoenetPath;
    protected PageEditorPage editorPage;
    protected FormButton formButton;


    private void addComponentInPage() throws TimeoutException {
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 20).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar layoutContainerEditableToolbar = editorPage.openEditableToolbar(testPage + "/jcr:content/root/responsivegrid/*");
        InsertComponentDialog insertComponentDialog = layoutContainerEditableToolbar.clickInsertComponent();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + proxyCompoenetPath + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        insertComponentDialog.selectComponent(proxyCompoenetPath);
        assertTrue(editorPage.getComponentOverlay(testComponentPath).exists(), "new inserted text component should exist in UI");
        Commons.assertResourceExist(authorClient, testComponentPath, "new inserted text component should exist on backend");
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBefore() throws ClientException {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();
        proxyCompoenetPath = Commons.creatProxyComponent(adminClient, COMPONENT_RESOURCE_TYPE, "Proxy Form Button", "formbutton");
        addPathtoComponentPolicy(responsiveGridPath, proxyCompoenetPath);
        formButton = new FormButtonV1();
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanup() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Test: Check the attributes of the default button rendered without any customisations via the edit dialog
     */
    @Test
    @DisplayName("Test: Check the attributes of the default button rendered without any customisations via the edit dialog")
    public void checkDefaultButtonAttributes() throws TimeoutException {
        addComponentInPage();
        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByType("Submit"), "Submit button should be present");
        assertTrue(formButton.getButtonText().contains("Submit"), "Button should contain 'Submit' text");

    }

    /**
     * Test: Create a button
     */
    @Test
    @DisplayName("Test: Create a button")
    public void createButton() throws TimeoutException {
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        addComponentInPage();
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.selectButtonType("button");
        buttonConfigureDialog.getTitleField().setValue("Button");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByType("Button"), "Button button should be present");
        assertTrue(formButton.getButtonText().contains("Button"), "Button should contain 'Submit' text");
    }

    /**
     * Test: Set button text
     */
    @Test
    @DisplayName("Test: Set button text")
    public void setButtonText() throws TimeoutException {
        String buttonLabel = "Test Button";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        addComponentInPage();
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.getTitleField().setValue(buttonLabel);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + " text");
    }

    /**
     * Test: Set button name
     */
    @Test
    @DisplayName("Test: Set button name")
    public void setButtonName() throws TimeoutException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonName = "button1";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        addComponentInPage();
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.getTitleField().setValue(buttonLabel);
        buttonConfigureDialog.getNameField().setValue(buttonName);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByName(buttonName), "Button should be present with name " + buttonName);
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + " text");
    }

    /**
     * Test: Set button value
     */
    @Test
    @DisplayName("Test: Set button value")
    public void setButtonValue() throws TimeoutException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonName = "button1";
        String buttonValue = "thisisthevalue";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        addComponentInPage();
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.getTitleField().setValue(buttonLabel);
        buttonConfigureDialog.getNameField().setValue(buttonName);
        buttonConfigureDialog.getValueField().setValue(buttonValue);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByValue(buttonValue), "Button should be present with value " + buttonValue);
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + "text");
    }

    /**
     * Test: Set button value without name
     */
    @Test
    @DisplayName("Test: Set button value without name")
    public void setButtonValueWithoutName() throws TimeoutException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonValue = "thisisthevalue";
        String testComponentPath = testPage + "/jcr:content/root/responsivegrid/formbutton";
        addComponentInPage();
        ButtonConfigureDialog buttonConfigureDialog = editorPage.openEditableToolbar(testComponentPath).clickConfigure().adaptTo(ButtonConfigureDialog.class);
        buttonConfigureDialog.getTitleField().setValue(buttonLabel);
        buttonConfigureDialog.getValueField().setValue(buttonValue);
        Commons.saveConfigureDialog();

        assertTrue(buttonConfigureDialog.getNameField().getAttribute("invalid").equals("true"),"Name field should be invalid");
    }
}
