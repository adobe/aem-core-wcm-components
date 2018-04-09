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
 * Test for the breadcrumb component
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var breadcrumb = window.CQ.CoreComponentsIT.Breadcrumb.v1;

    /**
     * v1 specifics
     */
    var itemSelector = {
        normal: "li.breadcrumb-item",
        active: "li.breadcrumb-item.active"
    };
    var tcExecuteBeforeTest = breadcrumb.tcExecuteBeforeTest(c.rtBreadcrumb_v1);
    var tcExecuteAfterTest = breadcrumb.tcExecuteAfterTest();


    /**
     * The main test suite.
     */
    new h.TestSuite("Breadcrumb v1", { path: "/apps/core/wcm/tests/test-suites/Breadcrumb/v1/Breadcrumb.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(breadcrumb.testHideCurrent(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.testShowHidden(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.changeStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.setZeroStartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(breadcrumb.set100StartLevel(itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/breadcrumb/v1/breadcrumb/clientlibs/site.css"));

}(hobs, jQuery));
