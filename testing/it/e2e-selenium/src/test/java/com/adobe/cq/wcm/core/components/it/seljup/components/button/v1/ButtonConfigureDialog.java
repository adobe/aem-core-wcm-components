/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.components.button.v1;

import com.adobe.qe.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.qe.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.SelenideElement;
import com.adobe.qe.selenium.pagewidgets.cq.AutoCompleteField;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ButtonConfigureDialog extends Dialog {
    private static final String CSS_SELECTOR = "coral-dialog";

    public ButtonConfigureDialog() {
        super(CSS_SELECTOR);
    }

    public ButtonConfigureDialog(SelenideElement element) {
        super(element);
    }

    public SelenideElement getTitleField() {
        return content().find("input[name='./jcr:title']");
    }

    public SelenideElement getNameField() {
        return content().find("input[name='./name']");
    }

    public SelenideElement getValueField() {
        return content().find("input[name='./value']");
    }

    public void setLinkField(String value) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("./link");
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, 5).until(ExpectedConditions.elementToBeClickable(By.cssSelector(autoCompleteField.getCssSelector())));
        autoCompleteField.sendKeys(value);
    }

    public SelenideElement getIcon() {
        return content().find("input[name='./icon']");
    }

    public void selectButtonType(String type) {
        CoralSelect coralSelect = new CoralSelect("name='./type'");
        coralSelect.selectItemByValue(type);
    }

}
