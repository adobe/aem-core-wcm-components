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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb.v1;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.breadcrumb.BaseBreadcrumbItems;
import static com.codeborne.selenide.Selenide.$x;

public class BreadcrumbItems extends BaseBreadcrumbItems {
    public BreadcrumbItems() {
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
