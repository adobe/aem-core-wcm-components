/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.jackson;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.models.DataLayer;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLayerSerializerTest {

    @Mock
    private DataLayer dataLayer;

    @Mock
    private Resource assetResource;

    @Mock
    private Asset asset;

    private static final String DATA_LAYER_ID = "DATA_LAYER_ID";

    @Test
    void serialize() throws Exception {
        when(dataLayer.getId()).thenReturn(DATA_LAYER_ID);
        when(dataLayer.getAssetResource()).thenReturn(assetResource);
        when(assetResource.adaptTo(Asset.class)).thenReturn(asset);
        when(asset.getMetadataValueFromJcr(TagConstants.PN_TAGS)).thenReturn("tag1,tag2");
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);
        DataLayerSerializer dataLayerSerializer = new DataLayerSerializer(DataLayer.class);
        dataLayerSerializer.serialize(dataLayer, jsonGenerator, serializerProvider);
        verify(jsonGenerator).writeStartObject();
        verify(jsonGenerator).writeObjectField(dataLayer.getId(), dataLayerSerializer.getDataLayerProperties(dataLayer));
        verify(jsonGenerator).writeEndObject();
    }
}