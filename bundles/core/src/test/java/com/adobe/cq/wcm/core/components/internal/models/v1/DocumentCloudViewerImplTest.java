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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.DocumentCloudViewer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ExtendWith(AemContextExtension.class)
class DocumentCloudViewerImplTest {

    private static final String BASE = "/documentcloudviewer";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/documentcloudviewer";
    private static final String GRID = ROOT_PAGE + "/jcr:content/root/responsivegrid";
    private static final String DCV_1 = "/documentcloudviewer-1";
    private static final String DCV_2 = "/documentcloudviewer-2";
    private static final String PATH_DCV_1 = GRID + DCV_1;
    private static final String PATH_DCV_2 = GRID + DCV_2;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
    }

    @Test
    void testSizedContainer() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_2);
        assertEquals("core/wcm/components/documentcloudviewer/v1/documentcloudviewer", dcv.getExportedType());
        assertEquals("SIZED_CONTAINER", dcv.getType());
        assertEquals("https://documentcloudviewer.test/Test Document2.pdf", dcv.getDocumentPath());
        assertEquals("Test Document2.pdf", dcv.getDocumentFileName());
        assertEquals("400px", dcv.getViewerHeight());
        assertEquals(false, dcv.getBorderless());
        assertEquals(false, dcv.getShowLeftHandPanel());
        assertEquals(false, dcv.getShowFullScreen());
        assertEquals(false, dcv.getShowFullScreen());
        assertEquals(false, dcv.getShowPrintPdf());
        assertEquals(false, dcv.getShowPageControls());
        assertEquals(false, dcv.getDockPageControls());
        assertEquals("adobe-dc-view-sized-container", dcv.getContainerClass());
        String json = "{\"embedMode\":\"SIZED_CONTAINER\",\"showFullScreen\":false,\"showPageControls\":false,\"dockPageControls\":false,\"showDownloadPDF\":false,\"showPrintPDF\":false}";
        assertEquals(json, dcv.getViewerConfigJson());
    }

    @Test
    void testExportedType() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("core/wcm/components/documentcloudviewer/v1/documentcloudviewer", dcv.getExportedType());
    }

    @Test
    void testGetType() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("FULL_WINDOW", dcv.getType());
    }

    @Test
    void testGetDocumentPath() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("https://documentcloudviewer.test/Test Document.pdf", dcv.getDocumentPath());
    }

    @Test
    void testGetDocumentFileName() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("Test Document.pdf", dcv.getDocumentFileName());
    }

    @Test
    void testGetDefaultViewMode() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("FIT_PAGE", dcv.getDefaultViewMode());
    }

    @Test
    void testGetViewerHeight() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("500px", dcv.getViewerHeight());
    }

    @Test
    void testGetBorderless() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(false, dcv.getBorderless());
    }

    @Test
    void testGetShowAnnotationTools() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowAnnotationTools());
    }

    @Test
    void testGetShowLeftHandPanel() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowLeftHandPanel());
    }

    @Test
    void testGetShowFullScreen() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowFullScreen());
    }

    @Test
    void testGetShowDownloadPdf() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowDownloadPdf());
    }

    @Test
    void testGetShowPrintPdf() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowPrintPdf());
    }

    @Test
    void testGetShowPageControls() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getShowPageControls());
    }

    @Test
    void testGetDockPageControls() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals(true, dcv.getDockPageControls());
    }
    
    @Test
    void testGetContainerClass() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        assertEquals("adobe-dc-view-full-window", dcv.getContainerClass());
    }

    @Test
    void testGetViewerConfigJson() {
        DocumentCloudViewer dcv = getDcvUnderTest(PATH_DCV_1);
        String json = "{\"embedMode\":\"FULL_WINDOW\",\"defaultViewMode\":\"FIT_PAGE\",\"showAnnotationTools\":true,\"showLeftHandPanel\":true,\"showPageControls\":true,\"dockPageControls\":true,\"showDownloadPDF\":true,\"showPrintPDF\":true}";
        assertEquals(json, dcv.getViewerConfigJson());
    }

    private DocumentCloudViewer getDcvUnderTest(String resourcePath) {
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(DocumentCloudViewer.class);
    }
}
