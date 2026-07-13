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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentSourceSearchMergerTest {

    @Test
    void merge_dedupesByIdKeepsHighestScore() {
        ContentSourceSearchResult first = result(item("doc_1", 0.5), item("doc_2", 0.9));
        ContentSourceSearchResult second = result(item("doc_1", 0.8), item("doc_3", 0.7));

        ContentSourceSearchResult merged = ContentSourceSearchMerger.merge(Arrays.asList(first, second), 10);

        assertEquals(3, merged.getTotalResults());
        assertEquals(3, merged.getResults().size());
        assertEquals("doc_2", merged.getResults().get(0).getId());
        assertEquals("doc_1", merged.getResults().get(1).getId());
        assertEquals(0.8, merged.getResults().get(1).getScore(), 0.001);
        assertNull(merged.getCursor());
    }

    @Test
    void merge_respectsLimit() {
        ContentSourceSearchResult partial = result(
            item("a", 0.9), item("b", 0.8), item("c", 0.7));

        ContentSourceSearchResult merged = ContentSourceSearchMerger.merge(Collections.singletonList(partial), 2);

        assertEquals(2, merged.getResults().size());
        assertEquals(2, merged.getTotalResults());
        assertEquals("a", merged.getResults().get(0).getId());
        assertEquals("b", merged.getResults().get(1).getId());
    }

    private static ContentSourceSearchResult result(ContentSourceSearchResult.Item... items) {
        ContentSourceSearchResult result = new ContentSourceSearchResult();
        result.setResults(Arrays.asList(items));
        result.setTotalResults(items.length);
        return result;
    }

    private static ContentSourceSearchResult.Item item(String id, double score) {
        ContentSourceSearchResult.Item item = new ContentSourceSearchResult.Item();
        item.setId(id);
        item.setScore(score);
        item.setData(new HashMap<String, Object>());
        return item;
    }
}
