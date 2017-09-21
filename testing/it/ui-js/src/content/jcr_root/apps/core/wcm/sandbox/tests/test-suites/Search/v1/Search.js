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
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/* globals hobs,jQuery */
;(function (h, $) {
    'use strict';

    var c                                    = window.CQ.CoreComponentsIT.commons,
        search                               = window.CQ.CoreComponentsIT.Search.v1;

    var tcExecuteBeforeTest = search.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtSearch_v1,
        'core/wcm/sandbox/tests/components/test-page-v2');
    var tcExecuteAfterTest  = search.tcExecuteAfterTest(c.policyPath_sandbox, c.policyAssignmentPath_sandbox);

    new h.TestSuite('Search v1', {
        path           : '/apps/core/wcm/sandbox/tests/core-components-it/v1/search.js',
        execBefore     : c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(search.testDefaultConfiguration(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testChangeStartLevel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testClearButton(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testOutsideClick(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testMark(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(search.testMinLength(tcExecuteBeforeTest, tcExecuteAfterTest, "/search/v1/search", "core/wcm/sandbox/components",
            c.policyPath_sandbox, c.policyAssignmentPath_sandbox))
        .addTestCase(search.testResultsSize(tcExecuteBeforeTest, tcExecuteAfterTest, "/search/v1/search", "core/wcm/sandbox/components",
            c.policyPath_sandbox, c.policyAssignmentPath_sandbox))
        .addTestCase(search.testScrollDown(tcExecuteBeforeTest, tcExecuteAfterTest, "/search/v1/search", "core/wcm/sandbox/components",
            c.policyPath_sandbox, c.policyAssignmentPath_sandbox))

}(hobs, jQuery));
