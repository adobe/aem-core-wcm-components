/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Tests for the core text component
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formText = window.CQ.CoreComponentsIT.FormText.v1;

    /**
     * v1 specifics
     */
    var itemSelector = ".help-block";
    var tcExecuteBeforeTest = formText.tcExecuteBeforeTest(c.rtFormText_v1);
    var tcExecuteAfterTest = formText.tcExecuteAfterTest();

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite("Form Text v1", { path: "/apps/core/wcm/tests/test-suites/FormText/v1/FormText.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formText.checkLabelMandatory(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setLabel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.hideLabel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setElementName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setValue(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.checkAvailableConstraints(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createTextInput(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createTextarea(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createEmail(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createTel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createNumber(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.createPassword(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setHelpMessage(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setHelpMessageAsPlaceholder(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setReadOnly(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setRequired(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formText.setConstraintMessage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/form/text/v1/text/clientlibs/site.js"));

}(hobs, jQuery));
