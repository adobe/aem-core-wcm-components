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
/* global Granite, Coral */
(function(document, $, Coral) {
    "use strict";

    var URL_VALIDATION_GET_SUFFIX = ".urlProcessor.json";
    var DATA_ATTR_EMBED_RESOURCE_PATH = "cmpEmbedDialogEditResourcePath";

    var selectors = {
        dialogContent: ".cmp-embed__editor",
        embeddable: "[data-cmp-embed-dialog-edit-hook='embeddable']",
        type: "[data-cmp-embed-dialog-edit-hook='type']",
        typeRadio: "[data-cmp-embed-dialog-edit-hook='type'] coral-radio",
        urlField: "[data-cmp-embed-dialog-edit-hook='url']"
    };

    var registry = $(window).adaptTo("foundation-registry");

    var embeddable;
    var type;
    var typeRadios;
    var foundationFieldSelectors;

    // URL field validation object
    var urlValidation = new function() {

        var validation = {};

        this.getElement = function() {
            return validation.el;
        };

        this.getUrl = function() {
            return validation.url;
        };

        this.isValidUrl = function() {
            return validation.isValid;
        };

        this.getErrorMessage = function() {
            return validation.errorMessage;
        };

        this.setElement = function(el) {
            validation.el = el;
            this.setUrl(el.value);
        };

        this.setUrl = function(url) {
            validation.url = url;
        };

        this.setValid = function(isValid) {
            validation.isValid = isValid;
        };

        this.setErrorMessage = function(errorMessage) {
            validation.errorMessage = errorMessage;
        };

        this.reset = function() {
            validation = {};
        };

        this.isDone = function() {
            return !isEmpty(validation);
        };

        // Performs the server-side validation and executes the callback
        this.perform = function(callback) {
            var that = this;
            var embedResourcePath = that.getElement().dataset[DATA_ATTR_EMBED_RESOURCE_PATH];
            var requestUrl = embedResourcePath + URL_VALIDATION_GET_SUFFIX + "?url=" + that.getUrl();
            var request = new XMLHttpRequest();
            request.open("GET", requestUrl, true);
            request.onload = function() {
                if (request.status === 200) {
                    that.setValid(true);
                } else if (request.status === 404) {
                    that.setValid(false);
                    that.setErrorMessage("This embed URL is not supported");
                } else {
                    that.setValid(false);
                    that.setErrorMessage("A problem occurred when validating the URL");
                }
                callback(that.getElement());
            };
            request.send();
        };

    };

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                embeddable = dialogContent.querySelector(selectors.embeddable);
                type = dialogContent.querySelector(selectors.type);
                typeRadios = type.querySelectorAll(selectors.typeRadio);
                foundationFieldSelectors = getFoundationFieldSelectors();

                if (typeRadios.length) {
                    for (var i = 0; i < typeRadios.length; i++) {
                        var typeRadio = typeRadios[i];

                        Coral.commons.ready(typeRadio, function(element) {
                            var value = element.value;
                            var showHideTarget = getShowHideTarget(type);

                            if (element.checked) {
                                toggleShowHideTargets(showHideTarget, value);
                            }

                            element.on("change", function() {
                                toggleShowHideTargets(showHideTarget, value);
                            });
                        });

                        Coral.commons.ready(embeddable, function(element) {
                            var showHideTarget = getShowHideTarget(element);

                            toggleShowHideTargets(showHideTarget, element.value);

                            element.on("change", function() {
                                toggleShowHideTargets(showHideTarget, element.value);
                            });
                        });
                    }
                }
            }
        }
    });

    // Registers a validator for the URL field
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: selectors.urlField,
        validate: function(el) {
            var errorMessage;
            if (urlValidation.isDone()) {
                if (el.value === urlValidation.getUrl()) {
                    if (!urlValidation.isValidUrl()) {
                        errorMessage = urlValidation.getErrorMessage();
                    }
                } else {
                    errorMessage = "A problem occurred when validating the URL";
                }
                urlValidation.reset();
            } else {
                urlValidation.setElement(el);
                urlValidation.perform(validateUIElement);
            }
            if (errorMessage) {
                return Granite.I18n.get(errorMessage);
            }
            return null;
        }
    });

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
     * Toggles a dialog toggle target, setting its disabled state and visibility.
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
            if (field) {
                field.setDisabled(false);
            }
        } else {
            if (toggleable) {
                toggleable.hide();
            } else {
                $element.hide();
            }
            if (field) {
                field.setDisabled(true);
            }
        }
        toggleTargetChildren($element, show);
    }

    /**
     * Toggles child fields of a dialog toggle target, setting their disabled states.
     *
     * @param {jQuery} $element The target
     * @param {Boolean} show true to disable and hide the target, false otherwise
     */
    function toggleTargetChildren($element, show) {
        var $childFoundationFields = $element.find(foundationFieldSelectors);
        $childFoundationFields.each(function(index, element) {
            var field = $(element).adaptTo("foundation-field");
            if (field) {
                if (show) {
                    field.setDisabled(false);
                } else {
                    field.setDisabled(true);
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
     * Checks whether the object is empty.
     * @param {Object} obj
     * @returns {Boolean} true if the object is empty, false otherwise
     */
    function isEmpty(obj) {
        for (var key in obj) {
            if (obj.hasOwnProperty(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Triggers the client-side element validation.
     * @param {String} el
     */
    function validateUIElement(el) {
        var api = $(el).adaptTo("foundation-validation");
        api.checkValidity();
        api.updateUI();
    }

})(document, Granite.$, Coral);
