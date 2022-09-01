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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class BaseBreadcrumbItems {

    protected  String activeItem;
    protected  String items;
    protected   String activeItems;


    /**
     * Check if breadcrumb item is active
     * @param containsString
     * @return true if item with containsString is active otherwise false
     */
    public  boolean isItemActive(String containsString) {
        return $x(String.format(activeItem,containsString)).exists();
    }

    /**
     * Get breadcrumb items
     * @return breadcrumb items
     */
    public  ElementsCollection getItems() {
        return $$x(items);
    }

    /**
     * Get the active breadcrumb items
     * @return active breadcrumb items
     */
    public  ElementsCollection getActiveItems() { return $$x(activeItems); }

    /**
     * Check if breadcrumb item is present
     * @param containsString
     * @return true if item with containsString is present otherwise false
     */
    public boolean isItemPresent(String containsString) {
        return $x(items + "/a/span[contains(text(),'" + containsString + "')]").exists();
    }
}
