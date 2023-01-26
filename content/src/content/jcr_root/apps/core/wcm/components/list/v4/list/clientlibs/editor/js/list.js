/*******************************************************************************
 * Copyright 2023 Adobe
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
    var staticHiddenPagesSelector = ".cmp-list__editor-static-hidden-pages";
    var staticMultifieldSelector = ".cmp-list__editor-static-multifield";
    var staticLinkTextSelector = ".cmp-list__editor-static-multifield-linkText";
    var staticLinkUrlSelector = ".cmp-list__editor-static-multifield-linkURL";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $staticMultifield = $dialogContent.find(staticMultifieldSelector);
            $staticMultifield.on("change", function(event) {
                var $target = $(event.target);
                if ($target.is("foundation-autocomplete")) {
                    updateLinkText($target);
                }
            });

            handleListV3StaticPages($dialogContent, $staticMultifield);
        }
    });

    $(document).on("foundation-contentloaded", function(e) {
        $(".cmp-list__editor coral-select.cq-dialog-dropdown-showhide", e.target).each(function(i, element) {
            var target = $(element).data("cqDialogDropdownShowhideTarget");
            if (target) {
                Coral.commons.ready(element, function(component) {
                    showHide(component, target);
                    component.on("change", function() {
                        showHide(component, target);
                    });
                });
            }
        });
        showHide($(".cq-dialog-dropdown-showhide", e.target));
    });

    function updateLinkText(target) {
        var url = target.val();
        if (url && url.startsWith("/")) {
            var textField = target.parents("coral-multifield-item").find(staticLinkTextSelector);
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

    function showHide(component, target) {
        var value = component.value;
        if (!value) {
            return;
        }

        $(target).not(".hide").addClass("hide");
        $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
        if ("static" === value) {
            $("coral-numberinput[name='./maxItems']").closest(".coral-Form-fieldwrapper").not(".hide").addClass("hide");
        } else {
            $("coral-numberinput[name='./maxItems']").closest(".coral-Form-fieldwrapper").removeClass("hide");
        }
    }

    // initiate the conversion of old data if available to new format
    function handleListV3StaticPages($dialogContent, $staticMultifield) {
        // old multifield should exist
        var $hiddenMultifield = $dialogContent.find(staticHiddenPagesSelector);
        if (!($hiddenMultifield && $hiddenMultifield.length > 0)) {
            return;
        }

        // old multifield should have items
        var hiddenItems = $hiddenMultifield[0].items.getAll();
        if (hiddenItems.length <= 0) {
            return;
        }

        // new multifield should not have items
        var newItemsList = $staticMultifield[0].items;
        if (newItemsList.length !== 0) {
            return;
        }

        // add an empty item to new multifield for each hidden item
        hiddenItems.map(function() {
            newItemsList.add(new Coral.Multifield.Item());
        });

        // ensure the item template is rendered in the DOM for the new items
        Coral.commons.nextFrame(function() {
            // fill the new multifield items using old multifield items
            var newItems = newItemsList.getAll();
            for (var index in hiddenItems) {
                var autocomplete = $(newItems[index]).find(staticLinkUrlSelector);
                var value = hiddenItems[index].content.querySelector("input[name='./pages']").value;
                // set page path to link url
                autocomplete.adaptTo("foundation-field").setValue(value);
                // trigger change event to autocomplete link text with page title
                $staticMultifield.trigger(jQuery.Event("change", { target: autocomplete }));
            }
            // remove old multifield values resulting in the removal of the old 'pages' property when dialog saved
            $hiddenMultifield[0].items.clear();
        });
    }
})(document, Granite.$, Coral);
