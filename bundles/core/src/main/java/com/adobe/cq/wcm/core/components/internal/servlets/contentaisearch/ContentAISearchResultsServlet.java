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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.services.contentai.ContentSourceSearchAggregator;
import com.adobe.cq.wcm.core.components.internal.services.contentai.ContentSourceSearchMerger;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAISearchResponse;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet exposing the ContentAI Supported Search component's results-list endpoint,
 * backed by the Content AI AI Search API.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=" + AbstractContentAISearchServlet.RESOURCE_TYPE,
        "sling.servlet.selectors=" + ContentAISearchResultsServlet.SELECTOR,
        "sling.servlet.extensions=" + AbstractContentAISearchServlet.EXTENSION
    }
)
public class ContentAISearchResultsServlet extends AbstractContentAISearchServlet {

    protected static final String SELECTOR = "search";
    protected static final String PARAM_CURSORS = "cursors";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final long serialVersionUID = 1L;

    @Override
    protected Object executeQuery(@NotNull SlingHttpServletRequest request, @NotNull ContentAISupportedSearch model,
        @NotNull String query) throws ContentAIClientException {
        List<String> sources = model.getContentSources();
        if (sources.isEmpty()) {
            return new ContentAISearchResponse();
        }

        Map<String, String> requestCursors = parseSourceCursors(request.getParameter(PARAM_CURSORS));
        boolean loadMore = !requestCursors.isEmpty();
        int apiLimit = Math.min(ContentSourceSearchAggregator.API_PAGE_SIZE, Math.max(model.getResultsSize(), 1));
        String contentSourceType = model.getContentSourceType();

        List<ContentSourceSearchResult> partials = new ArrayList<>();
        Map<String, String> nextCursors = new LinkedHashMap<>();
        for (String source : sources) {
            if (loadMore && !requestCursors.containsKey(source)) {
                continue;
            }
            String cursor = loadMore ? requestCursors.get(source) : null;
            ContentSourceSearchResult partial = ContentSourceSearchAggregator.fetchPage(
                contentAIClient, source, contentSourceType, query, apiLimit, cursor);
            partials.add(partial);
            if (StringUtils.isNotBlank(partial.getCursor())) {
                nextCursors.put(source, partial.getCursor());
            }
        }

        return ContentSourceSearchMerger.mergeToResponse(partials, nextCursors, 0);
    }

    @NotNull
    static Map<String, String> parseSourceCursors(String cursorsJson) {
        if (StringUtils.isBlank(cursorsJson)) {
            return Collections.emptyMap();
        }
        try {
            Map<String, String> parsed = MAPPER.readValue(cursorsJson, new TypeReference<Map<String, String>>() {
            });
            return parsed != null ? parsed : Collections.emptyMap();
        } catch (IOException e) {
            return Collections.emptyMap();
        }
    }

    @Override
    @NotNull
    protected String getOperationName() {
        return SELECTOR;
    }
}
