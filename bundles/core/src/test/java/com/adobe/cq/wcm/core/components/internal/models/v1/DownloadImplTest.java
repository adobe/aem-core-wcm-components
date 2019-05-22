/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.DownloadServlet;
import com.adobe.cq.wcm.core.components.models.Download;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class DownloadImplTest {

    private static final String TEST_BASE = "/download";
    private static final String CONTENT_ROOT = "/content";
    private static final String PDF_BINARY_NAME = "Download_Test_PDF.pdf";
    private static final String PDF_ASSET_PATH = "/content/dam/core/documents/" + PDF_BINARY_NAME;
    private static final String PDF_ASSET_DOWNLOAD_PATH = PDF_ASSET_PATH + "." + DownloadServlet.SELECTOR + ".pdf";
    private static final String TEST_CONTENT_DAM_JSON = "/test-content-dam.json";
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

    private Logger downloadLogger;

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + TEST_CONTENT_DAM_JSON, "/content/dam/core/documents");
        AEM_CONTEXT.load().binaryFile("/download/" + PDF_BINARY_NAME, PDF_ASSET_PATH + "/jcr:content/renditions/original");
    }

    @Before
    public void setTestFixture() throws NoSuchFieldException, IllegalAccessException {
        downloadLogger = spy(LoggerFactory.getLogger("FakeLogger"));
        Field field = DownloadImpl.class.getDeclaredField("LOG");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        field.setAccessible(true);
        // remove final modifier from field

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, downloadLogger);
    }

    @Test
    public void testFullyConfiguredDownload() {
        Download download = getDownloadUnderTest(DOWNLOAD_1);
        assertEquals(TITLE, download.getTitle());
        assertEquals(DESCRIPTION, download.getDescription());
        assertEquals(PDF_ASSET_DOWNLOAD_PATH, download.getDownloadUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FILESIZE_STRING, download.getSize());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
        assertEquals(COMPONENT_ACTION_TEXT, download.getActionText());
    }

    @Test
    public void testDownloadWithDamProperties() {
        Download download = getDownloadUnderTest(DOWNLOAD_2);
        assertEquals(DAM_TITLE, download.getTitle());
        assertEquals(DAM_DESCRIPTION, download.getDescription());
        assertEquals(PDF_ASSET_DOWNLOAD_PATH, download.getDownloadUrl());
        assertEquals(PDF_FILENAME, download.getFilename());
        assertEquals(PDF_EXTENSION, download.getExtension());
        assertEquals(PDF_FILESIZE_STRING, download.getSize());
        assertEquals(PDF_FORMAT_STRING, download.getFormat());
    }

    @Test
    public void testDisplayAllFileMetadata() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Download.PN_DISPLAY_FILENAME, "true");
            put(Download.PN_DISPLAY_FORMAT, "true");
            put(Download.PN_DISPLAY_SIZE, "true");
        }}));
        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertEquals("Display of filename is not enabled", true, download.displayFilename());
        assertEquals("Display of file size is not enabled", true, download.displaySize());
        assertEquals("Display of file format is not enabled", true, download.displayFormat());
    }

    @Test
    public void testDownloadWithTitleType() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Download.PN_TITLE_TYPE, "h5");
        }}));

        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertEquals("Expected title type is not correct", "h5", download.getTitleType());
    }

    @Test
    public void testDownloadWithDefaultTitleType() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource));

        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertEquals("Expected title type is not correct", null, download.getTitleType());
    }

    @Test
    public void testDownloadWithCustomActionText() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Download.PN_ACTION_TEXT, STYLE_ACTION_TEST);
        }}));

        Download download = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertEquals("Expected action text is not correct", COMPONENT_ACTION_TEXT, download.getActionText());
    }

    @Test
    public void testDownloadWithActionTextFromStyle() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Download.PN_ACTION_TEXT, STYLE_ACTION_TEST);
        }}));

        Download download = getDownloadUnderTest(DOWNLOAD_2, mockStyle);
        assertEquals("Expected action text is not correct", STYLE_ACTION_TEST, download.getActionText());
    }

    @Test
    public void testDownloadsWithDefaultActionText() throws Exception
    {
        Resource mockResource = mock(Resource.class);
        Style mockStyle = new MockStyle(mockResource, new MockValueMap(mockResource));

        Download downloadWithConfiguredActionText = getDownloadUnderTest(DOWNLOAD_1, mockStyle);
        assertEquals("Expected action text is not correct", COMPONENT_ACTION_TEXT, downloadWithConfiguredActionText.getActionText());

        Download downloadWithoutConfiguredActionText = getDownloadUnderTest(DOWNLOAD_2, mockStyle);
        assertEquals("Expected action text is not correct", null, downloadWithoutConfiguredActionText.getActionText());
    }


    private Download getDownloadUnderTest(String resourcePath) {
        return getDownloadUnderTest(resourcePath, null);
    }

    private Download getDownloadUnderTest(String resourcePath, Style currentStyle) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        if (currentStyle != null) {
            slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        }
        Component component = mock(Component.class);
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Download.class);
    }
}
