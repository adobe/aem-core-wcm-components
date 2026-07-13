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
package com.adobe.cq.wcm.core.components.internal.services.contentai;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

/**
 * Fetches all pages of search results from a single Content AI content source.
 */
public final class ContentSourceSearchAggregator {

    static final int API_PAGE_SIZE = 50;
    static final int MAX_PAGES = 20;

    private ContentSourceSearchAggregator() {
    }

    /**
     * @param client          Content AI client
     * @param contentSource   source name
     * @param contentSourceType source type
     * @param query           search query
     * @return aggregated result containing every page of matches for the source
     * @throws ContentAIClientException if a Content AI request fails
     */
    @NotNull
    public static ContentSourceSearchResult fetchAll(@NotNull ContentAIClient client, @NotNull String contentSource,
        @NotNull String contentSourceType, @NotNull String query) throws ContentAIClientException {
        List<ContentSourceSearchResult.Item> items = new ArrayList<>();
        long totalResults = 0;
        String cursor = null;

        for (int page = 0; page < MAX_PAGES; page++) {
            ContentSourceSearchResult partial = client.search(contentSource, contentSourceType, query, API_PAGE_SIZE, cursor);
            totalResults = Math.max(totalResults, partial.getTotalResults());
            if (partial.getResults() != null) {
                items.addAll(partial.getResults());
            }
            cursor = partial.getCursor();
            if (StringUtils.isBlank(cursor) || items.size() >= totalResults) {
                break;
            }
        }

        ContentSourceSearchResult result = new ContentSourceSearchResult();
        result.setResults(items);
        result.setTotalResults(Math.max(totalResults, items.size()));
        return result;
    }
}
