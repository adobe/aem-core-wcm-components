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

package com.adobe.cq.wcm.core.components.it.seljup.tests.formhidden.v1;

import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formhidden.v1.FormHidden;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formhidden.FormHiddenEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMHIDDEN_V1;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group1")
public class FormHiddenIT extends AuthorBaseUITest {

    // element name
    public static String elemName = "hiddenComponent_name";
    // element value
    public static String elemValue = "hiddenComponent_value";
    // element id
    public static String elemId = "hiddenComponent_id";

    protected static String formHiddenRT;

    protected String testPage;
    protected String hiddenPath;
    protected String compPathHidden;
    private EditorPage editorPage;
    private FormHidden formHidden;

    public void setComponentResources() {
        formHiddenRT = RT_FORMHIDDEN_V1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        hiddenPath = Commons.addComponentWithRetry(authorClient, formHiddenRT, testPage + Commons.relParentCompPath, "formhidden");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        formHidden = new FormHidden();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }


    /**
     * Test: Check if Label is mandatory
     */
    @Test
    @DisplayName("Check if Label is mandatory")
    public void testCheckMandatoryFields() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, hiddenPath);
        Commons.saveConfigureDialog();
        assertTrue(Commons.iseditDialogVisible(),"Config Dialog should be visible");
        assertTrue(formHidden.getConfigDialog().isMandatoryFieldsInvalid(),"Mandatory field Name should be invalid");
    }

    /**
     * Test: Set element name
     */
    @Test
    @DisplayName("Test: Set element name")
    public void testSetElementName() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, hiddenPath);
        formHidden.getConfigDialog().setMandatoryFields(elemName);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(formHidden.isNameSet(elemName),"Name should be set");
        Commons.switchToDefaultContext();
    }

    /**
     * Test: Set element value
     */
    @Test
    @DisplayName("Test: Set element value")
    public void testSetElementValue() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, hiddenPath);
        FormHiddenEditDialog dialog = formHidden.getConfigDialog();
        dialog.setMandatoryFields(elemName);
        dialog.setValue(elemValue);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        formHidden.isValueSet(elemName);
        Commons.switchToDefaultContext();
    }

    /**
     * Test: Set element identifier
     */
    @Test
    @DisplayName("Test: Set element identifier")
    public void testSetElementId() throws InterruptedException, TimeoutException {
        Commons.openEditDialog(editorPage, hiddenPath);
        FormHiddenEditDialog dialog = formHidden.getConfigDialog();
        dialog.setMandatoryFields(elemName);
        dialog.setId(elemId);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        formHidden.isIdSet(elemId);
        Commons.switchToDefaultContext();
    }
}
