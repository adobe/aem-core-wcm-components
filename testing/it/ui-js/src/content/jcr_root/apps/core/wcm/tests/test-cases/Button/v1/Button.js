/*
 *  Copyright 2019 Adobe
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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    window.CQ.CoreComponentsIT.Button.v1 = window.CQ.CoreComponentsIT.Button.v1 || {};
    var c = window.CQ.CoreComponentsIT.commons;
    var button = window.CQ.CoreComponentsIT.Button.v1;

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create proxy component
     * 3. add the component to the page
     * 4. open the new page in the editor
     */
    button.tcExecuteBeforeTest = function(tcExecuteBeforeTest, buttonRT, pageRT) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })
            // 2.
            .execFct(function(opts, done) {
                c.createProxyComponent(buttonRT, c.proxyPath, "compPath", done);
            })
            // 3.
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPath")(opts), h.param("testPagePath")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            // 4.
            .navigateTo("/editor.html%testPagePath%.html");
    };

    /**
     * After Test Case
     *
     * 1. delete the test page
     * 2. delete the proxy component
     */
    button.tcExecuteAfterTest = function(tcExecuteAfterTest) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })
            // 2.
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

    /**
     * Test: Set button text
     *
     * 1. open the edit dialog
     * 2. set the button text
     * 3. close the edit dialog
     * 4. verify the button is rendered with the correct text
     */
    button.tcSetText = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set Text", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // 2.
            .fillInput(selectors.editDialog.text, "Test Button")
            // 3.
            .execTestCase(c.tcSaveConfigureDialog)
            // 4.
            .asserts.isTrue(function() {
                return h.find(selectors.button.self, "#ContentFrame").text().trim() === "Test Button";
            });
    };

    /**
     * Test: Set button link
     *
     * 1. open the edit dialog
     * 2. set the button link
     * 3. close the edit dialog
     * 4. verify the button is an anchor tag with the correct href attribute
     */
    button.tcSetLink = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set Link", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // 2.
            .fillInput(selectors.editDialog.link, "https://www.adobe.com")
            // 3.
            .execTestCase(c.tcSaveConfigureDialog)
            // 4.
            .asserts.isTrue(function() {
                return h.find("a[href='https://www.adobe.com']", "#ContentFrame").size() === 1;
            });
    };

    /**
     * Test: Set button icon
     *
     * 1. open the edit dialog
     * 2. set the button icon identifier
     * 3. close the edit dialog
     * 4. verify the button has an icon rendered with the correct icon identifier as modifier
     */
    button.tcSetIcon = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set Icon", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // 2.
            .fillInput(selectors.editDialog.icon, "email")
            // 3.
            .execTestCase(c.tcSaveConfigureDialog)
            // 4.
            .asserts.isTrue(function() {
                return h.find(".cmp-button__icon--email", "#ContentFrame").size() === 1;
            });
    };

})(hobs, jQuery);
