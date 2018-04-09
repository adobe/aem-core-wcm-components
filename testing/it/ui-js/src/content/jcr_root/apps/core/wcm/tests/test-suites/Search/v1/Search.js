/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

/* globals hobs,jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var search = window.CQ.CoreComponentsIT.Search.v1;

    var tcExecuteBeforeTest = search.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtSearch_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = search.tcExecuteAfterTest(c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Search v1", {
        path: "/apps/core/wcm/tests/core-components-it/v1/search.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(search.testDefaultConfiguration(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testChangeSearchRoot(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testClearButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testKeyEnterInput(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testOutsideClick(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testMark(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testMinLength(tcExecuteBeforeTest, tcExecuteAfterTest, "/search", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(search.testResultsSize(tcExecuteBeforeTest, tcExecuteAfterTest, "/search", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(search.testScrollDown(tcExecuteBeforeTest, tcExecuteAfterTest, "/search", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/search/v1/search/clientlibs/site.js"))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/search/v1/search/clientlibs/site.css"));

}(hobs, jQuery));
