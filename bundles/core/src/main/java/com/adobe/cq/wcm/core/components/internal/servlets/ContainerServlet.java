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
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v1.AccordionImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.CarouselImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.TabsImpl;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.api.WCMException;
import com.day.crx.JcrConstants;

/**
 * Servlet that deletes/reorders the child nodes of a Accordion/Carousel/Tabs container.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes=" + CarouselImpl.RESOURCE_TYPE,
                "sling.servlet.resourceTypes=" + TabsImpl.RESOURCE_TYPE,
                "sling.servlet.resourceTypes=" + AccordionImpl.RESOURCE_TYPE,
                "sling.servlet.selectors=" + ContainerServlet.SELECTOR,
                "sling.servlet.extensions=" + ContainerServlet.EXTENSION
        }
)
public class ContainerServlet extends SlingAllMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerServlet.class);

    protected static final String SELECTOR = "container";
    protected static final String EXTENSION = "html";

    private static final String PARAM_DELETED_CHILDREN = "delete";
    private static final String PARAM_ORDERED_CHILDREN = "order";
    private static final String RT_GHOST = "wcm/msm/components/ghost";

    @Reference
    private transient LiveRelationshipManager liveRelationshipManager;

    @Override
    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response) throws IOException {

        ResourceResolver resolver = request.getResourceResolver();
        Resource container = request.getResource();

        // Delete the child items
        try {
            String[] deletedChildrenNames = request.getParameterValues(PARAM_DELETED_CHILDREN);
            if (deletedChildrenNames != null && deletedChildrenNames.length > 0) {
                for (String childName : deletedChildrenNames) {
                    Resource child = container.getChild(childName);
                    if (child != null) {
                        // For deleted items that have a live relationship, ensure a ghost is created
                        LiveRelationship liveRelationship = liveRelationshipManager.getLiveRelationship(child, false);
                        if (liveRelationship != null && liveRelationship.getStatus().isSourceExisting()) {
                            liveRelationshipManager.cancelRelationship(resolver, liveRelationship, true, false);
                            Resource parent = child.getParent();
                            String name = child.getName();
                            resolver.delete(child);
                            if (parent != null) {
                                createGhost(parent, name, resolver);
                            }
                        } else {
                            resolver.delete(child);
                        }
                    }
                }
            }
            resolver.commit();
        } catch (PersistenceException | RepositoryException | WCMException e) {
            LOGGER.error("Could not delete items of the container at {}", container.getPath(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

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

    private void createGhost(@NotNull Resource parent, String name, ResourceResolver resolver)
            throws PersistenceException, RepositoryException, WCMException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        properties.put(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, RT_GHOST);
        resolver.create(parent, name, properties);
    }
}
