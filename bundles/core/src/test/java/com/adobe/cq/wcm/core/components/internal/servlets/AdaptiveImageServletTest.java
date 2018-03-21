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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.handler.store.AssetStore;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.day.image.Layer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AdaptiveImageServletTest extends AbstractImageTest {

    private static String TEST_BASE = "/image";

    private AdaptiveImageServlet servlet;
    private static final int ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH = 1280;

    @BeforeClass
    public static void setUp() {
        internalSetUp(CONTEXT, TEST_BASE);
    }

    @Before
    public void init() throws IOException {
        resourceResolver = CONTEXT.resourceResolver();
        AssetHandler assetHandler = mock(AssetHandler.class);
        AssetStore assetStore = mock(AssetStore.class);
        when(assetStore.getAssetHandler(anyString())).thenReturn(assetHandler);
        when(assetHandler.getImage(any(Rendition.class))).thenAnswer(invocation -> {
            Rendition rendition = invocation.getArgumentAt(0, Rendition.class);
            return ImageIO.read(rendition.getStream());
        });
        servlet = new AdaptiveImageServlet(mockedMimeTypeService, assetStore, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
    }

    @After
    public void tearDown() {
        resourceResolver = null;
        servlet = null;
    }

    @Test
    public void testRequestWithWidthDesignAllowed() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_PATH,
                "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(800, 800);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered at requested size.", expectedDimension, actualDimension);
        assertEquals("Expected a PNG image.", "image/png", response.getContentType());
    }

    @Test
    public void testRequestWithWidthDesignNotAllowed() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.1000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals("Expected a 404 response when the design does not allow the requested width to be rendered.", HttpServletResponse
                .SC_NOT_FOUND, response.getStatus());
        assertArrayEquals("Expected an empty response output.", new byte[0], response.getOutput());
    }

    @Test
    public void testRequestWithWidthNoDesign() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        assertEquals("Expected a 404 response when the request contains width information but no content policy has been defined.",
                HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertArrayEquals("Expected an empty response output.", new byte[0], response.getOutput());
    }

    @Test
    public void testRequestNoWidthWithDesign() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = spy(requestResponsePair.getRight());
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        verify(response).setContentType("image/png");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered with the default resize configuration width.", expectedDimension, actualDimension);
    }


    @Test
    public void testRequestNoWidthNoDesign() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = spy(requestResponsePair.getRight());
        servlet.doGet(request, response);
        verify(response).setContentType("image/png");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension =
                new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered with the default resize configuration width.", expectedDimension, actualDimension);
    }

    @Test
    public void testWrongNumberOfSelectors() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.1.1", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        assertEquals("Expected a 404 response when the request has more selectors than expected.",
                HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertArrayEquals("Expected an empty response output.", new byte[0], response.getOutput());
    }

    @Test
    public void testInvalidWidthSelector() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.full", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        assertEquals("Expected a 404 response when the request has an invalid width selector.",
                HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertArrayEquals("Expected an empty response output.", new byte[0], response.getOutput());
    }

    @Test
    public void testWithInvalidDesignWidth() throws Exception {
        Logger logger = spy(LoggerFactory.getLogger("FakeLogger"));
        setFinalStatic(AdaptiveImageServlet.class.getDeclaredField("LOGGER"), logger);
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE1_PATH, "img.700", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(700, 700);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered at requested size.", expectedDimension, actualDimension);
        assertEquals("Expected a PNG image.", "image/png", response.getContentType());
        verify(logger).warn(
                "One of the configured widths ({}) from the {} content policy is not a valid Integer.",
                "invalid",
                "/conf/coretest/settings/wcm/policies/coretest/components/content/image/policy_1478854677327"
        );
    }

    @Test
    public void testWithInvalidFileReference() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE2_PATH, "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        assertEquals("Expected a 404 response when the image does not have a valid file reference.",
                HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        assertArrayEquals("Expected an empty response output.", new byte[0], response.getOutput());
    }

    @Test
    public void testGIFFileDirectStream() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE5_PATH, 1489998822138L, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.gif");
        assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    public void testGIFFileBrowserCached() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE5_PATH, 1489998822138L, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        // 1 millisecond less than the jcr:lastModified value from test-conf.json
        request.addDateHeader("If-Modified-Since", 1489998822137L);
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    public void testGIFUploadedToDAM() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE6_PATH, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.gif");
        assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    public void testGIFUploadedToDAMBrowserCached() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE6_PATH, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        // 1 millisecond less than the jcr:lastModified value from test-conf.json
        request.addDateHeader("If-Modified-Since", 1490005239001L);
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    public void testCorrectScalingPNGAssetWidth() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        InputStream directStream = this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png");
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        assertTrue("Expected to get the original asset back, since the requested width is equal to the image's width.",
                IOUtils.contentEquals(directStream, stream));
    }

    @Test
    public void testDAMFileUpscaledPNG() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        InputStream directStream = this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png");
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        assertTrue("Expected to get the original asset back, since the requested width would result in upscaling the image.",
                IOUtils.contentEquals(directStream, stream));
    }

    @Test
    public void testPNGFileDirectStream() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, "img.600", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(stream);
        assertEquals(600, image.getWidth());
        assertEquals(600, image.getHeight());
    }

    @Test
    public void testImageFileWithNegativeRequestedWidth() throws Exception {
        testNegativeRequestedWidth(IMAGE7_PATH);
    }

    @Test
    public void testDAMAssetWithNegativeRequestedWidth() throws Exception {
        testNegativeRequestedWidth(IMAGE8_PATH);
    }

    @Test
    public void testDAMAssetCropScaling() throws Exception {
        testCropScaling(IMAGE9_PATH, 1440, 1390, 515);
    }

    @Test
    public void testDAMAssetCrop() throws Exception {
        testCropScaling(IMAGE11_PATH, 1440, 1440, 1440);
    }

    @Test
    public void testDAMAssetCropScalingWithRotation() throws Exception {
        testCropScaling(IMAGE13_PATH, 1440, 515, 1390);
    }

    @Test
    public void testImageFileCropScaling() throws Exception {
        testCropScaling(IMAGE10_PATH, 1440, 1390, 515);
    }

    @Test
    public void testImageFileCrop() throws Exception {
        testCropScaling(IMAGE12_PATH, 1440, 1440, 1440);
    }

    @Test
    public void testImageFileCropScalingWithRotation() throws Exception {
        testCropScaling(IMAGE14_PATH, 1440, 515, 1390);
    }

    @Test
    public void testImageWithCorrectLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals("Expected a 200 response code.", 200, response.getStatus());
        assertEquals("Mon, 20 Mar 2017 10:20:39 GMT", response.getHeader(HttpConstants.HEADER_LAST_MODIFIED));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(800, 800);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered at requested size.", expectedDimension, actualDimension);
        assertEquals("Expected a PNG image.", "image/png", response.getContentType());
    }

    @Test
    public void testImageWithIncorrectLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "coreimg.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("/42.png");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals("Expected a 302 response code.", 302, response.getStatus());
        assertEquals("Expected redirect location with correct last modified suffix",
                CONTEXT_PATH + "/content/test/jcr%3acontent/root/image19.coreimg.800.png/1490005239000.png", response.getHeader("Location"));
    }

    @Test
    public void testImageWithMissingLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "coreimg.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("");
        servlet.doGet(request, response);
        assertEquals("Expected a 302 response code.", 302, response.getStatus());
        assertEquals("Expected redirect location with correct last modified suffix",
                CONTEXT_PATH + "/content/test/jcr%3acontent/root/image19.coreimg.800.png/1490005239000.png", response.getHeader("Location"));
    }

    @Test
    public void testFileReferencePriority() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE20_PATH,
                "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered at requested size.", expectedDimension, actualDimension);
        assertEquals("Expected a PNG image.", "image/png", response.getContentType());
    }

    @Test
    public void testDifferentSuffixExtension() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, 1490005239000L, "img.600", "png", "jpeg");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(requestResponsePair.getLeft(), response);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testInvalidSuffix() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, "img.600", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockRequestPathInfo rpi = (MockRequestPathInfo) request.getRequestPathInfo();
        rpi.setSuffix("/random");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());

        rpi.setSuffix("/1");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());

        rpi.setSuffix("/1.");
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testImageFromTemplateStructureNodeNoLastModifiedInfo() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "coreimg",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();

        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + ".png");
        servlet.doGet(request, response);
        assertEquals("Expected a 302 response code.", 302, response.getStatus());
        assertEquals("Expected redirect location with correct last modified suffix",
                CONTEXT_PATH + "/content/test.coreimg.png/structure/jcr%3acontent/root/image_template/1490005239000.png",
                response.getHeader("Location"));
    }

    @Test
    public void testImageFromTemplateStructureNodeOldLastModifiedInfo() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "coreimg",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();

        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + "/1490005238000.png");
        servlet.doGet(request, response);
        assertEquals("Expected a 302 response code.", 302, response.getStatus());
        assertEquals("Expected redirect location with correct last modified suffix",
                CONTEXT_PATH + "/content/test.coreimg.png/structure/jcr%3acontent/root/image_template/1490005239000.png",
                response.getHeader("Location"));
    }

    @Test
    public void testImageFromTemplateStructureNode() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "img",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + "/1490005239000.png");
        servlet.doGet(request, response);
        assertEquals("Expected a 200 response code.", 200, response.getStatus());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered at requested size.", expectedDimension, actualDimension);
        assertEquals("Expected a PNG image.", "image/png", response.getContentType());

    }

    @Test
    public void testHorizontalAndVerticalFlipWithDAMAsset() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE22_PATH, 1494867377756L, "img.2000", "png");
        testHorizontalAndVerticalFlip(requestResponsePair);
    }

    @Test
    public void testHorizontalAndVerticalFlipWithImageFile() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE23_PATH, 1494867377756L, "img.2000", "png");
        testHorizontalAndVerticalFlip(requestResponsePair);

    }

    private void testNegativeRequestedWidth(String imagePath) throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(imagePath, "img.-1", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    private void testCropScaling(String imagePath, int requestedWidth, int expectedWidth, int expectedHeight) throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(imagePath, "img." + requestedWidth, "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 1390px width, since the servlet should not perform cropping upscaling.",
                expectedWidth, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 515px height, since the servlet should not perform cropping upscaling.",
                expectedHeight, image.getHeight());
    }

    private void testHorizontalAndVerticalFlip(Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair) throws
            IOException {
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource(), request)).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals("Expected a 200 response.",
                HttpServletResponse.SC_OK, response.getStatus());
        Layer layer = new Layer(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png"));
        layer.flipHorizontally();
        layer.flipVertically();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        layer.write(StandardImageHandler.PNG1_MIMETYPE, 1.0, outputStream);
        assertArrayEquals(outputStream.toByteArray(), response.getOutput());
    }

    private Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> prepareRequestResponsePair(String resourcePath,
                                                                                                       String selectorString,
                                                                                                       String extension) {
        return prepareRequestResponsePair(resourcePath, 1490005239000L, selectorString, extension);
    }

    private Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> prepareRequestResponsePair(String resourcePath,
                                                                                                       long lastModifiedDate,
                                                                                                       String selectorString,
                                                                                                       String extension) {
        return prepareRequestResponsePair(resourcePath, lastModifiedDate, selectorString, extension, extension);
    }

    private Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> prepareRequestResponsePair(String resourcePath,
                                                                                                       long lastModifiedDate,
                                                                                                       String selectorString,
                                                                                                       String requestExtension,
                                                                                                       String suffixExtension) {
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        final MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
        Resource resource = resourceResolver.getResource(resourcePath);
        request.setResource(resource);
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("/" + lastModifiedDate + "." + suffixExtension);
        requestPathInfo.setSelectorString(selectorString);
        requestPathInfo.setExtension(requestExtension);
        requestPathInfo.setResourcePath(resourcePath);
        request.setContextPath(CONTEXT_PATH);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(SlingBindings.RESPONSE, response);
        bindings.put(SlingBindings.SLING, CONTEXT.slingScriptHelper());
        bindings.put(SlingBindings.RESOLVER, resourceResolver);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return new RequestResponsePair(request, response);
    }

    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private static class RequestResponsePair extends Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> {

        private MockSlingHttpServletRequest request;
        private MockSlingHttpServletResponse response;

        private RequestResponsePair(MockSlingHttpServletRequest request,
                                   MockSlingHttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public MockSlingHttpServletRequest getLeft() {
            return request;
        }

        @Override
        public MockSlingHttpServletResponse getRight() {
            return response;
        }

        @Override
        public MockSlingHttpServletResponse setValue(MockSlingHttpServletResponse value) {
            throw new UnsupportedOperationException();
        }
    }

}
