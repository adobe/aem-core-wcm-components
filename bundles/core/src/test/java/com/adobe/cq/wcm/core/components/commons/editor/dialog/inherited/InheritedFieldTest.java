/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.commons.editor.dialog.inherited;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(AemContextExtension.class)
class InheritedFieldTest {

    private static final String TEST_BASE = "/commons/inherited";
    private static final String TEST_PAGE = "/content/test";
    private static final String TEST_APPS = "/apps/dialog";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() throws Exception {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
        context.load().json(TEST_BASE + "/test-apps.json", TEST_APPS);
    }

    @Test
    void testOverridePropWithSuffix() {
        context.currentResource(TEST_APPS + "/brandSlug");
        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("/content/test/parent/child");
        InheritedField inheritedField = request.adaptTo(InheritedField.class);
        if (inheritedField != null) {
            assertEquals("Test Slug", inheritedField.getInheritedValue());
            assertEquals("Brand Slug", inheritedField.getHeading());
            assertNull(inheritedField.getSpecifiedValue());
            assertEquals("brandSlug", inheritedField.getProp());
            assertEquals("cssClass", inheritedField.getAttrClass());
            assertFalse(inheritedField.isOverride());
        } else {
            fail("can't create inherited field model");
        }
    }

    @Test
    void testOverridePropWithParam() {
        context.currentResource(TEST_APPS + "/brandSlug");
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", "/content/test/parent/child"));
        InheritedField inheritedField = request.adaptTo(InheritedField.class);
        if (inheritedField != null) {
            assertEquals("Test Slug", inheritedField.getInheritedValue());
            assertEquals("Brand Slug", inheritedField.getHeading());
            assertNull(inheritedField.getSpecifiedValue());
            assertEquals("brandSlug", inheritedField.getProp());
            assertFalse(inheritedField.isOverride());
        } else {
            fail("can't create inherited field model");
        }
    }

    @Test
    void testMissingPathError() {
        context.currentResource(TEST_APPS + "/brandSlug");
        MockSlingHttpServletRequest request = context.request();

        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger appLogger = (Logger) LoggerFactory.getLogger(InheritedField.class);
        appLogger.addAppender(appender);
        request.adaptTo(InheritedField.class);
        assertEquals(1, appender.list.stream()
            .filter(entry -> Level.ERROR.equals(entry.getLevel()))
            .filter(entry -> "Suffix and 'item' param are blank".equals(entry.getFormattedMessage()))
            .count()
        );
    }

    @Test
    void testMissingPropError() {
        context.currentResource(TEST_APPS + "/missingProp");
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", "/content/test/parent/child"));
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        Logger appLogger = (Logger) LoggerFactory.getLogger(InheritedField.class);
        appLogger.addAppender(appender);
        request.adaptTo(InheritedField.class);
        assertEquals(1, appender.list.stream()
            .filter(entry -> Level.ERROR.equals(entry.getLevel()))
            .filter(entry -> "'prop' value is null".equals(entry.getFormattedMessage()))
            .count()
        );
    }
}
