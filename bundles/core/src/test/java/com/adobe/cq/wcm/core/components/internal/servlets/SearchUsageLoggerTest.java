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
package com.adobe.cq.wcm.core.components.internal.servlets;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Search;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class SearchUsageLoggerTest {

    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/par/search";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        context.create().resource(COMPONENT_PATH);
        appender = new ListAppender<>();
        appender.start();
        Logger usageLogger = (Logger) LoggerFactory.getLogger(SearchUsageLogger.class);
        usageLogger.setLevel(Level.INFO);
        usageLogger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        Logger usageLogger = (Logger) LoggerFactory.getLogger(SearchUsageLogger.class);
        usageLogger.detachAppender(appender);
    }

    @Test
    void logsUsageWithResourcePathAndResultCount() {
        MockSlingHttpServletRequest request = context.request();
        context.currentResource(COMPONENT_PATH);
        Search model = mock(Search.class);
        when(model.getResultsSize()).thenReturn(10);

        SearchUsageLogger.logUsage(request, model, 4);

        assertEquals(1, appender.list.size());
        String message = appender.list.get(0).getFormattedMessage();
        assertTrue(message.contains("resourcePath=" + COMPONENT_PATH));
        assertTrue(message.contains("configuredResultsSize=10"));
        assertTrue(message.contains("resultCount=4"));
    }

    @Test
    void neverLogsTheSearchQueryString() {
        MockSlingHttpServletRequest request = context.request();
        context.currentResource(COMPONENT_PATH);
        request.setQueryString("fulltext=my-secret-health-condition");
        Search model = mock(Search.class);
        when(model.getResultsSize()).thenReturn(10);

        SearchUsageLogger.logUsage(request, model, 0);

        String message = appender.list.get(0).getFormattedMessage();
        assertTrue(!message.contains("my-secret-health-condition"), "visitor query must not be logged");
    }

    @Test
    void logsErrorWithResourcePathAndReason() {
        MockSlingHttpServletRequest request = context.request();
        context.currentResource(COMPONENT_PATH);
        Logger errorLogger = (Logger) LoggerFactory.getLogger(SearchUsageLogger.class);
        errorLogger.setLevel(Level.ERROR);

        SearchUsageLogger.logError(request, "invalid_results_offset", new NumberFormatException("3x"));

        assertEquals(1, appender.list.size());
        ILoggingEvent event = appender.list.get(0);
        assertEquals(Level.ERROR, event.getLevel());
        assertTrue(event.getFormattedMessage().contains("resourcePath=" + COMPONENT_PATH));
        assertTrue(event.getFormattedMessage().contains("reason=invalid_results_offset"));
    }

}
