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

package com.adobe.cq.wcm.core.components.it.seljup.tests.download.v1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.download.DownloadEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.download.v1.Download;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.google.common.net.HttpHeaders;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_DOWNLOAD_V1;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DownloadIT extends AuthorBaseUITest {

    private static String assetFilter = "[name='assetfilter_image_path']";
    private static String componentName = "download";
    protected static String testAssetsPath = "/content/dam/core-components";
    private static String testAssetName = "core-comp-test-image.jpg";
    private static String testAssetPath = testAssetsPath + "/" + testAssetName;

    protected EditorPage editorPage;
    protected String testPage;
    protected String cmpPath;
    protected Download download;
    protected String downloadRT;

    private void setupResources() {
        downloadRT = RT_DOWNLOAD_V1;
    }

    protected void setup() throws ClientException {
        testPage = authorClient.createPage("testPage", "Test Page", rootPage, defaultPageTemplate, 200, 201).getSlingPath();

        addPathtoComponentPolicy(responsiveGridPath, downloadRT);
        cmpPath = Commons.addComponentWithRetry(authorClient, downloadRT, testPage + Commons.relParentCompPath, componentName);

        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        download = new Download();
    }

    @BeforeEach
    public void setupBefore() throws Exception {
        setupResources();
        setup();
    }

    @AfterEach
    public void cleanup() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
    }

    @Test
    public void downloadFile() throws TimeoutException, InterruptedException, ClientException, URISyntaxException {
        Commons.openSidePanel();
        Commons.selectInAutocomplete(assetFilter, testAssetsPath);
        Commons.openEditDialog(editorPage, cmpPath);
        DownloadEditDialog downloadEditDialog = download.getEditDialog();
        downloadEditDialog.uploadAssetFromSidePanel(testAssetPath);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        String url = download.getDownloadTitleLink();
        SlingClient slingClient = new SlingClient(new URI(url), authorClient.getUser(), authorClient.getPassword());
        SlingHttpResponse response = slingClient.doGet(url, Collections.EMPTY_LIST, Collections.EMPTY_LIST, HttpStatus.SC_OK);
        Header headers[] = response.getHeaders(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(headers.length > 0);
        assertEquals("attachment; filename=\"" + testAssetName + "\"", headers[0].getValue());
    }
}
