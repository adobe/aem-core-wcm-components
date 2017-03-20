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
 * Test for the breadcrumb component
 */
;(function(h, $){

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    /**
     * Before Test Case
     */
    var tcExecuteBeforeTest = new h.TestCase("Create Sample Content")
        // common set up
        .execTestCase(c.tcExecuteBeforeTest)

        // TODO : turn this into a loop or recursive

        // create level 1
        .execFct(function (opts,done) {
            c.createPage(c.template, c.rootPage ,"level_1","level_1",done)
        })
        // create level 2
        .execFct(function (opts,done) {
            c.createPage(c.template, h.param("level_1")() ,"level_2","level_2",done)
        })
        // create level 3
        .execFct(function (opts,done) {
            c.createPage(c.template, h.param("level_2")() ,"level_3","level_3",done)
        })
        // create level 4
        .execFct(function (opts,done) {
            c.createPage(c.template, h.param("level_3")() ,"level_4","level_4",done)
        })
        // create level 5
        .execFct(function (opts,done) {
            c.createPage(c.template, h.param("level_4")() ,"level_5","level_5",done)
        })

        // add the component to the deepest level
        .execFct(function (opts, done){
            c.addComponent(c.rtBreadcrumb, h.param("level_5")(opts)+c.relParentCompPath,"cmpPath",done)
        })

        // open the deepest level in the editor
        .navigateTo("/editor.html%level_5%.html");

    /**
     * After Test Case
     */
    var tcExecuteAfterTest = new TestCase("Clean up after Test")
        // common clean up
        .execTestCase(c.tcExecuteAfterTest)
        // delete the test page we created
        .execFct(function (opts, done) {
            c.deletePage(h.param("level_1")(opts), done);
        });

    /**
     * Test: Set the Hide Current flag
     */
    var testHideCurrent = new h.TestCase("Check Hide Current Flag",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // check first if current page is shown
        .config.changeContext(c.getContentFrame)
        // the li entry for current page
        .assert.exist("li.breadcrumb-item.active:contains('level_5')",true)
        .config.resetContext()

        // Open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // check the checkbox th make the current page hidden
        .click(".coral-Checkbox-input[name='./hideCurrent']")
        // Close the configuration dialog
        .execTestCase(c.tcSaveConfigureDialog)
        // got to the content frame
        .config.changeContext(c.getContentFrame)

        // the li entry for current page should not be found
        .assert.exist("li.breadcrumb-item.active:contains('level_5')",false);

    /**
     * Test: Set the Show Hidden flag
     */
    var testShowHidden = new TestCase("Check Show Hidden Flag",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // TODO : should be moved to its own execBefore function
        // set one of the pages has hidden first
        .execFct(function(opts,done){
            $.ajax({
                url: h.param("level_3")(),
                method: "POST",
                complete:done,
                // POST data to be send in the request
                data: {
                    "_charset_": "utf-8",
                    "./jcr:content/hideInNav": "true"
                }
            })
        })
        // reload the page to make the change visible
        .reload()

        // go to content frame
        .config.changeContext(c.getContentFrame)
        // verify level 3 is no longer available
        .assert.exist("li.breadcrumb-item > a:contains('level_3')",false)
        // go back to edit frame
        .config.resetContext()

        // Open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // check the checkbox to show hidden pages
        .click(".coral-Checkbox-input[name='./showHidden']")
        // Close the configuration dialog
        .execTestCase(c.tcSaveConfigureDialog)
        // got to the content frame
        .config.changeContext(c.getContentFrame)

        // the level 3 should be visible again
        .assert.exist("li.breadcrumb-item > a:contains('level_3')",true);

    /**
     * Test: Change the start level
     */
    var changeStartLevel = new TestCase("Change Start Level",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // check the current number of parent levels
        .assert.isTrue(function(){
            return h.find("li.breadcrumb-item","iframe#ContentFrame").size() === 6})

        // Open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // check the current config setting
        .assert.isTrue(function(){
            return h.find("input[name='./startLevel']").val() == 2})
        // increase start level by 2
        .fillInput("input[name='./startLevel']", 4)
        // Close the configuration dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // check the current number
        .assert.isTrue(function(){
            return h.find("li.breadcrumb-item","iframe#ContentFrame").size() === 4});

    /**
     * Test: Set the start level to lowest allowed value of 0.
     * This shouldn't render anything since level 0 is not a valid page.
     */
    var setZeroStartLevel = new TestCase("Set Start Level to 0",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // check the current number of items
        .assert.isTrue(function(){
            return h.find("li.breadcrumb-item","iframe#ContentFrame").size() === 6})

        // Open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set it to 0
        .fillInput("input[name='./startLevel']", 0)
        // Close the configuration dialog
        .asserts.visible(c.selConfigDialog)
        // check if element name is marked as invalid
        .asserts.isTrue(function() {
            return h.find("input[name='./startLevel'].is-invalid").size() == 1
        })

    /**
     * Test: Set the start level to the highest possible value 100.
     * This shouldn't render anything since level 100 is higher the the current's page level.
     */
    var set100StartLevel = new TestCase("Set Start Level to 100",{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // check the current number of items
        .assert.isTrue(function(){
            return h.find("li.breadcrumb-item","iframe#ContentFrame").size() === 6})
        // Open the configuration dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set it to 100
        .fillInput("input[name='./startLevel']", 100)
        // Close the configuration dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // 100 is higher then current level so nothing should get rendered
        .assert.isTrue(function(){
            return h.find("li.breadcrumb-item","iframe#ContentFrame").size() === 0 &&
                h.find("li.breadcrumb-item.active","iframe#ContentFrame").size() === 0
        });

    /**
     * The main test suite.
     */
    new h.TestSuite("Core Components - Breadcrumb", {path:"/apps/core/wcm/tests/core-components-it/Breadcrumb.js",
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(testHideCurrent)
        .addTestCase(testShowHidden)
        .addTestCase(changeStartLevel)
        .addTestCase(setZeroStartLevel)
        .addTestCase(set100StartLevel);

}(hobs, jQuery));