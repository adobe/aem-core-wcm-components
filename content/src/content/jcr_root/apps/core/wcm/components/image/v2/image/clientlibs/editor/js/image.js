/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
/* global jQuery, Coral */
(function($) {
    "use strict";

    var dialogContentSelector = ".cmp-image__editor";
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1;
    var isDecorative;
    var altTuple;
    var captionTuple;
    var $altGroup;
    var $linkURLGroup;
    var $linkURLField;
    var $cqFileUpload;
    var $cqFileUploadEdit;
    var $dynamicMediaGroup;    
    var fileReference;
    var presetTypeRadioGroup = ".cmp-image__editor-dynamicmedia-presettype";
    var imagePresetDropDownList = ".cmp-image__editor-dynamicmedia-imagepreset";
	var smartCropRenditionsDropDownList = ".cmp-image__editor-dynamicmedia-smartcroprenditions";
	var imagePresetRequest;
	var imagePath;
	var smartCropPresetFromJcr;

    $(document).on("dialog-loaded", function(e) {
        var $dialog        = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            isDecorative      = dialogContent.querySelector('coral-checkbox[name="./isDecorative"]');
            altTuple          =
                new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./altValueFromDAM"]', 'input[name="./alt"]');
            $altGroup         = $dialogContent.find(".cmp-image__editor-alt");
            $linkURLGroup     = $dialogContent.find(".cmp-image__editor-link");
            $linkURLField     = $linkURLGroup.find('foundation-autocomplete[name="./linkURL"]');
            captionTuple      =
                new CheckboxTextfieldTuple(dialogContent, 'coral-checkbox[name="./titleValueFromDAM"]', 'input[name="./jcr:title"]');
            $cqFileUpload     = $dialog.find(".cq-FileUpload");
            $cqFileUploadEdit = $dialog.find(".cq-FileUpload-edit");
            $dynamicMediaGroup= $dialogContent.find(".cmp-image__editor-dynamicmedia");

            if ($cqFileUpload) {
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
                            toggleAlternativeFieldsAndLink(isDecorative);
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
            toggleAlternativeFieldsAndLink(isDecorative);
        }

        $(window).adaptTo("foundation-registry").register("foundation.validation.selector", {
            submittable: ".cmp-image__editor-alt-text",
            candidate: ".cmp-image__editor-alt-text:not(:hidden)",
            exclusion: ".cmp-image__editor-alt-text *"
        });
    });

    $(window).on("focus", function() {
        if (fileReference) {
            retrieveDAMInfo(fileReference);
        }
    });

    $(document).on("dialog-beforeclose", function() {
        $(window).off("focus");
    });

    $(document).on("change", dialogContentSelector + ' coral-checkbox[name="./isDecorative"]', function(e) {
        toggleAlternativeFieldsAndLink(e.target);
    });

    $(document).on("change", dialogContentSelector + " " + presetTypeRadioGroup, function(e) {
        if ((e.target).value == "image"){
            $dynamicMediaGroup.find(imagePresetDropDownList).show();
			$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().hide();
			resetSelectField($dynamicMediaGroup.find(smartCropRenditionsDropDownList));
        }
        if ((e.target).value == "smartCropPreset"){
            $dynamicMediaGroup.find(imagePresetDropDownList).hide();
			$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().show();
			resetSelectField($dynamicMediaGroup.find(imagePresetDropDownList));
        }		
        if ((e.target).value == "smartCrop"){
            $dynamicMediaGroup.find(imagePresetDropDownList).hide();
			$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().hide();
			resetSelectField($dynamicMediaGroup.find(imagePresetDropDownList));
			resetSelectField($dynamicMediaGroup.find(smartCropRenditionsDropDownList));
        }
    });

    function toggleAlternativeFieldsAndLink(checkbox) {
        if (checkbox) {
            if (checkbox.checked) {
                $linkURLGroup.hide();
                $altGroup.hide();
            } else {
                $altGroup.show();
                $linkURLGroup.show();
            }
            if ($linkURLField.length) {
                $linkURLField.adaptTo("foundation-field").setDisabled(checkbox.checked);
            }
            altTuple.hideTextfield(checkbox.checked);
            if (fileReference) {
                altTuple.hideCheckbox(checkbox.checked);
            }
        }
    }

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + "/_jcr_content/metadata.json"
        }).done(function(data) {
            if (data) {
                if (altTuple) {
                    var description = data["dc:description"];
                    if (description === undefined || description.trim() === "") {
                        description = data["dc:title"];
                    }
                    altTuple.seedTextValue(description);
                    altTuple.update();
                    toggleAlternativeFieldsAndLink(isDecorative);
                }
                if (captionTuple) {
                    var title = data["dc:title"];
                    captionTuple.seedTextValue(title);
                    captionTuple.update();
                }
                //show or hide "DynamicMedia section" depending on whether the file is DM
                var isFileDM = data["dam:scene7File"];
                if (isFileDM === undefined || isFileDM.trim() === "") {
                    $dynamicMediaGroup.hide();
                }
                else{
                    $dynamicMediaGroup.show();
					getImagePresetList(data["dam:scene7File"]);
                }
            }
        });
    }

    /**
     * Helper function to get core image instance 'smartcroppreset' property
     * @param filePath 
     */
    function retrieveInstanceInfo(filePath) {
        return $.ajax({
            url: filePath + ".json"
        }).done(function(data) {
            if (data) {
                //we need to get saved value of 'smartcroppreset' of Core Image component
               smartCropPresetFromJcr = data["smartcroppreset"];
            }
        });
    }	
	
    /**
     * Get the list of available image's smart crop renditions and fill drop-down list
     * @param imageUrl The link to image asset
     */	
	function getSmartCropRenditions(imageUrl){
		var smartCropRenditionsDropDownItemsList = $dynamicMediaGroup.find(smartCropRenditionsDropDownList).get(0);
		if (imagePresetRequest){
			imagePresetRequest.abort();
		}
		imagePresetRequest = new XMLHttpRequest();
		var url = window.location.origin + "/is/image/" + imageUrl + "?req=set,json";
		imagePresetRequest.open("GET", url, true);
		imagePresetRequest.onload = function() {
			if (imagePresetRequest.status >= 200 && imagePresetRequest.status < 400) {
				// success status
				var responseText = imagePresetRequest.responseText;
				var rePayload = new RegExp(/^(?:\/\*jsonp\*\/)?\s*([^()]+)\(([\s\S]+),\s*"[0-9]*"\);?$/gmi);
				var rePayloadJSON = new RegExp(/^{[\s\S]*}$/gmi);
				var resPayload = rePayload.exec(responseText);
				if (resPayload) {
					var payload;
					var payloadStr = resPayload[2];
					if (rePayloadJSON.test(payloadStr)) {
						payload = JSON.parse(payloadStr);
					}

				}
				//check "relation" - only in case of smartcrop renditions
				if (payload.set.relation && payload.set.relation.length > 0) {
					if (smartCropRenditionsDropDownItemsList.items) {
						smartCropRenditionsDropDownItemsList.items.clear();
					}
					//we need to add "NONE" item first in the list
					smartCropRenditionsDropDownItemsList.items.add({
					  content: {
						innerHTML: "NONE",
						value: ""
					  },
					  disabled: false,
					  selected: true
					});			
					for(var i=0; i < payload.set.relation.length ; i++) {
						smartCropRenditionsDropDownItemsList.items.add({
						  content: {
							innerHTML: payload.set.relation[i].userdata.SmartCropDef
						  },
						  disabled: false,
                          selected: (smartCropPresetFromJcr == payload.set.relation[i].userdata.SmartCropDef)
						});
					}
					prepareSmartCropPanel();
				}
				else {
					$dynamicMediaGroup.find(presetTypeRadioGroup).parent().hide();
					$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().hide();
				}
			} else {
				// error status
			}
		};
		imagePresetRequest.send();   
	}
	
    /**
     * Helper function to show/hide UI-elements of dialog depending on the chosen radio button
     */	
	function prepareSmartCropPanel() {
		var presetType = getSelectedRadio($(presetTypeRadioGroup));
		switch (presetType){
			case undefined:
				selectRadio($(presetTypeRadioGroup), "image");
				break;
			case "image":
				$dynamicMediaGroup.find(imagePresetDropDownList).show();
				$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().hide();
				break;
			case "smartCropPreset":
				$dynamicMediaGroup.find(imagePresetDropDownList).hide();
				$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().show();
				break;
			case "smartCrop":
				$dynamicMediaGroup.find(imagePresetDropDownList).hide();
				$dynamicMediaGroup.find(smartCropRenditionsDropDownList).parent().hide();
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
    function getSelectedRadio(component) {
        var radioComp = component.find('[type="radio"]');
        var val;

        radioComp.each( function(){
            if ($(this).prop('checked')) {
                val = $(this).val();
            }
        });

        return val;
    }

    /**
     * Select radio option helper
     * @param component
     * @param val
     */
    function selectRadio(component, val) {
        var radioComp = component.find('[type="radio"]');
        radioComp.each( function(){
            $(this).prop('checked', ($(this).val() == val));
        });
    }
	
    /**
     * Reset selection field
     * @param field
     */
    function resetSelectField(field) {
        field.find('coral-select-item[selected]').removeAttr('selected');
        field.find('button').find('span').html('NONE');
    }	

})(jQuery);
