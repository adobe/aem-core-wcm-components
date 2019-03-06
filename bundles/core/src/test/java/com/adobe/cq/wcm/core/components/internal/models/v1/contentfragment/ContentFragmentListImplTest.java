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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ContentFragmentListImplTest extends AbstractContentFragmentTest<ContentFragmentList> {

    private static final String NO_MODEL = "no-model";
    private static final String NON_EXISTING_MODEL = "non-existing-model";
    private static final String NON_EXISTING_MODEL_WITH_PATH_AND_TAGS = "non-existing-module-path-tags";

    private ResourceResolver leakingResourceResolverMock;

    @Override
    protected Class<ContentFragmentList> getClassType() {
        return ContentFragmentList.class;
    }

    @Override
    protected String getTestResourcesParentPath() {
        return "/content/list/contentfragments/jcr:content/root/responsivegrid";
    }

    @BeforeClass
    public static void beforeClass() {
        AbstractContentFragmentTest.beforeClass();

        // Load additional content for content list model
        AEM_CONTEXT.load().json("/contentfragmentlist/test-content.json", "/content/list");
    }

    @Before
    public void setUp() {
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
    public void verifyNoModel() {
        // GIVEN

        // WHEN
        ContentFragmentList contentFragmentList = getModelInstanceUnderTest(NO_MODEL);

        // THEN
        assertThat(contentFragmentList, not(nullValue()));
    }

    @Test
    public void verifyQueryBuilderInteractionWhenNonExistingModelIsGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap();
        expectedPredicates.put("path", ImmutableMap.of("path", ContentFragmentListImpl.DEFAULT_DAM_PARENT_PATH));
        expectedPredicates.put("type", ImmutableMap.of("type", "dam:Asset"));
        expectedPredicates.put("1_property", ImmutableMap.of(
                "property", "jcr:content/data/cq:model",
                "value", "foobar"));

        // WHEN
        getModelInstanceUnderTest(NON_EXISTING_MODEL);

        // THEN
        verifyPredicateGroup(expectedPredicates);
    }

    @Test
    public void verifyLeakingResourceResolverIsClosed() {
        // GIVEN
        // WHEN
        getModelInstanceUnderTest(NON_EXISTING_MODEL);

        // THEN
        Mockito.verify(leakingResourceResolverMock).close();
    }

    @Test
    public void verifyQueryBuilderInteractionWhenPathParameterAndTagsAreGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap();
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
        verifyPredicateGroup(expectedPredicates);
    }

    @Test
    public void verifyExportedTypeAndEmptyContentFragmentList() {
        // GIVEN

        // WHEN
        ContentFragmentList contentFragmentList = getModelInstanceUnderTest(NO_MODEL);

        // THEN
        assertThat(contentFragmentList.getExportedType(),
                is(ContentFragmentListImpl.RESOURCE_TYPE));
        assertThat(contentFragmentList.getListItems(), IsEmptyCollection.empty());
    }

    /**
     * Verifies that given expected predicates have been set on
     * {@link com.day.cq.search.QueryBuilder#createQuery(PredicateGroup, Session)} call.
     */
    private void verifyPredicateGroup(final Map<String, Map<String, String>> expectedPredicates) {
        Mockito.verify(queryBuilderMock).createQuery(ArgumentMatchers.argThat(argument -> {
            PredicateGroup predicateGroup = argument;
            for (String predicateName : expectedPredicates.keySet()) {
                Predicate predicate = predicateGroup.getByName(predicateName);
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
