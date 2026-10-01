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
package com.adobe.cq.wcm.core.components.it.pw.navigation;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_NAVIGATION_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class NavigationV1PwIT extends ComponentPwBaseTest {

    protected String navigationResourceType() {
        return RT_NAVIGATION_V1;
    }

    protected String navigationPath;
    protected String navigationRoot;
    protected String currentPage;

    protected void createNavigation() throws Exception {
        navigationRoot = authorClient.createPage("page_1", "Page 1", rootPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(navigationRoot, "navTitle", "Page 1", 200);
        currentPage = authorClient.createPage("page_1_1", "Page 1.1", navigationRoot, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(currentPage, "navTitle", "Page 1.1", 200);
        String child1 = authorClient.createPage("page_1_1_1", "Page 1.1.1", currentPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(child1, "navTitle", "Page 1.1.1", 200);
        String hidden = authorClient.createPage("page_1_1_2", "Page 1.1.2", currentPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(hidden, "hideInNav", "true", 200);
        String child3 = authorClient.createPage("page_1_1_3", "Page 1.1.3", currentPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(child3, "navTitle", "Page 1.1.3", 200);
        navigationPath = Commons.addComponentWithRetry(authorClient, navigationResourceType(),
            currentPage + Commons.relParentCompPath, "navigation");
        testPage = navigationRoot;
        openEditor(currentPage);
        page.navigate(baseUrl + currentPage + ".html");
    }

    protected Locator navigationItems() {
        return page.locator(".cmp-navigation__item");
    }

    @Test
    public void testDefaultConfiguration() throws Exception {
        createNavigation();
        openEditor(currentPage);
        openEditDialog(navigationPath);
        assertThat(dialog().locator("coral-checkbox[name='./collectAllPages'] input[type='checkbox']")).isChecked();
        selectAutocomplete("[name='./navigationRoot']", navigationRoot);
        saveDialog();
        page.navigate(baseUrl + currentPage + ".html");
        assertThat(navigationItems()).hasCount(3);
        assertThat(page.locator(".cmp-navigation__item--active")).containsText("Page 1.1");
        assertThat(navigationItems()).containsText("Page 1.1.1");
        assertThat(navigationItems()).containsText("Page 1.1.3");
        assertThat(navigationItems()).not().containsText("Page 1.1.2");
        assertThat(page.locator(".cmp-navigation a[href*='page_1_1.html']")).isVisible();
    }

    @Test
    public void testIncludeNavigationRoot() throws Exception {
        createNavigation();
        openEditor(currentPage);
        openEditDialog(navigationPath);
        selectAutocomplete("[name='./navigationRoot']", navigationRoot);
        dialog().locator("[name='./structureStart']").fill("0");
        saveDialog();
        page.navigate(baseUrl + currentPage + ".html");
        assertThat(navigationItems()).hasCount(4);
        assertThat(page.locator(".cmp-navigation__item--active")).containsText("Page 1.1");
        assertThat(navigationItems()).containsText("Page 1");
        assertThat(navigationItems()).containsText("Page 1.1.1");
        assertThat(navigationItems()).containsText("Page 1.1.3");
        assertThat(navigationItems()).not().containsText("Page 1.1.2");
    }

    @Test
    public void testChangeStructureDepthLevel() throws Exception {
        createNavigation();
        openEditor(currentPage);
        openEditDialog(navigationPath);
        selectAutocomplete("[name='./navigationRoot']", navigationRoot);
        Locator collectAll = dialog().locator("coral-checkbox[name='./collectAllPages'] input[type='checkbox']");
        collectAll.uncheck();
        assertTrue(dialog().locator("[name='./structureDepth']").isVisible());
        saveDialog();
        page.navigate(baseUrl + currentPage + ".html");
        assertThat(navigationItems()).hasCount(1);
        assertThat(page.locator(".cmp-navigation__item--active")).containsText("Page 1.1");
        assertThat(navigationItems()).not().containsText("Page 1.1.1");
    }
}
