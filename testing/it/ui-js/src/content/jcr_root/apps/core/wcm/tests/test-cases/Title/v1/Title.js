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

window.CQ.CoreComponentsIT.Title.v1 = window.CQ.CoreComponentsIT.Title.v1 || {}

/**
 * Tests for the core title component.
 */
;(function(h, $) {
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var title = window.CQ.CoreComponentsIT.Title.v1;

    /**
     * Before Test Case
     */
    title.tcExecuteBeforeTest = function(titleRT, pageRT) {
        return new h.TestCase("Setup Before Test")
            // common set up
            .execTestCase(c.tcExecuteBeforeTest)
            // create the test page , store page path in 'testPagePath'
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(titleRT, c.proxyPath, "compPath", done);
            })

            // add the component, store component path in 'cmpPath'
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPath")(opts), h.param("testPagePath")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            // open the new page in the editor
            .navigateTo("/editor.html%testPagePath%.html");
    };

    /**
     * After Test Case
     */
    title.tcExecuteAfterTest = function(policyPath, policyAssignmentPath) {
        return new h.TestCase("Clean up after Test")
            // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete the test page we created
            .execFct(function(opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })

            .execFct(function(opts, done) {
                c.deletePolicy("/title", done, policyPath);
            })
            .execFct(function(opts, done) {
                c.deletePolicyAssignment("/title", done, policyAssignmentPath);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

    /**
     * Test: Set the title value using the inline editing.
     */
    title.tcSetTitleValueUsingInlineEditor = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set title using inline editor", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // start the inline editor
            .execTestCase(c.tcOpenInlineEditor("cmpPath"))

            // switch to the content frame
            .config.changeContext(c.getContentFrame)

            // set the example text
            .execFct(function() {
                h.find(".cmp-title  h1").html("Content test");
            })

            // remove the focus so it triggers the post request
            .simulate(".cmp-title  h1", "blur")

            // check if text is rendered
            .assert.isTrue(
                function() {
                    var actualValue = h.find(".cmp-title  h1").html();
                    return actualValue === "Content test";
                })

            // swith back to edit frame
            .config.resetContext()

            // reload the page, to see if the text really got saved
            .navigateTo("/editor.html%testPagePath%.html")

            // switch to the content frame
            .config.changeContext(c.getContentFrame)

            // check if text is rendered
            .assert.isTrue(
                function() {
                    var actualValue = h.find(".cmp-title  h1").html();
                    return actualValue === "Content test";
                });
    };

    /**
     * Test: Set the title value using the design dialog.
     */
    title.tcSetTitleValueUsingConfigDialog = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set title using config dialog", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the configuration dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // add some example text
            .fillInput("[name='./jcr:title']", "Content name")
            // close the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to content frame
            .config.changeContext(c.getContentFrame)

            // check if text is rendered correctly
            .assert.isTrue(function() {
                var actualValue = h.find(".cmp-title h1").html();
                return actualValue === "Content name";
            });
    };

    /**
     * Test: Check the existence of all available title types.
     */
    title.tcCheckExistenceOfTitleTypes = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check available title types", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // check if all default title sizes are there
            .assert.exist("coral-selectlist-item[value='h1']")
            .assert.exist("coral-selectlist-item[value='h2']")
            .assert.exist("coral-selectlist-item[value='h3']")
            .assert.exist("coral-selectlist-item[value='h4']")
            .assert.exist("coral-selectlist-item[value='h5']")
            .assert.exist("coral-selectlist-item[value='h6']");
    };

    /**
     * Test: Check if setting the title type works.
     */
    title.tcSetTitleType = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set the title type", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // / open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .click("coral-selectlist-item[value='h5']")
            .execTestCase(c.tcSaveConfigureDialog)

            .assert.isTrue(function() {
                return h.find(".cmp-title  h5", "#ContentFrame").size() === 1;
            });
    };

    /**
     * Test: Check the existence of all available title types defined in a policy.
     */
    title.tcCheckExistenceOfTypesUsingPolicy = function(tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check available title types defined in a policy", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execFct(function(opts, done) {
                var data = {};
                data["allowedTypes"] = ["h2", "h3", "h4", "h6"];
                data["jcr:title"] = "New Policy";
                data["sling:resourceType"] = "wcm/core/components/policy/policy";
                data["type"] = "h2";

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath);

            })

            .execFct(function(opts, done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath);

            })

            // open the dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // check if all title sizes defined in policy are there
            .assert.exist("coral-selectlist-item[value='h2']")
            .assert.exist("coral-selectlist-item[value='h3']")
            .assert.exist("coral-selectlist-item[value='h4']")
            .assert.exist("coral-selectlist-item[value='h6']")

            // check if the default value is selected
            // .assert.exist("coral-selectlist-item[value='h2'].is-selected")
            .execTestCase(c.tcSaveConfigureDialog)

            .assert.isTrue(function() {
                return h.find(".cmp-title h2", "#ContentFrame").size() === 1;
            })
        ;
    };

    /**
     * Test: Check the type used when one type is defined in the policy.
     */
    title.tcCheckExistenceOfOneTypeUsingPolicy = function(tcExecuteBeforeTest, tcExecuteAfterTest, policyName, policyLocation, policyPath, policyAssignmentPath) {
        return new h.TestCase("Check the type used when one type is defined in the policy", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            .execFct(function(opts, done) {
                var data = {};
                data["allowedTypes"] = "h5";
                data["jcr:title"] = "New Policy";
                data["sling:resourceType"] = "wcm/core/components/policy/policy";
                data["type"] = "h5";

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath);
            })

            .execFct(function(opts, done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath);
            })

            // open the dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execTestCase(c.tcSaveConfigureDialog)

            .assert.isTrue(function() {
                return h.find(".cmp-title h5", "#ContentFrame").size() === 1;
            })
        ;
    };

}(hobs, jQuery));
