package com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v2;

import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbItems;

import static com.codeborne.selenide.Selenide.$x;

public class BreadcrumbItemsV2 extends BreadcrumbItems {
    public BreadcrumbItemsV2() {
        activeItem = "//li[contains(@class,'cmp-breadcrumb__item--active')]/span[contains(text(),\"%s\")]";
        items = "//li[contains(@class, 'cmp-breadcrumb__item')]";
        activeItems = "//li[contains(@class, 'cmp-breadcrumb__item--active')]";
    }

    /**
     * Check if breadcrumb item is present
     * @param containsString
     * @return true if item with containsString is present otherwise false
     */
    public boolean isItemPresent(String containsString) {
        return $x(items + "/a/span[contains(text(),'" + containsString + "')]").exists();
    }
}
