/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.page;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group4")
public class PageV2PwIT extends PageV1PwIT {
    private static final String CONFIGURATION = "/conf/core-components";

    @Override
    protected String pageResourceType() {
        return "core/wcm/components/page/v2/page";
    }

    @Override
    protected String segmentPath() {
        return "/conf/we-retail/settings/wcm/segments";
    }

    @Test
    public void testdvancedConfigurationPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");

        // uncheck the config inheritance
        page.locator(".cq-cloudconfig-configpathbrowser coral-checkbox.inheritance input[type='checkbox']").first()
            .setChecked(false);
        pickInPropertiesPicker("/conf", "./cq:conf", "core-components");
        saveProperties();

        openProperties();
        openTab("Advanced");
        assertThat(page.locator("[name='./cq:conf'] input[is='coral-textfield']").first()).hasValue(CONFIGURATION);
        assertProperty("cq:conf", CONFIGURATION);
    }

    @Test
    @Tag("IgnoreOn65")
    @Tag("IgnoreOn64")
    public void testAdvancedSeoPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        selectInPageSelect("./cq:robotsTags", "index");
        page.keyboard().press("Escape");
        selectInPageSelect("./cq:robotsTags", "follow");
        page.keyboard().press("Escape");
        checkbox("./sling:sitemapRoot").check();
        fillAutocomplete("./cq:canonicalUrl", testPage);
        saveProperties();

        openProperties();
        openTab("Advanced");
        Locator robotsTags = page.locator("coral-taglist[name='./cq:robotsTags'] coral-tag");
        assertThat(robotsTags).hasCount(2);
        assertThat(robotsTags.nth(0)).hasAttribute("value", "index");
        assertThat(robotsTags.nth(1)).hasAttribute("value", "follow");
        assertThat(page.locator("coral-select[name='./cq:robotsTags'] coral-select-item[value='index'][selected]"))
            .hasCount(1);
        assertThat(page.locator("coral-select[name='./cq:robotsTags'] coral-select-item[value='follow'][selected]"))
            .hasCount(1);
        assertThat(checkbox("./sling:sitemapRoot")).isChecked();
        assertThat(autocompleteInput("./cq:canonicalUrl")).hasValue(testPage);

        // validate the actual persisted values
        assertArrayProperty("cq:robotsTags", "index", "follow");
        assertProperty("sling:sitemapRoot", "true");
        assertProperty("cq:canonicalUrl", testPage);
    }

    @Test
    @Tag("IgnoreOn65")
    public void testBlueprintPageProperties() throws Exception {
        liveCopyPage = Commons.createLiveCopy(adminClient, testPage, rootPage, "testLiveCopy", "testLiveCopy", 200);

        openProperties();
        openTab("Blueprint");

        Locator rolloutButton = page.locator(
            "coral-actionbar-item .cq-siteadmin-admin-properties-actions-blueprint[href*='rollout']");
        Locator rolloutDialog = page.locator("coral-dialog.msm-rollout-dialog[open]");

        rolloutButton.click();
        assertThat(rolloutDialog).isVisible();
        // the live copy is present for rollout and selected
        Locator rows = rolloutDialog.locator("[handle=table] tbody tr");
        assertThat(rows).hasCount(1);
        Locator liveCopyRow = rolloutDialog.locator("[handle=table] tbody tr[data-path='" + liveCopyPage + "']");
        assertThat(liveCopyRow).hasCount(1);
        assertThat(liveCopyRow.locator("td").first().locator("coral-checkbox input[type='checkbox']")).isChecked();

        // cancel the rollout
        rolloutDialog.locator("button[title='Cancel']").click();
        assertThat(rolloutDialog).hasCount(0);

        // roll out now
        rolloutButton.click();
        assertThat(rolloutDialog).isVisible();
        rolloutDialog.locator("button[title='Rollout']").click();
        Locator scheduleDialog = page.locator("coral-dialog#aem-sites-rollout-schedule-dialog");
        assertThat(scheduleDialog).isVisible();
        scheduleDialog.locator("coral-radio[name='reportSchedule'][value='now']").click();
        scheduleDialog.locator("button[trackingelement='continue']").click();
        assertThat(scheduleDialog).isHidden();
        assertThat(rolloutDialog).hasCount(0);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertEquals(200, adminClient.doGet(liveCopyPage + ".html").getStatusLine().getStatusCode(),
            "The live copy should still be available after the rollout");
    }
}
