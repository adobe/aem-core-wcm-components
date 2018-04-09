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
 * Tests for core form button
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var formButtonV1 = window.CQ.CoreComponentsIT.FormButton.v1;

    /**
     * v2 specifics
     */

    var buttonSelector = ".cmp-form-button";

    var tcExecuteBeforeTest = formButtonV1.tcExecuteBeforeTest(c.rtFormButton_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = formButtonV1.tcExecuteAfterTest();

    /**
     * Test: The main test suite
     */
    new h.TestSuite("Form Button v2", { path: "/apps/core/wcm/test-suites/FormButton/v2/FormButton.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formButtonV1.checkDefaultButtonAttributes(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButtonV1.createButton(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButtonV1.setButtonText(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButtonV1.setButtonName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButtonV1.setButtonValue(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButtonV1.setButtonValueWithoutName(tcExecuteBeforeTest, tcExecuteAfterTest));

})(hobs, jQuery);
