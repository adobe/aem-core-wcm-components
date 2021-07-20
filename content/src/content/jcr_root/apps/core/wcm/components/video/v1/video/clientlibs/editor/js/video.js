/*******************************************************************************
 * Copyright 2021 Adobe
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

/* global jQuery*/
(function ($, Coral) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-video__editor",
        autoplay: "[data-cmp-video-v1-dialog-edit-hook='autoplay']",
        hideControl: "[data-cmp-video-v1-dialog-hook='hideControl']",
        muted: "[data-cmp-video-v1-dialog-edit-hook='mutedCheckboxStatus']"
    };

    var autoplay;
    var hideControl;
    var mutedCheckbox;
    var mutedStatus = false;

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;
        if ($dialog.length) {
            var dialogContent = $dialog[0].querySelector(selectors.dialogContent);
            if (dialogContent) {
                autoplay = dialogContent.querySelector(selectors.autoplay);
                hideControl = dialogContent.querySelector(selectors.hideControl);
                mutedCheckbox = dialogContent.querySelector(selectors.muted);

                if (mutedCheckbox.hasAttribute("checked")){
                    mutedStatus = true;
                }

                if (autoplay.hasAttribute("checked")) {
                    mutedCheckbox.setAttribute("checked", true);
                    mutedCheckbox.setAttribute("disabled", true);
                }

                if (hideControl) {
                    Coral.commons.ready(hideControl, function () {
                        hideControl.on("change", onHideControlChange);
                    });
                }

                if (mutedCheckbox) {
                    Coral.commons.ready(mutedCheckbox, function () {
                        mutedCheckbox.on("change", function(e) {
                            mutedStatus = event.target.checked;
                        })
                    });
                }

                if (autoplay) {
                    Coral.commons.ready(autoplay, function () {
                        autoplay.on("change", autoplayChange);
                    });
                }
            }
        }
    });

    function toggleMutedCheck(event) {
        mutedStatus = event.target.checked;
    }

    function autoplayChange(defaultNotChecked = false) {
        if(defaultNotChecked === true){
            autoplay.removeAttribute("checked");
        }
        if (autoplay.checked) {
            mutedCheckbox.setAttribute("checked");
            mutedCheckbox.setAttribute("disabled");
        } else {
            mutedCheckbox.removeAttribute("disabled");
            if (mutedStatus === true)
                mutedCheckbox.setAttribute("checked");
            else
                mutedCheckbox.removeAttribute("checked");
        }
    }

    function onHideControlChange() {
        if (autoplay && hideControl && hideControl.checked) {
            autoplay.setAttribute("checked");
            autoplayChange();
        } else {
            autoplayChange(true);
        }
    }

})(jQuery, Coral);
