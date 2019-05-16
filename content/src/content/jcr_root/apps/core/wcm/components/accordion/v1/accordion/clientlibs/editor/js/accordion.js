/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
(function($, Coral) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-accordion__editor",
        childrenEditor: "[data-cmp-is='childrenEditor']",
        expandedSelect: "[data-cmp-accordion-v1-dialog-edit-hook='expandedSelect']",
        expandedItem: "[data-cmp-accordion-v1-dialog-edit-hook='expandedItem']"
    };

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
                    var childrenEditor = accordionEditor.querySelector(selectors.childrenEditor);
                    var expandedSelect = accordionEditor.querySelector(selectors.expandedSelect);
                    var expandedItem = accordionEditor.querySelector(selectors.expandedItem);

                    Coral.commons.ready(childrenEditor, function() {
                        updateExpandedSelect(childrenEditor, expandedSelect, expandedItem);
                    });

                    childrenEditor.on("change", function() {
                        updateExpandedSelect(childrenEditor, expandedSelect, expandedItem);
                    });

                    expandedSelect.on("change", function() {
                        expandedItem.value = expandedSelect.value;
                    });
                }
            }
        }
    });

    /**
     * Update the list of accordion items in the expanded accordion selector
     * @param {HTMLElement} childrenEditor Children editor multifield
     * @param {HTMLElement} expandedSelect Expanded accordion select field
     * @param {HTMLElement} expandedItem Expanded accordion hidden input
     */
    function updateExpandedSelect(childrenEditor, expandedSelect, expandedItem) {
        var selectedValue = expandedSelect.value || expandedItem.value;
        expandedSelect.items.getAll().forEach(function(item) {
            if (item.value !== "") {
                expandedSelect.items.remove(item);
            }
        });

        var cmpChildrenEditor = $(childrenEditor).adaptTo("cmp-childreneditor");
        if (cmpChildrenEditor) {
            cmpChildrenEditor.items().forEach(function(item) {
                expandedSelect.items.add({
                    selected: item.name === selectedValue,
                    value: item.name,
                    content: {
                        textContent: item.description
                    }
                });
            });
        }
    }
})(jQuery, Coral);
