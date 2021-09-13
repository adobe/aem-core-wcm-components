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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.tabs;


import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralPopOver;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.adobe.cq.testing.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.$;

public class TabsEditDialog {

    private static SelenideElement itemsTab = $("coral-tab[data-foundation-tracking-event*='items']");
    private static SelenideElement propertiesTab = $("coral-tab[data-foundation-tracking-event*='properties']");

    /**
     * Returns ChildrenEditor object
     * @return ChildrenEditor
     */
    public ChildrenEditor getChildrenEditor() {
        return new ChildrenEditor();
    }

    public void openItemsTab() {
        $(itemsTab).click();
    }

    public EditDialogProperties openPropertiesTab() {
        $(propertiesTab).click();
        return new EditDialogProperties();
    }

    /**
     * Returns InsertComponent dialog
     * @return InsertComponentDialog
     */
    public InsertComponentDialog getInsertComponentDialog() {
        return new InsertComponentDialog();
    }

    public static final class EditDialogProperties {
        private static String activeSelect = "[data-cmp-tabs-v1-dialog-edit-hook='activeSelect']";
        private static String activeSelectButton = "[data-cmp-tabs-v1-dialog-edit-hook='activeSelect'] button";



        private void clickActiveSelect() {
            $(activeSelectButton).click();
        }

        private CoralSelectList selectList() {
            CoralSelectList coralSelectList = new CoralSelectList($(activeSelect));
            if(coralSelectList.isVisible()) {
                return coralSelectList;
            } else {
                CoralPopOver popOver = CoralPopOver.firstOpened();
                popOver.waitVisible();
                waitForElementAnimationFinished(popOver.getCssSelector());
                return new CoralSelectList(popOver.element());
            }
        }

        public void setItemActive(String item) {
            clickActiveSelect();
            ElementsCollection items = selectList().items();
            int itemsSize = items.size();
            for(int i = 0; i < items.size(); i++) {
                if(items.get(i).find(Selectors.SELECTOR_ITEM_ELEMENT_CONTENT).isDisplayed() &&
                    items.get(i).find(Selectors.SELECTOR_ITEM_ELEMENT_CONTENT).getText().contains(item) || (items.get(i).getText().contains(item))) {
                    items.get(i).click();
                    break;
                }
            }
        }
    }
}
