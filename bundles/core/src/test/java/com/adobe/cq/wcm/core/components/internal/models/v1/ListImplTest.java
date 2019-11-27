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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class ListImplTest {

    private static final String TEST_BASE = "/list";
    protected static final String LIST_1 = "/content/list/listTypes/staticListType";
    protected static final String LIST_2 = "/content/list/listTypes/staticListType";
    protected static final String LIST_3 = "/content/list/listTypes/childrenListType";
    protected static final String LIST_4 = "/content/list/listTypes/childrenListTypeWithDepth";
    protected static final String LIST_5 = "/content/list/listTypes/tagsListType";
    protected static final String LIST_6 = "/content/list/listTypes/searchListType";
    protected static final String LIST_7 = "/content/list/listTypes/staticOrderByTitleListType";
    protected static final String LIST_8 = "/content/list/listTypes/staticOrderByTitleDescListType";
    protected static final String LIST_9 = "/content/list/listTypes/staticOrderByModificationDateListType";
    protected static final String LIST_10 = "/content/list/listTypes/staticOrderByModificationDateDescListType";
    protected static final String LIST_11 = "/content/list/listTypes/staticMaxItemsListType";
    protected static final String LIST_12 = "/content/list/listTypes/staticOrderByModificationDateListTypeWithNoModificationDate";
    protected static final String LIST_13 = "/content/list/listTypes/staticOrderByModificationDateListTypeWithNoModificationDateForOneItem";
    protected static final String LIST_14 = "/content/list/listTypes/staticOrderByTitleListTypeWithNoTitle";
    protected static final String LIST_15 = "/content/list/listTypes/staticOrderByTitleListTypeWithNoTitleForOneItem";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    protected String testBase;

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        internalSetup();
    }

    protected void internalSetup() {
        context.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, "/content/list");
        context.load().json(testBase + "/test-etc.json", "/etc/tags/list");
    }

    @Test
    protected void testProperties() throws Exception {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_1));
    }

    @Test
    protected void testStaticListType() {
        List list = getListUnderTest(LIST_2);
        assertEquals(2, list.getItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_2));
    }

    @Test
    protected void testChildrenListType() throws Exception {
        List list = getListUnderTest(LIST_3);
        assertEquals(3, list.getItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_3));
    }

    @Test
    protected void testChildrenListTypeWithDepth() throws Exception {
        List list = getListUnderTest(LIST_4);
        assertEquals(4, list.getItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_4));
    }

    @Test
    protected void testTagsListType() throws Exception {
        List list = getListUnderTest(LIST_5);
        assertEquals(1, list.getItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_5));
    }

    @Test
    protected void testSearchListType() throws Exception {
        Session mockSession = mock(Session.class);
        SimpleSearch mockSimpleSearch = mock(SimpleSearch.class);
        context.registerAdapter(ResourceResolver.class, Session.class, mockSession);
        context.registerAdapter(Resource.class, SimpleSearch.class, mockSimpleSearch);
        SearchResult searchResult = mock(SearchResult.class);
        Hit hit = mock(Hit.class);

        when(mockSimpleSearch.getResult()).thenReturn(searchResult);
        when(searchResult.getHits()).thenReturn(Collections.singletonList(hit));
        Resource contentResource = context.resourceResolver().getResource("/content/list/pages/page_1/jcr:content");
        when(hit.getResource()).thenReturn(contentResource);

        List list = getListUnderTest(LIST_6);
        assertEquals(1, list.getItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_6));
    }

    @Test
    protected void testOrderBy() throws Exception {
        List list = getListUnderTest(LIST_7);
        checkListConsistency(list, new String[]{"Page 1", "Page 2"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_7));
    }

    @Test
    protected void testOrderDescBy() throws Exception {
        List list = getListUnderTest(LIST_8);
        checkListConsistency(list, new String[]{"Page 2", "Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_8));
    }

    @Test
    protected void testOrderByModificationDate() throws Exception {
        List list = getListUnderTest(LIST_9);
        checkListConsistency(list, new String[]{"Page 2", "Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_9));
    }

    @Test
    protected void testOrderByModificationDateDesc() throws Exception {
        List list = getListUnderTest(LIST_10);
        checkListConsistency(list, new String[]{"Page 1", "Page 2"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_10));
    }

    @Test
    protected void testMaxItems() throws Exception {
        List list = getListUnderTest(LIST_11);
        checkListConsistency(list, new String[]{"Page 1"});
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(testBase, LIST_11));
    }

    @Test
    protected void testOrderByModificationDateWithNoModificationDate() throws Exception {
        List list = getListUnderTest(LIST_12);
        checkListConsistency(list, new String[]{"Page 1.1", "Page 1.2"});
    }

    @Test
    protected void testOrderByModificationDateWithNoModificationDateForOneItem() throws Exception {
        List list = getListUnderTest(LIST_13);
        checkListConsistency(list, new String[]{"Page 2", "Page 1", "Page 1.2"});
    }

    @Test
    protected void testOrderByTitleWithNoTitle() throws Exception {
        List list = getListUnderTest(LIST_14);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_3", "/content/list/pages/page_4"});
    }

    @Test
    protected void testOrderByTitleWithNoTitleForOneItem() throws Exception {
        List list = getListUnderTest(LIST_15);
        checkListConsistencyByPaths(list, new String[]{"/content/list/pages/page_1", "/content/list/pages/page_2", "/content/list/pages/page_4"});
    }

    protected List getListUnderTest(String resourcePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to defines test resource " + resourcePath + "?");
        }
        context.currentResource(resource);
        return context.request().adaptTo(List.class);
    }

    protected void checkListConsistency(List list, String[] expectedPages) {
        assertTrue(list.getItems().size() == expectedPages.length,
                "Expected that the returned list will contain " + expectedPages.length + " items");
        int index = 0;
        for (Page item : list.getItems()) {
            assertEquals(expectedPages[index++], item.getTitle());
        }
    }

    protected void checkListConsistencyByPaths(List list, String[] expectedPagePaths) {
        assertTrue(list.getItems().size() == expectedPagePaths.length,
                "Expected that the returned list will contain " + expectedPagePaths.length + " items");
        int index = 0;
        for (Page item : list.getItems()) {
            assertEquals(expectedPagePaths[index++], item.getPath());
        }
    }
}
