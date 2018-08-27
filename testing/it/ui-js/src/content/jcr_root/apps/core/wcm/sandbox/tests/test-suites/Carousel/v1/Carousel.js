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
    var carousel = window.CQ.CoreComponentsIT.Carousel.v1;
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
                responsiveGrid: "coral-selectlist-item[value='/libs/wcm/foundation/components/responsivegrid']"
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
                placeholder: ".cq-Overlay[data-path='/content/core-components/core-components-page/carousel-page/jcr:content/root/responsivegrid/*']"
            }
        },
        carousel: {
            self: ".cmp-carousel",
            item: ".cmp-carousel__item",
            itemActive: ".cmp-carousel__item--active",
            indicator: ".cmp-carousel__indicator",
            indicatorActive: ".cmp-carousel__indicator--active"
        }
    };

    var tcExecuteBeforeTest = carousel.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtCarousel_v1,
        "core/wcm/tests/components/test-page-v2", "core.wcm.components.carousel.v1");
    var tcExecuteAfterTest  = carousel.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath_sandbox, c.policyAssignmentPath_sandbox);

    new h.TestSuite("Carousel v1", {
        path: "/apps/core/wcm/sandbox/tests/core-components-it/v1/Carousel.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(carousel.tcAddItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcRemoveItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcReorderItems(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcPanelSelect(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(carousel.tcAllowedComponents(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/carousel", "core-component/components/sandbox",
            c.policyPath_sandbox, c.policyAssignmentPath_sandbox, "core/wcm/tests/components/test-page-v2", c.rtCarousel_v1));
}(hobs, jQuery));
