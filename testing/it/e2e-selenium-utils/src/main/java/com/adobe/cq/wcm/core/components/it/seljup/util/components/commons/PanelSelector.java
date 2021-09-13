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

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.RequestConstants;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.actions;

public class PanelSelector extends BaseComponent {
    private static final ElementsCollection items = $$(".cmp-panelselector__table [is='coral-table-row']");

    public PanelSelector() {
        super(".cmp-panelselector");
    }

    /**
     * Returns the items in panel select
     * @return items in panel select
     */
    public ElementsCollection getItems() {
        return items;
    }

    /**
     * re-orders the items in panel select
     * @param dragElement element to be moved
     * @param targetElement element at which location the dragElement to be moved
     * @throws InterruptedException
     */
    public void reorderItems(int dragElement,int targetElement) throws InterruptedException {
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        ElementsCollection items = this.getItems();
        SelenideElement dragElementMoveButton = items.get(dragElement).find("button[coral-table-roworder='true']");
        SelenideElement targetElement1 = items.get(targetElement);
        int yOffset = targetElement1.getSize().getHeight() + 1;
        actions().clickAndHold(dragElementMoveButton).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().moveToElement(targetElement1).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().moveByOffset(0, yOffset).build().perform();
        Commons.webDriverWait(RequestConstants.WEBDRIVER_WAIT_TIME_MS);
        actions().release().build().perform();
    }
}
