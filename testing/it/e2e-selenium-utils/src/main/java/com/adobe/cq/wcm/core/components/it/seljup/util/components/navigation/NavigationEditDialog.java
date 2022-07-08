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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.navigation;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;

import static com.codeborne.selenide.Selenide.$;

public class NavigationEditDialog {

    private String collectAllPages = "[name='./collectAllPages']";
    private String structureDepth = "coral-numberinput[name='./structureDepth']";
    private String navigationRoot = "[name='./navigationRoot']";
    private String structureStart = "input[name='./structureStart']";

    public void clickCollectAllPages() {
        CoralCheckbox checkbox = new CoralCheckbox(collectAllPages);
        checkbox.click();
    }

    public boolean isCollectAllPagesChecked() {
        CoralCheckbox checkbox = new CoralCheckbox(collectAllPages);
        return checkbox.isChecked();
    }

    public boolean isStructureDepthVisible() {
        return $(structureDepth).isDisplayed();
    }

    public void setNavigationRoot(String path) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + navigationRoot);
        autoCompleteField.sendKeys(path);
        autoCompleteField.suggestions().selectByValue(path);
    }

    public void setStructureStart(String value) {
        $(structureStart).clear();
        $(structureStart).sendKeys(value);
    }
}
