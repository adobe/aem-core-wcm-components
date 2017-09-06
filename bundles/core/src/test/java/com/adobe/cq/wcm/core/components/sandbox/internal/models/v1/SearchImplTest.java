/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.Arrays;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.sandbox.models.Search;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchImplTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/search", "/content");

    private static final String TEST_ROOT = "/content/search/page";

    @Mock
    private QueryBuilder mockQueryBuilder;

    @Mock
    private SearchResult mockSearchResult;

    @Mock
    private Query mockQuery;

    @Mock
    private Hit mockHit;

    private SlingBindings slingBindings;

    @Before
    public void setUp() {
        when(mockQueryBuilder.createQuery(any(), any())).thenReturn(mockQuery);
        when(mockQuery.getResult()).thenReturn(mockSearchResult);
        when(mockSearchResult.getHits()).thenReturn(Arrays.asList(new Hit[]{mockHit}));

        context.registerService(QueryBuilder.class, mockQueryBuilder);
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_STYLE, slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class
                .getName()));
        slingBindings.put(WCMBindings.CURRENT_PAGE, context.currentPage("/content/search/page"));
    }

    @Test
    public void testGetRootPath() throws Exception {
        Resource resource = context.currentResource(TEST_ROOT + "/jcr:content/search");
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        Search search = context.request().adaptTo(Search.class);
        assertEquals("/content/search", search.getRootPath());
    }


    @Test
    public void testGetResults() throws Exception {
        Resource resource = context.currentResource(TEST_ROOT + "/jcr:content/search");
        when(mockHit.getResource()).thenReturn(resource);
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        Search search = context.request().adaptTo(Search.class);
        assertEquals(1, search.getResults().size());
        for(Resource searchResult: search.getResults()) {
            ValueMap valueMap = searchResult.adaptTo(ValueMap.class);
            assertEquals("search", valueMap.get("title"));
            assertEquals("/content/search/page", valueMap.get("path"));
        }
    }
}