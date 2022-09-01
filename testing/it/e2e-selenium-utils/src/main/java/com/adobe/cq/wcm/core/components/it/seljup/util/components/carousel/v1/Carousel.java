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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.carousel.v1;

import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.carousel.CarouselEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.CQOverlay;
import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;
import com.codeborne.selenide.ElementsCollection;


import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Carousel extends BaseComponent {

    private static String indicator = ".cmp-carousel__indicator";
    private static String activeIndicator = ".cmp-carousel__indicator--active";

    public Carousel() {
        super(".cmp-carousel");
    }

    public CarouselEditDialog openEditDialog(String dataPath) {
        Commons.openEditableToolbar(dataPath);
        $(Selectors.SELECTOR_CONFIG_BUTTON).click();
        Helpers.waitForElementAnimationFinished($(Selectors.SELECTOR_CONFIG_DIALOG));
        return new CarouselEditDialog();
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
