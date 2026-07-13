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

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentAISearchResultsServletTest {

    private static final String TEST_BASE = "/contentaisearchservlet";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/par/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ContentAISearchResultsServlet underTest;
    private ContentAIClient mockClient;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
        mockClient = mock(ContentAIClient.class);
        context.registerService(ContentAIClient.class, mockClient);
        underTest = context.registerInjectActivateService(new ContentAISearchResultsServlet());
    }

    @Test
    void doGetWritesSearchResultsAsJson() throws Exception {
        ContentSourceSearchResult expected = new ContentSourceSearchResult();
        ContentSourceSearchResult.Item item = new ContentSourceSearchResult.Item();
        item.setId("doc_1");
        item.setScore(0.75);
        expected.setResults(Collections.singletonList(item));
        when(mockClient.search(eq("my-source"), eq("ACQUISITION"), eq("electric cars"), anyInt())).thenReturn(expected);

        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=electric+cars");

        underTest.doGet(context.request(), context.response());

        assertEquals(200, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("\"id\":\"doc_1\""));
    }

    @Test
    void doGetReturns400WhenQueryMissing() throws Exception {
        context.currentResource(COMPONENT_PATH);

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }

    @Test
    void doGetReturns400WhenQueryExceedsMaxLength() throws Exception {
        context.currentResource(COMPONENT_PATH);
        String longQuery = StringUtils.repeat("a", 513);
        context.request().setQueryString("q=" + longQuery);

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
        verify(mockClient, never()).search(anyString(), anyString(), anyString(), anyInt());
    }
}
