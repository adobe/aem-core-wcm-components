/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.sandbox.models.Teaser;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TeaserImplTest {

    private static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    private static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/teasers";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TITLE = "Teaser";
    private static final String DESCRIPTION = "Description";
    private static final String LINK = "https://www.adobe.com";
    private static final String LINK_TEXT = "Adobe";
    private static final String TEASER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-1";
    private static final String TEASER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-2";
    private static final String TEASER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-3";
    private static final String TEASER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-4";
    private static final String TEASER_5 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-5";
    private Logger teaserLogger;

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext("/teaser", "/content");

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json("/teaser/test-content-dam.json", "/content/dam/core/images");
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
        assertEquals(LINK_TEXT, teaser.getLinkText());
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
        assertEquals(LINK_TEXT, teaser.getLinkText());
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
        verify(teaserLogger).warn("Please provide an asset path for the teaser component from " +
                "/content/teasers/jcr:content/root/responsivegrid/teaser-3.");
        assertNull(teaser.getImageResource());
    }

    @Test
    public void testTeaserWithoutLink() throws Exception {
        Teaser teaser = getTeaserUnderTest(TEASER_4);
        verify(teaserLogger).warn("Please provide a link for the teaser component from /content/teasers/jcr:content/root/responsivegrid/teaser-4.");
        assertNull(teaser.getLinkURL());
    }

    private Teaser getTeaserUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Teaser.class);
    }

    private void testImageResourceValueMap(ValueMap valueMap) {
        assertFalse(valueMap.containsKey(JcrConstants.JCR_TITLE));
        assertFalse(valueMap.containsKey(JcrConstants.JCR_DESCRIPTION));
        assertFalse(valueMap.containsKey(ImageResource.PN_LINK_URL));
    }
}
