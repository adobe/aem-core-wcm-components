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
    var actionsEnabledCheckboxSelector = 'coral-checkbox[name="./actionsEnabled"]';
    var actionsMultifieldSelector = ".cmp-teaser__editor-multifield_actions";
    var titleCheckboxSelector = 'coral-checkbox[name="./titleFromPage"]';
    var titleTextfieldSelector = 'input[name="./jcr:title"]';
    var descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionFromPage"]';
    var descriptionTextfieldSelector = '.cq-RichText-editable[name="./jcr:description"]';
    var linkURLWrapperSelector = ".cmp-teaser__editor-link-url";
    var linkURLSelector = '[name="./linkURL"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var actionsEnabled;
    var titleTuple;
    var descriptionTuple;
    var linkURL;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector);
            descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector, true);

            var $linkURLField = $dialogContent.find(linkURLSelector);
            linkURL = $linkURLField.adaptTo("foundation-field").getValue();
            $linkURLField.on("change", function() {
                linkURL = $linkURLField.adaptTo("foundation-field").getValue();
                retrievePageInfo($dialogContent);
            });

            var $actionsEnabledCheckbox = $dialogContent.find(actionsEnabledCheckboxSelector);
            if ($actionsEnabledCheckbox.size() > 0) {
                actionsEnabled = $actionsEnabledCheckbox.adaptTo("foundation-field").getValue() === "true";
                $actionsEnabledCheckbox.on("change", function(e) {
                    actionsEnabled = $(e.target).adaptTo("foundation-field").getValue() === "true";
                    toggleInputs($dialogContent);
                    retrievePageInfo($dialogContent);
                });

                var $actionsMultifield = $dialogContent.find(actionsMultifieldSelector);
                $actionsMultifield.on("change", function(event) {
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
        var $actionsMultifield = dialogContent.find(actionsMultifieldSelector);
        if (actionsEnabled) {
            $linkURLWrapper.hide();
            $actionsMultifield.show();
            if ($actionsMultifield.size() > 0) {
                var actionsMultifield = $actionsMultifield[0];
                if (actionsMultifield.items.length < 1) {
                    var newMultifieldItem = new Coral.Multifield.Item();
                    actionsMultifield.items.add(newMultifieldItem);
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
            $actionsMultifield.hide();
        }
    }

    function retrievePageInfo(dialogContent) {
        var url;
        if (actionsEnabled) {
            url = dialogContent.find('.cmp-teaser__editor-multifield_actions [name="link"]').val();
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
