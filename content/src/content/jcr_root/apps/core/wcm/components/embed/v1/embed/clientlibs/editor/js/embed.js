/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
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
/* global
 Granite, Coral
 */
(function(document, $, Coral) {
    "use strict";

    var EMBEDDABLE_TYPE_SETTINGS_SELECTOR = ".cmp-embeddable-settings";
    var EMBEDDABLE_TYPE_ELEMENT_SELECTOR  = ".cmp-embed-type-selection";
    var EMBED_TYPE_RADIO_SELECTOR = ".cmp-embed-option-selection coral-radio";

    $(document).on("foundation-contentloaded", function(e) {
        if ($(e.target).find(EMBEDDABLE_TYPE_ELEMENT_SELECTOR).length > 0) {
            $(EMBEDDABLE_TYPE_ELEMENT_SELECTOR, e.target).each(function(i, element) {
                var target = $(element).data("cqDialogDropdownShowhideTarget");
                if (target) {
                    Coral.commons.ready(element, function(component) {
                        showHide(component, target);
                        component.on("change", function() {
                            showHide(component, target);
                        });
                    });
                }
            });
            showHide($(".cq-dialog-dropdown-showhide", e.target));
        }
        if ($(e.target).find(EMBED_TYPE_RADIO_SELECTOR).length > 0) {
            $(EMBED_TYPE_RADIO_SELECTOR, e.target).each(function(i, element) {
                var target = $(element).parent().data("cqDialogRadioShowhideTarget");
                if (target) {
                    setVisibilityAndHandleFieldValidation($(target).parent().not(".hide"), false);
                    setVisibilityAndHandleFieldValidation($(EMBEDDABLE_TYPE_SETTINGS_SELECTOR), false);
                    Coral.commons.ready(element, function(component) {
                        if ($(component).attr("checked") === "checked") {
                            showHideEmbedOptions(component, target);
                        }
                        $(component).on("change", function(h) {
                            showHideEmbedOptions(this, target);
                        });
                    });
                }
            });
        }

    });

    function showHide(component, target) {
        var value = component.value;
        var $target = $(target);
        setVisibilityAndHandleFieldValidation($target.not(".hide"), false);
        $target.closest(EMBEDDABLE_TYPE_SETTINGS_SELECTOR).addClass("hide");
        $(target).filter("[data-showhidetargetvalue='" + value + "']").each(function(index, element) {
            var $element = $(element);
            setVisibilityAndHandleFieldValidation($element, true);
            $element.closest(EMBEDDABLE_TYPE_SETTINGS_SELECTOR).removeClass("hide");
        });
    }

    function showHideEmbedOptions(component, target) {
        var value = $(component).val();
        var $target = $(target);
        setVisibilityAndHandleFieldValidation($target.parent().not(".hide"), false);
        setVisibilityAndHandleFieldValidation($(EMBEDDABLE_TYPE_SETTINGS_SELECTOR), false);
        $(target).filter("[data-showhidetargetvalue='" + value + "']").each(function(index, element) {
            var $element = $(element);
            if ($element.hasClass(EMBEDDABLE_TYPE_ELEMENT_SELECTOR.substring(1))) {
                var target = $(element).data("cqDialogDropdownShowhideTarget");
                // Handle Dialog dropdown too
                showHide(element, target);
            }
            setVisibilityAndHandleFieldValidation($element.parent(), true);
        });
    }

    /**
     * Shows or hides an element based on parameter "show" and toggles validations if needed. If element
     * is being shown, all VISIBLE fields inside it whose validation is false would be changed to set the validation
     * to true. If element is being hidden, all fields inside it whose validation is true would be changed to
     * set validation to false.
     *
     * @param {jQuery} $element Element to show or hide.
     * @param {Boolean} show <code>true</code> to show the element.
     */
    function setVisibilityAndHandleFieldValidation($element, show) {
        if (show) {
            $element.removeClass("hide");
            $element.find("textarea[aria-required=false], input[aria-required=false], coral-multifield[aria-required=false], coral-select[aria-required=false]").
                filter(":not(.hide>input)").filter(":not(input.hide)").
                filter(":not(.hide>coral-multifield)").filter(":not(input.coral-multifield)").each(function(index, field) {
                    toggleValidation($(field));
                });
        } else {
            $element.addClass("hide");
            $element.find("textarea[aria-required=true], input[aria-required=true], coral-multifield[aria-required=true], coral-select[aria-required=true]").each(function(index, field) {
                toggleValidation($(field));
            });
        }
    }

    /**
     * If the form element is not shown we have to disable the required validation for that field.
     *
     * @param {jQuery} $field To disable / enable required validation.
     */
    function toggleValidation($field) {
        var notRequired = false;
        if ($field.attr("aria-required") === "true") {
            notRequired = true;
            $field.attr("aria-required", "false");
        } else if ($field.attr("aria-required") === "false") {
            $field.attr("aria-required", "true");
        }
        if ($field.hasClass(EMBEDDABLE_TYPE_ELEMENT_SELECTOR.substring(1))) {
            if ($field.attr("required") === "required") {
                $field.removeAttr("required");
            } else {
                $field.attr("required", "required");
            }
        }
        var api = $field.adaptTo("foundation-validation");
        if (api) {
            if (notRequired) {
                api.checkValidity();
            }
            api.updateUI();
        }
    }

})(document, Granite.$, Coral);
