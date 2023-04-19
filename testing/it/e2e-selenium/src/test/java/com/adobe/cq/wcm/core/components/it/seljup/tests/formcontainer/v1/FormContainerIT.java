/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formcontainer.v1;

import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.FormContainerEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.v1.FormComponents;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormContainerIT extends AuthorBaseUITest {
    // root location where form content will be stored
    private static final String userContent = "/content/usergenerated";

    // some test values
    private static final String from = "from@component.com";
    private static final String subject = "subject line";
    private static final String mailto1 = "mailto1@components.com";
    private static final String mailto2 = "mailto2@components.com";
    private static final String invalidMailto = "mailto2components";
    private static final String emptyMailto = "";
    private static final String cc1 = "cc1@components.com";
    private static final String cc2 = "cc2@components.com";
    private static final String invalidCC = "cc2components.com";
    private static final String emptyCC = "";
    private String containerPath;
    private EditorPage editorPage;
    private String testPage;

    protected String formContainerRT;
    protected String formTextRT;
    protected String formButtonRT;
    protected FormComponents formComponents;

    public void setComponentResources() {
        formContainerRT = RT_FORMCONTAINER_V1;
        formTextRT = RT_FORMTEXT_V1;
        formButtonRT = RT_FORMBUTTON_V1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        containerPath = Commons.addComponentWithRetry(authorClient, formContainerRT,testPage + Commons.relParentCompPath, "container");

        // inside the form add a form text input field
        String inputPath = Commons.addComponentWithRetry(authorClient, formTextRT,containerPath + "/", "text");

        // set name and default value for the input field
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", "inputName");
        data.put("defaultValue", "inputValue");
        Commons.editNodeProperties(authorClient, inputPath, data);

        // add a button to the form
        String buttonPath = Commons.addComponentWithRetry(authorClient, formButtonRT,containerPath + "/", "button");

        // create an option list items
        data.clear();
        data.put("type", "submit");
        data.put("title", "submit");
        Commons.editNodeProperties(authorClient, buttonPath, data);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        formComponents = new FormComponents();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete any user generated content
        if(authorClient.pageExists(userContent)) {
            authorClient.deletePageWithRetry(userContent, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
        }
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }

    /**
     * Test: Check if the action 'Store Content' works.
     */
    @Test
    @DisplayName("Test: Check if the action 'Store Content' works.")
    public void testStoreContent() throws ClientException, InterruptedException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.selectActionType("foundation/components/form/actions/store");
        String actionInputValue = dialog.getActionInputValue();
        String contentJsonUrl_allForm = actionInputValue.substring(0, actionInputValue.length() - 1);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();

        JsonNode json_allForm = authorClient.doGetJson(contentJsonUrl_allForm, 1, HttpStatus.SC_OK);
        Iterator<JsonNode> itr = json_allForm.elements();
        Boolean present = false;
        while(itr.hasNext()) {
            JsonNode node = itr.next();
            if(node.isObject()) {
                if (node.get("inputName") != null && node.get("inputName").toString().equals("\"inputValue\"")) {
                    present = true;
                }
            }
        }
        assertTrue(present, "input value for the form components is not saved");
    }

    /**
     * Test: set your own content path
     */
    @Test
    @DisplayName("Test: set your own content path")
    public void testSetContextPath() throws InterruptedException, ClientException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.selectActionType("foundation/components/form/actions/store");
        String actionInputValue = userContent + "/xxx";
        dialog.setActionInputValue(actionInputValue);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();
        JsonNode formContentJson = authorClient.doGetJson(actionInputValue , 1, HttpStatus.SC_OK);
        assertTrue(formContentJson.get("inputName").toString().equals("\"inputValue\""),"inputName field should be saved as inputValue");
    }

    /**
     * Test: set the thank You page path
     *
     */
    @Test
    @DisplayName("Test: set the thank You page path")
    public void testSetThankYouPage() throws InterruptedException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.selectActionType("foundation/components/form/actions/store");
        Commons.selectInAutocomplete("[name='./redirect']", rootPage);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();
        Commons.switchToDefaultContext();
        assertTrue(Commons.getCurrentUrl().endsWith(rootPage+".html"),"Current page should be thank you page set after redirection");
    }

    /**
     * Test: check if 'Mail' action works.
     *
     */
    @Test
    @DisplayName("Test: check if 'Mail' action works.")
    public void testSetMailAction() throws InterruptedException, ClientException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.setMailActionFields(from,subject,new String[] {mailto1,mailto2}, new String[] {cc1, cc2});
        Commons.saveConfigureDialog();
        JsonNode formContentJson = authorClient.doGetJson(containerPath , 1, HttpStatus.SC_OK);
        assertTrue(formContentJson.get("from").toString().equals("\""+from+"\""));
        assertTrue(formContentJson.get("subject").toString().equals("\""+subject+"\""));
        assertTrue(formContentJson.get("mailto").get(0).toString().equals("\""+mailto1+"\""));
        assertTrue(formContentJson.get("mailto").get(1).toString().equals("\""+mailto2+"\""));
        assertTrue(formContentJson.get("cc").get(0).toString().equals("\""+cc1+"\""));
        assertTrue(formContentJson.get("cc").get(1).toString().equals("\""+cc2+"\""));
    }

    /**
     * Test: check if invalid 'Mail' validation messages are displayed.
     *
     */
    @Test
    @DisplayName("Test: check if invalid 'Mail' validation messages are displayed.")
    public void testInvalidEmailValidationMessages() throws InterruptedException, ClientException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.setMailActionFields(from,subject,new String[] {invalidMailto}, new String[] {invalidCC});
        Commons.saveConfigureDialog();
        String mailToErrorMessage = "coral-multifield-item-content input[name='./mailto'] + coral-tooltip[variant='error']";
        String ccErrorMessage = "coral-multifield-item-content input[name='./mailto'] + coral-tooltip[variant='error']";
        assertTrue($(mailToErrorMessage).isDisplayed());
        assertEquals($(mailToErrorMessage).getText(), "Error: Invalid Email Address.");
        assertTrue($(ccErrorMessage).isDisplayed());
        assertEquals($(ccErrorMessage).getText(), "Error: Invalid Email Address.");
    }

    /**
     * Test: check if empty 'Mail' validation messages are displayed.
     *
     */
    @Test
    @DisplayName("Test: check if empty 'Mail' validation messages are displayed.")
    public void testEmptyEmailValidationMessages() throws InterruptedException, ClientException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.setMailActionFields(from,subject,new String[] {emptyMailto}, new String[] {emptyCC});
        Commons.saveConfigureDialog();
        String mailToErrorMessage = "coral-multifield-item-content input[name='./mailto'] + coral-tooltip[variant='error']";
        String ccErrorMessage = "coral-multifield-item-content input[name='./mailto'] + coral-tooltip[variant='error']";
        assertTrue($(mailToErrorMessage).isDisplayed());
        assertEquals($(mailToErrorMessage).getText(), "Error: Please fill out this field.");
        assertTrue($(ccErrorMessage).isDisplayed());
        assertEquals($(ccErrorMessage).getText(), "Error: Please fill out this field.");
    }

}
