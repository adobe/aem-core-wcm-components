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
package com.adobe.cq.wcm.core.components.internal;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonReader;

import org.apache.commons.lang.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import io.wcm.testing.mock.aem.junit5.AemContext;

import static com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils.PN_CFM_GRID_TYPE;
import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;
import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentFragmentUtilsTest {


    @Test
    public void getTypeWhenContentFragmentIsNull() {
        // GIVEN
        // WHEN
        String type = ContentFragmentUtils.getType(null);

        // THEN
        Assertions.assertEquals(type, "");
    }

    @Test
    public void getTypeWhenResourceIsNull() {
        // GIVEN
        FragmentTemplate fragmentTemplate = Mockito.mock(FragmentTemplate.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);
        Mockito.when(contentFragment.getTemplate()).thenReturn(fragmentTemplate);
        Mockito.when(contentFragment.getName()).thenReturn("foobar");

        // WHEN
        String type = ContentFragmentUtils.getType(contentFragment);

        // THEN
        Assertions.assertEquals(type, "foobar");
    }

    @Test
    public void getTypeWhenTemplateResourceIsNotNull() {
        // GIVEN
        Resource fragmentResource = Mockito.mock(Resource.class);
        Resource templateResource = Mockito.mock(Resource.class);
        FragmentTemplate fragmentTemplate = Mockito.mock(FragmentTemplate.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);

        Mockito.when(contentFragment.getTemplate()).thenReturn(fragmentTemplate);
        Mockito.when(contentFragment.adaptTo(Mockito.eq(Resource.class))).thenReturn(fragmentResource);
        Mockito.when(fragmentTemplate.adaptTo(Mockito.eq(Resource.class))).thenReturn(templateResource);
        Mockito.when(templateResource.getPath()).thenReturn("/foo/bar/qux");

        // WHEN
        String type = ContentFragmentUtils.getType(contentFragment);

        // THEN
        Assertions.assertEquals(type, "/foo/bar/qux");
    }

    @Test
    public void getTypeWhenTemplateResourceIsNotNullButIsContentNodeFallbackToParent() {
        // GIVEN
        Resource fragmentResource = Mockito.mock(Resource.class);
        Resource templateResourceParent = Mockito.mock(Resource.class);
        Resource templateResource = Mockito.mock(Resource.class);
        FragmentTemplate fragmentTemplate = Mockito.mock(FragmentTemplate.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);

        Mockito.when(contentFragment.getTemplate()).thenReturn(fragmentTemplate);
        Mockito.when(contentFragment.adaptTo(Mockito.eq(Resource.class))).thenReturn(fragmentResource);
        Mockito.when(fragmentTemplate.adaptTo(Mockito.eq(Resource.class))).thenReturn(templateResource);
        Mockito.when(templateResource.getName()).thenReturn(JCR_CONTENT);
        Mockito.when(templateResource.getParent()).thenReturn(templateResourceParent);
        Mockito.when(templateResourceParent.getPath()).thenReturn("/foo/bar");

        // WHEN
        String type = ContentFragmentUtils.getType(contentFragment);

        // THEN
        Assertions.assertEquals(type, "/foo/bar");
    }

    @Test
    public void getTypeOfStructuredContentFragment() {
        // GIVEN
        Resource fragmentResource = Mockito.mock(Resource.class);
        Resource fragmentDataResource = Mockito.mock(Resource.class);
        Resource templateResource = Mockito.mock(Resource.class);
        FragmentTemplate fragmentTemplate = Mockito.mock(FragmentTemplate.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);
        ValueMap valueMap = new MockValueMap(fragmentDataResource);
        valueMap.put("cq:model", "foo.bar.QuxModel");

        Mockito.when(contentFragment.getTemplate()).thenReturn(fragmentTemplate);
        Mockito.when(contentFragment.adaptTo(Mockito.eq(Resource.class))).thenReturn(fragmentResource);
        Mockito.when(fragmentResource.getChild(Mockito.eq(JCR_CONTENT + "/data"))).thenReturn(fragmentDataResource);
        Mockito.when(fragmentDataResource.getValueMap()).thenReturn(valueMap);
        Mockito.when(fragmentTemplate.adaptTo(Mockito.eq(Resource.class))).thenReturn(templateResource);
        Mockito.when(templateResource.getPath()).thenReturn("/foo/bar/qux/quux/corge/grault/garply");
        Mockito.when(templateResource.getName()).thenReturn("waldo");

        // WHEN
        String type = ContentFragmentUtils.getType(contentFragment);

        // THEN
        Assertions.assertEquals(type, "bar/models/waldo");
    }


    @Test
    public void filterEmptyElementNamesReturnsOriginalList() {
        // GIVEN
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);
        Iterator<ContentElement> contentElementIterator = Mockito.mock(Iterator.class);

        Mockito.when(contentFragment.getElements()).thenReturn(contentElementIterator);

        // WHEN
        Iterator<ContentElement> elementIterator = ContentFragmentUtils.filterElements(contentFragment, null);

        // THEN
        Assertions.assertEquals(elementIterator, contentElementIterator);
    }

    @Test
    public void filterElementNamesReturnsAppropriateElementsOnly() {
        // GIVEN
        ContentElement foo = Mockito.mock(ContentElement.class);
        ContentElement qux = Mockito.mock(ContentElement.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);
        Mockito.when(contentFragment.hasElement(Mockito.eq("foo"))).thenReturn(true);
        Mockito.when(contentFragment.hasElement(Mockito.eq("bar"))).thenReturn(false);
        Mockito.when(contentFragment.hasElement(Mockito.eq("qux"))).thenReturn(true);
        Mockito.when(contentFragment.getElement(Mockito.eq("foo"))).thenReturn(foo);
        Mockito.when(contentFragment.getElement(Mockito.eq("qux"))).thenReturn(qux);

        // WHEN
        Iterator<ContentElement> elementIterator = ContentFragmentUtils.filterElements(contentFragment,
                new String[]{"foo", "bar", "qux"});

        // THEN
        MatcherAssert.assertThat(() -> elementIterator, IsIterableContainingInOrder.contains(foo, qux));
    }

    @Test
    public void getEditorJsonOutputOfContentFragment() {
        // GIVEN

        Resource contentFragmentResource = Mockito.mock(Resource.class);
        ContentFragment contentFragment = Mockito.mock(ContentFragment.class);
        Iterator<Resource> associatedContentResourceIterator = Mockito.mock(Iterator.class);
        Resource firstAndOnlyAssociatedContent = Mockito.mock(Resource.class);
        ValueMap associatedContentValueMap = new MockValueMap(firstAndOnlyAssociatedContent);
        associatedContentValueMap.put(JCR_TITLE, "associatedContentTitle");

        Mockito.when(contentFragment.getTitle()).thenReturn("titleOfTheContentFragment");
        Mockito.when(contentFragment.getAssociatedContent()).thenReturn(associatedContentResourceIterator);
        Mockito.when(contentFragment.adaptTo(Mockito.eq(Resource.class))).thenReturn(contentFragmentResource);
        Mockito.when(contentFragmentResource.getPath()).thenReturn("/path/to/the/content/fragment");
        Mockito.when(associatedContentResourceIterator.hasNext()).thenReturn(true, true, false);
        Mockito.when(associatedContentResourceIterator.next()).thenReturn(firstAndOnlyAssociatedContent);
        Mockito.when(firstAndOnlyAssociatedContent.getPath()).thenReturn("/path/to/the/associated/content");
        Mockito.when(firstAndOnlyAssociatedContent.adaptTo(ValueMap.class)).thenReturn(associatedContentValueMap);

        // WHEN
        String json = ContentFragmentUtils.getEditorJSON(contentFragment, "slave",
                new String[]{"foo", "bar"});

        // THEN
        JsonReader expected = Json.createReader(this.getClass().getResourceAsStream("expectedJson.json"));
        JsonReader actual = Json.createReader(new StringReader(json));
        assertEquals(expected.read(), actual.read());
    }

    @Test
    public void getDefaultGridResourceType() {
        // GIVEN
        ResourceResolver resourceResolver = Mockito.mock(ResourceResolver.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(resource.getResourceResolver()).thenReturn(resourceResolver);

        // WHEN
        String defaultGridResourceType = ContentFragmentUtils.getGridResourceType(resource);

        // THEN
        Assertions.assertEquals(defaultGridResourceType, ContentFragmentUtils.DEFAULT_GRID_TYPE);
    }

    @Test
    public void getGridTypeSetInFragmentPolicy() {
        // GIVEN
        ContentPolicyManager contentPolicyManager = Mockito.mock(ContentPolicyManager.class);
        ContentPolicy contentPolicy = Mockito.mock(ContentPolicy.class);
        ResourceResolver resourceResolver = Mockito.mock(ResourceResolver.class);
        Resource resource = Mockito.mock(Resource.class);
        ValueMap valueMap = new MockValueMap(resource);
        valueMap.put(PN_CFM_GRID_TYPE, "foobar");

        Mockito.when(resource.getResourceResolver()).thenReturn(resourceResolver);
        Mockito.when(resourceResolver.adaptTo(Mockito.eq(ContentPolicyManager.class))).thenReturn(contentPolicyManager);
        Mockito.when(contentPolicyManager.getPolicy(Mockito.eq(resource))).thenReturn(contentPolicy);
        Mockito.when(contentPolicy.getProperties()).thenReturn(valueMap);

        // WHEN
        String defaultGridResourceType = ContentFragmentUtils.getGridResourceType(resource);

        // THEN
        Assertions.assertEquals(defaultGridResourceType, "foobar");
    }

    @Test
    public void getItemsOrderOfEmptyMap() {
        // GIVEN
        Map<String, Object> items = new HashMap<>();

        // WHEN
        String[] itemsOrder = ContentFragmentUtils.getItemsOrder(items);

        // THEN
        Assertions.assertArrayEquals(itemsOrder, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Test
    public void getItemsOrderOfMap() {
        // GIVEN
        Map<String, Object> items = new LinkedHashMap<>();
        items.put("first", "3");
        items.put("second", 1);
        items.put("third", 2L);

        // WHEN
        String[] itemsOrder = ContentFragmentUtils.getItemsOrder(items);

        // THEN
        Assertions.assertArrayEquals(itemsOrder, new String[]{"first", "second", "third"});
    }

    @Test
    public void getComponentExport() {
        // GIVEN
        AemContext slingContext = CoreComponentTestContext.newAemContext();
        slingContext.load().json(this.getClass().getResourceAsStream("foo.json"), "/foo");
        MockSlingHttpServletRequest slingHttpServletRequest =
                new MockSlingHttpServletRequest(slingContext.bundleContext());

        ComponentExporter componentExporter = new TestComponentExporter();

        ModelFactory modelFactory = Mockito.mock(ModelFactory.class);
        Mockito.when(modelFactory.getModelFromWrappedRequest(
                Mockito.any(), Mockito.any(), Mockito.eq(ComponentExporter.class)
        )).thenReturn(componentExporter);

        // WHEN
        Map<String, ComponentExporter> exporterMap =
                ContentFragmentUtils.getComponentExporters(slingContext.resourceResolver()
                        .getResource("/foo").listChildren(), modelFactory, slingHttpServletRequest, null);

        // THEN
        Assertions.assertEquals(componentExporter, exporterMap.get("bar"));
        Assertions.assertEquals(componentExporter, exporterMap.get("qux"));
    }

    /**
     * Dummy test {@link ComponentExporter component exporter}.
     */
    private static class TestComponentExporter implements ComponentExporter {
        @NotNull
        @Override
        public String getExportedType() {
            return "test";
        }
    }
}
