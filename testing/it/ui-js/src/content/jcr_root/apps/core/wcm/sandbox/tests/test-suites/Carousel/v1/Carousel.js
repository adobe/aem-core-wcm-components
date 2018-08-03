/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
    var carousel = window.CQ.CoreComponentsIT.Carousel.v1;
    var selectors = {
        editDialog: {
            addButton: ".childreneditor [coral-multifield-add]",
            firstRemoveButton: ".childreneditor button[handle='remove']:first",
            responsiveGridComponent: ".InsertComponentDialog.childreneditor coral-selectlist-item[value='/libs/wcm/foundation/components/responsivegrid']",
            firstItem: ".childreneditor coral-multifield-item:first",
            lastItem: ".childreneditor coral-multifield-item:last",
            lastItemInput: ".childreneditor coral-multifield-item:last input:first",
            itemInput: ".childreneditor coral-multifield-item input"
        }
    };

    var tcExecuteBeforeTest = carousel.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtCarousel_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = carousel.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Carousel v1", {
        path: "/apps/core/wcm/sandbox/tests/core-components-it/v1/Carousel.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(carousel.tcAddItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcReorderItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcRemoveItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));
}(hobs, jQuery));
