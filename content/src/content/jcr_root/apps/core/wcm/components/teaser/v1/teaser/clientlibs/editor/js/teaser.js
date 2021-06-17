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
    var linkURLSelector = '[name="./linkURL"]';
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var actionsEnabled;
    var titleTuple;
    var descriptionTuple;
    var linkURL;

    var presetTypeSelector = ".cmp-image__editor-dynamicmedia-presettype";
    var imagePresetDropDownSelector = ".cmp-image__editor-dynamicmedia-imagepreset";
    var smartCropRenditionDropDownSelector = ".cmp-image__editor-dynamicmedia-smartcroprendition";
    var $dynamicMediaGroup;
    var areDMFeaturesEnabled;
    var smartCropRenditionFromJcr;
    var smartCropRenditionsDropDown;
    var $cqFileUploadEdit;
    var $cqFileUpload;
    var imagePath;
    var fileReference;
    var imagePropertiesRequest;

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {

            $cqFileUpload = $dialog.find(".cq-FileUpload");
            $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
            $dynamicMediaGroup = $dialogContent.find(".cmp-image__editor-dynamicmedia");
            $dynamicMediaGroup.hide();
            areDMFeaturesEnabled = ($dynamicMediaGroup.length === 1);
            if (areDMFeaturesEnabled) {
                smartCropRenditionsDropDown = $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).get(0);
            }

            if ($cqFileUpload) {
                imagePath = $cqFileUpload.data("cqFileuploadTemporaryfilepath").slice(0, $cqFileUpload.data("cqFileuploadTemporaryfilepath").lastIndexOf("/"));
                retrieveInstanceInfo(imagePath);
                $cqFileUpload.on("assetselected", function(e) {
                    fileReference = e.path;
                    retrieveDAMInfo(fileReference).then(
                        function() {
                            if (areDMFeaturesEnabled) {
                                selectPresetType($(presetTypeSelector), "imagePreset");
                                resetSelectField($dynamicMediaGroup.find(smartCropRenditionDropDownSelector));
                            }
                        }
                    );
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

            var rteInstance = $(descriptionTextfieldSelector).data("rteinstance");
            // wait for the description textfield rich text editor to signal start before initializing.
            // Ensures that any state adjustments made here will not be overridden.
            if (rteInstance && rteInstance.isActive) {
                init(e, $dialog, $dialogContent, dialogContent);
            } else {
                $(descriptionTextfieldSelector).on("editing-start", function() {
                    init(e, $dialog, $dialogContent, dialogContent);
                });
            }
        }
    });

    $(window).on("focus", function() {
        if (fileReference) {
            retrieveDAMInfo(fileReference);
        }
    });

    $(document).on("change", dialogContentSelector + " " + presetTypeSelector, function(e) {
        switch (e.target.value) {
            case "imagePreset":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().show();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                resetSelectField($dynamicMediaGroup.find(smartCropRenditionDropDownSelector));
                break;
            case "smartCrop":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().hide();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().show();
                resetSelectField($dynamicMediaGroup.find(imagePresetDropDownSelector));
                break;
            default:
                break;
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
                if ($target.is("coral-multifield") && event.target.items && event.target.items.length === 0) {
                    actionsEnabled = false;
                    $actionsEnabledCheckbox.adaptTo("foundation-field").setValue(false);
                    toggleInputs($dialogContent);
                } else if ($target.is("foundation-autocomplete")) {
                    updateText($target);
                }
                retrievePageInfo($dialogContent);
            });
        }
        toggleInputs($dialogContent);
    }

    function toggleInputs(dialogContent) {
        var $actionsMultifield = dialogContent.find(actionsMultifieldSelector);
        var linkURLField = dialogContent.find(linkURLSelector).adaptTo("foundation-field");
        var actions = $actionsMultifield.adaptTo("foundation-field");
        if (linkURLField && actions) {
            if (actionsEnabled) {
                linkURLField.setDisabled(true);
                actions.setDisabled(false);
                if ($actionsMultifield.size() > 0) {
                    var actionsMultifield = $actionsMultifield[0];
                    if (actionsMultifield.items.length < 1) {
                        var newMultifieldItem = new Coral.Multifield.Item();
                        actionsMultifield.items.add(newMultifieldItem);
                        Coral.commons.ready(newMultifieldItem, function(element) {
                            var linkField = $(element).find("[data-cmp-teaser-v1-dialog-edit-hook='actionLink']");
                            if (linkField) {
                                linkField.val(linkURL);
                                linkField.trigger("change");
                            }
                        });
                    } else {
                        toggleActionItems($actionsMultifield, false);
                    }
                }
            } else {
                linkURLField.setDisabled(false);
                actions.setDisabled(true);
                toggleActionItems($actionsMultifield, true);
            }
        }
    }

    function toggleActionItems(actionsMultifield, disabled) {
        actionsMultifield.find("coral-multifield-item").each(function(ix, item) {
            var linkField = $(item).find("[data-cmp-teaser-v1-dialog-edit-hook='actionLink']").adaptTo("foundation-field");
            var textField = $(item).find("[data-cmp-teaser-v1-dialog-edit-hook='actionTitle']").adaptTo("foundation-field");
            if (disabled && linkField.getValue() === "" && textField.getValue() === "") {
                actionsMultifield[0].items.remove(item);
            }
            linkField.setDisabled(disabled);
            textField.setDisabled(disabled);
        });
    }

    function retrievePageInfo(dialogContent) {
        var url;
        if (actionsEnabled) {
            url = dialogContent.find('.cmp-teaser__editor-multifield_actions [data-cmp-teaser-v1-dialog-edit-hook="actionLink"]').val();
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
            var textField = target.parents("coral-multifield-item").find('[data-cmp-teaser-v1-dialog-edit-hook="actionTitle"]');
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

    /**
     * Helper function to get core image instance 'smartCropRendition' property
     * @param filePath
     */
    function retrieveInstanceInfo(filePath) {
        return $.ajax({
            url: filePath + ".json"
        }).done(function(data) {
            if (data) {
                // we need to get saved value of 'smartCropRendition' of Core Image component
                smartCropRenditionFromJcr = data["smartCropRendition"];
            }
        });
    }

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + "/_jcr_content/metadata.json"
        }).done(function(data) {
            if (data) {
                // show or hide "DynamicMedia section" depending on whether the file is DM
                var isFileDM = data["dam:scene7File"];
                if (isFileDM === undefined || isFileDM.trim() === "" || !areDMFeaturesEnabled) {
                    $dynamicMediaGroup.hide();
                } else {
                    $dynamicMediaGroup.show();
                    getSmartCropRenditions(data["dam:scene7File"]);
                }
            }
        });
    }

    /**
     * Get the list of available image's smart crop renditions and fill drop-down list
     * @param imageUrl The link to image asset
     */
    function getSmartCropRenditions(imageUrl) {
        if (imagePropertiesRequest) {
            imagePropertiesRequest.abort();
        }
        imagePropertiesRequest = new XMLHttpRequest();
        var url = window.location.origin + "/is/image/" + imageUrl + "?req=set,json";
        imagePropertiesRequest.open("GET", url, true);
        imagePropertiesRequest.onload = function() {
            if (imagePropertiesRequest.status >= 200 && imagePropertiesRequest.status < 400) {
                // success status
                var responseText = imagePropertiesRequest.responseText;
                var rePayload = new RegExp(/^(?:\/\*jsonp\*\/)?\s*([^()]+)\(([\s\S]+),\s*"[0-9]*"\);?$/gmi);
                var rePayloadJSON = new RegExp(/^{[\s\S]*}$/gmi);
                var resPayload = rePayload.exec(responseText);
                var payload;
                if (resPayload) {
                    var payloadStr = resPayload[2];
                    if (rePayloadJSON.test(payloadStr)) {
                        payload = JSON.parse(payloadStr);
                    }

                }
                // check "relation" - only in case of smartcrop renditions
                if (payload !== undefined && payload.set.relation && payload.set.relation.length > 0) {
                    if (smartCropRenditionsDropDown.items) {
                        smartCropRenditionsDropDown.items.clear();
                    }
                    // we need to add "NONE" item first in the list
                    addSmartCropDropDownItem("NONE", "", true);
                    // "AUTO" would trigger automatic smart crop operation; also we need to check "AUTO" was chosed in previous session
                    addSmartCropDropDownItem("Auto", "SmartCrop:Auto", (smartCropRenditionFromJcr === "SmartCrop:Auto"));
                    for (var i = 0; i < payload.set.relation.length; i++) {
                        smartCropRenditionsDropDown.items.add({
                            content: {
                                innerHTML: payload.set.relation[i].userdata.SmartCropDef
                            },
                            disabled: false,
                            selected: (smartCropRenditionFromJcr === payload.set.relation[i].userdata.SmartCropDef)
                        });
                    }
                    $dynamicMediaGroup.find(presetTypeSelector).parent().show();
                } else {
                    $dynamicMediaGroup.find(presetTypeSelector).parent().hide();
                    selectPresetType($(presetTypeSelector), "imagePreset");
                }
                prepareSmartCropPanel();
            } else {
                // error status
            }
        };
        imagePropertiesRequest.send();
    }

    /**
     * Helper function for populating dropdown list
     */
    function addSmartCropDropDownItem(label, value, selected) {
        smartCropRenditionsDropDown.items.add({
            content: {
                innerHTML: label,
                value: value
            },
            disabled: false,
            selected: selected
        });
    }

    /**
     * Helper function to show/hide UI-elements of dialog depending on the chosen radio button
     */
    function prepareSmartCropPanel() {
        var presetType = getSelectedPresetType($(presetTypeSelector));
        switch (presetType) {
            case undefined:
                selectPresetType($(presetTypeSelector), "imagePreset");
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                break;
            case "imagePreset":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().show();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().hide();
                break;
            case "smartCrop":
                $dynamicMediaGroup.find(imagePresetDropDownSelector).parent().hide();
                $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).parent().show();
                break;
            default:
                break;
        }
    }

    /**
     * Get selected radio option helper
     * @param component The radio option component
     * @returns {String} Value of the selected radio option
     */
    function getSelectedPresetType(component) {
        var radioComp = component.find('[type="radio"]');
        for (var i = 0; i < radioComp.length; i++) {
            if ($(radioComp[i]).prop("checked")) {
                return $(radioComp[i]).val();
            }
        }
        return undefined;
    }

    /**
     * Select radio option helper
     * @param component
     * @param val
     */
    function selectPresetType(component, val) {
        var radioComp = component.find('[type="radio"]');
        radioComp.each(function() {
            $(this).prop("checked", ($(this).val() === val));
        });
    }

    /**
     * Reset selection field
     * @param field
     */
    function resetSelectField(field) {
        if (field[0]) {
            field[0].clear();
        }
    }

})(jQuery);
