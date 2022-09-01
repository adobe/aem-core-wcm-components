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
package com.adobe.cq.wcm.core.components.internal.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.sling.models.spi.ImplementationPicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.models.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LatestVersionImplementationPickerTest {

    private static final Class<?> SAMPLE_ADAPTER = com.adobe.cq.wcm.core.components.models.Page.class;
    private static final Object SAMPLE_ADAPTABLE = new Object();

    private ImplementationPicker underTest;

    @BeforeEach
    void setUp() {
        underTest = new LatestVersionImplementationPicker();
    }

    @Test
    void testPickLatestModelVersion() {
        Class<?>[] implementations = new Class<?>[] { com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl.class,
                com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.class };
        assertEquals(com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.class, underTest.pick(SAMPLE_ADAPTER, implementations, SAMPLE_ADAPTABLE));
    }

    @Test
    void testPickExternalModel() {
        Object customPage = Proxy.newProxyInstance(Page.class.getClassLoader(), new Class[]{Page.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
        Class<?>[] implementations = new Class<?>[] { com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl.class,
                com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.class, customPage.getClass() };
        assertEquals(customPage.getClass(), underTest.pick(SAMPLE_ADAPTER, implementations, SAMPLE_ADAPTABLE));
    }

    @Test
    void testNoMatch() {
        Class<?>[] implementations = new Class<?>[] { String.class, Integer.class };
        assertEquals(String.class, underTest.pick(SAMPLE_ADAPTER, implementations, SAMPLE_ADAPTABLE));
    }

    @Test
    void testNoCoreComponent() {
        Class<?>[] implementations = new Class<?>[] { String.class, Integer.class };
        assertNull(underTest.pick(Comparable.class, implementations, SAMPLE_ADAPTABLE));
    }

    @Test
    void testOtherAdobeModelsGetFiltered() {
        Class<?>[] implementations = new Class<?>[] { com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl.class,
            com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.class,
                LatestVersionImplementationPickerTest.DummyPageImpl.class };
        assertEquals(com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl.class, underTest.pick(SAMPLE_ADAPTER, implementations, SAMPLE_ADAPTABLE));
    }

    private static class DummyPageImpl implements Page {

    }
}
