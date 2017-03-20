/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.models.impl.v1;

import java.util.Collections;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
import com.adobe.cq.wcm.core.components.context.MockStyle;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListImplTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/list", "/content/list");

    @Mock
    private Style mockStyle;

    @Mock
    private Session mockSession;

    @Mock
    private SimpleSearch mockSimpleSearch;

    private SlingBindings slingBindings;

    @Before
    public void setUp() throws Exception {
        context.load().json("/list/test-etc.json", "/etc/tags/list");
        context.registerAdapter(ResourceResolver.class, Session.class, mockSession);
        context.registerAdapter(Resource.class, SimpleSearch.class, mockSimpleSearch);
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_STYLE, mockStyle);
        slingBindings.put(WCMBindings.CURRENT_PAGE, context.currentPage("/content/list"));
    }

    @Test
    public void testProperties() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
    }

    @Test
    public void testStaticListType() {
        Resource resource = context.currentResource("/content/list/listTypes/staticListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        assertEquals(2, list.getItems().size());
    }

    @Test
    public void testChildrenListType() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/childrenListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        assertEquals(3, list.getItems().size());
    }

    @Test
    public void testChildrenListTypeWithDepth() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/childrenListTypeWithDepth");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        assertEquals(4, list.getItems().size());
    }

    @Test
    public void testTagsListType() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/tagsListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        assertEquals(1, list.getItems().size());
    }

    @Test
    public void testSearchListType() throws Exception {
        SearchResult searchResult = mock(SearchResult.class);
        Hit hit = mock(Hit.class);

        when(mockSimpleSearch.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        Resource contentResource = context.currentResource("/content/list/pages/page_1/jcr:content");
        when(hit.getResource()).thenReturn(contentResource);

        Resource resource = context.currentResource("/content/list/listTypes/searchListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));

        List list = context.request().adaptTo(List.class);
        assertEquals(1, list.getItems().size());
    }

    @Test
    public void testOrderBy() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticOrderByTitleListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        checkListConsistency(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    public void testOrderDescBy() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticOrderByTitleDescListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list2 = context.request().adaptTo(List.class);
        checkListConsistency(list2, new String[]{"Page 2", "Page 1"});
    }

    @Test
    public void testOrderByModificationDate() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticOrderByModificationDateListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        checkListConsistency(list, new String[]{"Page 2", "Page 1"});
    }

    @Test
    public void testOrderByModificationDateDesc() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticOrderByModificationDateDescListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        checkListConsistency(list, new String[]{"Page 1", "Page 2"});
    }

    @Test
    public void testMaxItems() throws Exception {
        Resource resource = context.currentResource("/content/list/listTypes/staticMaxItemsListType");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        List list = context.request().adaptTo(List.class);
        checkListConsistency(list, new String[]{"Page 1"});
    }

    private void checkListConsistency(List list, String[] expectedPages) {
        assertTrue("Expected that the returned list will contain " + expectedPages.length + " items",
                list.getItems().size() == expectedPages.length);
        int index = 0;
        for (Page item : list.getItems()) {
            assertEquals(expectedPages[index++], item.getTitle());
        }
    }
}