/*******************************************************************************
 * Copyright 2016 Adobe
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
(function($, channel, Coral) {
    "use strict";

    var EDIT_DIALOG = ".cmp-form-textfield-editDialog";
    var TEXTFIELD_TYPES = ".cmp-form-textfield-types";
    var TEXTFIELD_ROWS = ".cmp-form-textfield-rows";
    var TEXTFIELD_REQUIRED = ".cmp-form-textfield-required";
    var TEXTFIELD_CONSTRAINTMESSAGE = ".cmp-form-textfield-constraintmessage";
    var TEXTFIELD_REQUIREDMESSAGE = ".cmp-form-textfield-requiredmessage";
    var TEXTFIELD_READONLY = ".cmp-form-textfield-readonly";
    var TEXTFIELD_READONLYSELECTED_ALERT = ".cmp-form-textfield-readonlyselected-alert";
    var TEXTFIELD_REQUIREDSELECTED_ALERT = ".cmp-form-textfield-requiredselected-alert";

    /**
     * Toggles the display of the given element based on the actual and the expected values.
     * If the actualValue is equal to the expectedValue, then the element is shown,
     * otherwise the element is hidden.
     *
     * @param {HTMLElement} element The html element to show/hide.
     * @param {*} expectedValue The value to test against.
     * @param {*} actualValue The value to test.
     */
    function checkAndDisplay(element, expectedValue, actualValue) {
        if (expectedValue === actualValue) {
            element.show();
        } else {
            element.hide();
        }
    }

    /**
     * Toggles the visibility of the Text field number of rows input field based on the type of the text field.
     * If the type is textarea, the number of rows field is shown, otherwise it is hidden.
     *
     * @param {HTMLElement} dialog The dialog on which the operation is to be performed.
     */
    function handleTextarea(dialog) {
        var component = dialog.find(TEXTFIELD_TYPES)[0];
        var textfieldRows = dialog.find(TEXTFIELD_ROWS);
        checkAndDisplay(textfieldRows,
            "textarea",
            component.value);
        component.on("change", function() {
            checkAndDisplay(textfieldRows,
                "textarea",
                component.value);
        });
    }

    /**
     * Toggles the visibility of the constraint message input field based on the type of the text field
     * If the type of the text field is "text" or "textarea", the constraint message field is hidden,
     * otherwise it is shown.
     *
     * @param {HTMLElement} dialog The dialog on which the operation is to be performed.
     */
    function handleConstraintMessage(dialog) {
        var component = dialog.find(TEXTFIELD_TYPES)[0];
        var constraintMessage = dialog.find(TEXTFIELD_CONSTRAINTMESSAGE);
        var displayConstraintMessage = component.value !== "text" && component.value !== "textarea";
        checkAndDisplay(constraintMessage,
            true,
            displayConstraintMessage);
        component.on("change", function() {
            displayConstraintMessage = this.value !== "text" && this.value !== "textarea";
            checkAndDisplay(constraintMessage,
                true,
                displayConstraintMessage);
        });
    }

    /**
     * Toggles the visibility of the required message input field based on the "required" input field.
     * If the "required" field is set, the required message field is shown,
     * otherwise it is hidden.
     *
     * @param {HTMLElement} dialog The dialog on which the operation is to be performed.
     */
    function handleRequiredMessage(dialog) {
        var component = dialog.find(TEXTFIELD_REQUIRED)[0];
        var requiredMessage = dialog.find(TEXTFIELD_REQUIREDMESSAGE);
        checkAndDisplay(requiredMessage,
            true,
            component.checked);
        component.on("change", function() {
            checkAndDisplay(requiredMessage,
                true,
                component.checked);
        });
    }

    /**
     * Handles the exclusion between the two checkbox components.
     * Specifically, out of the two components, only one can be in checked state at a time.
     * If component1 is "checked" and the component2 is also in checked state, the component2 is unchecked,
     * and the alert is displayed.
     *
     * @param {HTMLElement} component1 The component which on being "checked" should uncheck(if in checked state) the component2.
     * @param {HTMLElement} component2 The component which should not be in checked state along with component1.
     * @param {HTMLElement} alert The alert to show if both the component2 is in checked state when the component1 is being "checked".
     */
    function handleExclusion(component1, component2, alert) {
        component1.on("change", function() {
            if (this.checked && component2.checked) {
                alert.show();
                component2.set("checked", false, true);
            }
        });
    }

    /**
     * Initialise the conditional display of the various elements of the dialog.
     *
     * @param {HTMLElement} dialog The dialog on which the operation is to be performed.
     */
    function initialise(dialog) {
        dialog = $(dialog);
        handleTextarea(dialog);
        handleConstraintMessage(dialog);
        handleRequiredMessage(dialog);

        var readonly = dialog.find(TEXTFIELD_READONLY)[0];
        var required = dialog.find(TEXTFIELD_REQUIRED)[0];
        handleExclusion(readonly,
            required,
            dialog.find(TEXTFIELD_REQUIREDSELECTED_ALERT)[0]);
        handleExclusion(required,
            readonly,
            dialog.find(TEXTFIELD_READONLYSELECTED_ALERT)[0]);
    }

    channel.on("foundation-contentloaded", function(e) {
        if ($(e.target).find(EDIT_DIALOG).length > 0) {
            Coral.commons.ready(e.target, function(component) {
                initialise(component);
            });
        }
    });

})(jQuery, jQuery(document), Coral);
