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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.hamcrest.ResourceMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbeddablesDataSourceServlet.EmbeddableDataResourceSource;
import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbeddablesDataSourceServlet.EmbeddableDescription;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class EmbedDesignTabsDataSourceServletTest {

    private static final String TEST_BASE = "/embed/v1/datasources/embeddesigntabs";
    private static final String APPS_ROOT = "/apps";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private SlingHttpServletRequest request;

    List<Resource> embeddableResources = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);

        Resource embeddable1 = Objects.requireNonNull(context.resourceResolver().getResource("/apps/my-app/youtube"));
        Resource embeddable2 = Objects.requireNonNull(context.resourceResolver().getResource("/apps/my-app/chatbot"));
        Resource embeddable3 = Objects.requireNonNull(context.resourceResolver().getResource("/apps/my-app/social"));
        embeddableResources.add(embeddable1);
        embeddableResources.add(embeddable2);
        embeddableResources.add(embeddable3);
        
        context.currentResource("/apps/embed");
        request = Mockito.spy(context.request());
        ResourceResolver resolver = Mockito.spy(context.resourceResolver());
        when(request.getResourceResolver()).thenReturn(resolver);
        final String rt = embeddable1.getPath().substring("/apps".length() + 1);

        List<Resource> outputResources = new ArrayList<>();
        outputResources.add(new EmbeddableDataResourceSource(
            new EmbeddableDescription(rt, embeddable1.getName(), embeddable1.getValueMap()), resolver));
        outputResources.add(new EmbeddableDataResourceSource(
            new EmbeddableDescription(rt, embeddable2.getName(), embeddable2.getValueMap()), resolver));
        context.request().setAttribute(DataSource.class.getName(), new SimpleDataSource(outputResources.iterator()));

        when(resolver.findResources(any(), any())).thenReturn(embeddableResources.iterator());
        when(resolver.getSearchPath()).thenReturn(context.resourceResolver().getSearchPath());
    }

    @Test
    public void testEmbedDesignTabsDataSourceServlet() {
        EmbedDesignTabsDataSourceServlet dataSourceServlet = new EmbedDesignTabsDataSourceServlet();
        dataSourceServlet.doGet(request, context.response());
        DataSource dataSource = (com.adobe.granite.ui.components.ds.DataSource) request.getAttribute(DataSource.class
            .getName());
        assertNotNull(dataSource);
        Iterator<Resource> resourceIterator = dataSource.iterator();
        Resource resourceTab = resourceIterator.next();
        assertNotNull(resourceTab);
        
        Object[] expectedProperties = new Object[] {
                "jcr:primaryType", "nt:unstructured"
        };
        MatcherAssert.assertThat(resourceTab, ResourceMatchers.nameAndProps("first", expectedProperties));
        
        resourceTab = resourceIterator.next();
        assertNotNull(resourceTab);
        MatcherAssert.assertThat(resourceTab, ResourceMatchers.nameAndProps("second", expectedProperties));
        
        Object[] expectedProperties2 = new Object[] {
                "jcr:primaryType", "nt:unstructured",
                "jcr:title", "YouTube Design Tab",
                "sling:resourceType", "granite/ui/components/coral/foundation/container"
        };
        resourceTab = resourceIterator.next();
        assertNotNull(resourceTab);
        MatcherAssert.assertThat(resourceTab, ResourceMatchers.nameAndProps("cq:design_dialog", expectedProperties2));
        
        resourceTab = resourceIterator.next();
        assertNotNull(resourceTab);
        MatcherAssert.assertThat(resourceTab, ResourceMatchers.nameAndProps("last", expectedProperties));
        
        assertFalse(resourceIterator.hasNext());
        
    }
}
