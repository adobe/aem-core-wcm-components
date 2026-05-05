/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentFragmentDataImplCompositeTest {

    @Test
    void elementDataTextIsNullForCompositeFields() {
        DAMContentFragment.DAMContentElement element = Mockito.mock(DAMContentFragment.DAMContentElement.class);
        Mockito.when(element.getDataType()).thenReturn(DAMContentFragment.COMPOSITE_DATA_TYPE);

        ContentFragmentDataImpl.ElementDataImpl data = new ContentFragmentDataImpl.ElementDataImpl(element);
        assertNull(data.getText());
    }

    @Test
    void elementDataTextDelegatesForScalarFields() {
        DAMContentFragment.DAMContentElement element = Mockito.mock(DAMContentFragment.DAMContentElement.class);
        Mockito.when(element.getDataType()).thenReturn("string");
        Mockito.when(element.getValue(String.class)).thenReturn("hello");

        ContentFragmentDataImpl.ElementDataImpl data = new ContentFragmentDataImpl.ElementDataImpl(element);
        assertEquals("hello", data.getText());
    }
}
