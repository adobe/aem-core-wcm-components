/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.page;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
    public void testImagePageProperties() {
        openProperties();
        openTab("Image");
        assertThumbnailGenerateAndRevert();
    }
}
