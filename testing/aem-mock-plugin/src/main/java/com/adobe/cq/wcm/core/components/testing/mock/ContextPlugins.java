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
package com.adobe.cq.wcm.core.components.testing.mock;

import org.apache.sling.testing.mock.osgi.context.AbstractContextPlugin;
import org.apache.sling.testing.mock.osgi.context.ContextPlugin;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor;

import io.wcm.testing.mock.aem.context.AemContextImpl;

/**
 * Mock context plugins.
 */
public final class ContextPlugins {

    private ContextPlugins() {
        // constants only
    }

    /**
     * Context plugin for AEM core components.
     */
    public static final @NotNull ContextPlugin<AemContextImpl> CORE_COMPONENTS = new AbstractContextPlugin<AemContextImpl>() {
        @Override
        public void afterSetUp(@NotNull AemContextImpl context) throws Exception {
            setUp(context);
        }
    };

    /**
     * Set up request context and Sling Models Extensions.
     * @param context Aem context
     */
    static void setUp(AemContextImpl context) {

        // register default path processor for core components link handling
        context.registerInjectActivateService(new DefaultPathProcessor());

    }

}
