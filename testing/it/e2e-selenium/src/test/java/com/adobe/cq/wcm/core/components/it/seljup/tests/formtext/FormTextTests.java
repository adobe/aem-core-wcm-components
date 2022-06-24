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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.FormTextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.BaseFormText;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;

import java.util.concurrent.TimeoutException;

import static com.codeborne.selenide.Selenide.$;
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


    public void setup(CQClient client, String formTextRT, String rootPage,
                      String defaultPageTemplate, BaseFormText formText) throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = client.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        formTextPath = Commons.addComponentWithRetry(client, formTextRT, testPage + Commons.relParentCompPath, "formtext");

        this.formText = formText;
        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    public void cleanup(CQClient client) throws ClientException, InterruptedException {
        client.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    public void testCheckLabelMandatory() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        Commons.saveConfigureDialog();
        assertTrue(Commons.iseditDialogVisible(),"Config Dialog should be visible");
        assertTrue(formText.getConfigDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    public void testSetLabel() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isLabelRendered(label), "Title should be rendered");
    }

    public void testHideLabel() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.hideTitle();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(!formText.isLabelRendered(label), "Label should not be rendered");
        assertTrue(formText.isInputAriaLabelSet(elemName,label), "aria-label attribute should be set on the input field");
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, formTextPath);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaAriaLabelSet(elemName,label),"aria-label attribute should be set on the textarea field");
    }

    public void testSetElementName() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputSet(elemName), "Input should be set");
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, formTextPath);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaSet(elemName),"Text input should be set");
    }

    public void testSetValue() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setDefaultValue(defaultValue);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isDefaultValueSet(defaultValue), "Default value should be set");
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, formTextPath);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaDefaultValueSet(elemName, defaultValue),"textarea default value should be set correctly");
    }

    public void testCreateTextInput() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("text");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputSet(elemName),"input should be rendered correctly");
    }

    public void testCreateTextarea() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaSet(elemName),"text area should be rendered correctly");
    }

    public void testCreateEmail() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("email");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isEmailSet(elemName),"email should be rendered correctly");
    }

    public void testCreateTel() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("tel");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTelSet(elemName),"telephone input should be rendered correctly");
    }

    public void testCreateDate() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("date");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isDateSet(elemName),"telephone input should be rendered correctly");
    }

    public void testCreateNumber() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("number");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isNumberSet(elemName),"number input should be rendered correctly");
    }

    public void testCreatePassword() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("password");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isPasswordSet(elemName),"password input should be rendered correctly");
    }

    public void testSetHelpMessage() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isHelpMessageRendered(helpMessage),"Help message should be rendered correctly");
    }

    public void testSetHelpMessageAsPlaceholder() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        configDialog.checkHelpAsPlaceHolder();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isHelpRenderedAsTooltip(elemName, helpMessage),"Help message should be rendered as tooltip");
    }

    public void testCheckAvailableConstraints() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        assertTrue(configDialog.checkAllConstraintsAvailable(),"All constraints should be available");
    }

    public void testSetReadOnly() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openConstraintsTab();
        configDialog.setReadOnly();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputReadOnly(elemName), "input field should be set to read only");
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, formTextPath);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaReadOnly(elemName), "textarea field should be set to read only");
    }

    public void testSetRequired() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openConstraintsTab();
        configDialog.setRequired();
        configDialog.setRequiredMessage(requiredMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputRequired(elemName), "input field should be set to required");
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, formTextPath);
        configDialog.setOptionType("textarea");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isTextAreaRequired(elemName), "Textarea should be set to required");
        assertTrue(formText.isTextAreaRequiredMessageSet(elemName, requiredMessage), "Required message should be set");
    }

    public void testSetConstraintMessage() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setMandatoryFields(elemName, label);
        configDialog.setOptionType("email");
        configDialog.openConstraintsTab();
        configDialog.setConstraintMessage(requiredMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formText.isInputConstraintMessageSet(elemName, requiredMessage), "Constraint message should be set");
    }

    public void testTextareaAccessibilityWhenHelpMessageIsSet() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setOptionType("textarea");
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement textareaElement = $("textarea");
        assertTrue(formText.elementHasExpectedAriaDescribedByAttribute(textareaElement, helpMessage));
    }

    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnTextarea() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setOptionType("textarea");
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement textareaElement = $("textarea");
        assertTrue(formText.elementHasNoAriaDescribedByAttribute(textareaElement));
    }

    public void testInputAccessibilityWhenHelpMessageIsSet() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setOptionType("text");
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        configDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement inputElement = $("input[type='text']");
        assertTrue(formText.elementHasExpectedAriaDescribedByAttribute(inputElement, helpMessage));
    }

    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnInput() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, formTextPath);
        FormTextEditDialog configDialog = formText.getConfigDialog();
        configDialog.setOptionType("text");
        configDialog.setMandatoryFields(elemName, label);
        configDialog.openAboutTab();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement inputElement = $("input[type='text']");
        assertTrue(formText.elementHasNoAriaDescribedByAttribute(inputElement));
    }
}
