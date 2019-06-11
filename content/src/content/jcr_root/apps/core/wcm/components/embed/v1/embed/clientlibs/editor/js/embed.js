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
/* global Granite, Coral */
(function(document, $, Coral) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-embed__editor",
        embeddable: "[data-cmp-embed-dialog-edit-hook='embeddable']",
        type: "[data-cmp-embed-dialog-edit-hook='type']",
        typeRadio: "[data-cmp-embed-dialog-edit-hook='type'] coral-radio"
    };

    var registry = $(window).adaptTo("foundation-registry");

    var embeddable;
    var type;
    var typeRadios;
    var foundationFieldSelectors;

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

})(document, Granite.$, Coral);
