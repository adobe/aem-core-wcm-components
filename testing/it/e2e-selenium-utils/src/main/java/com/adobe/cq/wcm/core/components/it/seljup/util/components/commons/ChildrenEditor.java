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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.commons;

import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.testing.selenium.pagewidgets.common.ActionComponent;
import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralMultiField;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ChildrenEditor extends BaseComponent {
    public ChildrenEditor () {
        super(".cmp-childreneditor");
    }

    private static String addButton = "[data-cmp-hook-childreneditor='add']";
    private static String removeButton = "button[handle='remove']";
    private static String moveButton = "button[handle='move']";
    private static String item = "coral-multifield-item";
    private static String inputItem = "[data-cmp-hook-childreneditor='itemTitle']";
    private static String hiddenInputItem = "[data-cmp-hook-childreneditor='itemResourceType']";

    /**
     * Click the add button in ChildrenEditor
     */
    public void clickAddButton() {
         $(addButton).click();
    }

    /**
     * Get the input elements in ChildrenEditor
     * @return input elements in ChildrenEditor
     */
    public ElementsCollection getInputItems() {
        return $$(inputItem).filter(visible);
    }

    /**
     * Removes the first element in ChildrenEditor
     */
    public void removeFirstItem() {
        ActionComponent<Dialog> dialogActionComponent = new ActionComponent<>($$(item + " " + removeButton).first(), () -> new Dialog("coral-dialog[variant='warning']"), false);
        Dialog dialog = Helpers.clickDialogAction(dialogActionComponent);
        dialog.clickWarning();
    }

    /**
     * Move the items in Children Editor
     * @param dragElement Element to be moved
     * @param targetElement Element before which drageElement to be mmoved
     * @throws InterruptedException
     */
    public void moveItems(int dragElement,int targetElement) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        SelenideElement dragElementMoveButton = $$(item + " " + moveButton).get(dragElement);
        SelenideElement targetElement1 = $$(item).get(targetElement);
        int yOffset = (targetElement1.getSize().getHeight() / 2  + 1) * (-1);
        actions().clickAndHold(dragElementMoveButton).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().moveToElement(targetElement1).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().moveByOffset(-1, yOffset).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().release().build().perform();
    }

    /**
     * Returns children editor multifield
     * @return children editor multifield
     */
    public CoralMultiField getItemsMultifield() {
        return new CoralMultiField("childrenEditor");
    }
}
