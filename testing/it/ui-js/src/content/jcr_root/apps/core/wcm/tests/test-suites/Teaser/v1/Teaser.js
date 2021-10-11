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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var teaser = window.CQ.CoreComponentsIT.Teaser.v1;
    var selectors = {
        component: {
            self: ".cmp-teaser",
            image: ".cmp-teaser__image",
            pretitle: ".cmp-teaser__pretitle",
            title: ".cmp-teaser__title",
            titleLink: ".cmp-teaser__title-link",
            description: ".cmp-teaser__description",
            actionLink: "a.cmp-teaser__action-link"
        },
        editDialog: {
            assetDrag: function(imagePath) {
                return 'coral-card.cq-draggable[data-path="' + imagePath + '"]';
            },
            assetDrop: '.cmp-teaser__editor coral-fileupload[name="./file"]',
            linkURL: '.cmp-teaser__editor foundation-autocomplete[name="./linkURL"]',
            titleFromPage: '.cmp-teaser__editor input[name="./titleFromPage"]',
            pretitle: '.cmp-teaser__editor input[name="./pretitle"]',
            title: '.cmp-teaser__editor input[name="./jcr:title"]',
            descriptionFromPage: '.cmp-teaser__editor input[name="./descriptionFromPage"]',
            description: '.cmp-teaser__editor input[name="./jcr:description"]',
            actionsEnabled: '.cmp-teaser__editor coral-checkbox[name="./actionsEnabled"]',
            actionLinkURL: '[data-cmp-teaser-v1-dialog-edit-hook="actionLink"]',
            actionText: '[data-cmp-teaser-v1-dialog-edit-hook="actionTitle"]',
            tabs: {
                image: ".cmp-teaser__editor coral-tab:eq(0)",
                text: ".cmp-teaser__editor coral-tab:eq(1)",
                linkAndActions: ".cmp-teaser__editor coral-tab:eq(2)"
            }
        },
        assetFinder: {
            filters: {
                path: {
                    self: 'foundation-autocomplete[name="assetfilter_image_path"]',
                    textField: 'foundation-autocomplete[name="assetfilter_image_path"] [is="coral-textfield"]',
                    buttonListItem: 'foundation-autocomplete[name="assetfilter_image_path"] [is="coral-buttonlist-item"]'
                }
            }
        }
    };

    var tcExecuteBeforeTest = teaser.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtTeaser_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = teaser.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Teaser v1", {
        path: "/apps/core/wcm/tests/core-components-it/v1/Teaser.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(teaser.testFullyConfiguredTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(teaser.testInheritedPropertiesTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(teaser.testNoImageTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(teaser.testHideElementsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(teaser.testLinksToElementsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(teaser.testDisableActionsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components",
            c.policyPath, c.policyAssignmentPath))
        .addTestCase(teaser.testWithActionsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components"))
        .addTestCase(teaser.testWithExternalActionsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components"))
        .addTestCase(teaser.testCheckboxTextfieldTuple(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));
}(hobs, jQuery));
