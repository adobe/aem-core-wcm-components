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
    void fetchPage_delegatesToClientWithCappedLimit() throws Exception {
        ContentAIClient client = mock(ContentAIClient.class);
        ContentSourceSearchResult page = new ContentSourceSearchResult();
        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(10), isNull())).thenReturn(page);

        ContentSourceSearchResult result = ContentSourceSearchAggregator.fetchPage(
            client, "my-source", "ACQUISITION", "block", 10, null);

        assertEquals(page, result);
        verify(client).search("my-source", "ACQUISITION", "block", 10, null);
    }

    @Test
    void fetchPage_capsLimitAtApiPageSize() throws Exception {
        ContentAIClient client = mock(ContentAIClient.class);
        ContentSourceSearchResult page = new ContentSourceSearchResult();
        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(50), isNull())).thenReturn(page);

        ContentSourceSearchAggregator.fetchPage(client, "my-source", "ACQUISITION", "block", 200, null);

        verify(client).search("my-source", "ACQUISITION", "block", 50, null);
    }

    @Test
    void fetchPage_passesCursorToClient() throws Exception {
        ContentAIClient client = mock(ContentAIClient.class);
        ContentSourceSearchResult page = new ContentSourceSearchResult();
        when(client.search(eq("my-source"), eq("ACQUISITION"), eq("block"), eq(10), eq("page-2"))).thenReturn(page);

        ContentSourceSearchAggregator.fetchPage(client, "my-source", "ACQUISITION", "block", 10, "page-2");

        verify(client).search("my-source", "ACQUISITION", "block", 10, "page-2");
    }
}
