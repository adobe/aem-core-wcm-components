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

package com.adobe.cq.wcm.core.components.it.seljup.components.teaser.v2;

import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TeaserEditDialog extends com.adobe.cq.wcm.core.components.it.seljup.components.teaser.v1.TeaserEditDialog {

    private static String inheritAltFromPage = ".cmp-teaser__editor coral-checkbox[name='./altValueFromPageImage']";
    private static String isDecorative = ".cmp-teaser__editor coral-checkbox[name='./isDecorative']";
    private static String altText = ".cmp-teaser__editor input[name='./alt']";

    public void openAssetsTab() {
        $$(".cmp-teaser__editor coral-tab").get(2).click();
    }
    public void openLinksTab() {
        $$(".cmp-teaser__editor coral-tab").get(0).click();
    }

    public void checkInheritAltFromPage() {
        CoralCheckbox checkbox = new CoralCheckbox(inheritAltFromPage);
        checkbox.click();
    }

    public void checkIsDecorative() {
        CoralCheckbox checkbox = new CoralCheckbox(isDecorative);
        checkbox.click();
    }

    public void setAltText(String value) {
        $(altText).clear();
        $(altText).sendKeys(value);
    }

}
