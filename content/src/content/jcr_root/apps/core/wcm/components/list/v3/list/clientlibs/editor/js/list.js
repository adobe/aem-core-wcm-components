/*******************************************************************************
 * Copyright 2022 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function(document, $, Coral) {
    "use strict";

    var dialogContentSelector = ".cmp-list__editor";
    var mixedMultifieldSelector = ".cmp-list__editor-mixed-multifield";
    var mixedLinkTextSelector = ".cmp-list__editor-mixed-multifield-linkText";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $mixedMultifield = $dialogContent.find(mixedMultifieldSelector);
            $mixedMultifield.on("change", function(event) {
                var $target = $(event.target);
                if ($target.is("foundation-autocomplete")) {
                    updateLinkText($target);
                    applyExternalLinksMode();
                } else if ($target.is("coral-multifield")) {
                    applyExternalLinksMode();
                }
            });

            $mixedMultifield.find("foundation-autocomplete input[is='coral-textfield']").on("blur", function() {
                applyExternalLinksMode();
            });

            $("coral-select[name='./orderBy']").get(0).addEventListener("coral-select:showitems", function() {
                applyExternalLinksMode();
            });
        }
    });

    $(document).on("foundation-contentloaded", function(e) {
        $(".cmp-list__editor coral-select.cq-dialog-dropdown-showhide", e.target).each(function(i, element) {
            var target = $(element).data("cqDialogDropdownShowhideTarget");
            if (target) {
                Coral.commons.ready(element, function(component) {
                    showHide(component, target);
                    applyExternalLinksMode();
                    component.on("change", function() {
                        showHide(component, target);
                        applyExternalLinksMode();
                    });
                });
            }
        });
        showHide($(".cq-dialog-dropdown-showhide", e.target));
    });

    function updateLinkText(target) {
        var url = target.val();
        if (url && url.startsWith("/")) {
            var textField = target.parents("coral-multifield-item").find(mixedLinkTextSelector);
            if (textField) {
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

    function applyExternalLinksMode() {
        var listFrom = $(".cmp-list__editor coral-select.cq-dialog-dropdown-showhide")[0].value;
        var externals = $(mixedMultifieldSelector).find("foundation-autocomplete").filter(function() {
            var url = this.value;
            return url && !url.startsWith("/");
        });
        if (externals.length === 0 || listFrom !== "mixed") {
            $("coral-select-item[value='modified']").removeClass("hide");
            $("coral-selectlist-item[value='modified']").removeClass("hide");
            $("coral-tab:contains('Item Settings')").removeClass("hide");
        } else {
            $("coral-select-item[value='modified']").not(".hide").addClass("hide");
            $("coral-selectlist-item[value='modified']").not(".hide").addClass("hide");
            $("coral-tab:contains('Item Settings')").not(".hide").addClass("hide");
        }
    }

    function showHide(component, target) {
        var value = component.value;
        if (!value) {
            return;
        }

        $(target).not(".hide").addClass("hide");
        $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
        if ("mixed" === value) {
            $("coral-numberinput[name='./maxItems']").closest(".coral-Form-fieldwrapper").not(".hide").addClass("hide");
        } else {
            $("coral-numberinput[name='./maxItems']").closest(".coral-Form-fieldwrapper").removeClass("hide");
        }
    }
})(document, Granite.$, Coral);
