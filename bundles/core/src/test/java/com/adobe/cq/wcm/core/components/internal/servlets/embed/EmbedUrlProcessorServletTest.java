/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.services.embed.PinterestUrlProcessor;
import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import com.adobe.cq.wcm.core.components.testing.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class EmbedUrlProcessorServletTest {

    private static final String TEST_BASE = "/embed";
    private static final String CONTENT_ROOT = "/content";

    private final EmbedUrlProcessorServlet servlet = new EmbedUrlProcessorServlet();

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        UrlProcessor pinterestUrlProcessor = new PinterestUrlProcessor();
        List<UrlProcessor> urlProcessors = new ArrayList<>();
        urlProcessors.add(pinterestUrlProcessor);
        Utils.setInternalState(servlet, "urlProcessors", urlProcessors);
    }

    @Test
    public void testWithoutUrlParameter() throws Exception {
        servlet.doGet(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, context.response().getStatus(), "Expected the 404 status code.");
        assertEquals("", context.response().getOutputAsString(), "Expected empty response output.");
    }

    @Test
    public void testUrlWithoutRegisteredProvider() throws Exception {
        context.request().setQueryString("url=http://www.no-registered-provider.com");
        servlet.doGet(context.request(), context.response());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, context.response().getStatus(), "Expected the 404 status code.");
        assertEquals("", context.response().getOutputAsString(), "Expected empty response output.");
    }

    @Test
    public void testUrlWithRegisteredProvider() throws Exception {
        context.request().setQueryString("url=https://www.pinterest.com/pin/99360735500167749/");
        servlet.doGet(context.request(), context.response());
        String expectedOutput = "{\"processor\":\"pinterest\",\"options\":{\"pinId\":\"99360735500167749\"}}";
        assertEquals(HttpServletResponse.SC_OK, context.response().getStatus(), "Expected the 200 status code.");
        assertEquals("application/json;charset=utf-8", context.response().getContentType(), "Expected the JSON content type.");
        assertEquals(expectedOutput, context.response().getOutputAsString(), "Does not match the expected response output.");
    }
}
