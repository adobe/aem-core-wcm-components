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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;

import java.util.Iterator;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(AemContextExtension.class)
public class ModelDataSourceServletTest {

    private ModelDataSourceServlet modelDatasourceServlet;

    public final AemContext context = CoreComponentTestContext.newAemContext();


    @BeforeEach
    public void setUp() {
        modelDatasourceServlet = new ModelDataSourceServlet();
    }

    @Test
    public void verifyDataSourceWhenSessionIsNull() throws Exception {
        // GIVEN
        SlingHttpServletRequest request = new MockSlingHttpServletRequest(context.bundleContext());

        // WHEN
        modelDatasourceServlet.doGet(request, context.response());

        // THEN
        assertThat(request.getAttribute(DataSource.class.getName()), IsInstanceOf.instanceOf(EmptyDataSource.class));
    }

    @Test
    public void verifyDataSource() throws Exception {
        // GIVEN
        context.load().json(getClass().getResourceAsStream("test-content.json"), "/conf/foobar/settings/dam/cfm/models/yetanothercfmodel");
        ResourceResolver resourceResolver = Mockito.spy(context.resourceResolver());
        Resource resource = resourceResolver.getResource("/conf/foobar/settings/dam/cfm/models/yetanothercfmodel");
        Resource leakingResource = Mockito.spy(resource);

        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        Query query = Mockito.mock(Query.class);
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        Iterator<Resource> iterator = Mockito.mock(Iterator.class);
        ResourceResolver leakingResourceResolver = Mockito.mock(ResourceResolver.class);

        Mockito.doReturn(leakingResourceResolver).when(leakingResource).getResourceResolver();
        Mockito.doReturn(queryBuilder).when(resourceResolver).adaptTo(Mockito.eq(QueryBuilder.class));
        Mockito.when(queryBuilder.createQuery(Mockito.any(), Mockito.any())).thenReturn(query);
        Mockito.when(query.getResult()).thenReturn(searchResult);
        Mockito.when(searchResult.getResources()).thenReturn(iterator);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(leakingResource);

        SlingHttpServletRequest request = new MockSlingHttpServletRequest(resourceResolver, context.bundleContext());

        // WHEN
        modelDatasourceServlet.doGet(request, context.response());

        // THEN
        SimpleDataSource simpleDataSource = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        assertThat(simpleDataSource.iterator().hasNext(), CoreMatchers.is(true));
        ValueMap valueMap = simpleDataSource.iterator().next().getValueMap();
        assertThat(valueMap.get("text"), CoreMatchers.is("YetAnotherCFModel"));
        assertThat(valueMap.get("value"), CoreMatchers.is("/conf/foobar/settings/dam/cfm/models/yetanothercfmodel"));
    }
}
