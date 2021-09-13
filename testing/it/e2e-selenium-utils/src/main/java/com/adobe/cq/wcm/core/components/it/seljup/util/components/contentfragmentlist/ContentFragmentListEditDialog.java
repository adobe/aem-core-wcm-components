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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.contentfragmentlist;

import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ContentFragmentListEditDialog extends Dialog {

    private static String parentPath = "[name='./parentPath']";
    private static String fragmentPath = "[name='./fragmentPath']";
    private static String tagNames = "[name='./tagNames']";
    private static String elementNameSelectButton = "coral-select[name='./elementNames']  > button";
    private static SelenideElement elementTab = $("coral-tab[data-foundation-tracking-event*='elements']");

    public String getParentPath() {
        return parentPath;
    }

    public String getTagNames() {
        return tagNames;
    }

    public void openElementTab() {
        $(elementTab).click();
    }

    public void addElement(String value) {
        $("[coral-multifield-add]").click();
        $$("coral-multifield-item").last().$(elementNameSelectButton).click();
        CoralSelectList coralSelectList = new CoralSelectList($$("coral-multifield-item").last().$("coral-select[name='./elementNames']"));
        if(!coralSelectList.isVisible()) {
            CoralPopOver popOver =  CoralPopOver.firstOpened();
            popOver.waitVisible();
            Helpers.waitForElementAnimationFinished(popOver.getCssSelector());
            coralSelectList = new CoralSelectList(popOver.element());
        }

        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + value + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        coralSelectList.selectByValue(value);
    }
}
