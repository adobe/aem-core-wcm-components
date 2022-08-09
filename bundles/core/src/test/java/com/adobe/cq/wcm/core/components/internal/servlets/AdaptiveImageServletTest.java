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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.commons.metrics.Timer;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.internal.models.v1.ImageImpl;
import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.handler.store.AssetStore;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.image.Layer;
import com.google.common.net.HttpHeaders;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class AdaptiveImageServletTest extends AbstractImageTest {

    private static final String TEST_BASE = "/image";

    private AdaptiveImageServlet servlet;
    private static final int ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH = 1280;
    protected static final String IMAGE0_RECTANGLE_PATH = PAGE + "/jcr:content/root/image0r";
    protected static final String IMAGE0_SMALL_PATH = PAGE + "/jcr:content/root/image0s";
    protected static final String PNG_IMAGE_RECTANGLE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark_rectangle.png";
    protected static final String PNG_RECTANGLE_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_RECTANGLE_BINARY_NAME;
    protected static final String PNG_IMAGE_SMALL_BINARY_NAME = "Adobe_Systems_logo_and_wordmark_small.png";
    protected static final String PNG_SMALL_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_SMALL_BINARY_NAME;
    private Logger testLogger;

    @BeforeEach
    void setUp() throws Exception {
        internalSetUp(TEST_BASE);
        context.load().binaryFile("/image/" + PNG_IMAGE_RECTANGLE_BINARY_NAME, PNG_RECTANGLE_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_RECTANGLE_ASSET_PATH +
            "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        context.load().binaryFile("/image/" + PNG_IMAGE_SMALL_BINARY_NAME, PNG_SMALL_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_SMALL_ASSET_PATH +
            "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        resourceResolver = context.resourceResolver();
        AssetHandler assetHandler = mock(AssetHandler.class);
        AssetStore assetStore = mock(AssetStore.class);
        AdaptiveImageServletMetrics metrics = mock(AdaptiveImageServletMetrics.class);
        when(metrics.startDurationRecording()).thenReturn(mock(Timer.Context.class));
        when(assetStore.getAssetHandler(anyString())).thenReturn(assetHandler);
        IIORegistry registry = IIORegistry.getDefaultInstance();
        Iterator iter = registry.getServiceProviders(ImageReaderSpi.class, true);
        // Force ImageIO to pick TwelveMonkeys plugins for JPG and TIFF
        while (iter.hasNext()) {
            ImageReaderSpi provider = (ImageReaderSpi) iter.next();
            if (!provider.getClass().getName().contains("twelvemonkeys") &&
                    ((Arrays.stream(provider.getFormatNames()).filter(f -> "JPG".equalsIgnoreCase(f)).count() > 0) ||
                            (Arrays.stream(provider.getFormatNames()).filter(f -> "TIFF".equalsIgnoreCase(f)).count() > 0))) {
                registry.deregisterServiceProvider(provider);
            }
        }
        when(assetHandler.getImage(any(Rendition.class))).thenAnswer(invocation -> {
            Rendition rendition = invocation.getArgument(0);
            return ImageIO.read(rendition.getStream());
        });
        servlet = new AdaptiveImageServlet(mockedMimeTypeService, assetStore, metrics, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, AdaptiveImageServlet.DEFAULT_MAX_SIZE);
        testLogger = Utils.mockLogger(AdaptiveImageServlet.class, "LOGGER");
    }

    @AfterEach
    void tearDown() {
        resourceResolver = null;
        servlet = null;
    }

    @Test
    void testRequestWithWidthDesignAllowed() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_PATH,
                "img.90.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"600","700","800"}, "jpegQuality",
                90);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(800, 800);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedRectangleSmall() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_RECTANGLE_PATH,
            "img.82.500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"500", "1000", "1500", "3000"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects downscaling of web rendition
        Dimension expectedDimension = new Dimension(500, 400);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedRectangleMedium() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_RECTANGLE_PATH,
            "img.82.1500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"500", "1000", "1500", "3000"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects downscaling of original rendition
        Dimension expectedDimension = new Dimension(1500, 1200);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedRectangleLarge() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_RECTANGLE_PATH,
            "img.82.3000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"500", "1000", "1500", "3000"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects original
        Dimension expectedDimension = new Dimension(2500, 2000);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedSmallImageWithLargeRenditionSmall() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_SMALL_PATH,
            "img.82.100", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"100", "500", "1500"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects downscaling of original
        Dimension expectedDimension = new Dimension(100, 100);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedSmallImageWithLargeRenditionMedium() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_SMALL_PATH,
            "img.82.500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"100", "500", "1500"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects downscaling of web rendition
        Dimension expectedDimension = new Dimension(500, 500);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignAllowedSmallImageWithLargeRenditionLarge() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE0_SMALL_PATH,
            "img.82.1500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
            "allowedRenditionWidths", new String[] {"100", "500", "1500"},
            "jpegQuality", 82);
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        // Expects no upscaling of web rendition
        Dimension expectedDimension = new Dimension(192, 192);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testRequestWithWidthDesignNotAllowed() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.1000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"600","700","800"});
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus(), "Expected a 404 response when the design does not allow the requested width to be rendered.");
        Assertions.assertArrayEquals(new byte[0], response.getOutput(), "Expected an empty response output.");
    }

    @Test
    void testRequestWithWidthNoDesign() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus(), "Expected a 404 response when the request contains width information but no content policy has been defined.");
        Assertions.assertArrayEquals(new byte[0], response.getOutput(), "Expected an empty response output.");
    }

    @Test
    void testRequestNoWidthWithDesign() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = spy(requestResponsePair.getRight());
        servlet.doGet(request, response);
        verify(response).setContentType("image/png");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered with the default resize configuration width.");
    }


    @Test
    void testRequestNoWidthNoDesign() throws Exception {
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
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered with the default resize configuration width.");
    }

    @Test
    void testWrongNumberOfSelectors() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.1.2.3", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus(), "Expected a 404 response when the request has more selectors than expected.");
        Assertions.assertArrayEquals(new byte[0], response.getOutput(), "Expected an empty response output.");
    }

    @Test
    void testInvalidWidthSelector() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.full", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus(), "Expected a 404 response when the request has an invalid width selector.");
        Assertions.assertArrayEquals(new byte[0], response.getOutput(), "Expected an empty response output.");
    }

    @Test
    void testWithInvalidDesignWidth() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE1_PATH, "img.700", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"600","700","invalid"});
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(700, 700);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
        verify(testLogger, times(1)).warn("One of the configured widths ({}) is not a " +
                "valid Integer.", "invalid");
    }

    @Test
    void testWithInvalidFileReference() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE2_PATH, "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus(), "Expected a 404 response when the image does not have a valid file reference.");
        Assertions.assertArrayEquals(new byte[0], response.getOutput(), "Expected an empty response output.");
    }

    @Test
    void testGIFFileDirectStream() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE5_PATH, 1489998822138L, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.gif");
        Assertions.assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    void testSVGFileDirectStream() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE24_PATH, 1489998822138L, "img", "svg");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertTrue(response.getHeader(HttpHeaders.CONTENT_DISPOSITION).startsWith("attachment"));
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.svg");
        Assertions.assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    void testGIFFileBrowserCached() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE5_PATH, 1489998822138L, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        // 1 millisecond less than the jcr:lastModified value from test-conf.json
        request.addDateHeader("If-Modified-Since", 1489998822137L);
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    void testGIFUploadedToDAM() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE6_PATH, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertTrue(response.getHeader(HttpHeaders.CONTENT_DISPOSITION).startsWith("inline"));
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.gif");
        Assertions.assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    void testSVGUploadedToDAM() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE25_PATH, "img", "svg");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertTrue(response.getHeader(HttpHeaders.CONTENT_DISPOSITION).startsWith("attachment"));
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        InputStream directStream =
                this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.svg");
        Assertions.assertTrue(IOUtils.contentEquals(stream, directStream));
    }

    @Test
    void testGIFUploadedToDAMBrowserCached() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE6_PATH, "img", "gif");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        // 1 millisecond less than the jcr:lastModified value from test-conf.json
        request.addDateHeader("If-Modified-Since", 1490005239001L);
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    void testCorrectScalingPNGAssetWidth() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"2000"});
        servlet.doGet(request, response);
        InputStream directStream = this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png");
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        Assertions.assertTrue(IOUtils.contentEquals(directStream, stream),
            "Expected to get the original asset back, since the requested width is equal to the image's width.");
    }

    @Test
    void testDAMFileUpscaledPNG() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2500", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"2500"});
        servlet.doGet(request, response);
        InputStream directStream = this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png");
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        Assertions.assertTrue(IOUtils.contentEquals(directStream, stream),
            "Expected to get the original asset back, since the requested width would result in upscaling the image.");
    }

    @Test
    void testPNGFileDirectStream() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, "img.600", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"600"});
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(stream);
        Assertions.assertEquals(600, image.getWidth());
        Assertions.assertEquals(600, image.getHeight());
    }

    @Test
    void testWithNoImageNameResourceTypeImage() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(
                IMAGE26_PATH, "coreimg.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"800"});
        requestPathInfo.setSuffix("/1494867377756.png");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
    }

    @Test
    void testWithNoImageNameFromTemplate() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(
                PAGE, "coreimg", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();

        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + "/1490005239000.png");
        servlet.doGet(request, response);
        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
    }

    @Test
    void testImageFileWithNegativeRequestedWidth() throws Exception {
        testNegativeRequestedWidth(IMAGE7_PATH);
    }

    @Test
    void testDAMAssetWithNegativeRequestedWidth() throws Exception {
        testNegativeRequestedWidth(IMAGE8_PATH);
    }

    @Test
    void testDAMAssetCropScaling() throws Exception {
        testCropScaling(IMAGE9_PATH, 1440, 1390, 515);
    }

    @Test
    void testDAMAssetCrop() throws Exception {
        testCropScaling(IMAGE11_PATH, 1440, 1440, 1440);
    }

    @Test
    void testDAMAssetCropScalingWithRotation() throws Exception {
        testCropScaling(IMAGE13_PATH, 1440, 515, 1390);
    }

    @Test
    void testImageFileCropScaling() throws Exception {
        testCropScaling(IMAGE10_PATH, 1440, 1390, 515);
    }

    @Test
    void testImageFileCrop() throws Exception {
        testCropScaling(IMAGE12_PATH, 1440, 1440, 1440);
    }

    @Test
    void testImageFileCropScalingWithRotation() throws Exception {
        testCropScaling(IMAGE14_PATH, 1440, 515, 1390);
    }

    @Test
    void testImageWithCorrectLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"800"});
        servlet.doGet(request, response);
        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("Mon, 20 Mar 2017 10:20:39 GMT", response.getHeader(HttpConstants.HEADER_LAST_MODIFIED));
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(800, 800);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testImageWithIncorrectLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "coreimg.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("/42.png");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(302, response.getStatus(), "Expected a 302 response code.");
        Assertions.assertEquals(CONTEXT_PATH + "/content/test/jcr%3acontent/root/image19.coreimg.800.png/1490005239000.png", response.getHeader("Location"),
            "Expected redirect location with correct last modified suffix");
    }

    @Test
    void testImageWithMissingLastModifiedSuffix() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE19_PATH,
                "coreimg.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("");
        servlet.doGet(request, response);
        Assertions.assertEquals(302, response.getStatus(), "Expected a 302 response code.");
        Assertions.assertEquals(CONTEXT_PATH + "/content/test/jcr%3acontent/root/image19.coreimg.800.png/1490005239000.png", response.getHeader("Location"),
            "Expected redirect location with correct last modified suffix");
    }

    @Test
    void testFileReferencePriority() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE20_PATH,
                "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");
    }

    @Test
    void testFileReferenceNonexistingAsset() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE30_PATH,
                "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    void testDifferentSuffixExtension() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, 1490005239000L, "img.600", "png", "jpeg");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(requestResponsePair.getLeft(), response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    void testInvalidSuffix() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE3_PATH, "img.600", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockRequestPathInfo rpi = (MockRequestPathInfo) request.getRequestPathInfo();
        rpi.setSuffix("/random");
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());

        rpi.setSuffix("/1");
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());

        rpi.setSuffix("/1.");
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    void testImageFromTemplateStructureNodeNoLastModifiedInfo() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "coreimg",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();

        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + ".png");
        servlet.doGet(request, response);
        Assertions.assertEquals(302, response.getStatus(), "Expected a 302 response code.");
        Assertions.assertEquals(
            CONTEXT_PATH + "/content/test.coreimg.png/structure/jcr%3acontent/root/image_template/1490005239000.png",
            response.getHeader("Location"),
            "Expected redirect location with correct last modified suffix");
    }

    @Test
    void testImageFromTemplateStructureNodeOldLastModifiedInfo() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "coreimg",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();

        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + "/1490005238000.png");
        servlet.doGet(request, response);
        Assertions.assertEquals(302, response.getStatus(), "Expected a 302 response code.");
        Assertions.assertEquals(
            CONTEXT_PATH + "/content/test.coreimg.png/structure/jcr%3acontent/root/image_template/1490005239000.png",
            response.getHeader("Location"),
            "Expected redirect location with correct last modified suffix");
    }

    @Test
    void testImageFromTemplateStructureNode() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(PAGE, "img",
                "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(TEMPLATE_IMAGE_PATH.replace(TEMPLATE_PATH, "") + "/1490005239000.png");
        servlet.doGet(request, response);
        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH, ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
        Assertions.assertEquals("image/png", response.getContentType(), "Expected a PNG image.");

    }

    @Test
    void testHorizontalAndVerticalFlipWithDAMAsset() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE22_PATH, 1494867377756L, "img.2000", "png");
        testHorizontalAndVerticalFlip(requestResponsePair);
    }

    @Test
    void testHorizontalAndVerticalFlipWithImageFile() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE23_PATH, 1494867377756L, "img.2000", "png");
        testHorizontalAndVerticalFlip(requestResponsePair);

    }

    @Test
    void testImageTooLarge() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE28_PATH,
                "img", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        Assertions.assertThrows(
                IOException.class,
                () -> servlet.doGet(request, response),
                "Expecting to throw an exception complaining that dimensions are too large");
    }

    @Test
    void testTransparentImage() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair = prepareRequestResponsePair(IMAGE29_PATH,
                1607501105000L, "coreimg.80.1500", "jpeg");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE,
                Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[] {"1500"},
                Image.PN_DESIGN_JPEG_QUALITY, 80);
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Layer responseImage = new Layer(new ByteArrayInputStream(response.getOutput()));
        Assertions.assertEquals(1500, responseImage.getWidth());
        for (int x = 0; x < responseImage.getWidth(); x++) {
            for (int y = 0; y < responseImage.getHeight(); y++) {
                Assertions.assertEquals(Color.white.getRGB(), responseImage.getPixel(x, y), "Expected white background");
            }
        }
    }

    @Test
    void testSelectRenditionWithSmallerWidthAndLargerSize() throws IOException {
        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/png");
        Rendition jpeg750px = mockRendition(mockAsset, "750px", 1000000, "image/jpeg", 750, 750);
        Rendition jpeg900px = mockRendition(mockAsset, "900px", 100000, "image/jpeg", 900, 900);
        Rendition jpeg100px = mockRendition(mockAsset, "100px", 100, "image/jpeg", 100, 100);
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<Rendition>() {{
            // larger size but best fit for width
            add(jpeg750px);
            // smaller size but not best fit for width
            add(jpeg900px);
            add(jpeg100px);
        }});
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/jpeg", 2000, 2000);
        when(mockAsset.getOriginal()).thenReturn(original);

        EnhancedRendition best = servlet.getBestRendition(mockAsset, 700, mockAsset.getMimeType());

        Assertions.assertEquals(750, best.getDimension().width);
    }

    @Test
    void testSelectRenditionWithSameMimeType() throws IOException {
        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/png");
        Rendition jpeg750px = mockRendition(mockAsset, "750px", 1000000, "image/jpeg", 750, 750);
        Rendition png850px = mockRendition(mockAsset, "750px", 1000000, "image/png", 850, 850);
        Rendition jpeg900px = mockRendition(mockAsset, "900px", 100000, "image/jpeg", 900, 900);
        Rendition jpeg100px = mockRendition(mockAsset, "100px", 100, "image/jpeg", 100, 100);
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<Rendition>() {{
            // larger size but best fit for width
            add(jpeg750px);
            add(png850px);
            // smaller size but not best fit for width
            add(jpeg900px);
            add(jpeg100px);
        }});
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/jpeg", 2000, 2000);
        when(mockAsset.getOriginal()).thenReturn(original);

        EnhancedRendition best = servlet.getBestRendition(mockAsset, 700, mockAsset.getMimeType());

        Assertions.assertEquals(850, best.getDimension().width);
    }

    @Test
    void testGetBestRenditionForTiffRenderedAsJpeg() throws IOException {
        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        Rendition jpeg750px = mockRendition(mockAsset, "750px", 1000000, "image/jpeg", 750, 750);
        Rendition jpeg850px = mockRendition(mockAsset, "850px", 2000000, "image/jpeg", 850, 850);
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<Rendition>() {{
            add(jpeg750px);
            add(jpeg850px);
        }});
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/jpeg", 2000, 2000);
        when(mockAsset.getOriginal()).thenReturn(original);

        EnhancedRendition best = servlet.getBestRendition(mockAsset, 1500, "image/jpeg");

        Assertions.assertEquals(850, best.getDimension().width);
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithJpegRenditions() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        Rendition smallerJpeg = mockRendition(mockAsset, "750px", 1000000, "image/jpeg", 750, 750);
        Rendition largerJpeg = mockRendition(mockAsset, "850px", 2000000, "image/jpeg", 850, 850);
        when(largerJpeg.getStream()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.850.jpg"));
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<Rendition>() {{
            add(smallerJpeg);
            add(largerJpeg);
        }});
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 2000, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/jpeg", response.getContentType(), "Expected a JPG image.");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(850, 850);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithJpegRenditionsWithResize() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.800", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        Rendition smallerJpeg = mockRendition(mockAsset, "750px", 1000000, "image/jpeg", 750, 750);
        Rendition largerJpeg = mockRendition(mockAsset, "850px", 2000000, "image/jpeg", 850, 850);
        when(largerJpeg.getStream()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.850.jpg"));
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<Rendition>() {{
            add(smallerJpeg);
            add(largerJpeg);
        }});
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 800, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/jpeg", response.getContentType(), "Expected a JPG image.");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(800, 800);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithoutJpegRenditions() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<>());
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(original.getStream()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.tiff"));
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 2000, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/jpeg", response.getContentType(), "Expected a JPG image.");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(2000, 2000);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithoutJpegRenditionsNoResize() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<>());
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(original.getStream()).thenReturn(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.tiff"));
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 0, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/jpeg", response.getContentType(), "Expected a JPG image.");

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(2000, 2000);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        Assertions.assertEquals(expectedDimension, actualDimension, "Expected image rendered at requested size.");
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithoutJpegRenditionsAndUnableToProcess() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<>());
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(original.getStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 0, 1, 2 }),
                new ByteArrayInputStream(new byte[]{ 0, 1, 2 }));
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 2000, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/tiff", response.getContentType(), "Expected a TIFF image.");

        Assertions.assertEquals(3, response.getOutput().length, "Expected three-byte original TIFF");
    }

    @Test
    void testTransformAndStreamAssetForTiffRenderedAsJpegWithoutJpegRenditionsAndUnableToProcessWithResize() throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();

        Asset mockAsset = mock(Asset.class);
        when(mockAsset.getMimeType()).thenReturn("image/tiff");
        when(mockAsset.getRenditions()).thenReturn(new LinkedList<>());
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGEWIDTH)).thenReturn("2000");
        when(mockAsset.getMetadataValue(DamConstants.TIFF_IMAGELENGTH)).thenReturn("2000");
        Rendition original = mockRendition(mockAsset, "original", 9999999, "image/tiff", 2000, 2000);
        when(original.getStream()).thenReturn(new ByteArrayInputStream(new byte[]{ 0, 1, 2 }),
                new ByteArrayInputStream(new byte[]{ 0, 1, 2 }), new ByteArrayInputStream(new byte[]{ 0, 1, 2 }));
        when(mockAsset.getOriginal()).thenReturn(original);

        servlet.transformAndStreamAsset(response, new MockValueMap(request.getResource(), new HashMap<>()), 800, 90, mockAsset, "image/jpeg", "test");

        Assertions.assertEquals(200, response.getStatus(), "Expected a 200 response code.");
        Assertions.assertEquals("image/tiff", response.getContentType(), "Expected a TIFF image.");

        Assertions.assertEquals(3, response.getOutput().length, "Expected three-byte original TIFF");
    }

    @Test
    void testStaticDesignWidthAndQuality() {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE0_PATH, "img.2000", "png");
        Resource mockResource = mock(Resource.class);
        ResourceResolver mockResourceResolver = mock(ResourceResolver.class);
        when(mockResource.getResourceResolver()).thenReturn(mockResourceResolver);
        Designer mockDesigner = mock(Designer.class);
        when(mockResourceResolver.adaptTo(Designer.class)).thenReturn(mockDesigner);
        Style mockStyle = mock(Style.class);
        when(mockDesigner.getStyle(mockResource)).thenReturn(mockStyle);
        String[] configuredWidths = { "400", "600", "800"};
        when(mockStyle.get(Image.PN_DESIGN_ALLOWED_RENDITION_WIDTHS, new String[0])).thenReturn(configuredWidths);
        List<Integer> allowedWidths = servlet.getAllowedRenditionWidths(mockResource, requestResponsePair.getLeft());
        Assertions.assertEquals(
                Arrays.stream(configuredWidths).map(Integer::valueOf).sorted().collect(Collectors.toList()),
                allowedWidths);

        int configuredQuality = 75;
        when(mockStyle.get(Image.PN_DESIGN_JPEG_QUALITY, AdaptiveImageServlet.DEFAULT_JPEG_QUALITY)).thenReturn(configuredQuality);
        Integer allowedQuality = servlet.getAllowedJpegQuality(mockResource, requestResponsePair.getLeft());
        Assertions.assertEquals(configuredQuality, allowedQuality.intValue());
    }

    private Rendition mockRendition(Asset asset, String name, long size, String mimeType, int width, int length) {
        Rendition rendition = mock(Rendition.class);
        when(rendition.getMimeType()).thenReturn(mimeType);
        when(rendition.getName()).thenReturn(name);
        ValueMap mockProperties = mock(ValueMap.class);
        when(mockProperties.containsKey(DamConstants.TIFF_IMAGEWIDTH)).thenReturn(true);
        when(mockProperties.get(DamConstants.TIFF_IMAGEWIDTH, String.class)).thenReturn(Integer.toString(width));
        when(mockProperties.containsKey(DamConstants.TIFF_IMAGELENGTH)).thenReturn(true);
        when(mockProperties.get(DamConstants.TIFF_IMAGELENGTH, String.class)).thenReturn(Integer.toString(length));
        when(rendition.getProperties()).thenReturn(mockProperties);
        when(rendition.getSize()).thenReturn(size);
        when(rendition.getAsset()).thenReturn(asset);
        return rendition;
    }

    private void testNegativeRequestedWidth(String imagePath) throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(imagePath, "img.-1", "png");
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"-1"});
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    private void testCropScaling(String imagePath, int requestedWidth, int expectedWidth, int expectedHeight) throws IOException {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(imagePath, "img." + requestedWidth, "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"1440"});
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        Assertions.assertEquals(expectedWidth, image.getWidth(), "Expected the cropped rectangle to have a 1390px width, since the servlet should not perform cropping upscaling.");
        Assertions.assertEquals(expectedHeight, image.getHeight(), "Expected the cropped rectangle to have a 515px height, since the servlet should not perform cropping upscaling.");
    }

    private void testHorizontalAndVerticalFlip(Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair) throws
            IOException {
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        context.contentPolicyMapping(ImageImpl.RESOURCE_TYPE, "allowedRenditionWidths", new String[] {"2000"});
        servlet.doGet(request, response);
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus(), "Expected a 200 response.");
        Layer layer = new Layer(this.getClass().getClassLoader().getResourceAsStream("image/Adobe_Systems_logo_and_wordmark.png"));
        layer.flipHorizontally();
        layer.flipVertically();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        layer.write(StandardImageHandler.PNG1_MIMETYPE, 1.0, outputStream);
        Assertions.assertArrayEquals(outputStream.toByteArray(), response.getOutput());
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
                new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext());
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
        bindings.put(SlingBindings.SLING, context.slingScriptHelper());
        bindings.put(SlingBindings.RESOLVER, resourceResolver);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return new RequestResponsePair(request, response);
    }

    private static class RequestResponsePair extends Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> {

        private final MockSlingHttpServletRequest request;
        private final MockSlingHttpServletResponse response;

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
