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
 ~ WITHOUT WARRANTIES OR REPRESENTATIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.formcomponents;

import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.AfterEach;
import java.util.stream.Stream;

import com.adobe.cq.wcm.core.components.it.pw.form.FormGroupPwBase;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMCONTAINER_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMCONTAINER_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMHIDDEN_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMHIDDEN_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMOPTIONS_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMOPTIONS_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V2;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group1")
public class FormComponentsPwIT extends FormGroupPwBase {

    private static final String ROOT_USER_CONTENT = "/content/usergenerated/core-components";

    static Stream<String> containerTypes() {
        return Stream.of(RT_FORMCONTAINER_V1, RT_FORMCONTAINER_V2);
    }

    @AfterEach
    void deleteSubmittedContent() throws Exception {
        if (authorClient.pageExists(ROOT_USER_CONTENT)) {
            authorClient.deletePageWithRetry(ROOT_USER_CONTENT, true, false,
                com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants.TIMEOUT_TIME_MS,
                com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants.RETRY_TIME_INTERVAL,
                org.apache.http.HttpStatus.SC_OK);
        }
    }

    private static boolean hasStoredValues(JsonNode json) {
        Iterator<JsonNode> nodes = json.elements();
        while (nodes.hasNext()) {
            JsonNode node = nodes.next();
            if (node.isObject() && node.has("inputName") && node.has("hiddenName") && node.has("optionName")
                && "\"inputValue\"".equals(node.get("inputName").toString())
                && "\"hiddenValue\"".equals(node.get("hiddenName").toString())
                && "\"value1\"".equals(node.get("optionName").toString())) {
                return true;
            }
        }
        return false;
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testStoreContent(String containerType) throws Exception {
        boolean versionTwo = RT_FORMCONTAINER_V2.equals(containerType);
        createFormPage();
        String containerPath = Commons.addComponentWithRetry(authorClient, containerType,
            testPage + Commons.relParentCompPath, "container");

        String inputPath = Commons.addComponentWithRetry(authorClient,
            versionTwo ? RT_FORMTEXT_V2 : RT_FORMTEXT_V1, containerPath + "/", "text");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("name", "inputName");
        properties.put("defaultValue", "inputValue");
        Commons.editNodeProperties(authorClient, inputPath, properties);

        String hiddenPath = Commons.addComponentWithRetry(authorClient,
            versionTwo ? RT_FORMHIDDEN_V2 : RT_FORMHIDDEN_V1, containerPath + "/", "hidden");
        properties.clear();
        properties.put("name", "hiddenName");
        properties.put("value", "hiddenValue");
        Commons.editNodeProperties(authorClient, hiddenPath, properties);

        String optionsPath = Commons.addComponentWithRetry(authorClient,
            versionTwo ? RT_FORMOPTIONS_V2 : RT_FORMOPTIONS_V1, containerPath + "/", "options");
        properties.clear();
        properties.put("./name", "optionName");
        properties.put("./type", "checkbox");
        properties.put("./items/item0/selected", "true");
        properties.put("./items/item0/text", "text1");
        properties.put("./items/item0/value", "value1");
        properties.put("./items/item1/selected", "false");
        properties.put("./items/item1/text", "text2");
        properties.put("./items/item1/value", "value2");
        Commons.editNodeProperties(authorClient, optionsPath, properties);

        String buttonPath = Commons.addComponentWithRetry(authorClient,
            versionTwo ? RT_FORMBUTTON_V2 : RT_FORMBUTTON_V1, containerPath + "/", "button");
        properties.clear();
        properties.put("type", "submit");
        properties.put("caption", "Submit");
        Commons.editNodeProperties(authorClient, buttonPath, properties);

        openEditor(testPage);
        openEditDialog(containerPath);
        selectInCoralSelect("[name='./actionType']", "foundation/components/form/actions/store");
        String actionUrl = dialog().locator("input[name='./action']").inputValue();
        actionUrl = actionUrl.substring(0, actionUrl.length() - 1);
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        page.locator("button[type='submit']").first().click();

        JsonNode saved = Commons.waitForJson(authorClient, actionUrl, 3, FormComponentsPwIT::hasStoredValues);
        assertTrue(hasStoredValues(saved), "All values for the form components are not saved");
    }
}
