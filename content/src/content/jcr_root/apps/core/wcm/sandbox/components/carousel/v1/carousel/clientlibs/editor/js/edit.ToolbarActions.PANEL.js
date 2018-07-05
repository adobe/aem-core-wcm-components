/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
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
(function($, ns, channel, window) {
    "use strict";

    /**
     * Toolbar action that presents a UI for selecting and reordering child
     * items of a toggleable panel container component for display.
     *
     * @alias Granite.author.edit.ToolbarActions.PANEL
     */
    ns.edit.ToolbarActions.PANEL = new ns.ui.ToolbarAction({
        name: "PANEL",
        text: Granite.I18n.get("Select panel"),
        icon: "multipleCheck",
        order: "after CONFIGURE",
        execute: function(editable, param, target) {
            var popover = new Coral.Popover().set({
                "class": "cq-editor-panel-popover",
                alignAt: Coral.Overlay.align.LEFT_BOTTOM,
                alignMy: Coral.Overlay.align.LEFT_TOP,
                target: target[0],
                open: true
            });

            var table = new Coral.Table().set({
                selectable: true
            });
            popover.appendChild(table);

            // add items
            if (editable.isContainer()) {
                var children = editable.getChildren();
                var items = [];

                children.filter(isDisplayable).forEach(function(child) {
                    items.push({
                        id: child.path,
                        title: ns.editableHelper.getEditableDisplayableName(child)
                    });
                });

                appendItems(table, items);
            }

            // event handling
            popover.on("coral-overlay:close", function() {
                $(popover).remove();
            });

            table.on("coral-table:change", function(event) {
                var row = event.target.selectedItem;
                var index = Array.prototype.slice.call(row.parentElement.children).indexOf(row);
                // TODO: make a generic message handler which calls the appropriate widget from a registry
                Granite.author.ContentFrame.postMessage("carousel", { slide: index });
            });

            ns.ContentFrame.scrollView[0].appendChild(popover);

            // do not close the toolbar
            return false;
        },
        condition: function(editable) {
            // TODO: improve with super type
            // TODO: or better, a config for determining any toggle container
            return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
        },
        isNonMulti: true
    });

    /**
     * Appends panel items to a Coral.Table
     *
     * @param {Coral.Table} table The table to append to
     * @param {Object[]} items Array of data items
     * @param {String} items[].id Unique ID for the item (path)
     * @param {String} items[].title Item title
     */
    function appendItems(table, items) {
        for (var i = 0; i < items.length; i++) {
            var row = table.items.add({});
            row.appendChild(new Coral.Table.Cell().set({
                content: {
                    textContent: items[i].title
                }
            }));
        }
    }

    /**
     * Test whether an Editable is displayable in the panel popover. Ignore Inspectables and Placeholders.
     *
     * @param {Granite.author.Editable} editable The Editable to test
     * @returns {Boolean}
     */
    function isDisplayable(editable) {
        return (editable instanceof ns.Editable &&
        (editable.isContainer() || (editable.hasActionsAvailable() && !editable.isNewSection())));
    }

}(jQuery, Granite.author, jQuery(document), this));
