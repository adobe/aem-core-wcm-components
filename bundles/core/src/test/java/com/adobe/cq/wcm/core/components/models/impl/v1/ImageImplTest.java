/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.models.impl.v1;

import java.util.Map;

import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageImplTest {

    @ClassRule
    public static final AemContext aemContext = CoreComponentTestContext.createContext("/image", "/content");

    private static final String TEST_ROOT = "/content";
    private static final String PAGE = TEST_ROOT + "/test";
    private static final String IMAGE0_PATH = PAGE + "/jcr:content/root/image0";
    private static final String IMAGE3_PATH = PAGE + "/jcr:content/root/image3";
    private static final String IMAGE4_PATH = PAGE + "/jcr:content/root/image4";
    private static final String IMAGE15_PATH = PAGE + "/jcr:content/root/image15";
    private static final String IMAGE16_PATH = PAGE + "/jcr:content/root/image16";
    private static final String CONTEXT_PATH = "/core";
    private static final String IMAGE_TITLE_ALT = "Adobe Logo";
    private static final String IMAGE_FILE_REFERENCE = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.svg.png";
    private static final String IMAGE_LINK = "https://www.adobe.com";

    private static final ContentPolicyManager contentPolicyManager = mock(ContentPolicyManager.class);

    @BeforeClass
    public static void init() {
        aemContext.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
                (Function<ResourceResolver, ContentPolicyManager>) resourceResolver -> contentPolicyManager
        );
        aemContext.load().json("/image/test-conf.json", "/conf");
    }

    @Test
    public void testImageWithTwoOrMoreSmartSizes() throws Exception {
        String escapedResourcePath = Text.escapePath(IMAGE0_PATH);
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/jcr%3acontent/root/image0.img.600.png\"," +
                "\"/core/content/test/jcr%3acontent/root/image0.img.700.png\",\"/core/content/test/jcr%3acontent/root/image0" +
                ".img.800.png\",\"/core/content/test/jcr%3acontent/root/image0.img.2000.png\", " +
                "\"/core/content/test/jcr%3acontent/root/image0.img.2500.png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":true}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals("/content/test-image.html", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.png", image.getSrc());
    }

    @Test
    public void testImageWithOneSmartSize() throws Exception {
        String escapedResourcePath = Text.escapePath(IMAGE3_PATH);
        Image image = getImageUnderTest(IMAGE3_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertNull("Did not expect a file reference.", image.getFileReference());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertEquals(IMAGE_LINK, image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.600.png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/jcr%3acontent/root/image3.img.600.png\"],\"smartSizes\":[600]," +
                "\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
    }

    @Test
    public void testSimpleDecorativeImage() throws Exception {
        String escapedResourcePath = Text.escapePath(IMAGE4_PATH);
        Image image = getImageUnderTest(IMAGE4_PATH);
        assertNull("Did not expect a value for the alt attribute, since the image is marked as decorative.", image.getAlt());
        assertNull("Did not expect a title for this image.", image.getTitle());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertNull("Did not expect a link for this image, since it's marked as decorative.", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.png", image.getSrc());
        compareJSON(
                "{\"" + Image.JSON_SMART_IMAGES + "\":[], \"" + Image.JSON_SMART_SIZES + "\":[], \"" + Image.JSON_LAZY_ENABLED +
                        "\":true}",
                image.getJson());
    }

    @Test
    public void testImageCacheKiller() throws Exception {
        String escapedResourcePath = Text.escapePath(IMAGE4_PATH);
        Image image = getImageUnderTest(IMAGE4_PATH, WCMMode.EDIT);
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.png/1494867377756.png", image.getSrc());

        escapedResourcePath = Text.escapePath(IMAGE15_PATH);
        image = getImageUnderTest(IMAGE15_PATH, WCMMode.EDIT);
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.png/1494867377756.png", image.getSrc());
    }

    @Test
    public void testTIFFImage() throws Exception {
        String escapedResourcePath = Text.escapePath(IMAGE16_PATH);
        Image image = getImageUnderTest(IMAGE16_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + ".img.jpeg", image.getSrc());
    }

    private void compareJSON(String expectedJson, String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map expectedMap = objectMapper.readValue(expectedJson, Map.class);
        Map jsonMap = objectMapper.readValue(json, Map.class);
        assertEquals(expectedMap, jsonMap);
    }

    private Image getImageUnderTest(String resourcePath) {
        return getImageUnderTest(resourcePath, null);
    }

    private Image getImageUnderTest(String resourcePath, WCMMode wcmMode) {
        Resource resource = aemContext.resourceResolver().getResource(resourcePath);
        ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();

        SlingBindings slingBindings = new SlingBindings();
        if (contentPolicy != null) {
            when(contentPolicyManager.getPolicy(resource)).thenReturn(contentPolicy);
        }
        slingBindings.put(SlingBindings.RESOURCE, resource);
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        Page page = aemContext.pageManager().getPage(PAGE);
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
        if (wcmMode != null) {
            request.setAttribute(WCMMode.REQUEST_ATTRIBUTE_NAME, wcmMode);
        }
        slingBindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(request));
        slingBindings.put(WCMBindings.PAGE_MANAGER, aemContext.pageManager());
        Style style = mock(Style.class);
        when(style.get(anyString(), (Object) Matchers.anyObject())).thenAnswer(
                invocationOnMock -> invocationOnMock.getArguments()[1]
        );
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Image.class);
    }

}
