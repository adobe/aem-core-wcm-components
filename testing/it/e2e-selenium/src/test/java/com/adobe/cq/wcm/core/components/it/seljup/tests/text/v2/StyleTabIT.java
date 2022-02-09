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

package com.adobe.cq.wcm.core.components.it.seljup.tests.text.v2;

import java.util.concurrent.TimeoutException;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.testing.selenium.pageobject.EditorPage;
import com.adobe.cq.testing.selenium.pageobject.PageEditorPage;
import com.adobe.cq.wcm.core.components.it.seljup.AuthorBaseUITest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.TextEditDialog;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class StyleTabIT extends AuthorBaseUITest {

    protected EditorPage editorPage;


    protected void setup() {
        String simplePageTestPath = "/content/core-components/simple-page";
        editorPage = new PageEditorPage(simplePageTestPath);
        editorPage.open();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setup();
    }

    @Test
    @DisplayName("Test: Edit dialog Style Tab, no default style applied")
    public void testStyleTabNoDefaultStyle() throws TimeoutException, InterruptedException {
        String noDefaultStyleTextComponentPath = "/content/core-components/simple-page/jcr:content/root/container/container/container/text";
        Commons.openEditDialog(editorPage, noDefaultStyleTextComponentPath);

        TextEditDialog editDialog = new TextEditDialog();
        editDialog.openStylesTab();
        assertTrue(editDialog.isStyleSelectMenuDisplayed());
        assertTrue(editDialog.isNoStyleOptionSelectedByDefault());
        editDialog.openStyleSelectDropdown();
        editDialog.areExpectedOptionsForNoDefaultStylePresentInDropdown();
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        assertTrue(editDialog.componentHasNoClassesAppliedByTheStyleSystem("#text1"));
    }

    @Test
    @DisplayName("Test: Edit dialog Style Tab, style applied")
    public void testStyleTabWithAppliedStyle() throws TimeoutException, InterruptedException {
        String defaultStyleTextComponentPath =
                "/content/core-components/simple-page/jcr:content/root/container/container/container/text_2093072800";
        Commons.openEditDialog(editorPage, defaultStyleTextComponentPath);

        TextEditDialog editDialog = new TextEditDialog();
        editDialog.openStylesTab();
        assertTrue(editDialog.isStyleSelectMenuDisplayed());
        assertTrue(editDialog.isBlueStyleOptionSelectedByDefault());
        editDialog.openStyleSelectDropdown();
        editDialog.areExpectedOptionsForDefaultStylePresentInDropdown();
        Commons.saveConfigureDialog();
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        assertTrue(editDialog.componentHasExpectedClassAppliedByTheStyleSystem("#text2", ".cmp-blue-text"));
        assertTrue(editDialog.componentHasNoSpecificClassAppliedByTheStyleSystem("#text2", ".cmp-red-text"));
    }
}
