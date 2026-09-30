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
package com.adobe.cq.wcm.core.components.it.pw.formbutton;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Tag;
import java.util.stream.Stream;
import java.util.Locale;

import com.adobe.cq.wcm.core.components.it.pw.form.FormGroupPwBase;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMBUTTON_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group1")
public class FormButtonPwIT extends FormGroupPwBase {

    static Stream<String> resourceTypes() {
        return Stream.of(RT_FORMBUTTON_V1, RT_FORMBUTTON_V2);
    }

    private String buttonSelector(String resourceType) {
        return resourceType.equals(RT_FORMBUTTON_V1) ? ".btn" : ".cmp-form-button";
    }

    private Locator openButtonDialog(String resourceType) throws Exception {
        addComponentToAllowedPolicy(resourceType);
        String path = addStandaloneComponent(resourceType, "formbutton");
        openEditDialog(path);
        return dialog();
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testCheckDefaultButtonAttributes(String resourceType) throws Exception {
        addComponentToAllowedPolicy(resourceType);
        addStandaloneComponent(resourceType, "formbutton");
        Locator button = contentFrame().locator(buttonSelector(resourceType));
        assertThat(button).isVisible();
        assertEquals("submit", button.getAttribute("type").toLowerCase(Locale.ROOT));
        assertThat(button).containsText("Submit");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testCreateButton(String resourceType) throws Exception {
        openButtonDialog(resourceType);
        selectInCoralSelect("[name='./type']", "button");
        dialog().locator("[name='./jcr:title']:visible").first().fill("Button");
        saveDialog();
        Locator button = contentFrame().locator(buttonSelector(resourceType));
        assertThat(button).isVisible();
        assertThat(button).containsText("Button");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetButtonText(String resourceType) throws Exception {
        openButtonDialog(resourceType);
        dialog().locator("[name='./jcr:title']:visible").first().fill("Test Button");
        saveDialog();
        assertThat(contentFrame().locator(buttonSelector(resourceType))).containsText("Test Button");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetButtonName(String resourceType) throws Exception {
        openButtonDialog(resourceType);
        dialog().locator("[name='./jcr:title']:visible").first().fill("BUTTON WITH NAME");
        dialog().locator("input[name='./name']").fill("button1");
        saveDialog();
        Locator button = contentFrame().locator(buttonSelector(resourceType) + "[name='button1']");
        assertThat(button).isVisible();
        assertThat(button).containsText("BUTTON WITH NAME");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetButtonValue(String resourceType) throws Exception {
        openButtonDialog(resourceType);
        dialog().locator("[name='./jcr:title']:visible").first().fill("BUTTON WITH NAME");
        dialog().locator("input[name='./name']").fill("button1");
        dialog().locator("input[name='./value']").fill("thisisthevalue");
        saveDialog();
        Locator button = contentFrame().locator(buttonSelector(resourceType) + "[value='thisisthevalue']");
        assertThat(button).isVisible();
        assertThat(button).containsText("BUTTON WITH NAME");
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetButtonValueWithoutName(String resourceType) throws Exception {
        openButtonDialog(resourceType);
        dialog().locator("[name='./jcr:title']:visible").first().fill("BUTTON WITH NAME");
        dialog().locator("input[name='./value']").fill("thisisthevalue");
        clickDone();
        assertThat(dialog().locator("[name='./name'][invalid='true'], [name='./name'][aria-invalid='true']")).hasCount(1);
    }
}
