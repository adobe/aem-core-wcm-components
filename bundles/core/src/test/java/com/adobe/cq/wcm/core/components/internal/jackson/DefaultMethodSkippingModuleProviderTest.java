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

import com.customer.models.CustomerInterfaceWithDefaultMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DefaultMethodSkippingModuleProviderTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DefaultMethodSkippingModuleProvider().getModule());
    }

    @Test
    public void testDefaultMethodIsSkipped() throws Exception {
        Object obj = new ClassUsingDefaultMethod();
        String result = objectMapper.writer().writeValueAsString(obj);
        try (JsonReader jsonReader = Json.createReader(new StringReader(result))) {
            JsonObject json = jsonReader.readObject();
            assertFalse(json.containsKey("defaultProperty"));
            assertEquals("fromcustomer", json.getString("customerDefaultProperty"));
            assertEquals("test2", json.getString("nonDefaultProperty"));
        }
    }

    @Test
    public void testOverriddenDefaultMethodIsIncluded() throws Exception {
        Object obj = new ClassOverridingDefaultMethod();
        String result = objectMapper.writer().writeValueAsString(obj);
        try (JsonReader jsonReader = Json.createReader(new StringReader(result))) {
            JsonObject json = jsonReader.readObject();
            assertEquals("fromcustomer", json.getString("customerDefaultProperty"));
            assertEquals("test3", json.getString("defaultProperty"));
            assertEquals("test4", json.getString("nonDefaultProperty"));
        }

    }

    private interface InterfaceWithDefaultMethod {
        default String getDefaultProperty() {
            return "test1";
        }

        String getNonDefaultProperty();
    }

    private static class ClassUsingDefaultMethod implements InterfaceWithDefaultMethod, CustomerInterfaceWithDefaultMethod {
        public String getNonDefaultProperty() {
            return "test2";
        }
    }

    private static class ClassOverridingDefaultMethod implements InterfaceWithDefaultMethod, CustomerInterfaceWithDefaultMethod {
        public String getDefaultProperty() {
            return "test3";
        }

        public String getNonDefaultProperty() {
            return "test4";
        }
    }

}
