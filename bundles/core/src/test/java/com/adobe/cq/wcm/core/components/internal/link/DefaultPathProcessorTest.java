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
package com.adobe.cq.wcm.core.components.internal.link;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkBuilderImpl.HTML_EXTENSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class})
class DefaultPathProcessorTest {

    public static final String PATH = "/some/path";
    private final AemContext context = CoreComponentTestContext.newAemContext();
    private final AemContext localContext = new AemContext();

    @Test()
    void testExternalizeWithException() {
        Externalizer externalizer = mock(Externalizer.class);
        when(externalizer.publishLink(any(ResourceResolver.class), anyString())).thenThrow(IllegalArgumentException.class);
        localContext.registerService(externalizer);
        DefaultPathProcessor underTest = localContext.registerService(new DefaultPathProcessor());
        String path = PATH;
        assertEquals(path, underTest.externalize(path, localContext.request()));
    }

    @Test
    void testMappingWithException() {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.map(any(SlingHttpServletRequest.class), anyString())).thenThrow(IllegalStateException.class);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resourceResolver);
        DefaultPathProcessor underTest = localContext.registerService(new DefaultPathProcessor());
        assertEquals(PATH, underTest.map(PATH, request));
    }

    @Test
    void testSanitizeInternalLik() {
        DefaultPathProcessor underTest = context.registerService(new DefaultPathProcessor());
        String path = "#internal";
        MockSlingHttpServletRequest request = context.request();
        assertEquals(path, underTest.sanitize(path, request));
        path = PATH + path;
        assertEquals(path, underTest.sanitize(path, request));
        path = "?request=value";
        assertEquals(path, underTest.sanitize(path, request));
        path = PATH + path;
        assertEquals(path, underTest.sanitize(path, request));
        path = "/some space#internal";
        assertEquals("/some%20space#internal", underTest.sanitize(path, request));
    }

    @Test
    void testVanityUrl() {
        Page page = context.create().page("/content/links/site1/en", "/conf/example",
                ImmutableMap.of("sling:vanityPath", "vanity.html"));
        DefaultPathProcessor underTest = context.registerInjectActivateService(new DefaultPathProcessor(), ImmutableMap.of(
                "vanityConfig", DefaultPathProcessor.VanityConfig.MAPPING.getValue()));
        assertTrue(underTest.accepts(page.getPath() + HTML_EXTENSION, context.request()));
        assertEquals("/content/links/site1/en.html", underTest.sanitize(page.getPath() + HTML_EXTENSION, context.request()));
        assertEquals("/vanity.html", underTest.map(page.getPath() + HTML_EXTENSION, context.request()));
        assertEquals("https://example.org/vanity.html", underTest.externalize(page.getPath() + HTML_EXTENSION, context.request()));
    }

    @Test
    void testVanityConfig() {
        Page page = context.create().page("/content/links/site1/en", "/conf/example",
                ImmutableMap.of("sling:vanityPath", "vanity.html"));
        DefaultPathProcessor underTest = context.registerInjectActivateService(new DefaultPathProcessor(), ImmutableMap.of(
                "vanityConfig", "shouldBeDefault"));
        assertEquals("/content/site1/en.html", underTest.map(page.getPath() + HTML_EXTENSION, context.request()));
        assertEquals("https://example.org/content/links/site1/en.html", underTest.externalize(page.getPath() + HTML_EXTENSION, context.request()));
        context.request().setContextPath("/cp");
        underTest = context.registerInjectActivateService(new DefaultPathProcessor(), ImmutableMap.of(
                "vanityConfig", DefaultPathProcessor.VanityConfig.ALWAYS.getValue()));
        assertEquals("/cp/vanity.html", underTest.sanitize(page.getPath() + HTML_EXTENSION, context.request()));
    }
}
