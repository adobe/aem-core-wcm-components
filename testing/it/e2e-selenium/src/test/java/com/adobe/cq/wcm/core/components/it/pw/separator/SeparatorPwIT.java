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
package com.adobe.cq.wcm.core.components.it.pw.separator;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_SEPARATOR_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group2")
public class SeparatorPwIT extends ComponentPwBaseTest {

    @Test
    public void testDecorative() throws Exception {
        String componentPath = addStandaloneComponent(RT_SEPARATOR_V1, "separator");
        Locator rule = contentFrame().locator(".cmp-separator__horizontal-rule");
        assertThat(rule).not().hasAttribute("role", "none");
        assertThat(rule).not().hasAttribute("aria-hidden", "true");

        openEditDialog(componentPath);
        checkCoralCheckbox("./isDecorative");
        saveDialog();
        assertThat(rule).hasAttribute("role", "none");
        assertThat(rule).hasAttribute("aria-hidden", "true");

        openEditDialog(componentPath);
        dialog().locator("coral-checkbox[name='./isDecorative'] input[type='checkbox']").uncheck();
        saveDialog();
        assertThat(rule).not().hasAttribute("role", "none");
        assertThat(rule).not().hasAttribute("aria-hidden", "true");
    }
}
