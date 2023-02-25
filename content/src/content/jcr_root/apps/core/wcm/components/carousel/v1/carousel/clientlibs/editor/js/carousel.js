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
        autoplayGroup: "[data-cmp-carousel-v1-dialog-hook='autoplayGroup']",
        childreneditorSelector: "[data-cmp-is='childrenEditor']",
        activeItemSelector: "[data-cmp-carousel-v1-dialog-edit-hook='activeItem']",
        activeSelectSelector: "[data-cmp-carousel-v1-dialog-edit-hook='activeSelect']"
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

                setupActiveSelect(dialogContent);
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

    /**
     * Set up event handlers to populate active select and active item fields.
     *
     * @param {HTMLElement} dialogContent content element of the edit dialog
     */
    function setupActiveSelect(dialogContent) {
        var childrenEditor = dialogContent.querySelector(selectors.childreneditorSelector);
        var activeSelect = dialogContent.querySelector(selectors.activeSelectSelector);
        var activeItem = dialogContent.querySelector(selectors.activeItemSelector);

        if (childrenEditor && activeSelect && activeItem) {
            Coral.commons.ready(childrenEditor, function() {
                updateActiveSelect(childrenEditor, activeSelect, activeItem);
            });

            childrenEditor.on("change", function() {
                updateActiveSelect(childrenEditor, activeSelect, activeItem);
            });

            activeSelect.on("change", function() {
                activeItem.value = activeSelect.value;
            });
        }
    }

    /**
     * Update the list of items in the active item selector.
     *
     * @param {HTMLElement} childrenEditor Children editor multifield
     * @param {HTMLElement} activeSelect Active item select field
     * @param {HTMLElement} activeItem Active item hidden input
     */
    function updateActiveSelect(childrenEditor, activeSelect, activeItem) {
        if (childrenEditor && activeSelect && activeItem) {
            var selectedValue = activeSelect.value || activeItem.value;
            activeSelect.items.getAll().forEach(function(item) {
                if (item.value !== "") {
                    activeSelect.items.remove(item);
                }
            });
            var cmpChildrenEditor = $(childrenEditor).adaptTo("cmp-childreneditor");
            if (cmpChildrenEditor) {
                cmpChildrenEditor.items().forEach(function(item) {
                    activeSelect.items.add({
                        selected: item.name === selectedValue,
                        value: item.name,
                        content: {
                            textContent: item.description
                        }
                    });
                });
            }
        }
    }
})(jQuery);
