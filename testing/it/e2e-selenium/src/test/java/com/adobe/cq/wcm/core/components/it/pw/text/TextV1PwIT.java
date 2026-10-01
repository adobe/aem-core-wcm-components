/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.text;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEXT_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group3")
public class TextV1PwIT extends ComponentPwBaseTest {

    protected String textResourceType() {
        return RT_TEXT_V1;
    }

    protected String textPath;

    /** CSS class the inline editor is attached to: v1 renders .cmp-text on the wrapper, v2 only on the inner div. */
    protected String textSelector() {
        return ".cmp-text";
    }

    /** The dialog's rich text widget is backed by a hidden input, which is what gets submitted. */
    protected void setDialogText(String value) {
        dialog().locator("input[name='./text']").evaluate("(e, v) => e.value = v", value);
    }

    protected String createText() throws Exception {
        textPath = addStandaloneComponent(textResourceType(), "text");
        return textPath;
    }

    @Test
    public void testSetTextValueUsingInlineEditor() throws Exception {
        createText();
        clickToolbarAction(textPath, "EDIT");
        contentFrame().locator(textSelector() + ".cq-Editable-dom[contenteditable]").waitFor();
        contentFrame().locator(textSelector() + ".aem-GridColumn p").first()
            .evaluate("(e, html) => e.innerHTML = html", "<b>This</b> is a <i>rich</i> <u>text</u>.");
        page.locator("button[is='coral-button'][title='Save']").click();
        page.waitForTimeout(500);
        assertThat(contentFrame().locator(textSelector() + ".aem-GridColumn p").first())
            .hasText("This is a rich text.");
        reloadEditor();
        assertThat(contentFrame().locator(textSelector() + ".aem-GridColumn p").first())
            .hasText("This is a rich text.");
    }
}
