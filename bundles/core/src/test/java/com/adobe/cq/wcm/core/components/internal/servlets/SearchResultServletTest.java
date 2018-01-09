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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.testing.MockLanguageManager;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchResultServletTest {

    private static final String TEST_BASE = "/search";

    private SearchResultServlet underTest;

    @Mock
    private QueryBuilder mockQueryBuilder;

    @Mock
    private LiveRelationshipManager mockLiveRelationshipManager;

    @Mock
    private SearchResult mockSearchResult;

    @Mock
    private Query mockQuery;

    @Mock
    private Hit mockHit;

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    private static final String TEST_ROOT_EN = "/content/en/search/page";
    private static final String TEST_TEMPLATE_EN = "/content/en/search/page-template";
    private static final String TEST_ROOT_DE = "/content/de";

    @Before
    public void setUp() throws Exception {
        context.load().json(TEST_BASE + "/test-conf.json", "/conf/test/settings/wcm/templates");
        underTest = new SearchResultServlet();
        when(mockQueryBuilder.createQuery(any(), any())).thenReturn(mockQuery);
        when(mockQuery.getResult()).thenReturn(mockSearchResult);
        when(mockSearchResult.getHits()).thenReturn(Arrays.asList(new Hit[]{mockHit}));
        Whitebox.setInternalState(underTest, "queryBuilder", mockQueryBuilder);
        Whitebox.setInternalState(underTest, "languageManager", new MockLanguageManager());
        Whitebox.setInternalState(underTest, "relationshipManager", mockLiveRelationshipManager);
    }

    @Test
    public void testSimpleSearch() throws Exception {
        Resource resource = context.currentResource(TEST_ROOT_EN);
        when(mockHit.getResource()).thenReturn(resource);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> exected = ImmutableList.of(
                ImmutableMap.of(
                        "url", "null/content/en/search/page.html",
                        "title", "Page"
                )
        );

        validateResponse(context.response(), exected);
    }

    @Test
    public void testTemplateBasedSearch() throws Exception {
        Resource resource = context.currentResource(TEST_TEMPLATE_EN);
        when(mockHit.getResource()).thenReturn(resource);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> exected = ImmutableList.of(
                ImmutableMap.of(
                        "url", "null/content/en/search/page-template.html",
                        "title", "Page"
                )
        );

        validateResponse(context.response(), exected);
    }

    private void validateResponse(MockSlingHttpServletResponse response, List<Map<String, String>> exected) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(ListItem.class, Item.class);
        SimpleModule module = new SimpleModule();
        module.setAbstractTypes(resolver);
        mapper.registerModule(module);
        ListItem[] listItems = mapper.readValue(response.getOutputAsString(), ListItem[].class);
        assertEquals(exected.size(), listItems.length);

        for (int i = 0; i < exected.size(); i++) {
            Map<String, String> expectedMap = exected.get(i);
            ListItem listItem = listItems[i];
            assertEquals(expectedMap.get("url"), listItem.getURL());
            assertEquals(expectedMap.get("title"), listItem.getTitle());

        }
    }

    private static class Item implements ListItem {
        private String url;
        private String title;
        private String path;
        private String description;
        private String lastModified;

        public Item() {
        }

        @Nullable
        @Override
        public String getURL() {
            return url;
        }

        @Nullable
        @Override
        public String getTitle() {
            return title;
        }
    }
}