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

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;


/**
 * Render condition that returns true if the requested resource's display mode matches with the display mode of the
 * referenced content fragment component. The display mode of the component is obtained by reading
 * {@link ElementNamesRenderCondition#PARAM_AND_PN_DISPLAY_MODE} parameter from request or reading the property of
 * same name if parameter is not passed. If the obtained value matches the value of
 * {@link ElementNamesRenderCondition#PARAM_AND_PN_DISPLAY_MODE} property present in requested resource's config,
 * the render condition is true otherwise false.
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + ElementNamesRenderCondition.RESOURCE_TYPE,
                "sling.servlet.methods=GET"
        }
)
public class ElementNamesRenderCondition extends SlingSafeMethodsServlet {

    static final String RESOURCE_TYPE = "core/wcm/components/contentfragment/v1/renderconditions/elementnames";

    /**
     * Name of the resource property containing the path to a
     * {@code /apps/core/wcm/components/contentfragment} component to use for this render condition.
     * The value may contain expressions.
     */
    private final static String PN_COMPONENT_PATH = "componentPath";

    @Reference
    private transient ExpressionResolver expressionResolver;

    /**
     * Defines a parameter name and property name whose value decides whether dialog field would be displayed or not.
     */
    static final String PARAM_AND_PN_DISPLAY_MODE = "displayMode";


    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        // return false by default
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(false));

        // get component path
        Config config = new Config(request.getResource());
        String componentPath = getParameter(config, request, PN_COMPONENT_PATH, String.class);
        if (componentPath == null) {
            return;
        }

        // get component resource
        Resource component = request.getResourceResolver().getResource(componentPath);
        if (component == null) {
            return;
        }

        // override fragment path if set
        ValueMap properties = component.getValueMap();

        String displayMode = properties.get(PARAM_AND_PN_DISPLAY_MODE, String.class);
        String displayModeParam = request.getParameter(PARAM_AND_PN_DISPLAY_MODE);
        if (displayModeParam != null) {
            displayMode = displayModeParam;
        }
        boolean shouldShow = displayMode != null &&
                displayMode.equals(getParameter(config, request, PARAM_AND_PN_DISPLAY_MODE, String.class));
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(shouldShow));
    }


    /**
     * Reads a parameter from the specified datasource configuration, resolving expressions using the
     * {@link ExpressionResolver}. If the parameter is not found, {@code null} is returned.
     */
    @Nullable
    private <T> T getParameter(@NotNull Config config, @NotNull SlingHttpServletRequest request, @NotNull String name,
                               Class<T> type) {
        // get value from configuration
        String value = config.get(name, String.class);
        if (value == null) {
            return null;
        }

        // evaluate value using the expression helper
        ExpressionHelper expressionHelper = new ExpressionHelper(expressionResolver, request);
        return expressionHelper.get(value, type);
    }

}
