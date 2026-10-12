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
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_TEXT_V2;
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
        // the rich text widget is backed by a hidden input, which is what gets submitted
        dialog().locator("input[name='./text']").evaluate("(e, v) => e.value = v", TEXT);
    }

    private Locator styleList;

    private static final String BLUE_ID = "1547060098888";
    private static final String RED_ID = "1550165689999";
    private static final String STYLE_SELECT = "coral-select[name='./cq:styleIds']";

    private void openStylesTab() {
        dialog().locator("coral-tab[data-foundation-tracking-event*='styles']").click();
        assertThat(dialog().locator(STYLE_SELECT)).isVisible();
    }

    private void openStyleDropdown() {
        styleList = openCoralSelect("[name='./cq:styleIds']");
    }

    private void selectStyle(String label) {
        styleList.locator("coral-selectlist-item").filter(new Locator.FilterOptions().setHasText(label)).click();
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
        openStylesTab();
        assertThat(dialog().locator(STYLE_SELECT + " > coral-select-item[selected]")).hasText("None");
        openStyleDropdown();
        assertThat(styleList.locator("coral-selectlist-item[selected]")).hasText("None");
        assertThat(styleList.locator("coral-selectlist-item[value='" + BLUE_ID + "']")).hasCount(1);
        assertThat(styleList.locator("coral-selectlist-item[value='" + RED_ID + "']")).hasCount(1);
        selectStyle("Blue");
        saveDialog();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id")).isVisible();
        assertThat(contentFrame().locator(".cmp-red-text #text-id")).hasCount(0);
    }

    @Test
    public void testChangeAppliedStyle() throws Exception {
        createTextWithStyles();
        setText();
        openStylesTab();
        openStyleDropdown();
        selectStyle("Blue");
        saveDialog();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id")).isVisible();
        openEditDialog(textPath);
        openStylesTab();
        openStyleDropdown();
        assertThat(styleList.locator("coral-selectlist-item[selected][value='" + BLUE_ID + "']")).hasCount(1);
        assertThat(styleList.locator("coral-selectlist-item[value='" + RED_ID + "']")).hasCount(1);
        assertThat(dialog().locator(STYLE_SELECT + " > coral-select-item[selected]")).hasText("Blue");
        selectStyle("Red");
        saveDialog();
        assertThat(contentFrame().locator(".cmp-red-text #text-id")).isVisible();
        assertThat(contentFrame().locator(".cmp-blue-text #text-id")).hasCount(0);
    }
}
