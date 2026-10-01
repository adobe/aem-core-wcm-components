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

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.microsoft.playwright.Locator;

import static com.adobe.cq.wcm.core.components.it.seljup.util.Commons.RT_SEARCH_V2;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("playwright-group3")
public class SearchV2PwIT extends SearchV1PwIT {

    @Override
    protected String searchResourceType() {
        return RT_SEARCH_V2;
    }

    @Override
    protected String searchClientlib() {
        return Commons.CLIENTLIBS_SEARCH_V2;
    }

    @Test
    public void testSearchResultsStatusMessage() throws Exception {
        createSearch();
        Locator status = page.locator(".cmp_search__info");
        page.locator(".cmp-search__input").fill("Page");
        assertThat(status).containsText("results");
        page.locator(".cmp-search__clear").dispatchEvent("click");
        assertThat(status).isHidden();
        page.locator(".cmp-search__input").fill("no-results-expected-text");
        assertThat(status).hasText("No results");
        page.locator(".cmp-search__input").fill("");
        assertThat(status).isHidden();
    }
}
