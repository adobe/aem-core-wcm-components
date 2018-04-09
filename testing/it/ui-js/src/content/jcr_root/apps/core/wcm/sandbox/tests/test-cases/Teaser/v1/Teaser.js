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

;(function (h, $) {
/* global hobs, jQuery */
    "use strict";

    window.CQ.CoreComponentsIT.Teaser.v1 = window.CQ.CoreComponentsIT.Teaser.v1 || {};
    var c                                = window.CQ.CoreComponentsIT.commons;
    var teaser                           = window.CQ.CoreComponentsIT.Teaser.v1;
    var testImagePath                    = "/content/dam/core-components/core-comp-test-image.jpg";
    var linkText                         = "Teaser Page";
    var title                            = "Teaser Title";
    var description                      = "Teaser Description";

    teaser.tcExecuteBeforeTest = function(tcExecuteBeforeTest, teaserRT, pageRT) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        }).execFct(function(opts, done) {
            c.createPage(c.template, c.rootPage, "teaser-page", "teaser_page", done, pageRT);
        })

        // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(teaserRT, c.proxyPath_sandbox, "proxyPath", done);
            })

        // create a proxy component for an image
            .execFct(function(opts, done) {
                c.createProxyComponent(c.rtImage_v2, c.proxyPath_sandbox, "imageProxyPath", done);
            })

            .execFct(function(opts, done) {
            // we need to set property for image rendering delegation from  teaser proxy to image proxy
                var data = {};
                data.imageDelegate = h.param("imageProxyPath")(opts);
                c.editNodeProperties(h.param("proxyPath")(opts), data, done);
            })

            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param("teaser_page")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%teaser_page%.html");
    };

    teaser.tcExecuteAfterTest = function(tcExecuteAfterTest) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        }).execFct(function(opts, done) {
            c.deletePage(h.param("teaser_page")(opts), done);
        })

        // delete the test proxies we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("proxyPath")(opts), done);
            })

            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("imageProxyPath")(opts), done);
            });
    };

    teaser.testFullyConfiguredTeaser = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Fully configured Teaser", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execFct(function(opts, done) {
                c.openSidePanel(done);
            })
            // drag'n'drop the test image
            .cui.dragdrop(selectors.editDialog.assetDrag(testImagePath), selectors.editDialog.assetDrop)
            .fillInput(selectors.editDialog.linkURL, "%teaser_page%")
            .fillInput(selectors.editDialog.linkText, linkText)
            .fillInput(selectors.editDialog.title, title)
            .fillInput(selectors.editDialog.description, description)
            .execTestCase(c.tcSaveConfigureDialog)
            .assert.isTrue(function() {
                return h.find(selectors.component.image + ' img[src*="' + h.param("teaser_page")() +
                    '/_jcr_content/root/responsivegrid/teaser."]', "#ContentFrame").size() === 1;
            })
            .assert.isTrue(function() {
                return h.find(selectors.component.title, "#ContentFrame").text() === title;
            })
            .assert.isTrue(function() {
                return h.find(selectors.component.description, "#ContentFrame").text() === description;
            })
            .assert.isTrue(function() {
                var $link = h.find("a" + selectors.component.link + '[href$="' + h.param("teaser_page")() + '.html"]', "#ContentFrame");
                return $link && $link.size() === 1 && $link.text() === linkText;
            });

    };

}(hobs, jQuery));
