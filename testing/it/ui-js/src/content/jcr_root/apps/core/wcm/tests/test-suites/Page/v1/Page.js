/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Tests for the core page component.
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var page = window.CQ.CoreComponentsIT.Page.v1;

    /**
     * v1 specifics
     */
    var pageSelector = {
        segmentPath: "/etc/segmentation/contexthub/male"
    };
    var tcExecuteBeforeTest = page.tcExecuteBeforeTest();
    var tcExecuteAfterTest = page.tcExecuteAfterTest();

    /**
     * The main test suite for Page component
     */
    new h.TestSuite("Page v1", { path: "/apps/core/wcm/tests/test-suites/Page/v1/Page.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(page.tcBasicTitleAndTagsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcBasicTitlesAndDescriptionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcBasicOnOffTimePageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcBasicVanityUrlPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcAdvancedSettingsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcAdvancedTemplatesSettingsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcAdvancedAuthenticationPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcAdvancedExportPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcThumbnailPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcSocialMediaPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcCloudServicesPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcPersonalizationPageProperties(pageSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        // .addTestCase(page.tcAddPermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcEditUserGroupPermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcEffectivePermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcBlueprintPageProperties63(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(page.tcLiveCopyPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));

