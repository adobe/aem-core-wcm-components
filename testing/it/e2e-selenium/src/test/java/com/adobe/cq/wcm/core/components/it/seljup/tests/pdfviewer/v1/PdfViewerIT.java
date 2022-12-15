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

package com.adobe.cq.wcm.core.components.it.seljup.tests.pdfviewer.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.pdfviewer.v1.PdfViewer;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.CLIENTLIBS_PDFVIEWER_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_PDFVIEWER_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfViewerIT extends AuthorBaseUITest {

    private String testPage;
    private String cmpPath;
    private EditorPage editorPage;
    private PdfViewer pdfViewer;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // enable CA config for the test page
        authorClient.setPageProperty(testPage, "sling:configRef", "/conf/core-components");

        // add pdfviewer clientlib to current page policy
        Commons.createPagePolicy(adminClient, defaultPageTemplate, label, new HashMap<String, String>() {{
            put("clientlibs", CLIENTLIBS_PDFVIEWER_V1);
        }});

        // add the pdfviewer component
        cmpPath = Commons.addComponentWithRetry(authorClient, RT_PDFVIEWER_V1,testPage + Commons.relParentCompPath, "pdfviewer");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        pdfViewer = new PdfViewer();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Test PDF viewer with default settings")
    public void testDefaultViewer() throws InterruptedException, TimeoutException {
        Commons.switchToDefaultContext();

        Commons.openEditDialog(editorPage, cmpPath);
        pdfViewer.getEditDialog().setDocumentPath("core-components/Bodea_Brochure.pdf");
        Commons.saveConfigureDialog();

        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        assertTrue(pdfViewer.hasContent("Bodea_Brochure"));
    }
}
