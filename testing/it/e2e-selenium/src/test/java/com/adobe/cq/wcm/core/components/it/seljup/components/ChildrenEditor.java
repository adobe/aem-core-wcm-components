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

package com.adobe.cq.wcm.core.components.it.seljup.components;

import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.adobe.qe.selenium.pagewidgets.coral.CoralMultiField;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Selenide.Wait;




import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.actions;

public class ChildrenEditor extends BaseComponent {
    public ChildrenEditor () {
        super(".cmp-childreneditor");
    }

    private static String addButton = "[data-cmp-hook-childreneditor='add']";
    private static String removeButton = "button[handle='remove']";
    private static String moveButton = "button[handle='move']";
    private static String item = "coral-multifield-item";
    private static String firstItem = "coral-multifield-item:first";
    private static String lastItem = "coral-multifield-item:last";
    private static String inputItem = "[data-cmp-hook-childreneditor='itemTitle']";
    private static String hiddenInputItem = "[data-cmp-hook-childreneditor='itemResourceType']";

    public void clickAddButton() {
         $(addButton).click();
    }

    public SelenideElement getRemoveButton() {
        return $(removeButton);
    }

    public SelenideElement getItem() {
        return $(item);
    }

    public SelenideElement getFirstItem() {
        return $(firstItem);
    }

    public SelenideElement getLastItem() {
        return $(lastItem);
    }

    public ElementsCollection getInputItems() {
        return $$(inputItem);
    }

    public SelenideElement getHiddenInputItem() {
        return $(hiddenInputItem);
    }

    public SelenideElement getFirstInputItem() {
        return $(firstItem + " " + inputItem);
    }

    public SelenideElement getLastInputItem() {
        return $(lastItem + " " + inputItem);
    }

    public void removeFirstItem() {
         $$(item + " " + removeButton).first().click();
    }

    public void moveItems(int dragElement,int targetElement) throws InterruptedException {
        //$$(item + " " + moveButton).get(dragElement).dragAndDropTo($$(item + " " + moveButton).get(targetElement));
        //actions().dragAndDrop($$(item + " " + moveButton).get(dragElement), $$(item).get(targetElement)).perform();
        //$$(item + " " + moveButton).get(dragElement).dragAndDropTo($$(item).get(targetElement));
        SelenideElement dragElementMoveButton = $$(item + " " + moveButton).get(dragElement);
        SelenideElement targetElement1 = $$(item).get(targetElement);
        int yOffset = (targetElement1.getSize().getHeight() / 2  + 1) * (-1);
        actions().clickAndHold(dragElementMoveButton).build().perform();
        Commons.webDriverWait(1000);
        actions().moveToElement(targetElement1).build().perform();
        Commons.webDriverWait(1000);
        actions().moveByOffset(-1, yOffset).build().perform();
        Commons.webDriverWait(1000);
        actions().release().build().perform();
    }

    public CoralMultiField getItemsMultifield() {
        return new CoralMultiField("childrenEditor");
    }
}
