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
package com.adobe.cq.wcm.core.components.it.pw.breadcrumb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_BREADCRUMB_V1;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_BREADCRUMB_V2;
import static com.adobe.cq.wcm.core.components.it.util.Commons.RT_BREADCRUMB_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group2")
public class BreadcrumbPwIT extends ComponentPwBaseTest {

    private String componentPath;
    private String hiddenPagePath;

    static Stream<String> breadcrumbVersions() {
        return Stream.of(RT_BREADCRUMB_V1, RT_BREADCRUMB_V2, RT_BREADCRUMB_V3);
    }

    /**
     * The dialog field defaults to 2 through {@code value="${not empty cqDesign.startLevel ? ... : 2}"}, which is what
     * the Selenium test asserted. Some AEM 6.5 / LTS releases don't resolve that expression in the edit dialog and
     * render it literally, which the number field shows as empty (the rendered breadcrumb still uses the default
     * level, which the item counts in the tests verify). The expectation therefore follows what the server actually
     * rendered for this dialog rather than the AEM flavour, and stays an exact value in both cases.
     */
    private void assertDefaultStartLevel(String resourceType, String componentPath, Locator startLevel)
        throws Exception {
        String version = resourceType.substring(resourceType.lastIndexOf('-') + 1);
        String dialogHtml = adminClient.doGet("/mnt/override/apps/core/wcm/components/breadcrumb/" + version
            + "/breadcrumb/_cq_dialog.html" + componentPath, 200).getContent();
        Matcher field = Pattern.compile("<coral-numberinput[^>]*name=\"\\./startLevel\"[^>]*>").matcher(dialogHtml);
        assertTrue(field.find(), "the dialog should render the startLevel field");
        if (field.group().contains("${")) {
            assertThat(startLevel).hasValue("");
        } else {
            assertThat(startLevel).hasValue("2");
        }
    }

    private String createBreadcrumbPage(String resourceType) throws Exception {
        testPage = authorClient.createPage("breadcrumb-root", "testPage_L1", rootPage, defaultPageTemplate)
            .getSlingPath();
        String currentPage = testPage;
        for (int level = 2; level <= 5; level++) {
            currentPage = authorClient.createPage("breadcrumb-level-" + level, "testPage_L" + level,
                currentPage, defaultPageTemplate).getSlingPath();
            if (level == 3) {
                hiddenPagePath = currentPage;
            }
        }
        addComponentToAllowedPolicy(resourceType);
        componentPath = Commons.addComponentWithRetry(authorClient, resourceType,
            currentPage + Commons.relParentCompPath, "breadcrumb");
        openEditor(currentPage);
        return componentPath;
    }

    private Locator items() {
        return contentFrame().locator(".cmp-breadcrumb li");
    }

    private Locator activeItem() {
        return contentFrame().locator(".cmp-breadcrumb li.active, .cmp-breadcrumb li[aria-current='page'], "
            + ".cmp-breadcrumb__item--active");
    }

    private void setCheckbox(String name) {
        checkCoralCheckbox(name);
    }

    @ParameterizedTest
    @MethodSource("breadcrumbVersions")
    public void testHideCurrent(String resourceType) throws Exception {
        String componentPath = createBreadcrumbPage(resourceType);
        assertThat(activeItem()).hasCount(1);
        assertThat(activeItem()).containsText("testPage_L5");
        openEditDialog(componentPath);
        setCheckbox("./hideCurrent");
        saveDialog();

        assertThat(activeItem()).hasCount(0);
        assertThat(items().filter(new Locator.FilterOptions().setHasText("testPage_L5"))).hasCount(0);
        assertThat(items()).hasCount(4);
    }

    @ParameterizedTest
    @MethodSource("breadcrumbVersions")
    public void testShowHidden(String resourceType) throws Exception {
        createBreadcrumbPage(resourceType);
        Commons.hidePage(authorClient, hiddenPagePath);
        reloadEditor();
        assertThat(items()).hasCount(4);
        assertThat(items().filter(new Locator.FilterOptions().setHasText("testPage_L3"))).hasCount(0);
        openEditDialog(componentPath);
        setCheckbox("./showHidden");
        saveDialog();

        assertThat(items()).hasCount(5);
        assertThat(items().filter(new Locator.FilterOptions().setHasText("testPage_L3"))).hasCount(1);
    }

    @ParameterizedTest
    @MethodSource("breadcrumbVersions")
    public void testChangeStartLevel(String resourceType) throws Exception {
        String componentPath = createBreadcrumbPage(resourceType);
        assertThat(items()).hasCount(5);
        openEditDialog(componentPath);
        Locator startLevel = dialog().locator("input[name='./startLevel']");
        assertDefaultStartLevel(resourceType, componentPath, startLevel);
        startLevel.fill("4");
        saveDialog();

        assertThat(items()).hasCount(3);
    }

    @ParameterizedTest
    @MethodSource("breadcrumbVersions")
    public void testSetZeroStartLevel(String resourceType) throws Exception {
        String componentPath = createBreadcrumbPage(resourceType);
        assertThat(items()).hasCount(5);
        openEditDialog(componentPath);
        Locator startLevel = dialog().locator("input[name='./startLevel']");
        assertDefaultStartLevel(resourceType, componentPath, startLevel);
        startLevel.fill("0");
        clickDone();

        assertThat(dialog()).isVisible();
        assertThat(startLevel).hasClass(java.util.regex.Pattern.compile(".*is-invalid.*"));
    }

    @ParameterizedTest
    @MethodSource("breadcrumbVersions")
    public void testSet100StartLevel(String resourceType) throws Exception {
        String componentPath = createBreadcrumbPage(resourceType);
        assertThat(items()).hasCount(5);
        assertThat(activeItem()).hasCount(1);
        openEditDialog(componentPath);
        assertDefaultStartLevel(resourceType, componentPath, dialog().locator("input[name='./startLevel']"));
        dialog().locator("input[name='./startLevel']").fill("100");
        saveDialog();

        assertThat(items()).hasCount(0);
        assertEquals(0, activeItem().count());
    }

    @ParameterizedTest
    @MethodSource("structureDataVersions")
    public void testStructureData(String resourceType) throws Exception {
        createBreadcrumbPage(resourceType);

        Locator list = contentFrame().locator(".cmp-breadcrumb__list[itemtype='http://schema.org/BreadcrumbList']");
        assertThat(list).isVisible();
        assertThat(list.locator("[itemprop='itemListElement']")).hasCount(5);
        assertThat(items()).hasCount(5);
        assertThat(activeItem()).hasCount(1);
    }

    static Stream<String> structureDataVersions() {
        return Stream.of(RT_BREADCRUMB_V2, RT_BREADCRUMB_V3);
    }
}
