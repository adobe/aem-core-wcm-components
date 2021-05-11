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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    private Map<String, String> filter(Map<String, String> map) {
        return map.entrySet().stream()
                .filter(x -> !IGNORED_HTML_ATTRIBUTES.contains(x.getKey()) && !StringUtils.isBlank(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void serialize(Map<String, String> map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
            for (Map.Entry<String, String> entry : filter(map).entrySet()) {
                gen.writeStringField(entry.getKey(), entry.getValue());
            }
        gen.writeEndObject();
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Map<String, String> map) {
        return filter(map).isEmpty();
    }
}
