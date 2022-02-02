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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.button.v1;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class Button extends BaseComponent {


    public Button() {
        super(".cmp-button");
    }

    public Button (SelenideElement element) {
        super(element);
    }

    public String getTitle() {
        SelenideElement buttonText = element().find(".cmp-button__text").shouldBe(Condition.visible);
        return buttonText.getText().trim();
    }

    public boolean checkLinkPresent(String link) {
        return $("a[href='" + link + "']").isDisplayed();
    }

    public boolean checkLinkPresentWithTarget(String link, String target) {
        return $("a[href='" + link + "'][target='" + target + "']").isDisplayed();
    }

    public boolean iconPresent(String icon) {
        return $(".cmp-button__icon--" + icon).exists();
    }
}
