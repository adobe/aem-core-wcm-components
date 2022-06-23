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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.v1;


import com.adobe.cq.testing.selenium.pagewidgets.Helpers;
import com.adobe.cq.wcm.core.components.it.seljup.util.components.formcomponents.FormContainerEditDialog;
import com.adobe.cq.wcm.core.components.it.seljup.util.constant.Selectors;
import com.adobe.cq.wcm.core.components.it.seljup.util.Commons;

import static com.codeborne.selenide.Selenide.$;

public class FormComponents {
    public FormComponents() {}

    public FormContainerEditDialog  openEditDialog(String dataPath) {
        Commons.openEditableToolbar(dataPath);
        $(Selectors.SELECTOR_CONFIG_BUTTON).click();
        Helpers.waitForElementAnimationFinished($(Selectors.SELECTOR_CONFIG_DIALOG));
        return new FormContainerEditDialog();

    }

}
