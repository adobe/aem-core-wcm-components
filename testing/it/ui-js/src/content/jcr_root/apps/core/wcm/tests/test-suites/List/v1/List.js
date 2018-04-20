/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Tests for the core text component
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var list = window.CQ.CoreComponentsIT.List.v1;

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = list.tcExecuteBeforeTest(c.rtList_v1, c.rtText_v1);
    var tcExecuteAfterTest = list.tcExecuteAfterTest();

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite("List v1", { path: "/apps/core/wcm/tests/test-suites/List/v1/List.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(list.tcCreateListDirectChildren(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcCreateListChildren(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcListSubChildren(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcCreateFixedList(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcCreateListBySearch(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcCreateListAnyTagsMatching(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcCreateListAllTagsMatching(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcOrderByTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcChangeOrderingTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcOrderByLastModifiedDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcChangeOrderingDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcSetMaxItems(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(list.tcLinkItemsForList(tcExecuteBeforeTest, tcExecuteAfterTest))

        .addTestCase(list.tcShowDescriptionForList(tcExecuteBeforeTest, tcExecuteAfterTest))

        .addTestCase(list.tcShowDateForList(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/list/v1/list/clientlibs/site.css"));

}(hobs, jQuery));
