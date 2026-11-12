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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;

import static com.adobe.cq.wcm.core.components.Utils.configureDataLayer;
import static com.adobe.cq.wcm.core.components.Utils.skipDataLayerInclude;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImplTest {

    private static final String TEST_BASE = "/page/v3";

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testRedirectTarget() {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
        assertValidLink(redirectTarget.getLink(), "/content/page/templated-page.html", context.request());
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testRedirectTarget_external() {
        Page page = getPageUnderTest(REDIRECT_PAGE_EXTERNAL);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertNull(redirectTarget.getPage());
        assertEquals("https://www.adobe.com/", redirectTarget.getURL());
        assertValidLink(redirectTarget.getLink(), "https://www.adobe.com/", context.request());
    }

    @Test
    protected void testIsClientlibsAsync_undefined() {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        boolean async = page.isClientlibsAsync();
        assertFalse(async);
    }

    @Test
    protected void testIsClientlibsAsync_true() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE, PageImpl.PN_CLIENTLIBS_ASYNC, "true");
        boolean async = page.isClientlibsAsync();
        assertTrue(async);
    }

    @Test
    protected void testIsClientlibsAsync_false() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE, PageImpl.PN_CLIENTLIBS_ASYNC, "false");
        boolean async = page.isClientlibsAsync();
        assertFalse(async);
    }

    @Test
    protected void testIsClientlibsAsync_undefinedPolicy() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE);

        // Set currentStyle = null
        Field currentStyleField = Class.forName("com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl").getDeclaredField("currentStyle");
        currentStyleField.setAccessible(true);
        currentStyleField.set(page, null);

        boolean async = page.isClientlibsAsync();
        assertFalse(async);
    }

    @Test
    protected void testIsDataLayerClientlibIncluded_caconfig_undefined() {
        Page page = getPageUnderTest(PAGE);
        assertTrue(page.isDataLayerClientlibIncluded(), "The data layer clientlib should be included.");
    }

    @Test
    protected void testIsDataLayerClientlibIncluded_caconfig_true() {
        Page page = getPageUnderTest(PAGE);
        skipDataLayerInclude(context,true);
        assertFalse(page.isDataLayerClientlibIncluded(), "The data layer clientlib should not be included.");
    }

    @Test
    protected void testIsDataLayerClientlibIncluded_caconfig_false() {
        Page page = getPageUnderTest(PAGE);
        skipDataLayerInclude(context,false);
        assertTrue(page.isDataLayerClientlibIncluded(), "The data layer clientlib should be included.");
    }

    @Test
    protected void testIsDataLayerName_caconfig_empty() {
        Page page = getPageUnderTest(PAGE);
        configureDataLayer(context,false, true, "");
        assertTrue(page.getDataLayerName().equals("adobeDataLayer"), "The data layer name should be the default adobeDataLayer.");
    }

    @Test
    protected void testIsDataLayerName_caconfig_configured() {
        Page page = getPageUnderTest(PAGE);
        configureDataLayer(context,false, true, "dataLayer");
        assertTrue(page.getDataLayerName().equals("dataLayer"), "The data layer name should be the configured 'dataLayer'.");
    }


}
