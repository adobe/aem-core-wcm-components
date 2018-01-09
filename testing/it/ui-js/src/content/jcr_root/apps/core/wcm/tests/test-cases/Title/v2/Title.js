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

window.CQ.CoreComponentsIT.Title.v2 = window.CQ.CoreComponentsIT.Title.v2 || {}

/**
 * Tests for the core title component.
 */
;(function(h, $){

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var title = window.CQ.CoreComponentsIT.Title.v2;

    /**
     * Before Test Case
     */
    title.tcExecuteBeforeTest = function(titleRT, pageRT) {
        return TestCase("Setup Before Test")
            // common set up
            .execTestCase(c.tcExecuteBeforeTest)
            // create the test page , store page path in 'testPagePath'
            .execFct(function (opts, done) {
                c.createPage(c.template, c.rootPage, 'page_' + Date.now(), "testPagePath", done, pageRT)
            })

            // create a proxy component
            .execFct(function (opts, done){
                c.createProxyComponent(titleRT, c.proxyPath, "compPath", done)
            })

            // add the component, store component path in 'cmpPath'
            .execFct(function (opts, done){
                c.addComponent(h.param("compPath")(opts), h.param("testPagePath")(opts) + c.relParentCompPath, "cmpPath", done)
            })
            // open the new page in the editor
            .navigateTo("/editor.html%testPagePath%.html");
    };

    /**
     * After Test Case
     */
    title.tcExecuteAfterTest = function(policyPath, policyAssignmentPath) {
        return new TestCase("Clean up after Test")
            // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete the test page we created
            .execFct(function (opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })

            .execFct(function (opts, done) {
                c.deletePolicy("/title", done, policyPath);
            })
            .execFct(function (opts, done) {
                c.deletePolicyAssignment("/title", done, policyAssignmentPath);
            })

            // delete the test page we created
            .execFct(function (opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

}(hobs, jQuery));
