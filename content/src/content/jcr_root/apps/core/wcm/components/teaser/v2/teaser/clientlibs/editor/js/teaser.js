/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
(function($, Granite) {
    "use strict";

    var dialogContentSelector = ".cmp-teaser__editor";
    var actionsMultifieldSelector = ".cmp-teaser__editor-multifield_actions";
    var titleCheckboxSelector = 'coral-checkbox[name="./titleFromPage"]';
    var titleTextfieldSelector = 'input[name="./jcr:title"]';
    var descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionFromPage"]';
    var descriptionTextfieldSelector = '.cq-RichText-editable[name="./jcr:description"]';
    var linkURLSelector = '[name="./linkURL"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var titleTuple;
    var descriptionTuple;
    var linkURL;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $descriptionTextfield = $(descriptionTextfieldSelector);
            if ($descriptionTextfield.length) {
                var rteInstance = $descriptionTextfield.data("rteinstance");
                // wait for the description textfield rich text editor to signal start before initializing.
                // Ensures that any state adjustments made here will not be overridden.
                if (rteInstance && rteInstance.isActive) {
                    init(e, $dialog, $dialogContent, dialogContent);
                } else {
                    $descriptionTextfield.on("editing-start", function() {
                        init(e, $dialog, $dialogContent, dialogContent);
                    });
                }
            } else {
                // init without description field
                init(e, $dialog, $dialogContent, dialogContent);
            }
        }
    });

    // Initialize all fields once both the dialog and the description textfield RTE have loaded
    function init(e, $dialog, $dialogContent, dialogContent) {
        titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector, false);
        descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector, true);
        retrievePageInfo($dialogContent);

        var $linkURLField = $dialogContent.find(linkURLSelector);
        if ($linkURLField.length) {
            linkURL = $linkURLField.adaptTo("foundation-field").getValue();
            $linkURLField.on("change", function() {
                linkURL = $linkURLField.adaptTo("foundation-field").getValue();
                retrievePageInfo($dialogContent);
            });
        }

        var $actionsMultifield = $dialogContent.find(actionsMultifieldSelector);
        $actionsMultifield.on("change", function(event) {
            var $target = $(event.target);
            if ($target.is("foundation-autocomplete")) {
                updateText($target);
            }
            retrievePageInfo($dialogContent);
        });
    }

    function retrievePageInfo(dialogContent) {
        var url;
        if (linkURL === undefined || linkURL === "") {
            url = dialogContent.find('.cmp-teaser__editor-multifield_actions [data-cmp-teaser-v2-dialog-edit-hook="actionLink"]').val();
        } else {
            url = linkURL;
        }
        // get the info from the current page in case no link is provided.
        if ((url === undefined || url === "") && (Granite.author && Granite.author.page)) {
            url = Granite.author.page.path;
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
            var textField = target.parents("coral-multifield-item").find('[data-cmp-teaser-v2-dialog-edit-hook="actionTitle"]');
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
})(jQuery, Granite);
