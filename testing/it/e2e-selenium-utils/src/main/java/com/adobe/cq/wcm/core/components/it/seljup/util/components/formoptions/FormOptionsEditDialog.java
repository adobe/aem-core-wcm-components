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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FormOptionsEditDialog extends Dialog {

    /**
     * Set the mandatory fields
     */
    public void setMandatoryFields(String value, String title) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        $("[name='./name']").sendKeys(value);
        SelenideElement el = Commons.getVisibleElement($$("[name='./jcr:title']"));
        if(el != null)
            el.sendKeys(title);
    }

    /**
     * Add an option
     */
    public void addOption(String value, String text) {
        $("button[coral-multifield-add='']").click();
        $("input[name$='./value']").sendKeys(value);
        $("input[name$='./text']").sendKeys(text);
    }

    /**
     * Set the option type
     */
    public void setOptionType(String optionType) {
        //Open selectlist
        $( "[name='./type'] > button").click();
        CoralSelectList coralSelectList = new CoralSelectList($("[name='./type']"));
        if(!coralSelectList.isVisible()) {
            CoralSelect selectList = new CoralSelect("name='./type'");
            coralSelectList = selectList.openSelectList();
        }

        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + optionType + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
         coralSelectList.selectByValue(optionType);
    }

    /**
     * Set help message
     */
    public void setHelpMessage(String helpMessage) {
        $("[name='./helpMessage']").sendKeys(helpMessage);
    }

    public boolean isMandatoryFieldsInvalid() {
        return isNameFieldsInvalid() && isTitleFieldsInvalid();
    }

    public boolean isNameFieldsInvalid() {
        return $("[name='./name']").getAttribute("invalid").equals("true");
    }

    public boolean isTitleFieldsInvalid() {
         return $$("[name='./jcr:title'][invalid='']").size() == 1;
    }

    public void checkSelectedCheckbox() {
        CoralCheckbox checkbox = new CoralCheckbox("[name$='selected']");
        checkbox.click();
    }


    public void checkDisabledCheckbox() {
        CoralCheckbox checkbox = new CoralCheckbox("[name$='disabled']");
        checkbox.click();
    }
}
