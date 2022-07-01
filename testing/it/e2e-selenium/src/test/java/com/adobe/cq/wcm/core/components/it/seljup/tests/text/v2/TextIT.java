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

package com.adobe.cq.wcm.core.components.it.seljup.tests.text.v2;

import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.TextEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.text.v2.Text;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class TextIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.text.v1.TextIT {

    private static String testXSS = "Hello World! <img =\"/\" onerror=\"alert(String.fromCharCode(88,83,83))\"></img>";
    private static String textXSSProtectedHTL = "Hello World! <img>";
    private static String textXSSProtectedRTE = "Hello World! <img />";

    protected void setComponentResources() {
        textRT = Commons.RT_TEXT_V2;
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setComponentResources();
        text = new Text();
        setup();
    }

    @Test
    @DisplayName("")
    public void testCheckTextWithXSSProtection() throws TimeoutException, InterruptedException, ClientException {
        Commons.openEditDialog(editorPage, compPath);
        TextEditDialog editDialog = new TextEditDialog();
        editDialog.setText(testXSS);
        Commons.saveConfigureDialog();
        Commons.switchContext("ContentFrame");
        assertTrue(text.isTextRenderedWithXSSProtection(textXSSProtectedHTL), "Text should have been rendered");

        JsonNode formContentJson = authorClient.doGetJson(compPath , 1, HttpStatus.SC_OK);
        assertTrue(formContentJson.get("text").toString().trim().equals("\"" + textXSSProtectedRTE + "\""), "The text should be rendered with XSS protection");
    }

}
