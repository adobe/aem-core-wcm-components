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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * JSON Serializer for {@link Page}. Provides a JSON created from page properties.
 */
public class PageSerializer extends StdSerializer<Page> {

    static final String JSON_KEY_NAME = "name";
    static final String JSON_KEY_TITLE = "title";
    static final String JSON_KEY_PAGE_TITLE = "pageTitle";
    static final String JSON_KEY_PATH = "path";
    static final String JSON_KEY_DESCRIPTION = "description";

    public PageSerializer() {
        this(null);
    }

    public PageSerializer(Class<Page> t) {
        super(t);
    }

    @Override
    public void serialize(Page page, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        Map<String, String> pageProperties = getPageProperties(page);
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, String> entry : pageProperties.entrySet()) {
            jsonGenerator.writeStringField(entry.getKey(), entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }

    private Map<String, String> getPageProperties(Page page) {
        Map<String, String> properties = new HashMap<>();
        properties.put(JSON_KEY_NAME, page.getName());
        properties.put(JSON_KEY_TITLE, page.getTitle());
        properties.put(JSON_KEY_PAGE_TITLE, page.getPageTitle());
        properties.put(JSON_KEY_PATH, page.getPath());
        properties.put(JSON_KEY_DESCRIPTION, page.getDescription());
        return properties;
    }
}
