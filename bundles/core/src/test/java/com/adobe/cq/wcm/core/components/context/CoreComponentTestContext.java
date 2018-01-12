/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.adobe.cq.wcm.core.components.testing.MockAdapterFactory;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;

/**
 * Provides a context for unit tests.
 */
public final class CoreComponentTestContext {

    public static final String TEST_CONTENT_JSON = "/test-content.json";
    public static final String TEST_APPS_JSON = "/test-apps.json";


    private CoreComponentTestContext() {
        // only static methods
    }

    public static AemContext createContext() {
        return createContext(null, null);
    }

    /**
     * Creates a new instance of {@link AemContext}, adds the project specific Sling Models and loads test data from the JSON file
     * "/test-content.json" in the current classpath
     *
     * @param testBase    Prefix of the classpath resource to load test data from. Optional, can be null. If null, test data will be
     *                    loaded from /test-content.json
     * @param contentRoot Path to import the JSON content to
     * @return New instance of {@link AemContext}
     */
    public static AemContext createContext(final String testBase, final String contentRoot) {
        return new AemContext(
                (AemContextCallback) context -> {
                    context.registerService(FormStructureHelperFactory.class, resource -> null);
                    context.registerService(ImplementationPicker.class, new ResourceTypeBasedResourcePicker());
                    if (testBase != null) {
                        if (StringUtils.isNotEmpty(testBase)) {
                            context.load().json(testBase + TEST_CONTENT_JSON, contentRoot);
                        } else {
                            context.load().json(TEST_CONTENT_JSON, contentRoot);
                        }
                    }
                    context.registerInjectActivateService(new MockAdapterFactory());
                },
                ResourceResolverType.JCR_MOCK
        );
    }
}
