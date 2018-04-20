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
    var listV1 = window.CQ.CoreComponentsIT.List.v1;

    /**
     * v2 specifics
     */
    var tcExecuteBeforeTest = listV1.tcExecuteBeforeTest(c.rtList_v2, c.rtText_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = listV1.tcExecuteAfterTest();

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite("List v2", { path: "/apps/core/wcm/test-suites/List/v2/List.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(listV1.tcCreateListDirectChildren(tcExecuteBeforeTest, tcExecuteAfterTest, "core/wcm/tests/components/test-page-v2"))
        .addTestCase(listV1.tcCreateListChildren(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcListSubChildren(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcCreateFixedList(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcCreateListBySearch(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcCreateListAnyTagsMatching(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcCreateListAllTagsMatching(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcOrderByTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcChangeOrderingTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcOrderByLastModifiedDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcChangeOrderingDate(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcSetMaxItems(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcLinkItemsForList(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcShowDescriptionForList(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(listV1.tcShowDateForList(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
