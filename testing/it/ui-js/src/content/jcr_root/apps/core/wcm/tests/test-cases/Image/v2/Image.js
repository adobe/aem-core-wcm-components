/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/* globals hobs,jQuery */
window.CQ.CoreComponentsIT.Image.v2 = window.CQ.CoreComponentsIT.Image.v2 || {};

(function(h, $) {
    "use strict";

    var c                      = window.CQ.CoreComponentsIT.commons;
    var image                  = window.CQ.CoreComponentsIT.Image.v2;
    var testImagePath          = "/content/dam/core-components/core-comp-test-image.jpg";
    var altText                = "Return to Arkham";
    var captionText            = "The Last Guardian";
    var originalDamTitle       = "Beach house";
    var originalDamDescription = "House on a beach with blue sky";

    image.tcDragImage = function() {
        return new h.TestCase("Drag Asset")
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execFct(function(opts, done) {
                c.openSidePanel(done);
            })
            .cui.dragdrop('coral-card.cq-draggable[data-path="' + testImagePath + '"]', 'coral-fileupload[name="./file"')
            .execTestCase(c.closeSidePanel);
    };

    image.tcAddImage = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Add an Image", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(image.tcDragImage())
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function() {
                return h.find('.cmp-image__image[src*="' + h.param("testPagePath")() +
                    '/_jcr_content/root/responsivegrid/image.coreimg."][alt="' + originalDamDescription + '"][title="' + originalDamTitle +
                    '"]',
                "#ContentFrame").size() === 1;
            });
    };

    /**
     * Test: set Alt Text and Title
     */
    image.tcAddAltTextAndTitle = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Alt and Title Text", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(image.tcDragImage())
            .click('coral-tab-label:contains("Metadata")')
            .wait(500)
            .click('input[type="checkbox"][name="./altValueFromDAM"]')
            .wait(200)
            .click('input[type="checkbox"][name="./titleValueFromDAM"]')
            .wait(200)
            .fillInput("input[name='./alt']", altText)
            .fillInput("input[name='./jcr:title']", captionText)
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function() {
                return h.find('.cmp-image__image[src*="' + h.param("testPagePath")() +
                    '/_jcr_content/root/responsivegrid/image.coreimg."][alt="' + altText + '"][title="' + captionText + '"]',
                "#ContentFrame").size() === 1;
            });
    };

    image.tcDisableCaptionAsPopup = function(titleSelector, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Disable Caption as Popup", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(image.tcDragImage())
            .click('coral-tab-label:contains("Metadata")')
            .wait(500)
            .click('input[name="./displayPopupTitle"')
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function() {
                return h.find('.cmp-image__image[src*="' + h.param("testPagePath")() +
                    '/_jcr_content/root/responsivegrid/image.coreimg."][alt="' + originalDamDescription + '"]', "#ContentFrame").size() === 1 &&
                    h.find(titleSelector + ':contains("' + originalDamTitle + '")', "#ContentFrame").size() === 1;
            });
    };

    image.tcSetImageAsDecorative = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Image as decorative", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest }
        )
            .execTestCase(image.tcDragImage())
            .click('coral-tab-label:contains("Metadata")')
            .wait(500)
            .simulate('foundation-autocomplete[name="./linkURL"] input[type!="hidden"]', "key-sequence", { sequence: c.rootPage + "{enter}" })
            .wait(500)
            .click('input[name="./isDecorative"')
            .wait(500)
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                return h.find(".cmp-image__image").attr("alt") === "" && h.find(".cmp-image__link").size() === 0;
            });
    };

}(hobs, jQuery));
