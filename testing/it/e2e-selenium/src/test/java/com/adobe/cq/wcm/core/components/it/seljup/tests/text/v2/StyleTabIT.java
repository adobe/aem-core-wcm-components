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

package com.adobe.cq.wcm.core.components.it.seljup.tests.text.v2;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.TextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class StyleTabIT extends AuthorBaseUITest {

    private static String testValue = "Text styled by Style System.";

    private String testPage;
    private String compPath;
    private EditorPage editorPage;

    private void addStyleNodesToPolicy(String policyPath) throws ClientException {
        adminClient.createNode(policyPath + "/jcr:content", "nt:unstructured");
        adminClient.createNode(policyPath + "/cq:styleGroups", "nt:unstructured");

        adminClient.createNode(policyPath + "/cq:styleGroups/item0", "nt:unstructured");
        adminClient.createNode(policyPath + "/cq:styleGroups/item0/cq:styles", "nt:unstructured");

        adminClient.createNode(policyPath + "/cq:styleGroups/item0/cq:styles/item0", "nt:unstructured");
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item0", "cq:styleClasses", "cmp-blue-text", 200, 201);
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item0", "cq:styleId", "1547060098888", 200, 201);
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item0", "cq:styleLabel", "Blue", 200, 201);

        adminClient.createNode(policyPath + "/cq:styleGroups/item0/cq:styles/item1", "nt:unstructured");
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item1", "cq:styleClasses", "cmp-red-text", 200, 201);
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item1", "cq:styleId", "1550165689999", 200, 201);
        adminClient.setPropertyString(policyPath + "/cq:styleGroups/item0/cq:styles/item1", "cq:styleLabel", "Red", 200, 201);
    }

    private void setup() throws ClientException {
        String textRT = Commons.RT_TEXT_V2;
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        compPath = Commons.addComponentWithRetry(authorClient, textRT, testPage + Commons.relParentCompPath, "text");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        // create component policy and add the styles nodes to it
        String policyPath = createComponentPolicy(textRT.substring(textRT.lastIndexOf('/')), new HashMap<>());
        addStyleNodesToPolicy(policyPath);
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false,
                RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Test: Check if the style is not applied by default to the Text component.")
    public void testNoStyleAppliedByDefault() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, compPath);
        TextEditDialog editDialog = new TextEditDialog();
        editDialog.setId("text-id");
        editDialog.setText(testValue);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        assertTrue(editDialog.componentHasNoClassesAppliedByTheStyleSystem("#text-id"));
    }

    @Test
    @DisplayName("Test: Check if the style is applied correctly to the Text component.")
    public void testApplyStyle() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, compPath);
        TextEditDialog editDialog = new TextEditDialog();
        editDialog.setId("text-id");
        editDialog.setText(testValue);
        editDialog.openStylesTab();
        assertTrue(editDialog.isStyleSelectMenuDisplayed());
        assertTrue(editDialog.isNoStyleOptionSelectedByDefault());
        editDialog.openStyleSelectDropdown();
        editDialog.areExpectedOptionsForNoStyleAppliedPresentInDropdown();
        editDialog.pressArrowDown();
        editDialog.pressEnter();
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        assertTrue(editDialog.componentHasExpectedClassAppliedByTheStyleSystem("#text-id", ".cmp-blue-text"));
        assertTrue(editDialog.componentHasNoSpecificClassAppliedByTheStyleSystem("#text-id", ".cmp-red-text"));
    }

    @Test
    @DisplayName("Test: Check if the style of the Text component is changed correctly when another style is already applied.")
    public void testChangeAppliedStyle() throws TimeoutException, InterruptedException {
        Commons.openEditDialog(editorPage, compPath);
        TextEditDialog editDialog = new TextEditDialog();
        editDialog.setId("text-id");
        editDialog.setText(testValue);
        editDialog.openStylesTab();
        editDialog.openStyleSelectDropdown();
        editDialog.pressArrowDown();
        editDialog.pressEnter();
        Commons.saveConfigureDialog();

        Commons.openEditDialog(editorPage, compPath);
        editDialog.openStylesTab();
        editDialog.openStyleSelectDropdown();
        assertTrue(editDialog.areExpectedOptionsForAppliedStylePresentInDropdown());
        assertTrue(editDialog.isBlueStyleOptionSelected());
        editDialog.pressArrowDown();
        editDialog.pressEnter();
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        assertTrue(editDialog.componentHasExpectedClassAppliedByTheStyleSystem("#text-id", ".cmp-red-text"));
        assertTrue(editDialog.componentHasNoSpecificClassAppliedByTheStyleSystem("#text-id", ".cmp-blue-text"));
    }
}
