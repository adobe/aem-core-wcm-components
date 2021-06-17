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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * Data source that returns the allowed embeddables.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes=" + AllowedEmbeddablesDataSourceServlet.RESOURCE_TYPE_V1,
        "sling.servlet.resourceTypes=" + AllowedEmbeddablesDataSourceServlet.RESOURCE_TYPE_V2,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class AllowedEmbeddablesDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/datasources/allowedembeddables";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/datasources/allowedembeddables";

    private static final long serialVersionUID = -3528015217249498756L;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        SimpleDataSource allowedEmbeddablesDataSource = new SimpleDataSource(getAllowedEmbeddables(request).iterator());
        request.setAttribute(DataSource.class.getName(), allowedEmbeddablesDataSource);
    }

    private List<Resource> getAllowedEmbeddables(@NotNull SlingHttpServletRequest request) {
        List<Resource> allowedEmbeddableResources = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        if (policyManager == null || contentResource == null) {
            return allowedEmbeddableResources;
        }
        ValueMap properties = null;
        ContentPolicy policy = policyManager.getPolicy(contentResource);
        if (policy != null) {
            properties = policy.getProperties();
        } else {
            Designer designer = resolver.adaptTo(Designer.class);
            if (designer != null) {
                properties = designer.getStyle(contentResource);
            }
        }

        if (properties == null) {
            return allowedEmbeddableResources;
        }
        String[] allowedEmbeddableResourceTypes = properties.get(Embed.PN_DESIGN_ALLOWED_EMBEDDABLES, String[].class);
        if (allowedEmbeddableResourceTypes != null && allowedEmbeddableResourceTypes.length > 0) {
            allowedEmbeddableResources.add(new AllowedEmbeddableResource("Select", "", resolver));
            for (String embeddableResourceType : allowedEmbeddableResourceTypes) {
                Resource componentResource = resolver.getResource(embeddableResourceType);
                if (componentResource != null) {
                    allowedEmbeddableResources.add(new AllowedEmbeddableResource(componentResource.getValueMap().get(
                        JcrConstants.JCR_TITLE, componentResource.getName()), embeddableResourceType, resolver));
                }
            }
        }
        return allowedEmbeddableResources;
    }

    private static class AllowedEmbeddableResource extends TextValueDataResourceSource {

        private final String title;
        private final String resourceType;

        AllowedEmbeddableResource(String embeddableTitle, String embeddableResourceType, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
            this.title = embeddableTitle;
            this.resourceType = embeddableResourceType;
        }

        @Override
        public String getText() {
            return title;
        }

        @Override
        public String getValue() {
            return resourceType;
        }
    }
}
