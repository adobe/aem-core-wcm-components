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

import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Slf4jTestCleanupExtension;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;

@ExtendWith({AemContextExtension.class, Slf4jTestCleanupExtension.class})
class InheritedFieldTest {

    private static final String TEST_BASE = "/commons/inherited";
    private static final String TEST_PAGE = "/content/test";
    private static final String TEST_APPS = "/apps/dialog";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private TestLogger testLogger;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
        context.load().json(TEST_BASE + "/test-apps.json", TEST_APPS);
        testLogger = TestLoggerFactory.getTestLogger(InheritedField.class);
        testLogger.setEnabledLevels(Level.ERROR);
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
        request.adaptTo(InheritedField.class);
        MatcherAssert.assertThat(testLogger.getLoggingEvents(), hasItem(error("Suffix and 'item' param are blank")));
    }

    @Test
    void testMissingPropError() {
        context.currentResource(TEST_APPS + "/missingProp");
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("item", "/content/test/parent/child"));
        request.adaptTo(InheritedField.class);
        MatcherAssert.assertThat(testLogger.getLoggingEvents(), hasItem(error("'prop' value is null")));
    }
}
