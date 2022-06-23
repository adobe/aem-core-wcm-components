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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.languagenavigation.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.languagenavigation.LanguageNavigationEditConfig;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class LanguageNavigation extends BaseComponent {

    private static String item = ".cmp-languagenavigation__item";
    private static String activeItem = ".cmp-languagenavigation__item--active";
    private static String level0Item = ".cmp-languagenavigation__item--level-0";
    private static String level1Item = ".cmp-languagenavigation__item--level-1";
    private static String linkItem = "cmp-languagenavigation__item-link";
    private static String placeholder = ".cq-placeholder";

    public LanguageNavigation() {
        super(".cmp-languagenavigation");
    }

    public LanguageNavigationEditConfig getEditDialog() {
        return new LanguageNavigationEditConfig();
    }

    public boolean isLevel0ItemActiveContainValue(String value) {
        return $(item + level0Item + activeItem).getText().trim().contains(value);
    }

    public boolean isLevel0ItemPresentContainValue(String value) {
         ElementsCollection items = $$(item + level0Item);
         boolean itemPresent = false;
         for(int i = 0; i < items.size(); i++) {
             if(items.get(i).getText().trim().contains(value)) {
                 itemPresent = true;
                 break;
             }
         }
         return itemPresent;
    }

    public boolean isLevel1ItemActiveContainValue(String value) {
        return $(item + level1Item + activeItem).getText().trim().contains(value);
    }

    public boolean isLevel1ItemPresentContainValue(String value) {
        ElementsCollection items = $$(item + level1Item);
        boolean itemPresent = false;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().trim().contains(value)) {
                itemPresent = true;
                break;
            }
        }
        return itemPresent;
    }

    public boolean isItemPresentContainValue(String value) {
        ElementsCollection items = $$(item);
        boolean itemPresent = false;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().trim().contains(value)) {
                itemPresent = true;
                break;
            }
        }
        return itemPresent;
    }

    public boolean isItemPresent() {
        return $(item).isDisplayed();
    }

    public boolean isPlaceholderItemPresent() {
        return $(placeholder + this.getCssSelector()).isDisplayed();
    }

    public boolean isLinkItemPresent() {
        return $(item + level0Item + " " + linkItem).isDisplayed();
    }
}
