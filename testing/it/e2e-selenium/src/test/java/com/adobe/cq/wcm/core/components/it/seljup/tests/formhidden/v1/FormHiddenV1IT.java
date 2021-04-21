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

import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FormHiddenV1IT extends AuthorBaseUITest {

    // element name
    public static String elemName = "hiddenComponent_name";
    // element value
    public static String elemValue = "hiddenComponent_value";
    // element id
    public static String elemId = "hiddenComponent_id";

    protected static String formHiddenRT = "core/wcm/components/form/hidden/v1/hidden";

    protected String testPage;
    protected String hiddenPath;
    protected String compPathHidden;
    private EditorPage editorPage;

    @BeforeEach
    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        compPathHidden = Commons.createProxyComponent(adminClient, formHiddenRT, Commons.proxyPath, null, null);

        // add the core form container component
        hiddenPath = Commons.addComponent(adminClient, compPathHidden, testPage + Commons.relParentCompPath, "container", null);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, compPathHidden);
    }

    @Test
    @DisplayName("Check if Label is mandatory")
    /**
     * Test: Check if Label is mandatory
     */
    public void checkMandatoryFields() {

    }
}
