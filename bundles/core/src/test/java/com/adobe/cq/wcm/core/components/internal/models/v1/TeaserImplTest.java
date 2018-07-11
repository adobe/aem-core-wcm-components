/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TeaserImplTest {

    private static final String TEST_BASE = "/teaser";
    private static final String CONTENT_ROOT = "/content";
    private static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    private static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/teasers";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TITLE = "Teaser";
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
    private Logger teaserLogger;

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content/dam/core/images");
        AEM_CONTEXT.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Before
    public void setTestFixture() throws NoSuchFieldException, IllegalAccessException {
        teaserLogger = spy(LoggerFactory.getLogger("FakeLogger"));
        Field field = TeaserImpl.class.getDeclaredField("LOGGER");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        field.setAccessible(true);
        // remove final modifier from field

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, teaserLogger);
    }

    @Test
    public void testFullyConfiguredTeaser() {
        Teaser teaser = getTeaserUnderTest(TEASER_1);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(teaser.getImageResource().adaptTo(ValueMap.class));
        }
        assertEquals(TEASER_1, teaser.getImageResource().getPath());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals(LINK, teaser.getLinkURL());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser1"));
    }

    @Test
    public void testFullyConfiguredTeaserVanityPath() {
        Teaser teaser = getTeaserUnderTest(TEASER_5);
        if (teaser.getImageResource() != null) {
            // let's verify the ValueMap wrapping here
            testImageResourceValueMap(teaser.getImageResource().getValueMap());
            testImageResourceValueMap(teaser.getImageResource().adaptTo(ValueMap.class));
        }
        assertEquals(TEASER_5, teaser.getImageResource().getPath());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals(CONTEXT_PATH + "/content/teasers.html", teaser.getLinkURL());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser5"));
    }

    @Test
    public void testPageInheritedProperties() {
        Teaser teaser = getTeaserUnderTest(TEASER_6);
        assertEquals("Teasers Test", teaser.getTitle());
        assertEquals("Teasers description", teaser.getDescription());
    }

    @Test
    public void testInvalidFileReference() throws Exception {
        Teaser teaser = getTeaserUnderTest(TEASER_2);
        verify(teaserLogger)
                .error("Asset /content/dam/core/images/Adobe_Systems_logo_and_wordmark configured for the teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-2 doesn't exist.");
        assertNull(teaser.getImageResource());
    }

    @Test
    public void testEmptyFileReference() throws Exception {
        Teaser teaser = getTeaserUnderTest(TEASER_3);
        verify(teaserLogger)
                .debug("Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-3 does not have an asset or an image file configured.");
        assertNull(teaser.getImageResource());
    }

    @Test
    public void testTeaserWithoutLink() throws Exception {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        verify(teaserLogger).debug("Teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-4 does not define a link.");
        assertNull(teaser.getLinkURL());
    }

    @Test
    public void testTeaserWithHiddenLinks() throws Exception {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Teaser.PN_TITLE_LINK_HIDDEN, true);
            put(Teaser.PN_IMAGE_LINK_HIDDEN, true);
        }}));
        Teaser teaser = getTeaserUnderTest(TEASER_5, mockStyle);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser7"));
    }

    @Test
    public void testTeaserWithHiddenElements() throws Exception {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Teaser.PN_TITLE_HIDDEN, true);
            put(Teaser.PN_DESCRIPTION_HIDDEN, true);
        }}));

        Teaser teaser = getTeaserUnderTest(TEASER_5, mockStyle);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser8"));
    }

    @Test
    public void testTeaserWithActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_7);
        assertTrue("Expected teaser with actions", teaser.isActionsEnabled());
        assertEquals("Expected to find two actions", 2, teaser.getActions().size());
        ListItem action = teaser.getActions().get(0);
        assertEquals("Action link does not match", "http://www.adobe.com", action.getPath());
        assertEquals("Action text does not match", "Adobe", action.getTitle());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser9"));
    }

    @Test
    public void testTeaserWithActionsDisabled() {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Teaser.PN_ACTIONS_DISABLED, true);
        }}));

        Teaser teaser = getTeaserUnderTest(TEASER_7, mockStyle);
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser10"));
    }

    @Test
    public void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser = getTeaserUnderTest(TEASER_8);
        assertTrue("Expected teaser with actions", teaser.isActionsEnabled());
        assertEquals("Expected to find two Actions", 2, teaser.getActions().size());
        assertEquals("Teasers Test", teaser.getTitle());
        assertEquals("Teasers description", teaser.getDescription());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser11"));
    }

    @Test
    public void testTeaserWithTitleType() throws Exception {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Teaser.PN_TITLE_TYPE, "h5");
        }}));

        Teaser teaser = getTeaserUnderTest(TEASER_1, mockStyle);
        assertEquals("Expected title type is not correct", "h5", teaser.getTitleType());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser2"));

    }

    @Test
    public void testTeaserWithDefaultTitleType() throws Exception {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource);

        Teaser teaser = getTeaserUnderTest(TEASER_1, mockStyle);
        assertEquals("Expected the default title type is not correct", null, teaser.getTitleType());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser1"));
    }

    private Teaser getTeaserUnderTest(String resourcePath) {
        return getTeaserUnderTest(resourcePath, null);
    }

    private Teaser getTeaserUnderTest(String resourcePath, Style currentStyle) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        if (currentStyle != null) {
            slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        }
        Component component = mock(Component.class);
        when(component.getProperties()).thenReturn(new ValueMapDecorator(new HashMap<String, Object>() {{
            put(AbstractImageDelegatingModel.IMAGE_DELEGATE, "core/wcm/components/image/v2/image");
        }}));
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Teaser.class);
    }

    private void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
    }
}
