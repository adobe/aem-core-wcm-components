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

package com.adobe.cq.wcm.core.components.it.seljup.tests.page;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.selenium.pageobject.cq.sites.PropertiesPage;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralMultiField;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.testing.selenium.pagewidgets.cq.RolloutDialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.AdvancedTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.BlueprintTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.CloudServicesTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.ImageTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.LiveCopyTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.PermissionsTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.SocialMediaTab;
import com.adobe.cq.testing.selenium.pagewidgets.cq.tabs.ThumbnailTab;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.page.v1.Page;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;

import static com.adobe.cq.testing.selenium.utils.ElementUtils.clickableClick;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageTests {
    private static String tag1 = "testTag1";
    private static String tag2 = "testTag2";
    private static String tag1Path = "default/testtag1";
    private static String tag2Path = "default/testtag2";
    private static String navTitle = "This is the navigation title";
    private static String subtitle = "This is the page subtitle";
    private static String description = "This is the page description";
    private static String vanityURL = "test/test-Page-URL";
    //value for Romanian language
    private static String language = "ro";
    private static String languageName = "Romanian";
    private static String design = "/libs/settings/wcm/designs";
    private static String alias  = "This is an alias";
    private static String allowedTemplate = "allowedTemplates";
    private static String loginPage = "/content/core-components/core-components-page";
    private static String exportConfiguration = "/etc/contentsync/templates";
    private static String variantPath = "/content/experience-fragments/core-components-test/footer";
    private static String cloudServiceConfig = "/etc/cloudservices/proxy";
    private static String contextHubPath = "/etc/cloudsettings/default/contexthub/device";
    private static String userPrincipalName = "corecomp";
    private static String userName = "CoreComponent Test";
    private static String configuration = "/conf/core-components";

    protected static String pageTitle = "This is the page title";
    protected String testPage;
    protected Page page;
    private String rootPage;

    protected String segmentPath;

    CQClient adminClient;


    private void setupResources(String segmentPath, CQClient adminClient, String rootPage) {
        this.segmentPath = segmentPath;
        this.adminClient = adminClient;
        this.rootPage = rootPage;
    }

    public void setupBeforeEach(CQClient adminClient, String rootPage, String pageRT, String segmentPath) throws ClientException {
        // create the test page
        testPage = Commons.createPage(adminClient, Commons.template, rootPage, "testPage", pageTitle, pageRT, "Test Page", 200);
        setupResources(segmentPath, adminClient, rootPage);
        page = new Page();
    }

    public void cleanupAfterEach() throws ClientException, InterruptedException {
        adminClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    public void testBasicTitleAndTagsPageProperties() throws ClientException, InterruptedException {
        // create tags
        String tag1FullPath = Commons.addTag(adminClient, tag1);
        String tag2FullPath = Commons.addTag(adminClient, tag2);

        // open the properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Basic tab
        PropertiesPage.Tabs.Basic basicTab = propertiesPage.tabs().basic();

        // check the page title
        assertTrue(basicTab.title().getValue().equals(pageTitle), "Title value should be set as page title");

        // change the page title
        basicTab.title().setValue("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        // add two tags
        Commons.selectInTags("[name='./cq:tags']", tag1Path);
        Commons.selectInTags("[name='./cq:tags']", tag2Path);

        // set the Hide in Navigation
        basicTab.hideInNav().click();

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();

        // check the page title
        assertTrue(basicTab.title().getValue().equals("Page"), "Title value should be set as new value: Page");
        // check if 'Hide in Navigation' is checked
        assertTrue(basicTab.hideInNav().isChecked(), "HideNav should be checked");
        // check if the tags were saved
        assertTrue(basicTab.isTagPresent(tag1),"Tag " + tag1 + " should br present");
        assertTrue(basicTab.isTagPresent(tag2),"Tag " + tag2 + " should br present");

        adminClient.deletePath("/content/cq:tags/default/" + tag1FullPath, HttpStatus.SC_OK);
        adminClient.deletePath("/content/cq:tags/default/" + tag2FullPath, HttpStatus.SC_OK);
    }

    public void testBasicTitlesAndDescriptionsPageProperties() throws InterruptedException {
        // open the properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Basic tab
        PropertiesPage.Tabs.Basic basicTab = propertiesPage.tabs().basic();
        //Edit basic page properties
        basicTab.pageTitle().setValue(pageTitle);
        basicTab.subtitle().setValue(subtitle);
        basicTab.navTitle().setValue(navTitle);
        basicTab.description().setValue(description);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();

        //Check if edited properties are saved
        assertTrue(basicTab.pageTitle().getValue().equals(pageTitle), "Page Title should be set to " + pageTitle);
        assertTrue(basicTab.subtitle().getValue().equals(subtitle), "Subtitle should be set to " + subtitle);
        assertTrue(basicTab.navTitle().getValue().equals(navTitle), "NavTitle should be set to " + navTitle);
        assertTrue(basicTab.description().getValue().equals(description), "Description should be set to " + description);
    }

    public void testBasicOnOffTimePageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        //set onTime to first day next month
        page.setOnTime();
        //set offTime to second day next month
        page.setOffTime();

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();

        // Check if on time and off time are correctly set
        assertTrue(!page.getOnTime().isEmpty(), "OnTime should be set");
        assertTrue(!page.getOffTime().isEmpty(), "OffTime should be set");
    }

    public void testBasicVanityUrlPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        PropertiesPage.Tabs.Basic basicTab = propertiesPage.tabs().basic();

        // add a vanity url
        CoralMultiField.MultiFieldItem item = page.addVanityUrl(basicTab, vanityURL);
        // delete the vanity url
        page.deleteVanityUrl(item);
        // add a vanity url
        item = page.addVanityUrl(basicTab, vanityURL);
        // set the Redirect Vanity URL
        basicTab.slingRedirect().click();

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();

        // check if the vanity url was saved
        assertTrue(page.getVanityUrlValue(0).equals(vanityURL), "Vanity URL should be set to " + vanityURL);
        // check if 'Redirect Vanity URL' is checked
        assertTrue(basicTab.slingRedirect().isChecked(), "Sling Redirect should be checked");

    }

    public void testAdvancedSettingsPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // open the Advanced tab
        AdvancedTab advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // set the language
        advancedTab.selectLanguage(language);
        // set the desigh path
        page.setDesignPath(design);
        // set the alias
        advancedTab.slingAlias().sendKeys(alias);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the language
        assertTrue(advancedTab.getLanguageSelected().equals(languageName),"Language " + languageName + " should be selected");
        // check the design
        assertTrue(page.getDesignPath().equals(design), "Design path should be set to " + design);
        // check the alias
        assertTrue(advancedTab.slingAlias().getValue().equals(alias), "Sling Alias should be set to " + alias);
    }

    public void testAdvancedTemplatesSettingsPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Advanced tab
        AdvancedTab advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // add the allowed template
        CoralMultiField.MultiFieldItem item = page.addTemplate(advancedTab, allowedTemplate);
        // detele the allowed template
        page.deleteTemplate(item);
        // add again the allowed template
        item = page.addTemplate(advancedTab, allowedTemplate);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the saved template
        assertTrue(page.getAllowTemplate(0).equals(allowedTemplate), "Allowed template should be set to " + allowedTemplate);
    }

    public void testAdvancedAuthenticationPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Advanced tab
        AdvancedTab advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // test the authentication requirement
        advancedTab.authenticationRequired().click();
        page.setLoginPage(loginPage);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the Enable check
        assertTrue(advancedTab.authenticationRequired().isChecked(), "Authentication required should be checked");
        // check the login page
        assertTrue(page.getLoginPath().equals(loginPage), "Login page should be set");
    }

    public void testAdvancedExportPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Advanced tab
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // tests for the export options
        page.setExportTemplate(exportConfiguration);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the Export Configuration
        assertTrue(page.getExportTemplate().equals(exportConfiguration), "Export Templates should be set to " + exportConfiguration);
    }

    public void testAdvancedSeoPageProperties() throws InterruptedException, ClientException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Advanced tab
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // tests for the SEO options
        page.setRobotsTags("index", "follow");
        page.setGenerateSitemap(true);
        page.setCanonicalUrl(testPage);

        // save the configuration and open again the page properties
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the SEO Configuration
        assertArrayEquals(new String[] {"index", "follow"}, page.getRobotsTags());
        assertTrue(page.getGenerateSitemap());
        assertEquals(testPage, page.getCanonicalUrl());

        // validate the actual persisted values
        JsonNode content = adminClient.doGetJson(testPage + "/_jcr_content", 1, HttpStatus.SC_OK);
        JsonNode robotsTags = content.get("cq:robotsTags");
        assertNotNull(robotsTags);
        assertTrue(robotsTags.isArray());
        assertEquals(2, robotsTags.size());
        assertEquals("index", robotsTags.get(0).asText());
        assertEquals("follow", robotsTags.get(1).asText());
        JsonNode sitemapRoot = content.get("sling:sitemapRoot");
        assertNotNull(sitemapRoot);
        assertEquals("true", sitemapRoot);
        JsonNode canonicalUrl = content.get("cq:canonicalUrl");
        assertNotNull(canonicalUrl);
        assertEquals(testPage, canonicalUrl.asText());
    }

    public void testThumbnailPageProperties() {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Thumbnail tab
        ThumbnailTab thumbnailTab = propertiesPage.clickTab("thumbnail", ThumbnailTab.class);

        //Generate thumbnail preview
        thumbnailTab.generateThumbnailPreview();
        //Revert button should be visible
        assertTrue(thumbnailTab.getRevert().isDisplayed(), "Revert button should be visible");
        //Revert the thumbnail preview
        thumbnailTab.revertThumbnailPreview();
        //Revert button should disappear
        assertTrue(!thumbnailTab.getRevert().isDisplayed(), "Revert button should not be visible");
    }

    public void testImagePageProperties() {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // open the Thumbnail tab
        ImageTab imageTab = propertiesPage.clickTab("images", ImageTab.class);

        //Generate thumbnail preview
        imageTab.generateThumbnailPreview();
        //Revert button should be visible
        assertTrue(imageTab.getRevert().isDisplayed(), "Revert button should be visible");
        //Revert the thumbnail preview
        imageTab.revertThumbnailPreview();
        //Revert button should disappear
        assertTrue(!imageTab.getRevert().isDisplayed(), "Revert button should not be visible");
    }

    public void testSocialMediaPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        //Open the Social Media tab
        SocialMediaTab socialMediaTab = propertiesPage.clickTab("social media", SocialMediaTab.class);

        // test social media sharing
        socialMediaTab.socialMediaSharing("facebook").click();
        socialMediaTab.socialMediaSharing("pinterest").click();
        page.setVariantPath(variantPath);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        socialMediaTab = propertiesPage.clickTab("social media", SocialMediaTab.class);

        // check if facebook is checked
        assertTrue(socialMediaTab.socialMediaSharing("facebook").isChecked(), "Social media sharing should be enabled for Facebook");
        // check if pinterest is checked
        assertTrue(socialMediaTab.socialMediaSharing("pinterest").isChecked(), "Social media sharing should be enabled for Pinterest");
        // check if variant path is set correctly
        assertTrue(page.getVariantPath().equals(variantPath), "Variant path should be set to " + variantPath);
    }

    public void testCloudServicesPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // Open Cloud Service tab
        CloudServicesTab cloudServicesTab = propertiesPage.clickTab("cloud services", CloudServicesTab.class);

        // Add cloud configuration
        cloudServicesTab.addCloudConfiguration(cloudServiceConfig);

        // Delete the added cloud configuration
        cloudServicesTab.deleteCloudConfiguration();

        // Add cloud configuration again
        cloudServicesTab.addCloudConfiguration(cloudServiceConfig);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        propertiesPage.clickTab("cloud services", CloudServicesTab.class);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        // Check is cloud configuration is set
        assertTrue(page.isCloudServiceConfigAdded(), "Cloud Service Config should be set");
    }

    public void testPersonalizationPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // Open Personalization tab
        clickableClick(propertiesPage.getCoralTabs().filter(Condition.matchText("Personalization")).first());

        // set the contextHub path
        page.setContextHubPath(contextHubPath);
        // set the segments path
        page.setContextHubSegmentsPath(segmentPath);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        clickableClick(propertiesPage.getCoralTabs().filter(Condition.matchText("Personalization")).first());

        // check the contextHub path
        assertTrue(page.getContextHubPath().equals(contextHubPath), "ContextHub path should be set to " + contextHubPath);
        // check the segments path
        assertTrue(page.getContextHubSegmentsPath().equals(segmentPath), "ContextHub Segments path should be set to " + segmentPath);
    }

    public void testAddPermissionsPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // Open Permission tab
        PermissionsTab permissions = propertiesPage.clickTab("permissions", PermissionsTab.class);

        final ElementsCollection currentPermissionsList = permissions.permissionsList();
        // Check no permission set
        currentPermissionsList.shouldHave(CollectionCondition.size(0));

        // Add permission
        final PermissionsTab.AddPermissionsDialog addPermissionsDialog = permissions.addPermission();
        addPermissionsDialog.waitVisible();
        final AutoCompleteField authList = addPermissionsDialog.authorizableList();
        authList.sendKeys(userPrincipalName);
        authList.buttonlist().selectByValue(userPrincipalName);
        addPermissionsDialog.read().click();
        addPermissionsDialog.modify().click();
        addPermissionsDialog.delete().click();
        addPermissionsDialog.clickPrimary();

        // Check the permission set
        currentPermissionsList.shouldHave(CollectionCondition.size(1));
        currentPermissionsList.shouldHave(CollectionCondition.texts(userName));
        assertTrue(permissions.isReadPermissionGranted(userName), "Read permission has been given, should be checked");
        assertTrue(permissions.isDeletePermissionGranted(userName), "Delete permission has been given, should be checked");
        assertTrue(permissions.isModifyPermissionGranted(userName), "Modify permission has been given, should be checked");
        assertTrue(!permissions.isReplicatePermissionGranted(userName), "Replicate permission has not been given, should not be checked");
        assertTrue(!permissions.isCreatePermissionGranted(userName), "Create permission has not been given, should not  be checked");

        // Edit the permission
        final PermissionsTab.EditPermissionDialog editPermissionDialog = permissions.editPermission(userName);
        editPermissionDialog.replicate().click();
        editPermissionDialog.clickPrimary();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(permissions.isReplicatePermissionGranted(userName), "Replicate permission has been given, should be checked");

        // Delete the permission
        permissions.deleteUserPermission(userName);
        currentPermissionsList.shouldHave(CollectionCondition.size(0));
    }

    public void testEditUserGroupPermissionsPageProperties() {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // Open Permission tab
        PermissionsTab permissions = propertiesPage.clickTab("permissions", PermissionsTab.class);

        // Check current CUG list is empty
        final ElementsCollection currentCUGList = permissions.cugList();
        currentCUGList.shouldHave(CollectionCondition.size(0));

        // Add CUG permission
        final PermissionsTab.EditCUGDialog editCUG = permissions.editCUG();
        editCUG.waitVisible();
        final AutoCompleteField cugList = editCUG.cugFinder();
        cugList.sendKeys("corecomp");
        cugList.buttonlist().selectByValue("corecomp");
        editCUG.clickPrimary();

        // Check the added CUG permission
        currentCUGList.shouldHave(CollectionCondition.size(1));
        currentCUGList.shouldHave(CollectionCondition.texts("CoreComponent Test"));
    }

    public void testEffectivePermissionsPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();
        // Open Permission tab
        PermissionsTab permissions = propertiesPage.clickTab("permissions", PermissionsTab.class);

        PermissionsTab.EffectivePermissionDialog effectivePermissionDialog = permissions.openEffectivePermissions();
        //Wait for Dialog to open
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(effectivePermissionDialog.isVisible(), "Effective Permission dialog should be open");
        //Close Effective Permission Dialog
        effectivePermissionDialog.close();
    }

    public void testLiveCopyPageProperties() throws ClientException, InterruptedException {
        // create the live copy page, store page path in 'testLiveCopyPagePath'
        String testLiveCopyPagePath = Commons.createLiveCopy(adminClient, testPage, rootPage, "testLiveCopy", "testLiveCopy", 200);

        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testLiveCopyPagePath);
        propertiesPage.open();
        // Open Live Copy tab
        LiveCopyTab liveCopyTab = propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Synchronize button
        Dialog liveCopySync = liveCopyTab.synchronize();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(liveCopySync.isVisible(), "livecopy sync dialog should be visible");
        liveCopySync.clickPrimary();

        // Open Live Copy tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Reset button
        Dialog revert = liveCopyTab.reset();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(revert.isVisible(), "revert dialog should be visible");
        revert.clickWarning();

        // Open Live Copy tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Suspend without children button
        Dialog suspendWithoutChildrenDialog = liveCopyTab.suspendWithoutChild();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(suspendWithoutChildrenDialog.isVisible(), "Suspend livecopy without children dialog should be visible");
        suspendWithoutChildrenDialog.clickWarning();

        // Open Live Copy tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Resume button
        Dialog resumeDialog = liveCopyTab.resume();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(resumeDialog.isVisible(), "resume livecopy dialog should be visible");
        resumeDialog.clickWarning();

        // Open Live Copy tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Suspend without children button
        Dialog suspendWithChildrenDialog = liveCopyTab.suspendWithoutChild();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(suspendWithChildrenDialog.isVisible(), "Suspend livecopy with children dialog should be visible");
        suspendWithChildrenDialog.clickWarning();

        // Open Live Copy tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("live copy", LiveCopyTab.class);

        // check the Detach button
        Dialog detachDialog = liveCopyTab.detach();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(detachDialog.isVisible(), "detach livecopy dialog should be visible");
        detachDialog.clickWarning();

        //Delete the created livecopy page
        adminClient.deletePageWithRetry(testLiveCopyPagePath, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);

    }

    public void testdvancedConfigurationPageProperties() throws InterruptedException {
        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // open the Advanced tab
        AdvancedTab advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // Uncheck the config inheritance
        CoralCheckbox advanceConfigInheritance = advancedTab.advanceConfigInheritance();
        if(advanceConfigInheritance.isChecked()) {
            advanceConfigInheritance.click();
        }
        // set the configuration
        page.setAdvanceConfig(configuration);

        // save the configuration and open again the page property
        propertiesPage.saveAndClose();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.open();
        advancedTab = propertiesPage.clickTab("advanced", AdvancedTab.class);

        // check the configuration
        assertTrue(page.getAdvanceConfig().equals(configuration), "Advance configuration should be set");
    }

    public void testBlueprintPageProperties() throws ClientException, InterruptedException {
        // create the live copy page, store page path in 'testLiveCopyPagePath'
        String testLiveCopyPagePath = Commons.createLiveCopy(adminClient, testPage, rootPage, "testLiveCopy", "testLiveCopy", 200);

        // Open properties page
        PropertiesPage propertiesPage = new PropertiesPage(testPage);
        propertiesPage.open();

        // open the Blueprint tab
        BlueprintTab blueprintTab = propertiesPage.clickTab("blueprint", BlueprintTab.class);

        RolloutDialog rolloutDialog = blueprintTab.rollout();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        // check livecopy is present for rollout
        assertTrue(rolloutDialog.numberOfLiveCopies() == 1, "There should be 1 livecopy");
        assertTrue(rolloutDialog.isLiveCopySelected(testLiveCopyPagePath), "Livecopy should be selected");

        // Check cancel rollout
        rolloutDialog.close();

        // Open Blueprint tab
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        propertiesPage.clickTab("blueprint", BlueprintTab.class);

        rolloutDialog = blueprintTab.rollout();

        // check rollout now
        rolloutDialog.rolloutNow();

        //Delete the created livecopy page
        adminClient.deletePageWithRetry(testLiveCopyPagePath, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }
}
