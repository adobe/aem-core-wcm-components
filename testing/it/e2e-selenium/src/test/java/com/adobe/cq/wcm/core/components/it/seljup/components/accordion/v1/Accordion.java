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

package com.adobe.cq.wcm.core.components.it.seljup.components.accordion.v1;

import com.adobe.cq.wcm.core.components.it.seljup.components.accordion.AccordionEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.commons.CQOverlay;
import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Accordion Component
 */
public class Accordion extends BaseComponent {

    private static String accordionItem = "[data-cmp-hook-accordion='item']";
    private static String itemExpanded = "[data-cmp-hook-accordion='item'][data-cmp-expanded]";
    private static String itemButton = "[data-cmp-hook-accordion='button']";

    public Accordion() {
        super(".cmp-accordion");
    }

    /**
     * Returns the accordion Items
     *
     * @returns the accordion Items
     */
    public ElementsCollection getAccordionItem() {
        return $$(accordionItem);
    }

    /**
     * Returns accordion item button at position idx
     * @param idx the position of the accordion item to return
     * @return accordion item at idx
     */
    public SelenideElement getAccordionItemButton(int idx) {
        return $$(accordionItem).get(idx).find(itemButton);
    }

    /**
     * Returns expanded items collection
     *
     * @return expanded items collection
     */
    public static ElementsCollection getItemExpanded() {
        return $$(itemExpanded);
    }

    /**
     * Returns accordion edit dialog object
     * @return Accordion EditDialog
     */
    public AccordionEditDialog getEditDialog() {
        return new AccordionEditDialog();
    }

    /**
     * Returns cq-overlay
     * @returncq cq-overlay
     */
    public CQOverlay getCQOverlay() {
        return new CQOverlay();
    }

}
