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
    var formHidden = window.CQ.CoreComponentsIT.FormHidden.v1;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = formHidden.tcExecuteBeforeTest(c.rtFormHidden_v1);
    var tcExecuteAfterTest = formHidden.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Form Hidden v1", { path: "/apps/core/wcm/tests/test-suites/FormHidden/v1/FormHidden.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formHidden.checkMandatoryFields(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formHidden.setElementName(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formHidden.setElementValue(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(formHidden.setElementId(tcExecuteBeforeTest, tcExecuteAfterTest));

})(hobs, jQuery);
