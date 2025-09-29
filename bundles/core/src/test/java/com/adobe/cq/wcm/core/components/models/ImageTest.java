/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2025 Adobe
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
package com.adobe.cq.wcm.core.components.models;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageTest {

    @Test
    public void testGetAltAsMap() {
        Image mockImage = mock(Image.class);

        // Test with non-empty alt text
        when(mockImage.getAlt()).thenReturn("Sample alt text");
        when(mockImage.getAltAsMap()).thenCallRealMethod();
        Map<String, String> altMap = mockImage.getAltAsMap();
        assertEquals(1, altMap.size());
        assertEquals("Sample alt text", altMap.get("alt"));

        // Test with empty alt text
        when(mockImage.getAlt()).thenReturn("");
        altMap = mockImage.getAltAsMap();
        assertTrue(altMap.isEmpty());

        // Test with null alt text
        when(mockImage.getAlt()).thenReturn(null);
        altMap = mockImage.getAltAsMap();
        assertTrue(altMap.isEmpty());

        // Test with whitespace-only alt text
        when(mockImage.getAlt()).thenReturn("   ");
        altMap = mockImage.getAltAsMap();
        assertTrue(altMap.isEmpty());
    }
}
