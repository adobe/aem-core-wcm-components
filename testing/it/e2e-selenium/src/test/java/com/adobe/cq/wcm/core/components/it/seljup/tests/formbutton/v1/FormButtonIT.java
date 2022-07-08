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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formbutton.v1;

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

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formbutton.v1.FormButton;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formbutton.BaseFormButton;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.button.ButtonEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.codeborne.selenide.WebDriverRunner;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormButtonIT extends AuthorBaseUITest {

    protected String testPage;
    protected String formbuttonRT;
    protected EditorPage editorPage;
    protected BaseFormButton formButton;
    protected String cmpPath;
    protected String componentName = "formbutton";

    protected void setupResources() {
        formbuttonRT = RT_FORMBUTTON_V1;
        formButton = new FormButton();
    }

    protected void setup() throws ClientException, InterruptedException {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();

        addPathtoComponentPolicy(responsiveGridPath, formbuttonRT);

        cmpPath = Commons.addComponentWithRetry(authorClient, formbuttonRT,testPage + Commons.relParentCompPath, componentName);

        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException, InterruptedException {
        setupResources();
        setup();
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanup() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    private ButtonEditDialog openButtonEditDialog() throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + testPage + "/jcr:content/root/responsivegrid/*" +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        return editorPage.openEditableToolbar(cmpPath).clickConfigure().adaptTo(ButtonEditDialog.class);
    }

    /**
     * Test: Check the attributes of the default button rendered without any customisations via the edit dialog
     */
    @Test
    @DisplayName("Test: Check the attributes of the default button rendered without any customisations via the edit dialog")
    public void testCheckDefaultButtonAttributes() {
        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByType("Submit"), "Submit button should be present");
        assertTrue(formButton.getButtonText().contains("Submit"), "Button should contain 'Submit' text");

    }

    /**
     * Test: Create a button
     */
    @Test
    @DisplayName("Test: Create a button")
    public void testCreateButton() throws TimeoutException, InterruptedException {
        ButtonEditDialog buttonEditDialog = openButtonEditDialog();
        buttonEditDialog.selectButtonType("button");
        buttonEditDialog.setTitleField("Button");
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
    public void testSetButtonText() throws TimeoutException, InterruptedException {
        String buttonLabel = "Test Button";
        ButtonEditDialog buttonEditDialog = openButtonEditDialog();
        buttonEditDialog.setTitleField(buttonLabel);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + " text");
    }

    /**
     * Test: Set button name
     */
    @Test
    @DisplayName("Test: Set button name")
    public void testSetButtonName() throws TimeoutException, InterruptedException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonName = "button1";
        ButtonEditDialog buttonEditDialog = openButtonEditDialog();
        buttonEditDialog.setTitleField(buttonLabel);
        buttonEditDialog.setNameField(buttonName);
        Commons.saveConfigureDialog();

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByName(buttonName), "Button should be present with name " + buttonName);
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + " text");
    }

    /**
     * Test: Set button value
     */
    @Test
    @DisplayName("Test: Set button value")
    public void testSetButtonValue() throws TimeoutException, InterruptedException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonName = "button1";
        String buttonValue = "thisisthevalue";
        ButtonEditDialog buttonEditDialog = openButtonEditDialog();
        buttonEditDialog.setTitleField(buttonLabel);
        buttonEditDialog.setNameField(buttonName);
        buttonEditDialog.setValueField(buttonValue);
        Commons.saveConfigureDialog();

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchContext("ContentFrame");
        assertTrue(formButton.isButtonPresentByValue(buttonValue), "Button should be present with value " + buttonValue);
        assertTrue(formButton.getButtonText().contains(buttonLabel), "Button should contain " + buttonLabel + "text");
    }

    /**
     * Test: Set button value without name
     */
    @Test
    @DisplayName("Test: Set button value without name")
    public void testSetButtonValueWithoutName() throws TimeoutException, InterruptedException {
        String buttonLabel = "BUTTON WITH NAME";
        String buttonValue = "thisisthevalue";
        ButtonEditDialog buttonEditDialog = openButtonEditDialog();
        buttonEditDialog.setTitleField(buttonLabel);
        buttonEditDialog.setValueField(buttonValue);
        Commons.saveConfigureDialog();

        assertTrue(buttonEditDialog.isNameFieldInvalid(),"Name field should be invalid");
    }
}
