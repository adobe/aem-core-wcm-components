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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.v2;

import com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext.BaseFormText;

import static com.codeborne.selenide.Selenide.$;

public class FormText extends BaseFormText {
    public FormText() {
        helpMessage = "//p[@class='cmp-form-text__help-block'][contains(text(),\"%s\")]";
    }

    public boolean isInputConstraintMessageSet(String elemName, String constraintMessage) {
        return $(".cmp-form-text[data-cmp-constraint-message='" + constraintMessage + "']").isDisplayed();
    }

    public boolean isTextAreaRequiredMessageSet(String elemName, String requiredMessage) {
        return $(".cmp-form-text[data-cmp-required-message='" + requiredMessage + "']").isDisplayed();
    }

}
