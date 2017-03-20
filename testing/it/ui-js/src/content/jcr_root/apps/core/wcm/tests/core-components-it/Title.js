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

/**
 * Tests for the core title component.
 */
;(function(h, $){

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    /**
     * Before Test Case
     */
    var tcExecuteBeforeTest = TestCase("Setup Before Test")
        // common set up
        .execTestCase(c.tcExecuteBeforeTest)
        // create the test page , store page path in 'testPagePath'
        .execFct(function (opts,done) {
            c.createPage(c.template, c.rootPage ,'page_' + Date.now(),"testPagePath",done)
        })
        // add the component, store component path in 'cmpPath'
        .execFct(function (opts, done){
            c.addComponent(c.rtTitle, h.param("testPagePath")(opts)+c.relParentCompPath,"cmpPath",done)
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
     * Test: Set the title value using the inline editing.
     */
    var setTitleValueUsingInlineEditor = new h.TestCase("Set title using inline editor", {
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // start the inline editor
        .execTestCase(c.tcOpenInlineEditor("cmpPath"))

        //switch to the content frame
        .config.changeContext(c.getContentFrame)

        // set the example text
        .execFct(function() {
            h.find(".cmp.cmp-title  h1").html("Content test")
        })

        // remove the focus so it triggers the post request
        .simulate(".cmp.cmp-title  h1","blur")

        // check if text is rendered
        .assert.isTrue(
        function() {
            var actualValue = h.find('.cmp.cmp-title  h1').html();
            return actualValue === "Content test";
        })

        // swith back to edit frame
        .config.resetContext()

        // reload the page, to see if the text really got saved
        .navigateTo("/editor.html%testPagePath%.html")

        //switch to the content frame
        .config.changeContext(c.getContentFrame)

        // check if text is rendered
        .assert.isTrue(
        function() {
            var actualValue = h.find('.cmp.cmp-title  h1').html();
            return actualValue === "Content test";
        });

    /**
     * Test: Set the title value using the design dialog.
     */
    var setTitleValueUsingConfigDialog = new h.TestCase("Set title using config dialog",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // add some example text
        .fillInput("[name='./jcr:title']","Content name")
        // close the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // switch to content frame
        .config.changeContext(c.getContentFrame)

        // check if text is rendered correctly
        .assert.isTrue(function() {
            var actualValue = h.find('.cmp.cmp-title h1').html();
            return actualValue === "Content name";
        });

    /**
     * Test: Check the existence of all available title types.
     */
    var checkExistenceOfTitleTypes = new h.TestCase("Check available title types",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // check if all default title sizes are there
        .assert.exist(".coral3-SelectList-item[value='h1']")
        .assert.exist(".coral3-SelectList-item[value='h2']")
        .assert.exist(".coral3-SelectList-item[value='h3']")
        .assert.exist(".coral3-SelectList-item[value='h4']")
        .assert.exist(".coral3-SelectList-item[value='h5']")
        .assert.exist(".coral3-SelectList-item[value='h6']");

    /**
     * Test: Check if setting the title type works.
     */
    var setTitleType = new h.TestCase("Set the title type",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        /// open the edit dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        .click(".coral3-SelectList-item[value='h5']")
        .execTestCase(c.tcSaveConfigureDialog)

        .assert.isTrue(function () {
            return h.find(".cmp.cmp-title  h5","#ContentFrame").size() == 1});

    /**
     * The main test suite for Title component
     */
    new h.TestSuite("Core Components - Title", {path:"/apps/core/wcm/tests/core-components-it/Title.js",
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(setTitleValueUsingInlineEditor)
        .addTestCase(setTitleValueUsingConfigDialog)
        .addTestCase(checkExistenceOfTitleTypes)
        .addTestCase(setTitleType)
    ;
}(hobs, jQuery));
