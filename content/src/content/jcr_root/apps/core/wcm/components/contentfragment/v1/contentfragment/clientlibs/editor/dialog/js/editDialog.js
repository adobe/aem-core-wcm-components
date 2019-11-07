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
    var CLASS_EDIT_DIALOG = "cmp-contentfragment__editor";

    // field selectors
    var SELECTOR_FRAGMENT_PATH = "[name='./fragmentPath']";
    var SELECTOR_ELEMENT_NAMES_CONTAINER = "[data-element-names-container='true']";
    var SELECTOR_ELEMENT_NAMES = "[data-granite-coral-multifield-name='./elementNames']";
    var SELECTOR_SINGLE_TEXT_ELEMENT = "[data-single-text-selector='true']";
    var SELECTOR_ELEMENT_NAMES_ADD = SELECTOR_ELEMENT_NAMES + " > [is=coral-button]";
    var SELECTOR_VARIATION_NAME = "[name='./variationName']";
    var SELECTOR_DISPLAY_MODE_RADIO_GROUP = "[data-display-mode-radio-group='true']";
    var SELECTOR_DISPLAY_MODE_CHECKED = "[name='./displayMode']:checked";
    var SELECTOR_PARAGRAPH_CONTROLS = ".cmp-contentfragment__editor-paragraph-controls";
    var SELECTOR_PARAGRAPH_SCOPE = "[name='./paragraphScope']";
    var SELECTOR_PARAGRAPH_RANGE = "[name='./paragraphRange']";
    var SELECTOR_PARAGRAPH_HEADINGS = "[name='./paragraphHeadings']";

    // mode in which only one multiline text element could be selected for display
    var SINGLE_TEXT_DISPLAY_MODE = "singleText";

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

    // the paragraph controls (field set)
    var paragraphControls;
    // the tab containing paragraph control
    var paragraphControlsTab;

    // keeps track of the current fragment path
    var currentFragmentPath;

    var editDialog;

    var elementsController;

    /**
     * A class which encapsulates the logic related to element selectors and variation name selector.
     */
    var ElementsController = function() {
        // container which contains either single elements select field or a multifield of element selectors
        this.elementNamesContainer = editDialog.querySelector(SELECTOR_ELEMENT_NAMES_CONTAINER);
        // element container resource path
        this.elementsContainerPath = this.elementNamesContainer.dataset.fieldPath;
        this.fetchedState = null;
        this._updateFields();
    };

    /**
     * Updates the member fields of this class according to current dom of dialog.
     */
    ElementsController.prototype._updateFields = function() {
        // The multifield containing element selector dropdowns
        this.elementNames = editDialog.querySelector(SELECTOR_ELEMENT_NAMES);
        // The add button in multifield
        this.addElement = this.elementNames ? this.elementNames.querySelector(SELECTOR_ELEMENT_NAMES_ADD) : undefined;
        // The dropdown containing element selector for multiline text elements only. Either this or the elementNames
        // multifield should be visible to user at a time.
        this.singleTextSelector = editDialog.querySelector(SELECTOR_SINGLE_TEXT_ELEMENT);
        // the variation name field (select)
        this.variationName = editDialog.querySelector(SELECTOR_VARIATION_NAME);
        this.variationNamePath = this.variationName.dataset.fieldPath;
    };

    /**
     * Disable all the fields of this controller.
     */
    ElementsController.prototype.disableFields = function() {
        if (this.addElement) {
            this.addElement.setAttribute("disabled", "");
        }
        if (this.singleTextSelector) {
            this.singleTextSelector.setAttribute("disabled", "");
        }
        if (this.variationName) {
            this.variationName.setAttribute("disabled", "");
        }
    };

    /**
     * Enable all the fields of this controller.
     */
    ElementsController.prototype.enableFields = function() {
        if (this.addElement) {
            this.addElement.removeAttribute("disabled");
        }
        if (this.singleTextSelector) {
            this.singleTextSelector.removeAttribute("disabled");
        }
        if (this.variationName) {
            this.variationName.removeAttribute("disabled");
        }
    };

    /**
     * Resets all the fields of this controller.
     */
    ElementsController.prototype.resetFields = function() {
        if (this.elementNames) {
            this.elementNames.items.clear();
        }
        if (this.singleTextSelector) {
            this.singleTextSelector.value = "";
        }
        if (this.variationName) {
            this.variationName.value = "";
        }
    };

    /**
     * Creates an http request object for retrieving fragment's element names or variation names and returns it.
     *
     * @param {String} displayMode - displayMode parameter for element name request. Should be "singleText" or "multi"
     * @param {String} type - type of request. It can have the following values -
     * 1. "variation" for getting variation names
     * 2. "elements" for getting element names
     * @returns {Object} the resulting request
     */
    ElementsController.prototype.prepareRequest = function(displayMode, type) {
        if (typeof displayMode === "undefined") {
            displayMode = editDialog.querySelector(SELECTOR_DISPLAY_MODE_CHECKED).value;
        }
        var data = {
            fragmentPath: fragmentPath.value,
            displayMode: displayMode
        };
        var url;
        if (type === "variation") {
            url = Granite.HTTP.externalize(this.variationNamePath) + ".html";
        } else if (type === "elements") {
            url = Granite.HTTP.externalize(this.elementsContainerPath) + ".html";
        }
        var request = $.get({
            url: url,
            data: data
        });
        return request;
    };

    /**
     * Retrieves the html for element names and variation names and keeps the fetched values as "fetchedState" member.
     *
     * @param {String} displayMode - display mode to use as parameter of element names request
     * @param {Function} callback - function to execute when response is received
     */
    ElementsController.prototype.testGetHTML = function(displayMode, callback) {
        var elementNamesRequest = this.prepareRequest(displayMode, "elements");
        var variationNameRequest = this.prepareRequest(displayMode, "variation");
        var self = this;
        // wait for requests to load
        $.when(elementNamesRequest, variationNameRequest).then(function(result1, result2) {
            var newElementNames = $(result1[0]).find(SELECTOR_ELEMENT_NAMES)[0];
            var newSingleTextSelector = $(result1[0]).find(SELECTOR_SINGLE_TEXT_ELEMENT)[0];
            var newVariationName = $(result2[0]).find(SELECTOR_VARIATION_NAME)[0];
            // get the fields from the resulting markup and create a test state
            Coral.commons.ready(newElementNames, function() {
                Coral.commons.ready(newSingleTextSelector, function() {
                    Coral.commons.ready(newVariationName, function() {
                        self.fetchedState = {
                            elementNames: newElementNames,
                            singleTextSelector: newSingleTextSelector,
                            variationName: newVariationName,
                            elementNamesContainerHTML: result1[0],
                            variationNameHTML: result2[0]
                        };
                        callback();
                    });
                });
            });

        }, function() {
            // display error dialog if one of the requests failed
            ui.prompt(errorDialogTitle, errorDialogMessage, "error");
        });
    };

    /**
     * Checks if the current states of element names, single text selector and variation names match with those
     * present in fetchedState.
     *
     * @returns {Boolean} true if the states match or if there was no current state, false otherwise
     */
    ElementsController.prototype.testStateForUpdate = function() {
        // check if some element names are currently configured
        if (this.elementNames && this.elementNames.items.length > 0) {
            // if we're unsetting the current fragment we need to reset the config
            if (!this.fetchedState || !this.fetchedState.elementNames) {
                return false;
            }
            // compare the items of the current and new element names fields
            var currentItems = this.elementNames.template.content.querySelectorAll("coral-select-item");
            var newItems = this.fetchedState.elementNames.template.content.querySelectorAll("coral-select-item");
            if (!itemsAreEqual(currentItems, newItems)) {
                return false;
            }
        }

        if (this.singleTextSelector && this.singleTextSelector.items.length > 0) {
            // if we're unsetting the current fragment we need to reset the config
            if (!this.fetchedState || !this.fetchedState.singleTextSelector) {
                return false;
            }
            // compare the items of the current and new element names fields
            var currentSingleTextItems = this.singleTextSelector.querySelectorAll("coral-select-item");
            var newSingleTextItems = this.fetchedState.singleTextSelector.querySelectorAll("coral-select-item");
            if (!itemsAreEqual(currentSingleTextItems, newSingleTextItems)) {
                return false;
            }
        }

        // check if a variation is currently configured
        if (this.variationName.value && this.variationName.value !== "master") {
            // if we're unsetting the current fragment we need to reset the config
            if (!this.fetchedState || !this.fetchedState.variationName) {
                return false;
            }
            // compare the items of the current and new variation name fields
            if (!itemsAreEqual(this.variationName.items.getAll(), this.fetchedState.variationName.items.getAll())) {
                return false;
            }
        }

        return true;
    };

    /**
     * Replace the current state with the values present in fetchedState and discard the fetchedState thereafter.
     */
    ElementsController.prototype.saveFetchedState = function() {
        if (!this.fetchedState) {
            return;
        }
        if (!this.elementNames && !this.singleTextSelector) {
            this._updateElementsHTML(this.fetchedState.elementNamesContainerHTML);
        } else if (this.fetchedState.elementNames) {
            if (this.fetchedState.elementNames.template.content.children) {
                this._updateElementsDOM(this.fetchedState.elementNames);
            } else {
                // if the content of template is not accessible through the DOM (IE 11!),
                // then use the HTML to update the elements multifield
                this._updateElementsHTML(this.fetchedState.elementNamesContainerHTML);
            }
        } else {
            this._updateElementsDOM(this.fetchedState.singleTextSelector);
        }
        this._updateVariationDOM(this.fetchedState.variationName);
        this.discardFetchedState();
    };

    /**
     * Discard the fetchedState data.
     */
    ElementsController.prototype.discardFetchedState = function() {
        this.fetchedState = null;
    };

    /**
     * Retrieve element names and update the current element names with the retrieved data.
     *
     * @param {String} displayMode - selected display mode of the component
     */
    ElementsController.prototype.fetchAndUpdateElementsHTML = function(displayMode) {
        var elementNamesRequest = this.prepareRequest(displayMode, "elements");
        var self = this;
        // wait for requests to load
        $.when(elementNamesRequest).then(function(result) {
            self._updateElementsHTML(result);
        }, function() {
            // display error dialog if one of the requests failed
            ui.prompt(errorDialogTitle, errorDialogMessage, "error");
        });
    };

    /**
     * Updates inner html of element container.
     *
     * @param {String} html - outerHTML value for elementNamesContainer
     */
    ElementsController.prototype._updateElementsHTML = function(html) {
        this.elementNamesContainer.innerHTML = $(html)[0].innerHTML;
        this._updateFields();
    };

    /**
     * Updates dom of element container with the passed dom. If the passed dom is multifield, the current multifield
     * template would be replaced with the dom's template otherwise the dom would used as the new singleTextSelector
     * member.
     *
     * @param {HTMLElement} dom - new dom
     */
    ElementsController.prototype._updateElementsDOM = function(dom) {
        if (dom.tagName === "CORAL-MULTIFIELD") {
            // replace the element names multifield's template
            this.elementNames.template = dom.template;
        } else {
            dom.value = this.singleTextSelector.value;
            this.singleTextSelector.parentNode.replaceChild(dom, this.singleTextSelector);
            this.singleTextSelector = dom;
            this.singleTextSelector.removeAttribute("disabled");
        }
        this._updateFields();
    };

    /**
     * Updates dom of variation name select dropdown.
     *
     * @param {HTMLElement} dom - dom for variation name dropdown
     */
    ElementsController.prototype._updateVariationDOM = function(dom) {
        // replace the variation name select, keeping its value
        dom.value = this.variationName.value;
        this.variationName.parentNode.replaceChild(dom, this.variationName);
        this.variationName = dom;
        this.variationName.removeAttribute("disabled");
        this._updateFields();
    };

    function initialize(dialog) {
        // get path of component being edited
        editDialog = dialog;

        // get the fields
        fragmentPath = dialog.querySelector(SELECTOR_FRAGMENT_PATH);
        paragraphControls = dialog.querySelector(SELECTOR_PARAGRAPH_CONTROLS);
        paragraphControlsTab = dialog.querySelector("coral-tabview").tabList.items.getAll()[1];

        // initialize state variables
        currentFragmentPath = fragmentPath.value;
        elementsController = new ElementsController();

        // disable add button and variation name if no content fragment is currently set
        if (!currentFragmentPath) {
            elementsController.disableFields();
        }
        // enable / disable the paragraph controls
        setParagraphControlsState();
        // hide/show paragraph control tab
        updateParagraphControlTabState();

        // register change listener
        $(fragmentPath).on("foundation-field-change", onFragmentPathChange);
        $(document).on("change", SELECTOR_PARAGRAPH_SCOPE, setParagraphControlsState);
        var $radioGroup = $(dialog).find(SELECTOR_DISPLAY_MODE_RADIO_GROUP);
        $radioGroup.on("change", function(e) {
            elementsController.fetchAndUpdateElementsHTML(e.target.value);
            updateParagraphControlTabState();
        });
    }

    /**
     * Executes after the fragment path has changed. Shows a confirmation dialog to the user if the current
     * configuration is to be reset and updates the fields to reflect the newly selected content fragment.
     */
    function onFragmentPathChange() {
        // if the fragment was reset (i.e. the fragment path was deleted)
        if (!fragmentPath.value) {
            var canKeepConfig = elementsController.testStateForUpdate();
            if (canKeepConfig) {
                // There was no current configuration. We just need to disable fields.
                currentFragmentPath = fragmentPath.value;
                elementsController.disableFields();
                return;
            }
            // There was some current configuration. Show a confirmation dialog
            confirmFragmentChange(null, null, elementsController.disableFields, elementsController);
            // don't do anything else
            return;
        }

        elementsController.testGetHTML(editDialog.querySelector(SELECTOR_DISPLAY_MODE_CHECKED).value, function() {
            // check if we can keep the current configuration, in which case no confirmation dialog is necessary
            var canKeepConfig = elementsController.testStateForUpdate();
            if (canKeepConfig) {
                if (!currentFragmentPath) {
                    elementsController.enableFields();
                }
                currentFragmentPath = fragmentPath.value;
                // its okay to save fetched state
                elementsController.saveFetchedState();
                return;
            }
            // else show a confirmation dialog
            confirmFragmentChange(elementsController.discardFetchedState, elementsController,
                elementsController.saveFetchedState, elementsController);
        });

    }

    /**
     * Presents the user with a confirmation dialog if the current configuration needs to be reset as a result
     * of the content fragment change.
     *
     * @param {Function} cancelCallback - callback to call if change is cancelled
     * @param {Object} cancelCallbackScope - scope (value of "this" keyword) for cancelCallback
     * @param {Function} confirmCallback a callback to execute after the change is confirmed
     * @param {Object} confirmCallbackScope - the scope (value of "this" keyword) to use for confirmCallback
     */
    function confirmFragmentChange(cancelCallback, cancelCallbackScope, confirmCallback, confirmCallbackScope) {

        ui.prompt(confirmationDialogTitle, confirmationDialogMessage, "warning", [{
            text: confirmationDialogCancel,
            handler: function() {
                // reset the fragment path to its previous value
                requestAnimationFrame(function() {
                    fragmentPath.value = currentFragmentPath;
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
                currentFragmentPath = fragmentPath.value;
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
     * @returns {Boolean} true if both arrays are equal, false otherwise
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
     * Enables or disables the paragraph range and headings field depending on the state of the paragraph scope field.
     */
    function setParagraphControlsState() {
        // get the selected scope radio button (might not be present at all)
        var scope = paragraphControls.querySelector(SELECTOR_PARAGRAPH_SCOPE + "[checked]");
        if (scope) {
            // enable or disable range and headings fields according to the scope value
            var range = paragraphControls.querySelector(SELECTOR_PARAGRAPH_RANGE);
            var headings = paragraphControls.querySelector(SELECTOR_PARAGRAPH_HEADINGS);
            if (scope.value === "range") {
                range.removeAttribute("disabled");
                headings.removeAttribute("disabled");
            } else {
                range.setAttribute("disabled", "");
                headings.setAttribute("disabled", "");
            }
        }
    }

    // Toggles the display of paragraph control tab depending on display mode
    function updateParagraphControlTabState() {
        var displayMode = editDialog.querySelector(SELECTOR_DISPLAY_MODE_CHECKED).value;
        if (displayMode === SINGLE_TEXT_DISPLAY_MODE) {
            paragraphControlsTab.hidden = false;
        } else {
            paragraphControlsTab.hidden = true;
        }
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
