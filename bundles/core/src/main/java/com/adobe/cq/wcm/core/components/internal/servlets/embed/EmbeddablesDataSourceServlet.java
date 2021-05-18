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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Data source that returns all enabled embeddables.
 */
@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes=" + EmbeddablesDataSourceServlet.RESOURCE_TYPE_V1,
        "sling.servlet.resourceTypes=" + EmbeddablesDataSourceServlet.RESOURCE_TYPE_V2,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class EmbeddablesDataSourceServlet extends SlingSafeMethodsServlet {

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/embed/v1/datasources/embeddables";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/embed/v2/datasources/embeddables";

    private static final long serialVersionUID = 1L;
    private static final String COMPONENT_PROPERTY_ENABLED = "enabled";
    private static final String COMPONENT_PROPERTY_ORDER = "order";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        SimpleDataSource embeddablesDataSource = new SimpleDataSource(getEmbeddableResources(
            request.getResourceResolver()).iterator());
        request.setAttribute(DataSource.class.getName(), embeddablesDataSource);
    }

    private List<Resource> getEmbeddableResources(ResourceResolver resourceResolver) {
        List<Resource> embeddableResources = new ArrayList<>();
        Iterator<EmbeddableDescription> embeddables = findEmbeddables(resourceResolver).iterator();
        while (embeddables.hasNext()) {
            EmbeddableDescription description = embeddables.next();
            embeddableResources.add(new EmbeddableDataResourceSource(description, resourceResolver));
        }
        return embeddableResources;
    }

    static Collection<EmbeddableDescription> findEmbeddables(ResourceResolver resourceResolver) {
        String[] searchPaths = resourceResolver.getSearchPath();
        for (int i = 0; i < searchPaths.length; i++) {
            searchPaths[i] = searchPaths[i].substring(0, searchPaths[i].length() - 1);
        }
        final Map<String, EmbeddableDescription> map = new HashMap<>();
        final List<String> disabledComponents = new ArrayList<>();
        for (final String path : searchPaths) {
            final StringBuilder queryStringBuilder = new StringBuilder("/jcr:root");
            queryStringBuilder.append(path);
            queryStringBuilder.append("//* [@");
            queryStringBuilder.append(JcrResourceConstants.SLING_RESOURCE_SUPER_TYPE_PROPERTY);
            queryStringBuilder.append("='");
            queryStringBuilder.append(Embed.RT_EMBEDDABLE_V1);
            queryStringBuilder.append("']");
            final Iterator<Resource> resourceIterator = resourceResolver.findResources(queryStringBuilder.toString(), "xpath");
            while (resourceIterator.hasNext()) {
                final Resource embeddableResource = resourceIterator.next();
                final ValueMap properties = ResourceUtil.getValueMap(embeddableResource);
                final String resourceType = embeddableResource.getPath().substring(path.length() + 1);
                if (properties.get(COMPONENT_PROPERTY_ENABLED, Boolean.TRUE)) {
                    if (!map.containsKey(resourceType) && !disabledComponents.contains(resourceType)) {
                        map.put(resourceType, new EmbeddableDescription(resourceType, embeddableResource.getName(),
                            properties));
                    }
                } else {
                    disabledComponents.add(resourceType);
                }
            }
        }
        final List<EmbeddableDescription> entries = new ArrayList<>(map.values());
        Collections.sort(entries);
        return entries;
    }

    public static class EmbeddableDataResourceSource extends TextValueDataResourceSource {

        private final EmbeddableDescription description;

        EmbeddableDataResourceSource(EmbeddableDescription description, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
            this.description = description;
        }

        @Override
        public String getText() {
            return description.getTitle();
        }

        @Override
        public String getValue() {
            return description.getResourceType();
        }
    }

    public static class EmbeddableDescription implements Comparable<EmbeddableDescription> {

        private final String resourceType;
        private final String title;
        private final int order;

        public EmbeddableDescription(final String rt, final String defaultName, final ValueMap properties) {
            this.resourceType = rt;
            this.title = properties.get(JcrConstants.JCR_TITLE, defaultName);
            this.order = properties.get(COMPONENT_PROPERTY_ORDER, 0);
        }

        public String getResourceType() {
            return this.resourceType;
        }

        public String getTitle() {
            return this.title;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(@NotNull final EmbeddableDescription o) {
            if (this.order < o.order) {
                return -1;
            } else if (this.order == o.order) {
                return this.title.compareTo(o.title);
            }
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            return compareTo((EmbeddableDescription) obj) == 0;
        }

        @Override
        public int hashCode() {
            return this.title.hashCode() + this.title.hashCode();
        }
    }
}
