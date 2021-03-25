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

import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

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
        ElementsCollection items = this.getItems();
        items.get(dragElement).find("[coral-table-roworder='true']").dragAndDropTo(items.get(targetElement));
    }
}
