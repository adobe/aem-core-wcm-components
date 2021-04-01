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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.Embed;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * Data source that returns the dialog options for all allowed embeddables.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes=" + EmbeddableOptionsDataSourceServlet.RESOURCE_TYPE_V1,
        "sling.servlet.resourceTypes=" + EmbeddableOptionsDataSourceServlet.RESOURCE_TYPE_V2,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class EmbeddableOptionsDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/datasources/embeddableoptions";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/datasources/embeddableoptions";

    private static final long serialVersionUID = 7672484310019288602L;
    private static final String NN_DIALOG = "cq:dialog";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        SimpleDataSource embeddableOptionsDataSource = new SimpleDataSource(getEmbeddableOptions(request).iterator());
        request.setAttribute(DataSource.class.getName(), embeddableOptionsDataSource);
    }

    private List<Resource> getEmbeddableOptions(@NotNull SlingHttpServletRequest request) {
        List<Resource> embeddableOptionsResources = new ArrayList<>();
        ResourceResolver resolver = request.getResourceResolver();
        Resource contentResource = resolver.getResource((String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE));
        ContentPolicyManager policyManager = resolver.adaptTo(ContentPolicyManager.class);
        if (policyManager != null) {
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
            if (properties != null) {
                String[] allowedEmbeddables = properties.get(Embed.PN_DESIGN_ALLOWED_EMBEDDABLES, String[].class);
                if (allowedEmbeddables != null && allowedEmbeddables.length > 0) {
                    for (String allowedEmbeddable : allowedEmbeddables) {
                        Resource dialogResource = resolver.getResource(allowedEmbeddable + "/" + NN_DIALOG);
                        if (dialogResource != null) {
                            embeddableOptionsResources.add(dialogResource);
                        }
                    }
                }
            }
        }
        return embeddableOptionsResources;
    }
}
