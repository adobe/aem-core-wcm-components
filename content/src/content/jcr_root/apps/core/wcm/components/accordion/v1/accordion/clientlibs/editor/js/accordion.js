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
(function($, Coral) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-accordion__editor",
        edit: {
            childrenEditor: "[data-cmp-is='childrenEditor']",
            singleExpansion: "[data-cmp-accordion-v1-dialog-edit-hook='singleExpansion']",
            expandedItems: "[data-cmp-accordion-v1-dialog-edit-hook='expandedItems']",
            expandedSelect: "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect']",
            expandedSelectSingle: "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelectSingle']",
            headingElement: "[data-cmp-accordion-v1-dialog-edit-hook='headingElement']"
        },
        policy: {
            allowedHeadingElements: "[data-cmp-accordion-v1-dialog-policy-hook='allowedHeadingElements']",
            headingElement: "[data-cmp-accordion-v1-dialog-policy-hook='headingElement']"
        }
    };

    var SELECT_FIX_PADDING = "240px";

    /**
     * Executes when the dialog is loaded and is an accordion dialog.
     */
    $(document).on("dialog-loaded", function(e) {
        var $dialog = e.dialog;
        if ($dialog.length) {
            var $dialogContent = $dialog.find(selectors.dialogContent);
            if ($dialogContent) {
                var accordionEditor = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
                if (accordionEditor) {
                    if (accordionEditor.querySelector("[data-cmp-accordion-v1-dialog-edit-hook]")) {
                        handleEditDialog(accordionEditor);
                    } else if (accordionEditor.querySelector("[data-cmp-accordion-v1-dialog-policy-hook]")) {
                        handlePolicyDialog(accordionEditor);
                    }
                }
            }
        }
    });

    /**
     * Binds policy dialog handling
     *
     * @param {HTMLElement} accordionEditor The dialog wrapper
     */
    function handlePolicyDialog(accordionEditor) {
        var allowedHeadingElements = accordionEditor.querySelector(selectors.policy.allowedHeadingElements);
        var headingElement = accordionEditor.querySelector(selectors.policy.headingElement);

        if (allowedHeadingElements && headingElement) {
            Coral.commons.ready(allowedHeadingElements, function() {
                updateHeadingElement(allowedHeadingElements, headingElement);
            });

            allowedHeadingElements.on("change", function() {
                updateHeadingElement(allowedHeadingElements, headingElement);
            });

            fixSelectDisplay(accordionEditor);
        }
    }

    /**
     * Binds edit dialog handling
     *
     * @param {HTMLElement} accordionEditor The dialog wrapper
     */
    function handleEditDialog(accordionEditor) {
        var childrenEditor = accordionEditor.querySelector(selectors.edit.childrenEditor);
        var singleExpansion = accordionEditor.querySelector(selectors.edit.singleExpansion);
        var expandedItems = Array.prototype.slice.call(accordionEditor.querySelectorAll(selectors.edit.expandedItems));
        var expandedItemValues = [];
        var expandedSelect = accordionEditor.querySelector(selectors.edit.expandedSelect);
        var expandedSelectSingle = accordionEditor.querySelector(selectors.edit.expandedSelectSingle);
        var headingElement = accordionEditor.querySelector(selectors.edit.headingElement);

        for (var i = 0; i < expandedItems.length; i++) {
            expandedItemValues.push(expandedItems[i].value);
        }

        if (childrenEditor && singleExpansion && expandedItems && expandedSelect && expandedSelectSingle) {
            Coral.commons.ready(childrenEditor, function() {
                var cmpChildrenEditor = $(childrenEditor).adaptTo("cmp-childreneditor");
                updateExpandedSelect(childrenEditor, expandedSelect, expandedItemValues, false);
                updateExpandedSelect(childrenEditor, expandedSelectSingle, expandedItemValues, true);
                if (cmpChildrenEditor.items().length === 0) {
                    toggleExpandedSelects(expandedSelect, expandedSelectSingle, undefined, true);
                } else {
                    toggleExpandedSelects(expandedSelect, expandedSelectSingle, singleExpansion.checked);
                }

                childrenEditor.on("change", function() {
                    updateExpandedSelect(childrenEditor, expandedSelect, expandedItemValues, false);
                    updateExpandedSelect(childrenEditor, expandedSelectSingle, expandedItemValues, true);
                    if (cmpChildrenEditor.items().length === 0) {
                        toggleExpandedSelects(expandedSelect, expandedSelectSingle, undefined, true);
                    } else {
                        toggleExpandedSelects(expandedSelect, expandedSelectSingle, singleExpansion.checked);
                    }
                });

                singleExpansion.on("change", function() {
                    if (cmpChildrenEditor.items().length === 0) {
                        toggleExpandedSelects(expandedSelect, expandedSelectSingle, undefined, true);
                    } else {
                        toggleExpandedSelects(expandedSelect, expandedSelectSingle, singleExpansion.checked);
                    }
                });
            });
        }

        if (headingElement) {
            Coral.commons.ready(headingElement, function(element) {
                var headingElementToggleable = $(element.parentNode).adaptTo("foundation-toggleable");
                var itemCount = element.items.getAll().length;
                if (itemCount < 2) {
                    headingElementToggleable.hide();
                }
            });
        }
    }

    /**
     * Toggles expanded selects based on single expansion state
     *
     * @param {HTMLElement} expandedSelect Expanded accordion items select field
     * @param {HTMLElement} expandedSelectSingle Expanded accordion items single select field
     * @param {Boolean} singleExpansion true if single expansion is enabled, false otherwise
     * @param {Boolean} [hideAll] true to disable and hide all selects
     */
    function toggleExpandedSelects(expandedSelect, expandedSelectSingle, singleExpansion, hideAll) {
        var expandedSelectField = $(expandedSelect).adaptTo("foundation-field");
        var expandedSelectToggleable = $(expandedSelect.parentNode).adaptTo("foundation-toggleable");
        var expandedSelectSingleField = $(expandedSelectSingle).adaptTo("foundation-field");
        var expandedSelectSingleToggleable = $(expandedSelectSingle.parentNode).adaptTo("foundation-toggleable");

        if (hideAll) {
            expandedSelectField.setDisabled(true);
            expandedSelectToggleable.hide();
            expandedSelectSingleField.setDisabled(true);
            expandedSelectSingleToggleable.hide();
        } else if (singleExpansion) {
            expandedSelectField.setDisabled(true);
            expandedSelectToggleable.hide();
            expandedSelectSingleField.setDisabled(false);
            expandedSelectSingleToggleable.show();
        } else {
            expandedSelectField.setDisabled(false);
            expandedSelectToggleable.show();
            expandedSelectSingleField.setDisabled(true);
            expandedSelectSingleToggleable.hide();
        }
    }

    /**
     * Update the list of accordion items in the expanded accordion items selector
     *
     * @param {HTMLElement} childrenEditor Children editor multifield
     * @param {HTMLElement} expandedSelect Expanded accordion items select field
     * @param {String[]} expandedItemValues Expanded accordion item values
     * @param {Boolean} singleExpansion true if single expansion is enabled, false otherwise
     */
    function updateExpandedSelect(childrenEditor, expandedSelect, expandedItemValues, singleExpansion) {
        var selectedValues = (expandedSelect.values.length) ? expandedSelect.values : expandedItemValues;
        expandedSelect.items.getAll().forEach(function(item) {
            if (item.value !== "") {
                expandedSelect.items.remove(item);
            }
        });

        var cmpChildrenEditor = $(childrenEditor).adaptTo("cmp-childreneditor");
        if (cmpChildrenEditor) {
            if (singleExpansion) {
                expandedSelect.items.add({
                    selected: (selectedValues.length === 0),
                    content: {
                        textContent: Granite.I18n.get("None")
                    }
                });
                expandedSelect.items.first().set("value", null, true);
            }
            cmpChildrenEditor.items().forEach(function(item) {
                expandedSelect.items.add({
                    selected: (selectedValues.indexOf(item.name) > -1),
                    value: item.name,
                    content: {
                        textContent: item.description
                    }
                });
            });
        }
    }

    /**
     * Updates the heading element based on the allowed heading element selection
     *
     * @param {HTMLElement} allowedHeadingElements Allowed heading elements select field
     * @param {HTMLElement} headingElement Heading element select field
     */
    function updateHeadingElement(allowedHeadingElements, headingElement) {
        var allowedItems = allowedHeadingElements.items.getAll();
        var headingElementToggleable = $(headingElement.parentNode).adaptTo("foundation-toggleable");
        var headingElementValue = headingElement.value;

        headingElement.items.clear();

        for (var i = 0; i < allowedItems.length; i++) {
            var allowedItem = allowedItems[i];
            if (allowedHeadingElements.values.indexOf(allowedItem.value) > -1) {
                var item = new Coral.Select.Item();
                item.content.textContent = allowedItem.content.textContent;
                item.value = allowedItem.value;
                headingElement.items.add(item);
            }
        }

        Coral.commons.nextFrame(function() {
            var value = (allowedItems.length) ? allowedItems.values[0] : "";

            if (allowedHeadingElements.values.indexOf(headingElementValue) > -1) {
                value = headingElementValue;
            }

            headingElement.value = value;

            if (allowedHeadingElements.values.length < 2) {
                headingElementToggleable.hide();
            } else {
                headingElementToggleable.show();
            }
        });
    }

    /**
     * Temporary workaround for select dropdown display in the accordion policy dialog. CQ-4206495, CUI-1818
     *
     * @param {HTMLElement} accordionEditor The dialog wrapper
     */
    function fixSelectDisplay(accordionEditor) {
        // sets the collision property for select overlays to "none"
        var selects = accordionEditor.querySelectorAll("coral-select");

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

})(jQuery, Coral);
