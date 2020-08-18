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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Collections;
import java.util.Objects;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ListImplTest {

    private static final String TEST_BASE = "/list";
    private static final String CONTENT_ROOT = "/content";
    private static final String CURRENT_PAGE = "/content/list";

    private static final String TEST_PAGE_CONTENT_ROOT = CURRENT_PAGE + "/jcr:content/root";
    private static final String LIST_1 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    private static final String LIST_2 = TEST_PAGE_CONTENT_ROOT + "/staticListType";
    private static final String LIST_3 = TEST_PAGE_CONTENT_ROOT + "/childrenListType";
    private static final String LIST_4 = TEST_PAGE_CONTENT_ROOT + "/childrenListTypeWithDepth";
    private static final String LIST_5 = TEST_PAGE_CONTENT_ROOT + "/tagsListType";
    private static final String LIST_6 = TEST_PAGE_CONTENT_ROOT + "/searchListType";
    private static final String LIST_7 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListType";
    private static final String LIST_8 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleDescListType";
    private static final String LIST_9 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListType";
    private static final String LIST_10 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateDescListType";
    private static final String LIST_11 = TEST_PAGE_CONTENT_ROOT + "/staticMaxItemsListType";
    private static final String LIST_12 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDate";
    private static final String LIST_13 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByModificationDateListTypeWithNoModificationDateForOneItem";
    private static final String LIST_14 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitle";
    private static final String LIST_15 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithNoTitleForOneItem";
    private static final String LIST_16 = TEST_PAGE_CONTENT_ROOT + "/staticOrderByTitleListTypeWithAccent";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_TAGS_JSON, CONTENT_ROOT + "/cq:tags/list");
    }

    @Test
    public void testProperties() {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_1));
    }

    @Test
    public void testStaticListType() {
        List list = getListUnderTest(LIST_2);
        checkListConsistencyByPaths(list, new String[]{
            "/content/list/pages/page_1",
            "/content/list/pages/page_2",
        });
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_2));
    }

    @Test
    public void testChildrenListType() {
        List list = getListUnderTest(LIST_3);
        checkListConsistencyByPaths(list, new String[]{
            "/content/list/pages/page_1/page_1_1",
            "/content/list/pages/page_1/page_1_2",
            "/content/list/pages/page_1/page_1_3",
        });
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_3));
    }

    @Test
    public void testChildrenListTypeWithDepth() {
        List list = getListUnderTest(LIST_4);
        checkListConsistencyByPaths(list, new String[]{
            "/content/list/pages/page_1/page_1_1",
            "/content/list/pages/page_1/page_1_2",
            "/content/list/pages/page_1/page_1_2/page_1_2_1",
            "/content/list/pages/page_1/page_1_3",

        });
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_4));
    }

    @Test
    public void testTagsListType() {
        List list = getListUnderTest(LIST_5);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1/page_1_3"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_5));
    }

    @Test
    public void testSearchListType() throws Exception {
        Session mockSession = mock(Session.class);
        SimpleSearch mockSimpleSearch = mock(SimpleSearch.class);
        context.registerAdapter(ResourceResolver.class, Session.class, mockSession);
        context.registerAdapter(Resource.class, SimpleSearch.class, mockSimpleSearch);
        SearchResult searchResult = mock(SearchResult.class);

        when(mockSimpleSearch.getResult()).thenReturn(searchResult);
        when(searchResult.getResources()).thenReturn(
            Collections.singletonList(Objects.requireNonNull(
                context.resourceResolver().getResource("/content/list/pages/page_1/jcr:content")))
                .iterator());

        List list = getListUnderTest(LIST_6);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_6));
    }

    @Test
    public void testOrderBy() {
        List list = getListUnderTest(LIST_7);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_7));
    }

    @Test
    public void testOrderDescBy() {
        List list = getListUnderTest(LIST_8);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_8));
    }

    @Test
    public void testOrderByModificationDate() {
        List list = getListUnderTest(LIST_9);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_9));
    }

    @Test
    public void testOrderByModificationDateDesc() {
        List list = getListUnderTest(LIST_10);
        checkListConsistencyByTitle(list, new String[]{"Page 1", "Page 2"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_10));
    }

    @Test
    public void testMaxItems() {
        List list = getListUnderTest(LIST_11);
        checkListConsistencyByTitle(list, new String[]{"Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_11));
    }

    @Test
    public void testOrderByModificationDateWithNoModificationDate() {
        List list = getListUnderTest(LIST_12);
        checkListConsistencyByTitle(list, new String[]{"Page 1.1", "Page 1.2"});
    }

    @Test
    public void testOrderByModificationDateWithNoModificationDateForOneItem() {
        List list = getListUnderTest(LIST_13);
        checkListConsistencyByTitle(list, new String[]{"Page 2", "Page 1", "Page 1.2"});
    }

    @Test
    public void testOrderByTitleWithNoTitle() {
        List list = getListUnderTest(LIST_14);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_3", "/content/list/pages/page_4"});
    }

    @Test
    public void testOrderByTitleWithNoTitleForOneItem() {
        List list = getListUnderTest(LIST_15);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1", "/content/list/pages/page_2", "/content/list/pages/page_4"});
    }

    @Test
    public void testOrderByTitleWithAccent() {
        List list = getListUnderTest(LIST_16);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1", "/content/list/pages/page_5", "/content/list/pages/page_2"});
    }

    private List getListUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to defines test resource " + resourcePath + "?");
        }
        context.currentResource(resource);
        return context.request().adaptTo(List.class);
    }

    private void checkListConsistencyByTitle(List list, String[] expectedPageTitles) {
        assertArrayEquals(expectedPageTitles, list.getItems().stream().map(Page::getTitle).toArray());
    }

    private void checkListConsistencyByPaths(List list, String[] expectedPagePaths) {
        assertArrayEquals(expectedPagePaths, list.getItems().stream().map(Page::getPath).toArray());
    }
}
