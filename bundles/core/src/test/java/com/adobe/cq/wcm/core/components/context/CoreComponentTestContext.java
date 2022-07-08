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
package com.adobe.cq.wcm.core.components.context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.adobe.cq.wcm.core.components.testing.MockResponsiveGrid;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.msm.api.MSMNameConstants;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;

import static org.apache.sling.testing.mock.caconfig.ContextPlugins.CACONFIG;

/**
 * Provides a context for unit tests.
 */
public final class CoreComponentTestContext {

    public static final String TEST_CONTENT_JSON = "/test-content.json";
    public static final String TEST_TAGS_JSON = "/test-tags.json";
    public static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
    public static final String TEST_APPS_JSON = "/test-apps.json";
    public static final String TEST_CONF_JSON = "/test-conf.json";


    private CoreComponentTestContext() {
        // only static methods
    }

    private static final ImmutableMap<String, Object> PROPERTIES =
            ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
                    "/:/",
                    "^/content/links/site1/(.+)</content/site1/$1"
            ));

    public static AemContext newAemContext() {
        return new AemContextBuilder()
                .plugin(CACONFIG)
                .resourceResolverType(ResourceResolverType.JCR_MOCK)
                .resourceResolverFactoryActivatorProps(PROPERTIES)
            .<AemContext>afterSetUp(context -> {
                    context.addModelsForClasses(MockResponsiveGrid.class);
                    context.addModelsForPackage("com.adobe.cq.wcm.core.components.models");
                    context.addModelsForPackage("com.adobe.cq.wcm.core.components.internal.link");
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
                    context.registerService(Externalizer.class, MockExternalizerFactory.getExternalizerService());
                    context.registerInjectActivateService(new DefaultPathProcessor(), ImmutableMap.of(
                            "vanityConfig", DefaultPathProcessor.VanityConfig.ALWAYS.getValue()));
                }
            )
            .build();
    }
}
