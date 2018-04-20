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

;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formContainerV1 = window.CQ.CoreComponentsIT.FormContainer.v1;
    /**
     * v2 specifics
     */
    var tcExecuteBeforeTest = formContainerV1.tcExecuteBeforeTest(c.rtFormContainer_v2, c.rtFormText_v2, c.rtFormButton_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = formContainerV1.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Form Container v2", { path: "/apps/core/wcm/test-suites/FormContainer/v2/FormContainer.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formContainerV1.storeContent(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainerV1.setMailAction(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainerV1.setContextPath(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainerV1.setThankYouPage(tcExecuteBeforeTest, tcExecuteAfterTest))
        // The View Data button can't be tested because it tries to open a new window and this can't be tested with hobbes
        // NOTE: its not possible to test reliably if the test workflow has been started so no workflow test
        // .addTestCase(startWorkflow)
        // See https://jira.corp.adobe.com/browse/CQ-106130
        // TODO : setting form identifier is going to be replaced by css styles
        // TODO : client validation not implemented yet
    ;

}(hobs, jQuery));

