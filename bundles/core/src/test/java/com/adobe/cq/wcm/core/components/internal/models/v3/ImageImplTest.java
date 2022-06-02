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

import com.adobe.cq.wcm.core.components.testing.MockAssetDelivery;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.wcm.api.designer.Style;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static com.adobe.cq.wcm.core.components.models.Image.PN_DESIGN_RESIZE_WIDTH;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ImageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImplTest {

    private static String TEST_BASE = "/image/v3";
    private static final String IMAGE50_PATH = PAGE + "/jcr:content/root/image50";
    private static final String IMAGE51_PATH = PAGE + "/jcr:content/root/image51";
    private static final String IMAGE52_PATH = PAGE + "/jcr:content/root/image52";
    private static final String IMAGE53_PATH = PAGE + "/jcr:content/root/image53";
    private static final String IMAGE54_PATH = PAGE + "/jcr:content/root/image54";
    private static final String IMAGE55_PATH = PAGE + "/jcr:content/root/image55";
    private static final String IMAGE56_PATH = PAGE + "/jcr:content/root/image56";
    private static final String IMAGE57_PATH = PAGE + "/jcr:content/root/image57";
    private static final String IMAGE58_PATH = PAGE + "/jcr:content/root/image58";
    private static final String IMAGE42_PATH = PAGE + "/jcr:content/root/image42";

    private static String PAGE0 = TEST_ROOT + "/test_page0";
    private static String PAGE1 = TEST_ROOT + "/test_page1";
    private static String PAGE2 = TEST_ROOT + "/test_page2";
    private static String PAGE3 = TEST_ROOT + "/test_page3";
    private static final String PAGE0_IMAGE0_PATH = PAGE0 + "/jcr:content/root/page0_image0";
    private static final String PAGE0_IMAGE1_PATH = PAGE0 + "/jcr:content/root/page0_image1";
    private static final String PAGE0_IMAGE2_PATH = PAGE0 + "/jcr:content/root/page0_image2";
    private static final String PAGE0_IMAGE3_PATH = PAGE0 + "/jcr:content/root/page0_image3";
    private static final String PAGE1_IMAGE0_PATH = PAGE1 + "/jcr:content/root/page1_image0";
    private static final String PAGE2_IMAGE0_PATH = PAGE2 + "/jcr:content/root/page2_image0";
    private static final String PAGE3_IMAGE0_PATH = PAGE3 + "/jcr:content/root/page3_image0";

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
    private Resource inheritedResource;

    @Mock
    private ValueMap inheritedResourceProperties;

    @Mock
    private Asset asset;

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Style currentStyle;

    @Test
    void testImageWithLazyThreshold() {
        context.contentPolicyMapping(resourceType, Image.PN_DESIGN_LAZY_THRESHOLD, 100);
        Image image = getImageUnderTest(AbstractImageTest.IMAGE3_PATH);
        assertEquals(0, image.getLazyThreshold());
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
    protected void testBaseDamAssetImageDimension() {
        asset = context.create().asset("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", 850, 509, StandardImageHandler.JPEG_MIMETYPE);
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(inheritedResource);
        when(inheritedResource.adaptTo(Asset.class)).thenReturn(asset);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn(null);

        assertEquals("850", imageImpl.getWidth());
        assertEquals("509", imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_resizeWidthSmallerThanAssetWidth() {
        asset = context.create().asset("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", 850, 509, StandardImageHandler.JPEG_MIMETYPE);
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(inheritedResource);
        when(inheritedResource.adaptTo(Asset.class)).thenReturn(asset);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn("600");

        assertEquals("600", imageImpl.getWidth());
        assertEquals("359", imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_resizeWidthBiggerThanAssetWidth() {
        asset = context.create().asset("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", 850, 509, StandardImageHandler.JPEG_MIMETYPE);
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(inheritedResource);
        when(inheritedResource.adaptTo(Asset.class)).thenReturn(asset);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn("1000");

        assertEquals("850", imageImpl.getWidth());
        assertEquals("509", imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_resizeWidthZero() {
        asset = context.create().asset("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg", 850, 509, StandardImageHandler.JPEG_MIMETYPE);
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(inheritedResource);
        when(inheritedResource.adaptTo(Asset.class)).thenReturn(asset);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn("0");

        assertEquals("850", imageImpl.getWidth());
        assertEquals("509", imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_nullFileReference() {
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn(null);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn(null);

        assertNull(imageImpl.getWidth());
        assertNull(imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_nullAssetResource() {
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(null);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn(null);

        assertNull(imageImpl.getWidth());
        assertNull(imageImpl.getHeight());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    protected void testBaseDamAssetImageDimension_nullAsset() {
        when(inheritedResource.getValueMap()).thenReturn(inheritedResourceProperties);
        when(inheritedResourceProperties.get(any(), any())).thenReturn("/content/dam/core-components-examples/library/sample-assets/lava-into-ocean.jpg");
        when(request.getResourceResolver()).thenReturn(resolver);
        when(resolver.getResource(any())).thenReturn(inheritedResource);
        when(inheritedResource.adaptTo(Asset.class)).thenReturn(null);
        when (currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class)).thenReturn(null);

        assertNull(imageImpl.getWidth());
        assertNull(imageImpl.getHeight());
    }

    @Test
    protected void testInheritedFeaturedImage_altValueFromPageImage() {
        Image image = getImageUnderTest(IMAGE51_PATH);
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE51_PATH));
    }

    @Test
    protected void testInheritedFeaturedImage_altValueFromImage() {
        Image image = getImageUnderTest(IMAGE52_PATH);
        assertEquals("image52 alt", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE52_PATH));
    }

    @Test
    protected void testInheritedFeaturedImage_altValueFromPageImage_decorative() {
        Image image = getImageUnderTest(IMAGE53_PATH);
        assertNull(image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE53_PATH));
    }

    @Test
    protected void testInheritedFeaturedImage_altValueFromImage_decorative() {
        Image image = getImageUnderTest(IMAGE54_PATH);
        assertNull(image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE54_PATH));
    }

    @Test
    protected void testNoInheritedFeaturedImage_altValueFromDAM() {
        Image image = getImageUnderTest(IMAGE55_PATH);
        assertEquals("transparent HD PNG", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/transparent_hd.png", image.getFileReference(), "getFileReference()");
        assertEquals("f6460529-b7b1-4b3a-8980-b3b3f0ee109c", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE55_PATH));
    }

    @Test
    protected void testNoInheritedFeaturedImage_altValueFromImage() {
        Image image = getImageUnderTest(IMAGE56_PATH);
        assertEquals("image52a alt", image.getAlt(), "getAlt()");
        assertNull(image.getFileReference(), "getFileReference()");
        assertNull(image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE56_PATH));
    }

    @Test
    protected void testNoInheritedFeaturedImage_altValueFromDAM_decorative() {
        Image image = getImageUnderTest(IMAGE57_PATH);
        assertNull(image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/transparent_hd.png", image.getFileReference(), "getFileReference()");
        assertEquals("f6460529-b7b1-4b3a-8980-b3b3f0ee109c", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE57_PATH));
    }

    @Test
    protected void testNoInheritedFeaturedImage_altValueFromImage_decorative() {
        Image image = getImageUnderTest(IMAGE58_PATH);
        assertNull(image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/transparent_hd.png", image.getFileReference(), "getFileReference()");
        assertEquals("f6460529-b7b1-4b3a-8980-b3b3f0ee109c", image.getUuid(), "getUuid()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE58_PATH));
    }

    @Test
    protected void testEmptyImage_emptyFeaturedImage() {
        Image image = getImageUnderTest(PAGE0_IMAGE0_PATH);
        assertNull(image.getSrc(), "getSrc()");
        assertNull(image.getAlt(), "getAlt()");
        assertNull(image.getFileReference(), "getFileReference()");
        assertNull(image.getUuid(), "getUuid()");
        assertEquals("image-c7ca64a6e5", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE0_IMAGE0_PATH));
    }

    @Test
    protected void testEmptyImage_emptyFeaturedImage_inherit() {
        Image image = getImageUnderTest(PAGE0_IMAGE1_PATH);
        assertNull(image.getSrc(), "getSrc()");
        assertNull(image.getAlt(), "getAlt()");
        assertNull(image.getFileReference(), "getFileReference()");
        assertNull(image.getUuid(), "getUuid()");
        assertEquals("image-61631780d5", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE0_IMAGE1_PATH));
    }

    @Test
    protected void testInheritedPageImage_pageImageAltValueFromDAM() {
        Image image = getImageUnderTest(PAGE1_IMAGE0_PATH);
        assertEquals("/core/content/test_page1/_jcr_content/root/page1_image0.coreimg.png/1490005239000/adobe-systems-logo-and-wordmark.png", image.getSrc(), "getSrc()");
        assertEquals("Adobe Systems Logo and Wordmark in PNG format", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        assertEquals("image-6401b77a35", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE1_IMAGE0_PATH));
    }

    @Test
    protected void testInheritedPageImage_pageImageAltValueFromResource() {
        Image image = getImageUnderTest(PAGE2_IMAGE0_PATH);
        assertEquals("/core/content/test_page2/_jcr_content/root/page2_image0.coreimg.png/1490005239000/adobe-systems-logo-and-wordmark.png", image.getSrc(), "getSrc()");
        assertEquals("featured image alt", image.getAlt(), "getAlt()");
        assertEquals("/content/dam/core/images/Adobe_Systems_logo_and_wordmark.png", image.getFileReference(), "getFileReference()");
        assertEquals("60a1a56e-f3f4-4021-a7bf-ac7a51f0ffe5", image.getUuid(), "getUuid()");
        assertEquals("image-310f56f715", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE2_IMAGE0_PATH));
    }

    @Test
    protected void testInheritedPageImage_pageImageAltValueFromResource_withFileResource() {
        Image image = getImageUnderTest(PAGE3_IMAGE0_PATH);
        assertEquals("/core/content/test_page3/_jcr_content/root/page3_image0.coreimg.png", image.getSrc(), "getSrc()");
        assertEquals("featured image alt", image.getAlt(), "getAlt()");
        assertNull(image.getFileReference(), "getFileReference()");
        assertNull(image.getUuid(), "getUuid()");
        assertEquals("image-96253254e2", image.getId(), "getId()");
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE3_IMAGE0_PATH));
    }

    @Test
    protected void testInheritedPageImage_withLink() {
        Image image = getImageUnderTest(PAGE0_IMAGE2_PATH);
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE0_IMAGE2_PATH));
    }

    @Test
    protected void testInheritedPageImage_withWrongLink() {
        Image image = getImageUnderTest(PAGE0_IMAGE3_PATH);
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, PAGE0_IMAGE3_PATH));
    }

    @Test
    protected void testInheritedPageImage_fromTemplate_noLink() {
        Image image = getImageUnderTest(TEMPLATE_IMAGE_INHERITED_PATH1);
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_INHERITED_PATH1));
    }

    @Test
    protected void testInheritedPageImage_fromTemplate_withLink() {
        Image image = getImageUnderTest(TEMPLATE_IMAGE_INHERITED_PATH2);
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, TEMPLATE_IMAGE_INHERITED_PATH2));
    }

    @Test
    void testDMWithEncoding() {
        context.contentPolicyMapping(com.adobe.cq.wcm.core.components.internal.models.v3.ImageImpl.RESOURCE_TYPE, Image.PN_DESIGN_DYNAMIC_MEDIA_ENABLED, true);
        Image image = getImageUnderTest(IMAGE42_PATH);
        assertTrue(image.isDmImage());
        assertEquals("https://s7d9.scene7.com/is/image/dmtestcompany/Adobe%20Systems%20logo%20and%20wordmark%20DM?ts=1490005239000&dpr=off", image.getSrc());
        Utils.testJSONExport(image, Utils.getTestExporterJSONPath(testBase, IMAGE42_PATH));
    }


    @Test
    void testSrcSetWithAssetDeliveryEnabledWithoutSmartSizes() {
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE0_PATH);
        assertEquals(null , image.getSrcset());
    }

    @Test
    void testSrcSetWithAssetDeliveryEnabledWithSmartSizes() {
        registerAssetDelivery();
        String escapedResourcePath = IMAGE0_PATH.replace("jcr:content", "_jcr_content");
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true,
            "allowedRenditionWidths", new int[]{600, 800});
        Image image = getImageUnderTest(IMAGE0_PATH);
        String expectedSrcSet = MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=600&quality=82&preferwebp=true 600w," +
                                MockAssetDelivery.BASE_URL + IMAGE_FILE_REFERENCE + "." + ASSET_NAME  + ".png?width=800&quality=82&preferwebp=true 800w";
        assertEquals(expectedSrcSet , image.getSrcset());
    }

    @Test
    void testAssetDeliveryServiceWithoutFileReference() {
        String escapedResourcePath = IMAGE27_PATH.replace("jcr:content", "_jcr_content");
        registerAssetDelivery();
        context.contentPolicyMapping(resourceType,
            "enableAssetDelivery", true);
        Image image = getImageUnderTest(IMAGE27_PATH);
        assertEquals(CONTEXT_PATH + escapedResourcePath + "." + selector + ".png/1490005239000" + ".png", image.getSrc());
    }
}
