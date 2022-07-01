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

import com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Editor;
import com.adobe.cq.wcm.core.components.internal.models.v1.PanelContainerImpl;
import com.day.cq.wcm.api.WCMMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet that deletes/reorders the child nodes of a Accordion/Carousel/Tabs container.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=" + HttpConstants.METHOD_POST,
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes=" + PanelContainerImpl.RESOURCE_TYPE,
        "sling.servlet.selectors=" + ContainerServlet.SELECTOR,
        "sling.servlet.extensions=" + ContainerServlet.EXTENSION
    }
)
public class ContainerServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerServlet.class);

    protected static final String SELECTOR = "container";
    protected static final String EXTENSION = "html";

    private static final String PARAM_ORDERED_CHILDREN = "order";

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException {

        ResourceResolver resolver = request.getResourceResolver();
        Resource container = request.getResource();

        // Order the child items
        try {
            String[] orderedChildrenNames = request.getParameterValues(PARAM_ORDERED_CHILDREN);
            if (orderedChildrenNames != null && orderedChildrenNames.length > 0) {
                final Node containerNode = container.adaptTo(Node.class);
                if (containerNode != null) {

                    // Create the items if they don't exist
                    for (int i = 0; i < orderedChildrenNames.length; i++) {
                        if (!containerNode.hasNode(orderedChildrenNames[i])) {
                            containerNode.addNode(orderedChildrenNames[i]);
                        }
                    }

                    // Order the items
                    for (int i = orderedChildrenNames.length - 1; i >= 0; i--) {
                        // Put the last item at the end
                        if (i == orderedChildrenNames.length - 1) {
                            containerNode.orderBefore(orderedChildrenNames[i], null);
                        } else {
                            containerNode.orderBefore(orderedChildrenNames[i], orderedChildrenNames[i + 1]);
                        }
                    }

                    // Persist the changes
                    resolver.commit();
                }
            }
        } catch (RepositoryException | PersistenceException e) {
            LOGGER.error("Could not order items of the container at {}", container.getPath(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
        Resource container = request.getResource();
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        options.setForceResourceType(Editor.RESOURCE_TYPE);
        request.setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, WCMMode.DISABLED);
        options.setReplaceSuffix(container.getPath());
        RequestDispatcher dispatcher = request.getRequestDispatcher(container, options);
        if (dispatcher != null) {
            dispatcher.include(request, response);
        }
    }
}
