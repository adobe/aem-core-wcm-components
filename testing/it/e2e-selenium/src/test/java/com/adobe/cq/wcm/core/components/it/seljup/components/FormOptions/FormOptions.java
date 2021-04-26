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

package com.adobe.cq.wcm.core.components.it.seljup.components.FormOptions;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;

import static com.codeborne.selenide.Selenide.$;

public class FormOptions extends BaseComponent {
    public FormOptions() {
        super("");
    }

    public FormOptionsConfigDialog getConfigDialog() {
        return new FormOptionsConfigDialog();
    }

    // check if the title is rendered
    public boolean isTitleRendered(String text) {
        return $("legend").getText().trim().equals(text);
    }
}
