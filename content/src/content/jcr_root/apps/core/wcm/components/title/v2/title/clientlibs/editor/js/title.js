/*******************************************************************************
 * Copyright 2017 Adobe
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
/**
 * Design dialog:
 * - The options of the select field to define the default value are added/removed based on the status
 * of the size checkboxes
 * - Validation: if no size checkboxes are checked, the dialog cannot be saved
 *
 * Edit dialog:
 * - displays all the sizes if no sizes have been defined in the policy
 * - hides all the sizes if only one size has been defined in the policy
 * - displays all the sizes defined in the policy if there are at least two
 */
(function($, Granite, ns, $document) {
    "use strict";

    var DEFAULT_SIZE_SELECTOR       = "coral-select.core-title-size-default";
    var DEFAULT_SIZES_SELECTOR      = "coral-select.core-title-sizes-default";
    var ALLOWED_SIZES_SELECTOR      = ".core-title-sizes-allowed coral-checkbox";
    var DATA_ATTR_VALIDATION_STATE  = "checkboxes.validation.state";
    var SIZES_SELECTOR              = "coral-select.core-title-sizes";
    var LINK_URL_SELECTOR           = ".cmp-title-link-url";
    var LINK_LABEL_SELECTOR         = ".cmp-title-link-label";
    var LINK_TITLE_SELECTOR         = ".cmp-title-link-title";

    // Update the select field that defines the default value
    function updateDefaultSizeSelect(checkboxToggled) {

        var select = $(DEFAULT_SIZE_SELECTOR).get(0);
        var $checkboxes = $(ALLOWED_SIZES_SELECTOR);
        var checkedTotal = 0;
        var selectValue = "";

        if (select === null || select === undefined) {
            return;
        }

        // clear the select items to work around a Coral.Select issue (CUI-5584)
        select.items.clear();

        // for each checked checkbox, add an option to the default sizes dropdown
        $checkboxes.each(function(i, checkbox) {
            if (checkbox.checked) {
                var newItem = new Coral.Select.Item();
                newItem.content.textContent = checkbox.label.innerHTML;
                newItem.value = checkbox.value;
                select.items.add(newItem);
                checkedTotal++;
            }
        });

        // set the default value of the size dropdown
        if (checkboxToggled) {
            selectValue = getAppropriateCheckedBoxValue($checkboxes, select.value);
        } else {
            // the default value is read from the repository
            selectValue = select.value;
        }

        // hide/show the select
        // Note: we use Coral.commons.nextFrame to make sure that the select widget has been updated
        Coral.commons.nextFrame(function() {
            select.value = selectValue;
            if (checkedTotal === 0 || checkedTotal === 1) {
                $(select).parent().hide();
            } else {
                $(select).parent().show();
            }
        });
    }

    // get the appropriate checked box value by checking if the current value of the default type is a valid option in the list of allowed types/sizes
    function getAppropriateCheckedBoxValue(checkboxes, currentDefaultTypeValue) {
        var isCurrentDefaultTypeValueValidOption = false;
        checkboxes.each(function(i, checkbox) {
            if (checkbox.checked && checkbox.value === currentDefaultTypeValue) {
                isCurrentDefaultTypeValueValidOption = true;
                return false;
            }
        });
        // if the current value of the default type is a valid option, it will return it
        if (isCurrentDefaultTypeValueValidOption) {
            return currentDefaultTypeValue;
        } else {
            // if the current value of the default type is a not valid option, it will return the value of the first checked box
            var firstCheckedValue = "";
            checkboxes.each(function(i, checkbox) {
                if (checkbox.checked) {
                    firstCheckedValue = checkbox.value;
                    return false;
                }
            });
            return firstCheckedValue;
        }
    }

    // toggles the disable attribute of the Link Label and Link Title Attribute inputs, based on the Link Url existence
    function toggleDisableAttributeOnLinkLabelAndTitleInputs() {
        $(LINK_LABEL_SELECTOR).prop("disabled", !$(LINK_URL_SELECTOR).val());
        $(LINK_TITLE_SELECTOR).prop("disabled", !$(LINK_URL_SELECTOR).val());
    }

    // temporary workaround until CQ-4206495 and CUI-1818 are fixed:
    // add a margin when opening the dropdown
    $document.on("coral-select:showitems", DEFAULT_SIZE_SELECTOR, function(e) {
        var select = e.currentTarget;
        var buttonHeight = $(select).find("button").outerHeight(true);
        var count = select.items.length;
        var totalHeight = count * (buttonHeight + 5);
        var maxHeight = parseInt($(select).find("coral-selectlist").css("max-height"), 10);
        var marginBottom = Math.min(totalHeight, maxHeight);
        $(select).css("margin-bottom", marginBottom);
    });

    // temporary workaround until CQ-4206495 and CUI-1818 are fixed:
    // remove the margin when closing the dropdown
    $document.on("coral-select:hideitems", DEFAULT_SIZE_SELECTOR, function(e) {
        var select = e.currentTarget;
        $(select).css("margin-bottom", 0);
    });

    // Update the default size select when an allowed size is checked/unchecked
    $document.on("change", ALLOWED_SIZES_SELECTOR, function(e) {
        updateDefaultSizeSelect(true);
    });

    $document.on("foundation-contentloaded", function(e) {
        // Update the default size select when the design title dialog is opened
        Coral.commons.ready($(ALLOWED_SIZES_SELECTOR), function(component) {
            updateDefaultSizeSelect(false);
        });

        // Hide/display the edit dialog size dropdown
        Coral.commons.ready($(SIZES_SELECTOR, DEFAULT_SIZES_SELECTOR), function(component) {
            var select = $(SIZES_SELECTOR).get(0);
            var defaultSelect = $(DEFAULT_SIZES_SELECTOR).get(0);
            if (select === null || select === undefined || defaultSelect === null || defaultSelect === undefined) {
                return;
            }
            var itemsCount = select.items.getAll().length;
            if (itemsCount === 0) {
                // display all the sizes
                $(select).parent().remove();
            } else if (itemsCount === 1) {
                // don't display anything
                $(select).parent().remove();
                $(defaultSelect).parent().remove();
            } else {
                // display the values defined in the design policy
                $(defaultSelect).parent().remove();
            }
        });
        Coral.commons.ready($(LINK_URL_SELECTOR, LINK_LABEL_SELECTOR, LINK_TITLE_SELECTOR), function(component) {
            toggleDisableAttributeOnLinkLabelAndTitleInputs();
        });
    });

    $(document).on("input", LINK_URL_SELECTOR, function(input) {
        $(LINK_URL_SELECTOR).val(input.target.value);
        toggleDisableAttributeOnLinkLabelAndTitleInputs();
    });

    $(document).on("change", LINK_URL_SELECTOR, function(input) {
        toggleDisableAttributeOnLinkLabelAndTitleInputs();
    });

    // Display an error if all checkboxes are empty
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: ALLOWED_SIZES_SELECTOR,
        validate: function(el) {

            var $checkboxes = $(el).parent().children(ALLOWED_SIZES_SELECTOR);
            var firstEl = $checkboxes.get(0);
            var isValid = $(firstEl).data(DATA_ATTR_VALIDATION_STATE);
            var validationDone = isValid !== undefined;

            // if the validation has already been done, we get the status from the first checkbox
            if (validationDone) {
                $(firstEl).removeData(DATA_ATTR_VALIDATION_STATE);
                if (!isValid) {
                    return Granite.I18n.get("Select at least one size option.");
                } else {
                    return;
                }
            }

            // set the validation status on the first checkbox
            isValid = false;
            $checkboxes.each(function(i, checkbox) {
                if (checkbox.checked) {
                    isValid = true;
                    return false;
                }
            });
            $(firstEl).data(DATA_ATTR_VALIDATION_STATE, isValid);

            // trigger the validation on the first checkbox
            var api = $(firstEl).adaptTo("foundation-validation");
            api.checkValidity();
            api.updateUI();
        },
        show: function(el, message) {
            var $el = $(el);

            var fieldAPI = $el.adaptTo("foundation-field");
            if (fieldAPI && fieldAPI.setInvalid) {
                fieldAPI.setInvalid(true);
            }

            var error = $el.data("foundation-validation.internal.error");

            if (error) {
                error.content.innerHTML = message;

                if (!error.parentNode) {
                    $el.after(error);
                    error.show();
                }
            } else {
                error = new Coral.Tooltip();
                error.variant = "error";
                error.interaction = "off";
                error.placement = "bottom";
                error.target = el;
                error.content.innerHTML = message;
                error.open = true;
                error.id = Coral.commons.getUID();

                $el.data("foundation-validation.internal.error", error);
                $el.after(error);
            }
        }
    });

}(jQuery, Granite, Granite.author, jQuery(document)));
