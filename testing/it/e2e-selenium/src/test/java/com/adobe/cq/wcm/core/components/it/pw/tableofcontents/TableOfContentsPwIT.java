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
package com.adobe.cq.wcm.core.components.it.pw.tableofcontents;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TABLEOFCONTENTS_V1;
import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_TITLE_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group2")
public class TableOfContentsPwIT extends ComponentPwBaseTest {

    private static final String PAGE_TITLE = "Test Page for Table of Contents";

    private String addTableOfContents() throws Exception {
        testPage = authorClient.createPage("testPage", PAGE_TITLE, rootPage, defaultPageTemplate).getSlingPath();
        addComponentToAllowedPolicy(RT_TABLEOFCONTENTS_V1);
        String path = Commons.addComponentWithRetry(authorClient, RT_TABLEOFCONTENTS_V1,
            testPage + Commons.relParentCompPath, "toc");
        openEditor(testPage);
        return path;
    }

    @Test
    public void testVisibilityOfTocTemplatePlaceholder() throws Exception {
        addTableOfContents();
        Locator templatePlaceholder = contentFrame().locator(".cmp-toc__template-placeholder");
        assertThat(templatePlaceholder).isVisible();

        page.navigate(baseUrl + testPage + ".html");
        assertThat(page.locator(".cmp-toc__template-placeholder")).hasCount(0);
    }

    @Test
    public void testTocRendering() throws Exception {
        addTableOfContents();
        addComponentToAllowedPolicy(RT_TITLE_V3);
        Commons.addComponentWithRetry(authorClient, RT_TITLE_V3, testPage + Commons.relParentCompPath, "title");
        reloadEditor();

        Locator toc = contentFrame().locator(".cmp-toc");
        assertThat(toc.locator(".cmp-toc__placeholder")).hasCount(0);
        assertThat(toc.locator(".cmp-toc__content")).isVisible();
        assertThat(toc.locator(".cmp-toc__content")).containsText(PAGE_TITLE);
    }

    @Test
    public void testAllTocConfigExist() throws Exception {
        String componentPath = addTableOfContents();
        openEditDialog(componentPath);
        Locator dialog = dialog();
        for (String name : new String[] {"./listType", "./startLevel", "./stopLevel", "./id"}) {
            assertThat(dialog.locator("[name='" + name + "']")).isVisible();
        }
        for (String name : new String[] {"./listType", "./startLevel", "./stopLevel"}) {
            Locator list = openCoralSelect("[name='" + name + "']");
            assertThat(list.locator("coral-selectlist-item")).not().hasCount(0);
            list.locator("coral-selectlist-item").first().click();
        }
        dialog.locator(DONE_BUTTON).click();
    }

    @Test
    public void testTocIdConfig() throws Exception {
        String componentPath = addTableOfContents();
        openEditDialog(componentPath);
        dialog().locator("[name='./id']").fill("toc-sample-id");
        saveDialog();

        assertThat(contentFrame().locator(".cmp-toc__content")).hasAttribute("id", "toc-sample-id");
    }

    @Test
    public void testTocConfigOverrideByDesignDialog() throws Exception {
        String componentPath = addTableOfContents();
        Map<String, String> policyProperties = new HashMap<>();
        policyProperties.put("restrictListType", "bulleted");
        policyProperties.put("restrictStartLevel", "h3");
        policyProperties.put("restrictStopLevel", "h4");
        policyProperties.put("includeClasses", "include-1,include-2");
        policyProperties.put("ignoreClasses", "ignore-1,ignore-2");
        createComponentPolicy("/tableofcontents-v1", policyProperties);

        openEditDialog(componentPath);
        for (String name : new String[] {"./listType", "./startLevel", "./stopLevel"}) {
            assertThat(dialog().locator("[name='" + name + "']")).hasCount(0);
        }
        saveDialog();
    }

    @Test
    @Tag("IgnoreOn65")
    public void testInvalidLevelsErrorTooltipInEditDialog() throws Exception {
        assertInvalidLevelValidation();
    }

    @Test
    @Tag("IgnoreOnSDK")
    public void testInvalidLevelsErrorTooltipInEditDialog65() throws Exception {
        assertInvalidLevelValidation();
    }

    private void assertInvalidLevelValidation() throws Exception {
        String componentPath = addTableOfContents();
        openEditDialog(componentPath);
        selectInCoralSelect("[name='./startLevel']", "h4");
        selectInCoralSelect("[name='./stopLevel']", "h3");
        assertThat(dialog().locator(".cmp-toc__editor coral-tooltip.is-open[variant='error']")).isVisible();

        selectInCoralSelect("[name='./stopLevel']", "h5");
        assertThat(dialog().locator(".cmp-toc__editor coral-tooltip.is-open[variant='error']")).hasCount(0);
        saveDialog();
    }
}
