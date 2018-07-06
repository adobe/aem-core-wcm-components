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

    var ui = $(window).adaptTo("foundation-ui");

    var popover;

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
            popover = new Coral.Popover().set({
                alignAt: Coral.Overlay.align.LEFT_BOTTOM,
                alignMy: Coral.Overlay.align.LEFT_TOP,
                target: target[0],
                open: true
            });

            popover.on("coral-overlay:close", function() {
                popover.parentNode.removeChild(popover);
            });

            popover.classList.add("editor-PanelSelector");

            var table = new Coral.Table().set({
                selectable: true
            });

            table.on("coral-table:change", function(event) {
                var row = event.target.selectedItem;
                var index = Array.prototype.slice.call(row.parentElement.children).indexOf(row);
                // TODO: make a generic message handler which calls the appropriate widget from a registry
                Granite.author.ContentFrame.postMessage("carousel", { slide: index });
            });

            popover.appendChild(table);
            ns.ContentFrame.scrollView[0].appendChild(popover);

            // determine editable children
            var children = [];
            if (editable.isContainer()) {
                children = editable.getChildren().filter(isDisplayable);
            }

            // read model JSON
            var promise = readJSON(editable.path);

            promise.done(function(data) {
                if (data && data.items) {
                    var items = [];
                    for (var i = 0; i < data.items.length; i++) {
                        items.push({
                            id: children[i].path,
                            title: getTitle(children[i], data.items[i], i + 1)
                        });
                    }
                    appendItems(table, items);
                }
            }).fail(function() {
                // fallback: editable children
                var items = [];

                children.forEach(function(child, index) {
                    items.push({
                        id: child.path,
                        title: getTitle(child, null, index + 1)
                    });
                });

                appendItems(table, items);
            }).always(function() {
                ui.clearWait();
            });

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
     * Reads items from component model json
     *
     * @param {String} path to the component
     * @returns {$.Deferred} A promise for handling read success
     */
    function readJSON(path) {
        ui.wait(popover);
        return $.ajax({
            url: path + ".model.json"
        });
    }

    /**
     * Retrieves a title from item data. If no item data exists, or it doesn't have a jcr:title
     * instead lookup the editable display name of the corresponding editable. Prefixes each title with an index.
     *
     * @param {Granite.author.Editable} editable The editable representing the item
     * @param {Object} item The item data
     * @param {Number} index Index of the item
     * @returns {String} The title
     */
    function getTitle(editable, item, index) {
        var title = "<span class='foundation-layout-util-subtletext'>" + index + "</span> ";

        if (item && item["jcr:title"]) {
            title = title + " " + Granite.I18n.getVar(item["jcr:title"]);
        } else {
            title = title + " " + Granite.I18n.getVar(ns.editableHelper.getEditableDisplayableName(editable));
        }

        return title;
    }

    /**
     * Appends panel items to a Coral.Table
     *
     * @param {Coral.Table} table The table to append to
     * @param {Object[]} items Array of data items
     * @param {String} items[].id Item ID (path)
     * @param {String} items[].title Item title
     */
    function appendItems(table, items) {
        for (var i = 0; i < items.length; i++) {
            var row = table.items.add({});
            row.appendChild(new Coral.Table.Cell().set({
                content: {
                    innerHTML: items[i].title
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
