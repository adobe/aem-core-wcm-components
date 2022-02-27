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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("group2")  // TODO
public class TableOfContentsIT extends AuthorBaseUITest {

    private String pageTitle;
    private String testPage;
    private String proxyPath;
    private String componentPath;
    private EditorPage editorPage;
    private TableOfContents tableOfContents;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        pageTitle = "Test Page for Table of Contents";

        testPage = authorClient.createPage(
            "testPage",
            pageTitle,
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
    @DisplayName("Test: Check the visibility of table of contents placeholder")
    public void testVisibilityOfTocPlaceholder() {
//        Commons.switchContext("ContentFrame");
//        assertTrue(tableOfContents.isTocPlaceholderVisible());
        editorPage.enterPreviewMode();
        assertFalse(tableOfContents.isTocPlaceholderVisible(),
            "Table of Contents placeholder should not be visible in preview mode");
    }

    @Test
    @DisplayName("Test: Check the actual rendering of table of contents")
    public void testTocRendering() throws ClientException {
         String titleProxyPath = Commons.createProxyComponent(adminClient,
             Commons.rtTitle_v3,
             Commons.proxyPath,
             "",
            null
        );

        Commons.addComponent(
            adminClient,
            titleProxyPath,
            testPage + Commons.relParentCompPath,
            null,
            null
        );

        editorPage.refresh();
        Commons.switchContext("ContentFrame");
        assertFalse(tableOfContents.isTocPlaceholderExists(),
            "Table of Contents placeholder should not be visible");
        assertTrue(tableOfContents.isActualTocContentExists(), "" +
            "Table of Contents actual content should be rendered");
        assertTrue(tableOfContents.isTextPresentInActualTocContent(pageTitle),
            "Table of Contents should contain the required text");
    }

    @Test
    @DisplayName("Test: Check the existence of list type, start level and stop level select fields")
    public void testAllTocConfigExist() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        assertTrue(tableOfContentsEditDialog.isListTypeSelectPresent(), "List Type should be visible");
        assertTrue(tableOfContentsEditDialog.isStartLevelSelectPresent(), "Start Level should be visible");
        assertTrue(tableOfContentsEditDialog.isStopLevelSelectPresent(), "Stop Level should be visible");
        assertTrue(tableOfContentsEditDialog.isIdTextboxPresent(), "ID text box should be present");
        assertTrue(tableOfContentsEditDialog.isAllListTypesPresent(), "All list types should be present");
        assertTrue(tableOfContentsEditDialog.isAllStartLevelsPresent(), "All start levels should be present");
        assertTrue(tableOfContentsEditDialog.isAllStopLevelsPresent(), "All stop levels should be present");
        Commons.closeConfigureDialog();
    }

    @Test
    @DisplayName("Test: Check if changing the config changes the toc placeholder data attributes")
    public void testTocConfigChange() throws InterruptedException, TimeoutException {
        String listType = "ordered";
        int startLevel = 2;
        int stopLevel = 5;
        String id = "toc-sample-id";

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        tableOfContentsEditDialog.selectListType(listType);
        tableOfContentsEditDialog.selectStartLevel(startLevel);
        tableOfContentsEditDialog.selectStopLevel(stopLevel);
        tableOfContentsEditDialog.setId(id);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertEquals(listType, tableOfContents.getListType(), "List type should be configured");
        assertEquals(startLevel, tableOfContents.getStartLevel() ,"Start level should be configured");
        assertEquals(stopLevel, tableOfContents.getStopLevel(), "Stop level should be configured");
        assertEquals(id, tableOfContents.getId(), "ID should be configured");
    }

    @Test
    @DisplayName("Test: Check the non existence of list type, start level and stop level select fields " +
        "if overridden by design dialog")
    public void testTocConfigOverrideByDesignDialog() throws ClientException, InterruptedException, TimeoutException {
        String listType = "ordered";
        int startLevel = 3;
        int stopLevel = 4;
        String[] includeClasses = new String[] { "include-1", "include-2" };
        String[] ignoreClasses = new String[] { "ignore-1", "ignore-2" };

        createComponentPolicy(proxyPath.substring(proxyPath.lastIndexOf('/')), new HashMap<String, String>() {{
            put("restrictListType", listType);
            put("restrictStartLevel", String.valueOf(startLevel));
            put("restrictStopLevel", String.valueOf(stopLevel));
            put("includeClasses", StringUtils.join(includeClasses, ","));
            put("ignoreClasses", StringUtils.join(ignoreClasses, ","));
        }});

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        assertFalse(tableOfContentsEditDialog.isListTypeSelectPresent(), "List Type should not be visible");
        assertFalse(tableOfContentsEditDialog.isStartLevelSelectPresent(), "Start Level should be visible");
        assertFalse(tableOfContentsEditDialog.isStopLevelSelectPresent(), "Stop Level should be visible");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertEquals(listType, tableOfContents.getListType(),
            "List type should be overridden by design dialog");
        assertEquals(startLevel, tableOfContents.getStartLevel(),
            "Start Level should be overridden by design dialog");
        assertEquals(stopLevel, tableOfContents.getStopLevel(),
            "Stop level should be overridden by design dialog");
        assertArrayEquals(includeClasses, tableOfContents.getIncludeClasses(),
            "Include class names should be configured by design dialog");
        assertArrayEquals(ignoreClasses, tableOfContents.getIgnoreClasses(),
            "Ignore class names should be configured by design dialog");
    }
}
