/*
 *  Copyright 2016 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * Tests for core form option
 */
;(function(h, $){

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    //element name
    var elemName = "form_options";
    //title value
    var title = "Options";
    //help message
    var helpMessage = "This is an help message"
    //value for 'value' field
    var value = "value1"
    //value for 'text' field
    var text = "text1"

    /**
     * Before Test Case
     */
    var tcExecuteBeforeTest = new TestCase("Setup Before Test")
        //common set up
        .execTestCase(c.tcExecuteBeforeTest)
        //create the test page, store page path in 'testPagePath'
        .execFct(function (opts,done) {
            c.createPage(c.template, c.rootPage ,'page_' + Date.now(),"testPagePath",done)
        })
        //add the component, store component path in 'optionPath'
        .execFct(function (opts, done){
            c.addComponent(c.rtFormOptions, h.param("testPagePath")(opts)+c.relParentCompPath,"optionPath",done)
        })
        //open the new page in the editor
        .navigateTo("/editor.html%testPagePath%.html");

    /**
     * After Test Case
     */
    var tcExecuteAfterTest = new TestCase("Clean up after Test")
        //common clean up
        .execTestCase(c.tcExecuteAfterTest)
        //delete the test page we created
        .execFct(function (opts, done) {
            c.deletePage(h.param("testPagePath")(opts), done);
        });

    /**
     * Helper test case: set the mandatory fields
     */
    var setMandatoryFields = new h.TestCase("Set Mandatory Fields")
        //set the mandatory title text
        .fillInput("[name='./jcr:title']",title)
        //set the mandatory element name
        .fillInput("[name='./name']",elemName);

    /**
     * Helper test case: add an option
     */
    var addOption = new h.TestCase("Add one option to the Form Options")
        //press the Add button
        .click("button :contains('Add')")
        //set the value
        .fillInput("input[name='./value']",value)
        //set the text
        .fillInput("input[name='./text']",text)
    ;

    /**
     * Helper function: set the option type
     */
    var setOptionType = function(optionType) {
        return new h.TestCase("Set Form Input Type to " + optionType)
            //open the dropdown
            .click("coral-select[name='./type'] button")
            //wait for the dropdown to appear
            .assert.visible("coral-select[name='./type'] coral-selectlist")
            //select the option type
            .click("coral-select[name='./type'] coral-selectlist-item[value='" + optionType + "']")
    };

    /**
     * Test: Check the mandatory fields
     */
    var checkMandatoryFields = new h.TestCase("Check Mandatory fields",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        .click(c.selSaveConfDialogButton,{expectNav:false})
        //check if the dialog is still open
        .asserts.visible(c.selConfigDialog)
        //check if label marked as invalid
        .asserts.isTrue(function() {
            return h.find("input[name='./jcr:title'].is-invalid").size() == 1
        })
        //check if element name is marked as invalid
        .asserts.isTrue(function() {
            return h.find("input[name='./name'].is-invalid").size() == 1
        })
    ;

    /**
     * Test: Set title text
     */
    var setTitle = new h.TestCase("Set title",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the title is rendered
        .asserts.isTrue(function() {
            return h.find("legend","#ContentFrame").text().trim() == title
        })
    ;

    /**
     * Test: Set element name
     */
    var setElementName = new h.TestCase("Set Element Name",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if input name is set correctly
        .asserts.isTrue(function() {
            return h.find("input[name='" + elemName + "']","#ContentFrame").size() == 1;
        })
    ;

    /**
     * Test: Set the help message
     */
    var setHelpMessage = new h.TestCase("Set Help Message",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //set the help message
        .fillInput("[name='./helpMessage']",helpMessage)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the help message is set correctly
        .asserts.isTrue(function() {
             return h.find(".help-block","#ContentFrame").text().trim() == helpMessage;
        })
    ;

    /**
     * Test : Set the checkbox type
     */
    var setCheckbox = new h.TestCase("Set checkbox type",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //set the option type to checkbox
        .execTestCase(setOptionType("checkbox"))
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the option type is set to checkbox
        .asserts.isTrue(function() {
            return h.find(".form-group.checkbox","#ContentFrame").size() == 1;
        })
    ;

    /**
     * Test : Set the radio button type
     */
    var setRadioButton = new h.TestCase("Set radio button type",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //set the option type to radio button
        .execTestCase(setOptionType("radio"))
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the option type is set to radio button
        .asserts.isTrue(function() {
            return h.find(".form-group.radio","#ContentFrame").size() == 1;
        })
    ;

    /**
     * Test : Set the drop-down type
     */
    var setDropDown = new h.TestCase("Set drop-down type",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //set the option type to drop-down
        .execTestCase(setOptionType("drop-down"))
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the option type is set to drop-down
        .asserts.isTrue(function() {
            return h.find(".form-group.drop-down","#ContentFrame").size() == 1;
        })
    ;

    /**
     * Test : Set the multi-select drop-down type
     */
    var setMultiSelectDropDown = new h.TestCase("Set multi-select drop-down type",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //set the option type to multi-select drop-down
        .execTestCase(setOptionType("multi-drop-down"))
        //add one option
        .execTestCase(addOption)
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)

        //check if the option type is set to multi-select drop-down
        .asserts.isTrue(function() {
            return h.find(".form-group.multi-drop-down","#ContentFrame").size() == 1;
        })
    ;

    var setActiveOptionForCheckbox = new h.TestCase("Set the 'Active' option for the Checkbox type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to checkbox
        .execTestCase(setOptionType("checkbox"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Active' option
        .click("input[type='checkbox'][name='./selected']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is active
        .asserts.isTrue(function() {
                return h.find("input[type='checkbox'][value="+value+"][checked]","#ContentFrame").size() == 1;
        })
    ;

    var setActiveOptionForRadioButton = new h.TestCase("Set the 'Active' option for the Radio button type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to radio
        .execTestCase(setOptionType("radio"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Active' option
        .click("input[type='radio'][name='./selected']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is active
        .asserts.isTrue(function() {
             return h.find("input[type='radio'][value='"+value+"'][checked]","#ContentFrame").size() == 1;
        })
    ;

    var setActiveOptionForDropDown = new h.TestCase("Set the 'Active' option for the Drop down type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to drop-down
        .execTestCase(setOptionType("drop-down"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Active' option
        .click("input[type='radio'][name='./selected']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is active
        .asserts.isTrue(function() {
             return h.find("option[value='"+value+"'][selected]","#ContentFrame").size() == 1;
        })
    ;

    var setActiveOptionForMultiSelectDropDown = new h.TestCase("Set the 'Active' option for the Multi select drop down type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to multi-select drop-down
        .execTestCase(setOptionType("multi-drop-down"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Active' option
        .click("input[type='checkbox'][name='./selected']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is active
        .asserts.isTrue(function() {
            return h.find("option[value='"+value+"'][selected]","#ContentFrame").size() == 1;
        })
    ;

    var setDisabledOptionForCheckbox = new h.TestCase("Set the 'Disabled' option for the Checkbox type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to checkbox
        .execTestCase(setOptionType("checkbox"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Disabled' option
        .click("input[type='checkbox'][name='./disabled']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is disabled
        .asserts.isTrue(function() {
            return h.find("input[type='checkbox'][value="+value+"][disabled]","#ContentFrame").size() == 1;
        })
    ;

    var setDisabedOptionForRadioButton = new h.TestCase("Set the 'Disabled' option for the Radio button type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to radio
        .execTestCase(setOptionType("radio"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Disabled' option
        .click("input[type='checkbox'][name='./disabled']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is disabled
        .asserts.isTrue(function() {
            return h.find("input[type='radio'][value='"+value+"'][disabled]","#ContentFrame").size() == 1;
        })
    ;

    var setDisabledOptionForDropDown = new h.TestCase("Set the 'Disabled' option for the Drop down type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to drop-down
        .execTestCase(setOptionType("drop-down"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Disabled' option
        .click("input[type='checkbox'][name='./disabled']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is disabled
        .asserts.isTrue(function() {
            return h.find("option[value='"+value+"'][disabled]","#ContentFrame").size() == 1;
        })
    ;

    var setDisabledOptionForMultiSelectDropDown = new h.TestCase("Set the 'Disabled' option for the Multi select drop down type", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})
        //open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("optionPath"))
        //set the option type to multi-select drop-down
        .execTestCase(setOptionType("multi-drop-down"))
        //set the mandatory fields
        .execTestCase(setMandatoryFields)
        //add one option
        .execTestCase(addOption)
        //check the 'Disabled' option
        .click("input[type='checkbox'][name='./disabled']")
        //close the edit dialog
        .execTestCase(c.tcSaveConfigureDialog)
        //check if the option is disabled
        .asserts.isTrue(function() {
            return h.find("option[value='"+value+"'][disabled]","#ContentFrame").size() == 1;
        })
    ;

    new h.TestSuite('Core Components - Form Options', {path: '/apps/core/wcm/tests/core-components-it/FormOptions.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : true})

        .addTestCase(checkMandatoryFields)
        .addTestCase(setTitle)
        .addTestCase(setElementName)
        .addTestCase(setHelpMessage)
        .addTestCase(setCheckbox)
        .addTestCase(setRadioButton)
        .addTestCase(setDropDown)
        .addTestCase(setMultiSelectDropDown)
        .addTestCase(setActiveOptionForCheckbox)
        .addTestCase(setActiveOptionForRadioButton)
        .addTestCase(setActiveOptionForDropDown)
        .addTestCase(setActiveOptionForMultiSelectDropDown)
        .addTestCase(setDisabledOptionForCheckbox)
        .addTestCase(setDisabedOptionForRadioButton)
        .addTestCase(setDisabledOptionForDropDown)
        .addTestCase(setDisabledOptionForMultiSelectDropDown)
    ;

})(hobs, jQuery);
