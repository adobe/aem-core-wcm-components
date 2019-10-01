/*******************************************************************************
 * Copyright 2019 Adobe
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
(function(window, $, channel, Granite, Coral) {
    "use strict";

    // class of the edit dialog content
    var CLASS_EDIT_DIALOG = "cmp-contentfragmentlist__editor";

    // field selectors
    var SELECTOR_MODEL_PATH = "[name='./modelPath']";
    var SELECTOR_ORDER_BY = "coral-select[name='./orderBy']";
    var SELECTOR_SORT_ORDER = "coral-select[name='./sortOrder']";
    var SELECTOR_ELEMENT_NAMES = "[data-granite-coral-multifield-name='./elementNames']";
    var SELECTOR_ELEMENT_NAMES_ADD = SELECTOR_ELEMENT_NAMES + " > [is=coral-button]";

    // ui helper
    var ui = $(window).adaptTo("foundation-ui");

    // dialog texts
    var confirmationDialogTitle = Granite.I18n.get("Warning");
    var confirmationDialogMessage = Granite.I18n.get("Please confirm replacing the current content fragment list and its configuration");
    var confirmationDialogCancel = Granite.I18n.get("Cancel");
    var confirmationDialogConfirm = Granite.I18n.get("Confirm");
    var errorDialogTitle = Granite.I18n.get("Error");
    var errorDialogMessage = Granite.I18n.get("Failed to load the elements of the selected content fragment");

    // the model path field
    var modelPath;

    // keeps track of the current model path
    var currentModelPath;

    var editDialog;

    var elementsController;

    /**
     * A class which encapsulates the logic related to element selectors and variation name selector.
     */
    var ContentFragmentListController = function() {
        // container which contains either single elements select field or a multifield of element selectors
        this.elementNamesContainer = editDialog.querySelector(SELECTOR_ELEMENT_NAMES).parentNode;
        // element container resource path
        this.elementsContainerPath = editDialog.querySelector(SELECTOR_ELEMENT_NAMES).dataset.fieldPath;
        this.orderBy = editDialog.querySelector(SELECTOR_ORDER_BY);
        this.orderByPath = this.orderBy.dataset.fieldPath;
        this.sortOrder = editDialog.querySelector(SELECTOR_SORT_ORDER);
        this.fetchedState = null;
        this._updateElementsNamesFields();
    };

    /**
     * Updates the member fields of this class according to current dom of dialog.
     */
    ContentFragmentListController.prototype._updateElementsNamesFields = function() {
        // The multifield containing element selector dropdowns
        this.elementNames = editDialog.querySelector(SELECTOR_ELEMENT_NAMES);
        // The add button in multifield
        this.addElement = this.elementNames ? this.elementNames.querySelector(SELECTOR_ELEMENT_NAMES_ADD) : undefined;
    };

    /**
     * Disable all the fields of this controller.
     */
    ContentFragmentListController.prototype.disableFields = function() {
        if (this.addElement) {
            this.addElement.setAttribute("disabled", "");
        }

        if (this.orderBy) {
            this.orderBy.setAttribute("disabled", "");
        }

        if (this.sortOrder) {
            this.sortOrder.setAttribute("disabled", "");
        }
    };

    /**
     * Enable all the fields of this controller.
     */
    ContentFragmentListController.prototype.enableFields = function() {
        if (this.addElement) {
            this.addElement.removeAttribute("disabled");
        }

        if (this.orderBy) {
            this.orderBy.removeAttribute("disabled");
        }

        if (this.sortOrder) {
            this.sortOrder.removeAttribute("disabled");
        }
    };

    /**
     * Resets all the fields of this controller.
     */
    ContentFragmentListController.prototype.resetFields = function() {
        if (this.elementNames) {
            this.elementNames.items.clear();
        }

        if (this.orderBy) {
            this.orderBy.items.clear();
        }
    };

    /**
     * Creates an http request object for retrieving fragment's element names or variation names and returns it.
     *
     * @returns {Object} the resulting request object
     */
    ContentFragmentListController.prototype.prepareElementNamesRequest = function() {
        var data = {
            modelPath: modelPath.value
        };
        var url = Granite.HTTP.externalize(this.elementsContainerPath) + ".html";
        var request = $.get({
            url: url,
            data: data
        });
        return request;
    };

    /**
     * Creates an http request object for retrieving fragment's order by properties and returns it.
     *
     * @returns {Object} the resulting request object
     */
    ContentFragmentListController.prototype.prepareOrderByRequest = function() {
        var data = {
            modelPath: modelPath.value
        };
        var url = Granite.HTTP.externalize(this.orderByPath) + ".html";
        var request = $.get({
            url: url,
            data: data
        });
        return request;
    };

    /**
     * Retrieves the html for element names and orderBy properties and keeps the fetched values as "fetchedState" member.
     *
     * @param {Function} callback - function to execute when response is received
     */
    ContentFragmentListController.prototype.getUpdatedComponentsHTML = function(callback) {
        var elementNamesRequest = this.prepareElementNamesRequest();
        var orderByRequest = this.prepareOrderByRequest();
        var self = this;
        // wait for requests to load
        $.when(elementNamesRequest, orderByRequest).done(function(elementNamesResult, orderByResult) {
            var newElementNames = $(elementNamesResult[0]).find(SELECTOR_ELEMENT_NAMES)[0];
            var orderBy = $(orderByResult[0]).find(SELECTOR_ORDER_BY)[0];
            self.fetchedState = {
                elementNames: newElementNames,
                elementNamesContainerHTML: elementNamesResult[0],
                orderBy: orderBy
            };
            callback();
        })
            .fail(function() {
                ui.prompt(errorDialogTitle, errorDialogMessage, "error");
            });
    };

    /**
     * Checks if the current states of element names, single text selector and variation names match with those
     * present in fetchedState.
     *
     * @returns {Boolean} true if the states match or if there was no current state, false otherwise
     */
    ContentFragmentListController.prototype.testStateForUpdate = function() {
        // check if some element names are currently configured
        if ((this.elementNames && this.elementNames.items.length > 0) || (this.orderBy && this.orderBy.items.length > 0)) {
            // if we're unsetting the current fragment we need to reset the config
            if (!this.fetchedState || !this.fetchedState.elementNames || !this.fetchedState.orderBy) {
                return false;
            }
            // compare the items of the current and new element names fields
            var currentElementNamesItems = this.elementNames.template.content.querySelectorAll("coral-select-item");
            var newElementNamesItems = this.fetchedState.elementNames.template.content.querySelectorAll("coral-select-item");
            var newOrderByItems = this.fetchedState.orderBy.items.getAll();
            if (!itemsAreEqual(currentElementNamesItems, newElementNamesItems) ||
                !itemsAreEqual(this.orderBy.items.getAll(), newOrderByItems)) {
                return false;
            }
        }

        return true;
    };

    /**
     * Replace the current state with the values present in fetchedState and discard the fetchedState thereafter.
     */
    ContentFragmentListController.prototype.saveFetchedState = function() {
        if (!this.fetchedState) {
            return;
        }
        this._updateElementsHTML(this.fetchedState.elementNamesContainerHTML);
        this._updateOrderByHTML(this.fetchedState.orderBy);
        this.discardFetchedState();
    };

    /**
     * Discard the fetchedState data.
     */
    ContentFragmentListController.prototype.discardFetchedState = function() {
        this.fetchedState = null;
    };

    /**
     * Updates inner html of element container.
     *
     * @param {String} html - outerHTML value for elementNamesContainer
     */
    ContentFragmentListController.prototype._updateElementsHTML = function(html) {
        this.elementNamesContainer.innerHTML = $(html)[0].innerHTML;
        this._updateElementsNamesFields();
    };

    /**
     * Replaces the html of orderBy select item.
     *
     * @param {String} html - html value for orderBy
     */
    ContentFragmentListController.prototype._updateOrderByHTML = function(html) {
        this.orderBy.replaceWith(html);
        this.orderBy = editDialog.querySelector(SELECTOR_ORDER_BY);
    };

    function initialize(dialog) {
        editDialog = dialog;

        // get the fields
        modelPath = dialog.querySelector(SELECTOR_MODEL_PATH);

        // initialize state variables
        currentModelPath = modelPath.value;
        elementsController = new ContentFragmentListController();

        // disable add button and variation name if no content fragment is currently set
        if (!currentModelPath) {
            elementsController.disableFields();
        }

        // register change listener
        $(modelPath).on("foundation-field-change", onModelPathChange);
    }

    /**
     * Executes after the fragment path has changed. Shows a confirmation dialog to the user if the current
     * configuration is to be reset and updates the fields to reflect the newly selected content fragment.
     */
    function onModelPathChange() {
        // if the fragment was reset (i.e. the fragment path was deleted)
        if (!modelPath.value) {
            var canKeepConfig = elementsController.testStateForUpdate();
            if (canKeepConfig) {
                // There was no current configuration. We just need to disable fields.
                currentModelPath = modelPath.value;
                elementsController.disableFields();
                return;
            }
            // There was some current configuration. Show a confirmation dialog
            confirmModelChange(null, null, elementsController.disableFields, elementsController);
            // don't do anything else
            return;
        }

        elementsController.getUpdatedComponentsHTML(function() {
            // check if we can keep the current configuration, in which case no confirmation dialog is necessary
            var canKeepConfig = elementsController.testStateForUpdate();
            if (canKeepConfig) {
                if (!currentModelPath) {
                    elementsController.enableFields();
                }
                currentModelPath = modelPath.value;
                // its okay to save fetched state
                elementsController.saveFetchedState();
                return;
            }
            // else show a confirmation dialog
            confirmModelChange(elementsController.discardFetchedState, elementsController,
                elementsController.saveFetchedState, elementsController);
        });

    }

    /**
     * Presents the user with a confirmation dialog if the current configuration needs to be reset as a result
     * of the content fragment change.
     *
     * @param {Function} cancelCallback - callback to call if change is cancelled
     * @param {Object} cancelCallbackScope - scope (value of "this" keyword) for cancelCallback
     * @param {Function} confirmCallback - a callback to execute after the change is confirmed
     * @param {Object} confirmCallbackScope - the scope (value of "this" keyword) to use for confirmCallback
     */
    function confirmModelChange(cancelCallback, cancelCallbackScope, confirmCallback, confirmCallbackScope) {

        ui.prompt(confirmationDialogTitle, confirmationDialogMessage, "warning", [{
            text: confirmationDialogCancel,
            handler: function() {
                // reset the fragment path to its previous value
                requestAnimationFrame(function() {
                    modelPath.value = currentModelPath;
                });
                if (cancelCallback) {
                    cancelCallback.call(cancelCallbackScope);
                }
            }
        }, {
            text: confirmationDialogConfirm,
            primary: true,
            handler: function() {
                // reset the current configuration
                elementsController.resetFields();
                // update the current fragment path
                currentModelPath = modelPath.value;
                // execute callback
                if (confirmCallback) {
                    confirmCallback.call(confirmCallbackScope);
                }
            }
        }]);
    }

    /**
     * Compares two arrays containing select items, returning true if the arrays have the same size and all contained
     * items have the same value and label.
     *
     * @param {Array} a1 - first array to compare
     * @param {Array} a2 - second array to compare
     * @returns {Boolean} true if both array are equals, false otherwise
     */
    function itemsAreEqual(a1, a2) {
        // verify that the arrays have the same length
        if (a1.length !== a2.length) {
            return false;
        }
        for (var i = 0; i < a1.length; i++) {
            var item1 = a1[i];
            var item2 = a2[i];
            if (item1.attributes.value.value !== item2.attributes.value.value ||
                item1.textContent !== item2.textContent) {
                // the values or labels of the current items didn't match
                return false;
            }
        }
        return true;
    }

    /**
     * Initializes the dialog after it has loaded.
     */
    channel.on("foundation-contentloaded", function(e) {
        if (e.target.getElementsByClassName(CLASS_EDIT_DIALOG).length > 0) {
            Coral.commons.ready(e.target, function(dialog) {
                initialize(dialog);
            });
        }
    });

})(window, jQuery, jQuery(document), Granite, Coral);
