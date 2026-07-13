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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.services.contentai.ContentSourceSearchMerger;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

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
    private static final long serialVersionUID = 1L;

    @Override
    protected Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query) throws ContentAIClientException {
        List<String> sources = model.getContentSources();
        if (sources.isEmpty()) {
            return new ContentSourceSearchResult();
        }
        List<ContentSourceSearchResult> partials = new ArrayList<>();
        String contentSourceType = model.getContentSourceType();
        for (String source : sources) {
            partials.add(contentAIClient.search(source, contentSourceType, query, model.getResultsSize()));
        }
        return ContentSourceSearchMerger.merge(partials, model.getResultsSize());
    }

    @Override
    @NotNull
    protected String getOperationName() {
        return SELECTOR;
    }
}
