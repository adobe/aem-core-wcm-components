/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;

import java.util.HashMap;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.ValueMapResource;

import static org.apache.sling.api.resource.Resource.RESOURCE_TYPE_NON_EXISTING;

/**
 * Abstract data source providing common logic.
 */
public abstract class AbstractDataSourceServlet extends SlingSafeMethodsServlet {

    /**
     * Name of the resource property containing the path to a component. The servlet uses the content fragment
     * referenced by the component. The value may contain expressions.
     */
    public final static String PN_COMPONENT_PATH = "componentPath";


    /**
     * Returns an expression resolver to be used to resolve expressions in the configuration properties (see
     * {@link #PN_COMPONENT_PATH}).
     *
     * @return an expression resolver
     */
    @NotNull
    protected abstract ExpressionResolver getExpressionResolver();

    /**
     * Returns datasource configuration.
     *
     * @param request the request
     * @return datasource configuration.
     */
    Config getConfig(SlingHttpServletRequest request) {
        // get datasource configuration
        Resource datasource = request.getResource().getChild(Config.DATASOURCE);
        if (datasource == null) {
            return null;
        }
        return new Config(datasource);
    }

    /**
     * Get value map corresponding to resource of the component.
     *
     * @param config  datasource configuration
     * @param request the request
     * @return value map.
     */
    ValueMap getComponentValueMap(Config config, SlingHttpServletRequest request) {
        if (config == null) {
            return null;
        }
        String componentPath = getParameter(config, PN_COMPONENT_PATH, request);
        if (componentPath == null) {
            return null;
        }

        // get component resource
        Resource component = request.getResourceResolver().getResource(componentPath);
        if (component == null) {
            return null;
        }
        return component.getValueMap();
    }

    /**
     * Reads a parameter from the specified datasource configuration, resolving expressions using the
     * {@link ExpressionResolver}. If the parameter is not found, {@code null} is returned.
     */
    @Nullable
    protected String getParameter(@NotNull Config config, @NotNull String name,
                                  @NotNull SlingHttpServletRequest request) {
        // get value from configuration
        String value = config.get(name, String.class);
        if (value == null) {
            return null;
        }

        // evaluate value using the expression helper
        ExpressionHelper expressionHelper = new ExpressionHelper(getExpressionResolver(), request);
        return expressionHelper.getString(value);
    }

    /**
     * Creates a virtual resource to use in a datasource.
     */
    @NotNull
    protected Resource createResource(@NotNull ResourceResolver resolver, @NotNull String textValue, @NotNull String valueValue) {
        ValueMap properties = new ValueMapDecorator(new HashMap<>());
        properties.put("text", textValue);
        properties.put("value", valueValue);
        return new ValueMapResource(resolver, new ResourceMetadata(), RESOURCE_TYPE_NON_EXISTING, properties);
    }
}
