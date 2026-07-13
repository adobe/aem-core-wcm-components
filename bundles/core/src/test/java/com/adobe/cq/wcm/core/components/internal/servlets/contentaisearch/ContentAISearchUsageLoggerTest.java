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

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ContentAISearchUsageLoggerTest {

    private static final String TEST_BASE = "/contentaisearchservlet";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/par/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
        context.registerInjectActivateService(new MockProductInfoProvider());
        appender = new ListAppender<>();
        appender.start();
        Logger logger = (Logger) LoggerFactory.getLogger(ContentAISearchUsageLogger.class);
        logger.setLevel(Level.INFO);
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(ContentAISearchUsageLogger.class);
        logger.detachAppender(appender);
    }

    @Test
    void servletRequestEmitsUsageLog() throws Exception {
        context.currentResource(COMPONENT_PATH);
        com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch model =
            context.request().adaptTo(com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch.class);

        ContentAISearchUsageLogger.logUsage("search", context.request(), model);

        assertEquals(1, appender.list.size());
        String message = appender.list.get(0).getFormattedMessage();
        assertTrue(message.contains("operation=search"));
        assertTrue(message.contains("resourcePath=" + COMPONENT_PATH));
        assertTrue(message.contains("contentSource=my-source"));
        assertTrue(message.contains("contentSources=[my-source]"));
        assertTrue(!message.contains("electric cars"), "visitor query must not be logged");
    }

    @Test
    void resolvesCustomerIdentifiersFromProgramAndEnvironmentIds() {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_PROGRAM_ID", "12345");
        env.put("AEM_ENV_ID", "67890");
        ContentAISearchUsageLogger.UsageContext usageContext = envContext(env);

        assertEquals("12345", usageContext.resolveProgramId());
        assertEquals("67890", usageContext.resolveEnvironmentId());
        assertEquals("p12345-e67890", usageContext.resolveCustomerBucket("12345", "67890"));
    }

    @Test
    void resolvesCustomerIdentifiersFromAemServiceFallback() {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_SERVICE", "cm-p99999-e11111");
        ContentAISearchUsageLogger.UsageContext usageContext = envContext(env);

        assertEquals("99999", usageContext.resolveProgramId());
        assertEquals("11111", usageContext.resolveEnvironmentId());
        assertEquals("p99999-e11111", usageContext.resolveCustomerBucket("99999", "11111"));
    }

    private static ContentAISearchUsageLogger.UsageContext envContext(Map<String, String> env) {
        return new ContentAISearchUsageLogger.UsageContext() {
            @Override
            protected String getEnv(String name) {
                return env.get(name);
            }
        };
    }
}
