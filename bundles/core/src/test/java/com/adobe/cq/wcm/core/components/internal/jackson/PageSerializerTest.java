/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import org.junit.jupiter.api.Test;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class PageSerializerTest {

    private static final String PAGE_NAME = "PAGE_NAME";
    private static final String PAGE_TITLE = "PAGE_TITLE";
    private static final String PAGE_PATH = "PAGE_PATH";
    private static final String PAGE_DESCRIPTION = "PAGE_DESCRIPTION";

    @Test
    public void serialize() throws Exception {
        Page page = mock(Page.class);
        when(page.getName()).thenReturn(PAGE_NAME);
        when(page.getTitle()).thenReturn(PAGE_TITLE);
        when(page.getPageTitle()).thenReturn(PAGE_TITLE);
        when(page.getPath()).thenReturn(PAGE_PATH);
        when(page.getDescription()).thenReturn(PAGE_DESCRIPTION);
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);
        PageSerializer pageSerializer = new PageSerializer(Page.class);
        pageSerializer.serialize(page, jsonGenerator, serializerProvider);
        verify(jsonGenerator).writeStartObject();
        verify(jsonGenerator).writeStringField(PageSerializer.JSON_KEY_NAME, page.getName());
        verify(jsonGenerator).writeStringField(PageSerializer.JSON_KEY_TITLE, page.getTitle());
        verify(jsonGenerator).writeStringField(PageSerializer.JSON_KEY_PAGE_TITLE, page.getPageTitle());
        verify(jsonGenerator).writeStringField(PageSerializer.JSON_KEY_PATH, page.getPath());
        verify(jsonGenerator).writeStringField(PageSerializer.JSON_KEY_DESCRIPTION, page.getDescription());
        verify(jsonGenerator).writeEndObject();
        verifyNoMoreInteractions(jsonGenerator);
    }

}
