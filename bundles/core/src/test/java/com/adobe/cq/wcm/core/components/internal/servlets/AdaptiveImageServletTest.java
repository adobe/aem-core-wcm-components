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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.handler.AssetHandler;
import com.day.cq.dam.api.handler.store.AssetStore;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AdaptiveImageServletTest extends AbstractImageTest {

    private AdaptiveImageServlet servlet;
    private static final int ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH = 1280;

    @Before
    public void init() throws IOException {
        resourceResolver = aemContext.resourceResolver();
        servlet = new AdaptiveImageServlet();
        Whitebox.setInternalState(servlet, "mimeTypeService", mockedMimeTypeService);
        AssetHandler assetHandler = mock(AssetHandler.class);
        AssetStore assetStore = mock(AssetStore.class);
        when(assetStore.getAssetHandler(anyString())).thenReturn(assetHandler);
        when(assetHandler.getImage(any(Rendition.class))).thenAnswer(new Answer<BufferedImage>() {
            @Override
            public BufferedImage answer(InvocationOnMock invocationOnMock) throws Throwable {
                Rendition rendition = invocationOnMock.getArgumentAt(0, Rendition.class);
                return ImageIO.read(rendition.getStream());
            }
        });
        Whitebox.setInternalState(servlet, "assetStore", assetStore);
        activateServlet(servlet);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        verify(response).setContentType("image/png");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        Dimension expectedDimension = new Dimension(600, 600);
        Dimension actualDimension = new Dimension(image.getWidth(), image.getHeight());
        assertEquals("Expected image rendered using the first width defined in the content policy.", expectedDimension, actualDimension);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
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
                prepareRequestResponsePair(IMAGE5_PATH, "img", "gif");
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
                prepareRequestResponsePair(IMAGE5_PATH, "img", "gif");
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
        request.addDateHeader("If-Modified-Since", 1489998822137L);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
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
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        ByteArrayInputStream stream = new ByteArrayInputStream(response.getOutput());
        BufferedImage image = ImageIO.read(stream);
        assertEquals(600, image.getWidth());
        assertEquals(600, image.getHeight());
    }

    @Test
    public void testImageFileWithNegativeRequestedWidth() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE7_PATH, "img.-1", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testDAMAssetWithNegativeRequestedWidth() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE8_PATH, "img.-1", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testDAMAssetCropScaling() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE9_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 1390px width, since the servlet should not perform cropping upscaling.",
                1390, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 515px height, since the servlet should not perform cropping upscaling.",
                515, image.getHeight());
    }

    @Test
    public void testDAMAssetCrop() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE11_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 1440px width, since the servlet should not perform cropping upscaling.",
                1440, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 1440px height, since the servlet should not perform cropping upscaling.",
                1440, image.getHeight());
    }

    @Test
    public void testDAMAssetCropScalingWithRotation() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE13_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 515px width, since the servlet should not perform cropping upscaling.",
                515, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 1390px height, since the servlet should not perform cropping upscaling.",
                1390, image.getHeight());
    }

    @Test
    public void testImageFileCropScaling() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE10_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 1390px width, since the servlet should not perform cropping upscaling.",
                1390, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 515px height, since the servlet should not perform cropping upscaling.",
                515, image.getHeight());
    }

    @Test
    public void testImageFileCrop() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE12_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 1440px width, since the servlet should not perform cropping upscaling.",
                1440, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 1440px height, since the servlet should not perform cropping upscaling.",
                1440, image.getHeight());
    }

    @Test
    public void testImageFileCropScalingWithRotation() throws Exception {
        Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> requestResponsePair =
                prepareRequestResponsePair(IMAGE14_PATH, "img.1440", "png");
        MockSlingHttpServletRequest request = requestResponsePair.getLeft();
        MockSlingHttpServletResponse response = requestResponsePair.getRight();
        ContentPolicyMapping mapping = request.getResource().adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = mapping.getPolicy();
        when(contentPolicyManager.getPolicy(request.getResource())).thenReturn(contentPolicy);
        servlet.doGet(request, response);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(response.getOutput()));
        assertEquals("Expected the cropped rectangle to have a 515px width, since the servlet should not perform cropping upscaling.",
                515, image.getWidth());
        assertEquals("Expected the cropped rectangle to have a 1390px height, since the servlet should not perform cropping upscaling.",
                1390, image.getHeight());
    }

    private Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse> prepareRequestResponsePair(String resourcePath,
                                                                                                       String selectorString,
                                                                                                       String extension) {
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(aemContext.resourceResolver(), aemContext.bundleContext());
        final MockSlingHttpServletResponse response = new MockSlingHttpServletResponse();
        Resource resource = resourceResolver.getResource(resourcePath);
        request.setResource(resource);
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSelectorString(selectorString);
        requestPathInfo.setExtension(extension);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(SlingBindings.RESPONSE, response);
        bindings.put(SlingBindings.SLING, aemContext.slingScriptHelper());
        bindings.put(SlingBindings.RESOLVER, resourceResolver);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return new Pair<MockSlingHttpServletRequest, MockSlingHttpServletResponse>() {
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
        };
    }

    private void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        // remove final modifier from field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, newValue);
    }

    private void activateServlet(AdaptiveImageServlet servlet) {
        servlet.activate(new AdaptiveImageServlet.Configuration() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public int defaultResizeWidth() {
                return ADAPTIVE_IMAGE_SERVLET_DEFAULT_RESIZE_WIDTH;
            }
        });
    }

}
