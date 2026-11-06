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
package com.adobe.cq.wcm.core.components.it.pw.formoptions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Tag;
import java.util.stream.Stream;

import com.adobe.cq.wcm.core.components.it.pw.form.FormGroupPwBase;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_FORMOPTIONS_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_FORMOPTIONS_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Tag("playwright-group1")
public class FormOptionsPwIT extends FormGroupPwBase {

    private static final String NAME = "form_options";
    private static final String TITLE = "Options";
    private static final String HELP = "This is an help message";
    private static final String VALUE = "value1";
    private static final String TEXT = "text1";

    static Stream<String> resourceTypes() {
        return Stream.of(RT_FORMOPTIONS_V1, RT_FORMOPTIONS_V2);
    }

    private String prepareOptions(String resourceType, String type, boolean help, boolean selected, boolean disabled)
        throws Exception {
        String path = addStandaloneComponent(resourceType, "formoption");
        openEditDialog(path);
        selectInCoralSelect("[name='./type']", type);
        dialog().locator("[name='./name']").fill(NAME);
        dialog().locator("[name='./jcr:title']:visible").first().fill(TITLE);
        Locator values = dialog().locator("input[name$='./value']");
        int existingOptions = values.count();
        dialog().locator("button[coral-multifield-add='']").click();
        assertThat(values).hasCount(existingOptions + 1);
        values.last().fill(VALUE);
        dialog().locator("input[name$='./text']").last().fill(TEXT);
        if (help) {
            dialog().locator("[name='./helpMessage']").fill(HELP);
        }
        if (selected) {
            dialog().locator("[name$='selected'] input[type='checkbox']").check();
        }
        if (disabled) {
            dialog().locator("[name$='disabled'] input[type='checkbox']").check();
        }
        saveDialog();
        return path;
    }

    private Locator helpMessage(String resourceType) {
        return contentFrame().locator(fieldSelector(resourceType, ".help-block", ".cmp-form-options__help-message"))
            .first();
    }

    private Locator description(String resourceType) {
        return contentFrame().locator(fieldSelector(resourceType, ".form-group input ~ span",
            ".cmp-form-options__field-description")).first();
    }

    private String fieldSelector(String resourceType, String versionOne, String versionTwo) {
        return resourceType.equals(RT_FORMOPTIONS_V1) ? versionOne : versionTwo;
    }

    private void assertOptionType(String resourceType, String type) throws Exception {
        prepareOptions(resourceType, type, false, false, false);
        String selector;
        switch (type) {
            case "checkbox":
                selector = fieldSelector(resourceType, ".form-group.checkbox", ".cmp-form-options__field--checkbox");
                break;
            case "radio":
                selector = fieldSelector(resourceType, ".form-group.radio", ".cmp-form-options__field--radio");
                break;
            case "drop-down":
                selector = fieldSelector(resourceType, ".form-group.drop-down", ".cmp-form-options__field--drop-down");
                break;
            default:
                selector = fieldSelector(resourceType, ".form-group.multi-drop-down", ".cmp-form-options__field--multi-drop-down");
        }
        assertThat(contentFrame().locator(selector)).isVisible();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testCheckMandatoryFields(String resourceType) throws Exception {
        String path = addStandaloneComponent(resourceType, "formoption");
        openEditDialog(path);
        clickDone();
        assertThat(dialog()).isVisible();
        assertThat(dialog().locator("[name='./name'][invalid='true'], [name='./name'][aria-invalid='true']")).hasCount(1);
        assertThat(dialog().locator("[name='./jcr:title'][invalid='']")).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetTitle(String resourceType) throws Exception {
        prepareOptions(resourceType, "checkbox", false, false, false);
        assertThat(contentFrame().locator("legend")).hasText(TITLE);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetElementName(String resourceType) throws Exception {
        prepareOptions(resourceType, "checkbox", false, false, false);
        assertThat(contentFrame().locator("input[name='" + NAME + "']")).isVisible();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetHelpMessage(String resourceType) throws Exception {
        prepareOptions(resourceType, "checkbox", true, false, false);
        assertThat(helpMessage(resourceType)).isVisible();
        assertThat(helpMessage(resourceType)).hasText(HELP);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetCheckbox(String resourceType) throws Exception {
        assertOptionType(resourceType, "checkbox");
        assertThat(description(resourceType)).hasText(TEXT);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetRadioButton(String resourceType) throws Exception {
        assertOptionType(resourceType, "radio");
        assertThat(description(resourceType)).hasText(TEXT);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetDropDown(String resourceType) throws Exception {
        assertOptionType(resourceType, "drop-down");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetMultiSelectDropDown(String resourceType) throws Exception {
        assertOptionType(resourceType, "multi-drop-down");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetActiveOptionForCheckbox(String resourceType) throws Exception {
        prepareOptions(resourceType, "checkbox", false, true, false);
        assertThat(contentFrame().locator("input[type='checkbox'][value='" + VALUE + "']")).isChecked();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetActiveOptionForRadioButton(String resourceType) throws Exception {
        prepareOptions(resourceType, "radio", false, true, false);
        assertThat(contentFrame().locator("input[type='radio'][value='" + VALUE + "']")).isChecked();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetActiveOptionForDropDown(String resourceType) throws Exception {
        prepareOptions(resourceType, "drop-down", false, true, false);
        assertThat(contentFrame().locator("option[value='" + VALUE + "']")).hasAttribute("selected", "");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetDisabledOptionForCheckbox(String resourceType) throws Exception {
        prepareOptions(resourceType, "checkbox", false, false, true);
        assertThat(contentFrame().locator("input[type='checkbox'][value='" + VALUE + "']")).isDisabled();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetDisabledOptionForRadioButton(String resourceType) throws Exception {
        prepareOptions(resourceType, "radio", false, false, true);
        assertThat(contentFrame().locator("input[type='radio'][value='" + VALUE + "']")).isDisabled();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetDisabledOptionForDropDown(String resourceType) throws Exception {
        prepareOptions(resourceType, "drop-down", false, false, true);
        assertThat(contentFrame().locator("option[value='" + VALUE + "']")).isDisabled();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetDisabledOptionForMultiSelectDropDown(String resourceType) throws Exception {
        prepareOptions(resourceType, "multi-drop-down", false, false, true);
        assertThat(contentFrame().locator("option[value='" + VALUE + "']")).isDisabled();
    }

    private void assertHelpRelationship(String resourceType, String type, boolean hasHelp) throws Exception {
        prepareOptions(resourceType, type, hasHelp, false, false);
        boolean dropDown = "drop-down".equals(type);
        Locator target = contentFrame().locator(dropDown ? "option" : "input[type='checkbox']");
        assertThat(contentFrame().locator(dropDown ? "label[for]" : "legend")).hasText(TITLE);
        if (hasHelp) {
            Locator help = helpMessage(resourceType);
            assertThat(help).isVisible();
            String helpId = help.getAttribute("id");
            assertNotNull(helpId, "The help message should have an id");
            assertThat(target).hasAttribute("aria-describedby", helpId);
        } else {
            assertThat(target).isAttached();
            assertNull(target.getAttribute("aria-describedby"));
        }
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testAccessibilityWhenHelpMessageIsSetOnDropDownType(String resourceType) throws Exception {
        assertHelpRelationship(resourceType, "drop-down", true);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnDropDownType(String resourceType) throws Exception {
        assertHelpRelationship(resourceType, "drop-down", false);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testAccessibilityWhenHelpMessageIsSetOnCheckboxType(String resourceType) throws Exception {
        assertHelpRelationship(resourceType, "checkbox", true);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testNoAriaDescribedByAttrWhenHelpMessageIsNotSetOnCheckboxType(String resourceType) throws Exception {
        assertHelpRelationship(resourceType, "checkbox", false);
    }
}
