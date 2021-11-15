/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
        dialogContent: ".cmp-container__editor",
        edit: {
            backgroundColorSwatchesOnly: "[data-cmp-container-v1-dialog-edit-hook='backgroundColorSwatchesOnly']"
        },
        policy: {
            backgroundColorEnabled: "[data-cmp-container-v1-dialog-policy-hook='backgroundColorEnabled']",
            backgroundColorSwatchesOnly: "[data-cmp-container-v1-dialog-policy-hook='backgroundColorSwatchesOnly']",
            backgroundColorAllowedSwatches: "[data-cmp-container-v1-dialog-policy-hook='backgroundColorAllowedSwatches']"
        }
    };

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(selectors.dialogContent);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            if (dialogContent.querySelector("[data-cmp-container-v1-dialog-edit-hook]")) {
                handleEditDialog(dialogContent);
            } else if (dialogContent.querySelector("[data-cmp-container-v1-dialog-policy-hook]")) {
                handlePolicyDialog(dialogContent);
            }
        }
    });

    /**
     * Binds edit dialog handling
     *
     * @param {HTMLElement} containerEditor The dialog wrapper
     */
    function handleEditDialog(containerEditor) {
        var backgroundColorSwatchesOnly = containerEditor.querySelector(selectors.edit.backgroundColorSwatchesOnly);
        if (backgroundColorSwatchesOnly) {
            backgroundColorSwatchesOnly.on("coral-overlay:beforeopen.cmp-container-v1-dialog-edit", function(event) {
                backgroundColorSwatchesOnly.off("coral-overlay:beforeopen.cmp-container-v1-dialog-edit");

                // ensures the swatches overlay is displayed correctly in dialog inline mode, by aligning it bottom left of the toggle button
                var target = event.target || event.srcElement;
                target.alignAt = Coral.Overlay.align.LEFT_BOTTOM;
                target.alignMy = Coral.Overlay.align.LEFT_TOP;
            });
        }
    }

    /**
     * Binds policy dialog handling
     *
     * @param {HTMLElement} containerEditor The dialog wrapper
     */
    function handlePolicyDialog(containerEditor) {
        var backgroundColorEnabled = containerEditor.querySelector(selectors.policy.backgroundColorEnabled);
        var backgroundColorSwatchesOnly = containerEditor.querySelector(selectors.policy.backgroundColorSwatchesOnly);
        var backgroundColorAllowedSwatches = containerEditor.querySelector(selectors.policy.backgroundColorAllowedSwatches);

        if (backgroundColorEnabled && backgroundColorSwatchesOnly && backgroundColorAllowedSwatches) {
            var backgroundColorSwatchesOnlyToggleable = $(backgroundColorSwatchesOnly).adaptTo("foundation-toggleable");
            var backgroundColorAllowedSwatchesToggleable = $(backgroundColorAllowedSwatches.parentNode).adaptTo("foundation-toggleable");
            toggle(backgroundColorSwatchesOnlyToggleable, backgroundColorEnabled.checked);
            toggle(backgroundColorAllowedSwatchesToggleable, backgroundColorEnabled.checked);

            backgroundColorEnabled.on("change", function(event) {
                toggle(backgroundColorSwatchesOnlyToggleable, backgroundColorEnabled.checked);
                toggle(backgroundColorAllowedSwatchesToggleable, backgroundColorEnabled.checked);
            });
        }
    }

    function toggle(toggleable, show) {
        if (show) {
            toggleable.show();
        } else {
            toggleable.hide();
        }
    }

})(jQuery);
