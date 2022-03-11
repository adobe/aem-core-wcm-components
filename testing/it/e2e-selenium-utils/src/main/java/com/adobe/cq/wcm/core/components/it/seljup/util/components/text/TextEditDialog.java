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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.text;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import static com.codeborne.selenide.Selenide.$;

public class TextEditDialog extends Dialog {

    private static SelenideElement stylesTab = $("coral-tab[data-foundation-tracking-event*='styles']");

    public void setText(String value) {
        String textBox = "input[name='./text']";
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(textBox));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].value=arguments[1];", element, value);
    }

    public void setId(String value) {
        $("[name='./id']").sendKeys(value);
    }

    // ----------------------------------------------------------
    // Style Tab
    // ----------------------------------------------------------

    public void openStylesTab() {
        stylesTab.click();
    }

    public Boolean isStyleSelectMenuDisplayed() {
        return $("coral-select[name='./cq:styleIds']").isDisplayed();
    }

    public Boolean isNoStyleOptionSelectedByDefault() {
        return $("coral-select[name='./cq:styleIds'] > coral-select-item[selected]").innerText().equals("None");
    }

    public Boolean isBlueStyleOptionSelected() {
        return $("coral-select[name='./cq:styleIds'] > coral-select-item[selected]").innerText().equals("Blue");
    }

    public void openStyleSelectDropdown() {
        $("coral-select[name='./cq:styleIds'] button").click();
    }

    public Boolean areExpectedOptionsForNoStyleAppliedPresentInDropdown() {
        return $("coral-selectlist-item[selected]").innerText().equals("None") &&
                $("coral-selectlist-item[value='1547060098888']").exists() &&
                $("coral-selectlist-item[value='1550165689999']").exists();
    }

    public Boolean areExpectedOptionsForAppliedStylePresentInDropdown() {
        return $("coral-selectlist-item[selected][value='1547060098888']").exists() &&
                $("coral-selectlist-item[value='1550165689999']").exists();
    }

    public Boolean componentHasNoClassesAppliedByTheStyleSystem(String textComponentId) {
        return !$(".cmp-blue-text" + textComponentId).exists() && !$(".cmp-red-text" + textComponentId).exists();
    }

    public Boolean componentHasExpectedClassAppliedByTheStyleSystem(String textComponentSelector, String expectedClassSelector) {
        return $(expectedClassSelector + " " + textComponentSelector).exists();
    }

    public Boolean componentHasNoSpecificClassAppliedByTheStyleSystem(String textComponentSelector, String classSelector) {
        return !$(textComponentSelector + " " + classSelector).exists();
    }

    public void pressArrowDown() {
        Selenide.actions().pause(Duration.ofMillis(250L)).sendKeys(new CharSequence[]{Keys.ARROW_DOWN}).perform();
    }

    public void pressEnter() {
        Selenide.actions().pause(Duration.ofMillis(250L)).sendKeys(new CharSequence[]{Keys.ENTER}).perform();
    }
}
