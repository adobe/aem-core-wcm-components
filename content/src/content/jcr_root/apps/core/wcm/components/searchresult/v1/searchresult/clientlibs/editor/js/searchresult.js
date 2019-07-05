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

    var dialogContentSelector = ".cmp-searchresult-editDialog";
    var facetHiddenCheckboxSelector = 'coral-checkbox[name="./enableFacet"]';
    var facetTitleSelector = '.coral-Form-fieldwrapper:has(input[name="./facetTitle"])';
    var facetTagPropertySelector = '.coral-Form-fieldwrapper:has(input[name="./tagProperty"])';
    var facetTagsSelector = '.coral-Form-fieldwrapper:has(span[data-property-path="./cq:tags"])';

    var sortHiddenCheckboxSelector = 'coral-checkbox[name="./enableSort"]';
    var sortMultifieldSelector = '.coral-Form-fieldwrapper:has(coral-multifield[data-granite-coral-multifield-name="./sortItems"])';

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $facetHiddenCheckbox = $dialogContent.find(facetHiddenCheckboxSelector);
            var $sortHiddenCheckbox = $dialogContent.find(sortHiddenCheckboxSelector);
            if ($facetHiddenCheckbox.size() > 0) {
                var facetTitleHidden = $facetHiddenCheckbox.adaptTo("foundation-field").getValue() !== "true";
                toggle($dialogContent, facetTitleSelector, !facetTitleHidden);
                toggle($dialogContent, facetTagPropertySelector, !facetTitleHidden);
                toggle($dialogContent, facetTagsSelector, !facetTitleHidden);

                $facetHiddenCheckbox.on("change", function(event) {
                    var facetTitleHidden = $(event.target).adaptTo("foundation-field").getValue() !== "true";
                    toggle($dialogContent, facetTitleSelector, !facetTitleHidden);
                    toggle($dialogContent, facetTagPropertySelector, !facetTitleHidden);
                    toggle($dialogContent, facetTagsSelector, !facetTitleHidden);
                });
            }
            if ($sortHiddenCheckbox.size() > 0) {
                var sortMultifieldHidden = $sortHiddenCheckbox.adaptTo("foundation-field").getValue() !== "true";
                toggle($dialogContent, sortMultifieldSelector, !sortMultifieldHidden);

                $sortHiddenCheckbox.on("change", function(event) {
                    var sortMultifieldHidden = $(event.target).adaptTo("foundation-field").getValue() !== "true";
                    toggle($dialogContent, sortMultifieldSelector, !sortMultifieldHidden);
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
