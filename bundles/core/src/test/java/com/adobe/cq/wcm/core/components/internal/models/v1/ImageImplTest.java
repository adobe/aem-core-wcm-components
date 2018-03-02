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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.io.StringReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageImplTest extends AbstractImageTest {

    protected static String TEST_ROOT = "/content";
    protected static String PAGE = TEST_ROOT + "/test";
    protected static String CONTEXT_PATH = "/core";
    protected static String IMAGE_TITLE_ALT = "Adobe Logo";
    protected static String IMAGE_FILE_REFERENCE = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png";
    protected static String IMAGE_LINK = "https://www.adobe.com";

    protected String testBase = TEST_BASE;
    protected String selector = SELECTOR;

    @BeforeClass
    public static void setUp() {
        internalSetUp(CONTEXT, TEST_BASE);
    }

    @Test
    public void testImageWithTwoOrMoreSmartSizes() {
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image0." + selector + ".600.png/1490005239000.png\"," +
                "\"/core/content/test/_jcr_content/root/image0." + selector + ".700.png/1490005239000.png\"," +
                "\"/core/content/test/_jcr_content/root/image0" +
                "." + selector + ".800.png/1490005239000.png\",\"/core/content/test/_jcr_content/root/image0." + selector + ".2000.png/1490005239000.png\", " +
                "\"/core/content/test/_jcr_content/root/image0." + selector + ".2500.png/1490005239000.png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":true}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE0_PATH));
    }

    @Test
    public void testImageWithOneSmartSize() {
        String escapedResourcePath = IMAGE3_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE3_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertNull("Did not expect a file reference.", image.getFileReference());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertEquals(IMAGE_LINK, image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".600.png/1490005239000.png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image3." + selector + ".600.png/1490005239000.png\"]," +
                "\"smartSizes\":[600]," +
                "\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE3_PATH));
    }

    @Test
    public void testSimpleDecorativeImage() {
        String escapedResourcePath = IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE4_PATH);
        assertNull("Did not expect a value for the alt attribute, since the image is marked as decorative.", image.getAlt());
        assertNull("Did not expect a title for this image.", image.getTitle());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertNull("Did not expect a link for this image, since it's marked as decorative.", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756.png", image.getSrc());
        compareJSON(
                "{\"" + Image.JSON_SMART_IMAGES + "\":[], \"" + Image.JSON_SMART_SIZES + "\":[], \"" + Image.JSON_LAZY_ENABLED +
                        "\":true}",
                image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE4_PATH));
    }

    @Test
    public void testInvalidAssetTypeImage() {
        Image image = getImageUnderTest(IMAGE17_PATH);
        assertNull(image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE17_PATH));
    }

    @Test
    public void testExtensionDeterminedFromMimetype() {
        String escapedResourcePath = IMAGE18_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE18_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE18_PATH));
    }

    @Test
    public void testImageCacheKiller() {
        String escapedResourcePath = IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE4_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756.png", image.getSrc());

        escapedResourcePath = IMAGE15_PATH.replace("jcr:content", "_jcr_content");
        image = getImageUnderTest(IMAGE15_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE15_PATH));
    }

    @Test
    public void testTIFFImage() {
        String escapedResourcePath = IMAGE16_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE16_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".jpeg/1500299989000.jpeg", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE16_PATH));
    }

    @Test
    public void testExportedType() {
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(ImageImpl.RESOURCE_TYPE, ((ImageImpl) image).getExportedType());
    }

    @Test
    public void testImageFromTemplateStructure() {
        Image image = getImageUnderTest(TEMPLATE_IMAGE_PATH);
        assertEquals(CONTEXT_PATH + "/content/test." + selector + ".png/structure/jcr%3acontent/root/image_template/1490005239000.png", image.getSrc());
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                    "\"/core/content/test." + selector + ".600.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".700.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".800.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".2000.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"," +
                    "\"/core/content/test." + selector + ".2500.png/structure/jcr%3acontent/root/image_template/1490005239000.png\"" +
                "]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":true" +
        "}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_PATH));
    }

    protected void compareJSON(String expectedJson, String json) {
        JsonReader expected = Json.createReader(new StringReader(expectedJson));
        JsonReader actual = Json.createReader(new StringReader(json));
        assertEquals(expected.read(), actual.read());
    }

    protected Image getImageUnderTest(String resourcePath) {
        return getImageUnderTest(resourcePath, Image.class);
    }

    protected <T> T getImageUnderTest(String resourcePath, Class<T> imageClass) {
        return getImageUnderTest(resourcePath, imageClass, null);
    }

    protected <T> T getImageUnderTest(String resourcePath, Class<T> imageClass, String policyDelegatePath) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = null;
        if (mapping != null) {
            contentPolicy = mapping.getPolicy();
        }
        SlingBindings slingBindings = new SlingBindings();
        Style style = null;
        if (contentPolicy != null) {
            when(contentPolicyManager.getPolicy(resource)).thenReturn(contentPolicy);
            style = new MockContentPolicyStyle(contentPolicy);
        }
        if (style == null) {
            style = mock(Style.class);
            when(style.get(anyString(), (Object) Matchers.anyObject())).thenAnswer(
                    invocationOnMock -> invocationOnMock.getArguments()[1]
            );
        }
        slingBindings.put(SlingBindings.RESOURCE, resource);
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        Page page = CONTEXT.pageManager().getPage(PAGE);
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
        slingBindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(request));
        slingBindings.put(WCMBindings.PAGE_MANAGER, CONTEXT.pageManager());
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        if (StringUtils.isNotBlank(policyDelegatePath)) {
            request.setParameterMap(new HashMap<String, Object>() {{ put("contentPolicyDelegatePath", policyDelegatePath);}});
        }
        return request.adaptTo(imageClass);
    }

}
