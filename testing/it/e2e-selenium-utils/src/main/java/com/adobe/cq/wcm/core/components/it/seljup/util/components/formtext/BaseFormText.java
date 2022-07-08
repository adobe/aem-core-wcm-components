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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formtext;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class BaseFormText {

    protected String helpMessage;

    public FormTextEditDialog getConfigDialog() {
        return new FormTextEditDialog();
    }

    // check if the label is rendered
    public boolean isLabelRendered(String label) {
        if($("label").isDisplayed())
            return $("label").getText().trim().equals(label);

        return false;
    }

    public boolean isInputAriaLabelSet(String elemName, String label) {
        return $("input[type='text'][name='" + elemName + "'][aria-label='" + label + "']").isDisplayed();
    }

    public boolean isInputSet(String elemName) {
        return $("input[type='text'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isInputReadOnly(String elemName) {
        return $("input[type='text'][name='" + elemName + "'][readonly]").isDisplayed();
    }

    public boolean isInputRequired(String elemName) {
        return $("input[type='text'][name='" + elemName + "'][required]").isDisplayed();
    }

    public boolean isInputConstraintMessageSet(String elemName, String requiredMessage) {
        return $("input[name='" + elemName + "'][data-cmp-constraint='" + requiredMessage + "']").isDisplayed();
    }

    public boolean isDefaultValueSet(String value) {
        return $("input[type='text'][value='" + value + "']").isDisplayed();
    }

    public boolean isEmailSet(String elemName) {
        return $("input[type='email'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isTelSet(String elemName) {
        return $("input[type='tel'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isDateSet(String elemName) {
        return $("input[type='date'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isNumberSet(String elemName) {
        return $("input[type='number'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isPasswordSet(String elemName) {
        return $("input[type='password'][name='" + elemName + "']").isDisplayed();
    }

    public boolean isTextAreaAriaLabelSet(String elemName, String label) {
        return $("textarea[name='" + elemName + "'][aria-label='" + label + "']").isDisplayed();
    }

    public boolean isTextAreaSet(String elemName) {
        return $("textarea[name='" + elemName + "']").isDisplayed();
    }

    public boolean isTextAreaReadOnly(String elemName) {
        return $("textarea[name='" + elemName + "'][readonly]").isDisplayed();
    }

    public boolean isTextAreaRequired(String elemName) {
        return $("textarea[name='" + elemName + "'][required]").isDisplayed();
    }

    public boolean isTextAreaRequiredMessageSet(String elemName, String requiredMessage) {
        return $("textarea[name='" + elemName + "'][data-cmp-required='" + requiredMessage + "']").isDisplayed();
    }

    public boolean isTextAreaDefaultValueSet(String elemName, String value) {
        if($("textarea[name='" + elemName + "']").isDisplayed()) {
            return $("textarea[name='" + elemName + "']").getText().trim().equals(value);
        }

        return false;
    }

    public boolean isHelpMessageRendered(String message) {
        return $x(String.format(helpMessage,message)).isDisplayed();
    }

    public boolean isHelpRenderedAsTooltip(String elemName, String helpMessage) {
        return $("input[type='text'][name='" + elemName + "'][placeholder='" + helpMessage + "']").isDisplayed();
    }

    public boolean elementHasExpectedAriaDescribedByAttribute(SelenideElement element, String message) {
        if ($x(String.format(helpMessage, message)).isDisplayed()) {
            String helpMessageId = $x(String.format(helpMessage, message)).getAttribute("id");
            String ariaDescribedByAttribute = element.getAttribute("aria-describedby");
            return ariaDescribedByAttribute != null && ariaDescribedByAttribute.equals(helpMessageId);
        }
        return false;
    }

    public boolean elementHasNoAriaDescribedByAttribute(SelenideElement element) {
        return element.getAttribute("aria-describedby") == null;
    }
}
