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

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentSourceSearchAggregatorTest {

    @Test
    void fetchAll_followsCursorUntilAllResultsLoaded() throws Exception {
        ContentAIClient client = mock(ContentAIClient.class);
        ContentSourceSearchResult firstPage = page(67, item("doc_1"), item("doc_2"));
        firstPage.setCursor("page-2");
        ContentSourceSearchResult secondPage = page(67, item("doc_3"));

        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(50), isNull())).thenReturn(firstPage);
        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(50), eq("page-2"))).thenReturn(secondPage);

        ContentSourceSearchResult result = ContentSourceSearchAggregator.fetchAll(client, "my-source", "ACQUISITION", "block");

        assertEquals(67, result.getTotalResults());
        assertEquals(3, result.getResults().size());
        verify(client).search("my-source", "ACQUISITION", "block", 50, "page-2");
    }

    @Test
    void fetchAll_returnsSinglePageWhenNoCursor() throws Exception {
        ContentAIClient client = mock(ContentAIClient.class);
        ContentSourceSearchResult page = page(10, item("doc_1"));
        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(50), isNull())).thenReturn(page);

        ContentSourceSearchResult result = ContentSourceSearchAggregator.fetchAll(client, "my-source", "ACQUISITION", "block");

        assertEquals(10, result.getTotalResults());
        assertEquals(1, result.getResults().size());
    }

    private static ContentSourceSearchResult page(long totalResults, ContentSourceSearchResult.Item... items) {
        ContentSourceSearchResult result = new ContentSourceSearchResult();
        result.setTotalResults(totalResults);
        result.setResults(Arrays.asList(items));
        return result;
    }

    private static ContentSourceSearchResult.Item item(String id) {
        ContentSourceSearchResult.Item item = new ContentSourceSearchResult.Item();
        item.setId(id);
        item.setScore(1.0);
        return item;
    }
}
