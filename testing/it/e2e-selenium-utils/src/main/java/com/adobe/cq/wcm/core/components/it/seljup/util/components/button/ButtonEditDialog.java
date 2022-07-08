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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.button;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelect;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.SelenideElement;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.codeborne.selenide.Selenide.$;


public class ButtonEditDialog extends Dialog {
    private static final String CSS_SELECTOR = "coral-dialog";
    private static String linkTarget = ".cmp-button__editor coral-checkbox[name='./linkTarget']";

    public ButtonEditDialog() {
        super(CSS_SELECTOR);
    }

    public ButtonEditDialog(SelenideElement element) {
        super(element);
    }

    public void setTitleField(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s " + Selectors.SELECTOR_CORAL_DIALOG_CONTENT, this.getCssSelector()))));
        content().find(Selectors.SELECTOR_BUTTON_TITLE).click();
        content().find(Selectors.SELECTOR_BUTTON_TITLE).sendKeys(value);
    }

    public void setNameField(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s " + Selectors.SELECTOR_CORAL_DIALOG_CONTENT, this.getCssSelector()))));
        content().find(Selectors.SELECTOR_BUTTON_NAME).click();
        content().find(Selectors.SELECTOR_BUTTON_NAME).sendKeys(value);
    }

    public void setValueField(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s " + Selectors.SELECTOR_CORAL_DIALOG_CONTENT, this.getCssSelector()))));
        content().find(Selectors.SELECTOR_BUTTON_VALUE).click();
        content().find(Selectors.SELECTOR_BUTTON_VALUE).sendKeys(value);
    }

    public boolean isNameFieldInvalid() {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s " + Selectors.SELECTOR_CORAL_DIALOG_CONTENT, this.getCssSelector()))));
        return content().find(Selectors.SELECTOR_BUTTON_NAME).getAttribute("invalid").equals("true");
    }

    public void setLinkField(String value) {
        setLinkField(value, "link");
    }

    public void setLinkField(String value, String propertyName) {
        String selector = "./" + propertyName;
        AutoCompleteField autoCompleteField = new AutoCompleteField(selector);
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC).until(ExpectedConditions.elementToBeClickable(By.cssSelector(autoCompleteField.getCssSelector())));
        autoCompleteField.sendKeys(value);
    }

    public String getLinkFieldValue() {
        String selector = "./linkURL";
        AutoCompleteField autoCompleteField = new AutoCompleteField(selector);
        return autoCompleteField.value();
    }

    public void clickLinkTarget() {
        CoralCheckbox checkbox = new CoralCheckbox(linkTarget);
        checkbox.click();
    }

    public SelenideElement getIcon() {
        return content().find(Selectors.SELECTOR_ICON);
    }

    public void selectButtonType(String type) {
        $( "[name='./type'] > button").click();
        CoralSelectList coralSelectList = new CoralSelectList($("[name='./type']"));
        if(!coralSelectList.isVisible()) {
            CoralSelect selectList = new CoralSelect("name='./type'");
            coralSelectList = selectList.openSelectList();
        }

        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector("coral-selectlist-item[value='" + type + "']"));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        coralSelectList.selectByValue(type);
    }

}
