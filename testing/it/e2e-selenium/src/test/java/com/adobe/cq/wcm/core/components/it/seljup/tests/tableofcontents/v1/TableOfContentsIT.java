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
import com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents.TableOfContentsEditDialog65;
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

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TABLEOFCONTENTS_V1;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("group2")  // TODO
public class TableOfContentsIT extends AuthorBaseUITest {

    private static String componentName = "button";
    private String pageTitle;
    private String testPage;
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

        componentPath = Commons.addComponentWithRetry(
            adminClient,
            RT_TABLEOFCONTENTS_V1,
            testPage + Commons.relParentCompPath,
            componentName
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
    }

    @Test
    @DisplayName("Test: Check the visibility of table of contents template placeholder")
    public void testVisibilityOfTocTemplatePlaceholder() {
        Commons.switchContext("ContentFrame");
        assertTrue(tableOfContents.isTocTemplatePlaceholderVisible());
        Commons.switchToDefaultContext();
        editorPage.enterPreviewMode();
        assertFalse(tableOfContents.isTocTemplatePlaceholderVisible(),
            "Table of Contents placeholder should not be visible in preview mode");
    }

    @Test
    @DisplayName("Test: Check the actual rendering of table of contents")
    public void testTocRendering() throws ClientException {
         String titleProxyPath = Commons.createProxyComponent(adminClient,
             Commons.RT_TITLE_V3,
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
        assertTrue(tableOfContents.isTocContentExists(), "" +
            "Table of Contents actual content should be rendered");
        assertTrue(tableOfContents.isTextPresentInTocContent(pageTitle),
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
        assertTrue(tableOfContentsEditDialog.isIdTextBoxPresent(), "ID text box should be present");
        assertTrue(tableOfContentsEditDialog.isAllListTypesPresent(), "All list types should be present");
        assertTrue(tableOfContentsEditDialog.isAllStartLevelsPresent(), "All start levels should be present");
        assertTrue(tableOfContentsEditDialog.isAllStopLevelsPresent(), "All stop levels should be present");
        Commons.closeConfigureDialog();
    }

    @Test
    @DisplayName("Test: Check if setting the id renders the id attribute on TOC content HTML element")
    public void testTocIdConfig() throws InterruptedException, TimeoutException {
        String id = "toc-sample-id";

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        tableOfContentsEditDialog.setId(id);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertEquals(id, tableOfContents.getId(), "ID should be configured");
    }

    @Test
    @DisplayName("Test: Check the non existence of list type, start level and stop level select fields " +
        "if overridden by design dialog")
    public void testTocConfigOverrideByDesignDialog() throws ClientException, InterruptedException, TimeoutException {
        String listType = "bulleted";
        String startLevel = "h3";
        String stopLevel = "h4";
        String[] includeClasses = new String[] { "include-1", "include-2" };
        String[] ignoreClasses = new String[] { "ignore-1", "ignore-2" };

        createComponentPolicy("/tableofcontents-v1", new HashMap<String, String>() {{
            put("restrictListType", listType);
            put("restrictStartLevel", startLevel);
            put("restrictStopLevel", stopLevel);
            put("includeClasses", StringUtils.join(includeClasses, ","));
            put("ignoreClasses", StringUtils.join(ignoreClasses, ","));
        }});

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        assertFalse(tableOfContentsEditDialog.isListTypeSelectPresent(), "List Type should not be visible");
        assertFalse(tableOfContentsEditDialog.isStartLevelSelectPresent(), "Start Level should not be visible");
        assertFalse(tableOfContentsEditDialog.isStopLevelSelectPresent(), "Stop Level should not be visible");
        Commons.saveConfigureDialog();
    }

    @Tag("IgnoreOn65")
    @Test
    @DisplayName("Test: Check that user is unable to select start level greater than stop level in edit dialog")
    public void testInvalidLevelsErrorTooltipInEditDialog() throws InterruptedException, TimeoutException {
        String startLevel = "h4";
        String invalidStopLevel = "h3";
        String validStopLevel = "h5";

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog tableOfContentsEditDialog = tableOfContents.getEditDialog();
        tableOfContentsEditDialog.selectStartLevel(startLevel);
        tableOfContentsEditDialog.selectStopLevel(invalidStopLevel);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(tableOfContentsEditDialog.isInvalidLevelsErrorTooltipPresent());
        tableOfContentsEditDialog.selectStopLevel(validStopLevel);
        assertFalse(tableOfContentsEditDialog.isInvalidLevelsErrorTooltipPresent());
        Commons.saveConfigureDialog();
    }

    @Tag("IgnoreOnSDK")
    @Test
    @DisplayName("Test: Check that user is unable to select start level greater than stop level in edit dialog")
    public void testInvalidLevelsErrorTooltipInEditDialog65() throws InterruptedException, TimeoutException {
        String startLevel = "h4";
        String invalidStopLevel = "h3";
        String validStopLevel = "h5";

        Commons.openEditDialog(editorPage, componentPath);
        TableOfContentsEditDialog65 tableOfContentsEditDialog65 = tableOfContents.getEditDialog65();
        tableOfContentsEditDialog65.selectStartLevel(startLevel);
        tableOfContentsEditDialog65.selectStopLevel(invalidStopLevel);
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(tableOfContentsEditDialog65.isInvalidLevelsErrorTooltipPresent());
        tableOfContentsEditDialog65.selectStopLevel(validStopLevel);
        assertFalse(tableOfContentsEditDialog65.isInvalidLevelsErrorTooltipPresent());
        Commons.saveConfigureDialog();
    }

}
