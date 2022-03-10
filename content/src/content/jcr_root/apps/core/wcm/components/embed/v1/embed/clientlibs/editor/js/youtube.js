/*******************************************************************************
 * Copyright 2022 Adobe
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

    var dialogContent;

    var selectors = {
        dialogContent: ".cmp-embeddable-youtube__editor",
        radioGroup: "[data-cmp-youtube-dialog-edit='radioGroup']",
        radioInput: "[data-cmp-youtube-dialog-edit='radioGroup'] coral-radio",
        layoutOption: "[data-cmp-youtube-dialog-layout-option]"
    };

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                initializeLayoutOptions();
            }
        }
    });

    function initializeLayoutOptions() {
        var radioGroup = dialogContent.querySelector(selectors.radioGroup);
        var radioInputs = radioGroup.querySelectorAll(selectors.radioInput);

        if (radioInputs.length) {
            for (var i = 0; i < radioInputs.length; i++) {
                var radioInput = radioInputs[i];

                Coral.commons.ready(radioInput, function(element) {
                    var value = element.value;

                    if (element.checked) {
                        toggleLayoutOptions(value);
                    }

                    element.on("change", function() {
                        if (element.checked) {
                            toggleLayoutOptions(value);
                        }
                    });
                });
            }
        }
    }

    function toggleLayoutOptions(value) {
        var layoutOptions = document.querySelectorAll(selectors.layoutOption);

        for (var i = 0; i < layoutOptions.length; i++) {
            var optionValue = layoutOptions[i].dataset["cmpYoutubeDialogLayoutOption"];
            toggleLayoutOption($(layoutOptions[i]), optionValue === value);
        }
    }

    function toggleLayoutOption($option, show) {
        var field = $option.adaptTo("foundation-field");
        var option = $option.parent(".foundation-toggleable").adaptTo("foundation-toggleable") || $option;

        option[show ? "show" : "hide"]();

        if (field && typeof field.setDisabled === "function") {
            field.setDisabled(!show);
        }
    }
})(document, Granite.$, Coral);
