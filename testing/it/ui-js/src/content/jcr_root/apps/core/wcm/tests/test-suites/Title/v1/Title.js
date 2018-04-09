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

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var title = window.CQ.CoreComponentsIT.Title.v1;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = title.tcExecuteBeforeTest(c.rtTitle_v1);
    var tcExecuteAfterTest = title.tcExecuteAfterTest(c.policyPath, c.policyAssignmentPath);

    /**
     * The main test suite for Title component
     */
    new h.TestSuite("Title v1", { path: "/apps/core/wcm/tests/test-suites/core-components-suites/Title.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        // TODO: Removed for now as it not stable randomly failing
        // .addTestCase(title.tcSetTitleValueUsingInlineEditor(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(title.tcSetTitleValueUsingConfigDialog(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(title.tcCheckExistenceOfTitleTypes(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(title.tcSetTitleType(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(title.tcCheckExistenceOfTypesUsingPolicy(tcExecuteBeforeTest, tcExecuteAfterTest, "/title", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(title.tcCheckExistenceOfOneTypeUsingPolicy(tcExecuteBeforeTest, tcExecuteAfterTest, "/title", "core-component/components",
            c.policyPath, c.policyAssignmentPath));

}(hobs, jQuery));
