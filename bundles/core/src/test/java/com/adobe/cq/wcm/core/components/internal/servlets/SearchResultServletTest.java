/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SearchResultServletTest {

    private static final String TEST_BASE = "/search";
    private static final String CONTENT_ROOT = "/content";

    private SearchResultServlet underTest;

    @Mock
    private QueryBuilder mockQueryBuilder;

    @Mock
    private LiveRelationshipManager mockLiveRelationshipManager;

    private ResourceResolver spyResolver;

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private static final String TEST_ROOT_EN = "/content/en/search/page";
    private static final String TEST_ROOT_DE = "/content/de/search/page";
    private static final String TEST_TEMPLATE_EN = "/content/en/search/page-template";
    private static final String TEST_XF_PAGE_EN = "/content/en/search/xf-page";
    private static final String TEST_XF_TEMPLATE_EN = "/content/en/search/xf-page-template";
    protected static final String CONTEXT_PATH = "/test-cp";

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + "/test-conf.json", "/conf/test/settings/wcm/templates");

        context.registerService(QueryBuilder.class, mockQueryBuilder);
        context.registerService(LiveRelationshipManager.class, mockLiveRelationshipManager);
        context.request().setContextPath(CONTEXT_PATH);
        underTest = context.registerInjectActivateService(new SearchResultServlet());
    }

    /**
     * Sets up a mock query builder.
     * If there are any results then `this.spyResolver` will be set to a non-null value.
     *
     * Note: any query executed on the query builder configured by calling this method will simply list all of the
     * child pages of the search path. No actual search, or other predicates on the query will be honoured. Therefore,
     * the results from the query builder cannot be trusted for testing purposes beyond testing the search root and
     * the handling / transformation of results.
     */
    public void setUpQueryBuilder() {
        doAnswer(invocationOnQueryBuilder -> {
            PredicateGroup predicateGroup = invocationOnQueryBuilder.getArgument(0);
            Query query = Mockito.mock(Query.class);
            doAnswer(invocationOnQuery -> {
                SearchResult result = Mockito.mock(SearchResult.class);
                doAnswer(invocationOnResult -> {
                    String searchPath = predicateGroup.getByName(PathPredicateEvaluator.PATH).get(PathPredicateEvaluator.PATH);
                    Iterator<Resource> res = Objects.requireNonNull(this.context.resourceResolver().getResource(searchPath)).listChildren();
                    List<Resource> resources = StreamSupport.stream(Spliterators.spliteratorUnknownSize(res, Spliterator.ORDERED), false)
                        .filter(r -> r.isResourceType("cq:Page"))
                        .collect(Collectors.toList());
                    if (resources.size() > 0) {
                        this.spyResolver = Mockito.spy(this.context.resourceResolver());
                        doNothing().when(spyResolver).close();
                        Resource spyResource = Mockito.spy(resources.get(0));
                        doAnswer(invocationOnMock3 -> spyResolver).when(spyResource).getResourceResolver();
                        resources.set(0, spyResource);
                    }
                    return resources.iterator();
                }).when(result).getResources();
                return result;
            }).when(query).getResult();
            return query;
        }).when(mockQueryBuilder).createQuery(any(), any());
    }

    @Test
    public void testSimpleSearch() throws Exception {
        setUpQueryBuilder();
        com.adobe.cq.wcm.core.components.Utils.enableDataLayer(context, true);
        context.currentResource(TEST_ROOT_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> expected = ImmutableList.of(
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page.html", "title", "Page", "id", "search-0dc87a6d22-item-2290228025"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page2.html", "title", "Page2", "id", "search-0dc87a6d22-item-ad3d190367"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page-template.html", "title", "Page3", "id", "search-0dc87a6d22-item-1abc47fffe"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page.html", "title", "XF Page", "id", "search-0dc87a6d22-item-2246ed81a7"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page-template.html", "title", "XF Page Template", "id", "search-0dc87a6d22-item-7345474d48"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search.html", "title", "Search", "id", "search-0dc87a6d22-item-04a609b18b")
        );

        validateResponse(context.response(), expected);
        verify(this.spyResolver, atLeastOnce()).close();
    }

    /**
     * Confirms that the servlet returns 404 status if the result offset is not a long.
     *
     * @throws Exception any exception.
     */
    @Test
    public void testSimpleSearch_badOffset() throws Exception {
        context.currentResource(TEST_ROOT_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod&"
            + SearchResultServlet.PARAM_RESULTS_OFFSET + "=3x");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, context.response().getStatus());
    }

    @Test
    public void testSimpleSearch_tooShortQuery() throws Exception {
        context.currentResource(TEST_ROOT_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yo");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());

        validateResponse(context.response(), new ArrayList<>());
    }

    @Test
    public void testSimpleSearch_default() throws Exception {
        setUpQueryBuilder();
        context.currentResource(TEST_ROOT_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        underTest.doGet(request, context.response());
        List<Map<String, String>> expected = ImmutableList.of(
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search.html", "title", "Search", "id", "-item-277e64839a")
        );

        validateResponse(context.response(), expected);
    }

    @Test
    public void testSimpleSearch_noPath() throws Exception {
        setUpQueryBuilder();
        context.currentResource(TEST_ROOT_DE);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());

        validateResponse(context.response(), new ArrayList<>());
    }

    @Test
    public void testTemplateBasedSearch() throws Exception {
        setUpQueryBuilder();
        com.adobe.cq.wcm.core.components.Utils.enableDataLayer(context, true);
        context.currentResource(TEST_TEMPLATE_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=yod");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> expected = ImmutableList.of(
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page.html", "title", "Page", "id", "search-ea349504cd-item-2290228025"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page2.html", "title", "Page2", "id", "search-ea349504cd-item-ad3d190367"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/page-template.html", "title", "Page3", "id", "search-ea349504cd-item-1abc47fffe"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page.html", "title", "XF Page", "id", "search-ea349504cd-item-2246ed81a7"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page-template.html", "title", "XF Page Template", "id", "search-ea349504cd-item-7345474d48"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search.html", "title", "Search", "id", "search-ea349504cd-item-04a609b18b")
        );

        validateResponse(context.response(), expected);
        verify(this.spyResolver, atLeastOnce()).close();
    }

    @Test
    public void testXFSearch() throws Exception {
        setUpQueryBuilder();
        com.adobe.cq.wcm.core.components.Utils.enableDataLayer(context, true);
        context.currentResource(TEST_XF_PAGE_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=found");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> expected = ImmutableList.of(
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page/searched-tree/found-01.html", "title", "Found 01", "id", "search-1f0d0a33b1-item-7ae4fa6340"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page/searched-tree/found-02.html", "title", "Found 02", "id", "search-1f0d0a33b1-item-05b6f78858"));

        validateResponse(context.response(), expected);
    }

    @Test
    public void testXFTemplateSearch() throws Exception {
        setUpQueryBuilder();
        com.adobe.cq.wcm.core.components.Utils.enableDataLayer(context, true);
        context.currentResource(TEST_XF_TEMPLATE_EN);
        MockSlingHttpServletRequest request = context.request();
        request.setQueryString(SearchResultServlet.PARAM_FULLTEXT + "=found");
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("jcr:content/search");
        underTest.doGet(request, context.response());
        List<Map<String, String>> expected = ImmutableList.of(
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page/searched-tree/found-01.html", "title", "Found 01", "id", "search-1f0d0a33b1-item-7ae4fa6340"),
            ImmutableMap.of("url", CONTEXT_PATH + "/content/en/search/xf-page/searched-tree/found-02.html", "title", "Found 02", "id", "search-1f0d0a33b1-item-05b6f78858"));

        validateResponse(context.response(), expected);
    }

    @SuppressWarnings("deprecation")
    private void validateResponse(MockSlingHttpServletResponse response, List<Map<String, String>> expected) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(ListItem.class, Item.class);
        SimpleModule module = new SimpleModule();
        module.setAbstractTypes(resolver);
        mapper.registerModule(module);
        ListItem[] listItems = mapper.readValue(response.getOutputAsString(), ListItem[].class);
        List<Map<String, String>> actual = new LinkedList<>();
        for (ListItem item : listItems) {
            actual.add(ImmutableMap.of("url", item.getURL(), "title", item.getTitle(), "id", item.getId()));
        }
        assertEquals(expected, actual);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Item implements ListItem {
        private Link link;
        private String id;
        private String url;
        private String title;

        public Item() {
        }

        @Nullable
        @Override
        public String getId() {
            return id;
        }

        @Override
        public @NotNull Link getLink() {
            return link;
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

    private static class Link implements com.adobe.cq.wcm.core.components.commons.link.Link {

        private boolean valid;
        private String url;
        private Map<String,String> htmlAttributes;

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public @Nullable String getURL() {
            return url;
        }

        @Override
        public @Nullable String getMappedURL() { return null; }

        @Override
        public @NotNull Map<String, String> getHtmlAttributes() {
            return htmlAttributes;
        }
    }

}
