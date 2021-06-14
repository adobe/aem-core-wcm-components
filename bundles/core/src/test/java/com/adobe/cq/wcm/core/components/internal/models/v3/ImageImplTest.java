/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImplTest {

    private static String TEST_BASE = "/image/v3";

    @BeforeEach
    @Override
    protected void setUp() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
        resourceType = ImageImpl.RESOURCE_TYPE;
        testBase = TEST_BASE;
        internalSetUp(TEST_BASE);
    }

    @Test
    void testImageWithLazyThreshold() {
        testImageWithLazyThreshold(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testGetDataLayerJson() throws Exception {
        testGetDataLayerJson(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImage() {
        testDMImage(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageOnAuthor() {
        testDMImageOnAuthor(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageOneSmartSize() {
        testDMImageOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageTwoSmartSizes() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithImagePreset() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithImagePresetOneSmartSize() {
        testDMImageWithImagePresetOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithImagePresetTwoSmartSizes() {
        testDMImageWithImagePresetTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithImageModifiers() {
        testDMImageWithImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithImagePresetAndImageModifiers() {
        testDMImageWithImagePresetAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithSmartCropRendition() {
        testDMImageTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithSmartCropRenditionOneSmartSize() {
        testDMImageWithSmartCropRenditionOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithSmartCropRenditionTwoSmartSizes() {
        testDMImageWithSmartCropRenditionTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithSmartCropRenditionAndImageModifiers() {
        testDMImageWithSmartCropRenditionAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithAutoSmartCrop() {
        testDMImageWithAutoSmartCrop(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithAutoSmartCropOneSmartSize() {
        testDMImageWithAutoSmartCropOneSmartSize(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithAutoSmartCropTwoSmartSizes() {
        testDMImageWithAutoSmartCropTwoSmartSizes(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    void testDMImageWithAutoSmartCropAndImageModifiers() {
        testDMImageWithAutoSmartCropAndImageModifiers(ImageImpl.RESOURCE_TYPE);
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testSimpleDecorativeImage() {
        context.contentPolicyMapping(resourceType,
                "uuidDisabled", true);
        String escapedResourcePath = AbstractImageTest.IMAGE4_PATH.replace("jcr:content", "_jcr_content");
        com.adobe.cq.wcm.core.components.models.Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);
        assertEquals(null, image.getAlt(), "Did not expect a value for the alt attribute, since the image is marked as decorative.");
        assertEquals("Adobe Systems Logo and Wordmark", image.getTitle());
        assertTrue(image.displayPopupTitle());
        assertEquals(null, image.getLink(), "Did not expect a link for this image, since it's marked as decorative.");
        assertNull(image.getImageLink(), "Expected null link");
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1494867377756/" + ASSET_NAME + ".png", image.getSrc());
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
        String expectedJson = "{\"smartImages\":[\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".600.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".700.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0" + "." + selector + "." + JPEG_QUALITY +
                ".800.png/1490005239000/" + ASSET_NAME + ".png\",\"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".2000.png/1490005239000/" + ASSET_NAME + ".png\", \"/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY +
                ".2500.png/1490005239000/" + ASSET_NAME + ".png\"],\"smartSizes\":[600,700,800,2000,2500],\"lazyEnabled\":false}";
        compareJSON(expectedJson, image.getJson());
        assertTrue(image.displayPopupTitle());
        assertEquals(CONTEXT_PATH + "/content/test-image.html", image.getLink());
        assertValidLink(image.getImageLink(), "/content/test-image.html", context.request());
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000/" + ASSET_NAME + ".png", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
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
            assertValidLink(area.getLink(), (String)expectedAreas[index][3], StringUtils.trimToNull((String)expectedAreas[index][4]));
            index++;
        }
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE24_PATH));
    }

}
