/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

    var dialogContentSelector = ".cmp-image__editor";
    var $dialogContent;
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var isDecorative;
    var altTuple;
    var captionTuple;
    var $altGroup;
    var $altTextField;
    var $linkURLGroup;
    var $linkURLField;
    var firstCtaLinkFieldSelector = ".cmp-teaser__editor-multifield_actions coral-multifield-item:first foundation-autocomplete";
    var $firstCtaLinkField;
    var $cqFileUpload;
    var $cqFileUploadEdit;
    var $dynamicMediaGroup;
    var areDMFeaturesEnabled;
    var fileReference;
    var presetTypeSelector = ".cmp-image__editor-dynamicmedia-presettype";
    var imagePresetDropDownSelector = ".cmp-image__editor-dynamicmedia-imagepreset";
    var smartCropRenditionDropDownSelector = ".cmp-image__editor-dynamicmedia-smartcroprendition";
    var imagePropertiesRequest;
    var imagePath;
    var smartCropRenditionFromJcr;
    var smartCropRenditionsDropDown;
    var imageFromPageImage;
    var altFromPageTuple;
    var $pageImageThumbnail;
    var altTextFromPage;
    var altTextFromDAM;
    var altCheckboxSelector = "coral-checkbox[name='./altValueFromDAM']";
    var altInputSelector = "input[name='./alt']";
    var altInputAlertIconSelector = "input[name='./alt'] + coral-icon[icon='alert']";
    var assetTabSelector = "coral-tab[data-foundation-tracking-event*='asset']";
    var assetTabAlertIconSelector = "coral-tab[data-foundation-tracking-event*='asset'] coral-icon[icon='alert']";
    var pageAltCheckboxSelector = "coral-checkbox[name='./cq:featuredimage/altValueFromDAM']";
    var pageAltInputSelector = "input[name='./cq:featuredimage/alt']";
    var pageImageThumbnailSelector = ".cq-page-image-thumbnail";
    var pageImageThumbnailImageSelector = ".cq-page-image-thumbnail__image";
    var pageImageThumbnailConfigPathAttribute = "data-thumbnail-config-path";
    var pageImageThumbnailComponentPathAttribute = "data-thumbnail-component-path";
    var pageImageThumbnailCurrentPagePathAttribute = "data-thumbnail-current-page-path";

    $(document).on("dialog-loaded", function(e) {
        altTextFromPage = undefined;
        altTextFromDAM = undefined;
        var $dialog        = e.dialog;
        $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            isDecorative = dialogContent.querySelector('coral-checkbox[name="./isDecorative"]');

            if ($(pageAltCheckboxSelector).length === 1) {
                // when the tuple is used in the page dialog to define the featured image
                altTuple = new CheckboxTextfieldTuple(dialogContent, pageAltCheckboxSelector, pageAltInputSelector);
            } else {
                // when the tuple is used in the image dialog
                altTuple = new CheckboxTextfieldTuple(dialogContent, altCheckboxSelector, altInputSelector);
            }

            $altGroup = $dialogContent.find(".cmp-image__editor-alt");
            $altTextField = $dialogContent.find(".cmp-image__editor-alt-text");
            $linkURLGroup = $dialogContent.find(".cmp-image__editor-link");
            $linkURLField = $dialogContent.find('foundation-autocomplete[name="./linkURL"]');
            captionTuple = new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./titleValueFromDAM"]', 'input[name="./jcr:title"]');
            $cqFileUpload = $dialog.find(".cmp-image__editor-file-upload");
            $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
            $dynamicMediaGroup = $dialogContent.find(".cmp-image__editor-dynamicmedia");
            $dynamicMediaGroup.hide();
            areDMFeaturesEnabled = ($dynamicMediaGroup.length === 1);
            if (areDMFeaturesEnabled) {
                smartCropRenditionsDropDown = $dynamicMediaGroup.find(smartCropRenditionDropDownSelector).get(0);
            }

            imageFromPageImage = dialogContent.querySelector("coral-checkbox[name='./imageFromPageImage']");

            altFromPageTuple = new CheckboxTextfieldTuple(dialogContent, "coral-checkbox[name='./altValueFromPageImage']", "input[name='./alt']");
            $pageImageThumbnail = $dialogContent.find(pageImageThumbnailSelector);
            altTextFromPage = $dialogContent.find(pageImageThumbnailImageSelector).attr("alt");

            if ($cqFileUpload.length) {
                imagePath = $cqFileUpload.data("cqFileuploadTemporaryfilepath").slice(0, $cqFileUpload.data("cqFileuploadTemporaryfilepath").lastIndexOf("/"));
                retrieveInstanceInfo(imagePath);
                $cqFileUpload.on("assetselected", function(e) {
                    fileReference = e.path;
                    retrieveDAMInfo(fileReference).then(
                        function() {
                            if (isDecorative) {
                                altTuple.hideCheckbox(isDecorative.checked);
                            }
                            captionTuple.hideCheckbox(false);
                            altTuple.reinitCheckbox();
                            captionTuple.reinitCheckbox();
                            toggleAlternativeFieldsAndLink(imageFromPageImage, isDecorative);
                            if (areDMFeaturesEnabled) {
                                selectPresetType($(presetTypeSelector), "imagePreset");
                                resetSelectField($dynamicMediaGroup.find(smartCropRenditionDropDownSelector));
                            }
                        }
                    );
                });
                $cqFileUpload.on("click", "[coral-fileupload-clear]", function() {
                    altTuple.reset();
                    captionTuple.reset();
                });
                $cqFileUpload.on("coral-fileupload:fileadded", function() {
                    if (isDecorative) {
                        altTuple.hideTextfield(isDecorative.checked);
                    }
                    altTuple.hideCheckbox(true);
                    captionTuple.hideTextfield(false);
                    captionTuple.hideCheckbox(true);
                    fileReference = undefined;
                });
            }
            if ($cqFileUploadEdit) {
                fileReference = $cqFileUploadEdit.data("cqFileuploadFilereference");
                if (fileReference === "") {
                    fileReference = undefined;
                }
                if (fileReference) {
                    retrieveDAMInfo(fileReference);
                } else {
                    altTuple.hideCheckbox(true);
                    captionTuple.hideCheckbox(true);
                }
            }
            toggleAlternativeFieldsAndLink(imageFromPageImage, isDecorative);
            togglePageImageInherited(imageFromPageImage, isDecorative);
            updateImageThumbnail();
        }

        $(window).adaptTo("foundation-registry").register("foundation.validation.selector", {
            submittable: ".cmp-image__editor-alt-text",
            candidate: ".cmp-image__editor-alt-text",
            exclusion: ".cmp-image__editor-alt-text *"
        });

        improveAltTextValidation();
    });

    $(window).on("focus", function() {
        if (fileReference) {
            retrieveDAMInfo(fileReference);
        }
    });

    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: altInputSelector,
        validate: function() {
            var seededValue = $(altInputSelector).attr("data-seeded-value");
            var isAltCheckboxChecked = $(altCheckboxSelector).attr("checked");
            var assetWithoutDescriptionErrorMessage = "Error: Please provide an asset which has a description that can be used as alt text.";
            if (isAltCheckboxChecked && !seededValue) {
                return Granite.I18n.get(assetWithoutDescriptionErrorMessage);
            }
        }
    });

    $(document).on("dialog-beforeclose", function() {
        $(window).off("focus");
    });

    $(document).on("change", dialogContentSelector + " coral-checkbox[name='./isDecorative']", function(e) {
        toggleAlternativeFieldsAndLink(imageFromPageImage, e.target);
    });

    $(document).on("change", dialogContentSelector + " coral-checkbox[name='./imageFromPageImage']", function(e) {
        togglePageImageInherited(e.target, isDecorative);
    });

    // Update the image thumbnail when the link field is updated
    $(document).on("change", dialogContentSelector + " foundation-autocomplete[name='./linkURL']", function(e) {
        updateImageThumbnail();
    });

    // Update the image thumbnail when the calls to action are updated, removed or reordered
    $(document).on("change", dialogContentSelector + " .cmp-teaser__editor-multifield_actions", function(e) {
        updateImageThumbnail();
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

    function updateImageThumbnail() {
        var linkValue;
        var thumbnailConfigPath = $(dialogContentSelector).find(pageImageThumbnailSelector).attr(pageImageThumbnailConfigPathAttribute);
        var thumbnailComponentPath = $(dialogContentSelector).find(pageImageThumbnailSelector).attr(pageImageThumbnailComponentPathAttribute);
        $firstCtaLinkField = $dialogContent.find(firstCtaLinkFieldSelector);
        if ($linkURLField && $linkURLField.adaptTo("foundation-field") && $linkURLField.adaptTo("foundation-field").getValue()) {
            linkValue = $linkURLField.adaptTo("foundation-field").getValue();
        } else if ($firstCtaLinkField && $firstCtaLinkField.adaptTo("foundation-field") && $firstCtaLinkField.adaptTo("foundation-field").getValue()) {
            linkValue = $firstCtaLinkField.adaptTo("foundation-field").getValue();
        }
        if (linkValue === undefined || linkValue === "") {
            linkValue = $(dialogContentSelector).find(pageImageThumbnailSelector).attr(pageImageThumbnailCurrentPagePathAttribute);
        }

        // Get the updated page image thumbnail HTML from the server
        return $.ajax({
            url: thumbnailConfigPath + ".html" + thumbnailComponentPath,
            data: {
                "pageLink": linkValue
            }
        }).done(function(data) {
            if (data) {

                // update the thumbnail image
                $pageImageThumbnail.replaceWith(data);
                $pageImageThumbnail = $(dialogContentSelector).find(pageImageThumbnailSelector);
                if (imageFromPageImage && imageFromPageImage.checked) {
                    $pageImageThumbnail.show();
                } else {
                    $pageImageThumbnail.hide();
                }

                // update the alt field
                altTextFromPage = $(dialogContentSelector).find(pageImageThumbnailImageSelector).attr("alt");
                if (imageFromPageImage.checked) {
                    altFromPageTuple.seedTextValue(altTextFromPage);
                    altFromPageTuple.update();
                }
            }
        });
    }

    function togglePageImageInherited(checkbox, isDecorative) {
        if (checkbox) {
            toggleAlternativeFields(checkbox, isDecorative);
            if (checkbox.checked) {
                $cqFileUpload.hide();
                $pageImageThumbnail.show();
            } else {
                $cqFileUpload.show();
                $pageImageThumbnail.hide();
            }
        }
    }

    function toggleAlternativeFields(fromPageCheckbox, isDecorativeCheckbox) {
        if (fromPageCheckbox && isDecorativeCheckbox) {
            if (isDecorativeCheckbox.checked) {
                $altGroup.hide();
                altTuple.hideTextfield(true);
                altTuple.hideCheckbox(true);
                altFromPageTuple.hideTextfield(true);
                altFromPageTuple.hideCheckbox(true);
            } else {
                $altGroup.show();
                altTuple.hideTextfield(false);
                altTuple.hideCheckbox(fromPageCheckbox.checked);
                altFromPageTuple.hideCheckbox(!fromPageCheckbox.checked);
                if (fromPageCheckbox.checked) {
                    altFromPageTuple.seedTextValue(altTextFromPage);
                    altFromPageTuple.update();
                } else {
                    altTuple.seedTextValue(altTextFromDAM);
                    altTuple.update();
                }
            }
        } else {
            $altGroup.show();
            altTuple.hideTextfield(false);
            altTuple.hideCheckbox(false);
            altTuple.seedTextValue(altTextFromDAM);
            altTuple.update();
        }
    }

    function toggleAlternativeFieldsAndLink(fromPageCheckbox, isDecorativeCheckbox) {
        if (fromPageCheckbox && isDecorativeCheckbox) {
            if (isDecorativeCheckbox.checked) {
                $linkURLGroup.hide();
            } else {
                $linkURLGroup.show();
            }
            var $imageLinkURLField = $linkURLGroup.find('foundation-autocomplete[name="./linkURL"]');
            if ($imageLinkURLField.length) {
                $imageLinkURLField.adaptTo("foundation-field").setDisabled(isDecorativeCheckbox.checked);
            }
            if ($altTextField.length) {
                $altTextField.adaptTo("foundation-field").setRequired(!isDecorativeCheckbox.checked && $("coral-fileupload.is-filled:not(:hidden)").length !== 0);
            }
        }
        toggleAlternativeFields(fromPageCheckbox, isDecorativeCheckbox);
    }

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + "/_jcr_content/metadata.json"
        }).done(function(data) {
            if (data) {
                if (altTuple) {
                    altTextFromDAM = data["dc:description"];
                    if (altTextFromDAM === undefined || altTextFromDAM.trim() === "") {
                        altTextFromDAM = data["dc:title"];
                    }
                    altTuple.seedTextValue(altTextFromDAM);
                    altTuple.update();
                    toggleAlternativeFieldsAndLink(imageFromPageImage, isDecorative);
                }
                if (captionTuple) {
                    var title = data["dc:title"];
                    captionTuple.seedTextValue(title);
                    captionTuple.update();
                }
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

    /**
     * Improve error validation for alternative text inherited from asset's description
     */
    function improveAltTextValidation() {
        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                if (mutation.type === "attributes") {
                    var isAltCheckboxChecked = $(altCheckboxSelector).attr("checked");
                    var alertIcon = $(altInputAlertIconSelector);
                    var assetTab = $(assetTabSelector);
                    var assetTabAlertIcon = $(assetTabAlertIconSelector);
                    if (mutation.attributeName === "data-seeded-value") {
                        if (isAltCheckboxChecked) {
                            if ($(altInputSelector).val()) {
                                if (alertIcon.length) {
                                    $(altInputSelector).removeClass("is-invalid");
                                    alertIcon.hide();
                                    assetTab.removeClass("is-invalid");
                                    assetTabAlertIcon.hide();
                                }
                            } else {
                                if (alertIcon.length) {
                                    $(altInputSelector).addClass("is-invalid");
                                    alertIcon.show();
                                    assetTab.addClass("is-invalid");
                                    assetTabAlertIcon.show();
                                }
                            }
                        }
                    }

                    if (mutation.attributeName === "disabled") {
                        if ($(altInputSelector).val()) {
                            if (alertIcon.length) {
                                $(altInputSelector).removeClass("is-invalid");
                                alertIcon.hide();
                                assetTab.removeClass("is-invalid");
                                assetTabAlertIcon.hide();
                            }
                        }
                    }

                    if (mutation.attributeName === "invalid") {
                        if (!$(altInputSelector).val()) {
                            if (alertIcon.length) {
                                $(altInputSelector).addClass("is-invalid");
                                alertIcon.show();
                                assetTab.addClass("is-invalid");
                                assetTabAlertIcon.show();
                            }
                        }
                    }
                }
            });
        });

        var altInput = document.querySelector(altInputSelector);
        if (altInput) {
            observer.observe(altInput, {
                attributeFilter: ["data-seeded-value", "disabled", "invalid"]
            });
        }
    }

})(jQuery, Granite);
