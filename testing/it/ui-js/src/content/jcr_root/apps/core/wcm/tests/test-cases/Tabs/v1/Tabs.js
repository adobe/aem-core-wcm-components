/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

    window.CQ.CoreComponentsIT.Tabs.v1 = window.CQ.CoreComponentsIT.Tabs.v1 || {};
    var c                              = window.CQ.CoreComponentsIT.commons;
    var tabs                           = window.CQ.CoreComponentsIT.Tabs.v1;
    var pageName                       = "tabs-page";
    var pageVar                        = "tabs_page";
    var pageDescription                = "tabs page description";

    var keyCodes = {
        END: 35,
        HOME: 36,
        ARROW_LEFT: 37,
        ARROW_UP: 38,
        ARROW_RIGHT: 39,
        ARROW_DOWN: 40
    };

    tabs.tcExecuteBeforeTest = function(tcExecuteBeforeTest, tabsRT, pageRT, clientlibs) {
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

                c.createPolicy(policySuffix, data, "policyPath", done, c.policyPath);
            })

            .execFct(function(opts, done) {
                var policySuffix = "/structure/page/new_policy";
                var policyLocation = "core-component/components";
                var policyAssignmentPath = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content";
                var data = {
                    "cq:policy": policyLocation + policySuffix,
                    "sling:resourceType": "wcm/core/components/policies/mappings"
                };

                c.assignPolicy("", data, done, policyAssignmentPath);
            })

            // create a proxy component
            .execFct(function(opts, done) {
                c.createProxyComponent(tabsRT, c.proxyPath, "proxyPath", done);
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

    tabs.tcExecuteAfterTest = function(tcExecuteAfterTest, policyPath, policyAssignmentPath) {
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
                c.deletePolicy("/structure/page", done, c.policyPath);
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
    tabs.tcCreateItems = function(selectors, component, cmpPath) {
        return new h.TestCase("Create child items")
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog(cmpPath))
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(component)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item0")
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(component)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item1")
            .click(selectors.editDialog.childrenEditor.addButton)
            .wait(200)
            .click(component)
            .wait(200)
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, "item2")
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200);
    };

    /**
     * Create a new tab and name it
     */
    tabs.tcAddTab = function(selectors, component, parentPath, cmpPath, tabName) {
        return new h.TestCase("Create a new tab")
            .execFct(function(opts, done) {
                c.addComponent(h.param(component)(opts), h.param(parentPath)(opts) + "/", cmpPath, done);
            })
            .execTestCase(c.tcOpenConfigureDialog(parentPath))
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, tabName)
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200);
    };

    /**
     * Test: Edit Dialog: Add child items
     */
    tabs.tcAddItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Add child items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))
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
    tabs.tcRemoveItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Remove child items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))
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
    tabs.tcReorderItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Re-order children", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))
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
     * Test: Edit Dialog : Re-order children
     */
    tabs.tcSetActiveItem = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Set active item", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))
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
            // switch to properties tab
            .click("coral-tab-label:contains('Properties')")
            // select second item as active
            .click(selectors.editDialog.properties.activeSelect + " button")
            .wait(200)
            .click(selectors.editDialog.properties.activeSelect + " coral-selectlist-item:contains('item1')")
            .wait(200)
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            .config.changeContext(c.getContentFrame)
            // check the second tab is active
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item1')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 2;
            })
            .config.resetContext()
            // open the edit dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // switch to properties tab
            .click("coral-tab-label:contains('Properties')")
            // select default as active
            .click(selectors.editDialog.properties.activeSelect + " button")
            .wait(200)
            .click(selectors.editDialog.properties.activeSelect + " coral-selectlist-item:contains('Default')")
            .wait(200)
            // save the edit dialog
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)
            .config.changeContext(c.getContentFrame)
            // check the first tab is active
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 1;
            })
            .config.resetContext();
    };

    /**
     * Test: Panel Select: Check items
     */
    tabs.tcPanelSelectItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Panel Select: Check items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // open the toolbar
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // verify that initially no panel select action is available
            .asserts.visible(selectors.editableToolbar.actions.panelSelect, false)

            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

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

            // verify initial Tabs DOM item order is as expected
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.tabs.tab + ":contains('item0'):first-child", true)
            .assert.exist(selectors.tabs.tab + ":contains('item1'):nth-child(2)", true)
            .assert.exist(selectors.tabs.tab + ":contains('item2'):last-child", true)
            .config.resetContext()

            // click elsewhere and verify an out of area click closes the panel selector
            .click(selectors.overlay.responsiveGrid.placeholder)
            .asserts.visible(selectors.panelSelector.self, false);
    };

    /**
     * Test: Panel Select: Reordering items
     */
    tabs.tcPanelSelectReorder = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Panel Select: Re-order items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // open the toolbar
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // open the panel selector and verify it's open
            .click(selectors.editableToolbar.actions.panelSelect)
            .asserts.visible(selectors.panelSelector.self)

            // drag to reorder
            .cui.dragdrop(selectors.panelSelector.item  + ":contains(item0)" + " [coral-table-roworder='true']", selectors.panelSelector.item  + ":contains(item2)")

            // verify new Tabs DOM item order is as expected
            .config.changeContext(c.getContentFrame)
            // TODO : item0 is placed in the second position on Firefox and 6.3 and in the last position on Chrome and other AEM versions. We should find a solution to place item0 in the last position for all browsers
            .asserts.isTrue(function() {
                var tabs = h.find(selectors.tabs.tab);
                return tabs.size() === 3 &&
                    $(tabs[0]).is(selectors.tabs.tab + ":contains(item1)") &&
                    ($(tabs[1]).is(selectors.tabs.tab + ":contains(item0)") || $(tabs[1]).is(selectors.tabs.tab + ":contains(item2)")) &&
                    ($(tabs[2]).is(selectors.tabs.tab + ":contains(item2)") || $(tabs[2]).is(selectors.tabs.tab + ":contains(item0)"));
            })
            .config.resetContext();
    };

    /**
     * Test: Allowed components
     */
    tabs.tcAllowedComponents = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, tabsRT) {
        return new h.TestCase("Allowed components", {
            execAfter: tcExecuteAfterTest
        })
            // create a proxy component for a teaser
            .execFct(function(opts, done) {
                c.createProxyComponent(c.rtTeaser_v1, c.proxyPath, "teaserProxyPath", done);
            })

            // add a policy for tabs component
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
                c.createProxyComponent(tabsRT, c.proxyPath, "proxyPath", done);
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
                c.deletePolicy("/tabs", done, policyPath);
            })
            .execFct(function(opts, done) {
                c.deletePolicyAssignment("/tabs", done, policyAssignmentPath);
            });
    };

    /**
     * Test: Accessibility : Navigate Right
     */
    tabs.tcAccessibilityNavigateRight = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, tabsRT) {
        return new h.TestCase("Accessibility : Navigate Right", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // switch to the content frame and focus the first tab
            .config.changeContext(c.getContentFrame)
            .click(selectors.tabs.tab + ":first-child")

            // simulate a right arrow keydown event and verify right navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_RIGHT })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item1')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 2;
            })

            // simulate a down arrow keydown event and verify right navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_DOWN })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item2')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 3;
            })

            // simulate a right arrow keydown event and verify no further navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_RIGHT })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item2')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 3;
            });
    };

    /**
     * Test: Accessibility : Navigate Left
     */
    tabs.tcAccessibilityNavigateLeft = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, tabsRT) {
        return new h.TestCase("Accessibility : Navigate Left", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // switch to the content frame and focus the last tab
            .config.changeContext(c.getContentFrame)
            .click(selectors.tabs.tab + ":last-child")

            // simulate a left arrow keydown event and verify left navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_LEFT })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item1')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 2;
            })

            // simulate an up arrow keydown event and verify left navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_UP })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 1;
            })

            // simulate a left arrow keydown event and verify no further navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.ARROW_LEFT })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 1;
            });
    };

    /**
     * Test: Keys : Navigate end / start
     */
    tabs.tcAccessibilityNavigateEndStart = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, tabsRT) {
        return new h.TestCase("Accessibility : Navigate End / Start", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create new items with titles
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // switch to the content frame and focus the first tab
            .config.changeContext(c.getContentFrame)
            .click(selectors.tabs.tab + ":first-child")

            // simulate an end arrow keydown event and verify end navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.END })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item2')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 3;
            })

            // simulate a home arrow keydown event and verify start navigation
            .simulate(selectors.tabs.tab + ":focus", "keydown", { keyCode: keyCodes.HOME })
            .asserts.isTrue(function() {
                var $tabActive = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive.size() === 1 && $tabpanelActive.size() === 1 && $tabpanelActive.index() === 1;
            });
    };

    /**
     * Test: Nested tabs
     */
    tabs.tcNestedTabs = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Test nested tabs", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // create nested tabs
            .execTestCase(tabs.tcAddTab(selectors, "proxyPath", "cmpPath", "tab1Path", "Tab 1"))
            .execTestCase(tabs.tcAddTab(selectors, "proxyPath", "cmpPath", "tab2Path", "Tab 2"))
            .execTestCase(tabs.tcAddTab(selectors, "proxyPath", "tab2Path", "tab21Path", "Tab 2.1"))

            // create new items on tab 2.1
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "tab21Path"))
            // create new items on tab 2
            .execTestCase(tabs.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "tab2Path"))

            .config.changeContext(c.getContentFrame)
            // check tab2's items
            .click(selectors.tabs.tab + ":contains('Tab 2'):first")
            .asserts.isTrue(function() {
                var $tabActive1 = h.find(selectors.tabs.tabActive + ":contains('Tab 2.1')");
                var $tabActive2 = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive1.size() === 1 && $tabActive2.size() === 1 && $tabpanelActive.size() === 3;
            })
            // check tab2.1's items
            .click(selectors.tabs.tab + ":contains('item1'):eq(1)")
            .asserts.isTrue(function() {
                var $tabActive1 = h.find(selectors.tabs.tabActive + ":contains('Tab 2.1')");
                var $tabActive2 = h.find(selectors.tabs.tabActive + ":contains('item1')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive1.size() === 1 && $tabActive2.size() === 1 && $tabpanelActive.size() === 3;
            })
            // check that tab2.1's items are not visible anymore
            .click(selectors.tabs.tab + ":contains('item0'):first")
            .asserts.isTrue(function() {
                var $tabActive1 = h.find(selectors.tabs.tabActive + ":contains('Tab 2.1')");
                var $tabActive2 = h.find(selectors.tabs.tabActive + ":contains('item0')");
                var $tabpanelActive = h.find(selectors.tabs.tabpanelActive);
                return $tabActive1.size() === 0 && $tabActive2.size() === 1 && $tabpanelActive.size() === 3;
            });
    };
}(hobs, jQuery));
