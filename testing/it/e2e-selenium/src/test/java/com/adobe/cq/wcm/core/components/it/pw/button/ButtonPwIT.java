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
package com.adobe.cq.wcm.core.components.it.pw.button;

import java.util.stream.Stream;
import java.util.HashMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_BUTTON_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_BUTTON_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group2")
public class ButtonPwIT extends ComponentPwBaseTest {

    static Stream<String> buttonVersions() {
        return Stream.of(RT_BUTTON_V1, RT_BUTTON_V2);
    }

    private String addButton(String resourceType) throws Exception {
        return addStandaloneComponent(resourceType, "button");
    }

    private void fillLink(String property, String value) {
        dialog().locator("foundation-autocomplete[name='./" + property + "'] input").fill(value);
    }

    @ParameterizedTest
    @MethodSource("buttonVersions")
    public void testSetText(String resourceType) throws Exception {
        String path = addButton(resourceType);
        openEditDialog(path);
        dialog().locator("input[name='./jcr:title']").fill("test button");
        saveDialog();

        assertThat(contentFrame().locator(".cmp-button")).containsText("test button");
    }

    @ParameterizedTest
    @MethodSource("buttonVersions")
    public void testSetLink(String resourceType) throws Exception {
        String path = addButton(resourceType);
        openEditDialog(path);
        fillLink(resourceType.equals(RT_BUTTON_V1) ? "link" : "linkURL", "https://www.adobe.com");
        saveDialog();

        assertThat(contentFrame().locator("a.cmp-button[href='https://www.adobe.com']")).isVisible();
    }

    @ParameterizedTest
    @MethodSource("buttonVersions")
    public void testSetIcon(String resourceType) throws Exception {
        String path = addButton(resourceType);
        openEditDialog(path);
        dialog().locator("input[name='./icon']").fill("email");
        saveDialog();

        assertThat(contentFrame().locator(".cmp-button__icon--email")).isVisible();
    }

    @Test
    public void testSetLinkWithTarget() throws Exception {
        String path = addButton(RT_BUTTON_V2);
        openEditDialog(path);
        fillLink("linkURL", "https://www.adobe.com");
        dialog().locator("coral-checkbox[name='./linkTarget'] input[type='checkbox']").check();
        saveDialog();

        assertThat(contentFrame().locator("a.cmp-button[href='https://www.adobe.com'][target='_blank']")).isVisible();
    }

    @Test
    public void testSetLinkWithButtonV1LinkProperty() throws Exception {
        String path = addButton(RT_BUTTON_V2);
        setProperty(path, "jcr:title", "button");
        setProperty(path, "link", "");
        setProperty(path, "linkURL", "");
        openEditDialog(path);
        Locator link = dialog().locator("foundation-autocomplete[name='./linkURL'] input");
        assertThat(link).hasValue("");
        saveDialog();

        setProperty(path, "link", "http://www.google.com");
        setProperty(path, "linkURL", "");
        openEditDialog(path);
        link = dialog().locator("foundation-autocomplete[name='./linkURL'] input");
        assertThat(link).hasValue("http://www.google.com");
        saveDialog();
        assertEquals(404, adminClient.doGet(path + "/link", 404).getStatusLine().getStatusCode());

        setProperty(path, "link", "");
        setProperty(path, "linkURL", "http://www.adobe.com");
        openEditDialog(path);
        link = dialog().locator("foundation-autocomplete[name='./linkURL'] input");
        assertThat(link).hasValue("http://www.adobe.com");
        saveDialog();

        setProperty(path, "link", "http://www.google.com");
        setProperty(path, "linkURL", "http://www.adobe.com");
        openEditDialog(path);
        link = dialog().locator("foundation-autocomplete[name='./linkURL'] input");
        assertThat(link).hasValue("http://www.adobe.com");
        saveDialog();
        assertEquals(404, adminClient.doGet(path + "/link", 404).getStatusLine().getStatusCode());
    }

    private void setProperty(String path, String name, String value) throws Exception {
        HashMap<String, String> properties = new HashMap<>();
        properties.put("_charset_", "UTF-8");
        properties.put("./" + name, value);
        com.adobe.cq.wcm.core.components.it.seljup.util.Commons.editNodeProperties(adminClient, path, properties);
    }
}
