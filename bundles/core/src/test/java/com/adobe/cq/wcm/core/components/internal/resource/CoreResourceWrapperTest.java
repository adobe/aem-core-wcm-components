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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoreResourceWrapperTest {

    @Test
    public void testBasicWrapping() {
        Resource toBeWrapped = mock(Resource.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(toBeWrapped.getValueMap()).thenReturn(new ValueMapDecorator(Collections.emptyMap()));
        when(toBeWrapped.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.isResourceType(any(CoreResourceWrapper.class), any(String.class))).thenReturn(true);
        Map<String, String> overriddenProperties = new HashMap<>();
        overriddenProperties.put("a", "1");
        overriddenProperties.put("b", "2");
        Resource wrappedResource = new CoreResourceWrapper(toBeWrapped, "a/b/c", overriddenProperties);

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
    public void testEmptyResourceTypeArgument() {
        assertThrows(IllegalArgumentException.class, () -> new CoreResourceWrapper(mock(Resource.class), "", new HashMap<>()));
    }
}
