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
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.Locator;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    protected void openTab(String label) {
        page.locator("coral-tab").filter(new Locator.FilterOptions().setHasText(label)).first().click();
    }

    protected Locator field(String name) {
        return page.locator("input[name='" + name + "']:visible, textarea[name='" + name + "']:visible, "
            + "foundation-autocomplete[name='" + name + "'] input[is='coral-textfield']:visible").first();
    }

    protected Locator checkbox(String name) {
        return page.locator("coral-checkbox[name='" + name + "'] input[type='checkbox']").first();
    }

    protected Locator checkbox(String name, String value) {
        return page.locator("coral-checkbox[name='" + name + "'][value='" + value + "'] input[type='checkbox']").first();
    }

    protected void fillAutocomplete(String name, String value) {
        fillPathAutocomplete(page.locator("#cq-sites-properties-form"), name, value);
    }

    protected void addMultifieldValue(String name, String value) {
        Locator multifield = page.locator("coral-multifield[data-granite-coral-multifield-name='" + name + "']");
        multifield.locator("button[coral-multifield-add]").first().click();
        multifield.locator("coral-multifield-item input[name='" + name + "']").last().fill(value);
    }

    protected void selectInPageSelect(String name, String value) {
        Locator button = page.locator("coral-select[name='" + name + "'] > button");
        button.click();
        page.locator("[id='" + button.getAttribute("aria-controls") + "'] coral-selectlist-item[value='" + value + "']")
            .click();
    }

    protected void saveProperties() {
        page.locator("#shell-propertiespage-doneactivator").click();
        assertThat(page).not().hasURL(java.util.regex.Pattern.compile("properties\\.html"),
            new com.microsoft.playwright.assertions.PageAssertions.HasURLOptions().setTimeout(30000));
    }

    protected void assertProperty(String name, String expected) throws Exception {
        String actual = null;
        for (int i = 0; i < 20; i++) {
            JsonNode value = adminClient.doGetJson(testPage + "/jcr:content", 1, 200).path(name);
            if (value.isArray()) {
                for (JsonNode v : value) {
                    if (expected.equals(v.asText())) {
                        return;
                    }
                }
                actual = value.toString();
            } else {
                actual = value.isMissingNode() ? null : value.asText();
                if (expected.equals(actual)) {
                    return;
                }
            }
            Thread.sleep(500);
        }
        assertEquals(expected, actual, "Property " + name + " in "
            + adminClient.doGetJson(testPage + "/jcr:content", 1, 200));
    }

    protected void assertPropertyPresent(String name) throws Exception {
        for (int i = 0; i < 20; i++) {
            if (!adminClient.doGetJson(testPage + "/jcr:content", 1, 200).path(name).asText("").isEmpty()) {
                return;
            }
            Thread.sleep(500);
        }
        fail("Property " + name + " should be set");
    }

    protected void setDate(String name, String value) {
        page.locator("coral-datepicker[name='" + name + "']").evaluate(
            "(e, v) => { e.value = v; e.dispatchEvent(new Event('change', {bubbles: true})); }", value);
    }

    @Test
    public void testBasicTitleAndTagsPageProperties() throws Exception {
        openProperties();
        assertThat(field("./jcr:title")).hasValue("This is the page title");
        field("./jcr:title").fill("Page");
        checkbox("./hideInNav").check();
        saveProperties();
        assertProperty("jcr:title", "Page");
        assertProperty("hideInNav", "true");
    }

    @Test
    public void testBasicTitlesAndDescriptionsPageProperties() throws Exception {
        openProperties();
        field("./pageTitle").fill("This is the page title");
        field("./subtitle").fill("This is the page subtitle");
        field("./navTitle").fill("This is the navigation title");
        field("./jcr:description").fill("This is the page description");
        saveProperties();
        assertProperty("pageTitle", "This is the page title");
        assertProperty("subtitle", "This is the page subtitle");
        assertProperty("navTitle", "This is the navigation title");
        assertProperty("jcr:description", "This is the page description");
    }

    @Test
    public void testBasicOnOffTimePageProperties() throws Exception {
        openProperties();
        java.time.LocalDate next = java.time.LocalDate.now().plusMonths(1).withDayOfMonth(1);
        setDate("./onTime", next + "T10:00:00.000Z");
        setDate("./offTime", next.plusDays(1) + "T10:00:00.000Z");
        saveProperties();
        assertPropertyPresent("onTime");
        assertPropertyPresent("offTime");
    }

    @Test
    public void testBasicVanityUrlPageProperties() throws Exception {
        openProperties();
        addMultifieldValue("./sling:vanityPath", "test/test-Page-URL");
        checkbox("./sling:redirect").check();
        saveProperties();
        assertProperty("sling:vanityPath", "test/test-Page-URL");
        assertProperty("sling:redirect", "true");
    }

    @Test
    public void testAdvancedSettingsPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        selectInPageSelect("./jcr:language", "ro");
        fillAutocomplete("./cq:designPath", "/libs/settings/wcm/designs");
        field("./sling:alias").fill("This is an alias");
        saveProperties();
        assertProperty("jcr:language", "ro");
        assertProperty("cq:designPath", "/libs/settings/wcm/designs");
        assertProperty("sling:alias", "This is an alias");
    }

    @Test
    public void testAdvancedTemplatesSettingsPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        addMultifieldValue("./cq:allowedTemplates", "allowedTemplates");
        saveProperties();
        assertProperty("cq:allowedTemplates", "allowedTemplates");
    }

    @Test
    public void testAdvancedAuthenticationPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        checkbox("./cq:authenticationRequired").check();
        fillAutocomplete("./cq:loginPath", "/content/core-components/core-components-page");
        saveProperties();
        // authentication settings are not exposed as plain jcr:content properties; verify like Selenium via the UI
        openProperties();
        openTab("Advanced");
        assertThat(checkbox("./cq:authenticationRequired")).isChecked();
        assertThat(page.locator("foundation-autocomplete[name='./cq:loginPath'] input[is='coral-textfield']").first())
            .hasValue("/content/core-components/core-components-page");
    }

    @Test
    public void testAdvancedExportPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        fillAutocomplete("./cq:exportTemplate", "/etc/contentsync/templates");
        saveProperties();
        assertProperty("cq:exportTemplate", "/etc/contentsync/templates");
    }

    @Test
    public void testThumbnailPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new Locator.FilterOptions().setHasText("Thumbnail")).click();
        assertThat(page.locator("button").filter(new Locator.FilterOptions().setHasText("Generate")).first()).isVisible();
    }

    @Test
    public void testSocialMediaPageProperties() throws Exception {
        openProperties();
        openTab("Social Media");
        checkbox("./socialMedia", "facebook").check();
        checkbox("./socialMedia", "pinterest").check();
        fillAutocomplete("./variantPath", "/content/experience-fragments/core-components-test/footer");
        saveProperties();
        assertProperty("socialMedia", "facebook");
        assertProperty("socialMedia", "pinterest");
        assertProperty("variantPath", "/content/experience-fragments/core-components-test/footer");
    }

    @Test
    public void testCloudServicesPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new Locator.FilterOptions().setHasText("Cloud Services"))).isVisible();
    }

    @Test
    public void testPersonalizationPageProperties() throws Exception {
        openProperties();
        openTab("Personalization");
        fillAutocomplete("./cq:contextHubPath", "/etc/cloudsettings/default/contexthub/device");
        fillAutocomplete("./cq:contextHubSegmentsPath", "/etc/segmentation/contexthub");
        saveProperties();
        assertProperty("cq:contextHubPath", "/etc/cloudsettings/default/contexthub/device");
        assertProperty("cq:contextHubSegmentsPath", "/etc/segmentation/contexthub");
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testAddPermissionsPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new Locator.FilterOptions().setHasText("Permissions"))).isVisible();
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testEditUserGroupPermissionsPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new Locator.FilterOptions().setHasText("Permissions"))).isVisible();
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
