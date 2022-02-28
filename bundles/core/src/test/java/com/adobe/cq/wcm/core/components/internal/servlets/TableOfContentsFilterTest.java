/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.loader.ContentLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class TableOfContentsFilterTest {

    private static final String TEST_BASE = "/tableofcontents";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private TableOfContentsFilter tableOfContentsFilter;

    @BeforeEach
    void setUp() throws Exception {
        tableOfContentsFilter = new TableOfContentsFilter();
    }

    /**
     * Covers all the include and ignore scenarios mentioned
     * <a href="https://github.com/adobe/aem-core-wcm-components/wiki/Table-of-Content-Component#include--ignore-behavior">here</a>
     * @throws Exception
     */
    @Test
    void testIncludeIgnoreBehavior() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-include-ignore-content.html",
            TEST_BASE + "/exporter-include-ignore-content.html"
        );
    }

    /**
     * Covers all the nesting behavior examples mentioned
     * <a href="https://github.com/adobe/aem-core-wcm-components/wiki/Table-of-Content-Component#nesting-behavior">here</a>
     * @throws Exception
     */
    @Test
    void testNestingBehavior() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-nesting-content.html",
            TEST_BASE + "/exporter-nesting-content.html"
        );
    }

    private void checkFilterResponse(String htmlContentPagePath, String expectedHtmlContentPagePath)
        throws Exception {

        MockSlingHttpServletRequest request = context.request();
        request.setAttribute("contains-table-of-contents", true);

        HttpServletResponseWrapper response = Mockito.mock(HttpServletResponseWrapper.class);
        Mockito.when(response.getContentType())
            .thenReturn("text/html");
        StringWriter responseWriter = new StringWriter();
        Mockito.when(response.getWriter())
            .thenReturn(new PrintWriter(responseWriter));

        FilterChain filterChain = (servletRequest, servletResponse) -> {
            String testHtmlContent = IOUtils.toString(
                ContentLoader.class.getResourceAsStream(htmlContentPagePath),
                StandardCharsets.UTF_8
            );
            servletResponse.getWriter()
                .write(testHtmlContent);
            servletResponse.getWriter().flush();
        };

        tableOfContentsFilter.doFilter(request, response, filterChain);
        String expectedContent = IOUtils.toString(
            ContentLoader.class.getResourceAsStream(expectedHtmlContentPagePath),
            StandardCharsets.UTF_8
        );
        assertEquals(
            expectedContent,
            responseWriter.toString()
        );
    }
}
