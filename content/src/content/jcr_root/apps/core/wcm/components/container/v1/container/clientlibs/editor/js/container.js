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
        dialogContent: ".cmp-container__design",
        backgroundColorEnabled: 'coral-checkbox[name="./backgroundColorEnabled"]',
        backgroundSwatchesOnly: 'coral-checkbox[name="./backgroundSwatchesOnly"]',
        colorMultifield: ".cmp-container__design-multifield",
        swatchesListLabel: ".coral-Form-fieldlabel-swatcheslist"
    };

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(selectors.dialogContent);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $backgroundColorEnabledCheckbox = $dialogContent.find(selectors.backgroundColorEnabled);
            if ($backgroundColorEnabledCheckbox.size() > 0) {
                var backgroundColorEnabled = $backgroundColorEnabledCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, selectors.colorMultifield, backgroundColorEnabled);
                toggle($dialogContent, selectors.backgroundSwatchesOnly, backgroundColorEnabled);
                toggle($dialogContent, selectors.swatchesListLabel, backgroundColorEnabled);

                $backgroundColorEnabledCheckbox.on("change", function(event) {
                    var backgroundColorEnabled = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, selectors.colorMultifield, backgroundColorEnabled);
                    toggle($dialogContent, selectors.backgroundSwatchesOnly, backgroundColorEnabled);
                    toggle($dialogContent, selectors.swatchesListLabel, backgroundColorEnabled);
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
