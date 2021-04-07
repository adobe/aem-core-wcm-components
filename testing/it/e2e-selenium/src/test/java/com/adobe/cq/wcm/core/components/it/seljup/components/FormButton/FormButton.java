/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

package com.adobe.cq.wcm.core.components.it.seljup.components.FormButton;

import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;

import static com.codeborne.selenide.Selenide.$;

public class FormButton extends BaseComponent {
    private String button;

    public FormButton(String button) {
        super(button);
        this.button = button;
    }

    public boolean isButtonPresentByType(String type) {
        return $(button +"[type='" + type + "']").isDisplayed();
    }

    public String getButtonText() {
        return $(button).getText();
    }

    public boolean isButtonPresentByName(String name) {
        return $(button +"[name='" + name + "']").isDisplayed();
    }

    public boolean isButtonPresentByValue(String value) {
        return $(button +"[value='" + value + "']").isDisplayed();
    }
}
