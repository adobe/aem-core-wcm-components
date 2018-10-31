/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

    var selectors = {
        dialogContent: ".cmp-carousel__editor",
        autoplay: "[data-cmp-carousel-v1-dialog-hook='autoplay']",
        autoplayGroup: "[data-cmp-carousel-v1-dialog-hook='autoplayGroup']",
        delay: "[data-cmp-carousel-v1-dialog-hook='delay']",
        autopauseDisabled: "[data-cmp-carousel-v1-dialog-hook='autopauseDisabled']"
    };

    var autoplay;
    var autoplayGroup;
    var delay;
    var autopauseDisabled;

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                autoplay = dialogContent.querySelector(selectors.autoplay);
                autoplayGroup = dialogContent.querySelector(selectors.autoplayGroup);
                delay = dialogContent.querySelector(selectors.delay);
                autopauseDisabled = dialogContent.querySelector(selectors.autopauseDisabled);

                if (autoplay) {
                    Coral.commons.ready(autoplay, function() {
                        autoplay.on("change", onAutoplayChange);
                        onAutoplayChange();
                    });
                }
            }
        }
    });

    /**
     * Handles a change in the autoplay checkbox state.
     * Conditionally toggles hidden state of the related autoplay group which contains
     * additional fields that are only relevant when autoplay is enabled.
     *
     * @private
     */
    function onAutoplayChange() {
        if (autoplay && autoplayGroup && delay && autopauseDisabled) {
            var delayField = $(delay).adaptTo("foundation-field");
            var autopauseDisabledField = $(autopauseDisabled).adaptTo("foundation-field");

            if (!autoplay.checked) {
                autoplayGroup.setAttribute("hidden", true);
                delayField.setDisabled(true);
                autopauseDisabledField.setDisabled(true);
            } else {
                delayField.setDisabled(false);
                autopauseDisabledField.setDisabled(false);
                autoplayGroup.removeAttribute("hidden");
            }
        }
    }

})(jQuery);
