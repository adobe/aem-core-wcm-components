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

package com.adobe.cq.wcm.core.components.it.seljup.components.Carousel.v1;

import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.ChildrenEditor;
import com.adobe.cq.wcm.core.components.it.seljup.components.Carousel.CarouselConfigureDialog;
import com.adobe.cq.wcm.core.components.it.seljup.components.Commons.CQOverlay;
import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.adobe.qe.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.qe.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.adobe.qe.selenium.pagewidgets.Helpers.waitForElementAnimationFinished;
import static com.codeborne.selenide.Selenide.*;

public class Carousel extends BaseComponent {

    private static String indicator = ".cmp-carousel__indicator";
    private static String activeIndicator = ".cmp-carousel__indicator--active";

    public Carousel() {
        super(".cmp-carousel");
    }

    public CarouselConfigureDialog getEditDialog() {
        return new CarouselConfigureDialog();
    }

    public CQOverlay getCQOverlay() {
        return new CQOverlay();
    }

    public ElementsCollection getIndicators() {
        return $$(indicator);
    }

    public ElementsCollection getActiveIndicator() {
        return $$(activeIndicator);
    }

    public void clickIndicator(int idx) {
        $$(indicator).get(idx).click();
    }

    public boolean isIndicatorActive(int idx) {
        return getIndicators().get(idx).getAttribute("class").contains("cmp-carousel__indicator--active");
    }


}
