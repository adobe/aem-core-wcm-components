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

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions.FormOptions;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions.FormOptionsConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions.V1.FormOptionsV1;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormOptionsV1IT extends AuthorBaseUITest {

    protected String testPage;
    private String compPath;
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
    protected FormOptions formOptions;

    public void setComponentResources() {
        formOptionsRT = Commons.rtFormOptions_v1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        compPath = Commons.createProxyComponent(adminClient, formOptionsRT, Commons.proxyPath, null, null);

        // add the core form container component
        optionPath = Commons.addComponent(adminClient, compPath, testPage + Commons.relParentCompPath, "container", null);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        formOptions = new FormOptionsV1();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, compPath);
    }

    /**
     * Test: Check the mandatory fields
     */
    @Test
    @DisplayName("Test: Check the mandatory fields")
    public void checkMandatoryFields() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        Commons.saveConfigureDialog();
        assertTrue(Commons.isConfigDialogVisible(),"Config Dialog should be visible");
        assertTrue(formOptions.getConfigDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    /**
     * Test: Set title text
     */
    @Test
    @DisplayName("Test: Set title text")
    public void setTitle() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isTitleRendered(title), "Title should be rendered");
    }

    /**
     * Test: Set element name
     */
    @Test
    @DisplayName("Test: Set element name")
    public void setElementName() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isNameSet(elemName), "Name should be set");
    }

    /**
     * Test: Set the help message
     */
    @Test
    @DisplayName("Test: Set the help message")
    public void setHelpMessage() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        configDialog.setHelpMessage(helpMessage);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isHelpMessageSet(helpMessage), "Help Message should be set");
    }

    /**
     * Test : Set the checkbox type
     */
    @Test
    @DisplayName("Test : Set the checkbox type")
    public void setCheckbox() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        configDialog.setOptionType("checkbox");
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
    public void setRadioButton() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        configDialog.setOptionType("radio");
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
    public void setDropDown()  throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        configDialog.setOptionType("drop-down");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownTypeSet(), "Option type should be set to drop-down");
    }

    /**
     * Test : Set the multi-select drop-down type
     */
    @Test
    @DisplayName("Test : Set the multi-select drop-down type")
    public void setMultiSelectDropDown()  throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        configDialog.setOptionType("multi-drop-down");
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isMultiSelectDropDownTypeSet(), "Option type should be set to multi-select drop-down");
    }

    /**
     * Test : Set the Active Option For Checkbox
     */
    @Test
    @DisplayName("Test : Set the Active Option For Checkbox")
    public void setActiveOptionForCheckbox()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("checkbox");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Selected' option
        configDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isCheckboxChecked(value),"Checkbox should be checked");
    }

    /**
     * Test : Set the Active Option For radio button
     */
    @Test
    @DisplayName("Test : Set the Active Option For radio button")
    public void setActiveOptionForRadioButton()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("radio");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Selected' option
        configDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isRadioButtonSelected(value),"Radio button should be checked");
    }

    /**
     * Test : Set the Active Option For drop-down
     */
    @Test
    @DisplayName("Test : Set the Active Option For drop-down")
    public void setActiveOptionForDropDown()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("drop-down");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Selected' option
        configDialog.checkSelectedCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownSelected(value),"Dropdown should be checked");
    }

    /**
     * Test : Set the Disabled Option For checkbox
     */
    @Test
    @DisplayName("Test : Set the Active Option For checkbox")
    public void setDisabledOptionForCheckbox()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("checkbox");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Disabled' option
        configDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isCheckboxDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the Disabled Option For radio button
     */
    @Test
    @DisplayName("Test : Set the Active Option For radio button")
    public void setDisabledOptionForRadioButton()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("radio");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Disabled' option
        configDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isRadioButtonDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the Disabled Option For drop-down
     */
    @Test
    @DisplayName("Test : Set the Active Option For drop-down")
    public void setDisabledOptionForDropDown()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("drop-down");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Disabled' option
        configDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isDropDownDisabled(value),"Checkbox should be disabled");
    }

    /**
     * Test : Set the 'Disabled' option for the Multi select drop down type
     */
    @Test
    @DisplayName("Test : Set the 'Disabled' option for the Multi select drop down type")
    public void setDisabledOptionForMultiSelectDropDown()  throws InterruptedException {
        // open the edit dialog
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        // set the option type to checkbox
        configDialog.setOptionType("multi-drop-down");
        // set the mandatory fields
        configDialog.setMandatoryFields(elemName, title);
        // add one option
        configDialog.addOption(value, text);
        // check the 'Disabled' option
        configDialog.checkDisabledCheckbox();
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isMultiSelectDropDownDisabled(value),"Checkbox should be disabled");
    }
}
