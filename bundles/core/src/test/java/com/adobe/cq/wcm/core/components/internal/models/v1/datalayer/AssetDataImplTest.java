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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.dam.api.DamConstants;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

import com.day.cq.dam.api.Asset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetDataImplTest {

    @Test
    void testGetLastModifiedDate() {
        Asset asset = mock(Asset.class);
        when(asset.getLastModified()).thenReturn(0L);
        ValueMap valueMap = mock(ValueMap.class);
        when(asset.adaptTo(ValueMap.class)).thenReturn(valueMap);
        Calendar now = Calendar.getInstance();
        when(valueMap.get(JcrConstants.JCR_CREATED, Calendar.class)).thenReturn(now);
        AssetData assetData = DataLayerBuilder.forAsset(asset).build();
        assertEquals(now.getTime(), assetData.getLastModifiedDate());
    }

    @Test
    void testGetSmartTags() {
        Asset asset = mock(Asset.class);
        Resource assetResource = mock(Resource.class);
        when(asset.adaptTo(Resource.class)).thenReturn(assetResource);

        // mock smart tags
        Resource predictedTagsResource = mock(Resource.class);
        List<Resource> children = new ArrayList<>();
        for (int i=2; i>0; --i) {
            Resource tagResource = mock(Resource.class);
            ValueMap valueMap = mock(ValueMap.class);
            when(valueMap.get("name")).thenReturn("tag"+i);
            when(valueMap.get("confidence")).thenReturn(0.78);
            when(tagResource.adaptTo(ValueMap.class)).thenReturn(valueMap);
            children.add(tagResource);
        }
        when(assetResource.getChild(DamConstants.PREDICTED_TAGS)).thenReturn(predictedTagsResource);
        when(predictedTagsResource.getChildren()).thenReturn(children);

        Map<String, Double> expectedSmartTags = new HashMap<String, Double>(){{
            put("tag1", 0.78);
            put("tag2", 0.78);
        }};

        AssetData assetData = DataLayerBuilder.forAsset(asset).build();
        assertEquals(expectedSmartTags, assetData.getSmartTags());
    }
}
