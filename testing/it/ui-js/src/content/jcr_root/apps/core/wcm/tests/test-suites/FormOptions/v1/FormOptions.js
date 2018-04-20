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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formOptions = window.CQ.CoreComponentsIT.FormOptions.v1;

    /**
     * v1 specifics
     */
    var itemSelector = {
        help: ".help-block",
        description: ".form-group input ~ span",
        checkbox: ".form-group.checkbox",
        radio: ".form-group.radio",
        dropDown: ".form-group.drop-down",
        multiDropDown: ".form-group.multi-drop-down"
    };

    var tcExecuteBeforeTest = formOptions.tcExecuteBeforeTest(c.rtFormOptions_v1);
    var tcExecuteAfterTest = formOptions.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Form Options v1", { path: "/apps/core/wcm/tests/test-suites/FormOptions/v1/FormOptions.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formOptions.checkMandatoryFields(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setElementName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setHelpMessage(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setCheckbox(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setRadioButton(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setDropDown(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setMultiSelectDropDown(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setActiveOptionForCheckbox(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setActiveOptionForRadioButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setActiveOptionForDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setActiveOptionForMultiSelectDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setDisabledOptionForCheckbox(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setDisabedOptionForRadioButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setDisabledOptionForDropDown(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formOptions.setDisabledOptionForMultiSelectDropDown(tcExecuteBeforeTest, tcExecuteAfterTest));

})(hobs, jQuery);
