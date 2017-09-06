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
;(function (h, $) {
    'use strict';

    window.CQ.CoreComponentsIT.Search.v1 = window.CQ.CoreComponentsIT.Search.v1 || {};

    var c                  = window.CQ.CoreComponentsIT.commons;
    var search             = window.CQ.CoreComponentsIT.Search.v1;

    var selectors = {
        component: {
            self    : '.cmp-search',
            input   : '.cmp-search__input',
            clear   : '.cmp-search__clear',
            results : '.cmp-search__results',
            item: {
                self  : '.cmp-search__item',
                mark  : '.cmp-search__item-mark'
            }
        },
        editDialog: {
            self       : '.cq-Dialog',
            startLevel : '.cq-Dialog coral-numberinput[name="./startLevel"]'
        }
    };

    var pollQuery = function(done, path, searchTerm, expected) {

        var maxRetries = 60,
            timeout = 2000,
            retries = 0,
            match = false;

        var poll = function() {
            $.ajax({
                url: '/bin/querybuilder.json',
                method: 'GET',
                data: {
                    path: path,
                    "p.limit": 100,
                    fulltext: searchTerm,
                    type: "cq:Page"
                }
            }).done(function(data) {
                if(data.hits && data.hits.length > 1) {
                    data.hits.forEach(function(item) {
                        if(item.path === expected) {
                            match = true;
                        }
                    });
                }
                if(match) {
                    done(true);
                } else {
                    if(retries++ === maxRetries) {
                        done(false, "Not able to get query result for " + expected);
                        return;
                    }
                    setTimeout(poll, timeout);
                }
            }).fail(function (jqXHR, textStatus, errorThrown) {
                    if (retries++ === maxRetries) {
                        done(false, "pollQuery failed! " + textStatus + "," + errorThrown);
                        return;
                    }
                    setTimeout(poll, timeout);
                });
        };

        poll();
    };

    search.tcExecuteBeforeTest = function (tcExecuteBeforeTest, searchRT, pageRT) {
        return new h.TestCase('Create Sample Content', {
            execBefore: tcExecuteBeforeTest
        })
            // level 1
            .execFct(function (opts, done) {
                c.createPage(c.template, c.rootPage, 'page_1_' + Date.now(), 'page_1', done, pageRT);
            })
            .execFct(function (opts, done) {
                $.ajax({
                    url     : h.param('page_1')(),
                    method  : 'POST',
                    complete: done,
                    dataType: 'json',
                    data    : {
                        '_charset_'             : 'UTF-8',
                        './jcr:content/jcr:title': 'Page 1'
                    }
                })
            })
            // level 2
            .execFct(function (opts, done) {
                c.createPage(c.template, h.param('page_1')(), 'page_1_1_' + Date.now(), 'page_1_1', done, pageRT);
            })
            .execFct(function (opts, done) {
                $.ajax({
                    url     : h.param('page_1_1')(),
                    method  : 'POST',
                    complete: done,
                    data    : {
                        '_charset_'             : 'UTF-8',
                        './jcr:content/jcr:title': 'Page 1.1'
                    }
                })
            })
            // level 2 1
            .execFct(function (opts, done) {
                c.createPage(c.template, h.param('page_1_1')(), 'page_1_1_1_' + Date.now(), 'page_1_1_1', done, pageRT);
            })
            .execFct(function (opts, done) {
                $.ajax({
                    url     : h.param('page_1_1_1')(),
                    method  : 'POST',
                    complete: done,
                    data    : {
                        '_charset_'             : 'UTF-8',
                        './jcr:content/jcr:title': 'Page 1.1.1'
                    }
                })
            })
            // level 2 2
            .execFct(function (opts, done) {
                c.createPage(c.template, h.param('page_1_1')(), 'page_1_1_2_' + Date.now(), 'page_1_1_2', done, pageRT);
            })
            .execFct(function (opts, done) {
                $.ajax({
                    url     : h.param('page_1_1_2')(),
                    method  : 'POST',
                    complete: done,
                    data    : {
                        '_charset_'              : 'UTF-8',
                        './jcr:content/jcr:title': 'Page 1.1.2'
                    }
                })
            })
            // level 2 3
            .execFct(function (opts, done) {
                c.createPage(c.template, h.param('page_1_1')(), 'page_1_1_3_' + Date.now(), 'page_1_1_3', done, pageRT);
            })
            .execFct(function (opts, done) {
                $.ajax({
                    url     : h.param('page_1_1_3')(),
                    method  : 'POST',
                    complete: done,
                    data    : {
                        '_charset_'             : 'UTF-8',
                        './jcr:content/jcr:title': 'Page 1.1.3'
                    }
                })
            })
            // add component
            .execFct(function (opts, done) {
                c.addComponent(searchRT, h.param('page_1_1')(opts) + c.relParentCompPath, 'cmpPath', done);
            })
            .navigateTo('/editor.html%page_1_1%.html');
    };

    /**
     * After Test Case
     */
    search.tcExecuteAfterTest = function() {
        return new TestCase('Clean up after test', {
            execAfter: c.tcExecuteAfterTest
        }).execFct(function (opts, done) {
            c.deletePage(h.param('page_1')(opts), done);
        });
    };

    /**
     * Test: Default configuration (start level 2)
     */
    search.testDefaultConfiguration = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase('Default configuration', {
            execBefore: tcExecuteBeforeTest,
            execAfter : tcExecuteAfterTest
        })
            .config.changeContext(c.getContentFrame)
            .execFct(function(opts, done) {
                pollQuery(done, c.rootPage, 'Page', h.param('page_1')());
            })
            .fillInput(selectors.component.input, 'Page', {delay: 1000})
            .assert.visible(selectors.component.results)
            .assert.exist(selectors.component.item.self + '[href="' + h.config.context_path + '%page_1%.html"]');
    };

    /**
     * Test: Change start level (start level 4)
     */
    search.testChangeStartLevel = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase('Change Start Level', {
            execBefore: tcExecuteBeforeTest,
            execAfter : tcExecuteAfterTest
        })
            .execTestCase(c.tcOpenConfigureDialog('cmpPath'))
            .fillInput(selectors.editDialog.startLevel, '4')
            .execTestCase(c.tcSaveConfigureDialog)
            .config.changeContext(c.getContentFrame)
            .fillInput(selectors.component.input, 'Page', {delay: 1000})
            .assert.visible(selectors.component.item.self + '[href="' + h.config.context_path + '%page_1%.html"]', false);
    };

    /**
     * Test: Clear button
     */
    search.testClearButton = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase('Clear Button', {
            execBefore: tcExecuteBeforeTest,
            execAfter : tcExecuteAfterTest
        })
            .config.changeContext(c.getContentFrame)
            .assert.visible(selectors.component.clear, false)
            .fillInput(selectors.component.input, 'Page', {delay: 1000})
            .assert.visible(selectors.component.clear)
            .click(selectors.component.clear, {delay: 1000})
            .assert.visible(selectors.component.clear, false)
            .assert.visible(selectors.component.results, false)
            .assert.exist(selectors.component.input + '[value="Page"]', false);
    };

    /**
     * Test: Outside Click - dismisses results
     */
    search.testOutsideClick = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase('Outside Click', {
            execBefore: tcExecuteBeforeTest,
            execAfter : tcExecuteAfterTest
        })
          .config.changeContext(c.getContentFrame)
          .assert.visible(selectors.component.clear, false)
          .fillInput(selectors.component.input, 'Page', {delay: 1000})
          .assert.visible(selectors.component.clear)
          .click('body', {delay: 1000})
          .assert.visible(selectors.component.results, false)
    };

    /**
     * Test: Mark - search term marked
     */
    search.testMark = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new TestCase('Mark', {
            execBefore: tcExecuteBeforeTest,
            execAfter : tcExecuteAfterTest
        })
          .config.changeContext(c.getContentFrame)
          .assert.visible(selectors.component.clear, false)
            .execFct(function(opts, done) {
                pollQuery(done, c.rootPage, 'Page', h.param('page_1')());
            })
          .fillInput(selectors.component.input, 'Page', {delay: 1000})
          .assert.visible(selectors.component.item.mark + ':contains("Page")')
    };

}(hobs, jQuery));
