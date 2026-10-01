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
package com.adobe.cq.wcm.core.components.it.pw.text;

import java.util.HashMap;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group3")
public class StyleTabV2PwIT extends ComponentPwBaseTest {

    private static final String TEXT = "Text styled by Style System.";
    private String textPath;

    private String createTextWithStyles() throws Exception {
        String resourceType = RT_TEXT_V2;
        textPath = addStandaloneComponent(resourceType, "text");
        String policy = createComponentPolicy(resourceType.substring(resourceType.lastIndexOf("/")), new HashMap<>());
        adminClient.createNode(policy + "/jcr:content", "nt:unstructured");
        adminClient.createNode(policy + "/cq:styleGroups", "nt:unstructured");
        adminClient.createNode(policy + "/cq:styleGroups/item0", "nt:unstructured");
        adminClient.createNode(policy + "/cq:styleGroups/item0/cq:styles", "nt:unstructured");
        for (int i = 0; i < 2; i++) {
            String style = policy + "/cq:styleGroups/item0/cq:styles/item" + i;
            adminClient.createNode(style, "nt:unstructured");
            adminClient.setPropertyString(style, "cq:styleClasses", i == 0 ? "cmp-blue-text" : "cmp-red-text", 200, 201);
            adminClient.setPropertyString(style, "cq:styleId", i == 0 ? "1547060098888" : "1550165689999", 200, 201);
            adminClient.setPropertyString(style, "cq:styleLabel", i == 0 ? "Blue" : "Red", 200, 201);
        }
        reloadEditor();
        return textPath;
    }

    private void setText() {
        openEditDialog(textPath);
        dialog().locator("[name='./id']").fill("text-id");
        dialog().locator("[name='./text']").fill(TEXT);
    }

    private void openStyleDropdown() {
        dialog().locator("coral-tab[data-foundation-tracking-event*='styles']").click();
        dialog().locator("coral-select[name='./cq:styleIds'] > button").click();
    }

    @Test
    public void testNoStyleAppliedByDefault() throws Exception {
        createTextWithStyles();
        setText();
        saveDialog();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id, .cmp-red-text #text-id")).hasCount(0);
    }

    @Test
    public void testApplyStyle() throws Exception {
        createTextWithStyles();
        setText();
        openStyleDropdown();
        page.locator("coral-selectlist-item").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Blue")).click();
        saveDialog();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id")).isVisible();
        assertThat(contentFrame().locator(".cmp-red-text #text-id")).hasCount(0);
    }

    @Test
    public void testChangeAppliedStyle() throws Exception {
        createTextWithStyles();
        setText();
        openStyleDropdown();
        page.locator("coral-selectlist-item").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Blue")).click();
        saveDialog();
        openEditDialog(textPath);
        openStyleDropdown();
        page.locator("coral-selectlist-item").filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText("Red")).click();
        saveDialog();
        assertThat(contentFrame().locator(".cmp-red-text #text-id")).isVisible();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id")).hasCount(0);
    }
}
