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
 * Tests for the core title component.
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var titleV1 = window.CQ.CoreComponentsIT.Title.v1;

    /**
     * v2 specifics
     */
    var tcExecuteBeforeTest = titleV1.tcExecuteBeforeTest(c.rtTitle_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = titleV1.tcExecuteAfterTest(c.policyPath, c.policyAssignmentPath);

    /**
     * The main test suite for Title component
     */
    new h.TestSuite("Title v2", { path: "/apps/core/wcm/test-suites/core-components-it/v2/Title.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        // TODO: Removed for now as it not stable randomly failing
        // .addTestCase(titleV1.tcSetTitleValueUsingInlineEditor(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(titleV1.tcSetTitleValueUsingConfigDialog(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(titleV1.tcCheckExistenceOfTitleTypes(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(titleV1.tcSetTitleType(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(titleV1.tcCheckExistenceOfTypesUsingPolicy(tcExecuteBeforeTest, tcExecuteAfterTest, "/title", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(titleV1.tcCheckExistenceOfOneTypeUsingPolicy(tcExecuteBeforeTest, tcExecuteAfterTest, "/title", "core-component/components",
            c.policyPath, c.policyAssignmentPath));

}(hobs, jQuery));
