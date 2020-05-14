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

import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

import com.day.cq.dam.api.Asset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssetDataImplTest {

    @Test
    void testGetLastModifiedDate() {
        Asset asset = mock(Asset.class);
        when(asset.getLastModified()).thenReturn(0l);
        ValueMap valueMap = mock(ValueMap.class);
        when(asset.adaptTo(ValueMap.class)).thenReturn(valueMap);
        Calendar now = Calendar.getInstance();
        when(valueMap.get(JcrConstants.JCR_CREATED, Calendar.class)).thenReturn(now);
        assertEquals(now.getTime(), new AssetDataImpl(asset).getLastModifiedDate());

    }
}
