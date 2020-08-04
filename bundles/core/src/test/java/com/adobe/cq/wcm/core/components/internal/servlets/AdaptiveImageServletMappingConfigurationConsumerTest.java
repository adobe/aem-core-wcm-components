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

import java.util.Collection;
import java.util.Hashtable;
import javax.servlet.Servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.day.cq.dam.api.handler.store.AssetStore;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class AdaptiveImageServletMappingConfigurationConsumerTest {

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        AssetStore assetStore = mock(AssetStore.class);
        context.registerService(AssetStore.class, assetStore);
    }

    @Test
    public void testConfigurationConsumer() throws Exception {
        AdaptiveImageServletMappingConfigurationConsumer configurationConsumer = new AdaptiveImageServletMappingConfigurationConsumer();
        context.registerInjectActivateService(configurationConsumer);

        context.registerInjectActivateService(new AdaptiveImageServletMappingConfigurationFactory(),
            new Hashtable<String, Object>() {{
                put(Constants.SERVICE_PID, "pid1");
                put("resource.types", new String[]{"a/b/c"});
                put("selectors", new String[]{"a"});
                put("extensions", new String[]{"jpeg"});
                put("defaultResizeWidth", AdaptiveImageServlet.DEFAULT_RESIZE_WIDTH);
        }});

        context.registerInjectActivateService(new AdaptiveImageServletMappingConfigurationFactory(),
            new Hashtable<String, Object>() {{
                put(Constants.SERVICE_PID, "pid2");
                put("resource.types", new String[]{"d/e/f"});
                put("selectors", new String[]{"a"});
                put("extensions", new String[]{"jpeg"});
                put("defaultResizeWidth", AdaptiveImageServlet.DEFAULT_RESIZE_WIDTH);
        }});

        Collection<ServiceReference<Servlet>> servletServiceReferences =
            context.bundleContext().getServiceReferences(Servlet.class, "(sling.servlet.resourceTypes=a/b/c)");
        assertEquals(1, servletServiceReferences.size());
        ServiceReference<Servlet> servletReference = servletServiceReferences.iterator().next();
        Servlet ais = context.bundleContext().getService(servletReference);
        assertTrue(ais instanceof AdaptiveImageServlet);
    }

    @Test
    public void testConfigurationConsumerWithPreviousAISConfig() throws Exception {
        Configuration aisConfiguration = mock(Configuration.class);
        when(aisConfiguration.getProperties()).thenReturn(new Hashtable<String, Object>(){{
            put("defaultResizeWidth", 1000);
        }});
        ConfigurationAdmin configurationAdmin = mock(ConfigurationAdmin.class);
        when(configurationAdmin.listConfigurations("(" + Constants.SERVICE_PID + "=" + AdaptiveImageServlet.class.getName() + ")"))
                .thenReturn(new Configuration[]{aisConfiguration});


        AdaptiveImageServletMappingConfigurationConsumer configurationConsumer = new AdaptiveImageServletMappingConfigurationConsumer();

        // override the default provided mock object, since it doesn't support #listConfigurations
        Utils.setInternalState(configurationConsumer, "configurationAdmin", configurationAdmin);
        context.registerInjectActivateService(configurationConsumer);

        context.registerInjectActivateService(new AdaptiveImageServletMappingConfigurationFactory(), new Hashtable<String, Object>() {{
            put("resource.types", new String[]{"a/b/c"});
            put("selectors", new String[]{"a/b/c"});
            put("extensions", new String[]{"jpeg"});
            put("defaultResizeWidth", AdaptiveImageServlet.DEFAULT_RESIZE_WIDTH);
        }});

        Collection<ServiceReference<Servlet>> servletServiceReferences =
            context.bundleContext().getServiceReferences(Servlet.class, "(sling.servlet.resourceTypes=a/b/c)");
        assertEquals(1, servletServiceReferences.size());
        ServiceReference<Servlet> servletReference = servletServiceReferences.iterator().next();
        Servlet ais = context.bundleContext().getService(servletReference);
        assertTrue(ais instanceof AdaptiveImageServlet);
    }
}
