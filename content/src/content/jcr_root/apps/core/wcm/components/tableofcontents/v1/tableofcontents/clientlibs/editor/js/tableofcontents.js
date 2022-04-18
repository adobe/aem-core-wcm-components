/*******************************************************************************
 * Copyright 2022 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function($, Granite) {
    "use strict";

    var START_LEVEL_ERROR_MESSAGE = "Start level is higher than stop level";
    var STOP_LEVEL_ERROR_MESSAGE = "Stop level is smaller than start level";

    var selectors = {
        edit: {
            startLevel: "input[name='./startLevel']",
            stopLevel: "input[name='./stopLevel']",
            restrictedStartLevel: "input[name='./restrictedStartLevel']",
            restrictedStopLevel: "input[name='./restrictedStopLevel']"
        },
        design: {
            restrictStartLevel: "input[name='./restrictStartLevel']",
            restrictStopLevel: "input[name='./restrictStopLevel']"
        }
    };

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: ".cmp-toc__editor .cmp-toc__validate[name='./startLevel']",
        validate: function() {
            return validateStartStopLevels(START_LEVEL_ERROR_MESSAGE);
        },
        show: showError,
        clear: clearError
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: ".cmp-toc__editor .cmp-toc__validate[name='./stopLevel']",
        validate: function() {
            return validateStartStopLevels(STOP_LEVEL_ERROR_MESSAGE);
        },
        show: showError,
        clear: clearError
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: ".cmp-toc__design .cmp-toc__validate[name='./restrictStartLevel']",
        validate: function() {
            return validateRestrictStartStopLevels(START_LEVEL_ERROR_MESSAGE);
        },
        show: showError,
        clear: clearError
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: ".cmp-toc__design .cmp-toc__validate[name='./restrictStopLevel']",
        validate: function() {
            return validateRestrictStartStopLevels(STOP_LEVEL_ERROR_MESSAGE);
        },
        show: showError,
        clear: clearError
    });

    function validateStartStopLevels(errorMessage) {
        var restrictedStartLevel = $(selectors.edit.restrictedStartLevel)[0].value;
        var restrictedStopLevel = $(selectors.edit.restrictedStopLevel)[0].value;
        var startLevel = restrictedStartLevel === "norestriction"
            ? $(selectors.edit.startLevel)[0].value
            : restrictedStartLevel;
        var stopLevel = restrictedStopLevel === "norestriction"
            ? $(selectors.edit.stopLevel)[0].value
            : restrictedStopLevel;
        if (startLevel > stopLevel) {
            return Granite.I18n.get(errorMessage);
        }
    }

    function validateRestrictStartStopLevels(errorMessage) {
        var restrictStartLevel = $(selectors.design.restrictStartLevel)[0].value;
        var restrictStopLevel = $(selectors.design.restrictStopLevel)[0].value;
        if (restrictStartLevel !== "norestriction" && restrictStopLevel !== "norestriction" &&
                restrictStartLevel > restrictStopLevel) {
            return Granite.I18n.get(errorMessage);
        }
    }

    function showError(element, message) {
        var $element = $(element);
        var fieldAPI = $element.adaptTo("foundation-field");
        if (fieldAPI && fieldAPI.setInvalid) {
            fieldAPI.setInvalid(true);
        }
        var error = $element.data("foundation-validation.internal.error");
        if (error) {
            if (!error.parentNode) {
                $element.after(error);
                error.show();
            }
        } else {
            error = new Coral.Tooltip().set({
                content: {
                    innerHTML: message
                },
                variant: "error",
                interaction: "off",
                placement: "bottom",
                target: element,
                open: true,
                id: Coral.commons.getUID()
            });
            $element.data("foundation-validation.internal.error", error);
            $element.after(error);
        }
    }

    function clearError() {
        $(".cmp-toc__validate").each(function() {
            var $element = $(this);
            var fieldAPI = $element.adaptTo("foundation-field");
            if (fieldAPI && fieldAPI.setInvalid) {
                fieldAPI.setInvalid(false);
            }
            var error = $element.data("foundation-validation.internal.error");
            error && error.remove();
        });
    }

})(jQuery, Granite);

