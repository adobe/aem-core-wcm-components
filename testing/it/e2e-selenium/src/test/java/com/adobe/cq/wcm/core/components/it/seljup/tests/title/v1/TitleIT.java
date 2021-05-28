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

package com.adobe.cq.wcm.core.components.it.seljup.tests.title.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.title.TitleEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.title.v1.Title;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class TitleIT extends AuthorBaseUITest {

    private static String componentName = "title";

    protected String proxyPath;
    protected String testPage;
    protected String redirectPage;
    protected String cmpPath;
    protected EditorPage editorPage;
    protected Title title;

    protected String clientlibs;
    protected String titleRT;

    public void setupResources() {
        clientlibs = "core.wcm.components.title.v1";
        titleRT = Commons.rtTitle_v1;
    }

    public void setup() throws ClientException {
        // 1.
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();
        redirectPage = authorClient.createPage("redirectPage", "Redirect Page Title", rootPage, defaultPageTemplate).getSlingPath();
        // 2.
        String policySuffix = "/structure/page/new_policy";
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("jcr:title", "New Policy");
        data.put("sling:resourceType", "wcm/core/components/policy/policy");
        data.put("clientlibs", clientlibs);
        String policyPath1 = "/conf/" + label + "/settings/wcm/policies/core-component/components";
        String policyPath = Commons.createPolicy(adminClient, policySuffix, data, policyPath1);

        // 3.
        String policyLocation = "core-component/components";
        String policyAssignmentPath = defaultPageTemplate + "/policies/jcr:content";
        data.clear();
        data.put("cq:policy", policyLocation + policySuffix);
        data.put("sling:resourceType", "wcm/core/components/policies/mappings");
        Commons.assignPolicy(adminClient, "", data, policyAssignmentPath);


        // 4.
        proxyPath = Commons.createProxyComponent(adminClient, titleRT, Commons.proxyPath, null, null);

        // 5.
        cmpPath = Commons.addComponent(adminClient, proxyPath, testPage + Commons.relParentCompPath, componentName, null);

        // 6.
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        // 7.
        title = new Title();
    }

    /**
     * Before Test Case
     *
     * @throws ClientException
     */
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    /**
     * After Test Case
     *
     * 1. delete the test proxy component
     * 2. delete the test page
     *
     * @throws ClientException
     * @throws InterruptedException
     */

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // 1.
        Commons.deleteProxyComponent(adminClient, proxyPath);

        // 2.
        authorClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);

    }

    /**
     * Test: Set the title value using the design dialog.
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Set the title value using the design dialog.")
        public void SetTitleValueUsingConfigDialog() throws TimeoutException, InterruptedException {
        String titleValue = "Content name";
        Commons.openEditDialog(editorPage, cmpPath);
        TitleEditDialog editDialog = title.getEditDialog();
        editDialog.setTitle(titleValue);
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleSet(titleValue), "Title should be set");
        Commons.switchToDefaultContext();
    }

    /**
     * Test: Check the existence of all available title types.
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check the existence of all available title types.")
    public void testCheckExistenceOfTitleTypes() throws TimeoutException, InterruptedException {
        // open the dialog
        Commons.openEditDialog(editorPage, cmpPath);
        // check if all default title sizes are there
        assertTrue(title.getEditDialog().isAllDefaultTitleTypesPresent(), "All default title sizes should be present in edit dialog ");
    }

    /**
     * Test: Check if setting the title type works.
     *
     * @throws TimeoutException
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Test: Check if setting the title type works.")
    public void testSetTitleType() throws TimeoutException, InterruptedException {
        // open the dialog
        Commons.openEditDialog(editorPage, cmpPath);
        // set title type
        title.getEditDialog().selectTitleType("6");
        Commons.saveConfigureDialog();

        Commons.switchContext("ContentFrame");
        assertTrue(title.isTitleWithTypePresent("6"), "Title with type h6 should be present");
    }

}
