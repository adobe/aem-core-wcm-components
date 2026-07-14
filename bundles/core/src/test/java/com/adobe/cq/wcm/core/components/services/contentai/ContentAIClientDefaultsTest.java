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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContentAIClientDefaultsTest {

    private final ContentAIClient client = new ContentAIClient() {
        @Override
        public ContentSourceListResult listContentSources() {
            return new ContentSourceListResult();
        }

        @Override
        public ContentSourceSearchResult search(String contentSource, String contentSourceType, String query,
            int limit) {
            return search(contentSource, contentSourceType, query, limit, null);
        }

        @Override
        public ContentSourceSearchResult search(String contentSource, String contentSourceType, String query,
            int limit, String cursor) {
            ContentSourceSearchResult result = new ContentSourceSearchResult();
            result.setCursor(contentSourceType + ":" + cursor);
            result.setTotalResults(limit);
            return result;
        }

        @Override
        public ContentSourceQueryResult genSearch(String contentSource, String contentSourceType, String query) {
            ContentSourceQueryResult result = new ContentSourceQueryResult();
            result.setQuery(contentSourceType + ":" + query);
            return result;
        }
    };

    @Test
    void defaultSearchDelegatesToTypedOverload() throws Exception {
        ContentSourceSearchResult result = client.search("source-a", "query", 10);
        assertEquals("ACQUISITION:null", result.getCursor());
        assertEquals(10, result.getTotalResults());
    }

    @Test
    void defaultSearchWithCursorDelegates() throws Exception {
        ContentSourceSearchResult result = client.search("source-a", "query", 5, "cursor-1");
        assertEquals("ACQUISITION:cursor-1", result.getCursor());
    }

    @Test
    void defaultGenSearchDelegates() throws Exception {
        ContentSourceQueryResult result = client.genSearch("source-a", "query");
        assertEquals("ACQUISITION:query", result.getQuery());
    }

    @Test
    void defaultContentSourceTypeConstant() {
        assertEquals("ACQUISITION", ContentAIClient.DEFAULT_CONTENT_SOURCE_TYPE);
    }
}
