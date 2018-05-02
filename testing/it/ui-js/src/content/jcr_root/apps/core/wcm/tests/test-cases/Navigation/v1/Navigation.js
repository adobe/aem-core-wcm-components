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

    window.CQ.CoreComponentsIT.Navigation.v1 = window.CQ.CoreComponentsIT.Navigation.v1 || {};
    var c                                    = window.CQ.CoreComponentsIT.commons;
    var navigation                           = window.CQ.CoreComponentsIT.Navigation.v1;

    navigation.tcExecuteBeforeTest = function(tcExecuteBeforeTest, navigationRT, pageRT) {
        return new h.TestCase("Create Sample Content", {
            execBefore: tcExecuteBeforeTest })

            // level 1
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, "page_1", "page_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("page_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "Page 1"
                    }
                });
            })
            // level 2
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("page_1")(), "page_1_1", "page_1_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("page_1_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "Page 1.1",
                        "./jcr:content/sling:vanityPath": "/page_1_1_vanity"
                    }
                });
            })
            // level 2 1
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("page_1_1")(), "page_1_1_1", "page_1_1_1", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("page_1_1_1")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "Page 1.1.1"
                    }
                });
            })
            // level 2 2
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("page_1_1")(), "page_1_1_2", "page_1_1_2", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("page_1_1_2")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/hideInNav": true
                    }
                });
            })
            // level 2 3
            .execFct(function(opts, done) {
                c.createPage(c.template, h.param("page_1_1")(), "page_1_1_3", "page_1_1_3", done, pageRT);
            })
            .execFct(function(opts, done) {
                $.ajax({
                    url: h.param("page_1_1_3")(),
                    method: "POST",
                    complete: done,
                    data: {
                        "_charset_": "UTF-8",
                        "./jcr:content/navTitle": "Page 1.1.3"
                    }
                });
            })
            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(navigationRT, c.proxyPath, "compPath", done);
            })

            // add component
            .execFct(function(opts, done) {
                c.addComponent(h.param("compPath")(opts), h.param("page_1_1")(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%page_1_1%.html");
    };

    navigation.tcExecuteAfterTest = function(tcExecuteAfterTest) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest })

            .execFct(function(opts, done) {
                c.deletePage(h.param("page_1")(opts), done);
            })

            // delete the test page we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("compPath")(opts), done);
            });
    };

    navigation.testDefaultConfiguration = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Test default configuration", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .assert.isTrue(function() {
                return h.find('coral-checkbox[name="./collectAllPages"]').prop("checked") === true;
            })
            .assert.visible('coral-numberinput[name="./structureDepth"]', false)
            .fillInput('foundation-autocomplete[name="./navigationRoot"]', "%page_1%")
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .assert.isTrue(function() {
                return h.find(".cmp-navigation__item").size() === 3;
            })
            .assert.exist(
                '.cmp-navigation__item.cmp-navigation__item--level-0.cmp-navigation__item--active:contains("Page 1.1")')
            .assert.exist('a.cmp-navigation__item-link[href$="/page_1_1_vanity"]')
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.1")')
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.2")', false)
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.3")');
    };

    navigation.testIncludeNavigationRoot = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Include Navigation Root", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .assert.isTrue(function() {
                return h.find('coral-checkbox[name="./collectAllPages"]').prop("checked") === true;
            })
            .assert.visible('coral-numberinput[name="./structureDepth"]', false)
            .fillInput('foundation-autocomplete[name="./navigationRoot"]', "%page_1%")
            // uncheck the skip root option
            .click('input[name="./skipNavigationRoot"]')
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            .config.changeContext(c.getContentFrame)
            .assert.isTrue(function() {
                return h.find(".cmp-navigation__item").size() === 4;
            })
            .assert.exist(
                '.cmp-navigation__item.cmp-navigation__item--level-0.cmp-navigation__item--active:contains("Page 1")')
            .assert.exist(
                '.cmp-navigation__item.cmp-navigation__item--level-1.cmp-navigation__item--active:contains("Page 1.1")')
            .assert.exist('a.cmp-navigation__item-link[href$="/page_1_1_vanity"]')
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-2:contains("Page 1.1.1")')
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-2:contains("Page 1.1.2")', false)
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-2:contains("Page 1.1.3")');
    };

    navigation.testChangeStructureDepthLevel = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Change max depth level", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .fillInput('foundation-autocomplete[name="./navigationRoot"]', "%page_1%")
            // uncheck
            .click('input[name="./collectAllPages"]', { after: 1000 })
            .assert.visible('coral-numberinput[name="./structureDepth"]', true)
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .assert.isTrue(function() {
                return h.find(".cmp-navigation__item").size() === 1;
            })
            .assert.exist(
                '.cmp-navigation__item.cmp-navigation__item--level-0.cmp-navigation__item--active:contains("Page 1.1")')
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.1")', false)
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.2")', false)
            .assert.exist('.cmp-navigation__item.cmp-navigation__item--level-1:contains("Page 1.1.3")', false);
    };

}(hobs, jQuery));
