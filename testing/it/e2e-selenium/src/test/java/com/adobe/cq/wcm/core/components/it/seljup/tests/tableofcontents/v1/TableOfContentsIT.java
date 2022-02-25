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
package com.adobe.cq.wcm.core.components.it.seljup.tests.tableofcontents.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.TableOfContentsEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.v1.TableOfContents;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")  // TODO
public class TableOfContentsIT extends AuthorBaseUITest {

    private String testPage;
    private String proxyPath;
    private String componentPath;
    private EditorPage editorPage;
    private TableOfContents tableOfContents;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        testPage = authorClient.createPage(
            "testPage",
            "Test Page for Table of Contents",
            rootPage,
            defaultPageTemplate
        ).getSlingPath();

        proxyPath = Commons.createProxyComponent(
            adminClient,
            Commons.rtTableOfContents_v1,
            Commons.proxyPath,
            TableOfContents.COMPONENT_NAME,
            null
        );

        componentPath = Commons.addComponent(
            adminClient,
            proxyPath,
            testPage + Commons.relParentCompPath,
            null,
            null
        );

        tableOfContents = new TableOfContents();
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        adminClient.deletePageWithRetry(
            testPage,
            true,
            false,
            RequestConstants.TIMEOUT_TIME_MS,
            RequestConstants.RETRY_TIME_INTERVAL,
            HttpStatus.SC_OK
        );
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    @Test
    @DisplayName("Test: Check the existence of list type, start level and stop level select fields")
    public void testAllConfigurationsExist() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        assertTrue(tableOfContentsEditDialog.isListTypeSelectPresent(), "List Type is not visible");
        assertTrue(tableOfContentsEditDialog.isStartLevelSelectPresent(), "Start Level is not visible");
        assertTrue(tableOfContentsEditDialog.isStopLevelSelectPresent(), "Stop Level is not visible");
        assertTrue(tableOfContentsEditDialog.isAllListTypesPresent(), "All list types are not present");
        assertTrue(tableOfContentsEditDialog.isAllStartLevelsPresent(), "All start levels are not present");
        assertTrue(tableOfContentsEditDialog.isAllStopLevelsPresent(), "All stop levels are not present");
        Commons.closeConfigureDialog();
//        Commons.saveConfigureDialog();
//        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
//        Commons.switchContext("ContentFrame");
    }
}
