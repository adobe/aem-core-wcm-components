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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.granite.ui.components.ExpressionCustomizer;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.components.ComponentManager;


@Component(
        service = {Servlet.class},
        property = {
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=" + ImageDelegateRenderCondition.RESOURCE_TYPE,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=GET"
        }
)
public class ImageDelegateRenderCondition extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE = "core/wcm/components/renderconditions/imagedelegate";
    private static final String IMAGE_DELEGATE_DESCRIPTION = "imageDelegateDescription";
    private static final String IMAGE_DELEGATE_DESCRIPTION_TEXT = "Image rendering is delegated to the {0} component.";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        boolean hasImageDelegation = false;
        ResourceResolver resourceResolver = request.getResourceResolver();
        ComponentManager componentManager = resourceResolver.adaptTo(ComponentManager.class);
        String suffix = request.getRequestPathInfo().getSuffix();
        if (componentManager != null && StringUtils.isNotEmpty(suffix)) {
            Resource policiesRootPage = getPoliciesRootPage(resourceResolver, suffix);
            if (policiesRootPage != null) {
                String resourceType = suffix.substring(policiesRootPage.getPath().length() + 1, suffix.lastIndexOf('/'));
                com.day.cq.wcm.api.components.Component component = componentManager.getComponent(resourceType);
                if (component != null && component.isAccessible()) {
                    String imageDelegate = component.getProperties().get(AbstractImageDelegatingModel.IMAGE_DELEGATE, String.class);
                    if (StringUtils.isNotEmpty(imageDelegate)) {
                        hasImageDelegation = true;
                        com.day.cq.wcm.api.components.Component delegate = componentManager.getComponent(imageDelegate);
                        if (delegate != null && delegate.isAccessible()) {
                            I18n i18n = new I18n(request);
                            ExpressionCustomizer customizer = ExpressionCustomizer.from(request);
                            customizer.setVariable(AbstractImageDelegatingModel.IMAGE_DELEGATE, delegate);
                            customizer.setVariable(IMAGE_DELEGATE_DESCRIPTION, i18n.getVar(IMAGE_DELEGATE_DESCRIPTION_TEXT, null,
                                    i18n.getVar(delegate.getTitle())));
                        }
                    }
                }
            }
        }
        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(hasImageDelegation));
    }

    /**
     * Given a {@code resourceResolver} and a {@code path} to a content policy, this method will return the policies root page
     * {@link Resource} for the provided {@code path}.
     *
     * @param resourceResolver a resource resolver
     * @param path             the path to analyse
     * @return the policies root page as a {@link Resource} or {@code null}
     */
    @Nullable
    private Resource getPoliciesRootPage(@NotNull ResourceResolver resourceResolver, @NotNull String path) {
        Resource resource = resourceResolver.getResource(path);
        if (resource != null && resource.getResourceType().equals(NameConstants.NT_PAGE)) {
            return resource;
        } else if (StringUtils.isNotEmpty(path) && path.lastIndexOf('/') > 0) {
            return getPoliciesRootPage(resourceResolver, path.substring(0, path.lastIndexOf('/')));
        } else {
            return null;
        }
    }
}
