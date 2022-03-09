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
/* global CQ */
(function($, ns, channel, window) {
    "use strict";

    var panelSelector;
    var NS_PANELSELECTOR = ".cmp-panelselector";
    var PN_PANEL_TITLE = "cq:panelTitle";

    var selectors = {
        indexMarker: ".cmp-panelselector__indexMarker"
    };

    /**
     * @typedef {Object} PanelSelectorConfig Represents a Panel Selector configuration object
     * @property {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} against which to create the panel selector
     * @property {HTMLElement} target The target against which to attach the panel selector UI
     */

    /**
     * @class CQ.CoreComponents.PanelSelector
     * @classdesc A Panel Selector creates a UI consisting of a [Coral.Popover]{@link Coral.Popover} and [Coral.Table]{@link Coral.Table}
     * which allows selection of panel container items for editing, as well as other operations such as reordering of panels.
     * @param {PanelSelectorConfig} config The Panel Selector configuration object
     */
    var PanelSelector = ns.util.createClass({

        /**
         * The Panel Selector configuration Object
         *
         * @member {PanelSelectorConfig} CQ.CoreComponents.PanelSelector#_config
         */
        _config: {},

        /**
         * The [CQ.CoreComponents.panelcontainer.v1.PanelContainer]{@link CQ.CoreComponents.panelcontainer.v1.PanelContainer} Object that is related to this Panel Selector
         *
         * @member {Object} CQ.CoreComponents.PanelSelector#_panelContainer
         */
        _panelContainer: {},

        /**
         * An Object that is used to cache the internal HTMLElements for this Panel Selector
         *
         * @member {Object} CQ.CoreComponents.PanelSelector#_elements
         */
        _elements: {},

        constructor: function PanelSelector(config) {
            var that = this;
            that._config = config;

            var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(that._config.editable);

            if (panelContainerType) {
                var element = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(that._config.editable);
                that._panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                    path: that._config.editable.path,
                    panelContainerType: panelContainerType,
                    el: element
                });

                that._render().done(function() {
                    that._bindEvents();
                });

                that._handleOutOfAreaClickBound = that._handleOutOfAreaClick.bind(that);
            }
        },

        /**
         * Determines whether this Panel Selector is open
         *
         * @returns {Boolean} True if open, False otherwise
         */
        isOpen: function() {
            return (this._elements.popover && this._elements.popover.open);
        },

        /**
         * Renders the Panel Selector, adds its items and attaches it to the DOM
         *
         * @private
         * @returns {Promise} Promise for handling completion
         */
        _render: function() {
            this._createPopover();
            this._createTable();

            this._elements.popover.content.appendChild(this._elements.table);
            ns.ContentFrame.scrollView[0].appendChild(this._elements.popover);

            return this._renderItems();
        },

        /**
         * Creates the [Coral.Popover]{@link Coral.Popover} for this Panel Selector
         *
         * @private
         */
        _createPopover: function() {
            var that = this;

            var popover = new Coral.Popover().set({
                alignAt: Coral.Overlay.align.LEFT_BOTTOM,
                alignMy: Coral.Overlay.align.LEFT_TOP,
                target: that._config.target,
                interaction: Coral.Popover.interaction.OFF,
                open: true
            });

            popover.classList.add("cmp-panelselector");

            this._elements.popover = popover;
        },

        /**
         * Creates the [Coral.Table]{@link Coral.Table} for this Panel Selector
         *
         * @private
         */
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
            tableHead.classList.add("cmp-panelselector__tableHead");

            table.appendChild(colgroup);
            table.appendChild(tableHead);
            table.classList.add("cmp-panelselector__table");

            this._elements.table = table;
        },

        /**
         * Fetches items for the related [Panel Container]{@link CQ.CoreComponents.panelcontainer.v1.PanelContainer} and renders them
         * to the [Coral.Table]{@link Coral.Table}
         *
         * @private
         * @returns {Promise} Promise for handling completion
         */
        _renderItems: function() {
            var deferred = $.Deferred();
            var that = this;

            var children = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerItems(that._config.editable);
            that._panelContainer.getItems().done(function(panelContainerItems) {
                var items = [];

                children.forEach(function(child, index) {
                    items.push({
                        id: child.path,
                        name: child.name,
                        title: getTitle(child, panelContainerItems[child.name], index + 1)
                    });
                });

                that._createItems(items);
                deferred.resolve();
            });

            return deferred.promise();
        },

        /**
         * Appends panel items to a [Coral.Table]{Coral.Table}
         *
         * @private
         * @param {Object[]} items Array of data items
         * @param {String} items[].id Item ID (path)
         * @param {String} items[].title Item title
         */
        _createItems: function(items) {
            var activeIndex = this._panelContainer.getActiveIndex();
            this._elements.reorderButtons = [];

            for (var i = 0; i < items.length; i++) {
                var row = this._elements.table.items.add({});
                row.dataset.id = items[i].id;
                row.dataset.name = items[i].name;
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
                this._elements.reorderButtons.push(button);

                row.appendChild(titleCell);
                row.appendChild(dragHandleCell);

                if (activeIndex === i) {
                    row.selected = true;
                }
            }

            // set the height of the table based on its content to allow scrolling when dragging rows
            this._elements.table.style.height = this._elements.table.offsetHeight + "px";
        },

        /**
         * Binds interaction events
         *
         * @private
         */
        _bindEvents: function() {
            var that = this;

            // escape key
            $(document).off("keyup" + NS_PANELSELECTOR).on("keyup" + NS_PANELSELECTOR, function(event) {
                if (event.keyCode === 27) {
                    that._finish();
                }
            });

            // out of area click
            document.removeEventListener("click", that._handleOutOfAreaClickBound);
            document.addEventListener("click", that._handleOutOfAreaClickBound, true);

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

            for (var i = 0; i < that._elements.reorderButtons.length; i++) {
                $(that._elements.reorderButtons[i]).on("mousedown", function() {
                    var selectedId = that._elements.table.selectedItem.dataset.id;

                    that._elements.table.off("coral-table:roworder").on("coral-table:roworder", function(event) {
                        that._markRowIndexes();

                        var items = that._elements.table.items.getAll();
                        var ordered = [];
                        for (var i = 0; i < items.length; i++) {
                            ordered.push(items[i].dataset.name);
                        }

                        that._panelContainer.update(ordered).done(function() {
                            ns.edit.EditableActions.REFRESH.execute(that._config.editable).done(function() {
                                that._config.editable.overlay.setSelected(true);

                                // update the Panel Container element following refresh
                                var element = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(that._config.editable);
                                that._panelContainer.setElement(element);

                                that._navigate();
                            });
                        });

                        if (event.detail.row.dataset.id === selectedId) {
                            event.detail.row.selected = true;
                        }
                    });
                });
            }

            // reposition the popover with overlay change,
            // as the editable toolbar can jump following navigation to a panel
            channel.off("cq-overlays-repositioned" + NS_PANELSELECTOR).on("cq-overlays-repositioned" + NS_PANELSELECTOR, function() {
                if (that._elements.popover) {
                    that._elements.popover.reposition();
                }
            });
        },

        /**
         * Handles clicks outside of the Panel Selector popover
         *
         * @private
         * @param {jQuery.Event} event The click event
         */
        _handleOutOfAreaClick: function(event) {
            var that = this;
            if (!$(event.target).closest(that._elements.popover).length) {
                that._finish();
            }
        },

        /**
         * Sets the text content of a row's index span
         * to match to its actual position in the [Coral.Table]{Coral.Table}
         *
         * @private
         */
        _markRowIndexes: function() {
            var rows = this._elements.table.items.getAll();

            for (var i = 0; i < rows.length; i++) {
                var indexSpan = rows[i].querySelector(selectors.indexMarker);
                if (indexSpan) {
                    indexSpan.textContent = i + 1;
                }
            }
        },

        /**
         * Reads the selected item from the [Coral.Table]{Coral.Table} and
         * calls the [Panel Container]{@link CQ.CoreComponents.panelcontainer.v1.PanelContainer} to handle the navigation
         *
         * @private
         */
        _navigate: function() {
            var selectedItem = this._elements.table.selectedItem;

            if (selectedItem) {
                var index = Array.prototype.slice.call(selectedItem.parentElement.children).indexOf(selectedItem);
                this._panelContainer.navigate(index);
            }
        },

        /**
         * Unbinds event handlers
         *
         * @private
         */
        _unbindEvents: function() {
            var that = this;
            $(document).off("click" + NS_PANELSELECTOR);
            that._elements.table.off("coral-table:change");
            for (var i = 0; i < that._elements.reorderButtons.length; i++) {
                $(that._elements.reorderButtons[i]).off("mousedown");
            }
        },

        /**
         * Finishes panel selection session. Cleans up Panel Selector.
         *
         * @private
         */
        _finish: function() {
            var that = this;

            if (that._elements.popover && that._elements.popover.parentNode) {
                that._elements.popover.open = false;
                that._unbindEvents();
                that._elements.popover.parentNode.removeChild(that._elements.popover);
            }
        }
    });

    /**
     * Toolbar action that presents a UI for selecting and reordering child
     * items of a toggleable panel container component for display.
     */
    var panelSelect = new ns.ui.ToolbarAction({
        name: "PANEL_SELECT",
        text: Granite.I18n.get("Select panel"),
        icon: "multipleCheck",
        order: "before COPY",
        execute: function(editable, param, target) {
            if (!panelSelector || !panelSelector.isOpen()) {
                panelSelector = new PanelSelector({
                    "editable": editable,
                    "target": target[0]
                });
            }

            // do not close the toolbar
            return false;
        },
        condition: function(editable) {
            var isPanelContainer = false;
            var children = [];

            if (CQ && CQ.CoreComponents && CQ.CoreComponents.panelcontainer &&
                CQ.CoreComponents.panelcontainer.v1 && CQ.CoreComponents.panelcontainer.v1.utils) {
                isPanelContainer = CQ.CoreComponents.panelcontainer.v1.utils.isPanelContainer(editable);
                children = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerItems(editable);
            }

            return (children.length > 0 && isPanelContainer);
        },
        isNonMulti: true
    });

    channel.on("cq-layer-activated", function(event) {
        if (event.layer === "Edit" || event.layer === "structure" || event.layer === "initial") {
            ns.EditorFrame.editableToolbar.registerAction("PANEL_SELECT", panelSelect);
        }
    });

    /**
     * Retrieves a title from item data. If no item data exists, or it doesn't have a title
     * instead lookup the editable display name of the corresponding [Editable]{@link Granite.author.Editable}.
     * Prefixes each title with an index.
     *
     * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} representing the item
     * @param {Object} item The item data
     * @param {Number} index Index of the item
     * @returns {String} The title
     */
    function getTitle(editable, item, index) {
        var title = "<span class='foundation-layout-util-subtletext cmp-panelselector__indexMarker'>" + index + "</span>&nbsp;&nbsp;";
        var subTitle = "";

        title = title + " " + Granite.I18n.getVar(ns.editableHelper.getEditableDisplayableName(editable));

        if (item) {
            if (item[PN_PANEL_TITLE]) {
                subTitle = item[PN_PANEL_TITLE];
            } else if (item.title) {
                subTitle = item.title;
            }
        }

        if (subTitle) {
            title = title + ": <span class='foundation-layout-util-subtletext'>" + subTitle + "</span>";
        }

        return title;
    }

}(jQuery, Granite.author, jQuery(document), this));
