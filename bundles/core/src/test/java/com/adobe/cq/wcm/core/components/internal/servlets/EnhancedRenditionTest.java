/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.io.InputStream;

import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnhancedRenditionTest {

    @Test
    public void testWrappedProperties() {
        Rendition mockRendition = mock(Rendition.class);
        when(mockRendition.getMimeType()).thenReturn("mimeType");
        when(mockRendition.getProperties()).thenReturn(ValueMap.EMPTY);
        when(mockRendition.getSize()).thenReturn((long)Math.floor(Math.random() * 100));
        when(mockRendition.getStream()).thenReturn(mock(InputStream.class));
        when(mockRendition.getAsset()).thenReturn(mock(Asset.class));
        EnhancedRendition rendition = new EnhancedRendition(mockRendition);
        assertEquals(mockRendition.getMimeType(), rendition.getMimeType());
        assertEquals(mockRendition.getProperties(), rendition.getProperties());
        assertEquals(mockRendition.getSize(), rendition.getSize());
        assertEquals(mockRendition.getStream(), rendition.getStream());
        assertEquals(mockRendition.getAsset(), rendition.getAsset());
    }

    @Test
    public void testGetDimensionFromAsset() {
        Rendition mockRendition = mock(Rendition.class);
        when(mockRendition.getName()).thenReturn(DamConstants.ORIGINAL_FILE);
        Asset mockAsset = mock(Asset.class);
        when(mockRendition.getAsset()).thenReturn(mockAsset);
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("1920");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("1080");
        EnhancedRendition rendition = new EnhancedRendition(mockRendition);
        assertEquals(1920, rendition.getDimension().getWidth());
        assertEquals(1080, rendition.getDimension().getHeight());

        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("bogus");
        rendition = new EnhancedRendition(mockRendition);
        assertNull(rendition.getDimension());
    }

    @Test
    public void testGetDimensionsFromMetadata() {
        Rendition mockRendition = mock(Rendition.class);
        ValueMap mockValueMap = mock(ValueMap.class);
        when(mockRendition.getProperties()).thenReturn(mockValueMap);
        when(mockValueMap.containsKey(DamConstants.TIFF_IMAGEWIDTH)).thenReturn(true);
        when(mockValueMap.get(DamConstants.TIFF_IMAGEWIDTH, String.class)).thenReturn("640");
        when(mockValueMap.containsKey(DamConstants.TIFF_IMAGELENGTH)).thenReturn(true);
        when(mockValueMap.get(DamConstants.TIFF_IMAGELENGTH, String.class)).thenReturn("480");
        EnhancedRendition rendition = new EnhancedRendition(mockRendition);
        assertEquals(640, rendition.getDimension().getWidth());
        assertEquals(480, rendition.getDimension().getHeight());

        when(mockValueMap.get(DamConstants.TIFF_IMAGELENGTH, String.class)).thenReturn("bogus");
        rendition = new EnhancedRendition(mockRendition);
        assertNull(rendition.getDimension());
    }

    @Test
    public void testGetDimensionsFromImage() {
        Rendition mockRendition = mock(Rendition.class);
        when(mockRendition.getProperties()).thenReturn(ValueMap.EMPTY);
        when(mockRendition.getName()).thenReturn("custom");
        when(mockRendition.getMimeType()).thenReturn("image/png");
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("image/1x1.png")) {
            when(mockRendition.getStream()).thenReturn(is);
            EnhancedRendition rendition = new EnhancedRendition(mockRendition);
            assertEquals(1, rendition.getDimension().getWidth());
            assertEquals(1, rendition.getDimension().getHeight());

            is.close();
            rendition = new EnhancedRendition(mockRendition);
            assertNull(rendition.getDimension());
        } catch (IOException ioex) {
            fail(ioex);
        }
    }

    @Test
    public void testInvalidRendition() {
        Rendition mockRendition = mock(Rendition.class);
        when(mockRendition.getProperties()).thenReturn(ValueMap.EMPTY);
        when(mockRendition.getName()).thenReturn("custom");
        when(mockRendition.getMimeType()).thenReturn("text");
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("image/1x1.png")) {
            when(mockRendition.getStream()).thenReturn(is);
            EnhancedRendition rendition = new EnhancedRendition(mockRendition);
            assertNull(rendition.getDimension());
        } catch (IOException ioex) {
            fail(ioex);
        }
    }
}
