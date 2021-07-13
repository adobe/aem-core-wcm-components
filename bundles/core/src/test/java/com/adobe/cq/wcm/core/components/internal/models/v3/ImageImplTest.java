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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.commons.Externalizer;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;

@ExtendWith(AemContextExtension.class)
class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImplTest {

    private static String TEST_BASE = "/image/v3";
    private static final String IMAGE50_PATH = PAGE + "/jcr:content/root/image50";

    @BeforeEach
    @Override
    protected void setUp() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
        resourceType = ImageImpl.RESOURCE_TYPE;
        testBase = TEST_BASE;
        internalSetUp(TEST_BASE);
    }

    @InjectMocks
    private ImageImpl imageImpl;

    @Mock
    Externalizer externalizer;

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
        assertNull(image.getSrcset());
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
        String expectedSrcset = "/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY + ".600.png/1490005239000/" + ASSET_NAME + ".png 600w," +
            "/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY + ".700.png/1490005239000/" + ASSET_NAME + ".png 700w," +
            "/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY + ".800.png/1490005239000/" + ASSET_NAME + ".png 800w," +
            "/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY + ".2000.png/1490005239000/" + ASSET_NAME + ".png 2000w," +
            "/core/content/test/_jcr_content/root/image0." + selector + "." + JPEG_QUALITY + ".2500.png/1490005239000/" + ASSET_NAME + ".png 2500w";
        assertEquals(expectedSrcset, image.getSrcset());
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
            assertValidLink(area.getLink(), (String) expectedAreas[index][3], StringUtils.trimToNull((String) expectedAreas[index][4]));
            index++;
        }
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE24_PATH));
    }

    @Test
    protected void testEmptyImageDelegatingToFeaturedImage() {
        Image image = getImageUnderTest(IMAGE50_PATH);
        assertEquals("/core/content/test/_jcr_content/root/image50.coreimg.png/1490005239000/adobe-systems-logo-and-wordmark.png", image.getSrc(), "getSrc()");
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        assertEquals("image-cf7954fac5", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE50_PATH));
    }

    @Override
    @Test
    protected void testImageWithMoreThanOneSmartSize() {
        context.contentPolicyMapping(resourceType,
            "allowedRenditionWidths", new int[]{600, 700, 800, 2000, 2500});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH);
        assertArrayEquals(new int[]{600, 700, 800, 2000, 2500}, image.getWidths());
        assertTrue(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE0_PATH));
    }

    @Override
    @Test
    protected void testImageWithNoSmartSize() {
        context.contentPolicyMapping(resourceType,
            "uuidDisabled", true);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE4_PATH);

        assertArrayEquals(new int[]{}, image.getWidths());
        assertTrue(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE4_PATH));
    }

    @Override
    @Test
    protected void testImageWithOneSmartSize() {
        context.contentPolicyMapping(resourceType,
            "allowedRenditionWidths", new int[]{600});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertArrayEquals(new int[]{600}, image.getWidths());
        assertTrue(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH));
    }

    @Override
    @Test
    protected void testImageWithOneSmartSizeAndPolicyDelegate() {
        context.contentPolicyMapping(resourceType,
            "allowedRenditionWidths", new int[]{600});
        context.request().setParameterMap(ImmutableMap.of("contentPolicyDelegatePath", IMAGE0_PATH));
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);

        assertArrayEquals(new int[]{600}, image.getWidths());
        assertTrue(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH + "-with-policy-delegate"));
    }

    @Test
    protected void testLazyLoadedDisabled() {
        context.contentPolicyMapping(resourceType,
            "disableLazyLoading", true, "allowedRenditionWidths", new int[]{600});
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertFalse(image.isLazyEnabled());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, AbstractImageTest.IMAGE3_PATH + "-with-lazy-loading-disabled"));
    }

    @Test
    protected void testGetSrcUriTemplate() {
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertEquals("/core/content/test/_jcr_content/root/image3.coreimg{.width}.png", image.getSrcUriTemplate());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testGetBaseImageResolution() {
        Mockito.when(externalizer.publishLink(any(), any())).thenReturn("https://via.placeholder.com/850.jpeg");
        assertArrayEquals(new String[]{"850", "850"}, imageImpl.getBaseImageResolution());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testGetBaseImageResolutionInvalidImageUrl() {
        Mockito.when(externalizer.publishLink(any(), any())).thenReturn("ghghiu");
        assertArrayEquals(new String[]{null, null}, imageImpl.getBaseImageResolution());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testGetBaseImageResolutionWrongImageUrl() {
        Mockito.when(externalizer.publishLink(any(), any())).thenReturn("https://invalidurl.com/whatever.jpeg");
        assertArrayEquals(new String[]{null, null}, imageImpl.getBaseImageResolution());
    }
}
