/*******************************************************************************
 * Copyright 2018 Adobe
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

    var NS = ".cmp-childreneditor";
    var NN_PREFIX = "item_";
    var PN_PANEL_TITLE = "cq:panelTitle";
    var PN_RESOURCE_TYPE = "sling:resourceType";
    var PN_COPY_FROM = "./@CopyFrom";
    var POST_SUFFIX = ".container.html";

    var selectors = {
        self: "[data-cmp-is='childrenEditor']",
        add: "[data-cmp-hook-childreneditor='add']",
        insertComponentDialog: {
            self: "coral-dialog.InsertComponentDialog",
            selectList: "coral-selectlist"
        },
        item: {
            icon: "[data-cmp-hook-childreneditor='itemIcon']",
            input: "[data-cmp-hook-childreneditor='itemTitle']",
            hiddenItemResourceType: "[data-cmp-hook-childreneditor='itemResourceType']",
            hiddenItemTemplatePath: "[data-cmp-hook-childreneditor='itemTemplatePath']"
        }
    };

    /**
     * @typedef {Object} ChildrenEditorConfig Represents a Children Editor configuration object
     * @property {HTMLElement} el The HTMLElement representing this Children Editor
     */

    /**
     * Children Editor
     *
     * @class ChildrenEditor
     * @classdesc A Children Editor is a dialog component based on a multifield that allows editing (adding, removing, renaming, re-ordering)
     * the child items of panel container components.
     * @param {ChildrenEditorConfig} config The Children Editor configuration object
     */
    var ChildrenEditor = function(config) {
        this._config = config;
        this._elements = {};
        this._path = "";
        this._orderedChildren = [];
        this._deletedChildren = [];
        this._init();

        var that = this;
        $(window).adaptTo("foundation-registry").register("foundation.adapters", {
            type: "cmp-childreneditor",
            selector: selectors.self,
            adapter: function() {
                return {
                    items: function() {
                        var items = [];
                        that._elements.self.items.getAll().forEach(function(item) {
                            var component = item.querySelector(selectors.item.icon + " [title]").getAttribute("title");
                            var title = item.querySelector(selectors.item.input);
                            var name = (title && title.name) ? title.name.match(".?/?(.+)/.*")[1] : "";
                            var description = component + ((title && title.value) ? ": " + title.value : "");
                            items.push({
                                name: name,
                                description: description
                            });
                        });
                        return items;
                    }
                };
            }
        });
    };

    ChildrenEditor.prototype = (function() {

        return {

            constructor: ChildrenEditor,

            /**
             * Persists item updates to an endpoint, returns a Promise for handling
             *
             * @returns {Promise} The promise for completion handling
             */
            update: function() {
                var url = this._path + POST_SUFFIX;

                this._processChildren();

                return $.ajax({
                    type: "POST",
                    url: url,
                    async: false,
                    data: {
                        "delete": this._deletedChildren,
                        "order": this._orderedChildren
                    }
                });
            },

            /**
             * Initializes the Children Editor
             *
             * @private
             */
            _init: function() {
                this._elements.self = this._config.el;
                this._elements.add = this._elements.self.querySelectorAll(selectors.add)[0];
                this._path = this._elements.self.dataset["containerPath"];

                // store a reference to the Children Editor object
                $(this._elements.self).data("childrenEditor", this);

                this._bindEvents();
            },

            /**
             * Renders a component icon
             *
             * @private
             * @param {Granite.author.Component} component The component to render the icon for
             * @returns {HTMLElement} The rendered icon
             */
            _renderIcon: function(component) {
                var iconHTML;
                var iconName = component.componentConfig.iconName;
                var iconPath = component.componentConfig.iconPath;
                var abbreviation = component.componentConfig.abbreviation;

                if (iconName) {
                    iconHTML = new Coral.Icon().set({
                        icon: iconName
                    });
                } else if (iconPath) {
                    iconHTML = document.createElement("img");
                    iconHTML.src = iconPath;
                } else {
                    iconHTML = new Coral.Tag().set({
                        color: "grey",
                        size: "M",
                        label: {
                            textContent: abbreviation
                        }
                    });
                    iconHTML.classList.add("cmp-childreneditor__item-tag");
                }

                iconHTML.title = component.getTitle();

                return iconHTML;
            },

            /**
             * Binds Children Editor events
             *
             * @private
             */
            _bindEvents: function() {
                var that = this;

                if (ns) {
                    Coral.commons.ready(that._elements.add, function() {
                        that._elements.add.on("click", function() {
                            var editable = ns.editables.find(that._path)[0];
                            var children = editable.getChildren();

                            // create the insert component dialog relative to a child item
                            // - against which allowed components are calculated.
                            if (children.length > 0) {
                                // display the insert component dialog
                                ns.edit.ToolbarActions.INSERT.execute(children[0]);

                                var insertComponentDialog = $(document).find(selectors.insertComponentDialog.self)[0];
                                var selectList = insertComponentDialog.querySelectorAll(selectors.insertComponentDialog.selectList)[0];

                                // next frame to ensure we remove the default event handler
                                Coral.commons.nextFrame(function() {
                                    selectList.off("coral-selectlist:change");
                                    selectList.on("coral-selectlist:change" + NS, function(event) {
                                        var resourceType = "";
                                        var componentTitle = "";
                                        var templatePath = "";

                                        insertComponentDialog.hide();

                                        var components = ns.components.find(event.detail.selection.value);
                                        if (components.length > 0) {
                                            resourceType = components[0].getResourceType();
                                            componentTitle = components[0].getTitle();
                                            templatePath = components[0].getTemplatePath();

                                            var item = that._elements.self.items.add(new Coral.Multifield.Item());

                                            // next frame to ensure the item template is rendered in the DOM

                                            Coral.commons.nextFrame(function() {
                                                var name = NN_PREFIX + Date.now();
                                                item.dataset["name"] = name;

                                                var input = item.querySelectorAll(selectors.item.input)[0];
                                                input.name = "./" + name + "/" + PN_PANEL_TITLE;
                                                input.placeholder = Granite.I18n.get(componentTitle);

                                                var hiddenItemResourceType = item.querySelectorAll(selectors.item.hiddenItemResourceType)[0];
                                                hiddenItemResourceType.value = resourceType;
                                                hiddenItemResourceType.name = "./" + name + "/" + PN_RESOURCE_TYPE;
                                                if (templatePath) {
                                                    var hiddenItemTemplatePath = item.querySelectorAll(selectors.item.hiddenItemTemplatePath)[0];
                                                    hiddenItemTemplatePath.value = templatePath;
                                                    hiddenItemTemplatePath.name = "./" + name + "/" + PN_COPY_FROM;
                                                }

                                                var itemIcon = item.querySelectorAll(selectors.item.icon)[0];
                                                var icon = that._renderIcon(components[0]);
                                                itemIcon.appendChild(icon);

                                                that._elements.self.trigger("change");
                                            });
                                        }
                                    });
                                });
                                // unbind events on dialog close
                                channel.one("coral-overlay:beforeclose", function() {
                                    selectList.off("coral-selectlist:change" + NS);
                                });
                            }
                        });
                    });
                } else {
                    // editor layer unavailable, remove the insert component action
                    that._elements.add.parentNode.removeChild(that._elements.add);
                }

                Coral.commons.ready(that._elements.self, function() {
                    that._elements.self.on("coral-collection:remove", function(event) {
                        var name = event.detail.item.dataset["name"];
                        that._deletedChildren.push(name);
                    });

                    that._elements.self.on("coral-collection:add", function(event) {
                        var name = event.detail.item.dataset["name"];
                        var index = that._deletedChildren.indexOf(name);

                        if (index > -1) {
                            that._deletedChildren.splice(index, 1);
                        }
                    });
                });
            },

            /**
             * Reads the current state and updates ordered children cache
             *
             * @private
             */
            _processChildren: function() {
                this._orderedChildren = [];
                var items = this._elements.self.items.getAll();

                for (var i = 0; i < items.length; i++) {
                    var name = items[i].dataset["name"];
                    this._orderedChildren.push(name);
                }
            }
        };
    })();

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
            if (childrenEditor) {
                return {
                    post: function() {
                        return childrenEditor.update();
                    }
                };
            } else {
                return {};
            }
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
