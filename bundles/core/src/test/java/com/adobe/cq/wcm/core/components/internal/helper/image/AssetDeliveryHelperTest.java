/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

package com.adobe.cq.wcm.core.components.internal.helper.image;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.testing.MockAssetDelivery;
import com.adobe.cq.wcm.spi.AssetDelivery;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(AemContextExtension.class)
public class AssetDeliveryHelperTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private static String TEST_ASSET_RESOURCE_PATH = "/content/dam/test";
    private static String TEST_IMAGE_COMPONENT_PATH = "/content/test/image/resource";
    private static String TEST_SEO_NAME = "test-seo";
    private static String JPEG_EXTENSION = "jpg";
    private static String WIDTH_PLACEHOLDER = "{width}";
    private static Integer JPEG_QUALITY = 80;

    @Test
    public void testSrcWithoutPath() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        assertNull(src);
    }

    @Test
    public void testSrcWithNonExistingAssetResource() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        assertNull(src);
    }

    @Test
    public void testSrcWithoutSeoname() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, "", JPEG_EXTENSION, 200, JPEG_QUALITY);
        assertNull(src);
    }

    @Test
    public void testSrcWithSVGExtension() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, "svg", 200, JPEG_QUALITY);
        assertNull(src);
    }

    @Test
    public void testSrcWithWidthAndQuality() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION + "?" +
            "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithNoWidth() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, null, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." +
            JPEG_EXTENSION + "?" + "quality=" + JPEG_QUALITY + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }


    @Test
    public void testSrcWithCropParameter() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(ImageResource.PN_IMAGE_CROP, "10,20,100,200");
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "c=" + 10 + "," + 20 + "," + 90 + "," + 180 + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithRotationParameter() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(ImageResource.PN_IMAGE_ROTATE, "90");
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "r=" + 90 + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithFlipParameterHorizontal() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(Image.PN_FLIP_HORIZONTAL, true);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "flip=HORIZONTAL" + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithFlipParameterVertical() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(Image.PN_FLIP_VERTICAL, true);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "flip=VERTICAL" + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithFlipParameterBoth() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(Image.PN_FLIP_VERTICAL, true);
        imageResourceProperties.put(Image.PN_FLIP_HORIZONTAL, true);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "flip=HORIZONTAL_AND_VERTICAL" + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcWithCropAndRotateAndFlipParameter() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        imageResourceProperties.put(ImageResource.PN_IMAGE_CROP, "10,20,100,200");
        imageResourceProperties.put(ImageResource.PN_IMAGE_ROTATE, "90");
        imageResourceProperties.put(Image.PN_FLIP_VERTICAL, true);
        imageResourceProperties.put(Image.PN_FLIP_HORIZONTAL, true);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrc(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, 200, JPEG_QUALITY);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION +
            "?" + "width=" + 200 + "&" + "quality=" + JPEG_QUALITY +
            "&" + "c=" + 10 + "," + 20 + "," + 90 + "," + 180 +
            "&" + "r=" + 90 +
            "&" + "flip=HORIZONTAL_AND_VERTICAL" +
            "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcUriTemplateWithWidths() {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrcUriTemplate(assetDelivery, imageComponentResource, TEST_SEO_NAME,
            JPEG_EXTENSION, JPEG_QUALITY, WIDTH_PLACEHOLDER);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL
            + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION
            + "?" + "width=" + WIDTH_PLACEHOLDER
            + "&" + "quality=" + JPEG_QUALITY
            + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcUriTemplateWithNoWidth() {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrcUriTemplate(assetDelivery, imageComponentResource, TEST_SEO_NAME,
            JPEG_EXTENSION, null, WIDTH_PLACEHOLDER);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL
            + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION
            + "?" + "width=" + WIDTH_PLACEHOLDER
            + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcUriTemplateWithWidthTemplateParam() {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String src = AssetDeliveryHelper.getSrcUriTemplate(assetDelivery, imageComponentResource, TEST_SEO_NAME,
            JPEG_EXTENSION, null, WIDTH_PLACEHOLDER);
        String expectedSrcUrl = MockAssetDelivery.BASE_URL
            + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION
            + "?" + "width=" + WIDTH_PLACEHOLDER
            + "&" + "preferwebp=true";
        assertEquals(expectedSrcUrl, src);
    }

    @Test
    public void testSrcSetWithoutMultipleWidths() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String srcSet = AssetDeliveryHelper.getSrcSet(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, new int[]{}, JPEG_QUALITY);
        assertNull(srcSet);
    }

    @Test
    public void testSrcSetWithoutAssetPath() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH);
        String srcSet = AssetDeliveryHelper.getSrcSet(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, new int[]{}, JPEG_QUALITY);
        assertNull(srcSet);
    }


    @Test
    public void testSrcSet() throws Exception {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.create().resource(TEST_ASSET_RESOURCE_PATH);
        Map<String, Object> imageResourceProperties = new HashMap<>();
        imageResourceProperties.put(DownloadResource.PN_REFERENCE, TEST_ASSET_RESOURCE_PATH);
        Resource imageComponentResource = context.create().resource(TEST_IMAGE_COMPONENT_PATH, imageResourceProperties);
        String srcSet = AssetDeliveryHelper.getSrcSet(assetDelivery, imageComponentResource, TEST_SEO_NAME, JPEG_EXTENSION, new int[]{200, 400}, JPEG_QUALITY);
        String expectedSrcSet = MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION + "?" +
                                   "width=" + 200 + "&" + "quality=" + JPEG_QUALITY + "&" + "preferwebp=true" + " 200w"
                                   + "," +
                                   MockAssetDelivery.BASE_URL + TEST_ASSET_RESOURCE_PATH + "." + TEST_SEO_NAME + "." + JPEG_EXTENSION + "?" +
                                   "width=" + 400 + "&" + "quality=" + JPEG_QUALITY + "&" + "preferwebp=true" + " 400w";
        assertEquals(expectedSrcSet, srcSet);
    }
}
