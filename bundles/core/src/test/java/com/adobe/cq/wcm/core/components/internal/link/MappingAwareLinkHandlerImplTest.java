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

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class MappingAwareLinkHandlerImplTest {

    private static final Map<String, Object> PROPERTIES = ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
            "/:/",
            "/content/site1/</"
    ));
    private final AemContext context = new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
            .resourceResolverFactoryActivatorProps(PROPERTIES)
            .build();

    private Page page;
    private LinkHandlerImpl underTest;

    @BeforeEach
    void setUp() {
        page = context.create().page("/content/site1/en");
        context.currentPage(page);
        underTest = context.request().adaptTo(MappingAwareLinkHandlerImpl.class);
    }

    @Test
    void testPageMappingLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, page.getPath());
        Optional<Link> link = underTest.getLink(linkResource);
        assertValidLink(link.get(), "/en.html");
    }
}