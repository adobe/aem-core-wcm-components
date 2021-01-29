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
package com.adobe.cq.wcm.core.components.internal.servlets;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.config.PWACaConfig;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + IsSiteProjectRenderConditionServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET"
        }
)
/*
 * a condition to determine if the resource at the given path is a sites project
 */
public class IsSiteProjectRenderConditionServlet extends SlingSafeMethodsServlet {

    static final String RESOURCE_TYPE = "core/wcm/components/commons/renderconditions/issiteproject";

    @Reference
    private ExpressionResolver expressionResolver;

    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) {
        final ExpressionHelper ex = new ExpressionHelper(expressionResolver, request);
        final Config config = new Config(request.getResource());
        final String path = ex.getString(config.get("path", String.class));
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        boolean isSiteProject = false;
        if (pageManager != null) {
            Page page = pageManager.getContainingPage(path);
            if (page != null) {
                Resource contextResource = page.getContentResource();
                ConfigurationBuilder configurationBuilder = contextResource.adaptTo(ConfigurationBuilder.class);
                if (configurationBuilder != null) {
                    PWACaConfig caConfig = configurationBuilder.as(PWACaConfig.class);
                    isSiteProject = page.getDepth() == caConfig.projectSiteRootLevel();
                }
            }
        }
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(isSiteProject));
    }
}