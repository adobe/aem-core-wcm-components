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
    var HTML_CLASS_URL_PROVIDER_MESSAGE = "cmp-embed-dialog-edit-url-provider-message";
    var HTML_CLASS_CORAL_FIELD_LABEL = "coral-Form-fieldlabel";
    var HTML_TAG_LABEL = "label";

    var selectors = {
        dialogContent: ".cmp-embed__editor",
        embeddable: "[data-cmp-embed-dialog-edit-hook='embeddable']",
        type: "[data-cmp-embed-dialog-edit-hook='type']",
        typeRadio: "[data-cmp-embed-dialog-edit-hook='type'] coral-radio",
        urlField: "[data-cmp-embed-dialog-edit-hook='url']",
        urlProviderMessage: "." + HTML_CLASS_URL_PROVIDER_MESSAGE
    };

    var registry = $(window).adaptTo("foundation-registry");

    var embeddable;
    var type;
    var typeRadios;
    var foundationFieldSelectors;
    var urlField;

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
                var embedResourcePath = el.dataset[DATA_ATTR_EMBED_RESOURCE_PATH];
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
            displayUrlProviderInfo();
            if (!urlValidation.isValidUrl()) {
                return Granite.I18n.get(urlValidation.getErrorMessage());
            }
        }
    });

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                embeddable = dialogContent.querySelector(selectors.embeddable);
                type = dialogContent.querySelector(selectors.type);
                typeRadios = type.querySelectorAll(selectors.typeRadio);
                foundationFieldSelectors = getFoundationFieldSelectors();
                urlField = dialogContent.querySelector(selectors.urlField);

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

                        Coral.commons.ready(urlField, function(element) {
                            validateUrlField();
                        });
                    }
                }
            }
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
     * Validates the URL field (on the client-side).
     */
    function validateUrlField() {
        var api = $(urlField).adaptTo("foundation-validation");
        api.checkValidity();
        api.updateUI();
        displayUrlProviderInfo();
    }

    /**
     * Displays a message below the URL field informing about the URL provider name.
     */
    function displayUrlProviderInfo() {
        var div = urlField.parentNode.querySelector(selectors.urlProviderMessage);
        if (!div) {
            div = document.createElement(HTML_TAG_LABEL);
            div.classList.add(HTML_CLASS_CORAL_FIELD_LABEL);
            div.classList.add(HTML_CLASS_URL_PROVIDER_MESSAGE);
            urlField.parentNode.insertBefore(div, urlField.nextSibling);
        }
        if (urlValidation.isValidUrl()) {
            div.innerHTML = Granite.I18n.get("This URL will be processed by a " + urlValidation.getProvider() + " provider.");
        } else {
            div.innerHTML = "";
        }
    }

    /**
     * Checks whether the URL is valid.
     * @param {String} url The url to validate
     * @returns {Boolean} true if the URL is valid, false otherwise
     */
    function isUrl(url) {
        // Matches all strings that seem to have a proper URL scheme - e.g. starting with http://, https://, mailto:, tel:
        var pattern = /^([^/]+:|\/\/).*$/;
        return pattern.test(url);
    }

})(document, Granite.$, Coral);
