/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
(function(document, window, $) {
    "use strict";

    var contentLoaded = false;
    $(document).on("foundation-contentloaded", function(e) {
        if (contentLoaded) {
            return;
        }

        var documentCloudSuffix = "/settings/cloudconfigs/documentcloud/";
        var $wizardForm = $("#documentcloud-cloudconfig-form");
        var $propertiesForm = $("#documentcloud-properties-form");

        if ($propertiesForm.length !== 0) {
            $("#documentcloud-cloudconfiguration-name").prop("disabled", true);
        }

        var configAlreadyExists = function(configName, configContainer) {
            var result = $.ajax({
                type: "GET",
                async: false,
                url: Granite.HTTP.externalize(encodeURI(configContainer) + ".1.json"),
                cache: false
            });

            if (result.status !== 200) {
                return false;
            }

            if (result.responseText !== null && result.responseText !== "") {
                var nodeList = JSON.parse(result.responseText);
                for (var i in nodeList) {
                    if (configName === i) {
                        return true;
                    }
                }
            }

            return false;
        };

        var displayError = function(errorMessage) {
            var $errorElement = $("#documentcloud-cloudconfiguration-error-message");
            $errorElement.text(errorMessage);

            $("#documentcloud-common-dialog").adaptTo("foundation-toggleable").show();
            $errorElement.removeAttr("hidden");
            $("#documentcloud-cloudconfiguration-already-exist-message").attr("hidden");
        };

        var submitForm = function($form, url) {
            $.ajax({
                type: $form.prop("method"),
                url: url,
                data: $form.serialize(),
                cache: false,
                contentType: $form.prop("enctype")
            }).done(function(data, textStatus, jqXHR) {
                var configurationContainer = url.substring(url.indexOf("/conf/"), url.indexOf("/settings/"));
                window.location.href =
                    Granite.HTTP.externalize("/mnt/overlay/core/wcm/cloudservices/documentcloud.html" + configurationContainer);
            }).fail(function(jqXHR, textStatus, errorThrown) {
                displayError(errorThrown);
            });
        };

        var isRestricted = function(charCode, keyCode) {
            if (charCode === 0 && (keyCode === 8 || keyCode === 9 || keyCode > 36 && keyCode < 47)) { //37-46 insert/delete/arrow keys
                return false;
            }
            if ((charCode > 47 && charCode < 58) ||  // 0-9 digits
                (charCode > 64 && charCode < 91) ||  // A-Z
                (charCode > 96 && charCode < 123) ||  // a-z
                (charCode === 45) || (charCode === 95)) { // "-" and "_"
                return false;
            } else {
                return true;
            }
        };

        var replaceRestrictedCodes = function(value) {
            if (value) {
                return value.replace(/[^0-9a-z-_]/ig, "-");
            }
        };

        contentLoaded = true;

        $(document).on("keypress", "#documentcloud-cloudconfiguration-name", function(e) {
            if (isRestricted(e.charCode, e.keyCode)) {
                e.preventDefault();
            }
        });

        $(document).on("input change", "#documentcloud-cloudconfiguration-title", function(event) {
            var configName = $("#documentcloud-cloudconfiguration-name");
            var altered = configName.data("altered");
            var title = replaceRestrictedCodes($(this).val()).toLowerCase();

            configName.data("title-value", title);

            if (!altered && !configName.attr("disabled")) {
                configName.val(title);
                configName.trigger("change");
            }
        });

        $(document).on("input", "#documentcloud-cloudconfiguration-name", function(event) {
            var value = $(this).val();
            var title = $(this).data("title-value");

            $(this).val(replaceRestrictedCodes(value));

            $(this).data("altered", title !== value);
        });

        $("#documentcloud-cloudconfig-form-create-button").on("click", function(e) {
            e.preventDefault();
            e.stopPropagation();

            var configName = $("#documentcloud-cloudconfiguration-name").val();
            if (configAlreadyExists(configName, $wizardForm.prop("action") + documentCloudSuffix)) {
                $("#documentcloud-common-dialog").adaptTo("foundation-toggleable").show();
                $("#documentcloud-cloudconfiguration-already-exist-message").removeAttr("hidden");
                $("#documentcloud-cloudconfiguration-error-message").attr("hidden");
                return;
            }

            // ajax request to save the Site Key and Secret Key.
            var submitUrl = Granite.HTTP.externalize($wizardForm.prop("action") + documentCloudSuffix + configName + "/jcr:content");
            submitForm($wizardForm, submitUrl);
        });

        $(".granite-form-saveactivator").on("click", function(e) {
            e.preventDefault();
            e.stopPropagation();

            var helper = $propertiesForm.adaptTo("foundation-validation-helper");
            if (helper.isValid()) {
                var submitUrl = Granite.HTTP.externalize($propertiesForm.prop("action"));
                submitForm($propertiesForm, submitUrl);
            } else {
                helper.getSubmittables().forEach(function(field) {
                    $(field).adaptTo("foundation-validation").updateUI();
                });
                displayError(Granite.I18n.get("There are errors on the form"));
            }
        });

        $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
            selector: "[data-foundation-validation~='documentcloud.cloud.config'],[data-validation~='documentcloud.cloud.config']",
            validate: function(element) {
                var length = element.value.length;

                if (length === 0) {
                    return Granite.I18n.get("The field is mandatory");
                } else {
                    return;
                }
            }
        });
    });
})(document, window, Granite.$);
