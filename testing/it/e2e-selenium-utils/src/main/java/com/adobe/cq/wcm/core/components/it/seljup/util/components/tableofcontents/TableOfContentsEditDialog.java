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
package com.adobe.cq.wcm.core.components.it.seljup.util.components.tableofcontents;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.$;

public class TableOfContentsEditDialog extends Dialog {

    private static String listTypeSelect = "[name='./listType']";
    protected static String startLevelSelect = "[name='./startLevel']";
    protected static String stopLevelSelect = "[name='./stopLevel']";
    private static String id = "[name='./id']";

    private static String invalidLevelsErrorTooltip = ".cmp-toc__editor coral-tooltip.is-open[variant='error']";

    private static String[] listTypes = new String[] {
        "bulleted",
        "numbered"
    };
    private static String listTypeSelectItemTemplate =
        "coral-popover.is-open coral-selectlist-item[value='%s']";
    private static int minLevel = 1;
    private static int maxLevel = 6;
    protected static String levelSelectItemTemplate =
        "coral-popover.is-open coral-selectlist-item[value='%s']";
;

    public boolean isListTypeSelectPresent() {
        return $(listTypeSelect).isDisplayed();
    }

    public boolean isAllListTypesPresent() throws InterruptedException {
        openSelectList(listTypeSelect);
        CoralSelectList coralSelectList = getSelectList(listTypeSelect);
        List<String> selectListItems = new ArrayList();
        for(int i = 0; i < coralSelectList.items().size(); i++) {
            selectListItems.add(
                coralSelectList.items().get(i).getValue().trim()
            );
        }
        boolean present = true;
        for(String listType: listTypes) {
            if(!selectListItems.contains(listType)) {
                present = false;
                break;
            }
        }
        coralSelectList.items().first().click();
        return present;
    }

    public boolean isStartLevelSelectPresent() {
        return $(startLevelSelect).isDisplayed();
    }

    public boolean isAllStartLevelsPresent() throws InterruptedException {
        return isAllLevelsPresent(startLevelSelect);
    }

    public boolean isStopLevelSelectPresent() {
        return $(stopLevelSelect).isDisplayed();
    }

    public boolean isAllStopLevelsPresent() throws InterruptedException {
        return isAllLevelsPresent(stopLevelSelect);
    }

    public boolean isIdTextBoxPresent() {
        return $(id).isDisplayed();
    }

    public void selectListType(String listType) throws InterruptedException {
        selectItem(listTypeSelect, String.format(listTypeSelectItemTemplate, listType));
    }

    public void selectStartLevel(String startLevel) throws InterruptedException {
        selectItem(startLevelSelect, String.format(levelSelectItemTemplate, startLevel));
    }

    public void selectStopLevel(String stopLevel) throws InterruptedException {
        selectItem(stopLevelSelect, String.format(levelSelectItemTemplate, stopLevel));
    }

    public boolean isInvalidLevelsErrorTooltipPresent() {
        return $(invalidLevelsErrorTooltip).isDisplayed();
    }

    public void setId(String id) {
        $(this.id).setValue(id);
    }

    private void openSelectList(String select) throws InterruptedException {
        $( select + " > button").click();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
    }

    private CoralSelectList getSelectList(String select) {
        CoralSelectList coralSelectList = new CoralSelectList($(select));
        if(coralSelectList.isVisible()) {
            return coralSelectList;
        } else {
            CoralPopOver popOver = CoralPopOver.firstOpened();
            popOver.waitVisible();
            waitForElementAnimationFinished(popOver.getCssSelector());
            return new CoralSelectList(popOver.element());
        }
    }

    private boolean isAllLevelsPresent(String levelSelect) throws InterruptedException {
        openSelectList(levelSelect);
        CoralSelectList coralSelectList = getSelectList(levelSelect);
        List<String> selectListItems = new ArrayList();
        for(int i = 0; i < coralSelectList.items().size(); i++) {
            selectListItems.add(
                coralSelectList.items().get(i).getValue().trim()
            );
        }
        boolean present = true;
        for(int level = minLevel; level <= maxLevel; level++) {
            if(!selectListItems.contains("h" + level)) {
                present = false;
                break;
            }
        }
        coralSelectList.items().first().click();
        return present;
    }

    void selectItem(String select, String item) throws InterruptedException {
        openSelectList(select);
        final WebDriver webDriver = WebDriverRunner.getWebDriver();
        WebElement element = webDriver.findElement(By.cssSelector(item));
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(true);", element);
        $(item).click();
    }
}
