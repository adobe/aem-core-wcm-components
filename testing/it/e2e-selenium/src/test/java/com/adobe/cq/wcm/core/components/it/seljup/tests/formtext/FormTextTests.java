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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formtext;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.EditableToolbar;
import com.adobe.cq.wcm.core.components.it.seljup.components.formtext.FormTextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.formtext.BaseFormText;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormTextTests {
    // element name
    private String  elemName = "Luigi";
    // input label
    private String  label = "It is me, Mario!";
    // default value
    private String  defaultValue = "Uncharted";
    // help message
    private String  helpMessage = "Skyrim";
    // required message
    private String  requiredMessage = "Attack ships on fire off the shoulder of Orion";

    private String testPage;
    private String compPath;
    private String formTextPath;
    private EditorPage editorPage;
    private BaseFormText formText;

    private void openConfig() throws TimeoutException {
        String component = "[data-type='Editable'][data-path='" + formTextPath +"']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, CoreComponentConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(component)));
        EditableToolbar editableToolbar = editorPage.openEditableToolbar(formTextPath);
        editableToolbar.clickConfigure();
    }

    public void setup(CQClient client, String formTextRT, String rootPage,
                      String defaultPageTemplate, BaseFormText formText) throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = client.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        compPath = Commons.createProxyComponent(client, formTextRT, Commons.proxyPath, null, null);

        // add the core form container component
        formTextPath = Commons.addComponent(client, compPath, testPage + Commons.relParentCompPath, "formtext", null);

        this.formText = formText;
        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_SEC  * 1000, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        Commons.deleteProxyComponent(client, compPath);
    }

    public void checkLabelMandatory() throws InterruptedException, TimeoutException {
        openConfig();
        Commons.saveConfigureDialog();
        assertTrue(Commons.iseditDialogVisible(),"Config Dialog should be visible");
        assertTrue(formText.getConfigDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    public void setLabel() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isLabelRendered(label), "Title should be rendered");
    }

    public void hideLabel() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.hideTitle();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(!formText.isLabelRendered(label), "Label should not be rendered");
        assertTrue(formText.isInputAriaLabelSet(elemName,label), "aria-label attribute should be set on the input field");
        Commons.switchToDefaultContext();
        openConfig();
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaAriaLabelSet(elemName,label),"aria-label attribute should be set on the textarea field");
    }

    public void setElementName() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputSet(elemName), "Input should be set");
        Commons.switchToDefaultContext();
        openConfig();
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaSet(elemName),"Text input should be set");
    }

    public void setValue() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setDefaultValue(defaultValue);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isDefaultValueSet(defaultValue), "Default value should be set");
        Commons.switchToDefaultContext();
        openConfig();
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaDefaultValueSet(elemName, defaultValue),"textarea default value should be set correctly");
    }

    public void createTextInput() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("text");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputSet(elemName),"input should be rendered correctly");
    }

    public void createTextarea() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaSet(elemName),"text area should be rendered correctly");
    }

    public void createEmail() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("email");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isEmailSet(elemName),"email should be rendered correctly");
    }

    public void createTel() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("tel");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTelSet(elemName),"telephone input should be rendered correctly");
    }

    public void createDate() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("date");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isDateSet(elemName),"telephone input should be rendered correctly");
    }

    public void createNumber() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("number");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isNumberSet(elemName),"number input should be rendered correctly");
    }

    public void createPassword() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("password");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isPasswordSet(elemName),"password input should be rendered correctly");
    }

    public void setHelpMessage() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isHelpMessageRendered(helpMessage),"Help message should be rendered correctly");
    }

    public void setHelpMessageAsPlaceholder() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        configDialog.checkHelpAsPlaceHolder();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isHelpRenderedAsTooltip(elemName, helpMessage),"Help message should be rendered as tooltip");
    }

    public void checkAvailableConstraints() throws TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        assertTrue(configDialog.checkAllConstraintsAvailable(),"All constraints should be available");
    }

    public void setReadOnly() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openConstraintsTab();
        configDialog.setReadOnly();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputReadOnly(elemName), "input field should be set to read only");
        Commons.switchToDefaultContext();
        openConfig();
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaReadOnly(elemName), "textarea field should be set to read only");
    }

    public void setRequired() throws TimeoutException, InterruptedException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openConstraintsTab();
        configDialog.setRequired();
        configDialog.setRequiredMessage(requiredMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputRequired(elemName), "input field should be set to required");
        Commons.switchToDefaultContext();
        openConfig();
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaRequired(elemName), "Textarea should be set to required");
        assertTrue(formText.isTextAreaRequiredMessageSet(elemName, requiredMessage), "Required message should be set");
    }

    public void setConstraintMessage() throws InterruptedException, TimeoutException {
        openConfig();
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("email");
        configDialog.openConstraintsTab();
        configDialog.setConstraintMessage(requiredMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputConstraintMessageSet(elemName, requiredMessage), "Constraint message should be set");
    }
}
