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

package com.adobe.cq.wcm.core.components.it.seljup.components.button.v1;

import com.adobe.qe.selenium.pagewidgets.coral.Dialog;
import com.codeborne.selenide.SelenideElement;

public class ButtonConfigureDialog extends Dialog {
    private static final String CSS_SELECTOR = "coral-dialog";

    public ButtonConfigureDialog() {
        super(CSS_SELECTOR);
    }

    public ButtonConfigureDialog(SelenideElement element) {
        super(element);
    }

    public SelenideElement getTitleField() {
        return content().find("input[name='./jcr:title']");
    }

    public SelenideElement getLinkField() {
        return content().find("[name='./link']");
    }

    public SelenideElement getIcon() {
        return content().find("[name='./icon']");
    }

}
