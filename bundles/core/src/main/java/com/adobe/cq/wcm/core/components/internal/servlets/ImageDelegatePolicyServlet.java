/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
import java.util.Objects;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.day.cq.wcm.api.WCMFilteringResourceWrapper;
import com.day.cq.wcm.api.components.ComponentManager;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes = ImageDelegatePolicyServlet.RESOURCE_TYPE
)
public class ImageDelegatePolicyServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE = "core/wcm/components/include/imagedelegate";
    private static final String PN_PATH = "path";

    @Reference
    ExpressionResolver expressionResolver;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        ComponentManager componentManager = resourceResolver.adaptTo(ComponentManager.class);
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        com.day.cq.wcm.api.components.Component component =
                Optional.ofNullable(requestPathInfo.getSuffix())
                        .flatMap(s -> Optional.ofNullable(resourceResolver.getResource(s)))
                        .flatMap(r -> Optional.ofNullable(componentManager)
                                .map(c -> c.getComponentOfResource(r)))
                        .orElse(null);
        if (Objects.nonNull(component)) {
            ValueMap properties = component.getProperties();
            String imageDelegate = properties.get(AbstractImageDelegatingModel.IMAGE_DELEGATE, String.class);
            RequestDispatcher requestDispatcher = Optional.ofNullable(request.getResource().getValueMap().get(PN_PATH
                            , String.class))
                    .map(p -> new WCMFilteringResourceWrapper(resourceResolver.getResource(p), new SyntheticResource(resourceResolver,
                            requestPathInfo.getSuffix(),
                            imageDelegate), expressionResolver, request)).map(request::getRequestDispatcher).orElse(null);
            if (Objects.nonNull(requestDispatcher)) {
                requestDispatcher.include(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
