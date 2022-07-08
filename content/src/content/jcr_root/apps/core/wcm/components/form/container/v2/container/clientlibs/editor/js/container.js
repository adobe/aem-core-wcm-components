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
(function(document, $, Coral) {
    "use strict";

    var ACTION_TYPE_SETTINGS_SELECTOR = "#cmp-action-type-settings";
    var ACTION_TYPE_ELEMENT_SELECTOR  = ".cmp-action-type-selection";
    var WORKFLOW_SELECT_ELEMENT_SELECTOR = ".cmp-workflow-container coral-select";
    var EMAIL_ADDRESS_SENDER_SELECTOR = "input[name='./from']";
    var EMAIL_ADDRESS_RECEIVER_SELECTOR = "coral-multifield-item-content input[name='./mailto']";
    var EMAIL_ADDRESS_CC_SELECTOR = "coral-multifield-item-content input[name='./cc']";

    $(document).on("foundation-contentloaded", function(e) {
        if ($(e.target).find(ACTION_TYPE_ELEMENT_SELECTOR).length > 0) {
            $(ACTION_TYPE_ELEMENT_SELECTOR, e.target).each(function(i, element) {
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
        if ($(e.target).find(WORKFLOW_SELECT_ELEMENT_SELECTOR).length > 0) {
            $(WORKFLOW_SELECT_ELEMENT_SELECTOR, e.target).each(function(i, element) {
                var target = $(element).data("cqDialogDropdownShowhideTarget");
                if (target) {
                    Coral.commons.ready(element, function(component) {
                        component.on("change", function() {
                            showHideWorkflowTitle(component, target);
                        });
                    });
                }
            });
        }
    });

    function showHideWorkflowTitle(component, target) {
        var value = component.value;
        var $target = $(target);

        setVisibilityAndHandleFieldValidation($target, true);
        $target.find("[data-reverseshowhidetargetvalue='" + value + "']").each(function(index, element) {
            var $element = $(element);
            setVisibilityAndHandleFieldValidation($element.closest(target), false);
        });
    }

    function showHide(component, target) {
        var value              = component.value;
        var $target            = $(target);
        var $workflowContainer = $(".cmp-workflow-container");
        var $redirectSelection = $(".cmp-redirect-selection");

        setVisibilityAndHandleFieldValidation($target.not(".hide"), false);
        setVisibilityAndHandleFieldValidation($workflowContainer, false);
        setVisibilityAndHandleFieldValidation($redirectSelection, false);

        $target.closest(ACTION_TYPE_SETTINGS_SELECTOR).addClass("hide");

        $(target).filter("[data-showhidetargetvalue='" + value + "']").each(function(index, element) {
            var $element = $(element);
            setVisibilityAndHandleFieldValidation($element, true);

            showHideOptional($element, $workflowContainer, "usesworkflow");
            showHideOptional($element, $redirectSelection, "usesredirect");
            $element.closest(ACTION_TYPE_SETTINGS_SELECTOR).removeClass("hide");
        });
    }

    function showHideOptional($element, $optional, data) {
        var showOptional = $element.data(data);
        var target;
        var $workflowSelect;

        if (showOptional) {
            if (data === "usesworkflow") {
                $workflowSelect = $optional.find("coral-select");
                target = $workflowSelect.data("cqDialogDropdownShowhideTarget");
                showHideWorkflowTitle($workflowSelect[0], target);
            }
            setVisibilityAndHandleFieldValidation($optional, true);
        }
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
            $element.find("input[aria-required=false], coral-multifield[aria-required=false]").
                filter(":not(.hide>input)").filter(":not(input.hide)").
                filter(":not(.hide>coral-multifield)").filter(":not(input.coral-multifield)").each(function(index, field) {
                    toggleValidation($(field));
                });
        } else {
            $element.addClass("hide");
            $element.find("input[aria-required=true], coral-multifield[aria-required=true]").each(function(index, field) {
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
        var api = $field.adaptTo("foundation-validation");
        if (api) {
            if (notRequired) {
                api.checkValidity();
            }
            api.updateUI();
        }
    }

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: EMAIL_ADDRESS_SENDER_SELECTOR,
        validate: function(element) {
            return validateEmail(element);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: EMAIL_ADDRESS_RECEIVER_SELECTOR,
        validate: function(element) {
            return validateEmail(element, true);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: EMAIL_ADDRESS_CC_SELECTOR,
        validate: function(element) {
            return validateEmail(element, true);
        }
    });

    function validateEmail(element, addEmptyFieldValidation) {
        var emptyFieldErrorMessage = Granite.I18n.get("Error: Please fill out this field.");
        var invalidEmailErrorMessage = Granite.I18n.get("Error: Invalid Email Address.");
        var validEmailRegex = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/;
        var input =  element.value;
        if (input && !input.match(validEmailRegex)) {
            return invalidEmailErrorMessage;
        }
        if (addEmptyFieldValidation && !input) {
            return emptyFieldErrorMessage;
        }
    }

})(document, Granite.$, Coral);
