/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DownloadServletTest {

    private static final String TEST_BASE = "/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);


    private DownloadServlet downloadServlet;

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        AEM_CONTEXT.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Before
    public void init() {
        downloadServlet = new DownloadServlet();
        AEM_CONTEXT.currentResource(PDF_ASSET_PATH);
    }

    @Test
    public void testAttachmentDownload() throws Exception {
        AEM_CONTEXT.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        AEM_CONTEXT.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertTrue(AEM_CONTEXT.response().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", AEM_CONTEXT.response().getHeader("Content-Disposition"));
        assertEquals(8192, AEM_CONTEXT.response().getBufferSize());
    }

    @Test
    public void testInlineDownload() throws Exception {
        AEM_CONTEXT.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR + "." + DownloadServlet.INLINE_SELECTOR);
        AEM_CONTEXT.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertTrue(AEM_CONTEXT.response().containsHeader("Content-Disposition"));
        assertEquals("inline", AEM_CONTEXT.response().getHeader("Content-Disposition"));
        assertEquals(8192, AEM_CONTEXT.response().getBufferSize());
    }

    @Test
    public void tesNotModifiedResponse() throws Exception {
        AEM_CONTEXT.request().setHeader("If-Modified-Since", "Fri, 19 Oct 2018 19:24:07 GMT");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertEquals(304, AEM_CONTEXT.response().getStatus());
    }

    @Test
    public void testETagResponse() throws Exception {
        AEM_CONTEXT.request().setHeader("If-None-Match", "78003C8AA0B29FB814691244B231E294");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertEquals(304, AEM_CONTEXT.response().getStatus());
    }
}