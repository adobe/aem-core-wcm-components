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

window.CQ.CoreComponentsIT.FormButton.v1 = window.CQ.CoreComponentsIT.FormButton.v1 || {}

/**
 * Tests for core form button
 */
;(function(h, $) {
    "use strict";

    // short cut
    var c = window.CQ.CoreComponentsIT.commons;
    var formButton = window.CQ.CoreComponentsIT.FormButton.v1;

    /**
     * Before Test Case
     */
    formButton.tcExecuteBeforeTest = function(formButtonRT, pageRT) {
        return new h.TestCase("Create Sample Content")

            // common set up
            .execTestCase(c.tcExecuteBeforeTest)
            // create the test page, store page path in 'testPagePath'
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(formButtonRT, c.proxyPath, "compPath", done);
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
    formButton.tcExecuteAfterTest = function() {
        return new h.TestCase("Clean up after Test")

            // common clean up
            .execTestCase(c.tcExecuteAfterTest)
            // delete the test page we created
            .execFct(function(opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

    /**
     * Test: Check the attributes of the default button rendered without any customisations via the edit dialog
     */
    formButton.checkDefaultButtonAttributes = function(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check Default Button Attributes", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // check that the type of button should be "submit"
            .asserts.isTrue(function() {
                return h.find(buttonSelector + "[type='submit']", "#ContentFrame").size() === 1;
            })
            // check that the title on the button should be "Submit"
            .asserts.isTrue(function() {
                return h.find(buttonSelector, "#ContentFrame").text().trim() === "Submit";
            });
    };

    /**
     * Test: Create a button
     */
    formButton.createButton = function(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Create a Button", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // Open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // change the type of the button
            .click("button[is='coral-button']:contains('Submit')")
            .click("coral-selectlist-item:contains('Button')")
            // set the button text
            .fillInput("[name='./jcr:title']", "BUTTON")
            // close the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // Check if the button tag is rendered with the correct type
            .asserts.isTrue(function() {
                return h.find(buttonSelector + "[type='Button']", "#ContentFrame").size() === 1;
            });
    };

    /**
     * Test: Set button text
     */
    formButton.setButtonText = function(buttonSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Button Text", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // Open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the button text
            .fillInput("[name='./jcr:title']", "Test Button")
            // close the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // Check if the button tag is rendered with the correct type
            .asserts.isTrue(function() {
                return h.find(buttonSelector, "#ContentFrame").text().trim() === "Test Button";
            });
    };

    /**
     * Test: Set button name
     */
    formButton.setButtonName = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Button Name", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // Open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the button text
            .fillInput("[name='./name']", "button1")
            // set button title
            .fillInput("[name='./jcr:title']", "BUTTON WITH NAME")
            // close the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            // Check if the button tag is rendered with the correct type
            .asserts.isTrue(function() {
                return h.find("[name='button1']", "#ContentFrame").size() === 1;
            })
        ;
    };

    /**
     * Test: Set button value
     */
    formButton.setButtonValue = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Button Value", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // Open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the button text
            .fillInput("[name='./name']", "button1")
            // set the button text
            .fillInput("[name='./value']", "thisisthevalue")
            // set button title
            .fillInput("[name='./jcr:title']", "BUTTON WITH VALUE")
            // close the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // Check if the button tag is rendered with the correct type
            .asserts.isTrue(function() {
                return h.find("[value='thisisthevalue']", "#ContentFrame").size() === 1;
            });
    };

    formButton.setButtonValueWithoutName = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Button Value without Name", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // Open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput("[name='./jcr:title']", "Button with Value But No Name")
            // set the button's value
            .fillInput("[name='./value']", "value")
            // close the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function() {
                return h.find("coral-icon.coral-Form-fielderror").size() === 1;
            });
    };

})(hobs, jQuery);
