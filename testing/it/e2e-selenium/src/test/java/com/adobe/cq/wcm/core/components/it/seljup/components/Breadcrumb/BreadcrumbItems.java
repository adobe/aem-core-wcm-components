package com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class BreadcrumbItems {

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
