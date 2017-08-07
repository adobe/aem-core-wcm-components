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

;(function(h, $){

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formContainer = window.CQ.CoreComponentsIT.v1.FormContainer;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = formContainer.tcExecuteBeforeTest(c.rtFormContainer, c.rtFormText, c.rtFormButton);
    var tcExecuteAfterTest = formContainer.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Core Components - Form Container",{path:"/apps/core/wcm/tests/test-suites/core-components-suites/FormContainer.js",
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(formContainer.storeContent(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setMailAction(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setContextPath(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formContainer.setThankYouPage(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));

