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

window.CQ.CoreComponentsIT.FormContainer.v1 = window.CQ.CoreComponentsIT.FormContainer.v1 || {}

;(function(h, $) {
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formContainer = window.CQ.CoreComponentsIT.FormContainer.v1;

    // root location where form content will be stored
    var userContent = "/content/usergenerated/core-components";

    // some test values
    var from = "from@component.com";
    var subject = "subject line";
    var mailto1 = "mailto1@components.com";
    var mailto2 = "mailto2@components.com";
    var cc1 = "cc1@components.com";
    var cc2 = "cc2@components.com";

    formContainer.tcExecuteBeforeTest = function(formContainerRT, formTextRT, formButtonRT, pageRT) {
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

            // inside the form add an form text input field
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathText")(opts), h.param("containerPath")(opts) + "/", "inputPath", done);
            })

            // set name and default value for the input field
            .execFct(function(opts, done) {
                var data = {};
                data.name = "inputname";
                data.defaultValue = "inputvalue";
                c.editNodeProperties(h.param("inputPath")(), data, done);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formButtonRT, c.proxyPath, "compPathButton", done);
            })

            // add a button to the form
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPathButton")(opts), h.param("containerPath")(opts) + "/", "buttonPath", done);
            })

            // make sure the button is a submit button
            .execFct(function(opts, done) {
                var data = {};
                data.type = "submit";
                data.title = "Submit";
                c.editNodeProperties(h.param("buttonPath")(), data, done);
            })

            // open the page in the editor
            .navigateTo("/editor.html%testPagePath%.html");
    };


    /**
     * After Test Case
     */
    formContainer.tcExecuteAfterTest = function() {
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
                c.deleteProxyComponent(h.param("compPathButton")(opts), done);
            });
    };

    /**
     * Test: Check if the action 'Store Content' works.
     */
    formContainer.storeContent = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Test Store Content action", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("containerPath"))
            // select action type
            .execTestCase(c.tcUseDialogSelect("./actionType", "foundation/components/form/actions/store"))
            // store the content path JSON Url in  a hobbes param
            .execFct(function(opts, done) {
                h.param("contentJsonUrl", h.find("input[name='./action']").val().slice(0, -1) + ".1.json");
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
                c.getJSON(h.param("contentJsonUrl")(opts), "json", done);
            })
            // check if the input value was saved
            .assert.isTrue(function() {
            // its stored in a child node with random name so we need to find it
                var data = h.param("json")();
                for (var prop in data) {
                // its the only sub object
                    if (typeof data[prop] === "object") {
                    // check the value is there
                        if (data[prop].inputname != null && data[prop].inputname === "inputvalue") {
                            return true;
                        }
                    }
                }
                // not found
                return false;
            });
    };

    /**
     * Test: set your own content path
     */
    formContainer.setContextPath = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Content Path", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("containerPath"))
            // select action type
            .execTestCase(c.tcUseDialogSelect("./actionType", "foundation/components/form/actions/store"))
            // check if the input field has become visible
            .assert.visible("input[name='./action']")
            // we set our own context path
            .fillInput("input[name='./action']", userContent + "/xxx")
            // close the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            // click on the submit button
            .click("button:contains('Submit')", { expectNav: true })

            // request json for the stored form content
            .execFct(function(opts, done) {
                c.getJSON(userContent + "/xxx/inputname.1.json", "formContentJson", done, 20, 500);
            })

            // check if the input value was saved
            .assert.isTrue(function() {
                var data = h.param("formContentJson")();
                if (data.inputname != null && data.inputname === "inputvalue") {
                    return true;
                } else {
                    return false;
                }
            });
    };

    /**
     * Test: set the thank You page path
     *
     * NOTE: Timing problem. the clean up after this test is faster then the user content being stored
     * so it not cleaned up reliably. Does not hurt but its not nice.
     * Once the thank you page option is available for all actions (See https://jira.corp.adobe.com/browse/CQ-106130)
     * switch to 'Mail' action to avoid this problem.
     */
    formContainer.setThankYouPage = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Thank You Page", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("containerPath"))
            // select action type
            .execTestCase(c.tcUseDialogSelect("./actionType", "foundation/components/form/actions/store"))
            // set the thank you page
            .execTestCase(c.tcSelectInAutocomplete("[name='./redirect']", c.rootPage))
            // close the dialog
            .execTestCase(c.tcSaveConfigureDialog)
            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            // click on the submit button
            .click("button:contains('Submit')", { expectNav: true })
            // go back to edit frame
            .config.resetContext()
            // check if the thank you page is shown
            .assert.isTrue(function() {
                return h.context().window.location.pathname.includes("core-components-page.html");
            });
    };

    /**
     * Test: check if 'Mail' action works.
     *
     */
    formContainer.setMailAction = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Test Mail action", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the config dialog
            .execTestCase(c.tcOpenConfigureDialog("containerPath"))
            // select action type
            .execTestCase(c.tcUseDialogSelect("./actionType", "foundation/components/form/actions/mail"))
            // wait for the dialog to update
            .assert.visible("[name='./from']")
            // set the 'from' field
            .fillInput("[name='./from']", from)
            // set the subject
            .fillInput("[name='./subject']", subject)

            // Fill in the Mailto
            .click("coral-multifield[data-granite-coral-multifield-name='./mailto'] > button")
            .fillInput("input[name='./mailto']", mailto1)
            .click("coral-multifield[data-granite-coral-multifield-name='./mailto'] > button")
            .fillInput("input[name='./mailto']:eq(1)", mailto2)

            // Fill in the CC
            .click("coral-multifield[data-granite-coral-multifield-name='./cc'] > button")
            .fillInput("input[name='./cc']", cc1)
            .click("coral-multifield[data-granite-coral-multifield-name='./cc'] > button")
            .fillInput("input[name='./cc']:eq(1)", cc2)

            // save the dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // workaround: request the 'from' property, does not exist yet. This way it will only continue once its
            // written changes to the repo otherwise we are to fast
            // TODO : implement some sort of polling
            .execFct(function(opts, done) {
                c.getJSON(h.param("containerPath")() + "/from.json", "json", done);
            })
            // get the json for the core form container
            .execFct(function(opts, done) {
                c.getJSON(h.param("containerPath")() + ".json", "json", done);
            })

            // check if the properties are stored
            .assert.isTrue(function() {
                return h.param("json")().from === from;
            })
            .assert.isTrue(function() {
                return h.param("json")().subject === subject;
            })
            .assert.isTrue(function() {
                return h.param("json")().mailto[0] === mailto1;
            })
            .assert.isTrue(function() {
                return h.param("json")().mailto[1] === mailto2;
            })
            .assert.isTrue(function() {
                return h.param("json")().cc[0] === cc1;
            })
            .assert.isTrue(function() {
                return h.param("json")().cc[1] === cc2;
            });
    };

}(hobs, jQuery));

