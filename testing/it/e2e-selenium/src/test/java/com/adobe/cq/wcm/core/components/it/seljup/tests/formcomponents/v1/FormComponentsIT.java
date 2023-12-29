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

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.FormContainerEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.v1.FormComponents;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;


import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormComponentsIT extends AuthorBaseUITest {

    private static String componentName = "formcomponents";
    // root location where form content will be stored
    private static final String userContent = "/content/usergenerated/core-components";

    private EditorPage editorPage;
    private String containerPath;
    private String testPage;
    protected String formContainerRT;
    protected String formTextRT;
    protected String formHiddenRT;
    protected String formOptionsRT;
    protected String formButtonRT;
    protected FormComponents formComponents;


    public void setComponentResources() {
        formContainerRT =RT_FORMCONTAINER_V1;
        formTextRT = RT_FORMTEXT_V1;
        formHiddenRT = RT_FORMHIDDEN_V1;
        formOptionsRT = RT_FORMOPTIONS_V1;
        formButtonRT = RT_FORMBUTTON_V1;
    }

    protected void setup() throws ClientException  {
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

        // inside the form add a hidden field component
        String hiddenPath = Commons.addComponentWithRetry(authorClient, formHiddenRT,containerPath + "/", "hidden");

        // set name and default value for the hidden field component
        data.clear();
        data.put("name", "hiddenName");
        data.put("value", "hiddenValue");
        Commons.editNodeProperties(authorClient, hiddenPath, data);


        // inside the form add a form option component
        String optionPath = Commons.addComponentWithRetry(authorClient, formOptionsRT,containerPath + "/", "options");

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
        Commons.editNodeProperties(authorClient, optionPath, data);

        // add a button to the form
        String buttonPath = Commons.addComponentWithRetry(authorClient, formButtonRT,containerPath + "/", "button");

        // make sure the button is a submit button
        data.clear();
        data.put("type","submit");
        data.put("caption","Submit");
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
        authorClient.deletePageWithRetry(userContent, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
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

        JsonNode json_allForm = authorClient.doGetJson(contentJsonUrl_allForm, 3, HttpStatus.SC_OK);
        Iterator<JsonNode> itr = json_allForm.elements();
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
