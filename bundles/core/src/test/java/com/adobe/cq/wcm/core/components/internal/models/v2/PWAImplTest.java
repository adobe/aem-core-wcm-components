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

    private static final String SITES_PROJECT_PATH = "/content/mysite";
    private static final String SITES_PAGE_PATH = "/content/mysite/us/en";
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
    }

    @Test
    public void testPWAReturnsManifestPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals(SITES_PROJECT_PATH + "/manifest.webmanifest", pwa.getManifestPath());
    }

    @Test
    public void testPWAReturnsProjectName() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals("mysite", pwa.getProjectName());
    }

    @Test
    public void testProjectNameReturnsBlankIfResourceIsNotUnderSitesProject() {
        when(resource.getPath()).thenReturn("/");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getProjectName());
    }

    @Test
    public void testReturnsProjectNameIfResourceIsSitesProject() {
        when(resource.getPath()).thenReturn("/foo/bar");
        pwa = resource.adaptTo(PWA.class);
        assertEquals("bar", pwa.getProjectName());
    }

    @Test
    public void testPWAReturnsServiceWorkerPath() {
        pwa = resource.adaptTo(PWA.class);
        assertEquals("/mysitesw.js", pwa.getServiceWorkerPath());
    }

    @Test
    public void testPWAReturnsFalseIfPWAOptionIsNotEnabled() {
        pwa = resource.adaptTo(PWA.class);
        assertFalse(pwa.isPWAEnabled());
    }

    @Test
    public void testPWAReturnsTrueIfPWAOptionIsNotEnabled() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("enablePWA", true);
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertTrue(pwa.isPWAEnabled());
    }

    @Test
    public void testPWAReturnsIconPath() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("pwaicon", "/content/dam/foo.png");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("/content/dam/foo.png", pwa.getIconPath());
    }

    @Test
    public void testPWAReturnsThemeColor() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("themecolor", "#AAAAAA");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("#AAAAAA", pwa.getThemecolor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBToHex() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("themecolor", "rgb(255,160,0)");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffa000", pwa.getThemecolor());
    }

    @Test
    public void testPWAReturnsThemeColorConvertingRGBAToHex() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("themecolor", "rgba(255,255,0,0.75)");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("#ffff00", pwa.getThemecolor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidFormat() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("themecolor", "123");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemecolor());
    }

    @Test
    public void testPWAReturnsBlankThemeColorIfValueHadInvalidValue() {
        when(resource.getPath()).thenReturn("/foo/bar/baz");
        ResourceResolver spyResolver = spy(resolver);
        when(resource.getResourceResolver()).thenReturn(spyResolver);
        Resource mockSitesProject = mock(Resource.class);
        when(spyResolver.getResource("/foo/bar/" + JcrConstants.JCR_CONTENT)).thenReturn(mockSitesProject);

        mvp.put("themecolor", "rgbbar");
        when(mockSitesProject.getValueMap()).thenReturn(mvp);

        pwa = resource.adaptTo(PWA.class);
        assertEquals("", pwa.getThemecolor());
    }

}
