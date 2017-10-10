/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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
(function (window, $, channel, Granite, Coral) {
    "use strict";

    // class of the edit dialog content
    var CLASS_EDIT_DIALOG = "cmp-contentfragment__edit-dialog";
    // name of the fragment path field
    var NAME_FRAGMENT_PATH = "./fragmentPath";
    // name of the element names field (multifield)
    var NAME_ELEMENT_NAMES = "./elementNames";

    // ui helper
    var ui = $(window).adaptTo("foundation-ui");

    // dialog texts
    var confirmationDialogTitle = Granite.I18n.get("Warning");
    var confirmationDialogMessage = Granite.I18n.get("Please confirm replacing the current content fragment and its configuration");
    var confirmationDialogCancel = Granite.I18n.get("Cancel");
    var confirmationDialogConfirm = Granite.I18n.get("Confirm");
    var errorDialogTitle = Granite.I18n.get("Error");
    var errorDialogMessage = Granite.I18n.get("Failed to load the elements of the selected content fragment");

    // the fragment path field (foundation autocomplete)
    var fragmentPath;
    // the element names field (multifield)
    var elementNames;
    // the add button of the element names field
    var addButton;
    // the path of the element names field
    var elementNamesPath;
    // keeps track of the current fragment path
    var currentFragmentPath;

    function initialize(dialog) {
        // get the fields
        fragmentPath = dialog.querySelector("[name='"+NAME_FRAGMENT_PATH+"']");
        var elementNamesSelector = "[data-granite-coral-multifield-name='"+NAME_ELEMENT_NAMES+"']";
        elementNames = dialog.querySelector(elementNamesSelector);
        addButton = elementNames.querySelector(elementNamesSelector + " > [is=coral-button]");

        // get the current fragment path
        currentFragmentPath = fragmentPath.value;
        // get the path of the element names field from its data attribute
        elementNamesPath = elementNames.dataset.fieldPath;
        // disable the add button if no content fragment is currently set
        if (!currentFragmentPath) {
            addButton.setAttribute("disabled", "");
        }

        // execute function when the fragment path changes
        $(fragmentPath).on("foundation-field-change", onFragmentPathChange);
    }

    /**
     * Executes after the fragment path has changed. Shows a confirmation dialog to the user if the configuration is to
     * be reset and updates the element names multifield to reflect the newly selected content fragment.
     */
    function onFragmentPathChange() {
        // if the fragment path was deleted
        if (!fragmentPath.value) {
            // if no elements are configured, then there is no need for a confirmation
            if (elementNames.items.length === 0) {
                // unset the current fragment path
                currentFragmentPath = null;
                // disable add button
                addButton.setAttribute("disabled", "");
            } else {
                // show confirmation dialog
                showConfirmation(function () {
                    // disable add button after confirmation
                    addButton.setAttribute("disabled", "");
                });
            }
            // don't do anything else
            return;
        }

        // request the markup of the element names field (with the new fragment path as a parameter)
        $.get({
            url: Granite.HTTP.externalize(elementNamesPath) + ".html",
            data: {
                fragmentPath: fragmentPath.value
            }
        }).success(function (html) {
            // find the updated multifield in the response markup
            var newElementNames = $(html).find("[data-granite-coral-multifield-name='"+NAME_ELEMENT_NAMES+"']")[0];
            Coral.commons.ready(newElementNames, function() {
                // find the select templates of both the existing and the new multifield
                var select1 = $(elementNames.template.content).find("coral-select").get(0);
                var select2 = $(newElementNames.template.content).find("coral-select").get(0);

                // if no elements are configured or the selects are equal (which means that the previous and the newly
                // selected content fragment have the same elements), then there is no need for a confirmation
                if (elementNames.items.length === 0 || selectsAreEqual(select1, select2)) {
                    // update the current fragment path
                    currentFragmentPath = fragmentPath.value;
                    // update the existing multifield's template
                    elementNames.template = newElementNames.template;
                    // enable add button
                    addButton.removeAttribute("disabled");
                } else {
                    // show confirmation dialog
                    showConfirmation(function () {
                        // update the existing multifield's template
                        elementNames.template = newElementNames.template;
                        // enable add button
                        addButton.removeAttribute("disabled");
                    });
                }
            });
        }).error(function () {
            ui.prompt(errorDialogTitle, errorDialogMessage, "error");
        });
    }

    /**
     * Shows a confirmation dialog. If the user cancels, the fragment path is reset to its previous value; if they
     * accept, the current fragment path is updated, the configured element names are cleared, and the specified
     * callback is invoked.
     */
    function showConfirmation(callback) {
        ui.prompt(confirmationDialogTitle, confirmationDialogMessage, "warning", [{
            text: confirmationDialogCancel,
            handler: function () {
                // reset the fragment path to its previous value
                requestAnimationFrame(function() {
                    fragmentPath.value = currentFragmentPath;
                });
            }
        }, {
            text: confirmationDialogConfirm,
            primary: true,
            handler: function () {
                // update the current fragment path
                currentFragmentPath = fragmentPath.value;
                // clear all configured elements
                elementNames.items.clear();
                // invoke callback
                callback();
            }
        }]);
    }

    /**
     * Verifies if two uninitialized coral-select components are equal by checking if they contain the same
     * coral-select-item elements in their DOM.
     */
    function selectsAreEqual(select1, select2) {
        // check if they have the same number of items
        var items1 = select1.querySelectorAll("coral-select-item");
        var items2 = select2.querySelectorAll("coral-select-item");
        if (items1.length !== items2.length) {
            return false;
        }

        // check if the items have the same values
        for (var i = 0; i < items1.length; i++) {
            var item1 = items1[i];
            var item2 = items2[i];
            if (item1.attributes.value.value !== item2.attributes.value.value) {
                return false;
            }
        }

        return true;
    }

    /**
     * Initializes the dialog after it has loaded.
     */
    channel.on("foundation-contentloaded", function (e) {
        if (e.target.getElementsByClassName(CLASS_EDIT_DIALOG).length > 0) {
            Coral.commons.ready(e.target, function (dialog) {
                initialize(dialog);
            });
        }
    });

})(window, jQuery, jQuery(document), Granite, Coral);
