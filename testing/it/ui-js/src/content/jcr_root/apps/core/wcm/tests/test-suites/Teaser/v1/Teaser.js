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
    var teaser = window.CQ.CoreComponentsIT.Teaser.v1;
    var selectors = {
        component: {
            self: ".cmp-teaser",
            image: ".cmp-teaser__image",
            title: ".cmp-teaser__title",
            titleLink: ".cmp-teaser__title-link",
            description: ".cmp-teaser__description",
            actionLink: "a.cmp-teaser__action-link"
        },
        editDialog: {
            assetDrag: function(imagePath) {
                return 'coral-card.cq-draggable[data-path="' + imagePath + '"]';
            },
            assetDrop: 'coral-fileupload[name="./file"',
            linkURL: 'foundation-autocomplete[name="./linkURL"]',
            titleFromPage: 'input[name="./titleFromPage"]',
            title: 'input[name="./jcr:title"]',
            descriptionFromPage: 'input[name="./descriptionFromPage"]',
            description: 'input[name="./jcr:description"]',
            actionsEnabled: 'coral-checkbox[name="./actionsEnabled"]',
            actionLinkURL: 'foundation-autocomplete[name="link"]',
            actionText: 'input[name="text"]'
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
        .addTestCase(teaser.testWithExternalActionsTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors, "/teaser", "core-component/components"));
}(hobs, jQuery));
