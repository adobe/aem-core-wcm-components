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

package com.adobe.cq.wcm.core.components.it.seljup.tests.text.v1;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InlineEditor;
import com.adobe.cq.testing.selenium.pagewidgets.cq.RichTextToolbar;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.components.text.BaseText;
import com.adobe.cq.wcm.core.components.it.seljup.components.text.v1.Text;
import com.adobe.cq.wcm.core.components.it.seljup.constant.CoreComponentConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group2")
public class TextIT extends AuthorBaseUITest {

    private static String elemName = "text";
    private static String testValue = "<b>This</b> is a <i>rich</i> <u>text</u>.";


    private String proxyPath;

    protected String testPage;
    protected String compPath;
    protected EditorPage editorPage;
    protected String textRT;
    protected BaseText text;

    protected void setComponentResources() {
        textRT = Commons.rtText_v1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // create a proxy component
        proxyPath = Commons.createProxyComponent(adminClient, textRT, Commons.proxyPath, null, null);

        // add the core form container component
        compPath = Commons.addComponent(adminClient, proxyPath, testPage + Commons.relParentCompPath, "text", null);

        // open the page in the editor
        editorPage = new PageEditorPage(testPage);
        editorPage.open();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        text = new Text();
        setup();
    }

    @AfterEach
    public void cleanupAfterEach() throws ClientException, InterruptedException {
        // delete the test page we created
        authorClient.deletePageWithRetry(testPage, true, false, CoreComponentConstants.TIMEOUT_TIME_MS, CoreComponentConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);

        // delete the proxy component created
        Commons.deleteProxyComponent(adminClient, proxyPath);
    }

    /**
     * Test: Check if text is stored/rendered correctly using the inline editor
     * @throws TimeoutException
     */
    @Test
    @DisplayName("Test: Check if text is stored/rendered correctly using the inline editor")
    public void testSetTextValueUsingInlineEditor() throws TimeoutException {
        InlineEditor inlineEditor = Commons.openInlineEditor(editorPage, compPath);
        RichTextToolbar rte = inlineEditor.getRichTextToolbar();
        Commons.switchContext("ContentFrame");
        text.setContent(testValue);
        Commons.switchToDefaultContext();
        Commons.saveInlineEditor();

        Commons.switchContext("ContentFrame");
        assertTrue(text.isTextRendered(testValue), "Text should have been rendered");

        Commons.switchToDefaultContext();
        editorPage.refresh();
        Commons.switchContext("ContentFrame");
        assertTrue(text.isTextRendered(testValue), "Text should have been rendered");
    }

}
