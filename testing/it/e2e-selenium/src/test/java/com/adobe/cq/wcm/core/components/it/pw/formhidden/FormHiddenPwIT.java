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
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.it.pw.formhidden;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Tag;
import java.util.stream.Stream;

import com.adobe.cq.wcm.core.components.it.pw.form.FormGroupPwBase;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMHIDDEN_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_FORMHIDDEN_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group1")
public class FormHiddenPwIT extends FormGroupPwBase {

    private static final String NAME = "hiddenComponent_name";
    private static final String VALUE = "hiddenComponent_value";
    private static final String ID = "hiddenComponent_id";

    static Stream<String> resourceTypes() {
        return Stream.of(RT_FORMHIDDEN_V1, RT_FORMHIDDEN_V2);
    }

    private String openHiddenDialog(String resourceType) throws Exception {
        String path = addStandaloneComponent(resourceType, "formhidden");
        openEditDialog(path);
        return path;
    }

    private void setName() {
        dialog().locator("[name='./name']").fill(NAME);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testCheckMandatoryFields(String resourceType) throws Exception {
        openHiddenDialog(resourceType);
        clickDone();
        assertThat(dialog()).isVisible();
        assertThat(dialog().locator("[name='./name'][invalid='true'], [name='./name'][aria-invalid='true']")).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetElementName(String resourceType) throws Exception {
        openHiddenDialog(resourceType);
        setName();
        saveDialog();
        assertThat(contentFrame().locator("input[type='hidden'][name='" + NAME + "']")).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetElementValue(String resourceType) throws Exception {
        openHiddenDialog(resourceType);
        setName();
        dialog().locator("[name='./value']").fill(VALUE);
        saveDialog();
        assertThat(contentFrame().locator("input[type='hidden'][name='" + NAME + "'][value='" + VALUE + "']")).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("resourceTypes")
    public void testSetElementId(String resourceType) throws Exception {
        openHiddenDialog(resourceType);
        setName();
        dialog().locator("[name='./id']").fill(ID);
        saveDialog();
        Locator hidden = contentFrame().locator("input[type='hidden'][name='" + NAME + "'][id='" + ID + "']");
        assertThat(hidden).hasCount(1);
    }
}
