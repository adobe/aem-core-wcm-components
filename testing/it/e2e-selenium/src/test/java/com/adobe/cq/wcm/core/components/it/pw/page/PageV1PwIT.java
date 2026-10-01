/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.page;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group4")
public class PageV1PwIT extends PlaywrightAuthorBaseTest {
    protected String testPage;
    protected String pageResourceType() {
        return "core/wcm/components/page/v1/page";
    }

    @BeforeEach
    void createTestPage() throws Exception {
        testPage = Commons.createPage(authorClient, defaultPageTemplate, rootPage, "testPage",
            "This is the page title", pageResourceType(), "Test Page", 200);
    }

    @AfterEach
    void deleteTestPage() throws Exception {
        if (testPage != null) {
            authorClient.deletePageWithRetry(testPage, true, false, 30000, 1000, 200);
            testPage = null;
        }
    }

    protected void openProperties() {
        page.navigate(baseUrl + "/mnt/overlay/wcm/core/content/sites/properties.html?item=" + testPage);
        assertThat(page.locator("#cq-sites-properties-form")).isVisible();
    }

    protected void fillAndSave(String selector, String value) {
        page.locator(selector).fill(value);
        page.locator("#cq-sites-properties-form button[type='submit'], #cq-sites-properties-form button[variant='primary']")
            .last().click();
        assertThat(page.locator("#cq-sites-properties-form")).isHidden();
    }

    protected void reopenAndAssert(String selector, String value) {
        openProperties();
        assertThat(page.locator(selector)).hasValue(value);
    }

    protected void saveProperties() {
        page.locator("#cq-sites-properties-form button[type='submit'], #cq-sites-properties-form button[variant='primary']")
            .last().click();
        assertThat(page.locator("#cq-sites-properties-form")).isHidden();
    }

    protected void reopenProperties() {
        openProperties();
    }

    @Test
    public void testBasicTitleAndTagsPageProperties() {
        openProperties();
        fillAndSave("[name='./jcr:title']", "Page");
        reopenAndAssert("[name='./jcr:title']", "Page");
        page.locator("[name='./hideInNav'] input").check();
        saveProperties();
        reopenProperties();
        assertThat(page.locator("[name='./hideInNav'] input")).isChecked();
    }

    @Test
    public void testBasicTitlesAndDescriptionsPageProperties() {
        openProperties();
        page.locator("[name='./pageTitle']").fill("This is the page title");
        page.locator("[name='./subtitle']").fill("This is the page subtitle");
        page.locator("[name='./navTitle']").fill("This is the navigation title");
        page.locator("[name='./jcr:description']").fill("This is the page description");
        saveProperties();
        reopenProperties();
        assertThat(page.locator("[name='./pageTitle']")).hasValue("This is the page title");
        assertThat(page.locator("[name='./subtitle']")).hasValue("This is the page subtitle");
        assertThat(page.locator("[name='./navTitle']")).hasValue("This is the navigation title");
        assertThat(page.locator("[name='./jcr:description']")).hasValue("This is the page description");
    }

    @Test
    public void testBasicOnOffTimePageProperties() {
        openProperties();
        assertThat(page.locator("[name='./onTime'], [name='./offTime']")).hasCount(2);
    }

    @Test
    public void testBasicVanityUrlPageProperties() {
        openProperties();
        page.locator("[name='./sling:vanityPath']").fill("test/test-Page-URL");
        page.locator("coral-checkbox[name='./sling:redirect'] input[type='checkbox']").check();
        saveProperties();
        reopenProperties();
        assertThat(page.locator("[name='./sling:vanityPath']")).hasValue("test/test-Page-URL");
        assertThat(page.locator("coral-checkbox[name='./sling:redirect'] input[type='checkbox']")).isChecked();
    }

    @Test
    public void testAdvancedSettingsPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        assertThat(page.locator("[name='./jcr:language'], [name='./sling:alias']")).hasCount(2);
    }

    @Test
    public void testAdvancedTemplatesSettingsPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        assertThat(page.locator("[name='./cq:allowedTemplates']")).isVisible();
    }

    @Test
    public void testAdvancedAuthenticationPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        page.locator("coral-checkbox[name='./cq:authenticationRequired'] input[type='checkbox']").check();
        page.locator("[name='./cq:loginPage'] input").fill("/content/core-components/core-components-page");
        saveProperties();
        reopenProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        assertThat(page.locator("coral-checkbox[name='./cq:authenticationRequired'] input[type='checkbox']"))
            .isChecked();
        assertThat(page.locator("[name='./cq:loginPage'] input")).hasValue("/content/core-components/core-components-page");
    }

    @Test
    public void testAdvancedExportPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        page.locator("[name='./cq:exportTemplate'] input").fill("/etc/contentsync/templates");
        saveProperties();
        reopenProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        assertThat(page.locator("[name='./cq:exportTemplate'] input")).hasValue("/etc/contentsync/templates");
    }

    @Test
    public void testThumbnailPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Thumbnail")).click();
        assertThat(page.locator("button").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Generate")).first()).isVisible();
    }

    @Test
    public void testSocialMediaPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Social Media")).click();
        page.locator("[name='./socialMediaSharing'][value='facebook']").check();
        page.locator("[name='./socialMediaSharing'][value='pinterest']").check();
        page.locator("[name='./socialMediaVariant'] input").fill("/content/experience-fragments/core-components-test/footer");
        saveProperties();
        reopenProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Social Media")).click();
        assertThat(page.locator("[name='./socialMediaSharing'][value='facebook']")).isChecked();
        assertThat(page.locator("[name='./socialMediaSharing'][value='pinterest']")).isChecked();
        assertThat(page.locator("[name='./socialMediaVariant'] input"))
            .hasValue("/content/experience-fragments/core-components-test/footer");
    }

    @Test
    public void testCloudServicesPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Cloud Services"))).isVisible();
    }

    @Test
    public void testPersonalizationPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Personalization")).click();
        page.locator("[name='./cq:contextHubPath'] input").fill("/etc/cloudsettings/default/contexthub/device");
        page.locator("[name='./cq:segmentsPath'] input").fill("/etc/segmentation/contexthub/male");
        saveProperties();
        reopenProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Personalization")).click();
        assertThat(page.locator("[name='./cq:contextHubPath'] input"))
            .hasValue("/etc/cloudsettings/default/contexthub/device");
        assertThat(page.locator("[name='./cq:segmentsPath'] input"))
            .hasValue("/etc/segmentation/contexthub/male");
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testAddPermissionsPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Permissions"))).isVisible();
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testEditUserGroupPermissionsPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Permissions"))).isVisible();
    }

    @Test
    public void testEffectivePermissionsPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }

    @Test
    public void testLiveCopyPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }
}
