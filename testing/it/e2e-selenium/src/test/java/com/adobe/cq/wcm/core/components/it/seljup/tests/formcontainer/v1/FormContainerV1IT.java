/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormComponents.FormContainerConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pageobject.EditorPage;
import com.adobe.qe.selenium.pageobject.PageEditorPage;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.codehaus.jackson.JsonNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormContainerV1IT extends AuthorBaseUITest {
    // root location where form content will be stored
    private static final String userContent = "/content/usergenerated/core-components";

    // some test values
    private static final String from = "from@component.com";
    private static final String subject = "subject line";
    private static final String mailto1 = "mailto1@components.com";
    private static final String mailto2 = "mailto2@components.com";
    private static final String cc1 = "cc1@components.com";
    private static final String cc2 = "cc2@components.com";

    private static String formContainerRT_v1 = "core/wcm/components/form/container/v1/container";
    private static String formTextRT = "core/wcm/components/form/text/v1/text";
    private static String formButtonRT = "core/wcm/components/form/button/v1/button";

    private String compPathContainer;
    private String containerPath;
    private String compPathText;
    private String compPathButton;
    private EditorPage editorPage;

    protected String testPage;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        //testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        testPage = Commons.createPage(adminClient,"testPage", "Test Page Title", rootPage, defaultPageTemplate,"core/wcm/tests/components/test-page-v2");

        // create a proxy component
        compPathContainer = Commons.createProxyComponent(adminClient, formContainerRT_v1, Commons.proxyPath, null, null);

        // add the core form container component
        containerPath = Commons.addComponent(adminClient, compPathContainer,testPage + Commons.relParentCompPath, "container", null);

        // create a proxy component
        compPathText = Commons.createProxyComponent(adminClient, formTextRT, Commons.proxyPath, null, null);

        // inside the form add a form text input field
        String inputPath = Commons.addComponent(adminClient, compPathText,containerPath + "/", "text", null);

        // set name and default value for the input field
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("name", "inputName");
        data.put("defaultValue", "inputValue");
        Commons.editNodeProperties(adminClient, inputPath, data);

        // create a proxy component
        compPathButton = Commons.createProxyComponent(adminClient, formButtonRT, Commons.proxyPath, null, null);

        // add a button to the form
        String buttonPath = Commons.addComponent(adminClient, compPathButton,containerPath + "/", "button", null);

        // create an option list items
        data.clear();
        data.put("type", "submit");
        data.put("title", "submit");
        Commons.editNodeProperties(adminClient, buttonPath, data);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete any user generated content
        authorClient.deletePageWithRetry(userContent, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy components created
        Commons.deleteProxyComponent(adminClient, compPathContainer);
        Commons.deleteProxyComponent(adminClient, compPathText);
        Commons.deleteProxyComponent(adminClient, compPathButton);
    }

    /**
     * Test: Check if the action 'Store Content' works.
     */
    @Test
    @DisplayName("Test: Check if the action 'Store Content' works.")
    public void storeContent() throws ClientException, InterruptedException {
        Commons.openConfigureDialog(containerPath);
        FormContainerConfigDialog dialog = new FormContainerConfigDialog();
        dialog.selectActionType("foundation/components/form/actions/store");
        String actionInputValue = dialog.getActionInputValue();
        String contentJsonUrl_allForm = actionInputValue.substring(0, actionInputValue.length() - 1);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();

        JsonNode json_allForm = adminClient.doGetJson(contentJsonUrl_allForm, 1, HttpStatus.SC_OK);
        Iterator<JsonNode> itr = json_allForm.getElements();
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
    public void setContextPath() throws InterruptedException, ClientException {
        Commons.openConfigureDialog(containerPath);
        FormContainerConfigDialog dialog = new FormContainerConfigDialog();
        dialog.selectActionType("foundation/components/form/actions/store");
        String actionInputValue = userContent + "/xxx";
        dialog.setActionInputValue(actionInputValue);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();
        JsonNode formContentJson = adminClient.doGetJson(actionInputValue , 1, HttpStatus.SC_OK);
        assertTrue(formContentJson.get("inputName").toString().equals("\"inputValue\""),"inputName field should be saved as inputValue");
    }

    /**
     * Test: set the thank You page path
     *
     */
    @Test
    @DisplayName("Test: set the thank You page path")
    public void setThankYouPage() throws InterruptedException {
        Commons.openConfigureDialog(containerPath);
        FormContainerConfigDialog dialog = new FormContainerConfigDialog();
        dialog.selectActionType("foundation/components/form/actions/store");
        Commons.selectInAutocomplete("[name='./redirect']", rootPage);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();
        Commons.switchToDefaultContext();
        assertTrue(Commons.getCurrentUrl().endsWith(rootPage+".html"),"");
    }

    /**
     * Test: check if 'Mail' action works.
     *
     */
    @Test
    @DisplayName("Test: check if 'Mail' action works.")
    public void setMailAction() throws InterruptedException, ClientException {
        Commons.openConfigureDialog(containerPath);
        FormContainerConfigDialog dialog = new FormContainerConfigDialog();
        dialog.setMailActionFields(from,subject,new String[] {mailto1,mailto2}, new String[] {cc1, cc2});
        Commons.saveConfigureDialog();
        JsonNode formContentJson = adminClient.doGetJson(containerPath , 1, HttpStatus.SC_OK);
        assertTrue(formContentJson.get("from").toString().equals("\""+from+"\""));
        assertTrue(formContentJson.get("subject").toString().equals("\""+subject+"\""));
        assertTrue(formContentJson.get("mailto").get(0).toString().equals("\""+mailto1+"\""));
        assertTrue(formContentJson.get("mailto").get(1).toString().equals("\""+mailto2+"\""));
        assertTrue(formContentJson.get("cc").get(0).toString().equals("\""+cc1+"\""));
        assertTrue(formContentJson.get("cc").get(1).toString().equals("\""+cc2+"\""));
    }

}
