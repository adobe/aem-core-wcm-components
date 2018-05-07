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

    var dialogContentSelector = ".cmp-teaser__design";
    var hideTitleCheckboxSelector = 'coral-checkbox[name="./hideTitle"]';
    var hideTitleLinkCheckboxSelector = 'coral-checkbox[name="./hideTitleLink"]';
    var hideDescriptionCheckboxSelector = 'coral-checkbox[name="./hideDescription"]';
    var hideDescriptionLinkCheckboxSelector = 'coral-checkbox[name="./hideDescriptionLink"]';
    var titleTypeSelectSelector = '.coral-Form-fieldwrapper:has(coral-select[name="./titleType"])';

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $hideTitleCheckbox = $dialogContent.find(hideTitleCheckboxSelector);
            if ($hideTitleCheckbox.size() > 0) {
                var hideTitle = $hideTitleCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, titleTypeSelectSelector, !hideTitle);
                toggle($dialogContent, hideTitleLinkCheckboxSelector, !hideTitle);

                $hideTitleCheckbox.on("change", function(event) {
                    var hideTitle = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, titleTypeSelectSelector, !hideTitle);
                    toggle($dialogContent, hideTitleLinkCheckboxSelector, !hideTitle);
                });
            }

            var $hideDescriptionCheckbox = $dialogContent.find(hideDescriptionCheckboxSelector);
            if ($hideDescriptionCheckbox.size() > 0) {
                var hideDescription = $hideDescriptionCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, hideDescriptionLinkCheckboxSelector, !hideDescription);

                $hideDescriptionCheckbox.on("change", function(event) {
                    var hideDescription = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, hideDescriptionLinkCheckboxSelector, !hideDescription);
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
