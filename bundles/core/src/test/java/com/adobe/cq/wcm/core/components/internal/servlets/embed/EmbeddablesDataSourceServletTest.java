/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbeddablesDataSourceServlet.EmbeddableDescription;
import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbeddablesDataSourceServlet.EmbeddableDataResourceSource;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class EmbeddablesDataSourceServletTest {

    private static final String TEST_BASE = "/embed/v1/datasources/embeddables";
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
    public void testEmbeddablesDataSourceServlet() {
        context.currentResource("/apps/embeddablesdatasource");
        EmbeddablesDataSourceServlet dataSourceServlet = new EmbeddablesDataSourceServlet();
        dataSourceServlet.doGet(request, context.response());
        DataSource dataSource = (com.adobe.granite.ui.components.ds.DataSource) request.getAttribute(DataSource.class
            .getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        ValueMap valueMap = resource.getValueMap();
        assertEquals("YouTube", valueMap.get(TextValueDataResourceSource.PN_TEXT, String.class));
        assertEquals("my-app/youtube", valueMap.get(TextValueDataResourceSource.PN_VALUE, String.class));
        EmbeddableDescription embed1 = new EmbeddableDescription(null, embeddableResources.get(1).getName(),
            embeddableResources.get(1).getValueMap());
        EmbeddableDescription embed2 = new EmbeddableDescription(null, embeddableResources.get(0).getName(),
            embeddableResources.get(0).getValueMap());
        assertNotEquals(embed2, embed1);
        assertNotNull(embed1.hashCode());
    }
}
