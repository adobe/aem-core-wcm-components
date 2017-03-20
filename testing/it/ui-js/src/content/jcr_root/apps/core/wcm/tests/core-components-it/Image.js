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

    var testImagePath = "/content/dam/core-components/core-comp-test-image.jpg";
    var altText = "Return to Arkham";
    var captionText = "The Last Guardian";

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
            c.addComponent(c.rtImage, h.param("testPagePath")(opts)+c.relParentCompPath,"cmpPath",done)
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

    var tcSetMinimalProps = new TestCase("Set Image and Alt Text")
        .execFct(function (opts,done) {c.openSidePanel(done);})
        // drag'n'drop the test image
        .cui.dragdrop("coral-card.cq-draggable[data-path='" + testImagePath + "']","coral-fileupload[name='./file'")
        // set mandatory alt text
        .fillInput("input[name='./alt']",altText)
        // close the side panel
        .execTestCase(c.closeSidePanel);



    /**
     * Test: add image
     */
    var addImage = new h.TestCase('Add an Image',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text
        .execTestCase(tcSetMinimalProps)
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // verify that the surrounding script tag has been removed and the img tag is there
        .asserts.isTrue(function () {
            return h.find("div.cmp-image img[src*='"+ h.param("testPagePath")() +
                "/_jcr_content/root/responsivegrid/image.img.jpg']", "#ContentFrame").size() == 1;
        });

    /**
     * Test: set Alt Text
     */
    var addAltText = new h.TestCase('Set Alt Text',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text
        .execTestCase(tcSetMinimalProps)
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // verify that alt text is there
        .asserts.isTrue(function () {
            return h.find("div.cmp-image img[alt='"+altText +"']", "#ContentFrame").size() == 1;
        });

    /**
     * Test: set link on image
     */
    var setLink = new h.TestCase('Set Link',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text
        .execTestCase(tcSetMinimalProps)
        // enter the link
        .simulate("foundation-autocomplete[name='./linkURL'] input[type!='hidden']", "key-sequence",
        {sequence: c.rootPage + "{enter}"})
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // switch to content frame
        .config.changeContext(c.getContentFrame)
        // click on the image
        .click("div.cmp-image img",{expectNav: true})
        // go back to top frame
        .config.resetContext()
        // check if the url is correct
        .asserts.isTrue(function(){
            return hobs.context().window.location.pathname.endsWith(c.rootPage + ".html")
        });

    /**
     * Test: set caption
     */
    var setCaption = new h.TestCase('Set Caption',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text
        .execTestCase(tcSetMinimalProps)
        // set caption text
        .fillInput("input[name='./jcr:title']",captionText)
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // switch to content frame
        .config.changeContext(c.getContentFrame)
        // check if the caption is rendered with <small> tag
        .asserts.isTrue(function(){
            return h.find("span.cmp-image--title:contains('" + captionText + "')").size() == 1
        });

    /**
     * Test: set caption as pop up
     */
    var setCaptionAsPopup = new h.TestCase('Set Caption as Pop Up',{
        execBefore: tcExecuteBeforeTest,
        execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text
        .execTestCase(tcSetMinimalProps)
        // set caption text
        .fillInput("input[name='./jcr:title']",captionText)
        // check the 'Caption as Pop Up' flag
        .click("input[type='checkbox'][name='./displayPopupTitle']")
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // switch to content frame
        .config.changeContext(c.getContentFrame)
        // check if the caption is rendered with <small> tag
        .asserts.isTrue(function(){
            return h.find("div.cmp-image img[title='" + captionText + "']").size() == 1
        });

    /**
     * Test: set caption as pop up
     */
    var setImageAsDecorative = new h.TestCase('Set Image as decorative',{
        execBefore: tcExecuteBeforeTest,
         execAfter: tcExecuteAfterTest})

        // open the config dialog
        .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
        // set image and alt text (to see if its not rendered)
        .execTestCase(tcSetMinimalProps)
        // save the dialog
        .execTestCase(c.tcSaveConfigureDialog)

        // switch to content frame
        .config.changeContext(c.getContentFrame)
        // check if the image is rendered without alt text even if it is set in the edit dialog

        .asserts.isTrue(function () {
            return h.find("div.cmp-image > img[alt='"+altText +"']", "#ContentFrame").size() == 0;
        });

    /**
     * The main test suite for Image Component
     */
    new h.TestSuite('Core Components - Image', {path: '/apps/core/wcm/tests/core-components-it/Image.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(addImage)
        .addTestCase(addAltText)
        .addTestCase(setLink)
        .addTestCase(setCaption)
        .addTestCase(setCaptionAsPopup)
        .addTestCase(setImageAsDecorative)

    ;
}(hobs, jQuery));
