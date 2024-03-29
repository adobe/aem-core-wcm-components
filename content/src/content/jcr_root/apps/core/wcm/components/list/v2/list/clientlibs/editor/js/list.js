/*******************************************************************************
 * Copyright 2016 Adobe
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

    $(document).on("foundation-contentloaded", function(e) {
        $(".cmp-list__editor coral-select.cq-dialog-dropdown-showhide", e.target).each(function(i, element) {
            var target = window.Granite.UI.Foundation.Utils.sanitizeHtml($(element).data("cqDialogDropdownShowhideTarget"));
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
