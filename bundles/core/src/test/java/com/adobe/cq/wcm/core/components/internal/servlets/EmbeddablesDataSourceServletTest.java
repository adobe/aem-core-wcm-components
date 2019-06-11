/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.EmbeddablesDataSourceServlet.EmbeddableDescription;
import com.adobe.cq.wcm.core.components.internal.servlets.EmbeddablesDataSourceServlet.EmbeddableDataResourceSource;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmbeddablesDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/embed/v1/datasources/embeddables",
        "/apps");

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private ResourceResolver resolver;

    private EmbeddablesDataSourceServlet dataSourceServlet;

    List<Resource> embeddableResources = new ArrayList<>();

    @Before
    public void setUp() {
        Resource embeddable1 = context.resourceResolver().getResource("/apps/my-app/youtube");
        Resource embeddable2 = context.resourceResolver().getResource("/apps/my-app/chatbot");
        Resource embeddable3 = context.resourceResolver().getResource("/apps/my-app/social");
        embeddableResources.add(embeddable1);
        embeddableResources.add(embeddable2);
        embeddableResources.add(embeddable3);
        when(request.getResourceResolver()).thenReturn(resolver);
        final String rt = embeddable1.getPath().substring("/apps".length() + 1);
        List<Resource> outputResources = new ArrayList<>();
        Resource resource1 = new EmbeddableDataResourceSource(new EmbeddableDescription(rt, embeddable1.getName(),
            ResourceUtil.getValueMap(embeddable1)), resolver);
        Resource resource2 = new EmbeddableDataResourceSource(new EmbeddableDescription(rt, embeddable2.getName(),
            ResourceUtil.getValueMap(embeddable2)), resolver);
        outputResources.add(resource1);
        outputResources.add(resource2);
        when(request.getAttribute(DataSource.class.getName())).thenReturn(
            new SimpleDataSource(outputResources.iterator()));
        when(resolver.findResources(any(), any())).thenReturn(embeddableResources.iterator());
        when(resolver.getSearchPath()).thenReturn(context.resourceResolver().getSearchPath());
    }

    @Test
    public void testEmbeddablesDataSourceServlet() throws Exception {
        context.currentResource("/apps/embeddablesdatasource");
        dataSourceServlet = new EmbeddablesDataSourceServlet();
        dataSourceServlet.doGet(request, context.response());
        DataSource dataSource = (com.adobe.granite.ui.components.ds.DataSource) request.getAttribute(DataSource.class
            .getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        assertEquals("YouTube", valueMap.get(TextValueDataResourceSource.PN_TEXT, String.class));
        assertEquals("my-app/youtube", valueMap.get(TextValueDataResourceSource.PN_VALUE, String.class));
        EmbeddableDescription embed1 = new EmbeddableDescription(null, embeddableResources.get(1).getName(),
            ResourceUtil.getValueMap(embeddableResources.get(1)));
        EmbeddableDescription embed2 = new EmbeddableDescription(null, embeddableResources.get(0).getName(),
            ResourceUtil.getValueMap(embeddableResources.get(0)));
        assertEquals(false, embed1.equals(embed2));
        assertNotNull(embed1.hashCode());
    }
}
