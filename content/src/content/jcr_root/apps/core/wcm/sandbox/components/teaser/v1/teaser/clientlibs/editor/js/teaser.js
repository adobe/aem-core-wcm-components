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

    var dialogContentSelector = ".cmp-teaser__editor";
    var withCTACheckboxSelector = 'coral-checkbox[name="./withCTA"]';
    var ctasMultifieldSelector = ".cmp-teaser__editor-multifield_ctas";
    var titleCheckboxSelector = 'coral-checkbox[name="./titleValueFromPage"]';
    var titleTextfieldSelector = 'input[name="./jcr:title"]';
    var descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionValueFromPage"]';
    var descriptionTextfieldSelector = 'textarea[name="./jcr:description"]';
    var linkURLWrapperSelector = ".cmp-teaser__editor-link-url";
    var linkURLSelector = '[name="./linkURL"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var withCTA;
    var titleTuple;
    var descriptionTuple;
    var linkURL;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector);
            descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector);

            var $linkURLField = $dialogContent.find(linkURLSelector);
            linkURL = $linkURLField.adaptTo("foundation-field").getValue();
            $linkURLField.on("change", function() {
                linkURL = $linkURLField.adaptTo("foundation-field").getValue();
                retrievePageInfo($dialogContent);
            });

            var $withCTACheckbox = $dialogContent.find(withCTACheckboxSelector);
            if ($withCTACheckbox.size() > 0) {
                withCTA = $withCTACheckbox.adaptTo("foundation-field").getValue() === "true";
                $withCTACheckbox.on("change", function(e) {
                    withCTA = $(e.target).adaptTo("foundation-field").getValue() === "true";
                    toggleInputs($dialogContent);
                    retrievePageInfo($dialogContent);
                });

                var $ctasMultifield = $dialogContent.find(ctasMultifieldSelector);
                $ctasMultifield.on("change", function(event) {
                    var $target = $(event.target);
                    if ($target.is("foundation-autocomplete")) {
                        updateText($target);
                    }
                    retrievePageInfo($dialogContent);
                });
            }

            toggleInputs($dialogContent);
            retrievePageInfo($dialogContent);
        }
    });

    function toggleInputs(dialogContent) {
        var $linkURLWrapper = dialogContent.find(linkURLWrapperSelector);
        var $ctasMultifield = dialogContent.find(ctasMultifieldSelector);
        if (withCTA) {
            $linkURLWrapper.hide();
            $ctasMultifield.show();
            if ($ctasMultifield.size() > 0) {
                var ctasMultifield = $ctasMultifield[0];
                if (ctasMultifield.items.length < 1) {
                    var newMultifieldItem = new Coral.Multifield.Item();
                    ctasMultifield.items.add(newMultifieldItem);
                    Coral.commons.ready(newMultifieldItem, function(element) {
                        var linkField = $(element).find('foundation-autocomplete[name="link"]');
                        if (linkField) {
                            linkField.val(linkURL);
                            linkField.trigger("change");
                        }
                    });
                }
            }
        } else {
            $linkURLWrapper.show();
            $ctasMultifield.hide();
        }
    }

    function retrievePageInfo(dialogContent) {
        var url;
        if (withCTA) {
            url = dialogContent.find('.cmp-teaser__editor-multifield_ctas [name="link"]').val();
        } else {
            url = linkURL;
        }
        if (url && url.startsWith("/")) {
            return $.ajax({
                url: url + "/_jcr_content.json"
            }).done(function(data) {
                if (data) {
                    titleTuple.seedTextValue(data["jcr:title"]);
                    titleTuple.update();
                    descriptionTuple.seedTextValue(data["jcr:description"]);
                    descriptionTuple.update();
                }
            });
        } else {
            titleTuple.update();
            descriptionTuple.update();
        }
    }

    function updateText(target) {
        var url = target.val();
        if (url && url.startsWith("/")) {
            var textField = target.parents("coral-multifield-item").find('[name="text"]');
            if (textField && !textField.val()) {
                $.ajax({
                    url: url + "/_jcr_content.json"
                }).done(function(data) {
                    if (data) {
                        textField.val(data["jcr:title"]);
                    }
                });
            }
        }
    }
})(jQuery);
