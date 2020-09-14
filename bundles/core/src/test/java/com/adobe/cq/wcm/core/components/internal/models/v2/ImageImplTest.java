/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImplTest {

    private static final String TEST_BASE = "/image/v2";
    private static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    private static final String IMAGE21_PATH = PAGE + "/jcr:content/root/image21";
    private static final String IMAGE22_PATH = PAGE + "/jcr:content/root/image22";

    @BeforeEach
    void setUp() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
        testBase = TEST_BASE;
        internalSetUp(TEST_BASE);
    }

    @Test
    void testExportedType() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals(ImageImpl.RESOURCE_TYPE, image.getExportedType());
    }

    @Test
    void testGetUuid() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    void testGetUuidNull() {
        Image image = getImageUnderTest(IMAGE22_PATH);
        assertNull(image.getUuid());
    }

    @Test
    void testImageWithOneSmartSize() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertArrayEquals(new int[] {600}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE3_PATH));
    }

    @Test
    void testImageWithOneSmartSizeAndPolicyDelegate() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600});
        context.request().setParameterMap(ImmutableMap.of("contentPolicyDelegatePath", IMAGE0_PATH));
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);

        assertArrayEquals(new int[] {600}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE3_PATH + "-with-policy-delegate"));
    }

    @Test
    void testImageWithMoreThanOneSmartSize() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertArrayEquals(new int[] { 600, 700, 800, 2000, 2500 }, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    void testImageWithNoSmartSize() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "uuidDisabled", true);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);

        assertArrayEquals(new int[] {}, image.getWidths());
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE4_PATH));
    }

    @Test
    void testImageWithAltAndTitleFromDAM() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(IMAGE20_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
    }

    @Test
    void testImageWithAltAndFallbackIfDescriptionIsEmpty() {
        Image image = getImageUnderTest(IMAGE21_PATH);
        assertEquals("Adobe Systems Logo and Wordmark", image.getAlt());
    }

    @Test
    @SuppressWarnings("deprecation")
    void testSimpleDecorativeImage() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
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
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE4_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImageWithTwoOrMoreSmartSizes() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        String escapedResourcePath = AbstractImageTest.IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt());
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertEquals(IMAGE_FILE_REFERENCE, image.getFileReference());
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
    void testImageWithMap() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
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
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE24_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImageFromTemplateStructure() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
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
    void testLocalFileWithoutFileNameParameter() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                "allowedRenditionWidths", new int[]{600});
        String escapedResourcePath = IMAGE27_PATH.replace("jcr:content", "_jcr_content");
        Image image = getImageUnderTest(IMAGE27_PATH);
        assertNull(image.getFileReference(), "Did not expect a file reference.");
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".82.600.png/1490005239000.png", image.getSrc());
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image27." + selector +  "." + jpegQuality +
            ".600.png/1490005239000.png\"],\"smartSizes\":[600],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, IMAGE27_PATH));
    }

    @Test
    void testSVGImage() {
        Image image = getImageUnderTest(IMAGE22_PATH);
        assertEquals(0, image.getWidths().length);
    }


    @Test
    void testImageWithLazyThreshold() {
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, Image.PN_DESIGN_LAZY_THRESHOLD, 100);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertEquals(100, image.getLazyThreshold());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE3_PATH + "-with-lazy-threshold"));
    }
}
