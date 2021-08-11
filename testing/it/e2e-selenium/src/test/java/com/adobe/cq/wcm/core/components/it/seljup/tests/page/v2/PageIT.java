/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.page.v2;


import com.adobe.cq.wcm.core.components.it.seljup.AdminBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.tests.page.PageTests;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;


@Tag("group4")
public class PageIT extends AdminBaseUITest {

    private PageTests pageTests;
    private String segmentPath = "/conf/we-retail/settings/wcm/segments";
    private String pageRT = "core/wcm/components/page/v2/page";
    /**
     * Before Test Case
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        pageTests = new PageTests();
        pageTests.setupBeforeEach(adminClient, rootPage, pageRT, segmentPath);
    }

    /**
     * After Test Case
     */
    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        pageTests.cleanupAfterEach();
    }

    /**
     * Test: Check the Basic Title and Tags options of a page properties.
     * @throws ClientException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Basic Title and Tags options of a page properties.")
    public void testBasicTitleAndTagsPageProperties() throws ClientException, InterruptedException {
        pageTests.testBasicTitleAndTagsPageProperties();
    }

    /**
     * Test: Check the Basic More titles and descriptions options of a page properties.
     */
    @Test
    @DisplayName("Test: Check the Basic More titles and descriptions options of a page properties.")
    public void testBasicTitlesAndDescriptionsPageProperties() throws InterruptedException {
        pageTests.testBasicTitlesAndDescriptionsPageProperties();
    }

    /**
     * Test: Check the Basic On/Off time options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Basic On/Off time options of a page properties.")
    public void testBasicOnOffTimePageProperties() throws InterruptedException {
        pageTests.testBasicOnOffTimePageProperties();
    }

    /**
     * Test: Check the Basic vanity URL options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Basic vanity URL options of a page properties.")
    public void testBasicVanityUrlPageProperties() throws InterruptedException {
        pageTests.testBasicVanityUrlPageProperties();
    }

    /**
     * Test: Check the Advanced Settings options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Settings options of a page properties.")
    public void testAdvancedSettingsPageProperties() throws InterruptedException {
        pageTests.testAdvancedSettingsPageProperties();
    }

    /**
     * Test: Check the Advanced Templates options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Templates options of a page properties.")
    public void testAdvancedTemplatesSettingsPageProperties() throws InterruptedException {
        pageTests.testAdvancedTemplatesSettingsPageProperties();
    }

    /**
     * Test: Check the Advanced Authentication options of a page properties.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Authentication options of a page properties.")
    public void testAdvancedAuthenticationPageProperties() throws InterruptedException {
        pageTests.testAdvancedAuthenticationPageProperties();
    }

    /**
     * Test: Check the Advanced Export options of a page properties.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Export options of a page properties.")
    public void testAdvancedExportPageProperties() throws InterruptedException {
        pageTests.testAdvancedExportPageProperties();
    }

    /**
     * Test: Check the Advanced Seo options of a page properties
     * @throws InterruptedException
     */
    @Tag("IgnoreOn65")
    @Tag("IgnoreOn64")
    @Test
    @DisplayName("Test: Check the Advanced SEO options of a page properties.")
    public void testAdvancedSeoPageProperties() throws InterruptedException, ClientException {
        pageTests.testAdvancedSeoPageProperties();
    }

    /**
     * Test: Check the Thumbnail options of a page properties.
     */
    @Test
    @DisplayName("Test: Check the Thumbnail options of a page properties.")
    public void testThumbnailPageProperties() {
        pageTests.testThumbnailPageProperties();
    }

    /**
     * Test: Check the Social Media options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Social Media options of a page properties.")
    public void testSocialMediaPageProperties() throws InterruptedException {
        pageTests.testSocialMediaPageProperties();
    }

    /**
     * Test: Check the Cloud Services options of a page properties.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Cloud Services options of a page properties.")
    public void testCloudServicesPageProperties() throws InterruptedException {
        pageTests.testCloudServicesPageProperties();
    }

    /**
     * Test: Check the Personalization options of a page properties.
     *
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Personalization options of a page properties.")
    public void testPersonalizationPageProperties() throws InterruptedException {
        pageTests.testPersonalizationPageProperties();
    }

    /**
     * Test: Check the Add Permissions options of a page properties.
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test: Check the Add Permissions options of a page properties.")
    public void testAddPermissionsPageProperties() throws InterruptedException {
        pageTests.testAddPermissionsPageProperties();
    }

    /**
     * Test: Check the Edit Closed User Group options of a page properties.
     */
    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test: Check the Edit Closed User Group options of a page properties.")
    public void testEditUserGroupPermissionsPageProperties() {
        pageTests.testEditUserGroupPermissionsPageProperties();
    }

    /**
     * Test: Check the Effective Permissions options of a page properties.
     */
    @Test
    @DisplayName("Test: Check the Effective Permissions options of a page properties.")
    public void testEffectivePermissionsPageProperties() throws InterruptedException {
        pageTests.testEffectivePermissionsPageProperties();
    }

    /**
     * Test: Check the Live Copy options of a page properties.
     *
     * @throws ClientException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Live Copy options of a page properties.")
    public void testLiveCopyPageProperties() throws ClientException, InterruptedException {
        pageTests.testLiveCopyPageProperties();
    }

    /**
     * Test: Check the Advanced Configuration option of a page properties.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the Advanced Configuration option of a page properties.")
    public void testdvancedConfigurationPageProperties() throws InterruptedException {
        pageTests.testdvancedConfigurationPageProperties();
    }

    /**
     * Test: Check the Blueprint options of a page properties.
     *
     * @throws ClientException
     * @throws InterruptedException
     */
    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Check the Blueprint options of a page properties.")
    public void testBlueprintPageProperties() throws ClientException, InterruptedException {
       pageTests.testBlueprintPageProperties();
    }
}
