/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
/* global hobs, jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    window.CQ.CoreComponentsIT.Carousel.v1 = window.CQ.CoreComponentsIT.Carousel.v1 || {};
    var c                                = window.CQ.CoreComponentsIT.commons;
    var carousel                         = window.CQ.CoreComponentsIT.Carousel.v1;
    var pageName                         = "carousel-page";
    var pageVar                          = "carousel_page";
    var pageDescription                  = "carousel page description";

    var PANEL_SELECTOR_ITEM_HEIGHT = 60;

    carousel.tcExecuteBeforeTest = function(tcExecuteBeforeTest, carouselRT, pageRT, clientlibs) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        })
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, pageName, pageVar, done, pageRT, pageDescription);
            })

            // create clientlib page policy and assignment
            .execFct(function(opts, done) {
                var policySuffix = "/structure/page/new_policy";
                var data = {
                    "jcr:title": "New Policy",
                    "sling:resourceType": "wcm/core/components/policy/policy",
                    "clientlibs": clientlibs
                };

                c.createPolicy(policySuffix, data, "policyPath", done, c.policyPath_sandbox);
            })

            .execFct(function(opts, done) {
                var policySuffix = "/structure/page/new_policy";
                var policyLocation = "core-component/components/sandbox";
                var policyAssignmentPath = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content";
                var data = {
                    "cq:policy": policyLocation + policySuffix,
                    "sling:resourceType": "wcm/core/components/policies/mappings"
                };

                c.assignPolicy("", data, done, policyAssignmentPath);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(carouselRT, c.proxyPath_sandbox, "proxyPath", done);
            })

            .execFct(function(opts, done) {
                // we need to set property cq:isContainer to true
                var data = {};
                data["cq:isContainer"] = "true";
                c.editNodeProperties(h.param("proxyPath")(opts), data, done);
            })

            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param(pageVar)(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%" + pageVar + "%.html");
    };

    carousel.tcExecuteAfterTest = function(tcExecuteAfterTest, policyPath, policyAssignmentPath) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        })
            // delete the test proxies we created
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("proxyPath")(opts), done);
            })

            .execFct(function(opts, done) {
                c.deletePage(h.param(pageVar)(opts), done);
            })

            // delete clientlib page policy and re-assign the default
            .execFct(function(opts, done) {
                c.deletePolicy("/structure/page", done, c.policyPath_sandbox);
            })
            .execFct(function(opts, done) {
                var policyAssignmentPath = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content";
                var data = {
                    "cq:policy": "wcm/foundation/components/page/default",
                    "sling:resourceType": "wcm/core/components/policies/mappings"
                };

                c.assignPolicy("", data, done, policyAssignmentPath);
            });
    };

    /**
     * Create child items
     */
    carousel.tcCreateItems = function(selectors) {
        return new h.TestCase("Create child items")
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(selectors.insertComponentDialog.components.responsiveGrid)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item0")
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(selectors.insertComponentDialog.components.responsiveGrid)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item1")
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(selectors.insertComponentDialog.components.responsiveGrid)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item2")
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200);
    };

    /**
     * Test: Edit Dialog: Add child items
     */
    carousel.tcAddItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Add child items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(carousel.tcCreateItems(selectors))
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // verify that 3 items have been created
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item0" &&
                    $(children[1]).val() === "item1" &&
                    $(children[2]).val() === "item2";
            })
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Edit Dialog : Remove child items
     */
    carousel.tcRemoveItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Remove child items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(carousel.tcCreateItems(selectors))
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // remove the first item
            .click(selectors.editDialog.childrenEditor.item.first + " " + selectors.editDialog.childrenEditor.removeButton)
            .wait(200)
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // verify that the first item has been removed
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 2 &&
                    $(children[0]).val() === "item1" &&
                    $(children[1]).val() === "item2";
            })
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Edit Dialog : Re-order children
     */
    carousel.tcReorderItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Re-order children", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(carousel.tcCreateItems(selectors))
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // move last item before the first one
            .execFct(function(opts, done) {
                var $first = h.find(selectors.editDialog.childrenEditor.item.first);
                var $last = h.find(selectors.editDialog.childrenEditor.item.last);
                $last.detach().insertBefore($first);
                done(true);
            })
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // verify the new order
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item2" &&
                    $(children[1]).val() === "item0" &&
                    $(children[2]).val() === "item1";
            })
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Panel Select
     */
    carousel.tcPanelSelect = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Panel Select", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // open the toolbar
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // verify that initially no panel select action is available
            .asserts.visible(selectors.editableToolbar.actions.panelSelect, false)

            // create new items with titles
            .execTestCase(carousel.tcCreateItems(selectors))

            // open the toolbar
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // verify the panel select action is available
            .asserts.visible(selectors.editableToolbar.actions.panelSelect)

            // open the panel selector and verify it's open
            .click(selectors.editableToolbar.actions.panelSelect)
            .asserts.visible(selectors.panelSelector.self)

            // verify that 3 items are available in the panel selector and the correct titles are visible
            .asserts.isTrue(function() {
                var items = h.find(selectors.panelSelector.item);
                return items.size() === 3 &&
                    $(items[0]).is(selectors.panelSelector.item + ":contains(item0)") &&
                    $(items[1]).is(selectors.panelSelector.item + ":contains(item1)") &&
                    $(items[2]).is(selectors.panelSelector.item + ":contains(item2)");
            })

            // verify initial Carousel DOM item order is as expected
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.carousel.indicator + ":contains('item0'):first-child", true)
            .config.resetContext()

            // drag to reorder
            .execFct(function(options, done) {
                hobs.find(selectors.panelSelector.item  + ":contains(item0) [coral-table-roworder='true']").simulate("drag-n-drop", { dx: 0, dy: PANEL_SELECTOR_ITEM_HEIGHT });
                done();
            })

            // verify new Carousel DOM item order is as expected
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.carousel.indicator + ":contains('item0'):nth-child(2)", true)
            .config.resetContext()

            // click elsewhere and verify an out of area click closes the panel selector
            .click(selectors.overlay.responsiveGrid.placeholder)
            .asserts.visible(selectors.panelSelector.self, false);
    };

    /**
     * Test: Allowed components
     */
    carousel.tcAllowedComponents = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, carouselRT) {
        return new h.TestCase("Allowed components", {
            execAfter: tcExecuteAfterTest
        })
            // create a proxy component for a teaser
            .execFct(function(opts, done) {
                c.createProxyComponent(c.rtTeaser_v1, c.proxyPath_sandbox, "teaserProxyPath", done);
            })

            // add a policy for carousel component
            .execFct(function(opts, done) {
                var data = {};
                data["jcr:title"] = "New Policy";
                data["sling:resourceType"] = "wcm/core/components/policy/policy";
                data["components"] = h.param("teaserProxyPath")(opts);

                c.createPolicy(policyName + "/new_policy", data, "policyPath", done, policyPath);
            })

            .execFct(function(opts, done) {
                var data = {};
                data["cq:policy"] = policyLocation + policyName + "/new_policy";
                data["sling:resourceType"] = "wcm/core/components/policies/mapping";

                c.assignPolicy(policyName, data, done, policyAssignmentPath);
            }, { after: 1000 })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(carouselRT, c.proxyPath_sandbox, "proxyPath", done);
            })

            .execFct(function(opts, done) {
                // we need to set property cq:isContainer to true
                var data = {};
                data["cq:isContainer"] = "true";
                c.editNodeProperties(h.param("proxyPath")(opts), data, done);
            })

            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, pageName, pageVar, done, pageRT, pageDescription);
            })

            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param(pageVar)(opts) + c.relParentCompPath, "cmpPath", done);
            })
            .navigateTo("/editor.html%" + pageVar + "%.html")

            .click(selectors.overlay.self + "[data-path='%cmpPath%/*']")

            // make sure its visible
            .asserts.visible(selectors.editableToolbar.self)
            // click on the 'insert component' button
            .click(selectors.editableToolbar.actions.insert)
            // verify teaser is in the list of allowed components
            .asserts.visible("coral-selectlist-item[value='%teaserProxyPath%']")

            // delete the teaser component
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("teaserProxyPath")(opts), done);
            })

            .execFct(function(opts, done) {
                c.deletePolicy("/carousel", done, policyPath);
            })
            .execFct(function(opts, done) {
                c.deletePolicyAssignment("/carousel", done, policyAssignmentPath);
            });
    };

}(hobs, jQuery));
