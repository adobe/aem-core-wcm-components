/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.page;

import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.PlaywrightAuthorBaseTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.PageAssertions;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.RequestOptions;
import com.adobe.cq.wcm.core.components.it.util.Commons;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("playwright-group4")
public class PageV1PwIT extends PlaywrightAuthorBaseTest {
    private static final String TAG1 = "testTag1";
    private static final String TAG2 = "testTag2";
    private static final String TAG1_PATH = "default/testtag1";
    private static final String TAG2_PATH = "default/testtag2";
    private static final String NAV_TITLE = "This is the navigation title";
    private static final String SUBTITLE = "This is the page subtitle";
    private static final String DESCRIPTION = "This is the page description";
    private static final String PAGE_TITLE = "This is the page title";
    private static final String VANITY_URL = "test/test-Page-URL";
    private static final String LANGUAGE = "ro";
    private static final String LANGUAGE_NAME = "Romanian";
    private static final String DESIGN = "/libs/settings/wcm/designs";
    private static final String ALIAS = "This is an alias";
    private static final String ALLOWED_TEMPLATE = "allowedTemplates";
    private static final String LOGIN_PAGE = "/content/core-components/core-components-page";
    private static final String EXPORT_CONFIGURATION = "/etc/contentsync/templates";
    private static final String VARIANT_PATH = "/content/experience-fragments/core-components-test/footer";
    private static final String CLOUD_SERVICE_CONFIG = "/etc/cloudservices/proxy";
    private static final String CLOUD_CONFIG_SELECT = "coral-select[placeholder='Add Configuration']";
    private static final String CLOUD_CONFIG_TAG = "button[data-title='Cloud Proxy Configuration']";
    private static final String CONTEXT_HUB_PATH = "/etc/cloudsettings/default/contexthub/device";
    private static final String USER_PRINCIPAL_NAME = "corecomp";
    private static final String USER_NAME = "CoreComponent Test";
    private static final String CONFIGURATION = "/conf/core-components";

    private static final String USER_PERMISSIONS_ROWS =
        "table.js-cq-sites-UserGroup-permissions tr.js-cq-sites-PermissionsProperties-row";
    private static final String CUG_ROWS =
        "table.js-cq-sites-ClosedUserGroup-permissions tr.js-cq-sites-PermissionsProperties-row";
    private static final String ADD_PERMISSIONS_DIALOG = "coral-dialog.js-cq-sites-CreatePermissionsDialog[open]";
    private static final String EDIT_PERMISSIONS_DIALOG = "coral-dialog.js-cq-sites-EditPermissionsDialog[open]";
    private static final String CUG_DIALOG = "coral-dialog.js-cq-sites-CUGPermissionsDialog[open]";
    private static final String EFFECTIVE_PERMISSIONS_DIALOG =
        "coral-dialog.js-cq-sites-EffectivePermissionsDialog[open]";

    protected String testPage;
    protected String liveCopyPage;

    protected String pageResourceType() {
        return "core/wcm/components/page/v1/page";
    }

    protected String segmentPath() {
        return "/etc/segmentation/contexthub/male";
    }

    @BeforeEach
    void createTestPage() throws Exception {
        testPage = Commons.createPage(adminClient, defaultPageTemplate, rootPage, "testPage",
            PAGE_TITLE, pageResourceType(), "Test Page", 200);
        // like the Selenium tests, the properties pages are driven as admin (permissions, live copy, ...)
        int status = context.request().post(baseUrl + "/libs/granite/core/content/login.html/j_security_check",
            RequestOptions.create()
                .setHeader("Referer", baseUrl + "/libs/granite/core/content/login.html")
                .setForm(FormData.create()
                    .set("j_username", adminClient.getUser())
                    .set("j_password", adminClient.getPassword())
                    .set("j_validate", "true")
                    .set("_charset_", "utf-8"))).status();
        assertEquals(200, status, "Admin login failed");
    }

    @AfterEach
    void deleteTestPage() throws Exception {
        if (liveCopyPage != null) {
            adminClient.deletePageWithRetry(liveCopyPage, true, false, 30000, 1000, 200);
            liveCopyPage = null;
        }
        if (testPage != null) {
            adminClient.deletePageWithRetry(testPage, true, false, 30000, 1000, 200);
            testPage = null;
        }
    }

    protected void openProperties() {
        openProperties(testPage);
    }

    protected void openProperties(String pagePath) {
        page.navigate(baseUrl + "/mnt/overlay/wcm/core/content/sites/properties.html?item=" + pagePath);
        assertThat(page.locator("#cq-sites-properties-form")).isVisible();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    protected void openTab(String label) {
        page.locator("coral-tab").filter(new Locator.FilterOptions().setHasText(label)).first().click();
    }

    protected Locator field(String name) {
        return page.locator("input[name='" + name + "']:visible, textarea[name='" + name + "']:visible, "
            + "foundation-autocomplete[name='" + name + "'] input[is='coral-textfield']:visible").first();
    }

    protected Locator autocompleteInput(String name) {
        return page.locator("foundation-autocomplete[name='" + name + "'] input[is='coral-textfield']").first();
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

    protected Locator multifieldItems(String name) {
        return page.locator("coral-multifield[data-granite-coral-multifield-name='" + name + "'] coral-multifield-item");
    }

    protected Locator addMultifieldValue(String name, String value) {
        Locator multifield = page.locator("coral-multifield[data-granite-coral-multifield-name='" + name + "']");
        Locator items = multifieldItems(name);
        int before = items.count();
        multifield.locator("button[coral-multifield-add]").first().click();
        assertThat(items).hasCount(before + 1);
        Locator item = items.last();
        item.locator("input[name='" + name + "']").first().fill(value);
        return item;
    }

    protected void removeMultifieldItem(String name, Locator item) {
        int before = multifieldItems(name).count();
        item.locator("button[handle='remove']").click();
        assertThat(multifieldItems(name)).hasCount(before - 1);
    }

    protected void selectInPageSelect(String name, String value) {
        selectInSelect("coral-select[name='" + name + "']", value);
    }

    protected void selectInSelect(String selector, String value) {
        Locator button = page.locator(selector + " > button");
        button.click();
        page.locator("[id='" + button.getAttribute("aria-controls") + "'] coral-selectlist-item[value='" + value + "']")
            .click();
    }

    /** Picks a resource below {@code prefix} through the selection dialog of a foundation-autocomplete. */
    protected void pickInPropertiesPicker(String prefix, String name, String relativePath) {
        Locator autocomplete = page.locator("foundation-autocomplete[name='" + name + "']").first();
        autocomplete.locator("button").first().click();
        Locator picker = page.locator("coral-dialog[open]").last();
        assertThat(picker).isVisible();
        String[] segments = relativePath.split("/");
        String currentPath = prefix;
        for (int i = 0; i < segments.length - 1; i++) {
            currentPath += "/" + segments[i];
            Locator folder = picker.locator("[data-foundation-collection-item-id='" + currentPath + "']");
            assertThat(folder).isVisible();
            folder.click();
        }
        currentPath += "/" + segments[segments.length - 1];
        Locator item = picker.locator("[data-foundation-collection-item-id='" + currentPath + "']");
        assertThat(item).isVisible();
        Locator itemCheckbox = item.locator("coral-checkbox");
        if (itemCheckbox.count() > 0 && itemCheckbox.first().isVisible()) {
            itemCheckbox.first().click();
        } else {
            item.locator("coral-columnview-item-thumbnail").click();
        }
        picker.locator("button.granite-pickerdialog-submit[is='coral-button']").click();
        assertThat(picker).isHidden();
    }

    protected void saveProperties() {
        page.locator("#shell-propertiespage-doneactivator").click();
        assertThat(page).not().hasURL(Pattern.compile("properties\\.html"),
            new PageAssertions.HasURLOptions().setTimeout(30000));
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

    protected void assertPropertyStartsWith(String name, String prefix) throws Exception {
        Commons.waitForJson(adminClient, testPage + "/jcr:content", 1, json -> {
            JsonNode value = json.path(name);
            for (JsonNode v : value.isArray() ? value : java.util.List.of(value)) {
                if (v.asText().startsWith(prefix)) {
                    return true;
                }
            }
            return false;
        });
    }

    // the cancelled flag may live on jcr:content or on its live sync child nodes depending on the version
    private static boolean isCancelled(JsonNode json) {
        for (JsonNode mixin : json.path("jcr:mixinTypes")) {
            if ("cq:LiveSyncCancelled".equals(mixin.asText())) {
                return true;
            }
        }
        for (JsonNode v : json.findValues("cq:isCancelled")) {
            if (v.asBoolean(false)) {
                return true;
            }
        }
        return false;
    }

    protected void assertArrayProperty(String name, String... expected) throws Exception {
        JsonNode content = Commons.waitForJson(adminClient, testPage + "/jcr:content", 1, json -> {
            JsonNode value = json.path(name);
            if (!value.isArray() || value.size() != expected.length) {
                return false;
            }
            for (int i = 0; i < expected.length; i++) {
                if (!expected[i].equals(value.get(i).asText())) {
                    return false;
                }
            }
            return true;
        });
        assertTrue(content.path(name).isArray(), "Property " + name + " should be an array");
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

    protected void pickDate(String name, int dayOfNextMonth) {
        page.locator("coral-datepicker[name='" + name + "'] button coral-icon[icon='calendar']").click();
        Locator popover = page.locator("coral-popover[open]").last();
        assertThat(popover).isVisible();
        popover.locator("button[handle='next']").click();
        Locator days = popover.locator("a.is-currentMonth, td.is-currentMonth");
        assertThat(days.first()).isVisible();
        Locator linkDays = popover.locator("a.is-currentMonth");
        (linkDays.count() > 0 ? linkDays : popover.locator("td.is-currentMonth")).nth(dayOfNextMonth - 1).click();
    }

    protected void assertThumbnailGenerateAndRevert() {
        Locator defaultThumbnail = page.locator("img.cq-wcm-pagethumbnail-image[alt='Page thumbnail']");
        Locator revert = page.locator("button[type='reset']").first();
        page.locator(".cq-wcm-pagethumbnail-activator").click();
        // generating the preview replaces the default thumbnail
        assertThat(defaultThumbnail).isHidden();
        assertThat(revert).isVisible();
        revert.click();
        assertThat(defaultThumbnail).isVisible();
        assertThat(revert).isHidden();
    }

    @Test
    public void testBasicTitleAndTagsPageProperties() throws Exception {
        Commons.addTag(adminClient, TAG1);
        Commons.addTag(adminClient, TAG2);
        try {
            openProperties();
            assertThat(field("./jcr:title")).hasValue(PAGE_TITLE);
            field("./jcr:title").fill("Page");
            pickInPropertiesPicker("/content/cq:tags", "./cq:tags", TAG1_PATH);
            pickInPropertiesPicker("/content/cq:tags", "./cq:tags", TAG2_PATH);
            checkbox("./hideInNav").check();
            saveProperties();

            openProperties();
            assertThat(field("./jcr:title")).hasValue("Page");
            assertThat(checkbox("./hideInNav")).isChecked();
            Locator tags = page.locator("coral-taglist[name='./cq:tags'] coral-tag");
            assertThat(tags.filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + TAG1 + "\\s*$"))))
                .hasCount(1);
            assertThat(tags.filter(new Locator.FilterOptions().setHasText(Pattern.compile("^\\s*" + TAG2 + "\\s*$"))))
                .hasCount(1);
            assertProperty("jcr:title", "Page");
            assertProperty("hideInNav", "true");
            assertProperty("cq:tags", "testtag1");
            assertProperty("cq:tags", "testtag2");
        } finally {
            adminClient.deletePath("/content/cq:tags/default/testtag1", 200, 404);
            adminClient.deletePath("/content/cq:tags/default/testtag2", 200, 404);
        }
    }

    @Test
    public void testBasicTitlesAndDescriptionsPageProperties() throws Exception {
        openProperties();
        field("./pageTitle").fill(PAGE_TITLE);
        field("./subtitle").fill(SUBTITLE);
        field("./navTitle").fill(NAV_TITLE);
        field("./jcr:description").fill(DESCRIPTION);
        saveProperties();

        openProperties();
        assertThat(field("./pageTitle")).hasValue(PAGE_TITLE);
        assertThat(field("./subtitle")).hasValue(SUBTITLE);
        assertThat(field("./navTitle")).hasValue(NAV_TITLE);
        assertThat(field("./jcr:description")).hasValue(DESCRIPTION);
        assertProperty("pageTitle", PAGE_TITLE);
        assertProperty("subtitle", SUBTITLE);
        assertProperty("navTitle", NAV_TITLE);
        assertProperty("jcr:description", DESCRIPTION);
    }

    @Test
    public void testBasicOnOffTimePageProperties() throws Exception {
        openProperties();
        // first day of next month, second day of next month
        pickDate("./onTime", 1);
        pickDate("./offTime", 2);
        saveProperties();

        openProperties();
        Pattern notEmpty = Pattern.compile(".+");
        assertThat(page.locator("input[name='./onTime']").first()).hasValue(notEmpty);
        assertThat(page.locator("input[name='./offTime']").first()).hasValue(notEmpty);
        assertPropertyPresent("onTime");
        assertPropertyPresent("offTime");
    }

    @Test
    public void testBasicVanityUrlPageProperties() throws Exception {
        openProperties();
        Locator item = addMultifieldValue("./sling:vanityPath", VANITY_URL);
        removeMultifieldItem("./sling:vanityPath", item);
        addMultifieldValue("./sling:vanityPath", VANITY_URL);
        checkbox("./sling:redirect").check();
        saveProperties();

        openProperties();
        assertThat(multifieldItems("./sling:vanityPath")).hasCount(1);
        assertThat(page.locator("input[name='./sling:vanityPath']").first()).hasValue(VANITY_URL);
        assertThat(checkbox("./sling:redirect")).isChecked();
        assertProperty("sling:vanityPath", VANITY_URL);
        assertProperty("sling:redirect", "true");
    }

    @Test
    public void testAdvancedSettingsPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        selectInPageSelect("./jcr:language", LANGUAGE);
        fillAutocomplete("./cq:designPath", DESIGN);
        field("./sling:alias").fill(ALIAS);
        saveProperties();

        openProperties();
        openTab("Advanced");
        assertThat(page.locator("coral-select[name='./jcr:language'] [handle='label']")).hasText(LANGUAGE_NAME);
        assertThat(autocompleteInput("./cq:designPath")).hasValue(DESIGN);
        assertThat(field("./sling:alias")).hasValue(ALIAS);
        assertProperty("jcr:language", LANGUAGE);
        assertProperty("cq:designPath", DESIGN);
        assertProperty("sling:alias", ALIAS);
    }

    @Test
    public void testAdvancedTemplatesSettingsPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        Locator item = addMultifieldValue("./cq:allowedTemplates", ALLOWED_TEMPLATE);
        removeMultifieldItem("./cq:allowedTemplates", item);
        addMultifieldValue("./cq:allowedTemplates", ALLOWED_TEMPLATE);
        saveProperties();

        openProperties();
        openTab("Advanced");
        assertThat(multifieldItems("./cq:allowedTemplates")).hasCount(1);
        assertThat(page.locator("input[name='./cq:allowedTemplates']").first()).hasValue(ALLOWED_TEMPLATE);
        assertProperty("cq:allowedTemplates", ALLOWED_TEMPLATE);
    }

    @Test
    public void testAdvancedAuthenticationPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        checkbox("./cq:authenticationRequired").check();
        fillAutocomplete("./cq:loginPath", LOGIN_PAGE);
        saveProperties();

        // authentication settings are not exposed as plain jcr:content properties; verify like Selenium via the UI
        openProperties();
        openTab("Advanced");
        assertThat(checkbox("./cq:authenticationRequired")).isChecked();
        assertThat(autocompleteInput("./cq:loginPath")).hasValue(LOGIN_PAGE);
    }

    @Test
    public void testAdvancedExportPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        fillAutocomplete("./cq:exportTemplate", EXPORT_CONFIGURATION);
        saveProperties();

        openProperties();
        openTab("Advanced");
        assertThat(autocompleteInput("./cq:exportTemplate")).hasValue(EXPORT_CONFIGURATION);
        assertProperty("cq:exportTemplate", EXPORT_CONFIGURATION);
    }

    @Test
    public void testThumbnailPageProperties() {
        openProperties();
        openTab("Thumbnail");
        assertThumbnailGenerateAndRevert();
    }

    @Test
    public void testSocialMediaPageProperties() throws Exception {
        openProperties();
        openTab("Social Media");
        checkbox("./socialMedia", "facebook").check();
        checkbox("./socialMedia", "pinterest").check();
        fillAutocomplete("./variantPath", VARIANT_PATH);
        saveProperties();

        openProperties();
        openTab("Social Media");
        assertThat(checkbox("./socialMedia", "facebook")).isChecked();
        assertThat(checkbox("./socialMedia", "pinterest")).isChecked();
        assertThat(autocompleteInput("./variantPath")).hasValue(VARIANT_PATH);
        assertProperty("socialMedia", "facebook");
        assertProperty("socialMedia", "pinterest");
        assertProperty("variantPath", VARIANT_PATH);
    }

    @Test
    public void testCloudServicesPageProperties() throws Exception {
        openProperties();
        openTab("Cloud Services");

        selectInSelect(CLOUD_CONFIG_SELECT, CLOUD_SERVICE_CONFIG);
        assertThat(page.locator(CLOUD_CONFIG_TAG)).isVisible();

        // delete the added cloud configuration
        page.locator(CLOUD_CONFIG_TAG).click();
        assertThat(page.locator(CLOUD_CONFIG_TAG)).hasCount(0);
        assertThat(page.locator(CLOUD_CONFIG_SELECT + " coral-select-item[value='" + CLOUD_SERVICE_CONFIG
            + "'][selected]")).hasCount(0);

        // add it again
        selectInSelect(CLOUD_CONFIG_SELECT, CLOUD_SERVICE_CONFIG);
        assertThat(page.locator(CLOUD_CONFIG_TAG)).isVisible();
        saveProperties();

        openProperties();
        openTab("Cloud Services");
        assertThat(page.locator("coral-select[name='./cq:cloudserviceconfigs']")).isVisible();
        assertThat(page.locator(CLOUD_CONFIG_TAG)).isVisible();
        assertPropertyStartsWith("cq:cloudserviceconfigs", CLOUD_SERVICE_CONFIG);
    }

    @Test
    public void testPersonalizationPageProperties() throws Exception {
        openProperties();
        openTab("Personalization");
        fillAutocomplete("./cq:contextHubPath", CONTEXT_HUB_PATH);
        fillAutocomplete("./cq:contextHubSegmentsPath", segmentPath());
        saveProperties();

        openProperties();
        openTab("Personalization");
        assertThat(autocompleteInput("./cq:contextHubPath")).hasValue(CONTEXT_HUB_PATH);
        assertThat(autocompleteInput("./cq:contextHubSegmentsPath")).hasValue(segmentPath());
        assertProperty("cq:contextHubPath", CONTEXT_HUB_PATH);
        assertProperty("cq:contextHubSegmentsPath", segmentPath());
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testAddPermissionsPageProperties() {
        openProperties();
        openTab("Permissions");

        Locator permissions = page.locator(USER_PERMISSIONS_ROWS);
        // no permission set
        assertThat(permissions).hasCount(0);

        // add a permission
        page.locator("button.js-cq-sites-AddPermissionsDialog-show").click();
        Locator addDialog = page.locator(ADD_PERMISSIONS_DIALOG);
        assertThat(addDialog).isVisible();
        selectAuthorizable(addDialog.locator("foundation-autocomplete.js-cq-sites-CreatePermissionsDialog-authorizableList"));
        addDialog.locator("coral-checkbox[name='read'] input[type='checkbox']").check();
        addDialog.locator("coral-checkbox[name='modify'] input[type='checkbox']").check();
        addDialog.locator("coral-checkbox[name='delete'] input[type='checkbox']").check();
        addDialog.locator("button[variant='primary']").click();

        // check the permission set
        assertThat(permissions).hasCount(1);
        assertThat(permissions).containsText(USER_NAME);
        Locator row = permissions.filter(new Locator.FilterOptions().setHasText(USER_NAME)).first();
        assertThat(permissionCell(row, 1)).isVisible(); // read
        assertThat(permissionCell(row, 3)).isVisible(); // delete
        assertThat(permissionCell(row, 2)).isVisible(); // modify
        assertThat(permissionCell(row, 4)).hasCount(0); // replicate
        assertThat(permissionCell(row, 5)).hasCount(0); // create

        // edit the permission
        row.locator("button.js-cq-sites-PermissionsProperties-edit").click();
        Locator editDialog = page.locator(EDIT_PERMISSIONS_DIALOG);
        assertThat(editDialog).isVisible();
        editDialog.locator("coral-checkbox[name='replicate'] input[type='checkbox']").check();
        editDialog.locator("button[variant='primary']").click();
        assertThat(permissionCell(row, 4)).isVisible();

        // delete the permission
        row.locator("button.js-cq-sites-PermissionsProperties-delete").click();
        page.locator("coral-dialog[open]").last().locator("button[variant='warning']").click();
        assertThat(permissions).hasCount(0);
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testEditUserGroupPermissionsPageProperties() {
        openProperties();
        openTab("Permissions");

        Locator cugList = page.locator(CUG_ROWS);
        assertThat(cugList).hasCount(0);

        page.locator("button.js-cq-sites-ClosedUserGroupDialog-show").click();
        Locator cugDialog = page.locator(CUG_DIALOG);
        assertThat(cugDialog).isVisible();
        selectAuthorizable(cugDialog.locator("foundation-autocomplete.js-cq-sites-CUGPermissionsDialog-authorizableList"));
        cugDialog.locator("button[variant='primary']").click();

        assertThat(cugList).hasCount(1);
        assertThat(cugList).containsText(USER_NAME);
    }

    @Test
    public void testEffectivePermissionsPageProperties() {
        openProperties();
        openTab("Permissions");

        page.locator("button.js-cq-sites-EffectivePermissionsDialog-show").click();
        Locator effectiveDialog = page.locator(EFFECTIVE_PERMISSIONS_DIALOG);
        assertThat(effectiveDialog).isVisible();
        effectiveDialog.locator("button[handle='closeButton']").click();
        assertThat(effectiveDialog).hasCount(0);
    }

    @Test
    public void testLiveCopyPageProperties() throws Exception {
        liveCopyPage = Commons.createLiveCopy(adminClient, testPage, rootPage, "testLiveCopy", "testLiveCopy", 200);
        String liveCopyContent = liveCopyPage + "/jcr:content";
        JsonNode content = Commons.waitForJson(adminClient, liveCopyContent, 1, json -> json.has("cq:LiveSyncConfig"));
        assertTrue(content.has("cq:LiveSyncConfig"), "The created page should be a live copy");

        openLiveCopyTab();
        assertThat(liveCopyButton("synchronize")).isVisible();
        assertThat(liveCopyButton("reset")).isVisible();
        assertThat(liveCopyButton("suspend")).isVisible();
        assertThat(liveCopyButton("detach")).isVisible();
        assertThat(liveCopyButton("resume")).isHidden();

        // Synchronize
        liveCopyButton("synchronize").click();
        confirmLiveCopyDialog("primary");

        // Reset
        openLiveCopyTab();
        liveCopyButton("reset").click();
        confirmLiveCopyDialog("warning");

        // Suspend (without children)
        openLiveCopyTab();
        liveCopyButton("suspend").click();
        page.locator("a[data-foundation-tracking-event*='\"element\":\"suspend\"']").click();
        confirmLiveCopyDialog("warning");
        Commons.waitForJson(adminClient, liveCopyContent, 1, json -> isCancelled(json));
        openLiveCopyTab();
        assertThat(liveCopyButton("resume")).isVisible();
        assertThat(liveCopyButton("suspend")).isHidden();

        // Resume
        liveCopyButton("resume").click();
        confirmLiveCopyDialog("warning");
        Commons.waitForJson(adminClient, liveCopyContent, 1, json -> !isCancelled(json));
        openLiveCopyTab();
        assertThat(liveCopyButton("suspend")).isVisible();
        assertThat(liveCopyButton("resume")).isHidden();

        // Suspend with children
        liveCopyButton("suspend").click();
        page.locator("a[data-foundation-tracking-event*='\"element\":\"suspend with children\"']").click();
        confirmLiveCopyDialog("warning");
        Commons.waitForJson(adminClient, liveCopyContent, 1, json -> isCancelled(json));
        openLiveCopyTab();
        assertThat(liveCopyButton("resume")).isVisible();

        // Detach
        liveCopyButton("detach").click();
        confirmLiveCopyDialog("warning");
        Commons.waitForJson(adminClient, liveCopyContent, 1, json -> !json.has("cq:LiveSyncConfig"));
    }

    private void openLiveCopyTab() {
        openProperties(liveCopyPage);
        openTab("Live Copy");
        assertThat(liveCopyButton("detach").or(liveCopyButton("synchronize")).first()).isVisible();
    }

    private Locator liveCopyButton(String trackingElement) {
        return page.locator("coral-actionbar-item button[trackingelement='" + trackingElement + "']");
    }

    private void confirmLiveCopyDialog(String variant) {
        Locator dialog = page.locator("coral-dialog[open]").last();
        assertThat(dialog).isVisible();
        dialog.locator("button[variant='" + variant + "']").click();
        assertThat(dialog).isHidden();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    private Locator permissionCell(Locator row, int column) {
        return row.locator("td").nth(column).locator("[icon='check']");
    }

    private void selectAuthorizable(Locator autocomplete) {
        Locator input = autocomplete.locator("input[role='combobox']");
        input.fill(USER_PRINCIPAL_NAME);
        assertThat(input).hasAttribute("aria-controls", Pattern.compile(".+"));
        Locator suggestion = page.locator("[id='" + input.getAttribute("aria-controls") + "'] button[value='"
            + USER_PRINCIPAL_NAME + "']");
        assertThat(suggestion).isVisible();
        suggestion.click();
    }
}
