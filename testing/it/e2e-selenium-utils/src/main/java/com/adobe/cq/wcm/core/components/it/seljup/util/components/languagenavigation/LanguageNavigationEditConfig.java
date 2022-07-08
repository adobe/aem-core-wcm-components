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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.languagenavigation;

import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.AutoCompleteField;

import static com.codeborne.selenide.Selenide.$;

public class LanguageNavigationEditConfig extends Dialog {
    public static String navigationRoot = "[name='./navigationRoot']";
    public static String structureDepth = "input[name='./structureDepth']";

    public LanguageNavigationEditConfig() {

    }

    public void setNavigationRoot(String value) {
        AutoCompleteField autoCompleteField = new AutoCompleteField("css:" + navigationRoot);
        autoCompleteField.sendKeys(value);
        autoCompleteField.suggestions().selectByValue(value);
    }

    public void setStructureDepth(String depth) {
        $(structureDepth).clear();
        $(structureDepth).sendKeys(depth);
    }

}
