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

package com.adobe.cq.wcm.core.components.it.seljup.tests.search.v3;

import java.util.HashMap;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v3.Search;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class SearchIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.search.v2.SearchIT {

    protected Search search;

    @Override
    protected void setupResources() {
        super.setupResources();
        clientlibs = Commons.CLIENTLIBS_SEARCH_V3;
        proxyPath = Commons.RT_SEARCH_V3;
        search = new Search();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    @Test
    @DisplayName("Test: AI Search toggle is visible by default")
    public void testAiSearchToggleVisible() {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertTrue(search.isAiToggleVisible(), "AI Search toggle should be visible by default");
    }

    @Test
    @DisplayName("Test: AI Search toggle prepends semantic prefix when enabled")
    public void testAiSearchTogglePrefix() throws InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        search.setAiToggle(true);
        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        String requestUrl = search.getLastSearchResultsRequestUrl();
        assertNotNull(requestUrl, "Search results request should be fired");
        assertTrue(requestUrl.contains("fulltext=%3F%7B%7D%3FPage") || requestUrl.contains("fulltext=?%7B%7D%3FPage"),
            "AI Search enabled request should include the ?{}? prefix in fulltext");
    }

    @Test
    @DisplayName("Test: hideAiSearchToggle policy hides the AI Search toggle")
    public void testHideAiSearchTogglePolicy() throws ClientException, InterruptedException {
        createComponentPolicy(proxyPath.substring(proxyPath.lastIndexOf("/")), new HashMap<String, String>() {{
            put("hideAiSearchToggle", "true");
        }});

        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");
        assertFalse(search.isAiToggleVisible(), "AI Search toggle should be hidden when policy is enabled");

        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);

        String requestUrl = search.getLastSearchResultsRequestUrl();
        assertNotNull(requestUrl, "Search results request should be fired");
        assertFalse(requestUrl.contains("%3F%7B%7D%3F"), "Lexical search should not include the semantic prefix");
    }
}
