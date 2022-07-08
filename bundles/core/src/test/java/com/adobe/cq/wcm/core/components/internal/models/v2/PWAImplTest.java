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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.models.PWA.MANIFEST_NAME;
import static com.adobe.cq.wcm.core.components.models.PWA.PN_PWA_ENABLED;
import static com.adobe.cq.wcm.core.components.models.PWA.PN_PWA_ICON_PATH;
import static com.adobe.cq.wcm.core.components.models.PWA.PN_PWA_START_URL;
import static com.adobe.cq.wcm.core.components.models.PWA.PN_PWA_THEME_COLOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class PWAImplTest {

    private static final String SITES_PAGE_PATH = "/content/mysite";
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
        when(spyResolver.getResource(SITES_PAGE_PATH + "/" + JcrConstants.JCR_CONTENT)).thenReturn(mockPWAResource);

        PageManager mockPageManager = mock(PageManager.class);
        Page mockPage = mock(Page.class);
        when(spyResolver.adaptTo(PageManager.class)).thenReturn(mockPageManager);
        when(mockPageManager.getContainingPage(resource)).thenReturn(mockPage);
        when(mockPage.getContentResource()).thenReturn(mockPWAResource);
        when(mockPage.getPath()).thenReturn(SITES_PAGE_PATH);

        Page mockParentPage = mock(Page.class);
        when(mockPage.getParent()).thenReturn(mockParentPage);

        mvp.put(PN_PWA_ENABLED, true);
        mvp.put(PN_PWA_START_URL, SITES_PAGE_PATH + ".html");
        when(mockPWAResource.getValueMap()).thenReturn(mvp);
    }

    @Test
    public void testPWAReturnsManifestPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals(SITES_PAGE_PATH + "/" + MANIFEST_NAME, pwa.getManifestPath());
    }

    @Test
    public void testPWAReturnsServiceWorkerPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals("/mysitesw.js", pwa.getServiceWorkerPath());
    }

    @Test
    public void testPWAReturnsFalseIfPWAOptionIsNotEnabled() {
        mvp.remove(PN_PWA_ENABLED);
        pwa = resource.adaptTo(PWA.class);
        assertFalse(pwa.isEnabled());
    }

    @Test
    public void testPWAReturnsTrueIfPWAOptionIsEnabled() {
        pwa = resource.adaptTo(PWA.class);
        assertTrue(pwa.isEnabled());
    }

    @Test
    public void testPWAReturnsIconPath() {
        mvp.put(PN_PWA_ICON_PATH, "/content/dam/foo.png");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("/content/dam/foo.png", pwa.getIconPath());
    }

    @Test
    public void testPWAReturnsThemeColor() {
        mvp.put(PN_PWA_THEME_COLOR, "#AAAAAA");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#AAAAAA", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBToHex() {
        mvp.put(PN_PWA_THEME_COLOR, "rgb(255,160,0)");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffa000", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBAToHex() {
        mvp.put(PN_PWA_THEME_COLOR, "rgba(255,255,0,0.75)");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffff00", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidFormat() {
        mvp.put(PN_PWA_THEME_COLOR, "123");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemeColor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidValue() {
        mvp.put(PN_PWA_THEME_COLOR, "rgbbar");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemeColor());
    }

}
