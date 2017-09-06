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
            // add the component, store component path in 'cmpPath'
            .execFct(function (opts, done){
                c.addComponent(titleRT, h.param("testPagePath")(opts) + c.relParentCompPath, "cmpPath", done)
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
            });
    };

    /**
     * Test: Check the CSS classes with following conditions:
     * - policy dialog: style section is optional, two styles are defined
     * - edit dialog: no config
     * -> the wrapper div of the title component has no classes
     */
    title.tcCheckCssClasses1 = function (tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check the CSS classes #1",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            .execFct(function (opts,done) {
                var data = {
                    "allowedTypes" : "h5",
                    "jcr:title" : "New Policy",
                    "sling:resourceType" : "wcm/core/components/policy/policy",
                    "type" : "h5",
                    "cq:allowNoStyle" : "true",
                    "cq:styles/item0/cq:styleName" : "s1",
                    "cq:styles/item0/cq:styleClasses" : "c1",
                    "cq:styles/item0/cq:styleId" : "1501764085853",
                    "cq:styles/item1/cq:styleName" : "s2",
                    "cq:styles/item1/cq:styleClasses" : "c2",
                    "cq:styles/item1/cq:styleId" : "1501764090487"
                };

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath)

            })

            .execFct(function (opts,done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath)

            })

            // reload the page
            .navigateTo("/editor.html%testPagePath%.html")

            .assert.isTrue(function () {
                return !h.find(".cmp-title","#ContentFrame").parent().hasClass("c1") &&
                        !h.find(".cmp-title","#ContentFrame").parent().hasClass("c2")})
            ;

    };


    /**
     * Test: Check the CSS classes with following conditions:
     * - policy dialog: style section is mandatory, two styles are defined
     * - edit dialog: no config
     * -> the wrapper div of the title component has the first class of the two styles
     */
    title.tcCheckCssClasses2 = function (tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check the CSS classes #2",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            .execFct(function (opts,done) {
                var data = {
                    "allowedTypes" : "h5",
                    "jcr:title" : "New Policy",
                    "sling:resourceType" : "wcm/core/components/policy/policy",
                    "type" : "h5",
                    "cq:allowNoStyle" : "false",
                    "cq:styles/item0/cq:styleName" : "s1",
                    "cq:styles/item0/cq:styleClasses" : "c1",
                    "cq:styles/item0/cq:styleId" : "1501764085853",
                    "cq:styles/item1/cq:styleName" : "s2",
                    "cq:styles/item1/cq:styleClasses" : "c2",
                    "cq:styles/item1/cq:styleId" : "1501764090487"
                };

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath)

            })

            .execFct(function (opts,done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath)

            })

            // reload the page
            .navigateTo("/editor.html%testPagePath%.html")

            .assert.isTrue(function () {
                return h.find(".cmp-title","#ContentFrame").parent().hasClass("c1")})
            ;

    };


    /**
     * Test: Check the CSS classes with following conditions:
     * - policy dialog: style section is optional, two styles are defined
     * - edit dialog: "none" is selected as style
     * -> the wrapper div of the title component has no classes
     */
    title.tcCheckCssClasses3 = function (tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check the CSS classes #3",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            .execFct(function (opts,done) {
                var data = {
                    "allowedTypes" : "h5",
                    "jcr:title" : "New Policy",
                    "sling:resourceType" : "wcm/core/components/policy/policy",
                    "type" : "h5",
                    "cq:allowNoStyle" : "true",
                    "cq:styles/item0/cq:styleName" : "s1",
                    "cq:styles/item0/cq:styleClasses" : "c1",
                    "cq:styles/item0/cq:styleId" : "1501764085853",
                    "cq:styles/item1/cq:styleName" : "s2",
                    "cq:styles/item1/cq:styleClasses" : "c2",
                    "cq:styles/item1/cq:styleId" : "1501764090487"
                };

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath)

            })

            .execFct(function (opts,done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath)

            })

            // open the edit dialog and select "none" style
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .click("coral-selectlist-item[value='none']")
            .execTestCase(c.tcSaveConfigureDialog)

            .assert.isTrue(function () {
                return !h.find(".cmp-title","#ContentFrame").parent().hasClass("c1") &&
                    !h.find(".cmp-title","#ContentFrame").parent().hasClass("c2")})
            ;

    };

    /**
     * Test: Check the CSS classes with following conditions:
     * - policy dialog: style section is optional, two styles are defined
     * - edit dialog: the second style (s2) is selected
     * -> the wrapper div of the title component has the second class (c2) of the two styles
     */
    title.tcCheckCssClasses4 = function (tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check the CSS classes #4",{
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            .execFct(function (opts,done) {
                var data = {
                    "allowedTypes" : "h5",
                    "jcr:title" : "New Policy",
                    "sling:resourceType" : "wcm/core/components/policy/policy",
                    "type" : "h5",
                    "cq:allowNoStyle" : "true",
                    "cq:styles/item0/cq:styleName" : "s1",
                    "cq:styles/item0/cq:styleClasses" : "c1",
                    "cq:styles/item0/cq:styleId" : "1501764085853",
                    "cq:styles/item1/cq:styleName" : "s2",
                    "cq:styles/item1/cq:styleClasses" : "c2",
                    "cq:styles/item1/cq:styleId" : "1501764090487"
                };

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath)

            })

            .execFct(function (opts,done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath)

            })

            // open the edit dialog and select the 2nd style (s2)
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .click("coral-selectlist-item[value='1501764090487']")
            .execTestCase(c.tcSaveConfigureDialog)

            .assert.isTrue(function () {
                return h.find(".cmp-title","#ContentFrame").parent().hasClass("c2")})
            ;

    };

}(hobs, jQuery));
