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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.testing.MockAssetDelivery;
import com.adobe.cq.wcm.core.components.testing.MockPublishUtils;
import com.adobe.cq.wcm.spi.AssetDelivery;
import com.day.cq.dam.api.s7dam.utils.PublishUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.mime.MimeTypeService;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class AbstractImageTest {

    protected static String TEST_BASE = "/image";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    protected static final String MIME_TYPE_SVG = "image/svg+xml";

    protected static final String CONTEXT_PATH = "/core";
    protected static final String TEST_CONTENT_ROOT = "/content";
    protected static final String TEST_APPS_ROOT = "/apps/core/wcm/components";
    protected static final String PAGE = TEST_CONTENT_ROOT + "/test";
    protected static final String SELECTOR = AdaptiveImageServlet.DEFAULT_SELECTOR;
    protected static final int JPEG_QUALITY = AdaptiveImageServlet.DEFAULT_JPEG_QUALITY;
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
    protected static final String IMAGE18_PATH = PAGE + "/jcr:content/root/image18";
    protected static final String IMAGE19_PATH = PAGE + "/jcr:content/root/image19";
    protected static final String IMAGE20_PATH = PAGE + "/jcr:content/root/image20";
    protected static final String IMAGE22_PATH = PAGE + "/jcr:content/root/image22";
    protected static final String IMAGE23_PATH = PAGE + "/jcr:content/root/image23";
    protected static final String IMAGE24_PATH = PAGE + "/jcr:content/root/image24";
    protected static final String IMAGE25_PATH = PAGE + "/jcr:content/root/image25";
    protected static final String IMAGE26_PATH = PAGE + "/jcr:content/root/image26";
    protected static final String IMAGE27_PATH = PAGE + "/jcr:content/root/image27";
    protected static final String IMAGE28_PATH = PAGE + "/jcr:content/root/image28";
    protected static final String IMAGE29_PATH = PAGE + "/jcr:content/root/image29";
    protected static final String IMAGE30_PATH = PAGE + "/jcr:content/root/image30";
    protected static final String TEMPLATE_PATH = "/conf/coretest/settings/wcm/templates/testtemplate";
    protected static final String TEMPLATE_STRUCTURE_PATH = TEMPLATE_PATH + "/structure";
    protected static final String TEMPLATE_IMAGE_PATH = TEMPLATE_STRUCTURE_PATH + "/jcr:content/root/image_template";
    protected static final String TEMPLATE_IMAGE_NO_DATE_PATH = TEMPLATE_STRUCTURE_PATH + "/jcr:content/root/image_template_no_date";
    protected static final String TEMPLATE_IMAGE_INHERITED_PATH1 = TEMPLATE_STRUCTURE_PATH + "/jcr:content/root/image_template_page_image1";
    protected static final String TEMPLATE_IMAGE_INHERITED_PATH2 = TEMPLATE_STRUCTURE_PATH + "/jcr:content/root/image_template_page_image2";
    protected static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    protected static final String GIF_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.gif";
    protected static final String JPG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.jpg";
    protected static final String _1PX_IMAGE_BINARY_NAME = "1x1.png";
    protected static final String _40MPX_IMAGE_BINARY_NAME = "20000x20000.png";
    protected static final String TRANSPARENT_IMAGE_BINARY_NAME = "transparent_hd.png";
    protected static final String TIFF_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.tiff";
    protected static final String SVG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.svg";
    protected static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    protected static final String PNG_ASSET_PATH_WITHOUT_EXTENSION = "/content/dam/core/images/Adobe_Systems_logo_and_wordmark";
    protected static final String GIF_ASSET_PATH = "/content/dam/core/images/" + GIF_IMAGE_BINARY_NAME;
    protected static final String TIFF_ASSET_PATH = "/content/dam/core/images/" + TIFF_IMAGE_BINARY_NAME;
    protected static final String SVG_ASSET_PATH = "/content/dam/core/images/" + SVG_IMAGE_BINARY_NAME;
    protected static final String GIF5_FILE_PATH = IMAGE5_PATH + "/file";
    protected static final String PNG3_FILE_PATH = IMAGE3_PATH + "/file";
    protected static final String PNG10_FILE_PATH = IMAGE10_PATH + "/file";
    protected static final String PNG12_FILE_PATH = IMAGE12_PATH + "/file";
    protected static final String PNG14_FILE_PATH = IMAGE14_PATH + "/file";
    protected static final String PNG20_FILE_PATH = IMAGE20_PATH + "/file";
    protected static final String PNG23_FILE_PATH = IMAGE23_PATH + "/file";
    protected static final String SVG_FILE_PATH = IMAGE24_PATH + "/file";
    protected static final String LARGE_PNG_ASSET_PATH = "/content/dam/core/images/20000x20000.png";
    protected static final String TRANSPARENT_PNG_ASSET_PATH = "/content/dam/core/images/transparent_hd.png";
    protected static final String ASSET_DELIVERY_TEST_URL="/asset/delivery/test/url";

    protected static ContentPolicyManager contentPolicyManager;
    protected static MimeTypeService mockedMimeTypeService;

    protected ResourceResolver resourceResolver;

    protected void internalSetUp(String testBase) {
        context.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_CONTENT_ROOT);
        context.load().json(testBase + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        context.registerService(PublishUtils.class, new MockPublishUtils());
        mockedMimeTypeService = mock(MimeTypeService.class);
        when(mockedMimeTypeService.getMimeType("tif")).thenReturn(StandardImageHandler.TIFF_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("tiff")).thenReturn(StandardImageHandler.TIFF_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("png")).thenReturn(StandardImageHandler.PNG1_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("jpg")).thenReturn(StandardImageHandler.JPEG_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("jpeg")).thenReturn(StandardImageHandler.JPEG_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("gif")).thenReturn(StandardImageHandler.GIF_MIMETYPE);
        when(mockedMimeTypeService.getMimeType("svg")).thenReturn(MIME_TYPE_SVG);
        when(mockedMimeTypeService.getExtension(StandardImageHandler.TIFF_MIMETYPE)).thenReturn("tiff");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.JPEG_MIMETYPE)).thenReturn("jpeg");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.PNG1_MIMETYPE)).thenReturn("png");
        when(mockedMimeTypeService.getExtension(StandardImageHandler.GIF_MIMETYPE)).thenReturn("gif");
        when(mockedMimeTypeService.getExtension(MIME_TYPE_SVG)).thenReturn("svg");
        context.load().json(testBase + "/test-conf.json", "/conf");
        context.load().json("/image/test-content-dam.json", "/content/dam/core/images");
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH +
                "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + JPG_IMAGE_BINARY_NAME, PNG_ASSET_PATH +
                "/jcr:content/renditions/cq5dam.web.1280.1280.jpg");
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH_WITHOUT_EXTENSION + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + PNG_IMAGE_BINARY_NAME, PNG_ASSET_PATH_WITHOUT_EXTENSION +
                "/jcr:content/renditions/cq5dam.web.1280.1280.png");
        context.load().binaryFile("/image/" + GIF_IMAGE_BINARY_NAME, GIF_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + "cq5dam.web.1280.1280_" + GIF_IMAGE_BINARY_NAME, GIF_ASSET_PATH +
                "/jcr:content/renditions/cq5dam.web.1280.1280.gif");
        context.load().binaryFile("/image/" + SVG_IMAGE_BINARY_NAME, SVG_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + GIF_IMAGE_BINARY_NAME, GIF5_FILE_PATH, StandardImageHandler.GIF_MIMETYPE);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG3_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG10_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG12_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG14_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + TIFF_IMAGE_BINARY_NAME, TIFF_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + _1PX_IMAGE_BINARY_NAME, PNG20_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME, PNG23_FILE_PATH, StandardImageHandler.PNG1_MIMETYPE);
        context.load().binaryFile("/image/" + SVG_IMAGE_BINARY_NAME, SVG_FILE_PATH, MIME_TYPE_SVG);
        context.load().binaryFile("/image/" + _40MPX_IMAGE_BINARY_NAME, LARGE_PNG_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/image/" + TRANSPARENT_IMAGE_BINARY_NAME, TRANSPARENT_PNG_ASSET_PATH + "/jcr:content/renditions/original");
    }

    protected void registerAssetDelivery() {
        AssetDelivery assetDelivery = new MockAssetDelivery();
        context.registerService(AssetDelivery.class, assetDelivery);
    }

}
