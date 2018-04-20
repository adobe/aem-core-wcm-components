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
 * Tests for the core form components
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formComponents = window.CQ.CoreComponentsIT.FormComponents.v1;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = formComponents.tcExecuteBeforeTest(c.rtFormContainer_v1, c.rtFormText_v1, c.rtFormHidden_v1, c.rtFormOptions_v1, c.rtFormButton_v1);
    var tcExecuteAfterTest = formComponents.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Form Components v1", { path: "/apps/core/wcm/tests/test-suites/FormComponents/v1/FormComponents.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formComponents.storeContent(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
