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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formcomponents.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.FormContainerEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.v1.FormComponents;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;

import org.codehaus.jackson.JsonNode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;


import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormComponentsIT extends AuthorBaseUITest {

    private static String componentName = "formcomponents";
    // root location where form content will be stored
    private static final String userContent = "/content/usergenerated/core-components";

    private String compPathContainer;
    private String compPathText;
    private String compPathHidden;
    private String compPathOptions;
    private String compPathButton;
    private EditorPage editorPage;
    private String containerPath;
    private String testPage;
    protected String formContainerRT;
    protected String formTextRT;
    protected String formHiddenRT;
    protected String formOptionsRT;
    protected String formButtonRT;
    protected FormComponents formComponents;



    protected void setup() throws ClientException  {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        compPathContainer = Commons.createProxyComponent(adminClient, formContainerRT, Commons.proxyPath, null, null);

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
        compPathHidden = Commons.createProxyComponent(adminClient, formHiddenRT, Commons.proxyPath, null, null);

        // inside the form add a hidden field component
        String hiddenPath = Commons.addComponent(adminClient, compPathHidden,containerPath + "/", "hidden", null);

        // set name and default value for the hidden field component
        data.clear();
        data.put("name", "hiddenName");
        data.put("value", "hiddenValue");
        Commons.editNodeProperties(adminClient, hiddenPath, data);

        // create a proxy component
        compPathOptions = Commons.createProxyComponent(adminClient, formOptionsRT, Commons.proxyPath, null, null);;

        // inside the form add a form option component
        String optionPath = Commons.addComponent(adminClient, compPathOptions,containerPath + "/", "options", null);

        // create an option list items
        data.clear();
        data.put("./name", "optionName");
        data.put("./type", "checkbox");
        data.put("./items/item0/selected", "true");
        data.put("./items/item0/text", "text1");
        data.put("./items/item0/value", "value1");
        data.put("./items/item1/selected", "false");
        data.put("./items/item1/text", "text2");
        data.put("./items/item1/value", "value2");
        Commons.editNodeProperties(adminClient, optionPath, data);

        // create a proxy component
        compPathButton = Commons.createProxyComponent(adminClient, formButtonRT, Commons.proxyPath, null, null);

        // add a button to the form
        String buttonPath = Commons.addComponent(adminClient, compPathButton,containerPath + "/", "button", null);

        // make sure the button is a submit button
        data.clear();
        data.put("type","submit");
        data.put("caption","Submit");
        Commons.editNodeProperties(adminClient, buttonPath, data);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        formComponents = new FormComponents();
    }


    public void setComponentResources() {
        formContainerRT = Commons.rtFormContainer_v1;
        formTextRT = Commons.rtFormText_v1;
        formHiddenRT = Commons.rtFormHidden_v1;
        formOptionsRT = Commons.rtFormOptions_v1;
        formButtonRT = Commons.rtFormButton_v1;
    }


    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete any user generated content
        authorClient.deletePageWithRetry(userContent, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy components created
        Commons.deleteProxyComponent(adminClient, compPathContainer);
        Commons.deleteProxyComponent(adminClient, compPathText);
        Commons.deleteProxyComponent(adminClient, compPathHidden);
        Commons.deleteProxyComponent(adminClient, compPathOptions);
        Commons.deleteProxyComponent(adminClient, compPathButton);
    }

    /**
     * Test: Check if the action 'Store Content' works.
     */
    @Test
    @DisplayName("Test: Check if the action 'Store Content' works.")
    public void testStoreContent() throws InterruptedException, ClientException {
        FormContainerEditDialog dialog = formComponents.openEditDialog(containerPath);
        dialog.selectActionType("foundation/components/form/actions/store");
        String actionInputValue = dialog.getActionInputValue();
        String contentJsonUrl_allForm = actionInputValue.substring(0, actionInputValue.length() - 1);
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        $(Selectors.SELECTOR_SUBMIT_BUTTON).click();

        JsonNode json_allForm = adminClient.doGetJson(contentJsonUrl_allForm, 3, HttpStatus.SC_OK);
        Iterator<JsonNode> itr = json_allForm.getElements();
        Boolean present = false;
        while(itr.hasNext()) {
            JsonNode node = itr.next();
            if(node.isObject()) {
                if (node.get("inputName") != null && node.get("inputName").toString().equals("\"inputValue\"") &&
                    node.get("hiddenName") != null && node.get("hiddenName").toString().equals("\"hiddenValue\"") &&
                    node.get("optionName") != null && node.get("optionName").toString().equals("\"value1\"") ) {
                    present = true;
                }

            }
        }

        assertTrue(present, "All values for the form components are not saved");
    }
}
