/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

	var dialog;
	var container;

	var polarisConfiguration;
	var polarisToken = {};
	var registered = false;

	//  TODO: this might be not the right event to register
	$(document).ready(function() {
		dialog = $("#dlg__polaris-picker");
		if (dialog.length === 0) {
			dialog = new Coral.Dialog().set({
				id: 'dlg__polaris-picker',
				content: {
					innerHTML: "<div id=\"polaris-picker-dialog-body\" class=\"pdb__polaris-picker\"></div>"
				},
				fullscreen: false
			});
			document.body.appendChild(dialog);
			$("#polaris-picker-dialog-body").parent().addClass("pdb__polaris-picker");
		}
		container = $(dialog).find("#polaris-picker-dialog-body");
	});

	$(document).on("click", ".btn__polaris-picker", function(e) {
		var origin = window.location.origin;
		var pathname = window.location.pathname;
		if (!pathname.startsWith("/content")) {
			pathname = pathname.substr(pathname.indexOf("/content"), pathname.length);
		}
		var pathnameWithoutExtension = pathname.substr(0, pathname.lastIndexOf("."));
		var polarisConfigUrl = origin + pathnameWithoutExtension + ".polaris-integration-config.json";
		console.log(polarisConfigUrl);
		$.get(polarisConfigUrl, function(data, status) {
			console.log(data);
			if (polarisConfiguration != data) {
			    registered = false;
			}
			polarisConfiguration = JSON.parse(data);
			registerAssetSelectorsIms();
			renderAssetSelectorInline();
		});
	});

	$(document).on("click", ".cq-FileUpload-clear", function(e) {
        var polarisImage = $("input[name=\"./polarisImage\"]");
        polarisImage.val("");
	});

	function registerAssetSelectorsIms() {
	    if (registered) {
	        return;
        }
		var imsClient = "exc_app";
		var imsProps = {
			imsClientId: imsClient,
			imsScope: "additional_info.projectedProductContext,openid,read_organizations", // Polaris Search service, needs `read_organizations` scope to determin the IMS groups the user is part of
			redirectUrl: window.location.href,
			env: polarisConfiguration.imsEnv,
			modalMode: false,
			adobeImsOptions: {
				modalSettings: {
					allowOrigin: window.location.origin
				},
				useLocalStorage: true
			},
			onImsServiceInitialized: (service) => {
				console.log("IMS SUSI: onImsServiceInitialized", service);
				registered = true;
			},
			onAccessTokenReceived: (token) => {
				console.log("IMS SUSI: onAccessTokenReceived", token);
				polarisToken = token;
			},
			onAccessTokenExpired: () => {
				console.log("IMS SUSI: onAccessTokenExpired");
			},
			onErrorReceived: (type, msg) => {
				console.log("IMS SUSI: onErrorReceived", type, msg);
			}
		};
		console.log("registerAssetSelectorsIms");
		console.log("imsProps: " + JSON.stringify(imsProps, null, 2));
		PureJSSelectors.registerAssetSelectorsIms(imsProps);
	}

	function renderAssetSelectorInline() {
		const assetSelectorProps = {
			"discoveryURL": polarisConfiguration.discoveryUrl,
			"repositoryId": polarisConfiguration.repositoryId,
			"apiKey": polarisConfiguration.apiKey,
			"orgName": "AEM Cloud Demos",
			"imsOrg": polarisConfiguration.imsOrg,
			"imsToken": polarisToken.token,
			"env": polarisConfiguration.env,
			"imsEnv": polarisConfiguration.imsEnv,
			handleSelection,
			onClose,
			hideTreeNav: true,
			acvConfig: {
				selectionType: "single"
			}
		};
		var assetSelectorContainer = container;
		console.log("assetSelectorProps: " + JSON.stringify(assetSelectorProps, null, 2));
		PureJSSelectors.renderAssetSelectorWithIms(container.get(0), assetSelectorProps, () => {
			console.log("Done!");
			dialog.show();
		});
		return false;
	}

	function handleSelection(selection) {
		console.log("Selected asset: ", selection);

		var selectedAsset = selection[0];
		var height = selectedAsset.height;
		var width = selectedAsset.width;
		var assetId = selectedAsset["repo:assetId"];
		var name = selectedAsset.name;
		var url = "https://" + polarisConfiguration.repositoryId + "/adobe/dynamicmedia/deliver/" + assetId + "/" + name + "?size=" + width + "," + height + "&preferWebP=true";

		var img = $(".cq-FileUpload-thumbnail-img").children();
		img.attr("src", url);
		img.attr("alt", name);
		img.attr("title", url);
        var polarisImage = $("input[name=\"./polarisImage\"]");
		polarisImage.val(url);
		var fileNameInput = $("input[name=\"./fileName\"]");
        fileNameInput.val("");
        var fileReferenceInput = $("input[name=\"./fileReference\"]");
        fileReferenceInput.val("");
	}

	function onClose() {
		dialog.hide();
	}

})(jQuery, Granite);
