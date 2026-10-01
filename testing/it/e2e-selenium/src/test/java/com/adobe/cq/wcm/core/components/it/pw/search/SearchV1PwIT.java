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
package com.adobe.cq.wcm.core.components.it.pw.search;

import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.pw.ComponentPwBaseTest;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_SEARCH_V1;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class SearchV1PwIT extends ComponentPwBaseTest {

    protected String searchResourceType() {
        return RT_SEARCH_V1;
    }

    protected String searchClientlib() {
        return Commons.CLIENTLIBS_SEARCH_V1;
    }

    protected String searchPath;
    protected String searchRoot;
    protected String searchPage;

    protected String createSearch() throws Exception {
        searchRoot = authorClient.createPage("search-root", "Parent Page 1", rootPage, defaultPageTemplate).getSlingPath();
        for (int i = 0; i < 20; i++) {
            String child = authorClient.createPage("page" + i, "Page " + i, searchRoot, defaultPageTemplate).getSlingPath();
            authorClient.setPageProperty(child, "jcr:title", "Page " + i, 200);
        }
        searchPage = authorClient.createPage("page_1_1", "Page 1.1", searchRoot, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(searchPage, "jcr:title", "Page 1.1", 200);
        String nested = authorClient.createPage("page_1_1_1", "Page 1.1.1", searchPage, defaultPageTemplate).getSlingPath();
        authorClient.setPageProperty(nested, "jcr:title", "Page 1.1.1", 200);
        authorClient.createPage("page_1_1_2", "Page 1.1.2", searchPage, defaultPageTemplate);
        authorClient.createPage("page_1_1_3", "Page 1.1.3", searchPage, defaultPageTemplate);

        createPagePolicy(Map.of("clientlibs", searchClientlib()));
        searchPath = Commons.addComponentWithRetry(authorClient, searchResourceType(),
            searchPage + Commons.relParentCompPath, "search");
        testPage = searchRoot;
        openEditor(searchPage);
        page.navigate(baseUrl + searchPage + ".html");
        return searchPath;
    }

    private Locator searchInput() {
        return page.locator(".cmp-search__input");
    }

    private Locator results() {
        return page.locator(".cmp-search__results");
    }

    protected void query(String value) {
        searchInput().fill(value);
    }

    @Test
    public void testDefaultConfiguration() throws Exception {
        createSearch();
        query("Page 1.1.1");
        assertThat(results()).containsText("Page 1.1.1");
    }

    @Test
    public void testChangeSearchRoot() throws Exception {
        createSearch();
        openEditor(searchPage);
        openEditDialog(searchPath);
        selectAutocomplete("[name='./searchRoot']", searchRoot);
        saveDialog();
        page.navigate(baseUrl + searchPage + ".html");
        query("Page 1");
        assertThat(results()).not().containsText("Parent Page 1");
    }

    @Test
    public void testClearButton() throws Exception {
        createSearch();
        Locator clear = page.locator(".cmp-search__clear");
        assertThat(clear).isHidden();
        query("Page");
        assertThat(clear).isVisible();
        clear.click();
        assertThat(searchInput()).hasValue("");
        assertThat(clear).isHidden();
        assertThat(results()).isHidden();
    }

    @Test
    public void testKeyEnterInput() throws Exception {
        createSearch();
        query("Page");
        searchInput().press("Enter");
        assertTrue(page.url().contains("page_1_1"));
        assertThat(searchInput()).hasValue("Page");
    }

    @Test
    public void testOutsideClick() throws Exception {
        createSearch();
        query("Page");
        assertThat(results()).isVisible();
        page.locator("body").click(new Locator.ClickOptions().setPosition(5, 5));
        assertThat(results()).isHidden();
    }

    @Test
    public void testMark() throws Exception {
        createSearch();
        query("Page");
        assertThat(results().locator("mark")).containsText("Page");
    }

    @Test
    public void testMinLength() throws Exception {
        createSearch();
        createComponentPolicy(searchResourceType().substring(searchResourceType().lastIndexOf("/")),
            Map.of("searchTermMinimumLength", "5"));
        reloadEditor();
        page.navigate(baseUrl + searchPage + ".html");
        query("Page");
        assertThat(results().locator(".cmp-search__item")).hasCount(0);
        query("Page ");
        assertThat(results().locator(".cmp-search__item")).not().hasCount(0);
    }

    @Test
    public void testResultsSize() throws Exception {
        createSearch();
        createComponentPolicy(searchResourceType().substring(searchResourceType().lastIndexOf("/")),
            Map.of("resultsSize", "2"));
        reloadEditor();
        page.navigate(baseUrl + searchPage + ".html");
        query("Page");
        assertThat(results().locator(".cmp-search__item")).hasCount(2);
    }

    @Test
    public void testScrollDown() throws Exception {
        createSearch();
        openEditor(searchPage);
        openEditDialog(searchPath);
        selectAutocomplete("[name='./searchRoot']", searchRoot);
        saveDialog();
        page.navigate(baseUrl + searchPage + ".html");
        query("Page");
        Locator items = results().locator(".cmp-search__item");
        assertThat(items).hasCount(10);
        results().evaluate("(e) => e.scrollTop = e.scrollHeight");
        assertThat(items).hasCount(20);
    }
}
