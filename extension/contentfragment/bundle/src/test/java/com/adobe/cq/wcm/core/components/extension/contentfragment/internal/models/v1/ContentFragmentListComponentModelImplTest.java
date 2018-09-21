/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1;

import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.AbstractContentFragmentTest;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.ContentFragmentListComponentModel;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.SearchResult;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ContentFragmentListComponentModelImplTest extends AbstractContentFragmentTest<ContentFragmentListComponentModel> {

    private static final String NO_MODEL = "no-model";
    private static final String NON_EXISTING_MODEL = "non-existing-model";
    private static final String NON_EXISTING_MODEL_WITH_PATH_AND_TAGS = "non-existing-module-path-tags";

    private ResourceResolver leakingResourceResolverMock;

    @Override
    protected Class<ContentFragmentListComponentModel> getClassType() {
        return ContentFragmentListComponentModel.class;
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

        Mockito.when(query.getResult()).thenReturn(searchResult);
        Mockito.when(searchResult.getResources()).thenReturn(iterator);
        Mockito.when(iterator.hasNext()).thenReturn(true, false);
        Mockito.when(iterator.next()).thenReturn(resource);
        Mockito.when(resource.getResourceResolver()).thenReturn(leakingResourceResolverMock);
        Mockito.when(queryBuilderMock.createQuery(Mockito.any(PredicateGroup.class), Mockito.any(Session.class)))
            .thenReturn(query);
    }

    @Test
    public void verifyNoModel() {
        // GIVEN

        // WHEN
        ContentFragmentListComponentModel contentFragmentListComponentModel = getModelInstanceUnderTest(NO_MODEL);

        // THEN
        Assert.assertThat(contentFragmentListComponentModel, CoreMatchers.not(CoreMatchers.nullValue()));
    }

    @Test
    public void verifyQueryBuilderInteractionWhenNonExistingModelIsGiven() {
        // GIVEN
        // Expected predicate parameters
        Map<String, Map<String, String>> expectedPredicates = new HashMap();
        expectedPredicates.put("path", ImmutableMap.of("path", ContentFragmentListComponentModelImpl.DEFAULT_DAM_PARENT_PATH));
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
        ContentFragmentListComponentModel contentFragmentListComponentModel = getModelInstanceUnderTest(NO_MODEL);

        // THEN
        Assert.assertThat(contentFragmentListComponentModel.getExportedType(),
            CoreMatchers.is(ContentFragmentListComponentModelImpl.RESOURCE_TYPE));
        Assert.assertThat(contentFragmentListComponentModel.getListItems(), IsEmptyCollection.empty());
    }

    /**
     * Verifies that given expected predicates have been set on
     * {@link com.day.cq.search.QueryBuilder#createQuery(PredicateGroup, Session)} call.
     */
    private void verifyPredicateGroup(final Map<String, Map<String, String>> expectedPredicates) {
        Mockito.verify(queryBuilderMock).createQuery(Matchers.argThat(new ArgumentMatcher<PredicateGroup>() {
            @Override
            public boolean matches(Object argument) {
                PredicateGroup predicateGroup = (PredicateGroup) argument;
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
            }
        }), Mockito.any(Session.class));
    }
}
