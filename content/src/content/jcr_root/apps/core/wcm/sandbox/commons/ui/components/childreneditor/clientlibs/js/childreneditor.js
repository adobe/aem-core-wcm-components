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
/* global CQ */
(function($, ns, channel, window) {
    "use strict";

    var NS = ".childreneditor";
    var NN_PREFIX = "item_";
    var PN_TITLE = "jcr:title";
    var PN_RESOURCE_TYPE = "sling:resourceType";

    var selectors = {
        self: "[data-cmp-is='childreneditor']",
        add: "[data-cmp-hook-childreneditor='add']",
        insertComponentDialog: {
            self: "coral-dialog.InsertComponentDialog",
            selectList: "coral-selectlist"
        },
        item: {
            input: "[data-cmp-hook-childreneditor='itemInput']",
            hiddenInput: "[data-cmp-hook-childreneditor='itemHiddenInput']"
        }
    };

    /**
     * @class ChildrenEditor
     * @classdesc A Children Editor is a dialog component based on a multifield that allows editing (adding, removing, renaming, re-ordering)
     * the child items of panel container components.
     * @param {Object} config The Children Editor configuration object
     */
    var ChildrenEditor = ns.util.createClass({

        /**
         * The Children Editor configuration Object
         *
         * @member {Object} ChildrenEditor#_config
         */
        _config: {},

        /**
         * An Object that is used to cache HTMLElement hooks for this Class
         *
         * @member {Object} ChildrenEditor#_elements
         */
        _elements: {},

        /**
         * Path to the Container related to this Children Editor
         *
         * @member {String} ChildrenEditor#_path
         */
        _path: "",

        /**
         * Panel Container related to this Children Editor
         *
         * @member {CQ.CoreComponents.PanelContainer} ChildrenEditor#_panelContainer
         */
        _panelContainer: null,

        /**
         * Stores the deleted chidren, for processing on form submit
         *
         * @member {Array} ChildrenEditor#_deletedChildren
         */
        _deletedChildren: [],

        constructor: function ChildrenEditor(config) {
            this._config = config;
            this._elements.self = this._config.el;
            this._elements.add = this._elements.self.querySelectorAll(selectors.add)[0];
            this._path = this._elements.self.dataset["containerPath"];
            this._panelContainer = new CQ.CoreComponents.PanelContainer({
                path: this._path
            });

            // store a reference to the Children Editor object
            $(this._elements.self).data("childrenEditor", this);

            this._bindEvents();
        },

        /**
         * Binds Children Editor events
         *
         * @private
         */
        _bindEvents: function() {
            var that = this;

            that._elements.add.on("click", function() {
                var editable = ns.editables.find(that._path)[0];
                var children = editable.getChildren();

                // create the insert component dialog relative to a child item
                // - against which allowed components are calculated.
                if (children.length > 0) {
                    // display the insert component dialog
                    ns.edit.ToolbarActions.INSERT.execute(children[0]);

                    var insertComponentDialog = $(document).find(selectors.insertComponentDialog.self)[0];
                    insertComponentDialog.off("coral-overlay:open" + NS).on("coral-overlay:open" + NS, function() {
                        var selectList = insertComponentDialog.querySelectorAll(selectors.insertComponentDialog.selectList)[0];

                        // override default handling for component selection
                        selectList.off("coral-selectlist:change").on("coral-selectlist:change", function() {
                            var resourceType = "";
                            var componentTitle = "";

                            insertComponentDialog.hide();

                            var item = that._elements.self.items.add(new Coral.Multifield.Item());
                            that._elements.self.trigger("change");

                            var component = ns.components.find(event.detail.selection.value);
                            if (component.length > 0) {
                                resourceType = component[0].getResourceType();
                                componentTitle = component[0].getTitle();
                            }

                            // wait one frame to ensure the item template is rendered in the DOM
                            Coral.commons.nextFrame(function() {
                                var name = NN_PREFIX + Date.now();
                                item.dataset["name"] = name;

                                var input = item.querySelectorAll(selectors.item.input)[0];
                                input.name = "./" + name + "/" + PN_TITLE;
                                input.placeholder = Granite.I18n.get(componentTitle) + " " + Granite.I18n.get("title");

                                var hiddenInput = item.querySelectorAll(selectors.item.hiddenInput)[0];
                                hiddenInput.value = resourceType;
                                hiddenInput.name = "./" + name + "/" + PN_RESOURCE_TYPE;
                            });
                        });
                    });
                }
            });

            that._elements.self.on("coral-collection:remove", function(event) {
                var name = event.detail.item.dataset["name"];
                that._deletedChildren.push(name);
            });
        },

        /**
         * Reads state of the children and
         * triggers a POST request to add, remove and re-order child nodes
         *
         * @private
         * @returns {Promise} Promise for handling completion
         */
        _processChildren: function() {
            var items = this._elements.self.items.getAll();
            var orderedChildren = [];

            for (var i = 0; i < items.length; i++) {
                var name = items[i].dataset["name"];
                orderedChildren.push(name);
            }

            return this._panelContainer.update(orderedChildren, this._deletedChildren);
        }
    });

    /**
     * Initializes Children Editors as necessary on content loaded event
     */
    channel.on("foundation-contentloaded", function(event) {
        $(event.target).find(selectors.self).each(function() {
            new ChildrenEditor({
                el: this
            });
        });
    });

    /**
     * Form pre-submit handler to process child updates
     */
    $(window).adaptTo("foundation-registry").register("foundation.form.submit", {
        selector: "*",
        handler: function(form) {
            // one children editor per form
            var el = form.querySelectorAll(selectors.self)[0];
            var childrenEditor = $(el).data("childrenEditor");
            return {
                post: function() {
                    childrenEditor._processChildren();
                }
            };
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
