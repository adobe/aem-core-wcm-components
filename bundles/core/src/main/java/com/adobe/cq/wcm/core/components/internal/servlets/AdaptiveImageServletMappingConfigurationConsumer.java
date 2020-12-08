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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.handler.store.AssetStore;

/**
 * Consumer for {@link AdaptiveImageServletMappingConfigurationFactory} configurations. Will be notified when a configuration is
 * added, updated or removed and will take care of properly registering the {@link AdaptiveImageServlet} based on configuration
 * options.
 */
@Component()
public class AdaptiveImageServletMappingConfigurationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveImageServletMappingConfigurationConsumer.class);

    @Reference
    private MimeTypeService mimeTypeService;

    @Reference
    private AssetStore assetStore;

    private BundleContext bundleContext;

    private Map<String, AdaptiveImageServletMappingConfigurationFactory> configs = new HashMap<>();

    private List<ServiceRegistration> serviceRegistrations = new ArrayList<>();

    private int oldAISDefaultResizeWidth = Integer.MIN_VALUE;

    @Reference
    private ConfigurationAdmin configurationAdmin;
    
    @Reference
    AdaptiveImageServletMetrics metrics;


    /**
     * Activation method
     *
     * @param componentContext - Component context
     * @param bundleContext - Bundle context
     * @param config - Config properties
     */
    @Activate
    public void activate(ComponentContext componentContext, BundleContext bundleContext, Map<String, Object> config) {
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations("(" + Constants.SERVICE_PID + "=" +
                    AdaptiveImageServlet.class.getName() + ")");
            if (ArrayUtils.isNotEmpty(configurations)) {
                Configuration oldConfig = configurations[0];
                oldAISDefaultResizeWidth = PropertiesUtil.toInteger(oldConfig.getProperties().get("defaultResizeWidth"), Integer.MIN_VALUE);
                if (oldAISDefaultResizeWidth > 0) {
                    LOG.warn(
                            "Found previous custom configuration for the {}. The configuration will be reused to control the {} " +
                            "registrations managed by this component. Please migrate the previous configuration to the {} factory" +
                            " configurations.",
                            AdaptiveImageServlet.class.getName(), AdaptiveImageServlet.class.getName(),
                            AdaptiveImageServletMappingConfigurationFactory.class.getName()
                    );
                }
            }
        } catch (IOException| InvalidSyntaxException|RuntimeException e) {
            LOG.error("Unable to retrieve previous configuration for the " + AdaptiveImageServlet.class.getName() + " component. " +
                    "The configuration, if it still exists, will not be reused to configure the defaultResizeWidth property of the " +
                    "servlet's registrations managed by this component.", e);
        }
        this.bundleContext = bundleContext;
        updateServletRegistrations();
    }

    /**
     * Deactivation method
     */
    @Deactivate
    public void deactivate() {
        configs.clear();
        updateServletRegistrations();
    }

    /**
     * Bind method for configurations, invoked when a configuration is added or updated.
     *
     * @param configurationFactory - {@link AdaptiveImageServletMappingConfigurationFactory} instance
     */
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, updated = "bindAdaptiveImageServletConfigurationFactory")
    protected  void bindAdaptiveImageServletConfigurationFactory(AdaptiveImageServletMappingConfigurationFactory configurationFactory, Map<String, ?> properties) {
        String servicePid = (String)properties.get(Constants.SERVICE_PID);
        applyValidConfiguration(configurationFactory, () -> {
            configs.put(servicePid, configurationFactory);
            updateServletRegistrations();
        });
    }

    /**
     * Unbind method for configurations, invoked when a configuration is removed.
     *
     * @param configurationFactory - {@link AdaptiveImageServletMappingConfigurationFactory} instance
     */
    protected  void unbindAdaptiveImageServletConfigurationFactory(AdaptiveImageServletMappingConfigurationFactory configurationFactory, Map<String, ?> properties) {
        String servicePid = (String)properties.get(Constants.SERVICE_PID);
        configs.remove(servicePid);
        updateServletRegistrations();
    }

    /**
     * Internal helper to update the server registrations. Invoked on all events, will remove all server registrations and register
     * the servlet again based on active configurations.
     */
    private void updateServletRegistrations() {
        for (ServiceRegistration serviceRegistration : serviceRegistrations) {
            if (serviceRegistration != null) {
                serviceRegistration.unregister();
            }
        }
        serviceRegistrations.clear();

        if (bundleContext != null) {
            for (AdaptiveImageServletMappingConfigurationFactory config : configs.values()) {
                final Hashtable<String, Object> properties = new Hashtable<>();
                properties.put("sling.servlet.methods", new String[]{"GET"});
                properties.put("sling.servlet.resourceTypes", config.getResourceTypes());
                properties.put("sling.servlet.selectors", config.getSelectors());
                properties.put("sling.servlet.extensions", config.getExtensions());
                serviceRegistrations.add(
                        bundleContext.registerService(
                                Servlet.class.getName(),
                                new AdaptiveImageServlet(
                                        mimeTypeService,
                                        assetStore,
                                        metrics,
                                        oldAISDefaultResizeWidth > 0 ? oldAISDefaultResizeWidth : config.getDefaultResizeWidth(),
                                        config.getMaxSize()),
                                properties
                        )
                );
            }
        }
    }

    /**
     * If the properties that apply to the {@link AdaptiveImageServlet} registration are valid, then the {@code apply} {@link Runnable} will
     * be executed.
     *
     * @param config the configuration to check
     * @param apply  the runnable to execute, if the configuration is valid
     */
    private void applyValidConfiguration(AdaptiveImageServletMappingConfigurationFactory config, Runnable apply) {
        if (!config.getResourceTypes().isEmpty() && !config.getSelectors().isEmpty() && !config.getExtensions().isEmpty()) {
            apply.run();
        } else {
            LOG.warn("One of the servlet registration properties from the following {} configuration is empty: {}.",
                    config.getClass().getName(), config.toString());
        }
    }
}
