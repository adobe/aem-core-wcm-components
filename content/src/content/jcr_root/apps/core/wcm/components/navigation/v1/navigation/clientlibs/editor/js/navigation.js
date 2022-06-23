/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the 'License');
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an 'AS IS' BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
(function($) {
    "use strict";

    var DIALOG_CONTENT_SELECTOR = ".cmp-navigation__editor";
    var COLLECT_ALL_PAGES_SELECTOR = DIALOG_CONTENT_SELECTOR + ' coral-checkbox[name="./collectAllPages"]';
    var STRUCTURE_DEPTH_SELECTOR = DIALOG_CONTENT_SELECTOR + ' coral-numberinput[name="./structureDepth"]';

    $(window).adaptTo("foundation-registry").register("foundation.adapters", {
        type: "foundation-toggleable",
        selector: STRUCTURE_DEPTH_SELECTOR,
        adapter: function(el) {
            var toggleable = $(el);
            return {
                isOpen: function() {
                    return !toggleable.adaptTo("foundation-field").isDisabled();
                },
                show: function() {
                    toggleable.adaptTo("foundation-field").setDisabled(false);
                    toggleable.parent().show();
                },
                hide: function() {
                    toggleable.adaptTo("foundation-field").setDisabled(true);
                    toggleable.parent().hide();
                }
            };
        }
    });

    function toggleStructureDepth(collectAllPages) {
        if (collectAllPages) {
            Coral.commons.ready(document.querySelector(STRUCTURE_DEPTH_SELECTOR), function(structureDepth) {
                if (collectAllPages.checked) {
                    $(structureDepth).adaptTo("foundation-toggleable").hide();
                } else {
                    $(structureDepth).adaptTo("foundation-toggleable").show();
                }
            });
        }
    }

    $(document).on("dialog-loaded", function() {
        toggleStructureDepth(document.querySelector(COLLECT_ALL_PAGES_SELECTOR));
    });

    $(document).on("change", COLLECT_ALL_PAGES_SELECTOR, function() {
        toggleStructureDepth(this);
    });

})(jQuery);
