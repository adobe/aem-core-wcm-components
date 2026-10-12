/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.page;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group4")
@Tag("IgnoreOn65")
public class PageV3PwIT extends PageV2PwIT {
    @Override
    protected String pageResourceType() {
        return "core/wcm/components/page/v3/page";
    }

    @Override
    public void testThumbnailPageProperties() {
        // v3 exposes the Image tab instead of the v1/v2 Thumbnail tab.
    }

    @Override
    public void testSocialMediaPageProperties() throws Exception {
        // Social Media is not an active v3 Selenium invocation.
    }

    @Test
    @Tag("IgnoreOn64")
    @Override
    public void testAdvancedSeoPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        selectInPageSelect("./cq:robotsTags", "index");
        page.keyboard().press("Escape");
        selectInPageSelect("./cq:robotsTags", "follow");
        page.keyboard().press("Escape");
        fillAutocomplete("./cq:canonicalUrl", testPage);
        checkbox("./sling:sitemapRoot").check();
        saveProperties();
        openProperties();
        openTab("Advanced");
        assertThat(page.locator("coral-select[name='./cq:robotsTags'] coral-select-item[value='index'][selected]")).hasCount(1);
        assertThat(page.locator("coral-select[name='./cq:robotsTags'] coral-select-item[value='follow'][selected]")).hasCount(1);
        assertThat(page.locator("foundation-autocomplete[name='./cq:canonicalUrl'] input[is='coral-textfield']").first()).hasValue(testPage);
        assertThat(checkbox("./sling:sitemapRoot")).isChecked();
    }

    @Test
    public void testImagePageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Image"))).isVisible();
    }

    @Test
    @Override
    public void testBlueprintPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }
}
