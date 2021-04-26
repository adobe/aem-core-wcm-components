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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formoptions.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions.FormOptions;
import com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions.FormOptionsConfigDialog;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormOptionsV1IT extends AuthorBaseUITest {

    protected static String formOptionsRT;

    protected String testPage;
    private String compPath;
    private String optionPath;
    private FormOptions formOptions;
    private EditorPage editorPage;

    // element name
    private String elemName = "form_options";
    // title value
    private String title = "Options";
    // help message
    private String helpMessage = "This is an help message";
    // value for 'value' field
    private String value = "value1";
    // value for 'text' field
    private String text = "text1";

    public void setComponentResources() {
        formOptionsRT = Commons.rtFormOptions_v1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        compPath = Commons.createProxyComponent(adminClient, formOptionsRT, Commons.proxyPath, null, null);

        // add the core form container component
        optionPath = Commons.addComponent(adminClient, compPath, testPage + Commons.relParentCompPath, "container", null);

        formOptions = new FormOptions();
        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, compPath);
    }

    /**
     * Test: Check the mandatory fields
     */
    @Test
    @DisplayName("Test: Check the mandatory fields")
    public void checkMandatoryFields() throws InterruptedException {
        Commons.openConfigureDialog(optionPath);
        Commons.saveConfigureDialog();
        assertTrue(Commons.isConfigDialogVisible(),"Config Dialog should be visible");
        assertTrue(formOptions.getConfigDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    /**
     * Test: Set title text
     */
    @Test
    @DisplayName("Test: Set title text")
    public void setTitle() throws InterruptedException, TimeoutException {
        Commons.openConfigureDialog(optionPath);
        FormOptionsConfigDialog configDialog = formOptions.getConfigDialog();
        configDialog.setMandatoryFields(elemName, title);
        configDialog.addOption(value, text);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formOptions.isTitleRendered(title), "Title should be rendered");
    }

}
