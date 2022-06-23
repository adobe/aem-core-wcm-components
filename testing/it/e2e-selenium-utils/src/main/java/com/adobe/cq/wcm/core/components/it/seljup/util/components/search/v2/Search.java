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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v2;

import static com.codeborne.selenide.Selenide.$;

public class Search extends com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v1.Search {
    static String searchResultsStatusMessage = ".cmp_search__info";

    public boolean isSearchResultsStatusMessageVisible() {
        return $(searchResultsStatusMessage).isDisplayed();
    }

    public boolean hasSearchResultsStatusMessageExpectedText(String expected) {
        return $(searchResultsStatusMessage).getText().equals(expected);
    }
}