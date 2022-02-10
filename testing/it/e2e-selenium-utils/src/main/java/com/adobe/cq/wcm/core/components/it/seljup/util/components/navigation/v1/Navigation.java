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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.navigation.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.navigation.NavigationEditDialog;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Navigation extends BaseComponent {
    private static String item = ".cmp-navigation__item";
    private static String activeItem = ".cmp-navigation__item--active";
    private static String level0Item = ".cmp-navigation__item--level-0";
    private static String level1Item = ".cmp-navigation__item--level-1";
    private static String level2Item = ".cmp-navigation__item--level-2";
    private static String levelItem = ".cmp-navigation__item--level-%s";
    private static String linkItem = "a.cmp-navigation__item-link[href$='%s']";

    public Navigation() {
        super(item);
    }

    public int navigationItemsCount() {
        return $$(item).size();
    }

    public boolean isActiveItemContainValue(String level, String value) {
        return $(item + String.format(levelItem,level) + activeItem).getText().trim().contains(value);
    }

    public boolean isItemPresentContainValue(String level, String value) {
        ElementsCollection items = $$(item + String.format(levelItem, level));
        boolean itemPresent = false;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getText().trim().contains(value)) {
                itemPresent = true;
                break   ;
            }
        }
        return itemPresent;
    }



    public boolean isLinkItemPresentContainsValue(String value) {
        return $(String.format(linkItem, value)).isDisplayed();
    }

    public NavigationEditDialog getEditDialog() {
        return new NavigationEditDialog();
    }
}
