/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
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
/* global
    Granite, Coral
 */
(function(document, $, Coral) {
    "use strict";
    var dialogContentSelector = ".cmp-teaser__editor";
    var linkchecherEnabled;
    var linkcheckerEnabledCheckboxSelector = 'coral-checkbox input[name="./linkTrackingEnabled"]';

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var $linkEnabledCheckbox = $dialog.find(linkcheckerEnabledCheckboxSelector);

        if ($linkEnabledCheckbox) {
            if ($linkEnabledCheckbox.prop("checked") === true) {
                $dialog.find('[name="./objectId"]').parent().removeClass("hide");
            } else {
                $dialog.find('[name="./objectId"]').parent().addClass("hide");
            }

        }
        $linkEnabledCheckbox.on("click", function(e) {
            $dialog.find('[name="./objectId"]').parent().toggleClass("hide");
        });
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            if ($linkEnabledCheckbox.size() > 0) {
                linkchecherEnabled = $linkEnabledCheckbox.adaptTo("foundation-field").getValue() === "true";
                if (!linkchecherEnabled) {
                    $dialogContent.find('[name="./objectId"]').parent().hide();
                }
            }
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

    function showHide(component, target) {
        var value = component.value;
        $(target).not(".hide").addClass("hide");
        $(target).filter("[data-showhidetargetvalue='" + value + "']").removeClass("hide");
    }

})(document, Granite.$, Coral);

(function($) {
    "use strict";
    var dialogContentSelector = ".cmp-image__editor";
    var linkchecherEnabled;
    var linkcheckerEnabledCheckboxSelector = 'coral-checkbox[name="./linkTrackingEnabled"]';
    $(document).on("dialog-loaded", function(e) {
        var $dialog        = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            var $linkEnabledCheckbox = $dialogContent.find(linkcheckerEnabledCheckboxSelector);
            if ($linkEnabledCheckbox.size() > 0) {
                linkchecherEnabled = $linkEnabledCheckbox.adaptTo("foundation-field").getValue() === "true";
                if (!linkchecherEnabled) {
                    $dialogContent.find('[name="./objectId"]').parent().hide();
                }
            }
            $linkEnabledCheckbox.on("change", function(e) {
                $dialogContent.find('[name="./objectId"]').parent().toggle();
            });
        }
    });


})(jQuery);

