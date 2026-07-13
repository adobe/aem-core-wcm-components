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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v3;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;

public class Search extends com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v2.Search {

    private static final String aiToggle = "[data-cmp-hook-search='aiToggle']";

    public boolean isAiToggleVisible() {
        return $(aiToggle).exists() && $(aiToggle).isDisplayed();
    }

    public boolean isAiToggleChecked() {
        return $(aiToggle).isSelected();
    }

    public void setAiToggle(boolean enabled) {
        if ($(aiToggle).isSelected() != enabled) {
            $(aiToggle).click();
        }
    }

    public String getLastSearchResultsRequestUrl() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        return (String) executor.executeScript(
            "var entries = performance.getEntriesByType('resource');"
                + "for (var i = entries.length - 1; i >= 0; i--) {"
                + "  if (entries[i].name.indexOf('searchresults') !== -1) { return entries[i].name; }"
                + "}"
                + "return null;");
    }
}
