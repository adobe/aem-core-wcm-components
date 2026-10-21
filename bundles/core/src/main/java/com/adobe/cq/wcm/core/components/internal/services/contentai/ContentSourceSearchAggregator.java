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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

/**
 * Fetches a single cursor page of search results from a Content AI content source.
 */
public final class ContentSourceSearchAggregator {

    public static final int API_PAGE_SIZE = 50;

    private ContentSourceSearchAggregator() {
    }

    /**
     * @param client            Content AI client
     * @param contentSource     source name
     * @param contentSourceType source type
     * @param query             search query
     * @param limit             requested page size (capped at {@link #API_PAGE_SIZE})
     * @param cursor            optional cursor from a previous response
     * @return one page of matches for the source
     * @throws ContentAIClientException if a Content AI request fails
     */
    @NotNull
    public static ContentSourceSearchResult fetchPage(@NotNull ContentAIClient client, @NotNull String contentSource,
        @NotNull String contentSourceType, @NotNull String query, int limit, @Nullable String cursor)
        throws ContentAIClientException {
        int effectiveLimit = Math.min(Math.max(limit, 1), API_PAGE_SIZE);
        return client.search(contentSource, contentSourceType, query, effectiveLimit, cursor);
    }
}
