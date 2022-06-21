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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.title;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.$;

public class TitleEditDialog extends Dialog {

    private static String titleWithSize = "coral-selectlist-item[value='h%s']";
    private static String titleType = "[name='./type']";
    private static String linkUrl = "[name='./linkURL']";
    private static String linkTarget = "coral-checkbox[name='./linkTarget']";
    private static String titleTypeDropdownDefaultSelected = titleType + " coral-select-item[selected]";

    private void openTitleTypeList() throws InterruptedException{
        $( titleType + " > button").click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    public void setTitle(String value) {
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        new WebDriverWait(webDriver, RequestConstants.TIMEOUT_TIME_SEC)
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format("%s " + Selectors.SELECTOR_CORAL_DIALOG_CONTENT, this.getCssSelector()))));
        content().find(Selectors.SELECTOR_BUTTON_TITLE).click();
        content().find(Selectors.SELECTOR_BUTTON_TITLE).sendKeys(value);
    }

    public boolean isTitleTypeSelectPresent() {
        return $(titleType).isDisplayed();
    }

    /**
     * @return list of the related coral popover that is opened.
     */
    public CoralSelectList getTitleTypeSelectList() {
        CoralSelectList coralSelectList = new CoralSelectList($(titleType));
        if(coralSelectList.isVisible()) {
            return coralSelectList;
        } else {
            CoralPopOver popOver = CoralPopOver.firstOpened();
            popOver.waitVisible();
            waitForElementAnimationFinished(popOver.getCssSelector());
            return new CoralSelectList(popOver.element());
        }
    }

    public boolean isAllDefaultTitleTypesPresent() throws InterruptedException {
        openTitleTypeList();
        boolean present = true;
        for(int i = 1; i <= 6; i++) {
            if(!$(String.format(titleWithSize, i)).isDisplayed()) {
                present = false;
            }
        }
        // click default
        getTitleTypeSelectList().items().first().click();
        return present;
    }

    public boolean isTitleTypePresent(String size) throws InterruptedException {
        openTitleTypeList();
        CoralSelectList coralSelectList = getTitleTypeSelectList();
        boolean present = false;
        for(int i = 0; i < coralSelectList.items().size(); i++) {
            if(coralSelectList.items().get(i).getValue().trim().equals("h" + size)) {
                present = true;
            }
        }

        // click default
        coralSelectList.items().first().click();
        return present;
    }

    public boolean isTitleTypesPresent(String [] sizes) throws InterruptedException {
        openTitleTypeList();
        boolean present = true;
        for(int i = 0; i < sizes.length; i++) {
            if(!$(String.format(titleWithSize, sizes[i])).isDisplayed()) {
                present = false;
            }
        }
        CoralSelectList coralSelectList = getTitleTypeSelectList();
        coralSelectList.items().first().click();
        return present;
    }

    public void selectTitleType(String size) throws InterruptedException {
        openTitleTypeList();
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(String.format(titleWithSize, size)));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        $(String.format(titleWithSize, size)).click();
    }

    public void setLinkURL(String url) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + linkUrl);
        autoCompleteField.sendKeys(url);
        $("button[is='coral-buttonlist-item'][value='" + url + "']").click();
    }

    public void clickLinkTarget() {
        CoralCheckbox checkbox = new CoralCheckbox(linkTarget);
        checkbox.click();
    }

    public String getTitleTypeDropdownDefaultSelectedText() {
        return $(titleTypeDropdownDefaultSelected).innerText();
    }

}
