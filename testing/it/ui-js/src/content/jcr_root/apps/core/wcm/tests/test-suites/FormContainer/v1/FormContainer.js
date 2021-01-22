/*
 *  Copyright 2016 Adobe
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
    var formContainer = window.CQ.CoreComponentsIT.FormContainer.v1;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = formContainer.tcExecuteBeforeTest(c.rtFormContainer_v1, c.rtFormText_v1, c.rtFormButton_v1);
    var tcExecuteAfterTest = formContainer.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Core Form Container v1", { path: "/apps/core/wcm/tests/test-suites/FormContainer/v1/FormContainer.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formContainer.storeContent(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setMailAction(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setContextPath(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setThankYouPage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/form/container/v1/container/clientlibs/site.css"))
        // The View Data button can't be tested because it tries to open a new window and this can't be tested with hobbes
        // NOTE: its not possible to test reliably if the test workflow has been started so no workflow test
        // .addTestCase(startWorkflow)
        // See https://jira.corp.adobe.com/browse/CQ-106130
        // TODO : setting form identifier is going to be replaced by css styles
        // TODO : client validation not implemented yet
    ;

}(hobs, jQuery));

