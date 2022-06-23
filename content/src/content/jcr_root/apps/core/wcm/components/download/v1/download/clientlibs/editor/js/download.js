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
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-download__editor";
    var titleCheckboxSelector = 'coral-checkbox[name="./titleFromAsset"]';
    var titleTextfieldSelector = 'input[name="./jcr:title"]';
    var descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionFromAsset"]';
    var descriptionTextfieldSelector = '.cq-RichText-editable[name="./jcr:description"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var titleTuple;
    var descriptionTuple;
    var fileReference;
    var $cqFileUpload;
    var $cqFileUploadEdit;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {

            var rteInstance = $(descriptionTextfieldSelector).data("rteinstance");
            // wait for the description textfield rich text editor to signal start before initializing.
            if (rteInstance && rteInstance.isActive) {
                init($dialog, dialogContent);
            } else {
                $(descriptionTextfieldSelector).on("editing-start", function() {
                    init($dialog, dialogContent);
                });
            }
        }
    });

    // Initialize all fields once both the dialog and the description textfield RTE have loaded
    function init($dialog, dialogContent) {
        titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector, false);
        descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector, true);
        $cqFileUpload     = $dialog.find(".cq-FileUpload");
        $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
        if ($cqFileUpload) {
            $cqFileUpload.on("assetselected", function(e) {
                fileReference = e.path;
                retrieveDAMInfo(fileReference).then(
                    function() {
                        titleTuple.reinitCheckbox();
                        descriptionTuple.reinitCheckbox();
                    }
                );
            });
            $cqFileUpload.on("click", "[coral-fileupload-clear]", function() {
                titleTuple.reset();
                descriptionTuple.reset();
            });
        }
        if ($cqFileUploadEdit) {
            fileReference = $cqFileUploadEdit.data("cqFileuploadFilereference");
            if (fileReference === "") {
                fileReference = undefined;
            }
            if (fileReference) {
                retrieveDAMInfo(fileReference);
            }
        }
    }

    $(window).on("focus", function() {
        if (fileReference) {
            retrieveDAMInfo(fileReference);
        }
    });

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + "/_jcr_content/metadata.json"
        }).done(function(data) {
            if (data) {
                if (descriptionTuple) {
                    var description = data["dc:description"];
                    if (description === undefined || description.trim() === "") {
                        description = data["dc:title"];
                    }
                    descriptionTuple.seedTextValue(description);
                    descriptionTuple.update();
                }
                if (titleTuple) {
                    var title = data["dc:title"];
                    titleTuple.seedTextValue(title);
                    titleTuple.update();
                }
            }
        });
    }
})(jQuery);
