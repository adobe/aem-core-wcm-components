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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    window.CQ.CoreComponentsIT.LanguageNavigation.v1 = window.CQ.CoreComponentsIT.LanguageNavigation.v1 || {};

    var c                  = window.CQ.CoreComponentsIT.commons;
    var languageNavigation = window.CQ.CoreComponentsIT.LanguageNavigation.v1;

    var selectors = {
        component: {
            self: ".cmp-languagenavigation",
            item: {
                self: ".cmp-languagenavigation__item",
                active: ".cmp-languagenavigation__item--active",
                level0: ".cmp-languagenavigation__item--level-0",
                level1: ".cmp-languagenavigation__item--level-1",
                link: {
                    self: ".cmp-languagenavigation__item-link"
                }
            },
            placeholder: ".cq-placeholder"
        },
        editDialog: {
            self: ".cq-Dialog",
            navigationRoot: '.cq-Dialog foundation-autocomplete[name="./navigationRoot"]',
            structureDepth: '.cq-Dialog input[name="./structureDepth"]'
        }
    };

    /**
     * Before Test Case
     */
    languageNavigation.tcExecuteBeforeTest = function(tcExecuteBeforeTest, languageNavigationRT, pageRT) {
        return new h.TestCase("Create Sample Content", {
            execBefore: tcExecuteBeforeTest })

            // site root
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "site_root", "site_root", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("site_root")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "Site Root"
                    }
                });
            })
            // 1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("site_root")(), "LOCALE_1", "LOCALE_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 1"
                    }
                });
            })
            // 1.1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_1")(), "LOCALE_3", "LOCALE_3_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_3_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 3 1"
                    }
                });
            })
            // 1.2
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_1")(), "LOCALE_4", "LOCALE_4", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_4")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 4",
                        "./jcr:content/sling:vanityPath": "/LOCALE_4_vanity"
                    }
                });
            })
            // 1.1.1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_3_1")(), "about", "about_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("about_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/jcr:title": "About Us"
                    }
                });
            })
            // 2
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("site_root")(), "LOCALE_2", "LOCALE_2", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_2")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 2"
                    }
                });
            })
            // 2.1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_2")(), "LOCALE_3", "LOCALE_3_2", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_3_2")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 3 2"
                    }
                });
            })
            // 2.2
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_2")(), "LOCALE_5", "LOCALE_5", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("LOCALE_5")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "LOCALE 5"
                    }
                });
            })
            // 2.2.1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("LOCALE_3_2")(), "about", "about_2", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("about_2")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "About Us"
                    }
                });
            })
            // 3
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("site_root")(), "hideInNav", "hideInNav", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("hideInNav")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/hideInNav": true
                    }
                });
            })
            // no structure
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "no_structure", "no_structure", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("no_structure")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "No Structure"
                    }
                });
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(languageNavigationRT, c.proxyPath, "compPath", done);
            })

            // add component
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPath")(opts), h.param("about_1")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%about_1%.html");
    };

    /**
     * After Test Case
     */
    languageNavigation.tcExecuteAfterTest = function() {
        return new h.TestCase("Clean up after test", {
            execAfter: c.tcExecuteAfterTest
        }).execFct(function(opts, done) {
            c.deletePage(h.param("site_root")(opts), done);
        })

        // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

    /**
     * Test: Default configuration (depth 1)
     */
    languageNavigation.testDefaultConfiguration = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Default configuration", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput(selectors.editDialog.navigationRoot, "%site_root%")
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + selectors.component.item.active + ':contains("LOCALE 1")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + ':contains("LOCALE 2")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + ':contains("hideInNav")', false)
            .assert.exist(selectors.component.item.self + ':contains("LOCALE 3 1")', false);
    };

    /**
     * Test: Change Structure Depth (depth 2)
     */
    languageNavigation.testChangeStructureDepth = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Change Structure Depth", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput(selectors.editDialog.navigationRoot, "%site_root%")
            .fillInput(selectors.editDialog.structureDepth, "2")
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + selectors.component.item.active + ':contains("LOCALE 1")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + ':contains("LOCALE 2")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + ':contains("hideInNav")', false)
            .assert.exist(selectors.component.item.self + selectors.component.item.level0 + " " + selectors.component.item.link, false)
            .assert.exist(selectors.component.item.self + selectors.component.item.level1 + selectors.component.item.active + ':contains("LOCALE 3 1")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level1 + ':contains("LOCALE 3 2")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level1 + ':contains("LOCALE 4")')
            .assert.exist(selectors.component.item.self + selectors.component.item.level1 + ':contains("LOCALE 5")')
            .assert.exist(selectors.component.item.self + ':contains("About Us")', false);
    };

    /**
     * Test: Change Structure Depth to zero - invalid input, no dialog submission
     */
    languageNavigation.testSetStructureDepthZero = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Structure Depth zero", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput(selectors.editDialog.navigationRoot, "%site_root%")
            .fillInput(selectors.editDialog.structureDepth, "0")
            .execTestCase(c.tcSaveConfigureDialog)
            .assert.visible(selectors.editDialog.self);
    };

    /**
     * Test: Navigation Root with no structure - no items, placeholder displayed
     */
    languageNavigation.testNavigationRootNoStructure = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Navigation Root with no structure", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput(selectors.editDialog.navigationRoot, "%no_structure%")
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.component.item.self, false)
            .assert.exist(selectors.component.placeholder + selectors.component.self);
    };

}(hobs, jQuery));
