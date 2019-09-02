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
     * 2. create proxy component
     * 3. add the component to the page
     * 4. open the new page in the editor
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
                c.createProxyComponent(embedRT, c.proxyPath, "compPath", done);
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
                c.deleteProxyComponent(h.param("compPath")(opts), done);
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
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlPinterest = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL Validation", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : Pinterest
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlPinterest = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : Pinterest", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : Facebook Post
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlOEmbedFacebookPost = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : Facebook Post", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors))
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[1], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : Instagram
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlOEmbedInstagram = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : Instagram", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors))
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[1], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : SoundCloud
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlOEmbedSoundcloud = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : SoundCloud", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : Twitter
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlOEmbedTwitter = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : Twitter", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors));
    };

    /**
     * URL : oEmbed : YouTube
     *
     * 1. open the edit dialog
     * 2. verify there's initially no URL status message
     * 3. close the edit dialog
     * 4. verify all test URLs
     */
    embed.tcUrlOEmbedYoutube = function(tcExecuteBeforeTest, tcExecuteAfterTest, urlProcessor, selectors) {
        return new h.TestCase("URL : oEmbed : YouTube", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })
            // 1.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2.
            .assert.visible(selectors.editDialog.properties.urlStatus, false)

            // 3.
            .execTestCase(c.tcCloseConfigureDialog)
            .wait(1000)

            // 4.
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[0], urlProcessor, selectors))
            .execTestCase(embed.tcVerifyUrl(urlProcessor.urls[1], urlProcessor, selectors));
    };

})(hobs, jQuery);
