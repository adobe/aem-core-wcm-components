/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/* global jQuery */
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-container__design";
    var colorHiddenCheckboxSelector = 'coral-checkbox[name="./colorsDisabled"]';
    var colorPropertyHiddenCheckboxSelector = 'coral-checkbox[name="./propertyDisabled"]';
    var colorMultifieldSelector = ".cmp-container__design-multifield";
    var swatchesListLabelSelector = ".coral-Form-fieldlabel-swatcheslist";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $colorHiddenCheckbox = $dialogContent.find(colorHiddenCheckboxSelector);
            if ($colorHiddenCheckbox.size() > 0) {
                var colorHidden = $colorHiddenCheckbox.adaptTo("foundation-field").getValue() === "false";
                toggle($dialogContent, colorMultifieldSelector, !colorHidden);
                toggle($dialogContent, colorPropertyHiddenCheckboxSelector, !colorHidden);
                toggle($dialogContent, swatchesListLabelSelector, !colorHidden);

                $colorHiddenCheckbox.on("change", function(event) {
                    var colorHidden = $(event.target).adaptTo("foundation-field").getValue() === "false";
                    toggle($dialogContent, colorMultifieldSelector, !colorHidden);
                    toggle($dialogContent, colorPropertyHiddenCheckboxSelector, !colorHidden);
                    toggle($dialogContent, swatchesListLabelSelector, !colorHidden);
                });
            }
        }
    });

    function toggle(dialog, selector, show) {
        var $target = dialog.find(selector);
        if ($target) {
            if (show) {
                $target.show();
            } else {
                $target.hide();
            }
        }
    }

})(jQuery);
