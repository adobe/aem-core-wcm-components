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

package com.adobe.cq.wcm.core.components.it.seljup.tests.separator.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.separator.v1.Separator;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_SEPARATOR_V1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeparatorIT extends AuthorBaseUITest {

    private String testPage;
    private String cmpPath;
    private EditorPage editorPage;
    private Separator separator;

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the separator component
        cmpPath = Commons.addComponentWithRetry(authorClient, RT_SEPARATOR_V1,testPage + Commons.relParentCompPath, "separator");

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();

        separator = new Separator();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        authorClient.deletePageWithRetry(testPage, true,false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL,  HttpStatus.SC_OK);
    }

    /**
     * Test the decorative separator.
     */
    @Test
    @DisplayName("Test decorative")
    public void testDecorative() throws InterruptedException, TimeoutException {
        Commons.switchContext("ContentFrame");

        // initial state is normal (not decorative)
        assertFalse(separator.isDecorative());

        // set it to decorative
        flipDecorativeInEditDialog();

        assertTrue(separator.isDecorative());

        // set back to normal
        flipDecorativeInEditDialog();

        assertFalse(separator.isDecorative());
    }

    private void flipDecorativeInEditDialog() throws TimeoutException, InterruptedException {
        Commons.switchToDefaultContext();
        Commons.openEditDialog(editorPage, cmpPath);
        separator.getEditDialog().checkDecorative();
        Commons.saveConfigureDialog();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        Commons.switchContext("ContentFrame");
    }
}
