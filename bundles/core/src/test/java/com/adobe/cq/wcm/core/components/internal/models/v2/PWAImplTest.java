/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.PWA;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class PWAImplTest {

    private static final String SITES_PAGE_PATH = "/content/mysite/en";
    private ResourceResolver resolver;
    private PWA pwa;
    private Resource resource;
    private ModifiableValueMap mvp;

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json("/pwa" + CoreComponentTestContext.TEST_CONTENT_JSON, SITES_PAGE_PATH);
        resolver = context.resourceResolver();
        resource = spy(resolver.getResource(SITES_PAGE_PATH));
        mvp = resource.adaptTo(ModifiableValueMap.class);

        when(resource.getPath()).thenReturn(SITES_PAGE_PATH);
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockPWAResource = mock(Resource.class);
        when(spyResolver.getResource("/content/mysite/" + JcrConstants.JCR_CONTENT)).thenReturn(mockPWAResource);

        mvp.put("pwaEnabled", true);
        mvp.put("startURL", SITES_PAGE_PATH + ".html");
        when(mockPWAResource.getValueMap()).thenReturn(mvp);
    }

    @Test
    public void testPWAReturnsManifestPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals(SITES_PAGE_PATH + "/manifest.webmanifest", pwa.getManifestPath());
    }

    @Test
    public void testPWAReturnsServiceWorkerPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals("/mysitesw.js", pwa.getServiceWorkerPath());
    }

    @Test
    public void testPWAReturnsFalseIfPWAOptionIsNotEnabled() {
        mvp.remove("pwaEnabled");
        pwa = resource.adaptTo(PWA.class);
        assertFalse(pwa.isPWAEnabled());
    }

    @Test
    public void testPWAReturnsTrueIfPWAOptionIsEnabled() {
        pwa = resource.adaptTo(PWA.class);
        assertTrue(pwa.isPWAEnabled());
    }

    @Test
    public void testPWAReturnsIconPath() {
        mvp.put("pwaIcon", "/content/dam/foo.png");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("/content/dam/foo.png", pwa.getIconPath());
    }

    @Test
    public void testPWAReturnsThemeColor() {
        mvp.put("themeColor", "#AAAAAA");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#AAAAAA", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBToHex() {
        mvp.put("themeColor", "rgb(255,160,0)");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffa000", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBAToHex() {
        mvp.put("themeColor", "rgba(255,255,0,0.75)");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffff00", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidFormat() {
        mvp.put("themeColor", "123");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidValue() {
        mvp.put("themeColor", "rgbbar");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemeColor());
    }

}
