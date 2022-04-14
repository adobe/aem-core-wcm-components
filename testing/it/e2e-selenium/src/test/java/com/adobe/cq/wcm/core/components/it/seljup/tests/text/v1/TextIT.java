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

import java.util.concurrent.TimeoutException;

import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InlineEditor;
import com.adobe.cq.testing.selenium.pagewidgets.cq.RichTextToolbar;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.BaseText;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.v1.Text;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TextIT extends AuthorBaseUITest {

    private static String elemName = "text";
    private static String testValue = "<b>This</b> is a <i>rich</i> <u>text</u>.";

    protected String testPage;
    protected String compPath;
    protected EditorPage editorPage;
    protected String textRT;
    protected BaseText text;

    protected void setComponentResources() {
        textRT = Commons.RT_TEXT_V1;
    }

    protected void setup() throws ClientException {
        // create the test page, store page path in 'testPagePath'
        testPage = authorClient.createPage("testPage", "Test Page Title", rootPage, defaultPageTemplate).getSlingPath();

        // add the core form container component
        compPath = Commons.addComponentWithRetry(authorClient, textRT, testPage + Commons.relParentCompPath, "text");

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
        authorClient.deletePageWithRetry(testPage, true, false, RequestConstants.TIMEOUT_TIME_MS, RequestConstants.RETRY_TIME_INTERVAL, HttpStatus.SC_OK);
    }

    /**
     * Test: Check if text is stored/rendered correctly using the inline editor
     * @throws TimeoutException
     */
    @Test
    @DisplayName("Test: Check if text is stored/rendered correctly using the inline editor")
    public void testSetTextValueUsingInlineEditor() throws TimeoutException, InterruptedException {
        InlineEditor inlineEditor = Commons.openInlineEditor(editorPage, compPath);
        RichTextToolbar rte = inlineEditor.getRichTextToolbar();
        Commons.switchContext("ContentFrame");
        text.setContent(testValue);
        Commons.switchToDefaultContext();
        Commons.saveInlineEditor();

        Commons.switchContext("ContentFrame");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(text.isTextRendered(testValue), "Text should have been rendered");

        Commons.switchToDefaultContext();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        editorPage.refresh();
        Commons.switchContext("ContentFrame");
        assertTrue(text.isTextRendered(testValue), "Text should have been rendered");
    }

}
