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
public class PageV2PwIT extends PageV1PwIT {
    @Override
    protected String pageResourceType() {
        return "core/wcm/components/page/v2/page";
    }

    @Test
    public void testdvancedConfigurationPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }

    @Test
    @Tag("IgnoreOn65")
    @Tag("IgnoreOn64")
    public void testAdvancedSeoPageProperties() throws Exception {
        openProperties();
        openTab("Advanced");
        fillAutocomplete("./cq:canonicalUrl", testPage);
        checkbox("./sling:sitemapRoot").check();
        saveProperties();
        openProperties();
        openTab("Advanced");
        assertThat(page.locator("foundation-autocomplete[name='./cq:canonicalUrl'] input[is='coral-textfield']").first()).hasValue(testPage);
        assertThat(checkbox("./sling:sitemapRoot")).isChecked();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testBlueprintPageProperties() {
        openProperties();
        assertThat(page.locator("coral-tabview")).isVisible();
    }
}
