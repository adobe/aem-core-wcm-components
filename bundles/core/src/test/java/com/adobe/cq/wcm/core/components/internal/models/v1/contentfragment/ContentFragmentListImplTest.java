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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ContentFragmentListImplTest extends AbstractContentFragmentTest<ContentFragmentList> {

    private static final String TEST_BASE = "/contentfragmentlist";
    private static final String NO_MODEL = "no-model";
    private static final String MODEL_PATH_AND_TAGS = "model-path-tags";
    private static final String MODEL_ELEMENTS = "model-elements";
    private static final String NON_EXISTING_MODEL = "non-existing-model";
    private static final String NON_EXISTING_MODEL_WITH_PATH_AND_TAGS = "non-existing-model-path-tags";
    private static final String MODEL_MAX_LIMIT = "model-max-limit";
    private static final String MODEL_ORDER_BY = "model-order-by";
    private static final String DEFAULT_NO_MAX_LIMIT_SET = "-1";

    private ResourceResolver leakingResourceResolverMock;

    @Override
    protected Class<ContentFragmentList> getClassType() {
        return ContentFragmentList.class;
    }

    @Override
    protected String getTestResourcesParentPath() {
        return "/content/tests/contentfragmentlist/jcr:content/root/responsivegrid";
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();

        // Load additional content for content list model
        context.load().json(TEST_BASE + "/test-content.json", "/content/tests");
        context.load().json("/contentfragmentlist/test-content-dam-contentfragments.json", "/content/dam/contentfragments-for-list");

        Query query = Mockito.mock(Query.class);
        SearchResult searchResult = Mockito.mock(SearchResult.class);
        Iterator<Resource> iterator = Mockito.mock(Iterator.class);
        Resource resource = Mockito.mock(Resource.class);

        leakingResourceResolverMock = Mockito.mock(ResourceResolver.class);

        when(query.getResult()).thenReturn(searchResult);
        when(searchResult.getResources()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(resource);
        when(resource.getResourceResolver()).thenReturn(leakingResourceResolverMock);
        when(queryBuilderMock.createQuery(Mockito.any(PredicateGroup.class), Mockito.any(Session.class)))
                .thenReturn(query);
    }

    @Test
    void testListWithNoModel() {
        ContentFragmentList contentFragmentList = getModelInstanceUnderTest(NO_MODEL);
        assertNotNull(contentFragmentList);
        assertEquals(contentFragmentList.getListItems().size(), 0);
        assertEquals(ContentFragmentListImpl.RESOURCE_TYPE_V1, contentFragmentList.getExportedType());
        Utils.testJSONExport(contentFragmentList, Utils.getTestExporterJSONPath(TEST_BASE, NO_MODEL));
    }

    @Test
    void testListWithOneFragmentWithoutElements() {
        testListWithOneFragment(MODEL_PATH_AND_TAGS);
    }

    @Test
    void testListWithOneFragmentWithElements() {
        testListWithOneFragment(MODEL_ELEMENTS);
    }

    @SuppressWarnings("unchecked")
    private void testListWithOneFragment(String listName) {
        Resource resource = context.resourceResolver().getResource("/content/dam/contentfragments-for-list/text-only");
        if (resource != null) {
            Resource DAMFragment = Mockito.spy(resource);
            Query query = Mockito.mock(Query.class);
            SearchResult searchResult = Mockito.mock(SearchResult.class);
            Iterator<Resource> iterator = Mockito.mock(Iterator.class);
            ResourceResolver spyResolver = Mockito.spy(DAMFragment.getResourceResolver());

            when(query.getResult()).thenReturn(searchResult);
            when(searchResult.getResources()).thenReturn(iterator);
            when(iterator.hasNext()).thenReturn(true, false);
            when(iterator.next()).thenReturn(DAMFragment);
            when(DAMFragment.getResourceResolver()).thenReturn(spyResolver);
            Mockito.doNothing().when(spyResolver).close();
            when(queryBuilderMock.createQuery(Mockito.any(PredicateGroup.class), Mockito.any(Session.class))).thenReturn(query);

            ContentFragmentList contentFragmentList = getModelInstanceUnderTest(listName);
            assertEquals(ContentFragmentListImpl.RESOURCE_TYPE_V1, contentFragmentList.getExportedType());
            assertEquals(contentFragmentList.getListItems().size(), 1);
            Utils.testJSONExport(contentFragmentList, Utils.getTestExporterJSONPath(TEST_BASE, listName));

            Mockito.doCallRealMethod().when(spyResolver).close();
        }
    }

    @Test
    void verifyQueryBuilderInteractionWhenNonExistingModelIsGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap<>();
        expectedPredicates.put("path", ImmutableMap.of("path", ContentFragmentListImpl.DEFAULT_DAM_PARENT_PATH));
        expectedPredicates.put("type", ImmutableMap.of("type", "dam:Asset"));
        expectedPredicates.put("1_property", ImmutableMap.of(
                "property", "jcr:content/data/cq:model",
                "value", "foobar"));

        // WHEN
        getModelInstanceUnderTest(NON_EXISTING_MODEL);

        // THEN
        verifyPredicateGroup(expectedPredicates, DEFAULT_NO_MAX_LIMIT_SET);
    }

    @Test
    void verifyQueryBuilderInteractionWhenOrderByIsGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap<>();
        expectedPredicates.put("path", ImmutableMap.of("path", ContentFragmentListImpl.DEFAULT_DAM_PARENT_PATH));
        expectedPredicates.put("type", ImmutableMap.of("type", "dam:Asset"));
        expectedPredicates.put("1_property", ImmutableMap.of(
                "property", "jcr:content/data/cq:model",
                "value", "foobar"));
        expectedPredicates.put("orderby", ImmutableMap.of(
                "orderby", "@main",
                "sort", "desc"));


        // WHEN
        getModelInstanceUnderTest(MODEL_ORDER_BY);

        // THEN
        verifyPredicateGroup(expectedPredicates, DEFAULT_NO_MAX_LIMIT_SET);
    }

    @Test
    void verifyLeakingResourceResolverIsClosed() {
        // GIVEN
        // WHEN
        getModelInstanceUnderTest(NON_EXISTING_MODEL);

        // THEN
        Mockito.verify(leakingResourceResolverMock).close();
    }

    @Test
    void verifyQueryBuilderInteractionWhenPathParameterAndTagsAreGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap<>();
        expectedPredicates.put("path", ImmutableMap.of("path", "/content/dam/some-other-parent-path"));
        expectedPredicates.put("type", ImmutableMap.of("type", "dam:Asset"));
        expectedPredicates.put("1_property", ImmutableMap.of(
                "property", "jcr:content/data/cq:model",
                "value", "foobar"));
        expectedPredicates.put("tagid", ImmutableMap.of(
                "property", "jcr:content/metadata/cq:tags",
                "1_value", "quux"));

        // WHEN
        getModelInstanceUnderTest(NON_EXISTING_MODEL_WITH_PATH_AND_TAGS);

        // THEN
        verifyPredicateGroup(expectedPredicates, DEFAULT_NO_MAX_LIMIT_SET);
    }

    @Test
    void verifyQueryBuilderInteractionWhenMaxLimitIsGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap<>();
        expectedPredicates.put("path", ImmutableMap.of("path", ContentFragmentListImpl.DEFAULT_DAM_PARENT_PATH));
        expectedPredicates.put("type", ImmutableMap.of("type", "dam:Asset"));
        expectedPredicates.put("1_property", ImmutableMap.of(
                "property", "jcr:content/data/cq:model",
                "value", "foobar"));

        //Expected Max Limit
        String expectedLimit = "20";

        // WHEN
        getModelInstanceUnderTest(MODEL_MAX_LIMIT);

        // THEN
        verifyPredicateGroup(expectedPredicates, expectedLimit);
    }

    /**
     * Verifies that given expected predicates have been set on
     * {@link com.day.cq.search.QueryBuilder#createQuery(PredicateGroup, Session)} call.
     */
    private void verifyPredicateGroup(final Map<String, Map<String, String>> expectedPredicates, String expectedLimit) {
        Mockito.verify(queryBuilderMock).createQuery(ArgumentMatchers.argThat(argument -> {

            //Check the result limit
            String actualLimit = argument.get(PredicateGroup.PARAM_LIMIT);
            if (actualLimit == null || !actualLimit.equals(expectedLimit)) {
                return false;
            }

            for (String predicateName : expectedPredicates.keySet()) {
                Predicate predicate = argument.getByName(predicateName);
                for (String predicateParameterName : expectedPredicates.get(predicateName).keySet()) {
                    String predicateParameterValue = predicate.getParameters().get(predicateParameterName);
                    String expectedPredicateParameterValue =
                            expectedPredicates.get(predicateName).get(predicateParameterName);
                    if (!predicateParameterValue.equals(expectedPredicateParameterValue)) {
                        return false;
                    }
                }
            }
            return true;
        }), Mockito.any(Session.class));
    }
}
