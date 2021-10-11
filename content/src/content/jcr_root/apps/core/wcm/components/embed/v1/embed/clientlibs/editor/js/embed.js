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
(function(document, $, Coral) {
    "use strict";

    var URL_VALIDATION_GET_SUFFIX = ".urlProcessor.json";

    var selectors = {
        dialogContent: ".cmp-embed__editor",
        designDialogContent: ".cmp-embed__design-editor",
        allowedEmbeddables: ".allowed-embeddables",
        toggleCheckboxes: ".toggle-checkbox",
        embeddableField: "[data-cmp-embed-dialog-edit-hook='embeddableField']",
        typeField: "[data-cmp-embed-dialog-edit-hook='typeField']",
        typeRadio: "[data-cmp-embed-dialog-edit-hook='typeField'] coral-radio",
        urlField: "[data-cmp-embed-dialog-edit-hook='urlField']",
        urlStatus: "[data-cmp-embed-dialog-edit-hook='urlStatus']"
    };

    /**
     * Enumeration of embed types.
     */
    var type = {
        URL: "url",
        EMBEDDABLE: "embeddable",
        HTML: "html"
    };

    var registry = $(window).adaptTo("foundation-registry");

    var embedResourcePath;
    var embeddableField;
    var typeField;
    var typeRadios;
    var foundationFieldSelectors;
    var urlField;
    var urlStatus;

    // URL field validation object
    var urlValidation = new function() {

        var validation = {};

        this.getUrl = function() {
            return validation.url;
        };

        this.isValidUrl = function() {
            return validation.isValid;
        };

        this.getErrorMessage = function() {
            return validation.errorMessage;
        };

        this.getProvider = function() {
            return validation.provider;
        };

        // Performs the URL field validation
        this.perform = function(el, callback) {
            validation.url = el.value;
            if (!isUrl(validation.url)) {
                validation.isValid = false;
                validation.errorMessage = "Please enter a valid URL";
            } else {
                // Performs the server-side validation and executes the callback
                var requestUrl = embedResourcePath + URL_VALIDATION_GET_SUFFIX + "?url=" + validation.url;
                var request = new XMLHttpRequest();
                request.open("GET", requestUrl, true);
                request.onload = function() {
                    if (request.status === 200) {
                        var result = JSON.parse(request.responseText);
                        if (result && result.options && result.options.provider) {
                            validation.provider = result.options.provider;
                        } else if (result && result.processor) {
                            validation.provider = result.processor;
                        }
                        validation.isValid = true;
                    } else if (request.status === 404) {
                        validation.isValid = false;
                        validation.errorMessage = "This embed URL is not supported";
                    } else {
                        validation.isValid = false;
                        validation.errorMessage = "A problem occurred when validating the URL";
                    }
                    callback(el);
                };
                request.send();
            }
        };

    };

    // Registers a validator for the URL field
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: selectors.urlField,
        validate: function(el) {
            var url = el.value;
            if (url !== urlValidation.getUrl()) {
                urlValidation.perform(el, validateUrlField);
            }
            updateUrlStatus();
            if (!urlValidation.isValidUrl()) {
                return Granite.I18n.get(urlValidation.getErrorMessage());
            }
        }
    });

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            embedResourcePath = $dialog[0].getAttribute("action");
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                embeddableField = dialogContent.querySelector(selectors.embeddableField);
                typeField = dialogContent.querySelector(selectors.typeField);
                typeRadios = typeField.querySelectorAll(selectors.typeRadio);
                foundationFieldSelectors = getFoundationFieldSelectors();
                urlField = dialogContent.querySelector(selectors.urlField);
                urlStatus = dialogContent.querySelector(selectors.urlStatus);
                var hasCheckedTypeRadio = false;

                if (typeRadios.length) {
                    for (var i = 0; i < typeRadios.length; i++) {
                        var typeRadio = typeRadios[i];

                        Coral.commons.ready(typeRadio, function(element) {
                            var value = element.value;
                            var showHideTarget = getShowHideTarget(typeField);

                            if (element.checked) {
                                toggleShowHideTargets(showHideTarget, value);
                                hasCheckedTypeRadio = true;
                            }

                            element.on("change", function() {
                                toggleShowHideTargets(showHideTarget, value);
                                if (embeddableField && (value === type.EMBEDDABLE)) {
                                    embeddableField.trigger("change");
                                }
                                if (urlField && (value === type.URL)) {
                                    urlField.trigger("change");
                                }
                            });
                        });
                    }

                    Coral.commons.nextFrame(function() {
                        if (!hasCheckedTypeRadio) {
                            typeRadios[0].checked = true;
                            typeRadios[0].trigger("change");
                        }
                    });

                    if (embeddableField) {
                        Coral.commons.ready(embeddableField, function(element) {
                            var showHideTarget = getShowHideTarget(element);

                            toggleShowHideTargets(showHideTarget, element.value);

                            element.on("change", function() {
                                toggleShowHideTargets(showHideTarget, element.value);
                            });
                        });
                    }

                    if (urlField) {
                        Coral.commons.ready(urlField, function(element) {
                            if (element.value !== "") {
                                validateUrlField();
                            }
                        });
                    }
                }
            }

            var designDialogContent = $dialog[0].querySelector(selectors.designDialogContent);
            if (designDialogContent) {
                // for all toggles
                var toggleCheckboxes = designDialogContent.querySelectorAll(selectors.toggleCheckboxes);
                toggleCheckboxes.forEach(function(toggleCheckbox) {
                    Coral.commons.ready(toggleCheckbox, function() {
                        var showHideTarget = getShowHideTarget(toggleCheckbox);

                        // either hide or show them depending on the value of the toggle
                        toggleShowHideTargets(showHideTarget, toggleCheckbox.checked.toString());

                        // register an event handler
                        toggleCheckbox.on("change", function() {
                            toggleShowHideTargets(showHideTarget, toggleCheckbox.checked.toString());
                        });
                    });
                });
            }
        }
    });

    /**
     * This is triggered after "dialog-loaded" but is required, as otherwise the relation between panel and tab is not yet established.
     */
    $(document).on("dialog-ready", function() {
        var designDialogContent = document.querySelector(selectors.designDialogContent);
        if (designDialogContent) {
            // for all optional tabs
            var allowedEmbeddablesDropdown = designDialogContent.querySelector(selectors.allowedEmbeddables);
            Coral.commons.ready(allowedEmbeddablesDropdown, function() {
                // register an event handler
                allowedEmbeddablesDropdown.on("change", function() {
                    toggleShowHideTabs(allowedEmbeddablesDropdown.values);
                });
                // set initial state inside requestAnimationFrame as only there the relevant attribute "aria-labelledby" is set
                window.requestAnimationFrame((function() {
                    toggleShowHideTabs(allowedEmbeddablesDropdown.values);
                }));
            });
        }
    });

    /**
     * Toggles the disabled state and visibility of tabs linked to panels matching the target.
     * Tabs that match the provided value are enabled / shown, otherwise they are disabled / hidden.
     *
     * @param {String[]} values The values of the target to enable and show
     */
    function toggleShowHideTabs(values) {
        var panelElements = document.querySelectorAll("[data-cmp-embed-dialog-edit-embeddableoptions]");
        for (var i = 0; i < panelElements.length; i++) {
            var showHideTargetValue = getShowHideTargetValue(panelElements[i]);
            var tabElement = getTabElementForPanel(panelElements[i]);
            if (values.includes(showHideTargetValue)) {
                toggleTarget($(tabElement), true);
            } else {
                toggleTarget($(tabElement), false);
            }
        }
    }

    /**
     * Retrieves the tab element connected to a given panel element
     *
     * @param {Element} panelElement The panel element for which to return the tab element
     * @returns {Element} The related tab element
     */
    function getTabElementForPanel(panelElement) {
        // go to one level below panelstack
        var panel = panelElement.closest("coral-panel");
        if (panel !== null) {
            // get tab id controlling this panel
            var tabId = panel.getAttribute("aria-labelledby");
            return document.getElementById(tabId);
        } else {
            return null;
        }
    }

    /**
     * Toggles the disabled state and visibility of elements matching the target.
     * Targets that match the provided value are enabled / shown, otherwise they are disabled / hidden.
     *
     * @param {String} target Comma separated list of targets to toggle
     * @param {String} value The value of the target to enable and show
     */
    function toggleShowHideTargets(target, value) {
        var showHideTargets = document.querySelectorAll(target);

        for (var i = 0; i < showHideTargets.length; i++) {
            var showHideTarget = showHideTargets[i];
            var showHideTargetValue = getShowHideTargetValue(showHideTarget);
            if (showHideTargetValue === value) {
                toggleTarget($(showHideTarget), true);
            } else {
                toggleTarget($(showHideTarget), false);
            }
        }
    }

    /**
     * Toggles a dialog toggle target, setting its disabled state, validity and visibility.
     *
     * @param {jQuery} $element The target
     * @param {Boolean} show true to disable and hide the target, false otherwise
     */
    function toggleTarget($element, show) {
        var field = $element.adaptTo("foundation-field");
        var toggleable = $element.parent(".foundation-toggleable").adaptTo("foundation-toggleable");
        if (show) {
            if (toggleable) {
                toggleable.show();
            } else {
                $element.show();
            }
            if (field && typeof field.setDisabled === "function") {
                field.setDisabled(false);
            }
        } else {
            if (toggleable) {
                toggleable.hide();
            } else {
                $element.hide();
            }
            if (field && typeof field.setDisabled === "function") {
                field.setDisabled(true);
                setFieldValid($element);
            }
        }
        toggleTargetChildren($element, show);
    }

    /**
     * Toggles child fields of a dialog toggle target, setting their disabled and valid states.
     *
     * @param {jQuery} $element The target
     * @param {Boolean} show true to disable and hide the target, false otherwise
     */
    function toggleTargetChildren($element, show) {
        var $childFoundationFields = $element.find(foundationFieldSelectors);
        $childFoundationFields.each(function(index, element) {
            var field = $(element).adaptTo("foundation-field");
            if (field) {
                if (show && typeof field.setDisabled === "function") {
                    field.setDisabled(false);
                } else {
                    if (typeof field.setDisabled === "function") {
                        field.setDisabled(true);
                    }
                    setFieldValid($(element));
                }
            }
        });
    }

    /**
     * Gets the show hide target for the passed element.
     *
     * @param {Element} element The element from which to get the show hide target
     * @returns {String} The show hide target
     */
    function getShowHideTarget(element) {
        return element.dataset["cmpEmbedDialogEditShowhidetarget"];
    }

    /**
     * Gets the show hide target value for the passed element.
     *
     * @param {Element} element The element from which to get the show hide target value
     * @returns {String} The show hide target value
     */
    function getShowHideTargetValue(element) {
        return element.dataset["cmpEmbedDialogEditShowhidetargetvalue"];
    }

    /**
     * Gets any registered foundation field selectors from the foundation registry.
     *
     * @returns {String} comma-separated foundation field selectors
     */
    function getFoundationFieldSelectors() {
        var fieldSelectors = registry.get("foundation.adapters").filter(function(adapter) {
            return adapter.type === "foundation-field" || adapter.type === "foundation-field-mixed";
        }).map(function(adapter) {
            return adapter.selector;
        });
        return fieldSelectors.join(",");
    }

    /**
     * Validates the URL field (on the client-side).
     */
    function validateUrlField() {
        var api = $(urlField).adaptTo("foundation-validation");
        api.checkValidity();
        api.updateUI();
        updateUrlStatus();
    }

    /**
     * Sets a field as valid and updates the validation UI.
     *
     * @param {jQuery} $element The field to set as valid
     */
    function setFieldValid($element) {
        var field = $element.adaptTo("foundation-field");
        var validation = $element.adaptTo("foundation-validation");
        if (field && validation) {
            if (typeof field.setInvalid === "function") {
                field.setInvalid(false);
            }
            validation.checkValidity();
            validation.updateUI();
        }
    }

    /**
     * Updates the URL status message and toggles it, indicating a match for a processor.
     */
    function updateUrlStatus() {
        if (urlStatus) {
            var provider = urlValidation.getProvider();
            var toggleable = $(urlStatus).adaptTo("foundation-toggleable");
            if (provider && urlValidation.isValidUrl()) {
                var capitalized = provider.charAt(0).toUpperCase() + provider.slice(1);
                urlStatus.innerText = Granite.I18n.get(capitalized + " URL can be processed.");
                toggleable.show();
            } else {
                urlStatus.innerText = "";
                toggleable.hide();
            }
        }
    }

    /**
     * Checks whether the URL has a valid format.
     *
     * @param {String} url The url to validate
     * @returns {Boolean} true if the URL has a valid format, false otherwise
     */
    function isUrl(url) {
        // Matches all strings that seem to have a proper URL scheme - e.g. starting with http://, https://, mailto:, tel:
        var pattern = /^([^/]+:|\/\/).*$/;
        return pattern.test(url);
    }

})(document, Granite.$, Coral);
