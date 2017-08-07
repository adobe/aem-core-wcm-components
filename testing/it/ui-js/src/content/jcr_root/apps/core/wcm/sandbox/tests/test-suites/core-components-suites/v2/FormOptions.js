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
    var formOptionsV1 = window.CQ.CoreComponentsIT.v1.FormOptions;

    /**
     * v2 specifics
     */
    var itemSelector ={
        help: '.cmp-form-options__help-message',
        checkbox: '.cmp-form-options__field--checkbox',
        radio: '.cmp-form-options__field--radio',
        dropDown: '.cmp-form-options__field--drop-down',
        multiDropDown: '.cmp-form-options__field--multi-drop-down'
    };
    var tcExecuteBeforeTest = formOptionsV1.tcExecuteBeforeTest(c.rtFormOptions_v2, "core/wcm/sandbox/tests/components/test-page-v2");
    var tcExecuteAfterTest = formOptionsV1.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite('Core Components - Form Options v2', {path: '/apps/core/wcm/sandbox/test-suites/core-components-it/v2/FormOptions.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(formOptionsV1.checkMandatoryFields(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setElementName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setHelpMessage(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setCheckbox(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setRadioButton(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setDropDown(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setMultiSelectDropDown(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setActiveOptionForCheckbox(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setActiveOptionForRadioButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setActiveOptionForDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setActiveOptionForMultiSelectDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setDisabledOptionForCheckbox(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setDisabedOptionForRadioButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setDisabledOptionForDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptionsV1.setDisabledOptionForMultiSelectDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
    ;

})(hobs, jQuery);
