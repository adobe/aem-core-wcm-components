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
package com.adobe.cq.wcm.core.extensions.amp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.testing.MockResponsiveGrid;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;

/**
 * Provides a context for AMP unit tests.
 */
public final class AmpTestContext {

    public static final String TEST_CONTENT_JSON = "/test-content.json";
    public static final String TEST_APPS_JSON = "/test-apps.json";


    private AmpTestContext() {
        // only static methods
    }

    public static io.wcm.testing.mock.aem.junit5.AemContext newAemContext() {
        return new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
                .<io.wcm.testing.mock.aem.junit5.AemContext>afterSetUp(context -> {
                            context.addModelsForClasses(MockResponsiveGrid.class);
                            context.registerService(SlingModelFilter.class, new MockSlingModelFilter() {
                                private final Set<String> IGNORED_NODE_NAMES = new HashSet<String>() {{
                                    add(NameConstants.NN_RESPONSIVE_CONFIG);
                                    add(MSMNameConstants.NT_LIVE_SYNC_CONFIG);
                                    add("cq:annotations");
                                }};

                                @Override
                                public Map<String, Object> filterProperties(Map<String, Object> map) {
                                    return map;
                                }

                                @Override
                                public Iterable<Resource> filterChildResources(Iterable<Resource> childResources) {
                                    return StreamSupport
                                            .stream(childResources.spliterator(), false)
                                            .filter(r -> !IGNORED_NODE_NAMES.contains(r.getName()))
                                            .collect(Collectors.toList());
                                }
                            });
                            context.registerService(ImplementationPicker.class, new ResourceTypeBasedResourcePicker());
                        }
                )
                .build();
    }
}
