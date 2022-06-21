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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + AllowedColorSwatchesDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class AllowedColorSwatchesDataSourceServlet extends SlingSafeMethodsServlet {

    protected static final String RESOURCE_TYPE = "core/wcm/components/commons/datasources/allowedcolorswatches/v1";
    protected static final String PN_ALLOWED_COLOR_SWATCHES = "allowedColorSwatches";
    protected static final String PN_COLOR_VALUE = "value";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource allowedColorSwatchesDataSource = new SimpleDataSource(getAllowedColorSwatches(request).iterator());
        request.setAttribute(DataSource.class.getName(), allowedColorSwatchesDataSource);
    }

    protected List<Resource> getAllowedColorSwatches(@NotNull SlingHttpServletRequest request) {
        List<Resource> colors = Collections.emptyList();
        String contentPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
        ResourceResolver resolver = request.getResourceResolver();
        ContentPolicy policy = null;
        if (StringUtils.isNotEmpty(contentPath)) {
            policy = getContentPolicy(contentPath, resolver);
        }

        if (StringUtils.isEmpty(contentPath) || policy == null) {
            contentPath = request.getRequestPathInfo().getSuffix();
            if (StringUtils.isNotEmpty(contentPath)) {
                policy = getContentPolicy(contentPath, resolver);
            }
        }

        if (policy != null) {
            colors = populateColors(policy, resolver);
        }

        return colors;
    }

    private ContentPolicy getContentPolicy(@NotNull String path, @NotNull ResourceResolver resolver) {
        ContentPolicy policy = null;
        ContentPolicyManager policyMgr = resolver.adaptTo(ContentPolicyManager.class);
        Resource contentResource = resolver.getResource(path);
        if (contentResource != null && policyMgr != null) {
            policy = policyMgr.getPolicy(contentResource);
        }
        return policy;
    }

    private List<Resource> populateColors(@NotNull ContentPolicy policy, @NotNull ResourceResolver resolver) {
        List<Resource> colors = new ArrayList<>();
            ValueMap color = null;
            ValueMap properties = policy.getProperties();
            if (properties != null) {
                String[] allowedColorSwatches = properties.get(PN_ALLOWED_COLOR_SWATCHES, String[].class);
                if (allowedColorSwatches != null && allowedColorSwatches.length > 0) {
                    for (String allowedColorSwatch : allowedColorSwatches) {
                        color = new ValueMapDecorator(new HashMap<>());
                        color.put(PN_COLOR_VALUE, allowedColorSwatch);
                        colors.add(new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
                                color));
                    }
                }
            }
            return colors;
    }
}
