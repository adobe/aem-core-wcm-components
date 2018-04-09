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
    var formTextV1 = window.CQ.CoreComponentsIT.FormText.v1;
    var formTextV2 = window.CQ.CoreComponentsIT.FormText.v2;

    /**
     * v2 specifics
     */
    var itemSelector = ".cmp-form-text__help-block";
    var tcExecuteBeforeTest = formTextV1.tcExecuteBeforeTest(c.rtFormText_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = formTextV1.tcExecuteAfterTest();

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite("Form Text v2", { path: "/apps/core/wcm/test-suites/FormText/v2/FormText.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formTextV1.checkLabelMandatory(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setLabel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.hideLabel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setElementName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setValue(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.checkAvailableConstraints(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createTextInput(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createTextarea(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createEmail(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createTel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createNumber(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.createPassword(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setHelpMessage(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setHelpMessageAsPlaceholder(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV1.setReadOnly(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV2.setRequired(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formTextV2.setConstraintMessage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/form/text/v2/text/clientlibs/site.js"));

}(hobs, jQuery));
