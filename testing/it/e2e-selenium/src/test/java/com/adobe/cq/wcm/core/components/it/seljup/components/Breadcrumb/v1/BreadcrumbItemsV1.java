package com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.v1;

import com.adobe.cq.wcm.core.components.it.seljup.components.Breadcrumb.BreadcrumbItems;

import static com.codeborne.selenide.Selenide.$x;

public class BreadcrumbItemsV1 extends BreadcrumbItems {
    public BreadcrumbItemsV1() {
        activeItem = "//li[@class='breadcrumb-item active'][contains(text(),\"%s\")]";
        items = "//li[contains(@class, 'breadcrumb-item')]";
        activeItems = "//li[contains(@class, 'breadcrumb-item active')]";
    }

    /**
     * Check if breadcrumb item is present
     * @param containsString
     * @return true if item with containsString is present otherwise false
     */
    public boolean isItemPresent(String containsString) {
        return $x(items + "/a[contains(text(),'" + containsString + "')]").exists();
    }
}
