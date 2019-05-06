/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

	var selectors = {
		dialogContent = ".cmp-container__design",
		colorHiddenCheckbox = 'coral-checkbox[name="./isColorsDisabled"]',
		colorPropertyHiddenCheckbox = 'coral-checkbox[name="./propertyDisabled"]',
		colorMultifield = ".cmp-container__design-multifield",
		swatchesListLabel = ".coral-Form-fieldlabel-swatcheslist"
    };

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(selectors.dialogContent);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $colorHiddenCheckbox = $dialogContent.find(selectors.colorHiddenCheckbox);
            if ($colorHiddenCheckbox.size() > 0) {
                var colorHidden = $colorHiddenCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, selectors.colorMultifield, !colorHidden);
                toggle($dialogContent, selectors.colorPropertyHiddenCheckbox, !colorHidden);
                toggle($dialogContent, selectors.swatchesListLabel, !colorHidden);

                $colorHiddenCheckbox.on("change", function(event) {
                    var colorHidden = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, selectors.colorMultifield, !colorHidden);
                    toggle($dialogContent, selectors.colorPropertyHiddenCheckbox, !colorHidden);
                    toggle($dialogContent, selectors.swatchesListLabel, !colorHidden);
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
