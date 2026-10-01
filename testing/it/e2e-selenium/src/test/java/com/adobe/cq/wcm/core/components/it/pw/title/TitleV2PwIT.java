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
package com.adobe.cq.wcm.core.components.it.pw.title;

import java.util.Map;
import java.util.Arrays;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TITLE_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("playwright-group3")
public class TitleV2PwIT extends TitleV1PwIT {

    @Override
    protected String titleResourceType() {
        return RT_TITLE_V2;
    }

    @Override
    protected String titleClientlib() {
        return Commons.CLIENTLIBS_TITLE_V2;
    }

    private String setTitlePolicy(String type, String... allowedTypes) throws Exception {
        String policy = createComponentPolicy(RT_TITLE_V2.substring(RT_TITLE_V2.lastIndexOf("/")),
            Map.of("type", type));
        adminClient.setPropertyStringArray(policy, "allowedTypes", Arrays.asList(allowedTypes), 200);
        return policy;
    }

    @Test
    public void testSetLink() throws Exception {
        String componentPath = createTitle();
        openEditDialog(componentPath);
        selectAutocomplete("[name='./linkURL']", redirectPage);
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-title__link")).hasAttribute("href", redirectPage + ".html");
    }

    @Test
    public void testCheckExistenceOfTypesUsingPolicy() throws Exception {
        String componentPath = createTitle();
        setTitlePolicy("h2", "h2", "h3", "h4", "h6");
        openEditor(testPage);
        openEditDialog(componentPath);
        com.microsoft.playwright.Locator options = openCoralSelect("[name='./type']");
        for (String titleType : Arrays.asList("h2", "h3", "h4", "h6")) {
            assertThat(options.locator("coral-selectlist-item[value='" + titleType + "']")).isVisible();
        }
        assertThat(options.locator("coral-selectlist-item[value='h5']")).hasCount(0);
        saveDialog();
        assertThat(page.locator(".cmp-title h2")).isVisible();
    }

    @Test
    public void testCheckExistenceOfOneTypeUsingPolicy() throws Exception {
        String componentPath = createTitle();
        setTitlePolicy("h1", "h1");
        openEditor(testPage);
        openEditDialog(componentPath);
        assertFalse(dialog().locator("coral-select[name='./type']").isVisible());
        saveDialog();
        assertThat(page.locator(".cmp-title h1")).isVisible();
    }

    @Test
    public void testDefaultSelectedDropdownValueUsingValidOption() throws Exception {
        String componentPath = createTitle();
        setTitlePolicy("h4", "h1", "h2", "h3", "h4", "h6");
        openEditor(testPage);
        openEditDialog(componentPath);
        assertThat(dialog().locator("coral-select[name='./type'] coral-select-item[selected]")).containsText("h4");
    }

    @Test
    public void testDefaultSelectedDropdownValueUsingInvalidOption() throws Exception {
        String componentPath = createTitle();
        setTitlePolicy("h5", "h3", "h4", "h6");
        openEditor(testPage);
        openEditDialog(componentPath);
        assertThat(dialog().locator("coral-select[name='./type'] coral-select-item[selected]")).containsText("h3");
    }
}
