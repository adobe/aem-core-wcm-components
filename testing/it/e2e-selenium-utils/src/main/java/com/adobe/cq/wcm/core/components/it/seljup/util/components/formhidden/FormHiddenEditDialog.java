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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formhidden;

import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;

import static com.codeborne.selenide.Selenide.$;

public class FormHiddenEditDialog extends Dialog {
    public void setMandatoryFields(String value) {
        $("[name='./name']").sendKeys(value);
    }

    public boolean isMandatoryFieldsInvalid() {
        return $("[name='./name']").getAttribute("invalid").equals("true");
    }

    public void setValue(String value) {
        $("[name='./value']").sendKeys(value);
    }

    public void setId(String value) {
        $("[name='./id']").sendKeys(value);
    }
}
