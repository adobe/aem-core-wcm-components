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
package com.adobe.cq.wcm.core.components.services.contentai;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * JSON response for the ContentAI Supported Search results endpoint, including cursor pagination metadata.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentAISearchResponse {

    private long totalResults;
    private List<ContentSourceSearchResult.Item> results = new ArrayList<>();
    private boolean hasMore;
    private Map<String, String> sourceCursors = new LinkedHashMap<>();

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public List<ContentSourceSearchResult.Item> getResults() {
        return results;
    }

    public void setResults(List<ContentSourceSearchResult.Item> results) {
        this.results = results;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public Map<String, String> getSourceCursors() {
        return sourceCursors;
    }

    public void setSourceCursors(Map<String, String> sourceCursors) {
        this.sourceCursors = sourceCursors;
    }
}
