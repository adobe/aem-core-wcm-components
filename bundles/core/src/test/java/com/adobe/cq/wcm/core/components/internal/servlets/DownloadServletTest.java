/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class DownloadServletTest {

    private static final String TEST_BASE = "/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;
    private static final String PDF_FILE_PATH = CONTENT_ROOT + "/downloads/jcr:content/root/responsivegrid/download-3";

    private final AemContext context = CoreComponentTestContext.newAemContext();


    private DownloadServlet downloadServlet;

    @BeforeEach
    void setUp() throws Exception {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        context.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
        context.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_FILE_PATH + "/file");
        downloadServlet = new DownloadServlet();
    }

    @Test
    void testAttachmentAssetDownload() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "");
        context.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        context.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(context.request(), context.response());
        assertTrue(context.response().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", context.response().getHeader("Content-Disposition"));
        assertEquals(8192, context.response().getBufferSize());
    }

    @Test
    void testAttachmentFileDownload() throws Exception {
        context.currentResource(PDF_FILE_PATH + "/file");
        context.request().setHeader("If-Modified-Since", "");
        context.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        context.requestPathInfo().setExtension("pdf");
        context.requestPathInfo().setSuffix("Download_Test_PDF.pdf");
        downloadServlet.doGet(context.request(), context.response());
        assertTrue(context.response().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", context.response().getHeader("Content-Disposition"));
        assertEquals(8192, context.response().getBufferSize());
    }

    @Test
    void testInlineAssetDownload() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "");
        context.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR + "." + DownloadServlet.INLINE_SELECTOR);
        context.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(context.request(), context.response());
        assertTrue(context.response().containsHeader("Content-Disposition"));
        assertEquals("inline", context.response().getHeader("Content-Disposition"));
        assertEquals(8192, context.response().getBufferSize());
    }

    @Test
    void testNotModifiedResponse() throws Exception {
        context.currentResource(PDF_ASSET_PATH);
        context.request().setHeader("If-Modified-Since", "Fri, 19 Oct 2018 19:24:07 GMT");
        downloadServlet.doGet(context.request(), context.response());
        assertEquals(304, context.response().getStatus());
    }

    @Test
    void tesNotModifiedResponseForResource() throws Exception {
        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix("Download_Test_PDF.pdf");
        context.currentResource(PDF_FILE_PATH + "/file");
        request.setHeader("If-Modified-Since", "Fri, 19 Oct 2018 19:24:07 GMT");
        downloadServlet.doGet(request, context.response());
        assertEquals(304, context.response().getStatus());
    }
}
