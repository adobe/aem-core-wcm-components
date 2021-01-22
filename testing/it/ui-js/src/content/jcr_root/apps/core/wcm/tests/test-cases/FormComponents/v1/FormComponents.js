/*
 *  Copyright 2016 Adobe
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
window.CQ.CoreComponentsIT.FormComponents.v1 = window.CQ.CoreComponentsIT.FormComponents.v1 || {}

/**
 * Tests for the core form components
 */
;(function(h, $) {
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formComponents = window.CQ.CoreComponentsIT.FormComponents.v1;

    // root location where form content will be stored
    var userContent = "/content/usergenerated/core-components";

    formComponents.tcExecuteBeforeTest = function(formContainerRT, formTextRT, formHihhenRT, formOptionsRT, formBUttonRT, pageRT) {
        return new h.TestCase("Setup Before Test")

            // common set up
            .execTestCase(c.tcExecuteBeforeTest)

            // create the test page, store page path in 'testPagePath'
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formContainerRT, c.proxyPath, "compPathContainer", done);
            })

            // add the core form container component
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathContainer")(opts), h.param("testPagePath")(opts) + c.relParentCompPath, "containerPath", done);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formTextRT, c.proxyPath, "compPathText", done);
            })

            // inside the form add a form text input field
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathText")(opts), h.param("containerPath")(opts) + "/", "inputPath", done);
            })

            // set name and default value for the input field
            .execFct(function(opts, done) {
                var data = {};
                data.name = "inputName";
                data.defaultValue = "inputValue";
                c.editNodeProperties(h.param("inputPath")(), data, done);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formHihhenRT, c.proxyPath, "compPathHidden", done);
            })

            // inside the form add a hidden field component
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathHidden")(opts), h.param("containerPath")(opts) + "/", "hiddenPath", done);
            })

            // set name and default value for the hidden field component
            .execFct(function(opts, done) {
                var data = {};
                data.name = "hiddenName";
                data.value = "hiddenValue";
                c.editNodeProperties(h.param("hiddenPath")(), data, done);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formOptionsRT, c.proxyPath, "compPathOptions", done);
            })

            // inside the form add a form option component
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathOptions")(opts), h.param("containerPath")(opts) + "/", "optionPath", done);
            })

            // create an option list items
            .execFct(function(opts, done) {
                // create the option component
                var data = {};
                data["./name"] = "optionName";
                data["./type"] = "checkbox";
                data["./items/item0/selected"] = "true";
                data["./items/item0/text"] = "text1";
                data["./items/item0/value"] = "value1";

                data["./items/item1/selected"] = "false";
                data["./items/item1/text"] = "text2";
                data["./items/item1/value"] = "value2";
                c.editNodeProperties(h.param("optionPath")(), data, done);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formBUttonRT, c.proxyPath, "compPathButton", done);
            })

            // add a button to the form
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathButton")(opts), h.param("containerPath")(opts) + "/", "buttonPath", done);
            })

            // make sure the button is a submit button
            .execFct(function(opts, done) {
                var data = {};
                data.type = "submit";
                data.caption = "Submit";
                c.editNodeProperties(h.param("buttonPath")(), data, done);
            })

            // open the page in the editor
            .navigateTo("/editor.html%testPagePath%.html");
    };


    /**
     * After Test Case
     */
    formComponents.tcExecuteAfterTest = function() {
        return new h.TestCase("Clean up after Test")

            // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete any user generated content
            .execFct(function(opts, done) {
                c.deletePage(userContent, done);
            })
            // delete the test page we created
            .execFct(function(opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPathContainer")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPathText")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPathHidden")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPathOptions")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPathButton")(opts), done);
            });
    };

    /**
     * Test: Check if the action 'Store Content' works.
     */
    formComponents.storeContent = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Test Store Content action", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("containerPath"))
            // select action type
            .execTestCase(c.tcUseDialogSelect("./actionType", "foundation/components/form/actions/store"))
            // store the content path JSON Url in  a hobbes param
            .execFct(function(opts, done) {
                h.param("contentJsonUrl_allForm", h.find("input[name='./action']").val().slice(0, -1) + ".3.json");
                done();
            })
            // close the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            // click on the submit button
            .click("button:contains('Submit')", { expectNav: true })

            // get the json for the content node
            .execFct(function(opts, done) {
                c.getJSON(h.param("contentJsonUrl_allForm")(opts), "json_allForm", done);
            })
            // check if all values for the form components are saved
            .assert.isTrue(function() {
                // its stored in a child node with random name so we need to find it
                var data = h.param("json_allForm")();
                for (var prop in data) {
                // its the only sub object
                    if (typeof data[prop] === "object") {
                    // check if the input value is there
                        if (data[prop].inputName == null || data[prop].inputName !== "inputValue") {
                            return false;
                        }
                        // check if the hidden value is there
                        if (data[prop].hiddenName == null || data[prop].hiddenName !== "hiddenValue") {
                            return false;
                        }
                        // check if the option value is there
                        if (data[prop].optionName == null || data[prop].optionName !== "value1") {
                            return false;
                        }
                    }
                }
                // not found
                return true;
            });
    };

}(hobs, jQuery));
