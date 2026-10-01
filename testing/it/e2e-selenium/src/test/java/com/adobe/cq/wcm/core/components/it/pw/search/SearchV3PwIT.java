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

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_SEARCH_V3;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class SearchV3PwIT extends SearchV2PwIT {

    @Override
    protected String searchResourceType() {
        return RT_SEARCH_V3;
    }

    @Override
    protected String searchClientlib() {
        return Commons.CLIENTLIBS_SEARCH_V3;
    }

    @Override
    protected String createSearch() throws Exception {
        String path = super.createSearch();
        createComponentPolicy(RT_SEARCH_V3.substring(RT_SEARCH_V3.lastIndexOf("/")),
            Map.of("hideAiSearchToggle", "false"));
        reloadEditor();
        page.navigate(baseUrl + searchPage + ".html");
        return path;
    }

    private String searchRequestAfterQuery() {
        return page.waitForRequest(request -> request.url().contains("searchresults.json")
            && request.url().contains("fulltext="),
            () -> page.locator(".cmp-search__input").fill("Page")).url();
    }

    @Test
    public void testAiSearchToggleVisible() throws Exception {
        createSearch();
        assertThat(page.locator(".cmp-search__ai-toggle")).isVisible();
    }

    @Test
    public void testAiSearchTogglePrefix() throws Exception {
        createSearch();
        page.locator(".cmp-search__ai-toggle-input").check();
        String request = searchRequestAfterQuery();
        assertNotNull(request);
        assertTrue(request.contains("fulltext=%3F%7B%7D%3FPage") || request.contains("fulltext=?%7B%7D%3FPage"));
    }

    @Test
    public void testHideAiSearchTogglePolicy() throws Exception {
        createSearch();
        createComponentPolicy(RT_SEARCH_V3.substring(RT_SEARCH_V3.lastIndexOf("/")),
            Map.of("hideAiSearchToggle", "true"));
        reloadEditor();
        page.navigate(baseUrl + searchPage + ".html");
        assertFalse(page.locator(".cmp-search__ai-toggle").isVisible());
        String request = searchRequestAfterQuery();
        assertNotNull(request);
        assertFalse(request.contains("%3F%7B%7D%3F"));
    }
}
