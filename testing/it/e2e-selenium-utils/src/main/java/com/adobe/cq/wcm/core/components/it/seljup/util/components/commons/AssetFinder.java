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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.commons;

import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;

public class AssetFinder {
    private static String filtersPath = "[name='assetfilter_image_path']";
    private static String textField = "foundation-autocomplete[name='assetfilter_image_path'] [is='coral-textfield']";
    private static String buttonListItem = "foundation-autocomplete[name='assetfilter_image_path'] [is='coral-buttonlist-item']";

    public void setFiltersPath(String filter) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + filtersPath);
        autoCompleteField.sendKeys(filter);
        autoCompleteField.suggestions().selectByValue(filter);
    }

}
