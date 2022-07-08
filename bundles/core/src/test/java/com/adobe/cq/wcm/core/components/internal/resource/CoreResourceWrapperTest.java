/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoreResourceWrapperTest {

    @Test
    public void testWrappingWithSimpleResource() {
        Map<String, Object> properties = new HashMap<String, Object>(){{
            put("a", 1);
            put("b", 2);
            put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "a/b/c");
        }};
        Resource wrappedResource = new CoreResourceWrapper(prepareResourceToBeWrapped(properties), "d/e/f");

        Map<String, Object> expectedProperties = new HashMap<>(properties);
        expectedProperties.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "d/e/f");
        testValueMap(expectedProperties.entrySet(), wrappedResource.adaptTo(ValueMap.class));
        testValueMap(expectedProperties.entrySet(), wrappedResource.getValueMap());
        assertEquals("d/e/f", wrappedResource.getResourceType());
    }

    @Test
    public void testWrappingWithHiddenProperties() {
        Map<String, Object> properties = new HashMap<String, Object>(){{
            put("a", 1);
            put("b", 2);
            put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "a/b/c");
        }};
        Resource wrappedResource = new CoreResourceWrapper(prepareResourceToBeWrapped(properties), "d/e/f", new ArrayList<String>() {{
            add("b");
        }}, new HashMap<>(), null);

        Map<String, Object> expectedProperties = new HashMap<>(properties);
        expectedProperties.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, "d/e/f");
        expectedProperties.remove("b");

        testValueMap(expectedProperties.entrySet(), wrappedResource.adaptTo(ValueMap.class));
        testValueMap(expectedProperties.entrySet(), wrappedResource.getValueMap());
        assertFalse(wrappedResource.getValueMap().containsKey("b"));
        assertEquals("d/e/f", wrappedResource.getResourceType());
    }

    @Test
    public void testWrappingWithOverridenProperties() {
        Resource toBeWrapped = mock(Resource.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(toBeWrapped.getValueMap()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
        when(toBeWrapped.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.isResourceType(any(CoreResourceWrapper.class), any(String.class))).thenReturn(true);
        Map<String, String> overriddenProperties = new HashMap<>();
        overriddenProperties.put("a", "1");
        overriddenProperties.put("b", "2");
        Resource wrappedResource = new CoreResourceWrapper(toBeWrapped, "a/b/c", new ArrayList<>(), overriddenProperties, null);

        // isResourceType()
        assertTrue(wrappedResource.isResourceType("a/b/c"));
        verify(resourceResolver).isResourceType(wrappedResource, "a/b/c");

        // getResourceType()
        assertEquals("a/b/c", wrappedResource.getResourceType(), "getResourceType()");

        // getValueMap()
        ValueMap properties = wrappedResource.getValueMap();
        assertEquals("1", properties.get("a", String.class), "getValueMap()");
        assertEquals("2", properties.get("b", String.class), "getValueMap()");
    }

    @Test
    public void testWrappingWithOverriddenChildren() {
        Resource toBeWrapped = mock(Resource.class);
        when(toBeWrapped.getValueMap()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
        Resource child1 = mock(Resource.class);
        when(toBeWrapped.getChild("path/to/child1")).thenReturn(child1);
        Resource child2 = mock(Resource.class);
        when(toBeWrapped.getChild("path/to/child2")).thenReturn(child2);
        Resource child3 = mock(Resource.class);
        when(toBeWrapped.getChild("path/to/child3")).thenReturn(child3);

        Map<String, Resource> overriddenChildren = new HashMap<>();
        Resource overriddenChild1 = mock(Resource.class);
        Resource overriddenChild2 = mock(Resource.class);
        overriddenChildren.put("path/to/child1", overriddenChild1);
        overriddenChildren.put("path/to/child2", overriddenChild2);

        Resource wrappedResource = new CoreResourceWrapper(toBeWrapped, "a/b/c", null, null, overriddenChildren);
        assertEquals(wrappedResource.getChild("path/to/child1"), overriddenChild1, "child should be overridden");
        assertEquals(wrappedResource.getChild("path/to/child2"), overriddenChild2, "child should be overridden");
        assertEquals(wrappedResource.getChild("path/to/child3"), child3, "child should not be overridden");
    }

    @Test
    public void testNulls() {
        assertThrows(IllegalArgumentException.class, () -> new CoreResourceWrapper(null, null));
    }

    @Test
    public void isResourceTypeDelegated() {
        Resource toBeWrapped = mock(Resource.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(toBeWrapped.getValueMap()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
        when(toBeWrapped.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.isResourceType(any(CoreResourceWrapper.class), any(String.class))).thenReturn(true);
        Resource wrappedResource = new CoreResourceWrapper(toBeWrapped, "a/b/c");
        assertTrue(wrappedResource.isResourceType("a/b/c"));
        verify(resourceResolver).isResourceType(wrappedResource, "a/b/c");
    }

    protected Resource prepareResourceToBeWrapped(Map<String, Object> properties) {
        Resource resource = mock(Resource.class);
        ValueMap valueMap = new ValueMapDecorator(properties);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(resource.adaptTo(ValueMap.class)).thenReturn(valueMap);
        return resource;
    }

    private void testValueMap(Collection<Map.Entry<String, Object>> keyValuePairs, ValueMap valueMap) {
        for (Map.Entry<String, Object> entry : keyValuePairs) {
            assertEquals(entry.getValue(), valueMap.get(entry.getKey()));
        }
    }
}
