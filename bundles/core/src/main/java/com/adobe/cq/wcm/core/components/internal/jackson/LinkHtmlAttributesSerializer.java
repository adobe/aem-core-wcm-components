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

package com.adobe.cq.wcm.core.components.internal.jackson;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ImmutableSet;

public class LinkHtmlAttributesSerializer extends StdSerializer<Map<String, String>> {

    /**
     * List of the link's ignored html attributes from the Json export.
     */
    private static final Set<String> IGNORED_HTML_ATTRIBUTES = ImmutableSet.of("href");

    public LinkHtmlAttributesSerializer() { this(null); }

    protected LinkHtmlAttributesSerializer(Class<Map<String, String>> t) { super(t); }

    private Map<String, String> filteredMap = new LinkedHashMap<>();

    @Override
    public void serialize(Map map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
            for (Map.Entry<String, String> entry : filteredMap.entrySet()) {
                gen.writeStringField(entry.getKey(), entry.getValue());
            }
        gen.writeEndObject();
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!IGNORED_HTML_ATTRIBUTES.contains(entry.getKey()) && !StringUtils.isBlank(entry.getValue())) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredMap.isEmpty();
    }
}
