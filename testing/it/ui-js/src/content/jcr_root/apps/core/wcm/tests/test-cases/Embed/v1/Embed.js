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
/* globals hobs,jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    window.CQ.CoreComponentsIT.Embed.v1 = window.CQ.CoreComponentsIT.Embed.v1 || {};
    var c = window.CQ.CoreComponentsIT.commons;
    var embed = window.CQ.CoreComponentsIT.Embed.v1;

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create the policy
     * 3. assign the policy
     * 4. create proxy component
     * 5. add the component to the page
     * 6. open the new page in the editor
     */
    embed.tcExecuteBeforeTest = function(tcExecuteBeforeTest, embedRT, pageRT) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_" + Date.now(), "testPagePath", done, pageRT);
            })

            // 2.
            .execFct(function(opts, done) {
                var data = {
                    "jcr:title": "New Policy",
                    "sling:resourceType": "wcm/core/components/policy/policy",
                    "allowedEmbeddables": "core/wcm/components/embed/v1/embed/embeddable/youtube"
                };

                c.createPolicy("/embed" + "/new_policy", data, "policyPath", done, c.policyPath);
            })

            // 3.
            .execFct(function(opts, done) {
                var data = {
                    "cq:policy": "core-component/components/embed/new_policy",
                    "sling:resourceType": "wcm/core/components/policies/mappings"
                };

                c.assignPolicy("/embed", data, done, c.policyAssignmentPath);
            })

            // 4.
            .execFct(function(opts, done) {
                c.createProxyComponent(embedRT, c.proxyPath, "proxyPath", done);
            })

            // 5.
            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param("testPagePath")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            // 6.
            .navigateTo("/editor.html%testPagePath%.html");
    };

    /**
     * After Test Case
     *
     * 1. delete the test page
     * 2. delete the proxy component
     * 3. delete the policy
     * 4. delete the policy assignment
     */
    embed.tcExecuteAfterTest = function(tcExecuteAfterTest) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.deletePage(h.param("testPagePath")(opts), done);
            })

            // 2.
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("proxyPath")(opts), done);
            })

            // 3.
            .execFct(function(opts, done) {
                c.deletePolicy("/embed", done, c.policyPath);
            })

            // 4.
            .execFct(function(opts, done) {
                c.deletePolicyAssignment("/embed", done, c.policyAssignmentPath);
            });
    };

    /**
     * Verify URL : verify a given URL can be processed
     *
     * 1. open the edit dialog
     * 2. enter the URL in the URL field
     * 3. verify the status message
     * 4. save the edit dialog
     * 5. verify the embed object on the page
     */
    embed.tcVerifyUrl = function(url, urlProcessor, selectors) {
        return new h.TestCase("Verify a given URL can be processed")
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .fillInput(selectors.editDialog.properties.urlField, url)
            .wait(1000)

            // 3.
            .assert.visible(selectors.editDialog.properties.urlStatus)
            .assert.exists(selectors.editDialog.properties.urlStatus + ":contains(" + urlProcessor.name + ")")

            // 4.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 5.
            .config.changeContext(c.getContentFrame)
            .assert.exists(urlProcessor.selector)
            .config.resetContext();
    };

    /**
     * URL Validation
     *
     * 1. open the edit dialog
     * 2. verify no URL status is currently showing
     * 3. enter a valid url
     * 4. verify the status message
     * 5. save the edit dialog
     * 6. open the edit dialog
     * 7. verify the status message
     * 8. enter an invalid URL
     * 9. verify field is marked invalid and the status message is not shown
     * 10. enter a malformed URL
     * 11. verify field is marked invalid and the status message is not shown
     * 12. enter an empty URL
     * 13. verify field is marked invalid and the status message is not shown
     * 14. verify that the properties tab is marked invalid
     * 15. switch type to embeddable
     * 16. verify that the properties tab is no longer marked invalid
     * 17. switch type to URL
     * 18. verify field is marked invalid and the status message is not shown
     */
    embed.tcUrlValidation = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlValidation, selectors) {
        return new h.TestCase("URL Validation", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.exists(selectors.editDialog.properties.urlStatus + ":contains('')")

            // 3.
            .fillInput(selectors.editDialog.properties.urlField, urlValidation.valid)
            .wait(200)

            // 4.
            .assert.visible(selectors.editDialog.properties.urlStatus)
            .assert.exists(selectors.editDialog.properties.urlStatus + ":contains(YouTube)")

            // 5.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 6.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .wait(200)

            // 7.
            .assert.visible(selectors.editDialog.properties.urlStatus)
            .assert.exists(selectors.editDialog.properties.urlStatus + ":contains(YouTube)")

            // 8.
            .fillInput(selectors.editDialog.properties.urlField, urlValidation.invalid)
            .wait(200)

            // 9.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)
            .assert.exists(selectors.editDialog.properties.urlField + ".is-invalid")

            // 10.
            .fillInput(selectors.editDialog.properties.urlField, urlValidation.malformed)
            .wait(200)

            // 11.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)
            .assert.exists(selectors.editDialog.properties.urlField + ".is-invalid")

            // 12.
            .fillInput(selectors.editDialog.properties.urlField, urlValidation.blank)
            .wait(200)

            // 13.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)
            .assert.exists(selectors.editDialog.properties.urlField + ".is-invalid")

            // 14.
            .assert.exists(selectors.editDialog.properties.self + ".is-invalid")

            // 15.
            .click(selectors.editDialog.properties.typeRadio + "[value='embeddable']")
            .wait(400)

            // 16.
            .assert.exists(selectors.editDialog.properties.self + ".is-invalid", false)

            // 17.
            .click(selectors.editDialog.properties.typeRadio + "[value='url']")

            // 18.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)
            .assert.exists(selectors.editDialog.properties.urlField + ".is-invalid");
    };

    /**
     * URL : oEmbed : Flickr
     *
     * 1. verify all test URLs
     */
    embed.tcUrlOEmbedFlickr = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : Flickr", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : SoundCloud
     *
     * 1. verify all test URLs
     */
    embed.tcUrlOEmbedSoundcloud = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : SoundCloud", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : Twitter
     *
     * 1. verify all test URLs
     */
    embed.tcUrlOEmbedTwitter = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : Twitter", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : YouTube
     *
     * 1. verify all test URLs
     */
    embed.tcUrlOEmbedYoutube = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : YouTube", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors))
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[1], urlProcessor, selectors));
    };

    /**
     * Embeddable : YouTube
     *
     * 1. open the edit dialog
     * 2. switch type to embeddable
     * 3. save the edit dialog, verifying it's possible to submit when no embeddable is selected
     * 4. open the edit dialog
     * 5. switch type to embeddable
     * 6. select the YouTube embeddable
     * 7. save the edit dialog
     * 8. verify the dialog did not submit, as the Video ID field is required
     * 9. add a Video ID
     * 10. save the edit dialog
     * 11. verify the YouTube video on the page
     */
    embed.tcEmbeddableYoutube = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Embeddable : YouTube", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .click(selectors.editDialog.properties.typeRadio + "[value='embeddable']")

            // 3.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 4.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 5.
            .click(selectors.editDialog.properties.typeRadio + "[value='embeddable']")

            // 6.
            .click(selectors.editDialog.properties.embeddableField.button)
            .assert.visible(selectors.editDialog.properties.embeddableField.selectList)
            .click(selectors.editDialog.properties.embeddableField.items.youtube)

            // 7.
            .click(c.selSaveConfDialogButton, { expectNav: false })
            .wait(200)

            // 8.
            .assert.visible(selectors.editDialog.self)

            // 9.
            .fillInput(selectors.editDialog.properties.embeddables.youtube.videoId, "5vOOa3-fifY")
            .wait(200)

            // 10.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 11.
            .config.changeContext(c.getContentFrame)
            .assert.exists(selectors.embed.youtube)
            .config.resetContext();
    };

    /**
     * HTML
     *
     * 1. open the edit dialog
     * 2. switch type to html
     * 3. save the edit dialog
     * 4. verify the dialog did not submit, as the HTML field is required
     * 5. add an HTML embed code
     * 6. save the edit dialog
     * 7. verify the HTML embed code is present on the page
     */
    embed.tcHtml = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("HTML", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .click(selectors.editDialog.properties.typeRadio + "[value='html']")

            // 3.
            .click(c.selSaveConfDialogButton, { expectNav: false })
            .wait(200)

            // 4.
            .assert.visible(selectors.editDialog.self)

            // 5.
            .fillInput(selectors.editDialog.properties.htmlField, "<div id='CmpEmbedHtml'>HTML</div>")
            .wait(200)

            // 6.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 7.
            .config.changeContext(c.getContentFrame)
            .assert.exists("#CmpEmbedHtml")
            .config.resetContext();
    };

})(hobs, jQuery);
