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
    public void testSocialMediaPageProperties() {
        // Social Media is not an active v3 Selenium invocation.
    }

    @Test
    @Tag("IgnoreOn64")
    public void testAdvancedSeoPageProperties() {
        openProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        page.locator("[name='./cq:robotsTags']").locator("input").fill("index");
        page.locator("[name='./cq:robotsTags']").locator("input").press("Enter");
        page.locator("[name='./cq:canonicalUrl']").fill(testPage);
        page.locator("[name='./sling:sitemapRoot']").check();
        saveProperties();
        reopenProperties();
        page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Advanced")).click();
        assertThat(page.locator("[name='./cq:canonicalUrl']")).hasValue(testPage);
        assertThat(page.locator("[name='./sling:sitemapRoot']")).isChecked();
        assertThat(page.locator("[name='./cq:robotsTags'], [name='./sling:sitemapRoot'], [name='./cq:canonicalUrl']")).hasCount(3);
    }

    @Test
    public void testImagePageProperties() {
        openProperties();
        assertThat(page.locator("coral-tab-label").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Image"))).isVisible();
    }

    @Test
    public void testBlueprintPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }
}
