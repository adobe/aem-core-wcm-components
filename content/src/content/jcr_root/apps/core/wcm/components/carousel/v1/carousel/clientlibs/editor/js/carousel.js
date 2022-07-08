/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
(function($) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-carousel__editor",
        autoplay: "[data-cmp-carousel-v1-dialog-hook='autoplay']",
        autoplayGroup: "[data-cmp-carousel-v1-dialog-hook='autoplayGroup']"
    };

    var autoplay;
    var autoplayGroup;

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;

        if ($dialog.length) {
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);

            if (dialogContent) {
                autoplay = dialogContent.querySelector(selectors.autoplay);
                autoplayGroup = dialogContent.querySelector(selectors.autoplayGroup);

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
        if (autoplay && autoplayGroup) {
            if (!autoplay.checked) {
                autoplayGroup.setAttribute("hidden", true);
            } else {
                autoplayGroup.removeAttribute("hidden");
            }
        }
    }

})(jQuery);
