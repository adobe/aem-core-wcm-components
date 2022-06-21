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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formoptions.v1;

import java.util.concurrent.TimeoutException;

import com.codeborne.selenide.SelenideElement;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions.BaseFormOptions;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions.FormOptionsEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions.v1.FormOptions;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMOPTIONS_V1;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormOptionsIT extends AuthorBaseUITest {

    protected String testPage;
    private String optionPath;
    private EditorPage editorPage;

    // element name
    private String elemName = "form_options";
    // title value
    private String title = "Options";
    // help message
    private String helpMessage = "This is an help message";
    // value for 'value' field
    private String value = "value1";
    // value for 'text' field
    private String text = "text1";

    protected String formOptionsRT;
    protected BaseFormOptions formOptions;

    protected void setComponentResources() {
        formOptionsRT = RT_FORMOPTIONS_V1;
        formOptions = new FormOptions();
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        optionPath = Commons.addComponentWithRetry(authorClient, formOptionsRT, testPage + Commons.relParentCompPath, "formoption");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }

    /**
     * Test: Check the mandatory fields
     */
    @Test
    @DisplayName("Test: Check the mandatory fields")
    public void testCheckMandatoryFields() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        Commons.saveConfigureDialog();
        assertTrue(Commons.iseditDialogVisible(),"Config Dialog should be visible");
        assertTrue(formOptions.geteditDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    /**
     * Test: Set title text
     */
    @Test
    @DisplayName("Test: Set title text")
    public void testSetTitle() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isTitleRendered(title), "Title should be rendered");
    }

    /**
     * Test: Set element name
     */
    @Test
    @DisplayName("Test: Set element name")
    public void testSetElementName() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isNameSet(elemName), "Name should be set");
    }

    /**
     * Test: Set the help message
     */
    @Test
    @DisplayName("Test: Set the help message")
    public void testSetHelpMessage() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isHelpMessageSet(helpMessage), "Help Message should be set");
    }

    /**
     * Test : Set the checkbox type
     */
    @Test
    @DisplayName("Test : Set the checkbox type")
    public void testSetCheckbox() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setOptionType("checkbox");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isCheckboxTypeSet(), "Option type should be set to checkbox");
        assertTrue(formOptions.isDescriptionSet(text), "Description should be set");
    }

    /**
     * Test : Set the checkbox type
     */
    @Test
    @DisplayName("Test : Set the checkbox type")
    public void testSetRadioButton() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setOptionType("radio");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isRadioButtonTypeSet(), "Option type should be set to radio button");
        assertTrue(formOptions.isDescriptionSet(text), "Description should be set");
    }

    /**
     * Test : Set the drop-down type
     */
    @Test
    @DisplayName("Test : Set the drop-down type")
    public void testSetDropDown()  throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setOptionType("drop-down");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownTypeSet(), "Option type should be set to drop-down");
    }

    /**
     * Test : Set the multi-select drop-down type
     */
    @Test
    @DisplayName("Test : Set the multi-select drop-down type")
    public void testSetMultiSelectDropDown()  throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setOptionType("multi-drop-down");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isMultiSelectDropDownTypeSet(), "Option type should be set to multi-select drop-down");
    }

    /**
     * Test : Set the Active Option For Checkbox
     */
    @Test
    @DisplayName("Test : Set the Active Option For Checkbox")
    public void testSetActiveOptionForCheckbox()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("checkbox");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Selected' option
        editDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isCheckboxChecked(value),"Checkbox should be checked");
    }

    /**
     * Test : Set the Active Option For radio button
     */
    @Test
    @DisplayName("Test : Set the Active Option For radio button")
    public void testSetActiveOptionForRadioButton()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("radio");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Selected' option
        editDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isRadioButtonSelected(value),"Radio button should be checked");
    }

    /**
     * Test : Set the Active Option For drop-down
     */
    @Test
    @DisplayName("Test : Set the Active Option For drop-down")
    public void testSetActiveOptionForDropDown()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("drop-down");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Selected' option
        editDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownSelected(value),"Dropdown should be checked");
    }

    /**
     * Test : Set the Disabled Option For checkbox
     */
    @Test
    @DisplayName("Test : Set the Active Option For checkbox")
    public void testSetDisabledOptionForCheckbox()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("checkbox");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Disabled' option
        editDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isCheckboxDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the Disabled Option For radio button
     */
    @Test
    @DisplayName("Test : Set the Active Option For radio button")
    public void testSetDisabledOptionForRadioButton()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("radio");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Disabled' option
        editDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isRadioButtonDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the Disabled Option For drop-down
     */
    @Test
    @DisplayName("Test : Set the Active Option For drop-down")
    public void testSetDisabledOptionForDropDown()  throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("drop-down");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Disabled' option
        editDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the 'Disabled' option for the Multi select drop down type
     */
    @Test
    @DisplayName("Test : Set the 'Disabled' option for the Multi select drop down type")
    public void testSetDisabledOptionForMultiSelectDropDown() throws InterruptedException, TimeoutException {
        // open the edit dialog
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        // set the option type to checkbox
        editDialog.setOptionType("multi-drop-down");
        // set the mandatory fields
        editDialog.setMandatoryFields(elemName, title);
        // add one option
        editDialog.addOption(value, text);
        // check the 'Disabled' option
        editDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isMultiSelectDropDownDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test: Set the help message and verify the option element of the drop-down to have the aria-describedby attribute equal with the help message id
     */
    @Test
    @DisplayName("Test: Set the help message and verify the option element of the drop-down to have the aria-describedby attribute equal with the help message id")
    public void testAccessibilityWhenHelpMessageIsSetOnDropDownType() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setOptionType("drop-down");
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement dropDownOptionElement = $("option");
        assertTrue(formOptions.elementHasExpectedAriaDescribedByAttribute(dropDownOptionElement));
    }

    /**
     * Test: Without setting a help message, verify the option element of the drop-down to have no aria-describedby attribute
     */
    @Test
    @DisplayName("Test: Without setting a help message, verify the option element of the drop-down to have no aria-describedby attribute")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnDropDownType() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setOptionType("drop-down");
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement dropDownOptionElement = $("option");
        assertTrue(formOptions.elementHasNoAriaDescribedByAttribute(dropDownOptionElement));
    }

    /**
     * Test: Set the help message and verify the checkbox input to have the aria-describedby attribute equal with the help message id
     */
    @Test
    @DisplayName("Test: Set the help message and verify the checkbox input to have the aria-describedby attribute equal with the help message id")
    public void testAccessibilityWhenHelpMessageIsSetOnCheckboxType() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setOptionType("checkbox");
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        editDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement checkboxElement = $("input[type='checkbox']");
        assertTrue(formOptions.elementHasExpectedAriaDescribedByAttribute(checkboxElement));
    }

    /**
     * Test: Without setting a help message, verify the checkbox input to have no aria-describedby attribute
     */
    @Test
    @DisplayName("Test: Without setting a help message, verify the checkbox input to have no aria-describedby attribute")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnCheckboxType() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, optionPath);
        FormOptionsEditDialog editDialog = formOptions.geteditDialog();
        editDialog.setOptionType("checkbox");
        editDialog.setMandatoryFields(elemName, title);
        editDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        SelenideElement checkboxElement = $("input[type='checkbox']");
        assertTrue(formOptions.elementHasNoAriaDescribedByAttribute(checkboxElement));
    }
}
