/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Test for the breadcrumb component
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var breadcrumbV1 = window.CQ.CoreComponentsIT.Breadcrumb.v1;

    /**
     * v2 specifics
     */
    var itemSelector = {
        normal: ".cmp-breadcrumb__item",
        active: ".cmp-breadcrumb__item--active"
    };
    var tcExecuteBeforeTest = breadcrumbV1.tcExecuteBeforeTest(c.rtBreadcrumb_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = breadcrumbV1.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Breadcrumb v2", { path: "/apps/core/wcm/test-suites/Breadcrumb/v2/Breadcrumb.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(breadcrumbV1.testHideCurrent(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumbV1.testShowHidden(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumbV1.changeStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumbV1.setZeroStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumbV1.set100StartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/breadcrumb/v2/breadcrumb/clientlibs/site.css"));

}(hobs, jQuery));
