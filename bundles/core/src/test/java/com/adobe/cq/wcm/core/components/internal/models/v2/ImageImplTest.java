/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.adobe.cq.wcm.core.components.testing.MockAssetDelivery;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.day.cq.wcm.api.WCMMode.REQUEST_ATTRIBUTE_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImplTest {

    private static final String TEST_BASE = "/image/v2";
    private static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    private static final String IMAGE21_PATH = PAGE + "/jcr:content/root/image21";
    private static final String IMAGE22_PATH = PAGE + "/jcr:content/root/image22";
    private static final String IMAGE32_PATH = PAGE + "/jcr:content/root/image32";
    private static final String IMAGE33_PATH = PAGE + "/jcr:content/root/image33";
    private static final String IMAGE34_PATH = PAGE + "/jcr:content/root/image34";
    private static final String IMAGE35_PATH = PAGE + "/jcr:content/root/image35";
    private static final String IMAGE36_PATH = PAGE + "/jcr:content/root/image36";
    private static final String IMAGE37_PATH = PAGE + "/jcr:content/root/image37";
    private static final String IMAGE38_PATH = PAGE + "/jcr:content/root/image38";
    private static final String IMAGE39_PATH = PAGE + "/jcr:content/root/image39";
    private static final String IMAGE40_PATH = PAGE + "/jcr:content/root/image40";
    private static final String IMAGE42_PATH = PAGE + "/jcr:content/root/image42";

    @BeforeEach
    @Override
    protected void setUp() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
        resourceType = ImageImpl.RESOURCE_TYPE;
        testBase = TEST_BASE;
        internalSetUp(TEST_BASE);
    }

    @Test
    protected void testGetUuid() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    protected void testGetUuidNull() {
        Image image = getImageUnderTest(IMAGE22_PATH);
        assertNull(image.getUuid());
    }

    @Test
    @Override
    protected void testImageWithOneSmartSize() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertArrayEquals(new int[] {600}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH));
    }

    @Test
    protected void testImageWithOneSmartSizeAndPolicyDelegate() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600});
        context.request().setParameterMap(ImmutableMap.of("contentPolicyDelegatePath", IMAGE0_PATH));
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);

        assertArrayEquals(new int[] {600}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH + "-with-policy-delegate"));
    }

    @Test
    protected void testImageWithMoreThanOneSmartSize() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertArrayEquals(new int[] { 600, 700, 800, 2000, 2500 }, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    protected void testImageWithNoSmartSize() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);

        assertArrayEquals(new int[] {}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE4_PATH));
    }

    @Test
    protected void testImageWithAltAndTitleFromDAM() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(IMAGE20_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
    }

    @Test
    protected void testImageWithAltAndFallbackIfDescriptionIsEmpty() {
        Image image = getImageUnderTest(IMAGE21_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getAlt());
    }

    @Test
    void testGetDataLayerJson() {
        testGetDataLayerJson(ImageImpl.RESOURCE_TYPE);
    }

    protected void testGetDataLayerJson(String resourceType) {
        Image image = getImageUnderTest(IMAGE6_PATH);
        assertNotNull(image.getData());

        String expected = "{\"image-db7ae5b54e\":{\"@type\":\"" + resourceType + "\",\"repo:modifyDate\":\"2017-03-20T08:33:42Z\",\"dc:title\":\"Adobe Systems Logo and Wordmark\",\"xdm:linkURL\":\"/core/content/test-image.html\",\"image\":{\"repo:id\":\"60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5\",\"repo:modifyDate\":\"2017-03-20T10:20:39Z\",\"@type\":\"image/gif\",\"repo:path\":\"/content/dam/core/images/Adobe_Systems_logo_and_wordmark.gif\",\"xdm:smartTags\":{\"nature\":0.74,\"lake\":0.79,\"water\":0.78,\"landscape\":0.75}}}}";
        assertEquals(Json.createReader(new StringReader(expected)).read(),
                Json.createReader(new StringReader(image.getData().getJson())).read());
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testSimpleDecorativeImage() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        String escapedResourcePath = AbstractImageTest.IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);
        assertNull(image.getAlt(), "Did not expect a value for the alt attribute, since the image is marked as decorative.");
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertTrue(image.displayPopupTitle(), "Image should display a caption popup.");
        assertNull(image.getLink(), "Did not expect a link for this image, since it's marked as decorative.");
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/" + ASSET_NAME + ".png", image.getSrc());
        compareJSON(
                "{\"" + com.adobe.cq.wcm.core.components.models.Image.JSON_SMART_IMAGES + "\":[], \"" + com.adobe.cq.wcm.core.components.models.Image.JSON_SMART_SIZES + "\":[], \"" + com.adobe.cq.wcm.core.components.models.Image.JSON_LAZY_ENABLED +
                        "\":false}",
                image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE4_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testImageWithTwoOrMoreSmartSizes() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        String escapedResourcePath = AbstractImageTest.IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        assertEquals("/core/content/test/_jcr_content/root/image0." + selector +
                "." + JPEG_QUALITY + ImageImpl.SRC_URI_TEMPLATE_WIDTH_VAR +
                ".png/1490005239000/adobe-systems-logo-and-wordmark.png", image.getSrcUriTemplate());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".600.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".700.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0" + "." + selector + "." + JPEG_QUALITY +
                ".800.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".2000.png/1490005239000/" + ASSET_NAME + ".png\", \"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".2500.png/1490005239000/" + ASSET_NAME + ".png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testImageWithMap() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(AbstractImageTest.IMAGE24_PATH);
        Object[][] expectedAreas = {
            {"circle", "256,256,256", "0.2000,0.3001,0.2000", "http://adobe.com", "", ""},
            {"rect", "256,171,1023,682", "0.1992,0.2005,0.7992,0.7995", "http://adobe.com", "", "altText"},
            {"poly", "917,344,1280,852,532,852", "0.7164,0.4033,1.0000,0.9988,0.4156,0.9988", "http://adobe.com", "_blank", ""}
        };
        List<ImageArea> areas = image.getAreas();
        int index = 0;
        while (areas.size() > index) {
            ImageArea area = areas.get(index);
            assertEquals(expectedAreas[index][0], area.getShape(), "The image area's shape is not as expected.");
            assertEquals(expectedAreas[index][1], area.getCoordinates(), "The image area's coordinates are not as expected.");
            assertEquals(expectedAreas[index][2], area.getRelativeCoordinates(), "The image area's relative coordinates are not as expected.");
            assertEquals(expectedAreas[index][3], area.getHref(), "The image area's href is not as expected.");
            assertEquals(expectedAreas[index][4], area.getTarget(), "The image area's target is not as expected.");
            assertEquals(expectedAreas[index][5], area.getAlt(), "The image area's alt text is not as expected.");
            index++;
        }
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE24_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testImageFromTemplateStructure() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(TEMPLATE_IMAGE_PATH);
        assertEquals(CONTEXT_PATH + "/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + ".png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                    "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." + JPEG_QUALITY +
                ".600.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." +
                JPEG_QUALITY +".700.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\", \"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." +
                selector + "." + JPEG_QUALITY + ".800.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\"," +
                "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." + JPEG_QUALITY + "." +
                "2000.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\"," + "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure."
                 + selector + "." + JPEG_QUALITY +".2500.png/structure/jcr%3acontent/root/image_template/1490005239000/" + ASSET_NAME + ".png\"" + "]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":false" +
        "}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testImageFromTemplateStructureNoDate() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(TEMPLATE_IMAGE_NO_DATE_PATH);
        assertEquals(CONTEXT_PATH + "/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + ".png/structure/jcr%3acontent/root/image_template_no_date.png", image.getSrc());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE_NO_DATE, image.getFileReference());
        String expectedJson = "{" +
                "\"smartImages\":[" +
                "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." + JPEG_QUALITY +
                ".600.png/structure/jcr%3acontent/root/image_template_no_date.png\",\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." +
                JPEG_QUALITY +".700.png/structure/jcr%3acontent/root/image_template_no_date.png\", \"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." +
                selector + "." + JPEG_QUALITY + ".800.png/structure/jcr%3acontent/root/image_template_no_date.png\"," +
                "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure." + selector + "." + JPEG_QUALITY + "." +
                "2000.png/structure/jcr%3acontent/root/image_template_no_date.png\"," + "\"/core/conf/coretest/settings/wcm/templates/testtemplate/structure."
                + selector + "." + JPEG_QUALITY +".2500.png/structure/jcr%3acontent/root/image_template_no_date.png\"" + "]," +
                "\"smartSizes\":[600,700,800,2000,2500]," +
                "\"lazyEnabled\":false" +
                "}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_NO_DATE_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testLocalFileWithoutFileNameParameter() {
        context.contentPolicyMapping(resourceType,
                "allowedRenditionWidths", new int[]{600});
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
    protected void testSVGImage() {
        Image image = getImageUnderTest(IMAGE22_PATH);
        assertEquals(0, image.getWidths().length);
    }


    @Test
    void testImageWithLazyThreshold() {
        testImageWithLazyThreshold(ImageImpl.RESOURCE_TYPE);
    }

    protected void testImageWithLazyThreshold(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_LAZY_THRESHOLD, 100);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertEquals(100, image.getLazyThreshold());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH + "-with-lazy-threshold"));
    }

    @Test
    void testDMImage() {
        testDMImage(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImage(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE32_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE32_PATH));
    }

    @Test
    void testDMImageOnAuthor() {
        testDMImageOnAuthor(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageOnAuthor(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        context.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
        Image image = getImageUnderTest(IMAGE32_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE32_PATH + "-on-author"));
    }

    @Test
    void testDMImageAnimatedGifOnAuthor() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        context.request().setAttribute(REQUEST_ATTRIBUTE_NAME, WCMMode.EDIT);
        Image image = getImageUnderTest(IMAGE40_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE40_PATH + "-on-author"));
    }

    @Test
    void testDMImageOneSmartSize() {
        testDMImageOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageOneSmartSize(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600});
        }});
        Image image = getImageUnderTest(IMAGE32_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE32_PATH + "-one-smart-size"));
    }

    @Test
    void testDMImageTwoSmartSizes() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageTwoSmartSizes(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600, 800});
        }});
        Image image = getImageUnderTest(IMAGE32_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE32_PATH + "-two-smart-sizes"));
    }

    @Test
    void testDMImageWithImagePreset() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithImagePreset(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE33_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE33_PATH));
    }

    @Test
    void testDMImageWithImagePresetOneSmartSize() {
        testDMImageWithImagePresetOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithImagePresetOneSmartSize(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600});
        }});
        Image image = getImageUnderTest(IMAGE33_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE33_PATH + "-one-smart-size"));
    }

    @Test
    void testDMImageWithImagePresetTwoSmartSizes() {
        testDMImageWithImagePresetTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithImagePresetTwoSmartSizes(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600, 800});
        }});
        Image image = getImageUnderTest(IMAGE33_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE33_PATH + "-two-smart-sizes"));
    }

    @Test
    void testDMImageWithImageModifiers() {
        testDMImageWithImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithImageModifiers(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE34_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE34_PATH));
    }

    @Test
    void testDMImageWithImagePresetAndImageModifiers() {
        testDMImageWithImagePresetAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithImagePresetAndImageModifiers(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE35_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE35_PATH));
    }

    @Test
    void testDMImageWithSmartCropRendition() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithSmartCropRendition(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE36_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE36_PATH));
    }

    @Test
    void testDMImageWithSmartCropRenditionOneSmartSize() {
        testDMImageWithSmartCropRenditionOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithSmartCropRenditionOneSmartSize(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600});
        }});
        Image image = getImageUnderTest(IMAGE36_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE36_PATH + "-one-smart-size"));
    }

    @Test
    void testDMImageWithSmartCropRenditionTwoSmartSizes() {
        testDMImageWithSmartCropRenditionTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithSmartCropRenditionTwoSmartSizes(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600, 800});
        }});
        Image image = getImageUnderTest(IMAGE36_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE36_PATH + "-two-smart-sizes"));
    }

    @Test
    void testDMImageWithSmartCropRenditionAndImageModifiers() {
        testDMImageWithSmartCropRenditionAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithSmartCropRenditionAndImageModifiers(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE37_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE37_PATH));
    }

    @Test
    void testDMImageWithAutoSmartCrop() {
        testDMImageWithAutoSmartCrop(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithAutoSmartCrop(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE38_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE38_PATH));
    }

    @Test
    void testDMImageWithAutoSmartCropOneSmartSize() {
        testDMImageWithAutoSmartCropOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithAutoSmartCropOneSmartSize(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600});
        }});
        Image image = getImageUnderTest(IMAGE38_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE38_PATH + "-one-smart-size"));
    }

    @Test
    void testDMImageWithAutoSmartCropTwoSmartSizes() {
        testDMImageWithAutoSmartCropTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithAutoSmartCropTwoSmartSizes(String resourceType) {
        context.contentPolicyMapping(resourceType, new HashMap<String, Object>() {{
            put(Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
            put(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new int[]{600, 800});
        }});
        Image image = getImageUnderTest(IMAGE38_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE38_PATH + "-two-smart-sizes"));
    }

    @Test
    void testDMImageWithAutoSmartCropAndImageModifiers() {
        testDMImageWithAutoSmartCropAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    protected void testDMImageWithAutoSmartCropAndImageModifiers(String resourceType) {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE39_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE39_PATH));
    }

    @Test
    void testDMAnimatedGif() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE40_PATH);
        assertTrue(image.isDmImage());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE40_PATH));
    }

    @Test
    void testDMWithEncoding() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE42_PATH);
        assertTrue(image.isDmImage());
        assertEquals("https://s7d9.scene7.com/is/image/dmtestcompany/Adobe%20Systems%20logo%20and%20wordmark%20DM?ts=1490005239000&dpr=on,{dpr}", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE42_PATH));
    }

    @Test
    void testAssetDeliveryEnabledWithoutSmartSizes() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType, "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertArrayEquals(new int[]{}, image.getWidths());
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedSrcUriTemplate = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  +
                ".png?width=" + ImageImpl.SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY + "&quality=82&preferwebp=true";
        assertEquals(expectedSrcUriTemplate , image.getSrcUriTemplate());
    }

    @Test
    void testAssetDeliveryEnabledWithOneSmartSize() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
                "enableAssetDelivery", true,
                "allowedRenditionWidths", new int[]{800});
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertArrayEquals(new int[]{800}, image.getWidths());
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=800&quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedSrcUriTemplate = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  +
                ".png?width=" + ImageImpl.SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY + "&quality=82&preferwebp=true";
        assertEquals(expectedSrcUriTemplate , image.getSrcUriTemplate());
    }

    @Test
    void testAssetDeliveryEnabledWithSmartSizes() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
                "enableAssetDelivery", true,
                "allowedRenditionWidths", new int[]{600, 800});
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertArrayEquals(new int[]{600, 800}, image.getWidths());
        String expectedSrc = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?quality=82&preferwebp=true";
        assertEquals(expectedSrc, image.getSrc());
        String expectedSrcUriTemplate = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  +
                ".png?width=" + ImageImpl.SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY + "&quality=82&preferwebp=true";
        assertEquals(expectedSrcUriTemplate , image.getSrcUriTemplate());
    }
}
