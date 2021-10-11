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

    var c = window.CQ.CoreComponentsIT.commons;
    var accordion = window.CQ.CoreComponentsIT.Accordion.v1;
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
            },
            properties: {
                self: "coral-tab-label:contains('Properties')",
                singleExpansion: "[data-cmp-accordion-v1-dialog-edit-hook='singleExpansion']",
                expandedSelectSingle: "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelectSingle']",
                expandedSelect: "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect']"
            }
        },
        insertComponentDialog: {
            self: ".InsertComponentDialog",
            components: {
                responsiveGrid: "coral-selectlist-item[value='/libs/wcm/foundation/components/responsivegrid']",
                accordion: "coral-selectlist-item[value='/apps/core-component/components/accordion']"
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
                placeholder: ".cq-Overlay[data-path='/content/core-components/core-components-page/accordion-page/jcr:content/root/responsivegrid/*']"
            }
        },
        accordion: {
            self: ".cmp-accordion",
            item: "[data-cmp-hook-accordion='item']",
            itemExpanded: "[data-cmp-hook-accordion='item'][data-cmp-expanded]",
            button: "[data-cmp-hook-accordion='button']"
        }
    };

    var tcExecuteBeforeTest = accordion.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtAccordion_v1,
        "core/wcm/tests/components/test-page-v2", "core.wcm.components.accordion.v1");
    var tcExecuteAfterTest  = accordion.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Accordion v1", {
        path: "/apps/core/wcm/tests/core-components-it/v1/Accordion.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(accordion.tcAddItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcRemoveItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcReorderItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcSetExpandedItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcSingleItemExpansion(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcPanelSelectItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcPanelSelectReorder(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcNested(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(accordion.tcAllowedComponents(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/accordion", "core-component/components",
            c.policyPath, c.policyAssignmentPath, "core/wcm/tests/components/test-page-v2", c.rtAccordion_v1));
}(hobs, jQuery));
