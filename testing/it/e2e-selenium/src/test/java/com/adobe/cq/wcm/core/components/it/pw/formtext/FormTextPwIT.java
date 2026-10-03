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
package com.adobe.cq.wcm.core.components.it.pw.formtext;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMTEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group1")
public class FormTextPwIT extends FormTextPwBase {

    private static final String POLICY_COMPONENT = RT_FORMTEXT_V2.substring(RT_FORMTEXT_V2.lastIndexOf("/"));

    @Override
    protected String formTextResourceType() {
        return RT_FORMTEXT_V2;
    }

    private static Map<String, String> map(String key, String value) {
        Map<String, String> properties = new HashMap<>();
        properties.put(key, value);
        return properties;
    }

    private void enableValidationPolicies(boolean withClientlib) throws Exception {
        createComponentPolicy(POLICY_COMPONENT, map("displayValidation", "true"));
        if (withClientlib) {
            createPagePolicy(map("clientlibs", "core.wcm.components.form.text.v2"));
        }
    }

    private void assertValidationMessagePresence(boolean expected) {
        saveWithHelpMessage("text", null);
        assertThat(validationMessage()).hasCount(expected ? 1 : 0);
        setOptionTypeAndSave("textarea");
        assertThat(field("textarea")).isVisible();
        assertThat(validationMessage()).hasCount(expected ? 1 : 0);
    }

    @Test
    public void testDisplayValidationMessageNotExists() {
        assertValidationMessagePresence(false);
    }

    @Test
    public void testDisplayValidationMessageDisabled() throws Exception {
        createComponentPolicy(POLICY_COMPONENT, map("displayValidation", "false"));
        assertValidationMessagePresence(false);
    }

    @Test
    public void testDisplayValidationMessageEnabled() throws Exception {
        enableValidationPolicies(false);
        assertValidationMessagePresence(true);
    }

    private void saveRequiredAndReload(String type, String requiredMessage, String constraintMessage) {
        openEditDialog(formTextPath);
        setOptionType(type);
        setMandatoryFields();
        openTab("constraints");
        checkCoralCheckbox("./required");
        if (requiredMessage != null) {
            dialog().locator("textarea[name='./requiredMessage']").fill(requiredMessage);
        }
        if (constraintMessage != null) {
            dialog().locator("textarea[name='./constraintMessage']").fill(constraintMessage);
        }
        saveDialog();
        assertThat(field(type)).isVisible();
        reloadEditor();
    }

    private void testValidationMessage(String type, String requiredMessage) throws Exception {
        enableValidationPolicies(true);
        saveRequiredAndReload(type, requiredMessage, null);
        if (requiredMessage == null) {
            assertThat(validationMessage()).containsText("fill out");
        } else {
            assertThat(validationMessage()).hasText(requiredMessage);
        }
        typeValue("text");
        assertThat(validationMessage()).isHidden();
    }

    @Test
    public void testDisplayValidationMessage() throws Exception {
        testValidationMessage("text", null);
    }

    @Test
    public void testDisplayValidationMessageForTextArea() throws Exception {
        testValidationMessage("textarea", null);
    }

    @Test
    public void testDisplayCustomValidationMessage() throws Exception {
        testValidationMessage("text", "Custom required message");
    }

    @Test
    public void testDisplayCustomValidationMessageForTextArea() throws Exception {
        testValidationMessage("textarea", "Custom required message");
    }

    @Test
    public void testDisplayCustomValidationMessageForEmail() throws Exception {
        enableValidationPolicies(true);
        saveRequiredAndReload("email", "Custom required message", "Custom constraint message");
        assertThat(validationMessage()).hasText("Custom required message");
        typeValue("email");
        assertThat(validationMessage()).hasText("Custom constraint message");
        typeValue("@email.em");
        assertThat(validationMessage()).isHidden();
    }
}
