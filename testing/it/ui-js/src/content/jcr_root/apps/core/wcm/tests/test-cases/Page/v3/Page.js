/*******************************************************************************
 * Copyright 2019 Adobe
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

window.CQ.CoreComponentsIT.Page.v3 = window.CQ.CoreComponentsIT.Page.v3 || {};

/**
 * Tests for the core page component.
 */
(function(h, $) {
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var pageV3 = window.CQ.CoreComponentsIT.Page.v3;


    var LV1_PAGE1_PATH_PARAM = "lv1_page1_path";
    var LV1_PAGE2_PATH_PARAM = "lv1_page2_path";
    var LV2_PAGE1_PATH_PARAM = "lv2_page1_path";

    /**
     * Before Test Case
     */
    pageV3.tcExecuteBeforeTest = function() {
        var pageRT = "core/wcm/tests/components/test-page-v3";
        return new h.TestCase("Setup Before Test")
            .execTestCase(c.tcExecuteBeforeTest)
            // create a parent page
            .execFct(function(opts, done) {
                var rootTemplate = "/conf/core-components/settings/wcm/templates/core-components-v3-root";
                c.createPage(rootTemplate, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })
            // add 1st sub-page
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("testPagePath")(opts), "page1_lv1", LV1_PAGE1_PATH_PARAM, done, pageRT);
            })
            // add 2nd sub-page
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("testPagePath")(opts), "page2_lv1", LV1_PAGE2_PATH_PARAM, done, pageRT);
            })
            // add lv2 sub-page
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param(LV1_PAGE1_PATH_PARAM)(opts), "page_lv2", LV2_PAGE1_PATH_PARAM, done, pageRT);
            });
    };

    /**
     * Test: Check the JSON representation of a hierarchy of pages.
     *
     * 1: get the JSON representation of the page structure
     * 2: check if all of the children are there
     */
    pageV3.tcHierarchyJSON = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check the JSON export of a hierarchy of pages", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest,
            metadata: {
                ignoreOn63: true
            } })

            // 1
            .execFct(function(opts, done) {
                c.getJSON(h.param("testPagePath")() + ".model.json", "modelJSON", done);
            })

            // 2
            .assert.isTrue(function() {
                var children = h.param("modelJSON")()[":children"];

                var paths = [
                    h.param(LV1_PAGE1_PATH_PARAM)(),
                    h.param(LV1_PAGE2_PATH_PARAM)(),
                    h.param(LV2_PAGE1_PATH_PARAM)()
                ];

                return (Object.keys(children).length === 3 &&
                    children[paths[0]] && children[paths[1]] && children[paths[2]]);
            })
        ;


    };

}(hobs, jQuery));
