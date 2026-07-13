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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

/**
 * Merges partial search results from multiple content sources into a single ranked list.
 */
public final class ContentSourceSearchMerger {

    private ContentSourceSearchMerger() {
    }

    /**
     * @param partials per-source search responses
     * @param limit    maximum number of merged results to return
     * @return merged result with deduplicated items, highest score wins, sorted descending
     */
    @NotNull
    public static ContentSourceSearchResult merge(@NotNull List<ContentSourceSearchResult> partials, int limit) {
        Map<String, ContentSourceSearchResult.Item> byId = new LinkedHashMap<>();
        for (ContentSourceSearchResult partial : partials) {
            if (partial == null || partial.getResults() == null) {
                continue;
            }
            for (ContentSourceSearchResult.Item item : partial.getResults()) {
                if (item == null || StringUtils.isBlank(item.getId())) {
                    continue;
                }
                ContentSourceSearchResult.Item existing = byId.get(item.getId());
                if (existing == null || item.getScore() > existing.getScore()) {
                    byId.put(item.getId(), item);
                }
            }
        }

        List<ContentSourceSearchResult.Item> merged = new ArrayList<>(byId.values());
        merged.sort(Comparator.comparingDouble(ContentSourceSearchResult.Item::getScore).reversed());

        int effectiveLimit = limit > 0 ? limit : merged.size();
        if (merged.size() > effectiveLimit) {
            merged = merged.subList(0, effectiveLimit);
        }

        ContentSourceSearchResult result = new ContentSourceSearchResult();
        result.setResults(merged);
        result.setTotalResults(merged.size());
        result.setCursor(null);
        return result;
    }
}
