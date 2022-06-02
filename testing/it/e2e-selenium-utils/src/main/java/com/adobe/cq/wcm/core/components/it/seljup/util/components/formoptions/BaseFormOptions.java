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

package com.adobe.cq.wcm.core.components.it.seljup.util.components.formoptions;

import com.adobe.cq.testing.selenium.pagewidgets.common.BaseComponent;
import com.adobe.cq.testing.selenium.pagewidgets.coral.CoralCheckbox;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class BaseFormOptions extends BaseComponent {
    protected String help;
    protected String description;
    protected String checkbox;
    protected String radio;
    protected String dropDown;
    protected String multiDropDown;

    public BaseFormOptions() {
        super("");
    }

    public FormOptionsEditDialog geteditDialog() {
        return new FormOptionsEditDialog();
    }

    // check if the title is rendered
    public boolean isTitleRendered(String text) {
        return $("legend").getText().trim().equals(text);
    }

    // check if input name is set correctly
    public boolean isNameSet(String elemName) {
        return $("input[name='" + elemName + "']").isDisplayed();
    }

    // check if the help message is set correctly
    public boolean isHelpMessageSet(String helpMessage) {
        return $(help).getText().trim().equals(helpMessage);
    }

    // check if the option type is set to checkbox
    public boolean isCheckboxTypeSet() {
        return $(checkbox).isDisplayed();
    }

    // check if the option type is set to radio button
    public boolean isRadioButtonTypeSet() {
        return $(radio).isDisplayed();
    }

    // check if the option type is set to drop-down
    public boolean isDropDownTypeSet() {
        return $(dropDown).isDisplayed();
    }

    // check if the option type is set to multi-select drop-down
    public boolean isMultiSelectDropDownTypeSet() {
        return $(multiDropDown).isDisplayed();
    }

    // check that the description is correctly set
    public boolean isDescriptionSet(String descriptionText) {
        return $(description).getText().trim().equals(descriptionText);
    }

    public boolean isCheckboxChecked(String value) {
        CoralCheckbox checkbox = new CoralCheckbox("[value='" + value + "']");
        return checkbox.isChecked();
    }

    public boolean isRadioButtonSelected(String value) {
        return $("input[type='radio'][value='" + value + "'][checked]").isDisplayed();
    }

    public boolean isDropDownSelected(String value) {
        return $("option[value='" + value + "'][selected]").isDisplayed();
    }

    public boolean isCheckboxDisabled(String value) {
        return $("input[type='checkbox'][value=" + value + "][disabled]").isDisplayed();
    }

    public boolean isRadioButtonDisabled(String value) {
        return $("input[type='radio'][value='" + value + "'][disabled]").isDisplayed();
    }

    public boolean isDropDownDisabled(String value) {
        return $("option[value='" + value + "'][disabled]").isDisplayed();
    }

    public boolean isMultiSelectDropDownDisabled(String value) {
        return $("option[value='" + value + "'][disabled]").isDisplayed();
    }

    public boolean elementHasExpectedAriaDescribedByAttribute(SelenideElement element) {
        if ($(help).isDisplayed()) {
            String helpMessageId = $(help).getAttribute("id");
            String ariaDescribedByAttribute = element.getAttribute("aria-describedby");
            return ariaDescribedByAttribute != null && ariaDescribedByAttribute.equals(helpMessageId);
        }
        return false;
    }

    public boolean elementHasNoAriaDescribedByAttribute(SelenideElement element) {
        return element.getAttribute("aria-describedby") == null;
    }
}
