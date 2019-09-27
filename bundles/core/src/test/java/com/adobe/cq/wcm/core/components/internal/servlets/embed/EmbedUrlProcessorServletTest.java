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

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.services.embed.PinterestUrlProcessor;
import com.adobe.cq.wcm.core.components.internal.servlets.embed.EmbedUrlProcessorServlet;
import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;
import com.adobe.cq.wcm.core.components.testing.Utils;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EmbedUrlProcessorServletTest {

    private static final String TEST_BASE = "/embed";

    private EmbedUrlProcessorServlet servlet;

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    @Before
    public void setUp() {
        servlet = new EmbedUrlProcessorServlet();
        UrlProcessor pinterestUrlProcessor = new PinterestUrlProcessor();
        List<UrlProcessor> urlProcessors = new ArrayList<>();
        urlProcessors.add(pinterestUrlProcessor);
        Utils.setInternalState(servlet, "urlProcessors", urlProcessors);
    }

    @After
    public void tearDown() {
        servlet = null;
    }

    @Test
    public void testWithoutUrlParameter() throws Exception {
        servlet.doGet(context.request(), context.response());
        MockSlingHttpServletResponse response = context.response();
        assertEquals("Expected the 404 status code.", HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("Expected empty response output.", "", response.getOutputAsString());
    }

    @Test
    public void testUrlWithoutRegisteredProvider() throws Exception {
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();
        request.setQueryString("url=http://www.no-registered-provider.com");
        servlet.doGet(request, context.response());
        assertEquals("Expected the 404 status code.", HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertEquals("Expected empty response output.", "", response.getOutputAsString());
    }

    @Test
    public void testUrlWithRegisteredProvider() throws Exception {
        MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();
        request.setQueryString("url=https://www.pinterest.com/pin/99360735500167749/");
        servlet.doGet(request, response);
        String expectedOutput = "{\"processor\":\"pinterest\",\"options\":{\"pinId\":\"99360735500167749\"}}";
        assertEquals("Expected the 200 status code.", HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("Expected the JSON content type.", "application/json;charset=utf-8", response.getContentType());
        assertEquals("Does not match the expected response output.", expectedOutput, response.getOutputAsString());
    }
}
