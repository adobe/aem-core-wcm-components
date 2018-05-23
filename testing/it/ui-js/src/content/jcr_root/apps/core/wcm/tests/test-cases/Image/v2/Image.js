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

    /**
     * Test: Check image map areas are rendered, navigate correctly and are responsively adjusted on window resize
     */
    image.tcCheckMapAreaNavigationAndResponsiveResize = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check map area navigation and responsive resize", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest }
        )

            // create and assign a content policy to enable lazy loading; which initializes the frontend handling
            .execFct(function(opts, done) {
                var componentPath = h.param("cmpPath")();
                h.param("cmpNodeName", componentPath.substr(componentPath.lastIndexOf("/")));
                var data = {};
                data["jcr:title"] = "New Policy";
                data["sling:resourceType"] = "wcm/core/components/policy/policy";
                data["disableLazyLoading"] = "false";

                c.createPolicy(h.param("cmpNodeName")() + "/new_policy", data, "policyPath", done, c.policyPath);
            })

            .execFct(function(opts, done) {
                var data = {};
                data["cq:policy"] = "core-component/components" + h.param("cmpNodeName")() + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(h.param("cmpNodeName")(), data, done, c.policyAssignmentPath);
            })

            // set up an example image and persist a test image map with a single map area
            .execTestCase(image.tcDragImage())
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(500)
            .execFct(function(opts, done) {
                var data = {};
                data.imageMap = "[rect(0,0,226,230)\"/content/we-retail/us/en\"|\"\"|\"Alt Text\"|(0.0000,0.0000,0.1948,0.2295)]";

                c.editNodeProperties(h.param("cmpPath")(), data, done);
            })
            .wait(500)

            // refresh the component rendering
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execTestCase(c.tcSaveConfigureDialog)

            // verify the map area is available
            .asserts.isTrue(function() {
                return h.find(selectors.elements.map + '[name*="' + h.param("testPagePath")() +
                    '/jcr:content/root/responsivegrid/image"] ' + selectors.elements.area, "#ContentFrame").size() === 1;
            })
            .wait(500)

            // switch to the content frame, click the area link and verify navigation
            .config.changeContext(c.getContentFrame)
            .click(selectors.elements.area + ":first-child")
            .asserts.isTrue(function() {
                var pathname = h.context().window.location.pathname;
                return pathname.includes("/content/we-retail/us/en");
            })

            // navigate back to the test page
            .config.resetContext()
            .navigateTo("/editor.html%testPagePath%.html")
            .config.changeContext(c.getContentFrame)

            // manually resize the test image to a narrow width and trigger a window resize
            .execFct(function() {
                h.find(selectors.elements.image).width(100);
                $(h.context().window).trigger("resize");
            })
            .wait(500)

            // verify that the applied coordinates are correct in relation to the new image size
            .asserts.isTrue(function() {
                var area = h.find(selectors.elements.area)[0];
                var image = h.find(selectors.elements.image)[0];
                var coords = area.coords.split(",");
                var relativeCoords = area.dataset.cmpRelcoords.split(",");
                var width = image.width;
                var height = image.height;
                var passed = true;

                var expectedCoords = [
                    Math.floor((relativeCoords[0] * width)),
                    Math.floor((relativeCoords[1] * height)),
                    Math.floor((relativeCoords[2] * width)),
                    Math.floor((relativeCoords[3] * height))
                ];

                for (var i = 0; i < coords.length; i++) {
                    if (parseInt(coords[i]) !== expectedCoords[i]) {
                        passed = false;
                        break;
                    }
                }

                return passed;
            })

            // cleanup
            .execFct(function(opts, done) {
                c.deletePolicy(h.param("cmpNodeName")(), done, c.policyPath);
            })

            .execFct(function(opts, done) {
                c.deletePolicyAssignment(h.param("cmpNodeName")(), done, c.policyAssignmentPath);
            });
    };

}(hobs, jQuery));
