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
package com.adobe.cq.wcm.core.components.it.pw.formcontainer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Tag;

import com.adobe.cq.wcm.core.components.it.pw.form.FormGroupPwBase;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMCONTAINER_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMCONTAINER_V2;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group1")
public class FormContainerPwIT extends FormGroupPwBase {

    private static final String USER_CONTENT = "/content/usergenerated";
    private static final String FROM = "from@component.com";
    private static final String SUBJECT = "subject line";
    private static final String MAILTO1 = "mailto1@components.com";
    private static final String MAILTO2 = "mailto2@components.com";
    private static final String INVALID_MAILTO = "mailto2components";
    private static final String CC1 = "cc1@components.com";
    private static final String CC2 = "cc2@components.com";
    private static final String INVALID_CC = "cc2components.com";

    static Stream<String> containerTypes() {
        return Stream.of(RT_FORMCONTAINER_V1, RT_FORMCONTAINER_V2);
    }

    @AfterEach
    void deleteStoredContent() throws Exception {
        if (authorClient.pageExists(USER_CONTENT)) {
            authorClient.deletePageWithRetry(USER_CONTENT, true, false,
                RequestConstants.TIMEOUT_TIME_MS,
                RequestConstants.RETRY_TIME_INTERVAL,
                HttpStatus.SC_OK);
        }
    }

    private String setupContainer(String containerType, String textType, String buttonType) throws Exception {
        String containerPath = addContainer(containerType, "container");
        String inputPath = Commons.addComponentWithRetry(authorClient, textType, containerPath + "/", "text");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("name", "inputName");
        properties.put("defaultValue", "inputValue");
        Commons.editNodeProperties(authorClient, inputPath, properties);
        String buttonPath = Commons.addComponentWithRetry(authorClient, buttonType, containerPath + "/", "button");
        properties.clear();
        properties.put("type", "submit");
        properties.put("title", "submit");
        Commons.editNodeProperties(authorClient, buttonPath, properties);
        reloadEditor();
        return containerPath;
    }

    private void selectAction(String action) {
        selectInCoralSelect("[name='./actionType']", action);
    }

    private void openPageForSubmission() {
        page.navigate(baseUrl + testPage + ".html");
    }

    private void submit() {
        page.locator("button[type='submit']").first().click();
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testStoreContent(String containerType) throws Exception {
        String textType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMTEXT_V1 : RT_FORMTEXT_V2;
        String buttonType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMBUTTON_V1 : RT_FORMBUTTON_V2;
        String containerPath = setupContainer(containerType, textType, buttonType);
        openEditDialog(containerPath);
        selectAction("foundation/components/form/actions/store");
        String contentUrl = dialog().locator("input[name='./action']").inputValue();
        contentUrl = contentUrl.substring(0, contentUrl.length() - 1);
        saveDialog();
        openPageForSubmission();
        submit();
        JsonNode formContent = Commons.waitForJson(authorClient, contentUrl, 1, FormContainerPwIT::hasStoredInput);
        assertTrue(hasStoredInput(formContent), "input value for the form components is not saved");
    }

    private static boolean hasStoredInput(JsonNode json) {
        Iterator<JsonNode> values = json.elements();
        while (values.hasNext()) {
            JsonNode value = values.next();
            if (value.isObject() && value.get("inputName") != null
                && "\"inputValue\"".equals(value.get("inputName").toString())) {
                return true;
            }
        }
        return false;
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testSetContextPath(String containerType) throws Exception {
        String textType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMTEXT_V1 : RT_FORMTEXT_V2;
        String buttonType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMBUTTON_V1 : RT_FORMBUTTON_V2;
        String containerPath = setupContainer(containerType, textType, buttonType);
        String actionInputValue = "/content/usergenerated/xxx";
        openEditDialog(containerPath);
        selectAction("foundation/components/form/actions/store");
        dialog().locator("input[name='./action']").fill(actionInputValue);
        saveDialog();
        openPageForSubmission();
        submit();
        JsonNode json = Commons.waitForJson(authorClient, actionInputValue, 1, node -> node.has("inputName"));
        assertEquals("\"inputValue\"", json.get("inputName").toString());
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testSetThankYouPage(String containerType) throws Exception {
        String textType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMTEXT_V1 : RT_FORMTEXT_V2;
        String buttonType = RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMBUTTON_V1 : RT_FORMBUTTON_V2;
        String containerPath = setupContainer(containerType, textType, buttonType);
        openEditDialog(containerPath);
        selectAction("foundation/components/form/actions/store");
        selectAutocomplete("[name='./redirect']", rootPage);
        saveDialog();
        openPageForSubmission();
        submit();
        assertThat(page).hasURL(baseUrl + rootPage + ".html");
    }

    private void setMailFields(String from, String subject, String[] mailto, String[] cc) {
        selectAction("foundation/components/form/actions/mail");
        dialog().locator("[name='./from']").fill(from);
        dialog().locator("[name='./subject']").fill(subject);
        for (String address : mailto) {
            dialog().locator("coral-multifield[data-granite-coral-multifield-name='./mailto'] > button").click();
            dialog().locator("input[name='./mailto']").last().fill(address);
        }
        for (String address : cc) {
            dialog().locator("coral-multifield[data-granite-coral-multifield-name='./cc'] > button").click();
            dialog().locator("input[name='./cc']").last().fill(address);
        }
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testSetMailAction(String containerType) throws Exception {
        String containerPath = setupContainer(containerType,
            RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMTEXT_V1 : RT_FORMTEXT_V2,
            RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMBUTTON_V1 : RT_FORMBUTTON_V2);
        openEditDialog(containerPath);
        setMailFields(FROM, SUBJECT, new String[] {MAILTO1, MAILTO2}, new String[] {CC1, CC2});
        saveDialog();
        JsonNode json = authorClient.doGetJson(containerPath, 1, HttpStatus.SC_OK);
        assertEquals("\"" + FROM + "\"", json.get("from").toString());
        assertEquals("\"" + SUBJECT + "\"", json.get("subject").toString());
        assertEquals("\"" + MAILTO1 + "\"", json.get("mailto").get(0).toString());
        assertEquals("\"" + MAILTO2 + "\"", json.get("mailto").get(1).toString());
        assertEquals("\"" + CC1 + "\"", json.get("cc").get(0).toString());
        assertEquals("\"" + CC2 + "\"", json.get("cc").get(1).toString());
    }

    private void assertEmailMessage(String containerType, String mailTo, String cc, String message) throws Exception {
        String containerPath = setupContainer(containerType,
            RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMTEXT_V1 : RT_FORMTEXT_V2,
            RT_FORMCONTAINER_V1.equals(containerType) ? RT_FORMBUTTON_V1 : RT_FORMBUTTON_V2);
        openEditDialog(containerPath);
        setMailFields(FROM, SUBJECT, new String[] {mailTo}, new String[] {cc});
        clickDone();
        Locator mailToTooltip = dialog().locator(
            "coral-multifield-item-content input[name='./mailto'] + coral-tooltip[variant='error']");
        Locator ccTooltip = dialog().locator(
            "coral-multifield-item-content input[name='./cc'] + coral-tooltip[variant='error']");
        assertThat(mailToTooltip).hasText("Error: " + message);
        assertThat(ccTooltip).hasText("Error: " + message);
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testInvalidEmailValidationMessages(String containerType) throws Exception {
        assertEmailMessage(containerType, INVALID_MAILTO, INVALID_CC, "Invalid Email Address.");
    }

    @ParameterizedTest
    @MethodSource("containerTypes")
    public void testEmptyEmailValidationMessages(String containerType) throws Exception {
        assertEmailMessage(containerType, "", "", "Please fill out this field.");
    }
}
