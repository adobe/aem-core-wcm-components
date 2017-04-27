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

/**
 * Tests for the core text component
 */
;(function (h, $) {

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    var testValue = '<b>This</b> is a <i>rich</i> <u>text</u>.';

    /**
     * Before Test Case
     */
    var tcExecuteBeforeTest = new TestCase("Setup Before Test")
        // common set up
        .execTestCase(c.tcExecuteBeforeTest)
        // create the test page, store page path in 'testPagePath'
        .execFct(function (opts,done) {
            c.createPage(c.template, c.rootPage ,'page_' + Date.now(),"testPagePath",done)
        })
        // add the component, store component path in 'cmpPath'
        .execFct(function (opts, done){
            c.addComponent(c.rtText, h.param("testPagePath")(opts)+c.relParentCompPath,"cmpPath",done)
        })
        // open the new page in the editor
        .navigateTo("/editor.html%testPagePath%.html");

    /**
     * After Test Case
     */
    var tcExecuteAfterTest = new TestCase("Clean up after Test")
        // common clean up
        .execTestCase(c.tcExecuteAfterTest)
        // delete the test page we created
        .execFct(function (opts, done) {
            c.deletePage(h.param("testPagePath")(opts), done);
        });

    /**
     * Test: Check if text is stored/rendered correctly using the inline editor
     */
    var setTextValueUsingInlineEditor = new h.TestCase('Set text using inline editor',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the inline editor
        .execTestCase(c.tcOpenInlineEditor("cmpPath"))

        //switch to the content frame
        .config.changeContext(c.getContentFrame)

        // set the example text
        .execFct(function() {
            h.find('.cmp-text.aem-GridColumn p').html(testValue);
        })

        // switch back to edit frame
        .config.resetContext()

        // click on save on the inline editor toolbar
        .execTestCase(c.tcSaveInlineEditor)

        //switch to the content frame
        .config.changeContext(c.getContentFrame)

        // check if the text is rendered
        .assert.isTrue(
        function() {
            var actualValue = h.find('.cmp-text.aem-GridColumn  p').html();
            return actualValue === testValue;
        })

        // swith back to edit frame
        .config.resetContext()

        // reload the page, to see if the text really got saved
        .navigateTo("/editor.html%testPagePath%.html")

        //switch to the content frame
        .config.changeContext(c.getContentFrame)

        // check again if the text is still there
        .assert.isTrue(
        function() {
            var actualValue = h.find('.cmp-text.aem-GridColumn  p').html();
            return actualValue === testValue;
        });

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite('Core Components - Text', {path: '/apps/core/wcm/tests/core-components-it/Text.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : true})

        .addTestCase(setTextValueUsingInlineEditor)
    ;
}(hobs, jQuery));