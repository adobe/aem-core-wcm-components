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

    // short cut
    var c = window.CQ.CoreComponentsIT.commons;
    var formButton = window.CQ.CoreComponentsIT.FormButton.v1;

    /**
     * v1 specifics
     */
    var buttonSelector = ".btn";

    var tcExecuteBeforeTest = formButton.tcExecuteBeforeTest(c.rtFormButton_v1);
    var tcExecuteAfterTest = formButton.tcExecuteAfterTest();

    /**
     * Test: The main test suite
     */
    new h.TestSuite("Form Button v1", { path: "/apps/core/wcm/tests/test-suites/FormButton/v1/FormButton.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formButton.checkDefaultButtonAttributes(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButton.createButton(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButton.setButtonText(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButton.setButtonName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButton.setButtonValue(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formButton.setButtonValueWithoutName(tcExecuteBeforeTest, tcExecuteAfterTest));

})(hobs, jQuery);
