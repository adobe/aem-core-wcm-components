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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.loader.ContentLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.TableOfContentsImpl;
import com.day.cq.wcm.api.WCMMode;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class TableOfContentsFilterTest {

    private static final String TEST_BASE = "/tableofcontents";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private TableOfContentsFilter tableOfContentsFilter;

    @BeforeEach
    void setUp() throws Exception {
        tableOfContentsFilter = new TableOfContentsFilter();
        TableOfContentsFilter.Config mockConfig = Mockito.mock(TableOfContentsFilter.Config.class);
        Mockito.when(mockConfig.enabled()).thenReturn(true);
        tableOfContentsFilter.activate(mockConfig);
    }

    @Test
    void testConfigNotEnabled() throws ServletException, IOException {
        TableOfContentsFilter.Config mockConfig = Mockito.mock(TableOfContentsFilter.Config.class);
        Mockito.when(mockConfig.enabled()).thenReturn(false);
        tableOfContentsFilter.activate(mockConfig);
        FilterChain filterChain = Mockito.mock(FilterChain.class);
        tableOfContentsFilter.doFilter(context.request(), context.response(), filterChain);
        Mockito.verify(filterChain, Mockito.times(1)).doFilter(context.request(), context.response());
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
            TEST_BASE + "/exporter-include-ignore-content.html",
            true,
            "TOC include and ignore classes behaviour is not correct");
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
            TEST_BASE + "/exporter-nesting-content.html",
            true,
            "TOC Nesting behaviour is not correct");
    }

    /**
     * Checks the filter response in case invalid start and stop levels are passed
     * @throws Exception
     */
    @Test
    void testInvalidStartStopLevels() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-invalid-toc-content.html",
            TEST_BASE + "/exporter-invalid-toc-content.html",
            true,
            "TOC Content should be empty and template placeholder should be present");
    }

    /**
     * Checks the filter response in case TOC flag is not set as a request attribute
     * @throws Exception
     */
    @Test
    void testNoTocFlag() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-nesting-content.html",
            TEST_BASE + "/test-nesting-content.html",
            false,
            "Page's HTML Content should not be modified if there's no TOC in it"
        );
    }

    /**
     * Checks the filter response in case there's no heading element on the page
     * @throws Exception
     */
    @Test
    void testNoHeadingElementOnPage() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-no-heading-content.html",
            TEST_BASE + "/exporter-no-heading-content.html",
            true,
            "TOC Content should be empty and not throw any error if there's no heading in it"
        );
    }

    /**
     * Checks whether TOC template placeholder will exist or be removed depending upon
     * TOC content being empty or not
     * @throws Exception
     */
    @Test
    void testTocTemplatePlaceholderWithWcmEditMode() throws Exception {
        context.request().setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
        checkFilterResponse(
            TEST_BASE + "/test-template-placeholder-content.html",
            TEST_BASE + "/exporter-template-placeholder-content.html",
            true,
            "TOC template placeholder should exist & not exist, " +
                "depending upon TOC content being empty or not"
        );
    }

    /**
     * Checks whether TOC generates unique IDs for h1-h6 elements having exactly same textual content
     * @throws Exception
     */
    @Test
    void testDuplicateIDs() throws Exception {
        checkFilterResponse(
            TEST_BASE + "/test-duplicate-ids-content.html",
            TEST_BASE + "/exporter-duplicate-ids-content.html",
            true,
            "TOC should generate unique IDs for h1-h6 elements having exactly same textual content"
        );
    }

    /**
     * Checks no NPE is thrown if response content type is null
     * @throws Exception
     */
    @Test
    void testNoNpeIfContentTypeIsNull() throws Exception {
        MockSlingHttpServletRequest request = context.request();
        HttpServletResponseWrapper response = Mockito.mock(HttpServletResponseWrapper.class);
        Mockito.when(response.getContentType())
            .thenReturn(null);
        Mockito.when(response.getWriter())
            .thenReturn(new PrintWriter(new StringWriter()));
        FilterChain filterChain = (servletRequest, servletResponse) -> {};
        tableOfContentsFilter.doFilter(request, response, filterChain);
    }

    private void checkFilterResponse(String htmlContentPagePath, String expectedHtmlContentPagePath, boolean setTocFlag,
                                     String errorMessage)
        throws Exception {

        MockSlingHttpServletRequest request = context.request();
        if(setTocFlag) {
            request.setAttribute(TableOfContentsImpl.TOC_REQUEST_ATTR_FLAG, true);
        }

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
            responseWriter.toString(),
            errorMessage
        );
    }
}
