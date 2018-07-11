/*
 *  Copyright 2018 Adobe Systems Incorporated
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
/* globals hobs,jQuery */
window.CQ.CoreComponentsIT.Breadcrumb.v2 = window.CQ.CoreComponentsIT.Breadcrumb.v2 || {}

/**
 * Test for the breadcrumb component
 */
;(function(h, $) {
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var breadcrumb = window.CQ.CoreComponentsIT.Breadcrumb.v2;

    /**
     * Test: structure data (schema.org)
     */
    breadcrumb.testStructureData = function(listSelector, itemSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check structure data", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // check first if itemType BreadcrumbList is available
            .config.changeContext(c.getContentFrame)
            .assert.exist(listSelector.itemType, true)
            // check if all items have itemType ListItem
            .assert.isTrue(function() {
                return h.find(itemSelector.itemType).size() === 6;
            });
    };

}(hobs, jQuery));
