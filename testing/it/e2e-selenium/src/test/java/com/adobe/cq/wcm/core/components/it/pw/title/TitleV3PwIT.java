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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TITLE_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group3")
public class TitleV3PwIT extends TitleV2PwIT {

    @Override
    protected String titleResourceType() {
        return RT_TITLE_V3;
    }

    @Override
    protected String titleClientlib() {
        return Commons.CLIENTLIBS_TITLE_V3;
    }

    @Test
    public void testSetLinkWithTarget() throws Exception {
        String componentPath = createTitle();
        openEditDialog(componentPath);
        selectAutocomplete("[name='./linkURL']", redirectPage);
        dialog().locator("coral-checkbox[name='./linkTarget'] input").check();
        saveDialog();
        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-title__link")).hasAttribute("href", redirectPage + ".html");
        assertThat(page.locator(".cmp-title__link")).hasAttribute("target", "_blank");
    }
}
