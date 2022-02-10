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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.carousel;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.commons.ChildrenEditor;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.adobe.cq.testing.selenium.pagewidgets.coral.Dialog;
import com.adobe.cq.testing.selenium.pagewidgets.cq.InsertComponentDialog;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CarouselEditDialog extends Dialog {

    private static String autoplay = "[data-cmp-carousel-v1-dialog-hook='autoplay']";
    private static String autoplayGroup = "[data-cmp-carousel-v1-dialog-hook='autoplayGroup']";
    private static String delay = "[data-cmp-carousel-v1-dialog-hook='delay']";
    private static String autopauseDisabled = "[data-cmp-carousel-v1-dialog-hook='autopauseDisabled']";

    public CarouselEditDialog() {

    }

    public void openEditDialogProperties() {
        $$(".cmp-carousel__editor coral-tab").get(1).click();
    }

    public ChildrenEditor getChildrenEditor() { return new ChildrenEditor(); }
    public InsertComponentDialog getInsertComponentDialog() {
        return new InsertComponentDialog();
    }

    public CoralCheckbox getAutoplay() {
        return new CoralCheckbox(autoplay);
    }

    public SelenideElement getAutoplayGroup() {
        return $(autoplayGroup);
    }

    public SelenideElement getDelay() {
        return $(delay);
    }

    public SelenideElement getAutopauseDisabled() {
        return $(autopauseDisabled);
    }


}
