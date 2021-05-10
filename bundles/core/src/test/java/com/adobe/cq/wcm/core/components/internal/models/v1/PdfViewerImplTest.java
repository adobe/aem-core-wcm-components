/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.PdfViewer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class PdfViewerImplTest {

    private static final String BASE = "/pdfviewer";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/pdfviewer";
    private static final String GRID = ROOT_PAGE + "/jcr:content/root/responsivegrid";
    private static final String DCV_1 = "/pdfviewer-1";
    private static final String DCV_2 = "/pdfviewer-2";
    private static final String DCV_3 = "/pdfviewer-3";
    private static final String PATH_DCV_1 = GRID + DCV_1;
    private static final String PATH_DCV_2 = GRID + DCV_2;
    private static final String PATH_DCV_3 = GRID + DCV_3;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
    }

    @Test
    void testSizedContainer() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_2);
        assertEquals("core/wcm/components/pdfviewer/v1/pdfviewer", dcv.getExportedType());
        assertEquals("SIZED_CONTAINER", dcv.getType());
        assertEquals("https://pdfviewer.test/Test Document2.pdf", dcv.getDocumentPath());
        assertEquals("Test Document2.pdf", dcv.getDocumentFileName());
        assertFalse(dcv.isBorderless());
        assertFalse(dcv.isShowLeftHandPanel());
        assertFalse(dcv.isShowFullScreen());
        assertFalse(dcv.isShowFullScreen());
        assertFalse(dcv.isShowPrintPdf());
        assertFalse(dcv.isShowPageControls());
        assertFalse(dcv.isDockPageControls());
        assertEquals(PdfViewer.CSS_SIZED_CONTAINER, dcv.getContainerClass());
        String json = "{\"embedMode\":\"SIZED_CONTAINER\",\"showFullScreen\":false,\"showPageControls\":false,\"dockPageControls\":false,\"showDownloadPDF\":false,\"showPrintPDF\":false}";
        assertEquals(json, dcv.getViewerConfigJson());
    }

    @Test
    void testExportedType() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("core/wcm/components/pdfviewer/v1/pdfviewer", dcv.getExportedType());
    }

    @Test
    void testGetType() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("FULL_WINDOW", dcv.getType());
    }

    @Test
    void testGetDocumentPath() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("https://pdfviewer.test/Test Document.pdf", dcv.getDocumentPath());
    }

    @Test
    void testGetDocumentPathNotSet() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_3);
        assertNull(dcv.getDocumentPath());
    }

    @Test
    void testGetDocumentFileName() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("Test Document.pdf", dcv.getDocumentFileName());
    }

    @Test
    void testGetDocumentFileNameNotSet() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_3);
        assertNull(dcv.getDocumentFileName());
    }

    @Test
    void testGetDefaultViewMode() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("FIT_PAGE", dcv.getDefaultViewMode());
    }

    @Test
    void testGetBorderless() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertFalse(dcv.isBorderless());
    }

    @Test
    void testGetShowAnnotationTools() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowAnnotationTools());
    }

    @Test
    void testGetShowLeftHandPanel() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowLeftHandPanel());
    }

    @Test
    void testGetShowFullScreen() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowFullScreen());
    }

    @Test
    void testGetShowDownloadPdf() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowDownloadPdf());
    }

    @Test
    void testGetShowPrintPdf() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowPrintPdf());
    }

    @Test
    void testGetShowPageControls() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isShowPageControls());
    }

    @Test
    void testGetDockPageControls() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertTrue(dcv.isDockPageControls());
    }

    @Test
    void testGetContainerClass() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(PdfViewer.CSS_FULL_WINDOW, dcv.getContainerClass());
    }

    @Test
    void testGetViewerConfigJson() {
        PdfViewer dcv = getDcvUnderTest(PATH_DCV_1);
        String json = "{\"embedMode\":\"FULL_WINDOW\",\"defaultViewMode\":\"FIT_PAGE\",\"showAnnotationTools\":true,\"showLeftHandPanel\":true,\"showPageControls\":true,\"dockPageControls\":true,\"showDownloadPDF\":true,\"showPrintPDF\":true}";
        assertEquals(json, dcv.getViewerConfigJson());
    }

    private PdfViewer getDcvUnderTest(String resourcePath) {
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(PdfViewer.class);
    }
}
