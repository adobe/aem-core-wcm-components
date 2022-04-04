/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.tests.search.v2;

import org.apache.sling.testing.clients.ClientException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v2.Search;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("group3")
public class SearchIT extends com.adobe.cq.wcm.core.components.it.seljup.tests.search.v1.SearchIT {

    protected Search search;

    @Override
    protected void setupResources() {
        super.setupResources();
        clientlibs = Commons.CLIENTLIBS_SEARCH_V2;
        proxyPath = Commons.RT_SEARCH_V2;
        search = new Search();
    }

    @BeforeEach
    public void setupBeforeEach() throws ClientException {
        setupResources();
        setup();
    }

    /**
     * Test: Search Status Message - Message containing the number of found results
     */
    @Test
    @DisplayName("Test: Search Status Message - Message containing the number of found results")
    public void testSearchResultsStatusMessage() throws InterruptedException {
        editorPage.enterPreviewMode();
        Commons.switchContext("ContentFrame");

        search.setInput("Page");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isSearchResultsStatusMessageVisible());
        assertTrue(search.hasSearchResultsStatusMessageExpectedText(search.getResultsCount() + " results"),
            "Search results status message containing the number of results should be displayed");

        search.clickClear();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(search.isSearchResultsStatusMessageVisible(),
            "Search results status message should not be displayed after clicking the Clear button");


        search.setInput("no-results-expected-text");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertTrue(search.isSearchResultsStatusMessageVisible());
        assertTrue(search.hasSearchResultsStatusMessageExpectedText("No results"),
            "No results status message should be displayed when there are no search results");

        search.setInput("");
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        assertFalse(search.isSearchResultsStatusMessageVisible(),
            "Search results status message should not be displayed when no character is inserted in the search input");
    }
}
