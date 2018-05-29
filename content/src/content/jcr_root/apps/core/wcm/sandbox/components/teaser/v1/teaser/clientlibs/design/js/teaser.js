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
    var titleHiddenCheckboxSelector = 'coral-checkbox[name="./titleHidden"]';
    var titleLinkHiddenCheckboxSelector = 'coral-checkbox[name="./titleLinkHidden"]';
    var titleTypeSelectSelector = '.coral-Form-fieldwrapper:has(coral-select[name="./titleType"])';

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $titleHiddenCheckbox = $dialogContent.find(titleHiddenCheckboxSelector);
            if ($titleHiddenCheckbox.size() > 0) {
                var titleHidden = $titleHiddenCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, titleTypeSelectSelector, !titleHidden);
                toggle($dialogContent, titleLinkHiddenCheckboxSelector, !titleHidden);

                $titleHiddenCheckbox.on("change", function(event) {
                    var titleHidden = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, titleTypeSelectSelector, !titleHidden);
                    toggle($dialogContent, titleLinkHiddenCheckboxSelector, !titleHidden);
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
