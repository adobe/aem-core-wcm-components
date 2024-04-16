/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.download.v2;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.download.DownloadEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.google.common.net.HttpHeaders;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_DOWNLOAD_V2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DownloadIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.download.v1.DownloadIT {

    private static final String assetDocumentFilter = "[name='assetfilter_document_path']";
    private static final String testAssetWordName = "core-comp-test-word.doc";
    private static final String testAssetWordPath = testAssetsPath + "/" + testAssetWordName;
    private static final String testAssetXlsxName = "core-comp-test-xlsx.xlsx";
    private static final String testAssetXlsxPath = testAssetsPath + "/" + testAssetXlsxName;

    private static final String testAssetPptxName = "core-comp-test-pptx.pptx";

    private static final String testAssetPptxPath = testAssetsPath + "/" + testAssetPptxName;

    private void setupResources() {
        downloadRT = RT_DOWNLOAD_V2;
    }

    @BeforeEach
    public void setupBefore() throws Exception {
        setupResources();
        setup();
    }

    @Test
    public void downloadWordFile() throws TimeoutException, InterruptedException, ClientException, URISyntaxException {
        Header[] headers = getDownloadedFileHeaders(testAssetWordPath);
        assertTrue(headers.length > 0);
        assertEquals("attachment; filename=\"" + testAssetWordName + "\"", headers[0].getValue());
    }

    @Test
    public void downloadXlsxFile() throws TimeoutException, InterruptedException, ClientException, URISyntaxException {
        Header[] headers = getDownloadedFileHeaders(testAssetXlsxPath);
        assertTrue(headers.length > 0);
        assertEquals("attachment; filename=\"" + testAssetXlsxName + "\"", headers[0].getValue());
    }

    @Test
    public void downloadPptxFile() throws TimeoutException, InterruptedException, ClientException, URISyntaxException {
        Header[] headers = getDownloadedFileHeaders(testAssetPptxPath);
        assertTrue(headers.length > 0);
        assertEquals("attachment; filename=\"" + testAssetPptxName + "\"", headers[0].getValue());
    }

    private Header[] getDownloadedFileHeaders (String testAssetPath) throws InterruptedException, TimeoutException, URISyntaxException, ClientException {
        Commons.openSidePanel();
        Commons.useDialogSelect("assetfilter_type_selector", "Documents");
        Commons.selectInAutocomplete(assetDocumentFilter, testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        DownloadEditDialog downloadEditDialog = download.getEditDialog();
        downloadEditDialog.uploadAssetFromSidePanel(testAssetPath);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        String url = download.getDownloadTitleLink();
        SlingClient slingClient = new SlingClient(new URI(url), authorClient.getUser(), authorClient.getPassword());
        SlingHttpResponse response = slingClient.doGet(url, Collections.EMPTY_LIST, Collections.EMPTY_LIST, HttpStatus.SC_OK);
        return response.getHeaders(HttpHeaders.CONTENT_DISPOSITION);
    }

}
