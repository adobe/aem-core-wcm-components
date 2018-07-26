/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
/* global CQ */
(function($, ns, channel, window, undefined) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    var NS = ".editor-panelselector";

    CQ.CoreComponents.PanelSelector = ns.util.createClass({

        _config: {},
        _elements: {},

        constructor: function PanelSelector(config) {
            this._config = config;

            var panelContainer = CQ.CoreComponents.panelcontainer.utils.getPanelContainer(this._config.editable);

            if (panelContainer) {
                this._panelContainer = new CQ.CoreComponents.PanelContainer({
                    path: this._config.editable.path,
                    panelContainer: panelContainer,
                    el: this._config.editable.dom,
                });

                this._render();
                this._bindEvents();
            }
        },

        _render: function() {
            this._createPopover();
            this._createTable();

            this._elements.popover.appendChild(this._elements.table);
            ns.ContentFrame.scrollView[0].appendChild(this._elements.popover);

            this._renderItems();
        },

        _createPopover: function() {
            var that = this;

            var popover = new Coral.Popover().set({
                alignAt: Coral.Overlay.align.LEFT_BOTTOM,
                alignMy: Coral.Overlay.align.LEFT_TOP,
                target: that._config.target,
                open: true
            });

            popover.classList.add("editor-PanelSelector");

            this._elements.popover = popover;
        },

        _createTable: function() {
            var table = new Coral.Table().set({
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

            this._elements.table = table;
        },

        _renderItems: function() {
            var that = this;

            // determine editable children
            var children = [];
            if (that._config.editable.isContainer()) {
                children = that._config.editable.getChildren().filter(isDisplayable);
            }

            // read model JSON
            ui.wait(that._elements.popover);
            that._panelContainer.getItems().done(function(items) {
                if (items) {
                    var itemsData = [];
                    for (var i = 0; i < items.length; i++) {
                        itemsData.push({
                            id: children[i].path,
                            title: getTitle(children[i], items[i], i + 1)
                        });
                    }
                    that._createItems(itemsData);
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

                that._createItems(items);
            }).always(function() {
                ui.clearWait();
            });
        },

        /**
         * Appends panel items to a Coral.Table
         *
         * @param {Object[]} items Array of data items
         * @param {String} items[].id Item ID (path)
         * @param {String} items[].title Item title
         */
        _createItems: function(items) {
            var activeIndex = this._panelContainer.getActiveIndex();

            for (var i = 0; i < items.length; i++) {
                var row = this._elements.table.items.add({});
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

                if (activeIndex === i) {
                    row.selected = true;
                }
            }

            // set the height of the table based on its content to allow scrolling when dragging rows
            this._elements.table.style.height = this._elements.table.offsetHeight + "px";
        },

        _bindEvents: function() {
            var that = this;

            that._elements.popover.on("coral-overlay:close", function() {
                that._elements.popover.parentNode.removeChild(that._elements.popover);
            });

            that._elements.table.on("coral-table:change", function(event) {
                // ensure at least one item is selected
                if (event.detail.selection.length === 0) {
                    if (event.detail.oldSelection.length) {
                        Coral.commons.nextFrame(function() {
                            event.detail.oldSelection[0].setAttribute("selected", true);
                        });
                    }
                }

                if (event.detail.selection !== event.detail.oldSelection) {
                    that._navigate();
                }
            });

            that._elements.table.on("coral-table:roworder", function(event) {
                var before = event.detail.before;
                var insertBehavior = ns.persistence.PARAGRAPH_ORDER.before;
                var childEditable = ns.editables.find({
                    path: event.detail.row.dataset.id
                })[0];
                var editableNeighbor;
                // TODO: add undo/redo behaviour
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

                that._markRowIndexes();

                ns.edit.EditableActions.MOVE.execute(childEditable, insertBehavior, editableNeighbor, historyConfig).done(function() {
                    ns.edit.EditableActions.REFRESH.execute(that._config.editable).done(function() {
                        that._navigate();
                    });
                    // TODO: move fail handler
                });
            });

            // reposition the popover with overlay change,
            // as the editable toolbar can jump following navigation to a panel
            channel.off("cq-overlays-repositioned" + NS).on("cq-overlays-repositioned" + NS, function() {
                if (that._elements.popover) {
                    that._elements.popover.reposition();
                }
            });
        },

        /**
         * Sets the text content of a row's index span
         * to match to its actual position in the table
         */
        _markRowIndexes: function() {
            var rows = this._elements.table.items.getAll();

            for (var i = 0; i < rows.length; i++) {
                var indexSpan = rows[i].getElementsByTagName("span");
                indexSpan[0].textContent = i + 1;
            }
        },

        /**
         * Reads the selected item from the table and
         * calls the panelContainer to handle the navigation.
         */
        _navigate: function() {
            var selectedItem = this._elements.table.selectedItem;

            if (selectedItem) {
                var index = Array.prototype.slice.call(selectedItem.parentElement.children).indexOf(selectedItem);
                this._panelContainer.navigate(index);
            }
        }
    });

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
     * Test whether an Editable is displayable in the panel popover. Ignore Inspectables and Placeholders.
     *
     * @param {Granite.author.Editable} editable The Editable to test
     * @returns {Boolean} Whether the Editable is displayed in the panel popover, or not
     */
    function isDisplayable(editable) {
        return (editable instanceof ns.Editable &&
        (editable.isContainer() || (editable.hasActionsAvailable() && !editable.isNewSection())));
    }

}(jQuery, Granite.author, jQuery(document), this));
