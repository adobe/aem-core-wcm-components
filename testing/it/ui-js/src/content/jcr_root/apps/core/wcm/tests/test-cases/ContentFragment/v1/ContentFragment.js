/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var PN_VARIATION_NAME                               = "./variationName";
    var PN_ELEMENT_NAMES                                = "./elementNames";
    var ELEMENT_NAMES                                   = {
        DESCRIPTION: "component-description",
        LATEST_VERSION: "component-latest-version",
        TITLE: "component-title",
        TYPE: "component-type"
    };

    window.CQ.CoreComponentsIT.ContentFragment.v1       = window.CQ.CoreComponentsIT.ContentFragment.v1 || {};
    var c                                               = window.CQ.CoreComponentsIT.commons;
    var contentfragment                                 = window.CQ.CoreComponentsIT.ContentFragment.v1;
    var pageName                                        = "contentfragment-page";
    var pageVar                                         = "contentfragment_page";
    var pageDescription                                 = "contentfragment page description";
    var fragmentPath1                                   = "/content/dam/core-components/contentfragments-tests/simple-fragment";
    var fragmentPath2                                   = "/content/dam/core-components/contentfragments-tests/image-fragment";
    var variationName1                                  = "short";

    contentfragment.tcExecuteBeforeTest = function(tcExecuteBeforeTest, contentfragmentRT, pageRT, clientlibs) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        })
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, pageName, pageVar, done, pageRT, pageDescription);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(contentfragmentRT, c.proxyPath, "proxyPath", done);
            })

            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param(pageVar)(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%" + pageVar + "%.html");
    };

    contentfragment.tcExecuteAfterTest = function(tcExecuteAfterTest, policyPath, policyAssignmentPath) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        })
            // delete the test proxies we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("proxyPath")(opts), done);
            })

            .execFct(function(opts, done) {
                c.deletePage(h.param(pageVar)(opts), done);
            });
    };

    /**
     * Set the fragment path
     */
    contentfragment.tcSetFragmentPath = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set the fragment path", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
        // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the fragment path
            .execTestCase(c.tcSelectInTags(selectors.editDialog.fragmentPath, fragmentPath1))
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                var $contentfragmenttitle = h.find(selectors.contentfragment.title);
                var $contentfragmentelementtitles = h.find(selectors.contentfragment.elements.element.title);
                var $contentfragmentelementvalues = h.find(selectors.contentfragment.elements.element.value);
                return $contentfragmenttitle.size() === 1 &&
                    $contentfragmenttitle[0].innerHTML === "Simple Fragment" &&
                    $contentfragmentelementtitles.size() === 1 &&
                    $contentfragmentelementtitles[0].innerHTML === "Main" &&
                    $contentfragmentelementvalues.size() === 1 &&
                    $contentfragmentelementvalues.find("h2")[0].innerHTML === "Master variation";
            });
    };

    /**
     * Set the variation name
     */
    contentfragment.tcSetVariationName = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set the variation name", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
        // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the fragment path
            .execTestCase(c.tcSelectInTags(selectors.editDialog.fragmentPath, fragmentPath1))
            .wait(200)
            // set the variation name
            .execTestCase(c.tcUseDialogSelect(PN_VARIATION_NAME, variationName1))
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                var $contentfragmenttitle = h.find(selectors.contentfragment.title);
                var $contentfragmentelementtitles = h.find(selectors.contentfragment.elements.element.title);
                var $contentfragmentelementvalues = h.find(selectors.contentfragment.elements.element.value);
                return $contentfragmenttitle.size() === 1 &&
                    $contentfragmenttitle[0].innerHTML === "Simple Fragment" &&
                    $contentfragmentelementtitles.size() === 1 &&
                    $contentfragmentelementtitles[0].innerHTML === "Main" &&
                    $contentfragmentelementvalues.size() === 1 &&
                    $contentfragmentelementvalues.find("h2")[0].innerHTML === "Short variation";
            });
    };

    /**
     * Set a structured content fragment
     */
    contentfragment.tcSetStructuredContentFragment = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Structured content fragment", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
        // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the fragment path
            .execTestCase(c.tcSelectInTags(selectors.editDialog.fragmentPath, fragmentPath2))
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                var $contentfragmenttitle = h.find(selectors.contentfragment.title);
                var $contentfragmentelementtitles = h.find(selectors.contentfragment.elements.element.title);
                var $contentfragmentelementvalues = h.find(selectors.contentfragment.elements.element.value);
                return $contentfragmenttitle.size() === 1 &&
                    $contentfragmenttitle[0].innerHTML === "Image Fragment" &&
                    $contentfragmentelementtitles.size() === 4 &&
                    $contentfragmentelementtitles[0].innerHTML === "Title" &&
                    $contentfragmentelementvalues[0].innerHTML.trim() === "Image" &&
                    $contentfragmentelementtitles[1].innerHTML === "Description" &&
                    $contentfragmentelementtitles[2].innerHTML === "Latest Version" &&
                    $contentfragmentelementvalues[2].innerHTML.trim() === "2" &&
                    $contentfragmentelementtitles[3].innerHTML === "Type";
            });
    };

    /**
     * Set the element names
     */
    contentfragment.tcSetElementNames = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Set the element names", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
        // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set the fragment path
            .execTestCase(c.tcSelectInTags(selectors.editDialog.fragmentPath, fragmentPath2))

            // add a first element
            .click(selectors.editDialog.elements.addButton)
            .wait(200)
            // select the title element
            .execTestCase(c.tcUseDialogSelect(PN_ELEMENT_NAMES, ELEMENT_NAMES.TITLE))
            // add a second element
            .click(selectors.editDialog.elements.addButton)
            .wait(200)
            // expand the dropdown
            .click(selectors.editDialog.elements.last + " " + selectors.editDialog.elements.select.button)
            // select the type element
            .click(selectors.editDialog.elements.last + " " + selectors.editDialog.elements.select.item + "[value='" + ELEMENT_NAMES.TYPE + "']")

            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            .asserts.isTrue(function() {
                var $contentfragmenttitle = h.find(selectors.contentfragment.title);
                var $contentfragmentelementtitles = h.find(selectors.contentfragment.elements.element.title);
                var $contentfragmentelementvalues = h.find(selectors.contentfragment.elements.element.value);
                return $contentfragmenttitle.size() === 1 &&
                    $contentfragmenttitle[0].innerHTML === "Image Fragment" &&
                    $contentfragmentelementtitles.size() === 2 &&
                    $contentfragmentelementtitles[0].innerHTML === "Title" &&
                    $contentfragmentelementvalues[0].innerHTML.trim() === "Image" &&
                    $contentfragmentelementtitles[1].innerHTML === "Type";
            });
    };

}(hobs, jQuery));
