/*******************************************************************************
 * Copyright 2021 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function(window, document, $) {
    "use strict";

    /**
     * Handler to enable/disable the brand slug textfield in the page properties dialog.
     */
    var brandSlugCheckboxSelector = 'coral-checkbox[name="./brandSlug_override"]';
    var brandSlugTextfieldSelector = 'input[name="./brandSlug"]';
    var brandSlugSectionSelector = "section.cq-siteadmin-admin-properties-basic-brandSlug";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $brandSlugSection = $(brandSlugSectionSelector, $dialog);
        var $brandSlugCheckbox = $(brandSlugCheckboxSelector, $brandSlugSection);
        var $brandSlugTextfield = $(brandSlugTextfieldSelector, $brandSlugSection);

        if ($brandSlugCheckbox.length > 0 && $brandSlugTextfield.length > 0) {
            var inheritedValue = $brandSlugTextfield.data("inheritedValue");
            var specifiedValue = $brandSlugTextfield.data("specifiedValue");
            var textfieldFoundation = $brandSlugTextfield.adaptTo("foundation-field");
            var checkboxFoundation  = $brandSlugCheckbox.adaptTo("foundation-field");
            changeTextFieldState(textfieldFoundation, checkboxFoundation.getValue() === "true", inheritedValue, specifiedValue);
            $brandSlugCheckbox.on("change", function() {
                changeTextFieldState(textfieldFoundation, this.checked, inheritedValue, specifiedValue);
            });
        }
    });

    function changeTextFieldState(textfield, enabled, inheritedValue, specifiedValue) {
        if (enabled) {
            textfield.setDisabled(false);
            textfield.setValue(specifiedValue || "");
        } else {
            textfield.setDisabled(true);
            textfield.setValue(inheritedValue || "");
        }
    }

})(window, document, Granite.$);
