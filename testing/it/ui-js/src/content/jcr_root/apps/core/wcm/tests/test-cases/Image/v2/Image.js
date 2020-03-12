/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
    var testAssetsPath         = "/content/dam/core-components";
    var testImagePath          = testAssetsPath + "/core-comp-test-image.jpg";
    var altText                = "Return to Arkham";
    var captionText            = "The Last Guardian";
    var originalDamTitle       = "Beach house";
    var originalDamDescription = "House on a beach with blue sky";

    var selectors = {
        elements: {
            self: ".cmp-image",
            image: "[data-cmp-hook-image='image']",
            map: "[data-cmp-hook-image='map']",
            area: "[data-cmp-hook-image='area']"
        }
    };

    image.tcDragImage = function() {
        return new h.TestCase("Drag Asset")
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execFct(function(opts, done) {
                c.openSidePanel(done);
            })
            .fillInput('foundation-autocomplete[name="assetfilter_image_path"] input[is="coral-textfield"]', testAssetsPath)
            .click('foundation-autocomplete[name="assetfilter_image_path"] [is="coral-buttonlist-item"][value="' + testAssetsPath + '"]')
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
            execAfter: tcExecuteAfterTest })
            .execTestCase(image.tcDragImage())
            .click('coral-tab-label:contains("Metadata")')
            .wait(500)
            .execTestCase(c.tcSelectInAutocomplete("[name='./linkURL']", c.rootPage))
            .click('input[name="./isDecorative"')
            .wait(500)
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                return h.find(".cmp-image__image[alt]").size() === 0 && h.find(".cmp-image__link").size() === 0;
            });
    };

    /**
     * Test: Check image map areas are rendered, navigate correctly and are responsively adjusted on window resize
     */
    image.tcCheckMapAreaNavigationAndResponsiveResize = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check map area navigation and responsive resize", {
            execBefore: new hobs.TestCase("Before Test")
                .execTestCase(tcExecuteBeforeTest)

                // persist a test image map with a single map area
                .execFct(function(opts, done) {
                    var data = {};
                    var cmpPath = h.param("cmpPath")();
                    data.imageMap = "[rect(0,0,226,230)\"/content/core-components/core-components-page\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]";
                    data.fileReference = testImagePath;

                    c.editNodeProperties(cmpPath, data, done);
                })

                // refresh the component
                .navigateTo("/editor.html%testPagePath%.html"),
            execAfter: tcExecuteAfterTest }
        )
            // verify the map area is available
            .asserts.isTrue(function() {
                return h.find(selectors.elements.area, "#ContentFrame").size() === 1;
            })

            // switch to the content frame, click the area link and verify navigation
            .config.changeContext(c.getContentFrame)
            .click(selectors.elements.area)
            .asserts.isTrue(function() {
                var pathname = h.context().window.location.pathname;
                return pathname.includes("/content/core-components/core-components-page");
            })

            // navigate back to the test page
            .config.resetContext()
            .navigateTo("/editor.html%testPagePath%.html")
            .config.changeContext(c.getContentFrame)
            .wait(2000)

            // manually resize the test image to a narrow width
            .execFct(function() {
                h.find(selectors.elements.image).width(300);
            })

            // trigger a window resize event
            .execFct(function() {
                h.context().window.dispatchEvent(new Event("resize"));
            })
            .wait(2000)

            // verify that the adjusted coordinates are correct
            .asserts.isTrue(function() {
                var area = h.find(selectors.elements.area)[0];
                var coords = area.coords.split(",");
                var expectedCoords = [0, 0, 58, 38];
                var passed = true;

                for (var i = 0; i < coords.length; i++) {
                    if (parseInt(coords[i]) !== expectedCoords[i]) {
                        passed = false;
                        break;
                    }
                }

                return passed;
            });
    };

}(hobs, jQuery));
