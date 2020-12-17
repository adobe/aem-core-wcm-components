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
/* global hobs, jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    window.CQ.CoreComponentsIT.Accordion.v1 = window.CQ.CoreComponentsIT.Accordion.v1 || {};
    var c = window.CQ.CoreComponentsIT.commons;
    var accordion = window.CQ.CoreComponentsIT.Accordion.v1;
    var pageName = "accordion-page";
    var pageVar = "accordion_page";
    var pageDescription = "accordion page description";

    /**
     * Before Test Case
     *
     * 1. create test page
     * 2. create clientlib page policy
     * 3. assign clientlib page policy
     * 4. create the proxy component
     * 5. set cq:isContainer property true
     * 6. add the proxy component to the page
     * 7. open the test page in the editor
     */
    accordion.tcExecuteBeforeTest = function(tcExecuteBeforeTest, accordionRT, pageRT, clientlibs) {
        return new h.TestCase("Create sample content", {
            execBefore: tcExecuteBeforeTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, pageName, pageVar, done, pageRT, pageDescription);
            })

            // 2.
            .execFct(function(opts, done) {
                var policySuffix = "/structure/page/new_policy";
                var data = {
                    "jcr:title": "New Policy",
                    "sling:resourceType": "wcm/core/components/policy/policy",
                    "clientlibs": clientlibs
                };

                c.createPolicy(policySuffix, data, "policyPath", done, c.policyPath);
            })

            // 3.
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

            // 4.
            .execFct(function(opts, done) {
                c.createProxyComponent(accordionRT, c.proxyPath, "proxyPath", done);
            })

            // 5.
            .execFct(function(opts, done) {
                var data = {};
                data["cq:isContainer"] = "true";
                c.editNodeProperties(h.param("proxyPath")(opts), data, done);
            })

            // 6.
            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param(pageVar)(opts) + c.relParentCompPath, "cmpPath", done);
            })

            // 7.
            .navigateTo("/editor.html%" + pageVar + "%.html");
    };

    /**
     * After Test Case
     *
     * 1. delete the test proxy component
     * 2. delete the test page
     * 3. delete the clientlib page policy
     * 4. reassign the default policy
     */
    accordion.tcExecuteAfterTest = function(tcExecuteAfterTest, policyPath, policyAssignmentPath) {
        return new h.TestCase("Clean up after test", {
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("proxyPath")(opts), done);
            })

            // 2.
            .execFct(function(opts, done) {
                c.deletePage(h.param(pageVar)(opts), done);
            })

            // 3.
            .execFct(function(opts, done) {
                c.deletePolicy("/structure/page", done, c.policyPath);
            })

            // 4.
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
     * Create three items via the children editor
     *
     * 1. open the edit dialog
     * 2. add item via the children editor
     * 3. save the edit dialog
     */
    accordion.tcCreateItems = function(selectors, component, cmpPath) {
        return new h.TestCase("Create items")
            // 1.
            .execTestCase(c.tcOpenConfigureDialog(cmpPath))

            // 2.
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

            // 3.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200);
    };

    /**
     * Create and title a single accordion item
     *
     * 1. add a component to the accordion
     * 2. open the edit dialog
     * 3. name the accordion item
     * 4. save the edit dialog
     */
    accordion.tcAddAccordionItem = function(selectors, component, parentPath, cmpPath, tabName) {
        return new h.TestCase("Create a new accordion item")
            // 1.
            .execFct(function(opts, done) {
                c.addComponent(h.param(component)(opts), h.param(parentPath)(opts) + "/", cmpPath, done);
            })

            // 2.
            .execTestCase(c.tcOpenConfigureDialog(parentPath))

            // 3.
            .fillInput(selectors.editDialog.childrenEditor.item.last + " " + selectors.editDialog.childrenEditor.item.input, tabName)

            // 4.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200);
    };

    /**
     * Switches to the edit dialog properties tab and verifies the provided (ordered) expanded items exist
     * in the expanded items select. Assumes the edit dialog is open.
     *
     * 1. switch to the properties tab
     * 2. open the expanded items select
     * 3. verify the expanded items match those passed
     */
    accordion.tcVerifyExpandedItemsSelect = function(items, selectors) {
        return new h.TestCase("Verify expanded items select")
            // 1.
            .click(selectors.editDialog.properties.self)

            // 2.
            .click(selectors.editDialog.properties.expandedSelect + " > button")
            .wait(200)

            // 3.
            .asserts.isTrue(function() {
                var $selectItems = h.find(selectors.editDialog.properties.expandedSelect + " coral-selectlist-item");
                if ($selectItems.length !== items.length) {
                    return false;
                }

                for (var i = 0; i < items.length; i++) {
                    var $selectItem = h.find(selectors.editDialog.properties.expandedSelect + " coral-selectlist-item:contains(" + items[i] + ")");
                    if (!$selectItem.length) {
                        return false;
                    }
                }

                return true;
            });
    };

    /**
     * Switches context to the content frame and verifies the passed (ordered) items
     *
     * 1. switch to the content frame
     * 2. verify the expanded items match those passed
     * 3. reset context back to the edit frame
     */
    accordion.tcVerifyExpandedItems = function(items, selectors) {
        return new h.TestCase("Verify expanded items")
            // 1.
            .config.changeContext(c.getContentFrame)

            // 2.
            .asserts.isTrue(function() {
                var $itemsExpanded = h.find(selectors.accordion.itemExpanded);

                if ($itemsExpanded.length !== items.length) {
                    return false;
                }

                for (var i = 0; i < items.length; i++) {
                    var $expandedItem = $itemsExpanded.eq(i).find(selectors.accordion.button + ":contains(" + items[i] + ")");
                    if (!$expandedItem.length) {
                        return false;
                    }
                }

                return true;
            })

            // 3.
            .config.resetContext();
    };

    /**
     * Test: Edit Dialog: Add items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. verify that three items have been created
     * 4. verify the expanded items select
     * 5. save the edit dialog
     */
    accordion.tcAddItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Add items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 3.
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item0" &&
                    $(children[1]).val() === "item1" &&
                    $(children[2]).val() === "item2";
            })

            // 4.
            .execTestCase(accordion.tcVerifyExpandedItemsSelect(["item0", "item1", "item2"], selectors))

            // 5.
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Edit Dialog : Remove items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. remove the first item and save the edit dialog
     * 4. open the edit dialog
     * 5. verify that the first item has been removed
     * 6. verify the expanded items select
     * 7. save the edit dialog
     */
    accordion.tcRemoveItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Remove items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 3.
            .click(selectors.editDialog.childrenEditor.item.first + " " + selectors.editDialog.childrenEditor.removeButton)
            .wait(200)
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 4.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 5.
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 2 &&
                    $(children[0]).val() === "item1" &&
                    $(children[1]).val() === "item2";
            })

            // 6.
            .execTestCase(accordion.tcVerifyExpandedItemsSelect(["item1", "item2"], selectors))

            // 7.
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Edit Dialog : Reorder items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. move the last item before the first one
     * 4. save the edit dialog
     * 5. open the edit dialog
     * 6. verify the new order
     * 7. verify the expanded items select
     * 8. save the edit dialog
     */
    accordion.tcReorderItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Reorder items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 3.
            .execFct(function(opts, done) {
                var $first = h.find(selectors.editDialog.childrenEditor.item.first);
                var $last = h.find(selectors.editDialog.childrenEditor.item.last);
                $last.detach().insertBefore($first);
                done(true);
            })

            // 4.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 5.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 6.
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item2" &&
                    $(children[1]).val() === "item0" &&
                    $(children[2]).val() === "item1";
            })

            // 7.
            .execTestCase(accordion.tcVerifyExpandedItemsSelect(["item2", "item0", "item1"], selectors))

            // 8.
            .execTestCase(c.tcSaveConfigureDialog);
    };

    /**
     * Test: Edit Dialog : Set expanded items
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. verify that three items have been created
     * 4. switch to the properties tab and set second item expanded
     * 5. save the edit dialog
     * 6. verify the second item is expanded
     * 7. open the edit dialog
     * 8. switch to the properties tab and also set the third item expanded
     * 9. save the edit dialog
     * 10. verify both second and third items are expanded
     *
     */
    accordion.tcSetExpandedItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Set expanded items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 3.
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item0" &&
                    $(children[1]).val() === "item1" &&
                    $(children[2]).val() === "item2";
            })

            // 4.
            .click(selectors.editDialog.properties.self)
            .click(selectors.editDialog.properties.expandedSelect + " > button")
            .wait(200)
            .click(selectors.editDialog.properties.expandedSelect + " coral-selectlist-item:contains('item1')")
            .wait(500)

            // 5.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 6.
            .execTestCase(accordion.tcVerifyExpandedItems(["item1"], selectors))

            // 7.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .wait(200)

            // 8.
            .click(selectors.editDialog.properties.self)
            .wait(200)
            .click(selectors.editDialog.properties.expandedSelect + " > button")
            .wait(200)
            .click(selectors.editDialog.properties.expandedSelect + " coral-selectlist-item:contains('item2')")
            .wait(500)

            // 9.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 10.
            .execTestCase(accordion.tcVerifyExpandedItems(["item1", "item2"], selectors));
    };

    /**
     * Test: Edit Dialog : Single item expansion
     *
     * 1. create new items with titles
     * 2. open the edit dialog
     * 3. verify that three items have been created
     * 4. switch to the properties tab
     * 5. verify that the expanded items select is enabled, expanded item select is disabled and single item expansion disabled.
     * 6. enable single item expansion
     * 7. verify that the expanded items select is disabled and expanded item select is enabled.
     * 8. save the edit dialog
     * 9. verify that the first item is expanded
     *
     */
    accordion.tcSingleItemExpansion = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Edit Dialog : Single item expansion", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 3.
            .asserts.isTrue(function() {
                var children = h.find(selectors.editDialog.childrenEditor.item.input);
                return children.size() === 3 &&
                    $(children[0]).val() === "item0" &&
                    $(children[1]).val() === "item1" &&
                    $(children[2]).val() === "item2";
            })

            // 4.
            .click(selectors.editDialog.properties.self)

            // 5.
            .assert.exist(selectors.editDialog.properties.singleExpansion + "[checked]", false)
            .assert.exist(selectors.editDialog.properties.expandedSelect + ":visible", true)
            .assert.exist(selectors.editDialog.properties.expandedSelect + "[disabled]", false)
            .assert.exist(selectors.editDialog.properties.expandedSelectSingle + ":visible", false)
            .assert.exist(selectors.editDialog.properties.expandedSelectSingle + "[disabled]", true)

            // 6.
            .click(selectors.editDialog.properties.singleExpansion)
            .wait(500)

            // 7.
            .assert.exist(selectors.editDialog.properties.singleExpansion + "[checked]", true)
            .assert.exist(selectors.editDialog.properties.expandedSelect + ":visible", false)
            .assert.exist(selectors.editDialog.properties.expandedSelect + "[disabled]", true)
            .assert.exist(selectors.editDialog.properties.expandedSelectSingle + ":visible", true)
            .assert.exist(selectors.editDialog.properties.expandedSelectSingle + "[disabled]", false)

            // 8.
            .execTestCase(c.tcSaveConfigureDialog)
            .wait(200)

            // 9.
            .execTestCase(accordion.tcVerifyExpandedItems(["item0"], selectors));
    };

    /**
     * Test: Panel Select: Check items
     *
     * 1. open the component edit toolbar
     * 2. verify that initially no panel select action is available
     * 3. create new items with titles
     * 4. open the component edit toolbar
     * 5. verify the panel select action is available
     * 6. open the panel selector and verify it's open
     * 7. verify that three items are available and the correct titles are visible
     * 8. verify initial Accordion DOM item order is as expected
     * 9. click elsewhere and verify an out of area click closes the panel selector
     */
    accordion.tcPanelSelectItems = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Panel Select: Check items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // 2.
            .asserts.visible(selectors.editableToolbar.actions.panelSelect, false)

            // 3.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 4.
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // 5.
            .asserts.visible(selectors.editableToolbar.actions.panelSelect)

            // 6.
            .click(selectors.editableToolbar.actions.panelSelect)
            .asserts.visible(selectors.panelSelector.self)

            // 7.
            .asserts.isTrue(function() {
                var items = h.find(selectors.panelSelector.item);
                return items.size() === 3 &&
                    $(items[0]).is(selectors.panelSelector.item + ":contains(item0)") &&
                    $(items[1]).is(selectors.panelSelector.item + ":contains(item1)") &&
                    $(items[2]).is(selectors.panelSelector.item + ":contains(item2)");
            })

            // 8.
            .config.changeContext(c.getContentFrame)
            .assert.exist(selectors.accordion.item + ":first-child " + selectors.accordion.button + ":contains(item0)", true)
            .assert.exist(selectors.accordion.item + ":nth-child(2) " + selectors.accordion.button + ":contains(item1)", true)
            .assert.exist(selectors.accordion.item + ":last-child " + selectors.accordion.button + ":contains(item2)", true)
            .config.resetContext()
            .wait(200)

            // 9.
            .click(selectors.overlay.responsiveGrid.placeholder)
            .asserts.visible(selectors.panelSelector.self, false);
    };

    /**
     * Test: Panel Select: Reordering items
     *
     * 1. create new items with titles
     * 2. open the component edit toolbar
     * 3. open the panel selector and verify it's open
     * 4. drag to reorder
     * 5. verify new Accordion DOM item order is as expected
     */
    accordion.tcPanelSelectReorder = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Panel Select: Reorder items", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcCreateItems(selectors, selectors.insertComponentDialog.components.responsiveGrid, "cmpPath"))

            // 2.
            .click(selectors.overlay.self + "[data-path='%cmpPath%']")
            .asserts.visible(selectors.editableToolbar.self)

            // 3.
            .click(selectors.editableToolbar.actions.panelSelect)
            .asserts.visible(selectors.panelSelector.self)

            // 4.
            .cui.dragdrop(selectors.panelSelector.item  + ":contains(item0)" + " [coral-table-roworder='true']", selectors.panelSelector.item  + ":contains(item2)")

            // 5.
            .config.changeContext(c.getContentFrame)
            .wait(200)

            // TODO : item0 is placed in the second position on Firefox and 6.3 and in the last position on Chrome and other AEM versions. We should find a solution to place item0 in the last position for all browsers
            .asserts.isTrue(function() {
                var buttons = h.find(selectors.accordion.button);
                return buttons.size() === 3 &&
                    $(buttons[0]).is(selectors.accordion.button + ":contains(item1)") &&
                    ($(buttons[1]).is(selectors.accordion.button + ":contains(item0)") || $(buttons[1]).is(selectors.accordion.button + ":contains(item2)")) &&
                    ($(buttons[2]).is(selectors.accordion.button + ":contains(item2)") || $(buttons[2]).is(selectors.accordion.button + ":contains(item0)"));
            })
            .config.resetContext();
    };

    /**
     * Test: Nested
     *
     * 1. create nested accordions
     * 2. change context to the content frame
     * 3. verify items
     */
    accordion.tcNested = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors) {
        return new h.TestCase("Nested", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execTestCase(accordion.tcAddAccordionItem(selectors, "proxyPath", "cmpPath", "accordion1Path", "Accordion 1.1"))
            .execTestCase(accordion.tcAddAccordionItem(selectors, "proxyPath", "cmpPath", "accordion2Path", "Accordion 1.2"))
            .execTestCase(accordion.tcAddAccordionItem(selectors, "proxyPath", "accordion2Path", "accordion21Path", "Accordion 2.1"))
            .execTestCase(accordion.tcAddAccordionItem(selectors, "proxyPath", "accordion2Path", "accordion22Path", "Accordion 2.2"))

            // 2.
            .config.changeContext(c.getContentFrame)
            .wait(200)

            // 3.
            .assert.exist(selectors.accordion.button + ":contains(Accordion 1.1)", true)
            .assert.exist(selectors.accordion.button + ":contains(Accordion 1.2)", true)
            .assert.exist(selectors.accordion.button + ":contains(Accordion 2.1)", true)
            .assert.exist(selectors.accordion.button + ":contains(Accordion 2.2)", true);
    };

    /**
     * Test: Allowed components
     *
     * 1. create a proxy teaser component
     * 2. add a policy for the accordion component that allows teasers only
     * 3. create a proxy accordion component
     * 4. set accordion component property cq:isContainer to true
     * 5. create a test page
     * 6. add the accordion proxy component to the page
     * 7. navigate to the test page
     * 8. open the component edit toolbar
     * 9. open the insert component popover
     * 10. verify teaser is in the list of allowed components
     * 11. clean up
     */
    accordion.tcAllowedComponents = function(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, policyName, policyLocation, policyPath, policyAssignmentPath, pageRT, accordionRT) {
        return new h.TestCase("Allowed components", {
            execAfter: tcExecuteAfterTest
        })
            // 1.
            .execFct(function(opts, done) {
                c.createProxyComponent(c.rtTeaser_v1, c.proxyPath, "teaserProxyPath", done);
            })

            // 2.
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

            // 3.
            .execFct(function(opts, done) {
                c.createProxyComponent(accordionRT, c.proxyPath, "proxyPath", done);
            })

            // 4.
            .execFct(function(opts, done) {
                var data = {};
                data["cq:isContainer"] = "true";
                c.editNodeProperties(h.param("proxyPath")(opts), data, done);
            })

            // 5.
            .execFct(function(opts, done) {
                c.createPage(c.template, c.rootPage, pageName, pageVar, done, pageRT, pageDescription);
            })

            // 6.
            .execFct(function(opts, done) {
                c.addComponent(h.param("proxyPath")(opts), h.param(pageVar)(opts) + c.relParentCompPath, "cmpPath", done);
            })

            // 7.
            .navigateTo("/editor.html%" + pageVar + "%.html")

            // 8.
            .click(selectors.overlay.self + "[data-path='%cmpPath%/*']")
            .asserts.visible(selectors.editableToolbar.self)

            // 9.
            .click(selectors.editableToolbar.actions.insert)

            // 10.
            .asserts.visible("coral-selectlist-item[value='%teaserProxyPath%']")

            // 11.
            .execFct(function(opts, done) {
                c.deleteProxyComponent(h.param("teaserProxyPath")(opts), done);
            })
            .execFct(function(opts, done) {
                c.deletePolicy("/accordion", done, policyPath);
            })
            .execFct(function(opts, done) {
                c.deletePolicyAssignment("/accordion", done, policyAssignmentPath);
            });
    };
}(hobs, jQuery));
