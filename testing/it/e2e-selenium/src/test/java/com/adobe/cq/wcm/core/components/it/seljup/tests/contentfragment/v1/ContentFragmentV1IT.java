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

package com.adobe.cq.wcm.core.components.it.seljup.tests.contentfragment.v1;

import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("group2")
public class ContentFragmentV1IT extends AuthorBaseUITest {
    private static String PN_VARIATION_NAME = "./variationName";
    private static String PN_ELEMENT_NAMES = "./elementNames";
    private static String DESCRIPTION = "component-description";
    private static String LATEST_VERSION = "component-latest-version";
    private static String TITLE = "component-title";
    private static String TYPE = "component-type";
    private static String fragmentPath1 = "/content/dam/core-components/contentfragments-tests/simple-fragment";
    private static String fragmentPath2 = "/content/dam/core-components/contentfragments-tests/image-fragment";
    private static String variationName1 = "short";

    private String testPage;
    private String proxyPath;
    private String cmpPath;
    private PageEditorPage editorPage;
    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = adminClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        proxyPath = Commons.createProxyComponent(adminClient, Commons.rtContentFragment_v1, Commons.proxyPath, null, null);

        // add the core form container component
        cmpPath = Commons.addComponent(adminClient, proxyPath, testPage + Commons.relParentCompPath, "formtext", null);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        adminClient.deletePageWithRetry(testPage, true,false, CoreComponentConstants.TIMEOUT_TIME_SEC  * 1000, CoreComponentConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    /**
     * Set the fragment path
     */
    @Test
    @DisplayName("Set the fragment path")
    public void setFragmentPath() {

    }

}
