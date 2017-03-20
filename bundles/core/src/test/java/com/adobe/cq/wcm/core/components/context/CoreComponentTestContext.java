/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.impl.ResourceTypeBasedResourcePicker;
import org.apache.sling.models.spi.ImplementationPicker;
import org.apache.sling.testing.mock.sling.ResourceResolverType;

import com.day.cq.wcm.foundation.forms.FormStructureHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit.AemContext;
import io.wcm.testing.mock.aem.junit.AemContextCallback;

/**
 * Provides a context for unit tests.
 */
public final class CoreComponentTestContext {


    private CoreComponentTestContext() {
        // only static methods
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
        return new AemContext(new AemContextCallback() {
            @Override
            public void execute(AemContext context) throws IOException {
                context.registerService(FormStructureHelperFactory.class, new FormStructureHelperFactory() {
                    @Override
                    public FormStructureHelper getFormStructureHelper(Resource formElement) {
                        return null;
                    }
                });
                context.registerService(ImplementationPicker.class, new ResourceTypeBasedResourcePicker());
                context.addModelsForPackage("com.adobe.cq.wcm.core.components.models");
                if (StringUtils.isNotEmpty(testBase)) {
                    context.load().json(testBase + "/test-content.json", contentRoot);
                } else {
                    context.load().json("/test-content.json", contentRoot);
                }
            }
        }, ResourceResolverType.JCR_MOCK);
    }
}
