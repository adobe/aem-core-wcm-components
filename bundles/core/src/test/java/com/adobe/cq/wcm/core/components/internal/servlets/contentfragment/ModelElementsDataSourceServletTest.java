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

import java.io.InputStream;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.google.common.collect.ImmutableMap;

import static com.adobe.cq.wcm.core.components.internal.servlets.contentfragment.ModelElementsDataSourceServlet.PARAMETER_AND_PN_MODEL_PATH;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class ModelElementsDataSourceServletTest {

    @Rule
    public SlingContext slingContext = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private ModelElementsDataSourceServlet modelElementsDataSourceServlet;

    @Before
    public void setUp() {
        modelElementsDataSourceServlet = new ModelElementsDataSourceServlet();
    }

    @Test
    public void verifyDataSourceWhenNoParameterIsGiven() {
        // GIVEN
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(slingContext.bundleContext());
        request.setResource(Mockito.mock(Resource.class));

        // WHEN
        modelElementsDataSourceServlet.doGet(request, slingContext.response());

        // THEN
        assertThat(request.getAttribute(DataSource.class.getName()), instanceOf(EmptyDataSource.class));
    }

    @Test
    public void verifyDataSourceWhenOrderByIsGiven() {
        // GIVEN
        InputStream jsonResourceAsStream = getClass().getResourceAsStream("test-content.json");
        slingContext.load().json(jsonResourceAsStream, "/conf/foobar/settings/dam/cfm/models/yetanothercfmodel");
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(slingContext.resourceResolver(),
                slingContext.bundleContext());
        Resource mockResource = Mockito.mock(Resource.class);
        when(mockResource.isResourceType(ModelElementsDataSourceServlet.RESOURCE_TYPE_ORDER_BY)).thenReturn(true);
        request.setResource(mockResource);
        request.setParameterMap(ImmutableMap.of(
                PARAMETER_AND_PN_MODEL_PATH, "/conf/foobar/settings/dam/cfm/models/yetanothercfmodel"));

        // WHEN
        modelElementsDataSourceServlet.doGet(request, slingContext.response());

        // THEN
        SimpleDataSource simpleDataSource = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        List<Resource> resourceList = IteratorUtils.toList(simpleDataSource.iterator());
        assertThat(resourceList, not(empty()));
        assertThat(resourceList, allOf(
                hasItem(resourceWithPropertiesTextAndValue("Created", "jcr:created")),
                hasItem(resourceWithPropertiesTextAndValue("Last Modified", "jcr:content/jcr:lastModified")),
                hasItem(resourceWithPropertiesTextAndValue("textFieldLabel", "jcr:content/data/master/textField")),
                hasItem(resourceWithPropertiesTextAndValue("multiTextField", "jcr:content/data/master/multiTextField"))));
    }

    @Test
    public void verifyDataSourceWhenModelParameterIsGiven() {
        // GIVEN
        InputStream jsonResourceAsStream = getClass().getResourceAsStream("test-content.json");
        slingContext.load().json(jsonResourceAsStream, "/conf/foobar/settings/dam/cfm/models/yetanothercfmodel");
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(slingContext.resourceResolver(),
                slingContext.bundleContext());
        request.setResource(Mockito.mock(Resource.class));
        request.setParameterMap(ImmutableMap.of(
                PARAMETER_AND_PN_MODEL_PATH, "/conf/foobar/settings/dam/cfm/models/yetanothercfmodel"));

        // WHEN
        modelElementsDataSourceServlet.doGet(request, slingContext.response());

        // THEN
        SimpleDataSource simpleDataSource = (SimpleDataSource) request.getAttribute(DataSource.class.getName());
        List<Resource> resourceList = IteratorUtils.toList(simpleDataSource.iterator());
        assertThat(resourceList, not(empty()));
        assertThat(resourceList, allOf(
                hasItem(resourceWithPropertiesTextAndValue("textFieldLabel", "textField")),
                // Multi text field doesn't have the 'fieldLabel' property, instead label is stored as 'cfm-element':
                hasItem(resourceWithPropertiesTextAndValue("multiTextField", "multiTextField")),
                hasItem(resourceWithPropertiesTextAndValue("numberFieldLabel", "numberField")),
                // Boolean field is a checkbox and therefore doesn't have a 'fieldLabel', instead there is a 'text' property
                hasItem(resourceWithPropertiesTextAndValue("booleanField", "booleanField")),
                hasItem(resourceWithPropertiesTextAndValue("dateAndTimeFieldLabel", "dateAndTimeField")),
                hasItem(resourceWithPropertiesTextAndValue("enumerationFieldLabel", "enumerationField"))));
    }

    /**
     * Custom matchers that checks if resource has given values set on properties <code>text</code> and <code>value</code>.
     */
    private static Matcher<Resource> resourceWithPropertiesTextAndValue(final Object textValue, final Object valueValue) {
        return new TypeSafeMatcher<Resource>() {
            @Override
            public void describeTo(Description description) {
                description
                        .appendText("was a resource with a property text=")
                        .appendValue(textValue)
                        .appendText(" and a property value=")
                        .appendValue(valueValue);
            }

            @Override
            protected boolean matchesSafely(Resource item) {
                if (item == null) {
                    return false;
                }
                ValueMap resourceValueMap = item.getValueMap();
                return resourceValueMap.get("text").equals(textValue) &&
                        resourceValueMap.get("value").equals(valueValue);
            }
        };
    }
}
