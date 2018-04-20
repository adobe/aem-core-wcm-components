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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var navigation = window.CQ.CoreComponentsIT.Navigation.v1;

    var tcExecuteBeforeTest = navigation.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtNavigation_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = navigation.tcExecuteAfterTest(c.tcExecuteAfterTest);

    new h.TestSuite("Navigation v1", {
        path: "/apps/core/wcm/test-suites/Navigation/v1/Navigation.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(navigation.testDefaultConfiguration(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(navigation.testIncludeNavigationRoot(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(navigation.testChangeStructureDepthLevel(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
