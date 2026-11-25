/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkHandler;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

@ExtendWith(AemContextExtension.class)
class LinkManagerImplTest {

    public final AemContext context = new AemContext();

    @BeforeEach
    void setUp() {
        // Mock and register required OSGi PathProcessor service
        PathProcessor pathProcessor = mock(PathProcessor.class);
        when(pathProcessor.accepts(any(), any())).thenReturn(Boolean.TRUE);
        when(pathProcessor.map(any(), any())).then(returnsFirstArg());
        when(pathProcessor.sanitize(any(), any())).then(returnsFirstArg());
        when(pathProcessor.externalize(any(), any())).then(returnsFirstArg());

        context.registerService(PathProcessor.class, pathProcessor);

        // Register the implementation model
        context.addModelsForClasses(LinkManagerImpl.class);
    }

    @Test
    void testGetLinkPageWithLinkHandler() {
        Page page = context.create().page("/content/test-page");
        LinkHandler linkHandler = context.request().adaptTo(LinkHandler.class);

        assertNotNull(linkHandler);
        Link<Page> link = linkHandler.getLink(page);
        assertNotNull(link);
        assertEquals("/content/test-page.html", link.getURL());
    }

    @Test
    void testGetLinkResourceWithLinkHandler() {
        context.create().page("/content/test-page");
        Resource resource = context.currentResource("/content/test-page");
        LinkHandler linkHandler = context.request().adaptTo(LinkHandler.class);

        assertNotNull(linkHandler);
        Link<Page> link = linkHandler.getLink(resource);
        assertNotNull(link);
    }

    @Test
    void testGetLinkAssetWithLinkHandler() {
        Asset asset = context.create().asset("/content/dam/test-asset.png", 100, 100, "image/png");
        LinkHandler linkHandler = context.request().adaptTo(LinkHandler.class);

        assertNotNull(linkHandler);
        Link<Asset> link = linkHandler.getLink(asset);
        assertNotNull(link);
        assertEquals("/content/dam/test-asset.png", link.getURL());
    }

    @Test
    void testGetLinkUrlWithLinkHandler() {
        LinkHandler linkHandler = context.request().adaptTo(LinkHandler.class);

        assertNotNull(linkHandler);
        Link<String> link = linkHandler.getLink("/content/test-url");
        assertNotNull(link);
        assertEquals("/content/test-url", link.getURL());
    }
}
