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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.DownloadServlet;
import com.adobe.cq.wcm.core.components.models.Download;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
class DownloadImplTest {

    private static final String TEST_BASE = "/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;
    private static final String PDF_ASSET_WITHOUT_SIZE_PROP_PATH = "/content/dam/core/documents_without_size/" + PDF_BINARY_NAME;
    private static final String PDF_FILE_PATH = "/content/downloads/jcr:content/root/responsivegrid/download-3/file";
    private static final String PDF_ASSET_DOWNLOAD_PATH = PDF_ASSET_PATH + "." + DownloadServlet.SELECTOR + ".pdf";
    private static final String PDF_FILE_DOWNLOAD_PATH = PDF_FILE_PATH + "." + DownloadServlet.SELECTOR + ".inline.pdf/" + PDF_BINARY_NAME;
    private static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
    private static final String TEST_CONTENT_DAM_WITHOUT_SIZE_PROP_JSON = "/test-content-dam-without-size-prop.json";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/downloads";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TITLE = "Download";
    private static final String DESCRIPTION = "Description";
    private static final String DAM_TITLE = "This is the title from the PDF.";
    private static final String DAM_DESCRIPTION = "This is the description from the PDF.";
    private static final String PDF_FILESIZE_STRING = "147 KB";
    private static final String PDF_FILENAME = "Download_Test_PDF.pdf";
    private static final String PDF_FORMAT_STRING = "application/pdf";
    private static final String PDF_EXTENSION = "pdf";
    private static final String COMPONENT_ACTION_TEXT = "Click";
    private static final String STYLE_ACTION_TEST = "Download";
    private static final String DOWNLOAD_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/download-1";
    private static final String DOWNLOAD_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/download-2";
    private static final String DOWNLOAD_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/download-3";
    private static final String DOWNLOAD_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/download-4";
    private static final String DOWNLOAD_FULLY_CONFIGURED = "download-fully-configured";
    private static final String DOWNLOAD_WITH_DAM_PROPERTIES = "download-with-dam-properties";
    private static final String DOWNLOAD_FULLY_CONFIGURED_FILE = "download-fully-configured-file";
    private static final String DOWNLOAD_WITH_TITLE_TYPE = "download-with-title-type";
    private static final String DOWNLOAD_WITHOUT_ACTION_TEXT = "download-without-action-text";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        context.load().binaryFile(TEST_BASE + "/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Test
    void testFullyConfiguredDownload() {
        Download download = getDownloadUnderTest(DOWNLOAD_1);
        assertEquals(TITLE, download.getTitle());
        assertEquals(DESCRIPTION, download.getDescription());
        assertEquals(CONTEXT_PATH + PDF_ASSET_DOWNLOAD_PATH, download.getUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FILESIZE_STRING, download.getSize());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
        assertEquals(COMPONENT_ACTION_TEXT, download.getActionText());
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_FULLY_CONFIGURED));
    }

    @Test
    void testFullyConfiguredDownload_assetWithoutSizeProperty() {
        context.load().json(TEST_BASE + TEST_CONTENT_DAM_WITHOUT_SIZE_PROP_JSON, "/content/dam/core/documents_without_size");
        context.load().binaryFile(TEST_BASE+ "/" + PDF_BINARY_NAME, PDF_ASSET_WITHOUT_SIZE_PROP_PATH + "/jcr:content/renditions/original");

        Download download = getDownloadUnderTest(DOWNLOAD_4);
        assertEquals(TITLE, download.getTitle());
        assertEquals(DESCRIPTION, download.getDescription());
        assertEquals(CONTEXT_PATH + PDF_ASSET_WITHOUT_SIZE_PROP_PATH + "." + DownloadServlet.SELECTOR + ".pdf", download.getUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FILESIZE_STRING, download.getSize());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
        assertEquals(COMPONENT_ACTION_TEXT, download.getActionText());
    }

    @Test
    void testFullyConfiguredFileDownload() {
        Download download = getDownloadUnderTest(DOWNLOAD_3);
        assertEquals(TITLE, download.getTitle());
        assertEquals(DESCRIPTION, download.getDescription());
        assertEquals(CONTEXT_PATH + PDF_FILE_DOWNLOAD_PATH, download.getUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
        assertEquals(COMPONENT_ACTION_TEXT, download.getActionText());
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_FULLY_CONFIGURED_FILE));
    }

    @Test
    void testDownloadWithDamProperties() {
        Download download = getDownloadUnderTest(DOWNLOAD_2);
        assertEquals(DAM_TITLE, download.getTitle());
        assertEquals(DAM_DESCRIPTION, download.getDescription());
        assertEquals(CONTEXT_PATH + PDF_ASSET_DOWNLOAD_PATH, download.getUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FILESIZE_STRING, download.getSize());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_WITH_DAM_PROPERTIES));
    }

    @Test
    void testDisplayAllFileMetadata() {
        Download download = getDownloadUnderTest(DOWNLOAD_1,
                Download.PN_DISPLAY_FILENAME, true,
                Download.PN_DISPLAY_FORMAT, true,
                Download.PN_DISPLAY_SIZE, true);
        assertTrue(download.displayFilename(), "Display of filename is not enabled");
        assertTrue(download.displaySize(), "Display of file size is not enabled");
        assertTrue(download.displayFormat(), "Display of file format is not enabled");
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_FULLY_CONFIGURED));
    }

    @Test
    void testDownloadWithTitleType() {
        Download download = getDownloadUnderTest(DOWNLOAD_1,
                Download.PN_TITLE_TYPE, "h5");
        assertEquals("h5", download.getTitleType(), "Expected title type is not correct");
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_WITH_TITLE_TYPE));
    }

    @Test
    void testDownloadWithDefaultTitleType() {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource));

        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertNull(download.getTitleType(), "Expected title type is not correct");
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_FULLY_CONFIGURED));
    }

    @Test
    void testDownloadWithHiddenTitleLink() {
        Resource mockResource = mock(Resource.class);
        MockValueMap mockValueMap = new MockValueMap(mockResource);
        mockValueMap.put(Download.PN_HIDE_TITLE_LINK, true);
        Style mockStyle = new MockStyle(mockResource, mockValueMap);

        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertTrue(download.hideTitleLink(), "Expected title link to be hidden");
    }

    @Test
    void testDownloadWithCustomActionText() {
        Download download = getDownloadUnderTest(DOWNLOAD_1,
                Download.PN_ACTION_TEXT, STYLE_ACTION_TEST);
        assertEquals(COMPONENT_ACTION_TEXT, download.getActionText(), "Expected action text is not correct");
        Utils.testJSONExport(download, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_FULLY_CONFIGURED));
    }

    @Test
    void testDownloadWithoutActionText() {
        Download downloadWithoutActionText = getDownloadUnderTest(DOWNLOAD_2);
        assertNull(downloadWithoutActionText.getActionText(), "Expected action text is not correct");
        Utils.testJSONExport(downloadWithoutActionText, Utils.getTestExporterJSONPath(TEST_BASE, DOWNLOAD_WITHOUT_ACTION_TEXT));
    }

    private Download getDownloadUnderTest(String resourcePath, Object ... properties) {
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(Download.class);
    }
}
