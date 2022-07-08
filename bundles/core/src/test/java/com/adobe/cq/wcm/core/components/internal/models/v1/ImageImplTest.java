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
import com.adobe.cq.wcm.core.components.testing.MockAssetDelivery;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class ImageImplTest extends AbstractImageTest {

    protected static final String TEST_ROOT = "/content";
    protected static String PAGE = TEST_ROOT + "/test";
    private static final String IMAGE_TITLE_ALT = "Adobe Logo";
    protected static String IMAGE_FILE_REFERENCE = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png";
    protected static String IMAGE_FILE_REFERENCE_NO_DATE = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark_no_date.png";
    private static final String IMAGE_LINK = "https://www.adobe.com";
    protected static String ASSET_NAME = "adobe-systems-logo-and-wordmark";

    protected String testBase = TEST_BASE;
    protected String resourceType = ImageImpl.RESOURCE_TYPE;
    protected String selector = SELECTOR;
    protected int jpegQuality = JPEG_QUALITY;

    @BeforeEach
    protected void setUp() {
        internalSetUp(testBase);
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testImageWithTwoOrMoreSmartSizes() {
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(resourceType,
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
    protected void testImageWithOneSmartSize() {
        String escapedResourcePath = IMAGE3_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600}, "disableLazyLoading", true);
        Image image = getImageUnderTest(IMAGE3_PATH);
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertNull(image.getFileReference(), "Did not expect a file reference.");
        assertFalse(image.displayPopupTitle(), "Image should not display a caption popup.");
        assertEquals(IMAGE_LINK, image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".82.600.png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image3." + selector +  "." + jpegQuality +
        ".600.png/1490005239000/" + ASSET_NAME + ".png\"],\"smartSizes\":[600],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE3_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testSimpleDecorativeImage() {
        String escapedResourcePath = IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE4_PATH);
        assertNull(image.getAlt(), "Did not expect a value for the alt attribute, since the image is marked as decorative.");
        assertNull(image.getTitle(), "Did not expect a title for this image.");
        assertFalse(image.displayPopupTitle(), "Image should not display a caption popup.");
        assertNull(image.getLink(), "Did not expect a link for this image, since it's marked as decorative.");
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/adobe-systems-logo-and-wordmark.png", image.getSrc());
        compareJSON(
                "{\"" + Image.JSON_SMART_IMAGES + "\":[], \"" + Image.JSON_SMART_SIZES + "\":[], \"" + Image.JSON_LAZY_ENABLED +
                        "\":true}",
                image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE4_PATH));
    }

    @Test
    protected void testExtensionDeterminedFromMimetype() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{128,256,512,1024,1280,1440,1920,2048},
                "uuidDisabled", true,
                "disableLazyLoading", false);
        String escapedResourcePath = IMAGE18_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE18_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE18_PATH));
    }

    @Test
    protected void testImageCacheKiller() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        String escapedResourcePath = IMAGE15_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE15_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/adobe-systems-logo-and-wordmark.png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE15_PATH));
    }

    @Test
    protected void testTIFFImage() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        String escapedResourcePath = IMAGE16_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE16_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".jpeg/1500299989000/" + ASSET_NAME + ".jpeg", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE16_PATH));
    }

    @Test
    protected void testExportedType() {
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(resourceType, (image).getExportedType());
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testImageFromTemplateStructure() {
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
    protected void testImageFromTemplateStructureNoDate() {
        context.contentPolicyMapping("core/wcm/components/image",
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(TEMPLATE_IMAGE_NO_DATE_PATH);
        assertEquals(CONTEXT_PATH + "/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + ".png/structure/jcr" +
                "%3acontent/root/image_template_no_date.png", image.getSrc());
        assertEquals(IMAGE_TITLE_ALT, image.getAlt());
        assertEquals(IMAGE_TITLE_ALT, image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE_NO_DATE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." + jpegQuality +
                ".600.png/structure/jcr%3acontent/root/image_template_no_date.png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." +
                jpegQuality + ".700.png/structure/jcr%3acontent/root/image_template_no_date.png\", \"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." +
                selector + "." + jpegQuality + ".800.png/structure/jcr%3acontent/root/image_template_no_date.png\"," +
                "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." + jpegQuality +
                ".2000.png/structure/jcr%3acontent/root/image_template_no_date.png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector +  "." +
                jpegQuality + ".2500.png/structure/jcr%3acontent/root/image_template_no_date.png\"]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":true" +
                "}";
        compareJSON(expectedJson, image.getJson());
        assertFalse(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_NO_DATE_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testLocalFileWithoutFileNameParameter() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600}, "disableLazyLoading", true);
        String escapedResourcePath = IMAGE27_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE27_PATH);
        assertNull(image.getFileReference(), "Did not expect a file reference.");
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".82.600.png/1490005239000.png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image27." + selector +  "." + jpegQuality +
        ".600.png/1490005239000.png\"],\"smartSizes\":[600],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE27_PATH));
    }

    @Test
    void testGetDataLayerJson() throws Exception {
        Image image = getImageUnderTest(IMAGE6_PATH);
        assertNotNull(image.getData());

        String expected = "{\"image-db7ae5b54e\":{\"image\":{\"repo:id\":\"60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5\",\"@type\":\"image/gif\",\"repo:modifyDate\":\"2017-03-20T10:20:39Z\",\"repo:path\":\"/content/dam/core/images/Adobe_Systems_logo_and_wordmark.gif\",\"xdm:smartTags\":{\"nature\":0.74,\"lake\":0.79,\"water\":0.78,\"landscape\":0.75}},\"dc:title\":\"Adobe Logo\",\"@type\":\"core/wcm/components/image/v1/image\",\"xdm:linkURL\":\"/core/content/test-image.html\",\"repo:modifyDate\":\"2017-03-20T10:20:39Z\"}}";
        assertEquals(Json.createReader(new StringReader(expected)).read(),
            Json.createReader(new StringReader(image.getData().getJson())).read());
    }

    @Test
    void testenableAssetDeliveryWithoutService() throws Exception {
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
    }

    @Test
    void testEnableAssetDeliveryWithServiceRegistered() throws Exception {
        registerAssetDelivery();
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?quality=82&preferwebp=true", image.getSrc());
    }

    @Test
    void testAssetDeliveryServiceWithoutFileReference() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE2_PATH);
        assertEquals(null, image.getSrc());
    }

    @Test
    void testAssetDeliveryServiceWithInlineFileResource() {
        String escapedResourcePath = IMAGE5_PATH.replace("jcr:content", "_jcr_content");
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE5_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".gif/1489998822138/" + ASSET_NAME + ".gif", image.getSrc());
    }

    @Test
    void testAssetDeliveryEnabledWithoutSmartSizes() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType, "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE0_PATH);
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedJson = "{" +
                "\"smartImages\":[]," +
                "\"smartSizes\":[]," +
                "\"lazyEnabled\":true" +
                "}";
        assertEquals(expectedJson, image.getJson());
    }

    @Test
    void testAssetDeliveryEnabledWithOneSmartSize() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
                "enableAssetDelivery", true,
                "allowedRenditionWidths", new int[]{800});
        Image image = getImageUnderTest(IMAGE0_PATH);
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=800&quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                "\"" + MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=800&quality=82&preferwebp=true\"" +
                "]," +
                "\"smartSizes\":[800]," +
                "\"lazyEnabled\":true" +
                "}";
        assertEquals(expectedJson, image.getJson());
    }

    @Test
    void testAssetDeliveryEnabledWithSmartSizes() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
                "enableAssetDelivery", true,
                "allowedRenditionWidths", new int[]{600, 800});
        Image image = getImageUnderTest(IMAGE0_PATH);
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                    "\"" + MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=600&quality=82&preferwebp=true\"," +
                    "\"" + MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=800&quality=82&preferwebp=true\"" +
                "]," +
                "\"smartSizes\":[600,800]," +
                "\"lazyEnabled\":true" +
                "}";
        assertEquals(expectedJson, image.getJson());
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
