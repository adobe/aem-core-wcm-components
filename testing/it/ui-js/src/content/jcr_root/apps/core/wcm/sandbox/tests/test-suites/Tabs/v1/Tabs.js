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
/* globals hobs,jQuery */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var tabs = window.CQ.CoreComponentsIT.Tabs.v1;
    var selectors = {
        editDialog: {
            childrenEditor: {
                self: ".cmp-childreneditor",
                addButton: "[data-cmp-hook-childreneditor='add']",
                removeButton: "button[handle='remove']",
                item: {
                    self: "coral-multifield-item",
                    first: "coral-multifield-item:first",
                    last: "coral-multifield-item:last",
                    input: "[data-cmp-hook-childreneditor='itemTitle']",
                    hiddenInput: "[data-cmp-hook-childreneditor='itemResourceType']"
                }
            }
        },
        insertComponentDialog: {
            self: ".InsertComponentDialog",
            components: {
                responsiveGrid: "coral-selectlist-item[value='/libs/wcm/foundation/components/responsivegrid']",
                tab: "coral-selectlist-item[value='/apps/core-component/components/sandbox/tabs']"
            }
        },
        editableToolbar: {
            self: "#EditableToolbar",
            actions: {
                insert: ".cq-editable-action[data-action='INSERT']",
                panelSelect: ".cq-editable-action[data-action='PANEL_SELECT']"
            }
        },
        panelSelector: {
            self: ".cmp-panelselector",
            item: ".cmp-panelselector__table [is='coral-table-row']"
        },
        overlay: {
            self: ".cq-Overlay",
            responsiveGrid: {
                placeholder: ".cq-Overlay[data-path='/content/core-components/core-components-page/tabs-page/jcr:content/root/responsivegrid/*']"
            }
        },
        tabs: {
            self: ".cmp-tabs",
            tab: ".cmp-tabs__tab",
            tabActive: ".cmp-tabs__tab--active",
            tabpanel: ".cmp-tabs__tabpanel",
            tabpanelActive: ".cmp-tabs__tabpanel--active"
        }
    };

    var tcExecuteBeforeTest = tabs.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtTabs_v1,
        "core/wcm/tests/components/test-page-v2", "core.wcm.components.tabs.v1");
    var tcExecuteAfterTest  = tabs.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath_sandbox, c.policyAssignmentPath_sandbox);

    new h.TestSuite("Tabs v1", {
        path: "/apps/core/wcm/sandbox/tests/core-components-it/v1/Tabs.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(tabs.tcAddItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcRemoveItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcReorderItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcPanelSelect(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcNestedTabs(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcAllowedComponents(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/tabs", "core-component/components/sandbox",
            c.policyPath_sandbox, c.policyAssignmentPath_sandbox, "core/wcm/tests/components/test-page-v2", c.rtTabs_v1))
        .addTestCase(tabs.tcAccessibilityNavigateRight(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcAccessibilityNavigateLeft(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(tabs.tcAccessibilityNavigateEndStart(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));
}(hobs, jQuery));
