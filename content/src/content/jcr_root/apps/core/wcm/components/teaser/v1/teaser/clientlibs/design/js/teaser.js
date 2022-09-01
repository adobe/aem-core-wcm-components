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

    var dialogContentSelector = ".cmp-teaser__design";
    var titleHiddenCheckboxSelector = "coral-checkbox[name='./titleHidden']";
    var titleLinkHiddenCheckboxSelector = 'coral-checkbox[name="./titleLinkHidden"]';
    var titleTypeSelectElementWrapperSelector = ".coral-Form-fieldwrapper:has(coral-select[name='./titleType'])";
    var titleTypeSelectElementSelector = "coral-select[name='./titleType']";
    var allowedHeadingElementsWrapperSelector = ".coral-Form-fieldwrapper:has(coral-select[name='./allowedHeadingElements'])";
    var allowedHeadingElementsSelector = "coral-select[name='./allowedHeadingElements']";
    var SELECT_FIX_PADDING = "240px";

    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        var $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            var $titleHiddenCheckbox = $dialogContent.find(titleHiddenCheckboxSelector);
            if ($titleHiddenCheckbox.size() > 0) {
                var titleHidden = $titleHiddenCheckbox.adaptTo("foundation-field").getValue() === "true";
                toggle($dialogContent, titleTypeSelectElementWrapperSelector, !titleHidden);
                toggle($dialogContent, titleLinkHiddenCheckboxSelector, !titleHidden);
                toggle($dialogContent, allowedHeadingElementsWrapperSelector, !titleHidden);

                $titleHiddenCheckbox.on("change", function(event) {
                    var titleHidden = $(event.target).adaptTo("foundation-field").getValue() === "true";
                    toggle($dialogContent, titleTypeSelectElementWrapperSelector, !titleHidden);
                    toggle($dialogContent, titleLinkHiddenCheckboxSelector, !titleHidden);
                    toggle($dialogContent, allowedHeadingElementsWrapperSelector, !titleHidden);
                });
            }
            handleSelectDefaultTitleTypeLogic(dialogContent);
        }
    });

    function toggle(dialog, selector, show) {
        var $target = dialog.find(selector);
        if ($target) {
            if (show) {
                $target.show();
            } else {
                $target.hide();
            }
        }
    }

    /**
     * Handles the logic for selecting the correct default title type based on the allowed heading elements selection
     *
     * @param {HTMLElement} dialogContent The dialog content
     */
    function handleSelectDefaultTitleTypeLogic(dialogContent) {
        var allowedHeadingElements = dialogContent.querySelector(allowedHeadingElementsSelector);
        var titleTypeElement = dialogContent.querySelector(titleTypeSelectElementSelector);

        if (allowedHeadingElements && titleTypeElement) {
            Coral.commons.ready(allowedHeadingElements, function() {
                updateTitleTypeElement(allowedHeadingElements, titleTypeElement);
            });

            allowedHeadingElements.on("change", function() {
                updateTitleTypeElement(allowedHeadingElements, titleTypeElement);
            });

            fixSelectDisplay(dialogContent);
        }
    }

    /**
     * Updates the title type element
     *
     * @param {HTMLElement} allowedHeadingElements Allowed heading elements select field
     * @param {HTMLElement} titleTypeElement Title type element select field
     */
    function updateTitleTypeElement(allowedHeadingElements, titleTypeElement) {
        var allowedItems = allowedHeadingElements.items.getAll();
        var titleElementToggleable = $(titleTypeElement.parentNode).adaptTo("foundation-toggleable");
        var titleElementValue = titleTypeElement.value;

        titleTypeElement.items.clear();

        for (var i = 0; i < allowedItems.length; i++) {
            var allowedItem = allowedItems[i];
            if (allowedHeadingElements.values.indexOf(allowedItem.value) > -1) {
                var item = new Coral.Select.Item();
                item.content.textContent = allowedItem.content.textContent;
                item.value = allowedItem.value;
                titleTypeElement.items.add(item);
            }
        }

        Coral.commons.nextFrame(function() {
            var value = (allowedItems.length) ? allowedItems.values[0] : "";

            if (allowedHeadingElements.values.indexOf(titleElementValue) > -1) {
                value = titleElementValue;
            }

            titleTypeElement.value = value;

            if (allowedHeadingElements.values.length < 2) {
                titleElementToggleable.hide();
            } else {
                titleElementToggleable.show();
            }
        });
    }

    /**
     * Temporary workaround for select dropdown display in the teaser policy dialog. CQ-4206495, CUI-1818
     *
     * @param {HTMLElement} dialogContent The dialog content
     */
    function fixSelectDisplay(dialogContent) {
        // sets the collision property for select overlays to "none"
        var selects = dialogContent.querySelectorAll("coral-select");

        for (var i = 0; i < selects.length; i++) {
            var overlay = selects[i].querySelector("coral-overlay");
            if (overlay) {
                overlay.collision = Coral.Overlay.collision.NONE;
            }
        }

        // adds a sufficient padding to the bottom of the wrapper such that selects
        // have a guaranteed space to expand into.
        if (selects.length) {
            var field = selects[0].parentNode;
            var wrapper = field.parentNode;
            wrapper.style.paddingBottom = SELECT_FIX_PADDING;
        }
    }

})(jQuery);
