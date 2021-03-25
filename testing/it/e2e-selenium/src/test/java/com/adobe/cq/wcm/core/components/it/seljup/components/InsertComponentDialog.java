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

package com.adobe.cq.wcm.core.components.it.seljup.components;

import com.adobe.qe.selenium.pagewidgets.common.BaseComponent;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class InsertComponentDialog extends BaseComponent {
    public InsertComponentDialog() {
        super(".InsertComponentDialog");
    }

    public static final class Components {

        private static SelenideElement responsiveGrid = $("coral-selectlist-item[value='/libs/wcm/foundation/components/responsivegrid']");
        private static SelenideElement accordion = $("coral-selectlist-item[value='/apps/core-component/components/accordion']");
        private static SelenideElement tab = $("coral-selectlist-item[value='/apps/core-component/components/tabs']");

        private Components() {
        }

        public SelenideElement getAccordion() {
            return accordion;
        }

        public SelenideElement getResponsiveGrid() {
            return responsiveGrid;
        }

        public SelenideElement getTab() {
            return tab;
        }
    }
}
