/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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

window.CQ.CoreComponentsIT.Text.v1 = window.CQ.CoreComponentsIT.Text.v1 || {}

/**
 * Tests for the core text component
 */
;(function(h, $) {
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var text = window.CQ.CoreComponentsIT.Text.v1;

    var testValue = "<b>This</b> is a <i>rich</i> <u>text</u>.";

    hobs.config.pacing_delay = 250;

    /**
     * Before Test Case
     */
    text.tcExecuteBeforeTest = function(textRT, pageRT) {
        return new h.TestCase("Setup Before Test")
            // common set up
            .execTestCase(c.tcExecuteBeforeTest)
            // create the test page, store page path in 'testPagePath'
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(textRT, c.proxyPath, "compPath", done);
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
    text.tcExecuteAfterTest = function() {
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
     * Test: Check if text is stored/rendered correctly using the inline editor
     */
    text.tcSetTextValueUsingInlineEditor = function(selectors, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set text using inline editor", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // open the inline editor
            .execTestCase(c.tcOpenInlineEditor("cmpPath"))

            // switch to the content frame
            .config.changeContext(c.getContentFrame)

            // set the example text
            .execFct(function() {
                h.find(selectors.editor).html(testValue);
            })

            // switch back to edit frame
            .config.resetContext()

            // click on save on the inline editor toolbar
            .execTestCase(c.tcSaveInlineEditor)

            // switch to the content frame
            .config.changeContext(c.getContentFrame)

            // check if the text is rendered
            .assert.isTrue(
                function() {
                    var actualValue = h.find(selectors.rendered).html();
                    return actualValue === testValue;
                })

            // swith back to edit frame
            .config.resetContext()

            // reload the page, to see if the text really got saved
            .navigateTo("/editor.html%testPagePath%.html")

            // switch to the content frame
            .config.changeContext(c.getContentFrame)

            // check again if the text is still there
            .assert.isTrue(
                function() {
                    var actualValue = h.find(selectors.rendered).html();
                    return actualValue === testValue;
                });
    };

    hobs.config.pacing_delay = 0;

}(hobs, jQuery));
