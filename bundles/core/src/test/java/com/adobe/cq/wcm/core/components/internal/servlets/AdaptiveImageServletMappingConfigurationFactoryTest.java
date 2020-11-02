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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdaptiveImageServletMappingConfigurationFactoryTest {

    @Test
    public void testConfigurationFactory() {
        AdaptiveImageServletMappingConfigurationFactory configurationFactory = new AdaptiveImageServletMappingConfigurationFactory();
        configurationFactory.configure(new AdaptiveImageServletMappingConfigurationFactory.Config() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String[] resource_types() {
                return new String[] {"core/image"};
            }

            @Override
            public String[] selectors() {
                return new String[] {"coreimg", ""};
            }

            @Override
            public String[] extensions() {
                return new String[] {"jpg", "gif", "png"};
            }

            @Override
            public int defaultResizeWidth() {
                return AdaptiveImageServlet.DEFAULT_RESIZE_WIDTH;
            }

            @Override
            public int maxSize() {
                return AdaptiveImageServlet.DEFAULT_MAX_SIZE;
            }
        });
        testValues(new String[] {"core/image"}, configurationFactory.getResourceTypes());
        testValues(new String[] {"coreimg"}, configurationFactory.getSelectors());
        testValues(new String[] {"jpg", "gif", "png"}, configurationFactory.getExtensions());
        assertEquals("{resourceTypes: [core/image], selectors: [coreimg], extensions: [jpg, gif, png], defaultResizeWidth: 1280}",
                configurationFactory.toString());
    }

    private void testValues(String[] expected, List<String> actuals) {
        assertEquals(expected.length, actuals.size());
    }
}
