/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.services.contentai.ContentSourceLabelFormatter;
import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListItem;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListResult;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

/**
 * Author dialog datasource listing Content AI content sources of a given type from the generic
 * {@code GET /content-sources?type={type}} listing (e.g. {@code type=AEM_PUBLISH}, {@code type=ACQUISITION}).
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.resourceTypes=" + ContentSourcesDataSourceServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    }
)
public class ContentSourcesDataSourceServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentSourcesDataSourceServlet.class);

    public static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/datasources/contentsources";

    private static final String PARAM_CONTENT_SOURCE_TYPE = "contentSourceType";

    /**
     * Safety cap on the number of {@code GET /content-sources} pages followed per request, so a misbehaving
     * Content AI response (e.g. a cursor that never comes back blank) can't turn this into an unbounded loop.
     */
    private static final int MAX_PAGES = 50;

    private static final long serialVersionUID = 1L;

    @Reference
    private transient ContentAIClient contentAIClient;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
        throws ServletException, IOException {

        String contentSourceType = resolveContentSourceType(request);
        List<Resource> options = new ArrayList<>();

        try {
            ResourceResolver resolver = request.getResourceResolver();
            for (ContentSourceListItem item : fetchAllContentSources(contentSourceType)) {
                if (!matchesType(item, contentSourceType) || !isPublicSource(item)) {
                    continue;
                }
                String indexName = ContentSourceLabelFormatter.resolveIndexName(item.getName(), item.getId());
                if (StringUtils.isBlank(indexName)) {
                    continue;
                }
                String label = ContentSourceLabelFormatter.formatLabel(indexName, item.getResolvableDescription());
                options.add(new ContentSourceOptionResource(resolver, label, indexName));
            }
        } catch (ContentAIClientException e) {
            LOGGER.error("Failed to list Content AI content sources", e);
        }

        DataSource dataSource = options.isEmpty()
            ? EmptyDataSource.instance()
            : new SimpleDataSource(options.iterator());
        request.setAttribute(DataSource.class.getName(), dataSource);
    }

    /**
     * Fetches every Content AI content source of the given type across all cursor pages, so the picker isn't
     * silently truncated to just the first page {@code GET /content-sources?type={type}} returns.
     *
     * @param contentSourceType the Content AI content source type to list, e.g. {@code "AEM_PUBLISH"} or
     *                           {@code "ACQUISITION"}
     * @return the combined items from every page, in the order returned by the API
     * @throws ContentAIClientException if any page request fails
     */
    @NotNull
    private List<ContentSourceListItem> fetchAllContentSources(@NotNull String contentSourceType)
        throws ContentAIClientException {
        List<ContentSourceListItem> allItems = new ArrayList<>();

        ContentSourceListResult page = contentAIClient.listContentSources(contentSourceType, null);
        appendItems(allItems, page);
        String cursor = page != null ? page.getCursor() : null;

        int pagesFetched = 1;
        while (StringUtils.isNotBlank(cursor) && pagesFetched < MAX_PAGES) {
            page = contentAIClient.listContentSources(contentSourceType, cursor);
            appendItems(allItems, page);
            cursor = page != null ? page.getCursor() : null;
            pagesFetched++;
        }
        if (StringUtils.isNotBlank(cursor)) {
            LOGGER.warn("Stopped listing Content AI content sources after {} pages ({} items); the API is still "
                + "returning a cursor, so the list may be incomplete", pagesFetched, allItems.size());
        }
        return allItems;
    }

    private void appendItems(@NotNull List<ContentSourceListItem> allItems, @Nullable ContentSourceListResult page) {
        if (page != null && page.getItems() != null) {
            allItems.addAll(page.getItems());
        }
    }

    @NotNull
    private String resolveContentSourceType(@NotNull SlingHttpServletRequest request) {
        String fromRequest = request.getParameter(PARAM_CONTENT_SOURCE_TYPE);
        if (isResolvableContentSourceType(fromRequest)) {
            return fromRequest.trim();
        }

        Resource resource = request.getResource();
        if (resource != null) {
            String fromResource = resource.getValueMap().get(PARAM_CONTENT_SOURCE_TYPE, String.class);
            if (isResolvableContentSourceType(fromResource)) {
                return fromResource.trim();
            }
            Resource datasourceChild = resource.getChild(Config.DATASOURCE);
            if (datasourceChild != null) {
                String fromChild = datasourceChild.getValueMap().get(PARAM_CONTENT_SOURCE_TYPE, String.class);
                if (isResolvableContentSourceType(fromChild)) {
                    return fromChild.trim();
                }
            }
        }

        String contentPath = (String) request.getAttribute(Value.CONTENTPATH_ATTRIBUTE);
        if (StringUtils.isNotBlank(contentPath)) {
            Resource contentResource = request.getResourceResolver().getResource(contentPath);
            if (contentResource != null) {
                String fromComponent = contentResource.getValueMap().get(PARAM_CONTENT_SOURCE_TYPE, String.class);
                if (isResolvableContentSourceType(fromComponent)) {
                    return fromComponent.trim();
                }
            }
        }

        return ContentAISupportedSearch.DEFAULT_CONTENT_SOURCE_TYPE;
    }

    private boolean isResolvableContentSourceType(@Nullable String value) {
        return StringUtils.isNotBlank(value) && !value.contains("${");
    }

    private boolean matchesType(@NotNull ContentSourceListItem item, @NotNull String contentSourceType) {
        return Objects.equals(item.getType(), contentSourceType);
    }

    private boolean isPublicSource(@NotNull ContentSourceListItem item) {
        if (item.getConfig() == null || item.getConfig().getAccess() == null) {
            return true;
        }
        return item.isPublicAccess();
    }

    private static class ContentSourceOptionResource extends TextValueDataResourceSource {

        private final String text;
        private final String value;

        ContentSourceOptionResource(@NotNull ResourceResolver resolver, @NotNull String text, @NotNull String value) {
            super(resolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
            this.text = text;
            this.value = value;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
