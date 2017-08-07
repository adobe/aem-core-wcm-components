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

window.CQ.CoreComponentsIT.v2.Page = window.CQ.CoreComponentsIT.v2.Page || {}

/**
 * Tests for the core page component.
 */
;(function(h, $){

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var pageV1 = window.CQ.CoreComponentsIT.v1.Page;
    var pageV2 = window.CQ.CoreComponentsIT.v2.Page;

    var configuration = "/conf/we-retail";

    /**
     * Test: Check the Advanced Configuration option of a page properties.
     */
    pageV2.tcAdvancedConfigurationPageProperties = function(tcExecuteBeforeTest,tcExecuteAfterTest) {
        return new h.TestCase("Advanced Configuration page property", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })
        // open the new page in the sites
            .navigateTo("/sites.html%testPagePath%")

            .execTestCase(pageV1.openPageProperties)

            /***** Insert information for 'Settings' *****/

            //open the Advanced tab
            .click("coral-tab-label:contains('Advanced')", {delay: 1000})
            //check if the "Advanced" option was selected
            .assert.isTrue(function () {
                return h.find("coral-tab.is-selected coral-tab-label:contains('Advanced')").size() === 1
            })

            //test the configuration settings

            //set the configuration
            .click(".cq-cloudconfig-configpathbrowser .pathbrowser button")
            .assert.visible(".coral-Pathbrowser-picker")
            .click(".coral-Pathbrowser-picker [data-value='" + configuration + "']")
            .assert.isTrue(function () {
                return h.find("a.is-active[data-value='" + configuration + "']")
            })
            .click(".coral-Pathbrowser-picker .js-coral-pathbrowser-confirm")


            /*****  Check if the configuration is saved *****/

            //save the configuration and open again the page property
            .click("coral-buttongroup button:contains('Save & Close')")
            .execTestCase(pageV1.openPageProperties)
            .click("coral-tab-label:contains('Advanced')", {delay: 1000})

            //check the configuration
            .assert.isTrue(function (opts) {
                return h.find("input[name='./cq:conf'] span:contains('" + configuration + "')")
            });
    };
    
    /**
     * v2 specifics
     */
    var pageSelector={
        segmentPath: '/conf/we-retail/settings/wcm/segments'
    };
    var tcExecuteBeforeTest = pageV1.tcExecuteBeforeTest("core/wcm/sandbox/tests/components/test-page-v2");
    var tcExecuteAfterTest = pageV1.tcExecuteAfterTest();

    /**
     * The main test suite for Page component
     */
    new h.TestSuite("Core Components - Page v2", {path:"/apps/core/wcm/test-suites/core-components-it/v2/Page.js",
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(pageV1.tcBasicTitleAndTagsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcBasicTitlesAndDescriptionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcBasicOnOffTimePageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcBasicVanityUrlPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcAdvancedSettingsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcAdvancedTemplatesSettingsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV2.tcAdvancedConfigurationPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcAdvancedAuthenticationPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcAdvancedExportPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcThumbnailPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcSocialMediaPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcCloudServicesPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcPersonalizationPageProperties(pageSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcAddPermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcEditUserGroupPermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcEffectivePermissionsPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcBlueprintPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(pageV1.tcLiveCopyPageProperties(tcExecuteBeforeTest, tcExecuteAfterTest))
    ;

}(hobs, jQuery));
