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

import java.util.Calendar;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.commons.jcr.JcrConstants;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DownloadServletTest {

    private static final String TEST_BASE = "/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;
    private static final String PDF_FILE_PATH = CONTENT_ROOT + "downloads/jcr:content/root/responsivegrid/download-3";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);


    private DownloadServlet downloadServlet;

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        AEM_CONTEXT.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
        AEM_CONTEXT.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_FILE_PATH + "/file");
    }

    @Before
    public void init() throws Exception {
        downloadServlet = new DownloadServlet();
        Resource resource = AEM_CONTEXT.currentResource(PDF_FILE_PATH + "/file/" + JcrConstants.JCR_CONTENT);
        if (resource != null) {
            ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
            if (map != null) {
                map.put(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                AEM_CONTEXT.resourceResolver().commit();
            }
        }
    }

    @Test
    public void testAttachmentAssetDownload() throws Exception {
        AEM_CONTEXT.currentResource(PDF_ASSET_PATH);
        AEM_CONTEXT.request().setHeader("If-Modified-Since", "");
        AEM_CONTEXT.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        AEM_CONTEXT.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertTrue(AEM_CONTEXT.response().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", AEM_CONTEXT.response().getHeader("Content-Disposition"));
        assertEquals(8192, AEM_CONTEXT.response().getBufferSize());
    }

    @Test
    public void testAttachmentFileDownload() throws Exception {
        AEM_CONTEXT.currentResource(PDF_FILE_PATH + "/file");
        AEM_CONTEXT.request().setHeader("If-Modified-Since", "");
        AEM_CONTEXT.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR);
        AEM_CONTEXT.requestPathInfo().setExtension("pdf");
        AEM_CONTEXT.requestPathInfo().setSuffix("Download_Test_PDF.pdf");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertTrue(AEM_CONTEXT.response().containsHeader("Content-Disposition"));
        assertEquals("attachment; filename=\"Download_Test_PDF.pdf\"", AEM_CONTEXT.response().getHeader("Content-Disposition"));
        assertEquals(8192, AEM_CONTEXT.response().getBufferSize());
    }

    @Test
    public void testInlineAssetDownload() throws Exception {
        AEM_CONTEXT.currentResource(PDF_ASSET_PATH);
        AEM_CONTEXT.request().setHeader("If-Modified-Since", "");
        AEM_CONTEXT.requestPathInfo().setSelectorString(DownloadServlet.SELECTOR + "." + DownloadServlet.INLINE_SELECTOR);
        AEM_CONTEXT.requestPathInfo().setExtension("pdf");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertTrue(AEM_CONTEXT.response().containsHeader("Content-Disposition"));
        assertEquals("inline", AEM_CONTEXT.response().getHeader("Content-Disposition"));
        assertEquals(8192, AEM_CONTEXT.response().getBufferSize());
    }

    @Test
    public void testNotModifiedResponse() throws Exception {
        AEM_CONTEXT.currentResource(PDF_ASSET_PATH);
        AEM_CONTEXT.request().setHeader("If-Modified-Since", "Fri, 19 Oct 2018 19:24:07 GMT");
        downloadServlet.doGet(AEM_CONTEXT.request(), AEM_CONTEXT.response());
        assertEquals(304, AEM_CONTEXT.response().getStatus());
    }
}
