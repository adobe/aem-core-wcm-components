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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.annotation.Nullable;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.junit.ClassRule;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.testing.MockAdapterFactory;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;

import io.wcm.testing.mock.aem.junit.AemContext;

public class AbstractImageTest {

    protected static String TEST_BASE = "/image";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext();

    protected static final String CONTEXT_PATH = "/core";
    protected static final String TEST_CONTENT_ROOT = "/content";
    protected static final String TEST_APPS_ROOT = "/apps/core/wcm/components";
    protected static final String PAGE = TEST_CONTENT_ROOT + "/test";
    protected static final String SELECTOR = AdaptiveImageServlet.DEFAULT_SELECTOR;
    protected static final String IMAGE0_PATH = PAGE + "/jcr:content/root/image0";
    protected static final String IMAGE1_PATH = PAGE + "/jcr:content/root/image1";
    protected static final String IMAGE2_PATH = PAGE + "/jcr:content/root/image2";
    protected static final String IMAGE3_PATH = PAGE + "/jcr:content/root/image3";
    protected static final String IMAGE4_PATH = PAGE + "/jcr:content/root/image4";
    protected static final String IMAGE5_PATH = PAGE + "/jcr:content/root/image5";
    protected static final String IMAGE6_PATH = PAGE + "/jcr:content/root/image6";
    protected static final String IMAGE7_PATH = PAGE + "/jcr:content/root/image7";
    protected static final String IMAGE8_PATH = PAGE + "/jcr:content/root/image8";
    protected static final String IMAGE9_PATH = PAGE + "/jcr:content/root/image9";
    protected static final String IMAGE10_PATH = PAGE + "/jcr:content/root/image10";
    protected static final String IMAGE11_PATH = PAGE + "/jcr:content/root/image11";
    protected static final String IMAGE12_PATH = PAGE + "/jcr:content/root/image12";
    protected static final String IMAGE13_PATH = PAGE + "/jcr:content/root/image13";
    protected static final String IMAGE14_PATH = PAGE + "/jcr:content/root/image14";
    protected static final String IMAGE15_PATH = PAGE + "/jcr:content/root/image15";
    protected static final String IMAGE16_PATH = PAGE + "/jcr:content/root/image16";
    protected static final String IMAGE17_PATH = PAGE + "/jcr:content/root/image17";
    protected static final String IMAGE18_PATH = PAGE + "/jcr:content/root/image18";
    protected static final String IMAGE19_PATH = PAGE + "/jcr:content/root/image19";
    protected static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    protected static final String IMAGE22_PATH = PAGE + "/jcr:content/root/image22";
    protected static final String IMAGE23_PATH = PAGE + "/jcr:content/root/image23";
    protected static final String TEMPLATE_PATH = "/conf/coretest/settings/wcm/templates/testtemplate";
    protected static final String TEMPLATE_STRUCTURE_PATH = TEMPLATE_PATH + "/structure";
    protected static final String TEMPLATE_IMAGE_PATH = TEMPLATE_STRUCTURE_PATH + "/jcr:content/root/image_template";
    protected static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    protected static final String GIF_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.gif";
    protected static final String _1PX_IMAGE_BINARY_NAME = "1x1.png";
    protected static final String TIFF_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.tiff";
    protected static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    protected static final String PNG_ASSET_PATH_WITHOUT_EXTENSION = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark";
    protected static final String GIF_ASSET_PATH = "/content/dam/core/images/" + GIF_IMAGE_BINARY_NAME;
    protected static final String TIFF_ASSET_PATH = "/content/dam/core/images/" + TIFF_IMAGE_BINARY_NAME;
    protected static final String GIF5_FILE_PATH = IMAGE5_PATH + "/file";
    protected static final String PNG3_FILE_PATH = IMAGE3_PATH + "/file";
    protected static final String PNG10_FILE_PATH = IMAGE10_PATH + "/file";
    protected static final String PNG12_FILE_PATH = IMAGE12_PATH + "/file";
    protected static final String PNG14_FILE_PATH = IMAGE14_PATH + "/file";
    protected static final String PNG20_FILE_PATH = IMAGE20_PATH + "/file";
    protected static final String PNG23_FILE_PATH = IMAGE23_PATH + "/file";

    protected static ContentPolicyManager contentPolicyManager;
    protected static MimeTypeService mockedMimeTypeService;

    protected ResourceResolver resourceResolver;

    protected static void internalSetUp(AemContext aemContext, String testBase) {
        CONTEXT.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);
        CONTEXT.load().json(testBase + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        mockedMimeTypeService = mock(MimeTypeService.class);
        when(mockedMimeTypeService.getMimeType("tif")).thenReturn(StandardImageHandler.TIFF_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("tiff")).thenReturn(StandardImageHandler.TIFF_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("png")).thenReturn(StandardImageHandler.PNG1_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("jpg")).thenReturn(StandardImageHandler.JPEG_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("jpeg")).thenReturn(StandardImageHandler.JPEG_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("gif")).thenReturn(StandardImageHandler.GIF_MIMETYPE);
        when(mockedMimeTypeService.getExtension(StandardImageHandler.TIFF_MIMETYPE)).thenReturn("tiff");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.JPEG_MIMETYPE)).thenReturn("jpeg");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.PNG1_MIMETYPE)).thenReturn("png");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.GIF_MIMETYPE)).thenReturn("gif");
        aemContext.load().json(testBase + "/test-conf.json", "/conf");
        aemContext.load().json("/image/test-content-dam.json", "/content/dam/core/images");
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
        aemContext.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH +
                "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH_WITHOUT_EXTENSION + "/jcr:content/renditions/original");
        aemContext.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH_WITHOUT_EXTENSION +
                "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        aemContext.load().binaryFile("/image/" + GIF_IMAGE_BINARY_NAME, GIF_ASSET_PATH + "/jcr:content/renditions/original");
        aemContext.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + GIF_IMAGE_BINARY_NAME, GIF_ASSET_PATH +
                "/jcr:content/renditions/cq5dam.web.1280.1280.gif");
        aemContext.load().binaryFile("/image/" + GIF_IMAGE_BINARY_NAME, GIF5_FILE_PATH, StandardImageHandler.GIF_MIMETYPE);
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG3_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG10_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG12_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG14_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.load().binaryFile("/image/" + TIFF_IMAGE_BINARY_NAME, TIFF_ASSET_PATH + "/jcr:content/renditions/original");
        aemContext.load().binaryFile("/image/" + _1PX_IMAGE_BINARY_NAME, PNG20_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG23_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        aemContext.registerInjectActivateService(new MockAdapterFactory());
        contentPolicyManager = mock(ContentPolicyManager.class);
        aemContext.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
                new Function<ResourceResolver, ContentPolicyManager>() {
                    @Nullable
                    @Override
                    public ContentPolicyManager apply(@Nullable ResourceResolver resolver) {
                        return contentPolicyManager;
                    }
                });
    }

}
