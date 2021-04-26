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

package com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions;

import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralSelectList;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FormOptionsConfigDialog extends Dialog {

    /**
     * Set the mandatory fields
     */
    public void setMandatoryFields(String value, String title) throws InterruptedException {
        Commons.webDriverWait(1000);
        $("[name='./name']").sendKeys(value);
        SelenideElement el = Commons.getVisibleElement($$("[name='./jcr:title']"));
        if(el != null)
            el.sendKeys(title);
    }

    /**
     * Add an option
     */
    public void addOption(String value, String text) {
        $("button[coral-multifield-add='']").click();
        $("input[name$='./value']").sendKeys(value);
        $("input[name$='./text']").sendKeys(text);
    }

    /**
     * Set the option type
     */
    public void setOptionType(String optionType) {
        $("coral-select[name='./type'] button").click();
        CoralSelectList selectList = new CoralSelectList("name='./type");
        selectList.selectByValue(optionType);
    }

    public boolean isMandatoryFieldsInvalid() {
        return isNameFieldsInvalid() && isTitleFieldsInvalid();
    }

    public boolean isNameFieldsInvalid() {
        return $("[name='./name']").getAttribute("invalid").equals("true");
    }

    public boolean isTitleFieldsInvalid() {
         return $$("[name='./jcr:title'][invalid='']").size() == 1;
    }
}
