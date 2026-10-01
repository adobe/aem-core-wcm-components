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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TEXT_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("playwright-group3")
public class TextV2PwIT extends TextV1PwIT {

    private static final String XSS_TEXT = "Hello World! <img =\"/\" onerror=\"alert(String.fromCharCode(88,83,83))\"></img>";

    @Override
    protected String textResourceType() {
        return RT_TEXT_V2;
    }

    @Test
    public void testCheckTextWithXSSProtection() throws Exception {
        String path = createText();
        openEditDialog(path);
        Locator richText = dialog().locator("[name='./text']");
        richText.fill(XSS_TEXT);
        saveDialog();
        assertThat(page.frameLocator("#ContentFrame").locator(".cmp-text")).containsText("Hello World!");
        assertEquals(0, page.frameLocator("#ContentFrame").locator(".cmp-text img[onerror]").count());
        String stored = authorClient.doGetJson(path, 1, 200).get("text").asText();
        assertEquals("Hello World! <img />", stored.trim());
    }
}
