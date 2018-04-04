/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
(function($, Granite, ns, $document) {
    "use strict";

    var OPTION_SELECTED_SELECTOR       = "./selected";
    var CHECKBOX_SELECTOR              = "coral-checkbox";
    var RADIO_SELECTOR                 = "coral-radio";
    var OPTION_TYPE_ELEMENT_SELECTOR   = ".cmp-form-options__editor-type";
    var GRANITE_UI_FOUNDATION_FIELD    = "foundation-field";

    /**
     * Toggles checkboxes <-> radio buttons of the dialog depending on the value of the "./multiSelection" input field:
     * - if multiSelection is checked, checkboxes are displayed
     * - otherwise, radio buttons are displayed
     *
     * The transformation only applies to checkboxes / radio buttons named "./selected".
     *
     * @param {jQuery} $dialog The options editor dialog.
     * @param {HTMLInputElement} component The options type select input.
     */
    function toggleRadioCheckbox($dialog, component) {

        var value = component.value;
        var isMultiSelection = (!(value === "drop-down" || value === "radio"));

        // toggle the 'selected' input, which is either a checkbox or a radio button
        $dialog.find("input[name='" + OPTION_SELECTED_SELECTOR + "']").each(function() {
            var $input      = $(this);
            var $checkbox   = $input.closest(CHECKBOX_SELECTOR);
            var checkboxAPI = $checkbox.adaptTo(GRANITE_UI_FOUNDATION_FIELD);
            var $radio      = $input.closest(RADIO_SELECTOR);
            var radioAPI    = $radio.adaptTo(GRANITE_UI_FOUNDATION_FIELD);

            // if multiple selection of options is possible, display the checkboxes and hide/disable the radio buttons
            if (isMultiSelection) {
                $checkbox.show();
                $radio.hide();
                // enable the checkbox fields
                if (checkboxAPI) {
                    checkboxAPI.setDisabled(false);
                }
                // disable the radio button fields
                if (radioAPI) {
                    radioAPI.setDisabled(true);
                }
                // if multiple selection of options is possible, hide/disable the checkboxes and display the radio buttons
            } else {
                $checkbox.hide();
                $radio.show();
                // disable the checkbox fields
                if (checkboxAPI) {
                    checkboxAPI.setDisabled(true);
                }
                // enable the radio button fields
                if (radioAPI) {
                    radioAPI.setDisabled(false);
                }
            }
        });
    }

    $document.on("foundation-contentloaded", function(e) {
        var $dialog = $(e.target);
        if ($dialog.find(OPTION_TYPE_ELEMENT_SELECTOR).length > 0) {
            $(OPTION_TYPE_ELEMENT_SELECTOR, e.target).each(function(i, element) {
                Coral.commons.ready(element, function(component) {
                    toggleRadioCheckbox($dialog, component);
                    component.on("change", function() {
                        toggleRadioCheckbox($dialog, component);
                    });
                    $document.on("foundation-field-change", function(e) {
                        toggleRadioCheckbox($dialog, component);
                    });
                });
            });
        }
    });


}(jQuery, Granite, Granite.author, jQuery(document)));
