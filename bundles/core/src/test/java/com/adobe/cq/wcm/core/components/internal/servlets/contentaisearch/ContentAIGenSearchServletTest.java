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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceQueryResult;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentAIGenSearchServletTest {

    private static final String TEST_BASE = "/contentaisearchservlet";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/par/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ContentAIGenSearchServlet underTest;
    private ContentAIClient mockClient;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
        mockClient = mock(ContentAIClient.class);
        context.registerService(ContentAIClient.class, mockClient);
        context.registerInjectActivateService(new MockProductInfoProvider());
        underTest = context.registerInjectActivateService(new ContentAIGenSearchServlet());
    }

    @Test
    void doGetWritesGenSearchResultAsJson() throws Exception {
        ContentSourceQueryResult expected = new ContentSourceQueryResult();
        expected.setResult("Electric cars are efficient.");
        when(mockClient.genSearch(eq("my-source"), eq("ACQUISITION"), eq("electric cars"))).thenReturn(expected);

        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=electric+cars");

        underTest.doGet(context.request(), context.response());

        assertEquals(200, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("Electric cars are efficient."));
    }

    @Test
    void doGetReturns400WhenQueryMissing() throws Exception {
        context.currentResource(COMPONENT_PATH);

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }

    @Test
    void doGetReturns502WhenContentAiFails() throws Exception {
        when(mockClient.genSearch(eq("my-source"), eq("ACQUISITION"), eq("electric cars")))
            .thenThrow(new ContentAIClientException("failed", 502));

        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=electric+cars");

        underTest.doGet(context.request(), context.response());

        assertEquals(502, context.response().getStatus());
    }

    @Test
    void doGetReturns400WhenQueryExceedsMaxLength() throws Exception {
        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=" + StringUtils.repeat("a", 513));

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }
}
