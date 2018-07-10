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
    var table;

    /**
     * Toolbar action that presents a UI for selecting and reordering child
     * items of a toggleable panel container component for display.
     */
    var panelSelect = new ns.ui.ToolbarAction({
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

            table = new Coral.Table().set({
                orderable: true,
                selectable: true
            });

            var colgroup = document.createElement("colgroup");
            colgroup.appendChild(new Coral.Table.Column());
            colgroup.appendChild(new Coral.Table.Column().set({
                fixedWidth: true
            }));
            var tableHead = new Coral.Table.Head().set({
                sticky: true
            });
            tableHead.appendChild(new Coral.Table.HeaderCell());
            tableHead.appendChild(new Coral.Table.HeaderCell());
            tableHead.classList.add("editable-PanelSelector-tableHead");

            table.appendChild(colgroup);
            table.appendChild(tableHead);
            table.classList.add("editor-PanelSelector-table");

            table.on("coral-table:change", function(event) {
                // ensure selection of a single item
                if (event.detail.selection.length === 0) {
                    if (event.detail.oldSelection.length) {
                        Coral.commons.nextFrame(function() {
                            event.detail.oldSelection[0].setAttribute("selected", true);
                        });
                    }
                }

                if (event.detail.selection !== event.detail.oldSelection) {
                    navigate();
                }
            });

            table.on("coral-table:roworder", function(event) {
                var before = event.detail.before;
                var insertBehavior = ns.persistence.PARAGRAPH_ORDER.before;
                var childEditable = ns.editables.find({
                    path: event.detail.row.dataset.id
                })[0];
                var editableNeighbor;
                // TODO: add undo/redo behaviour. It's turned off for now.
                var historyConfig = {
                    preventAddHistory: true
                };

                if (before) {
                    editableNeighbor = ns.editables.find({
                        path: before.dataset.id
                    })[0];
                } else {
                    // dragged row to table end
                    var after = event.detail.row.previousElementSibling;
                    insertBehavior = ns.persistence.PARAGRAPH_ORDER.after;
                    editableNeighbor = ns.editables.find({
                        path: after.dataset.id
                    })[0];
                }

                markRowIndexes();

                ns.edit.EditableActions.MOVE.execute(childEditable, insertBehavior, editableNeighbor, historyConfig).done(function() {
                    ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
                        navigate();
                    });
                    // TODO: move fail handler
                });
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
            // TODO: or better, a config for determining any toggle container so we don't have to maintain this list
            var supportedResourceTypes = [
                "core/wcm/sandbox/components/carousel/v1/carousel",
                "weretail/components/content/carousel"
            ];

            var children = [];
            if (editable.isContainer()) {
                children = editable.getChildren().filter(isDisplayable);
            }

            return (children.length > 1) && (supportedResourceTypes.indexOf(editable.type) > -1);
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
     * Reads the selected item from the table and posts
     * the navigation message to the content frame to implement the change
     */
    function navigate() {
        if (!table) {
            return;
        }

        var selectedItem = table.selectedItem;

        if (selectedItem) {
            var index = Array.prototype.slice.call(selectedItem.parentElement.children).indexOf(selectedItem);
            // TODO: make a generic message handler which calls the appropriate widget from a registry
            // TODO: not hardcoded to carousel (could be tabs, accordion, ...)
            Granite.author.ContentFrame.postMessage("carousel", { slide: index });
        }
    }

    /**
     * Sets the text content of a row's index span
     * to match to its actual position in the table
     */
    function markRowIndexes() {
        if (!table) {
            return;
        }

        var rows = table.items.getAll();

        for (var i = 0; i < rows.length; i++) {
            var indexSpan = rows[i].getElementsByTagName("span");
            indexSpan[0].textContent = i + 1;
        }
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
        var title = "<span class='foundation-layout-util-subtletext'>" + index + "</span>&nbsp;&nbsp;";

        if (item && item["jcr:title"]) {
            title = title + " " + item["jcr:title"];
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
            row.dataset.id = items[i].id;
            var titleCell = new Coral.Table.Cell().set({
                content: {
                    innerHTML: items[i].title
                }
            });
            var button = new Coral.Button().set({
                icon: "dragHandle",
                iconSize: "S",
                variant: "minimal"
            });
            button.setAttribute("coral-table-roworder", true);
            var dragHandleCell = new Coral.Table.Cell();
            dragHandleCell.appendChild(button);

            row.appendChild(titleCell);
            row.appendChild(dragHandleCell);
        }

        // set the height of the table based on its content to allow scrolling when dragging rows
        table.style.height = table.offsetHeight + "px";
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

    channel.on("cq-layer-activated", function(event) {
        if (event.layer === "Edit") {
            ns.EditorFrame.editableToolbar.registerAction("PANEL_SELECT", panelSelect);
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
