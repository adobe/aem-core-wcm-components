/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import javax.json.Json;
import javax.json.JsonReader;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.Image;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.*;

@ExtendWith(AemContextExtension.class)
public class ImageImplTest extends AbstractImageTest {

    private static String TEST_ROOT = "/content";
    protected static String PAGE = TEST_ROOT + "/test";
    private static String IMAGE_TITLE_ALT = "Adobe Logo";
    protected static String IMAGE_FILE_REFERENCE = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png";
    private static String IMAGE_LINK = "https://www.adobe.com";
    protected static String ASSET_NAME = "adobe-systems-logo-and-wordmark";

    protected String testBase = TEST_BASE;
    protected String selector = SELECTOR;
    protected int jpegQuality = JPEG_QUALITY;

    @BeforeEach
    void setUp() {
        internalSetUp(testBase);
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImageWithTwoOrMoreSmartSizes() {
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image0." + selector + "." + jpegQuality +
                ".600.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + jpegQuality +
                ".700.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + jpegQuality +
                ".800.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + jpegQuality +
                ".2000.png/1490005239000/" + ASSET_NAME + ".png\", \"/core/content/test/_jcr_content/root/image0." + selector + "." + jpegQuality +
                ".2500.png/1490005239000/" + ASSET_NAME + ".png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":true}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE0_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImageWithOneSmartSize() {
        String escapedResourcePath = IMAGE3_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600}, "disableLazyLoading", true);
        Image image = getImageUnderTest(IMAGE3_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertNull("Did not expect a file reference.", image.getFileReference());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertEquals(IMAGE_LINK, image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".82.600.png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image3." + selector +  "." + jpegQuality +
        ".600.png/1490005239000/" + ASSET_NAME + ".png\"],\"smartSizes\":[600],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE3_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testSimpleDecorativeImage() {
        String escapedResourcePath = IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE4_PATH);
        assertNull("Did not expect a value for the alt attribute, since the image is marked as decorative.", image.getAlt());
        assertNull("Did not expect a title for this image.", image.getTitle());
        assertFalse("Image should not display a caption popup.", image.displayPopupTitle());
        assertNull("Did not expect a link for this image, since it's marked as decorative.", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/adobe-systems-logo-and-wordmark.png", image.getSrc());
        compareJSON(
                "{\"" + Image.JSON_SMART_IMAGES + "\":[], \"" + Image.JSON_SMART_SIZES + "\":[], \"" + Image.JSON_LAZY_ENABLED +
                        "\":true}",
                image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE4_PATH));
    }

    @Test
    void testExtensionDeterminedFromMimetype() {
        context.contentPolicyMapping(com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{128,256,512,1024,1280,1440,1920,2048},
                "uuidDisabled", true,
                "disableLazyLoading", false);
        String escapedResourcePath = IMAGE18_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE18_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE18_PATH));
    }

    @Test
    void testImageCacheKiller() {
        context.contentPolicyMapping(com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl.RESOURCE_TYPE,
                "uuidDisabled", true);
        String escapedResourcePath = IMAGE15_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE15_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/adobe-systems-logo-and-wordmark.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE15_PATH));
    }

    @Test
    void testTIFFImage() {
        context.contentPolicyMapping(com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl.RESOURCE_TYPE,
                "uuidDisabled", true);
        String escapedResourcePath = IMAGE16_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE16_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".jpeg/1500299989000/" + ASSET_NAME + ".jpeg", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE16_PATH));
    }

    @Test
    void testExportedType() {
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(ImageImpl.RESOURCE_TYPE, (image).getExportedType());
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImageFromTemplateStructure() {
        context.contentPolicyMapping("core/wcm/components/image",
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(TEMPLATE_IMAGE_PATH);
        assertEquals(CONTEXT_PATH + "/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + ".png/structure/jcr" +
                "%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." + jpegQuality +
                ".600.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." +
                jpegQuality + ".700.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\", \"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." +
                selector + "." + jpegQuality + ".800.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\"," +
                    "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." + jpegQuality +
                ".2000.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." +
                jpegQuality + ".2500.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\"]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":true" +
        "}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testLocalFileWithoutFileNameParameter() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600}, "disableLazyLoading", true);
        String escapedResourcePath = IMAGE27_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE27_PATH);
        assertNull("Did not expect a file reference.", image.getFileReference());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".82.600.png/1490005239000.png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image27." + selector +  "." + jpegQuality +
        ".600.png/1490005239000.png\"],\"smartSizes\":[600],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE27_PATH));
    }

    protected void compareJSON(String expectedJson, String json) {
        JsonReader expected = Json.createReader(new StringReader(expectedJson));
        JsonReader actual = Json.createReader(new StringReader(json));
        assertEquals(expected.read(), actual.read());
    }

    protected Image getImageUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(Image.class);
    }

}
