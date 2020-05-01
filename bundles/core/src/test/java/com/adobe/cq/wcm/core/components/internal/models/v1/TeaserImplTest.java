/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;


@ExtendWith(AemContextExtension.class)
class TeaserImplTest {

    private static final String TEST_BASE = "/teaser";
    private static final String CONTENT_ROOT = "/content";
    private static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    private static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/teasers";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TITLE = "Teaser";
    private static final String PRETITLE = "Teaser's Pretitle";
    private static final String DESCRIPTION = "Description";
    private static final String LINK = "https://www.adobe.com";
    private static final String TEASER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-1";
    private static final String TEASER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-2";
    private static final String TEASER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-3";
    private static final String TEASER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-4";
    private static final String TEASER_5 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-5";
    private static final String TEASER_6 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-6";
    private static final String TEASER_7 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-7";
    private static final String TEASER_8 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-8";
    private static final String TEASER_9 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-9";
    private static final String TEASER_10 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-10";
    private static final String TEASER_11 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-11";
    private static final String TEASER_12 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-12";

    private final AemContext context = CoreComponentTestContext.newAemContext();
    private TestLogger testLogger;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_DAM_JSON, "/content/dam/core/images");
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
        testLogger = TestLoggerFactory.getTestLogger(TeaserImpl.class);
    }

    @AfterEach
    void tearDown() {
        TestLoggerFactory.clear();
    }

    @Test
    void testFullyConfiguredTeaser() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(Objects.requireNonNull(teaser.getImageResource().adaptTo(ValueMap.class)));
            assertEquals(TEASER_1, teaser.getImageResource().getPath());
        }
        assertEquals(PRETITLE, teaser.getPretitle());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals(LINK, teaser.getLinkURL());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser1"));
    }

    @Test
    void testFullyConfiguredTeaserVanityPath() {
        Teaser teaser = getTeaserUnderTest(TEASER_5);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(Objects.requireNonNull(teaser.getImageResource().adaptTo(ValueMap.class)));
            assertEquals(TEASER_5, teaser.getImageResource().getPath());
        }
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals(CONTEXT_PATH + "/content/teasers.html", teaser.getLinkURL());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser5"));
    }

    @Test
    void testPageInheritedProperties() {
        Teaser teaser = getTeaserUnderTest(TEASER_6);
        assertEquals("Teasers Test", teaser.getTitle());
        assertEquals("Teasers description", teaser.getDescription());
    }

    @Test
    void testInvalidFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_2);
        assertThat(testLogger.getLoggingEvents(), hasItem(error(
            "Asset /content/dam/core/images/Adobe_Systems_logo_and_wordmark configured for the teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-2 doesn't exist.")));
        assertNull(teaser.getImageResource());
    }

    @Test
    void testEmptyFileReference() {
        Teaser teaser = getTeaserUnderTest(TEASER_3);
        assertThat(testLogger.getLoggingEvents(), hasItem(debug(
            "Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-3 does not have an asset or an image file configured.")));
        assertNull(teaser.getImageResource());
    }

    @Test
    void testTeaserWithoutLink() {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        assertThat(testLogger.getLoggingEvents(),
            hasItem(debug("Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-4 does not define a link.")));
        assertNull(teaser.getLinkURL());
    }

    @Test
    void testTeaserWithHiddenLinks() {
        Teaser teaser = getTeaserUnderTest(TEASER_5,
            Teaser.PN_TITLE_LINK_HIDDEN, true,
            Teaser.PN_IMAGE_LINK_HIDDEN, true);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser7"));
    }

    @Test
    void testTeaserWithHiddenElements() {
        Teaser teaser = getTeaserUnderTest(TEASER_5,
            Teaser.PN_TITLE_HIDDEN, true,
            Teaser.PN_DESCRIPTION_HIDDEN, true,
            Teaser.PN_PRETITLE_HIDDEN, true);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser8"));
    }

    @Test
    void testTeaserWithoutImage() {
        Teaser teaser = getTeaserUnderTest(TEASER_9);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser12"));
    }

    @Test
    void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue("Expected teaser with actions", teaser.isActionsEnabled());
        assertEquals("Expected to find two actions", 2, teaser.getActions().size());
        ListItem action = teaser.getActions().get(0);
        assertEquals("Action link does not match", "http://www.adobe.com", action.getPath());
        assertEquals("Action text does not match", "Adobe", action.getTitle());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser9"));
    }

    @Test
    void testTeaserWithActionsDisabled() {
        Teaser teaser = getTeaserUnderTest(TEASER_7,
            Teaser.PN_ACTIONS_DISABLED, true);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser10"));
    }

    @Test
    void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue("Expected teaser with actions", teaser.isActionsEnabled());
        assertEquals("Expected to find two Actions", 2, teaser.getActions().size());
        assertEquals("Teasers Test", teaser.getTitle());
        assertEquals("Teasers description", teaser.getDescription());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser11"));
    }

    @Test
    void testTeaserWithTitleType() {
        Teaser teaser = getTeaserUnderTest(TEASER_1,
            Teaser.PN_TITLE_TYPE, "h5");
        assertEquals("Expected title type is not correct", "h5", teaser.getTitleType());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser2"));
    }

    @Test
    void testTeaserWithDefaultTitleType() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        assertNull("Expected the default title type is not correct", teaser.getTitleType());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser1"));
    }

    @Test
    void testTeaserWithTitleNotFromLinkedPageAndNoActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_10);
        assertEquals("Teaser", teaser.getTitle());
        assertTrue(teaser.getActions().isEmpty());
    }

    @Test
    void testTeaserWithTitleNotFromLinkedPageAndActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_11);
        assertEquals("Teaser", teaser.getTitle());
        List<ListItem> actions = teaser.getActions();
        assertEquals("http://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
        assertEquals("/content/teasers", actions.get(1).getPath());
        assertEquals("Teasers", actions.get(1).getTitle());
    }

    @Test
    void testTeaserWithTitleFromLinkedPageAndActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_12);
        assertEquals("Adobe", teaser.getTitle());
        List<ListItem> actions = teaser.getActions();
        assertEquals("http://www.adobe.com", actions.get(0).getPath());
        assertEquals("Adobe", actions.get(0).getTitle());
    }

    private Teaser getTeaserUnderTest(String resourcePath, Object... properties) {
        Utils.enableDataLayer(context, true);
        MockSlingHttpServletRequest request = context.request();
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        Component component = mock(Component.class);
        when(component.getProperties()).thenReturn(new ValueMapDecorator(new HashMap<String, Object>() {{
            put(AbstractImageDelegatingModel.IMAGE_DELEGATE, "core/wcm/components/image/v2/image");
        }}));
        SlingBindings slingBindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(Teaser.class);
    }

    private void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
    }
}
