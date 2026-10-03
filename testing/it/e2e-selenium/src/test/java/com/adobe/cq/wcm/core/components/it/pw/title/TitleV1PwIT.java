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
package com.adobe.cq.wcm.core.components.it.pw.title;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TITLE_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Tag("playwright-group3")
public class TitleV1PwIT extends ComponentPwBaseTest {

    protected String titleResourceType() {
        return RT_TITLE_V1;
    }

    protected String titleClientlib() {
        return Commons.CLIENTLIBS_TITLE_V1;
    }

    protected String redirectPage;

    protected String createTitle() throws Exception {
        createPagePolicy(Collections.singletonMap("clientlibs", titleClientlib()));
        String path = addStandaloneComponent(titleResourceType(), "title");
        redirectPage = authorClient.createPage("redirectPage", "Redirect Page Title", testPage, defaultPageTemplate).getSlingPath();
        return path;
    }

    @Test
    public void SetTitleValueUsingConfigDialog() throws Exception {
        String componentPath = createTitle();
        openEditDialog(componentPath);
        dialog().locator("[name='./jcr:title']").fill("Content name");
        saveDialog();
        assertThat(contentFrame().locator("h1")).hasText("Content name");
    }

    @Test
    public void testCheckExistenceOfTitleTypes() throws Exception {
        String componentPath = createTitle();
        openEditDialog(componentPath);
        List<String> titleTypes = java.util.Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6");
        com.microsoft.playwright.Locator options = openCoralSelect("[name='./type']");
        for (String titleType : titleTypes) {
            assertThat(options.locator("coral-selectlist-item[value='" + titleType + "']")).isVisible();
        }
    }

    @Test
    public void testSetTitleType() throws Exception {
        String componentPath = createTitle();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./type']", "h6");
        saveDialog();
        assertThat(contentFrame().locator("h6")).isVisible();
    }
}
