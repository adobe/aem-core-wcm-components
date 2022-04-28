/*******************************************************************************
 * Copyright 2017 Adobe
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
/**
 * Design dialog:
 * - The options of the select field to define the default value are added/removed based on the status
 * of the size checkboxes
 * - Validation: if no size checkboxes are checked, the dialog cannot be saved
 *
 * Edit dialog:
 * - displays all the sizes if no sizes have been defined in the policy
 * - hides all the sizes if only one size has been defined in the policy
 * - displays all the sizes defined in the policy if there are at least two
 */
(function($, Granite, ns, $document) {
    "use strict";

    var selectors = {
        designDialogContent: ".cmp-title__design-editor",
        editDialogContent: ".cmp-title__editor",
        titleTypeSelectElement: "coral-select[name='./type']",
        allowedHeadingElements: "coral-select[name='./allowedTypes']",
        linkUrl: ".cmp-title-link-url",
        linkLabel: ".cmp-title-link-label",
        linkTitle: ".cmp-title-link-title"
    };

    var SELECT_FIX_PADDING = "240px";

    /**
     * Executes when the dialog is loaded and is a Title dialog
     */
    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        if ($dialog.length) {
            var $designDialogContent = $dialog.find(selectors.designDialogContent);
            var $editDialogContent = $dialog.find(selectors.editDialogContent);
            if ($designDialogContent) {
                var designDialogContent = $designDialogContent.length > 0 ? $designDialogContent[0] : undefined;
                if (designDialogContent) {
                    handleDesignDialog(designDialogContent);
                }
            }
            if ($editDialogContent) {
                var editDialogContent = $editDialogContent.length > 0 ? $editDialogContent[0] : undefined;
                if (editDialogContent) {
                    handleEditDialog(editDialogContent);
                }
            }
        }
    });

    /**
     * Binds the design dialog handling
     *
     * @param {HTMLElement} designDialogContent The design dialog content wrapper
     */
    function handleDesignDialog(designDialogContent) {
        var allowedHeadingElements = designDialogContent.querySelector(selectors.allowedHeadingElements);
        var titleTypeElement = designDialogContent.querySelector(selectors.titleTypeSelectElement);

        if (allowedHeadingElements && titleTypeElement) {
            Coral.commons.ready(allowedHeadingElements, function() {
                updateTitleTypeElement(allowedHeadingElements, titleTypeElement);
            });

            allowedHeadingElements.on("change", function() {
                updateTitleTypeElement(allowedHeadingElements, titleTypeElement);
            });

            fixSelectDisplay(designDialogContent);
        }
    }

    /**
     * Binds the edit dialog handling
     *
     * @param {HTMLElement} editDialogContent The edit dialog content wrapper
     */
    function handleEditDialog(editDialogContent) {

        manageTitleTypeSelectDropdownFieldVisibility(editDialogContent);

        $document.on("foundation-contentloaded", function(e) {
            Coral.commons.ready($(selectors.linkUrl, selectors.linkLabel, selectors.linkTitle), function(component) {
                toggleDisableAttributeOnLinkLabelAndTitleInputs();
            });
        });

        $(document).on("input", selectors.linkUrl, function(input) {
            $(selectors.linkUrl).val(input.target.value);
            toggleDisableAttributeOnLinkLabelAndTitleInputs();
        });

        $(document).on("change", selectors.linkUrl, function(input) {
            toggleDisableAttributeOnLinkLabelAndTitleInputs();
        });
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
     * Temporary workaround for select dropdown display in the title policy dialog. CQ-4206495, CUI-1818
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

    /**
     * Toggles the disable attribute of the Link Label and Link Title Attribute inputs, based on the Link Url existence
     */
    function toggleDisableAttributeOnLinkLabelAndTitleInputs() {
        $(selectors.linkLabel).prop("disabled", !$(selectors.linkUrl).val());
        $(selectors.linkTitle).prop("disabled", !$(selectors.linkUrl).val());
    }

    /**
     * Hides the title type select dropdown field in the edit dialog if there's only one allowed heading element defined in a policy
     *
     * @param {HTMLElement} dialogContent The dialog content
     */
    function manageTitleTypeSelectDropdownFieldVisibility(dialogContent) {
        var titleTypeElement = dialogContent.querySelector(selectors.titleTypeSelectElement);
        if (titleTypeElement) {
            Coral.commons.ready(titleTypeElement, function(element) {
                var titleTypeElementToggleable = $(element.parentNode).adaptTo("foundation-toggleable");
                var itemCount = element.items.getAll().length;
                if (itemCount < 2) {
                    titleTypeElementToggleable.hide();
                }
            });
        }
    }

}(jQuery, Granite, Granite.author, jQuery(document)));
