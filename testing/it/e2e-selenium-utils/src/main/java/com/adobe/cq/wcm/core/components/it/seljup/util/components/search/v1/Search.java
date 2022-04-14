/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.it.seljup.util.components.search.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.search.SearchEditDialog;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Search extends BaseComponent {
    private static String search = "[data-cmp-is='search']";
    private static String input = "[data-cmp-hook-search='input']";
    private static String clear = "[data-cmp-hook-search='clear']";
    private static String results = "[data-cmp-hook-search='results']";
    private static String item = "[data-cmp-hook-search='item']";
    private static String itemMark = ".cmp-search__item-mark";

    public Search() {
        super(search);
    }

    public void setInput(String value) {
        $(input).clear();
        $(input).sendKeys(value);
    }

    public boolean isResultsVisible() {
        return $(results).isDisplayed();
    }

    public int getResultsCount() {
        return $$(item).size();
    }

    public boolean isPagePresentInSearch(String pagePath) {
        return $(item + "[href='"+pagePath+".html']").isDisplayed();
    }

    public SearchEditDialog getEditDialog() {
        return new SearchEditDialog();
    }

    public boolean isClearVisible() {
        return $(clear).isDisplayed();
    }

    public String getInputValue() {
        return $(input).getValue();
    }

    public void clickClear() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(clear));
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].click();", element);
    }

    public void pressEnter() {
        Selenide.actions().pause(Duration.ofMillis(250L)).sendKeys(new CharSequence[]{Keys.ENTER}).perform();
    }

    public void clickOutside() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.xpath("//body"));
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].click();", element);
    }

    public boolean isMarkItemsPresent(String value) {
        if($(itemMark).isDisplayed()) {
            return $(itemMark).getText().contains(value);
        }

        return false;
    }

    public void scrollResults(int scrollBy) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(results));
        JavascriptExecutor executor = (JavascriptExecutor) webDriver;
        executor.executeScript("arguments[0].scrollTop += arguments[1]", element, scrollBy);
    }
}
