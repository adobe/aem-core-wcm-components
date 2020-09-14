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

import java.lang.reflect.Field;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.type.ClassKey;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PageModuleProviderTest {

    @Test
    public void testPageModule() throws Exception {
        PageModuleProvider pmp = new PageModuleProvider();
        Module module = pmp.getModule();
        assertTrue(module instanceof SimpleModule);
        Field field = module.getClass().getDeclaredField("_serializers");
        field.setAccessible(true);
        SimpleSerializers simpleSerializers = (SimpleSerializers) field.get(module);
        field = simpleSerializers.getClass().getDeclaredField("_interfaceMappings");
        field.setAccessible(true);
        HashMap<ClassKey, JsonSerializer<?>> classMappings = (HashMap) field.get(simpleSerializers);
        assertTrue(classMappings.containsKey(new ClassKey(Page.class)));
    }

}
