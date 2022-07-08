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
    var SUFFIX = ".container.html";

    var selectors = {
        self: "[data-cmp-is='childrenEditor']",
        add: "[data-cmp-hook-childreneditor='add']",
        insertComponentDialog: {
            self: "coral-dialog.InsertComponentDialog",
            selectList: ".InsertComponentDialog-list"
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
                            var description = Granite.I18n.get(component) + ((title && title.value) ? ": " + Granite.I18n.get(title.value) : "");
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
                var url = this._path + SUFFIX;

                this._processChildren();

                return $.ajax({
                    type: "POST",
                    url: url,
                    async: false,
                    data: {
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

                function getSelectListChangeEvent(onCloud) {
                    return (onCloud ? "click" : "coral-selectlist:change");
                }

                function getSelectListSelector(onCloud) {
                    return (onCloud ? "coral-list-item" : null);
                }

                function getSelectListItems(event, onCloud) {
                    return (onCloud ? ns.components.find(event.target.closest("coral-list-item").value)
                        : ns.components.find(event.detail.selection.value));
                }

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
                                var onCloud = selectList.toString() === "Coral.List";

                                // next frame to ensure we remove the default event handler
                                Coral.commons.nextFrame(function() {

                                    selectList.off(getSelectListChangeEvent(onCloud));
                                    selectList.on(getSelectListChangeEvent(onCloud) + NS, getSelectListSelector(onCloud), function(event) {
                                        var resourceType = "";
                                        var componentTitle = "";
                                        var templatePath = "";
                                        var components = "";
                                        insertComponentDialog.hide();

                                        components = getSelectListItems(event, onCloud);

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
                                    selectList.off(getSelectListChangeEvent(onCloud) + NS);
                                });
                            }
                        });
                    });
                } else {
                    // editor layer unavailable, remove the insert component action
                    that._elements.add.parentNode.removeChild(that._elements.add);
                }

                Coral.commons.ready(that._elements.self, function(item) {
                    // As a reordering of the multifield also triggers the coral-collection:remove event we have to add
                    // a check for moved items so the prompt get only shown on a real remove action.
                    var movedItem;

                    that._elements.self.on("coral-multifield:itemorder", function(event) {
                        movedItem = event.detail.item.dataset["name"];
                    });

                    that._elements.self.on("coral-collection:remove", function(event) {
                        var name = event.detail.item.dataset["name"];
                        if (movedItem !== name) {
                            ns.ui.helpers.prompt({
                                title: Granite.I18n.get("Delete"),
                                message: Granite.I18n.get("You are going to delete the selected component(s)."),
                                type: ns.ui.helpers.PROMPT_TYPES.WARNING,
                                actions: [
                                    {
                                        id: "CANCEL",
                                        text: Granite.I18n.get("Cancel", "Label for Cancel button")
                                    },
                                    {
                                        id: "DELETE",
                                        text: Granite.I18n.get("Delete", "Label for Confirm button"),
                                        warning: true
                                    }
                                ],
                                callback: function(actionId) {
                                    if (actionId === "CANCEL") {
                                        that._update(that._path + SUFFIX);
                                    } else {
                                        that._sendDeleteParagraph(that._path + "/" + name)
                                            .then(function(data) {
                                                that._update(that._path + SUFFIX);
                                            });
                                    }
                                }
                            });
                        }
                    });

                    that._elements.self.on("coral-collection:add", function(event) {
                        if (movedItem !== event.detail.item.dataset["name"]) {
                            movedItem = undefined;
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
            },

            _sendDeleteParagraph: function(path) {
                return (new ns.persistence.PostRequest()
                    .prepareDeleteParagraph({
                        path: path
                    })
                    .send()
                );
            },

            _update: function(url) {
                var that = this;
                return $.ajax({
                    type: "GET",
                    url: url,
                    dataType: "html",
                    async: false,
                    success: function(data) {
                        var tmp = document.createElement("div");
                        tmp.innerHTML = data;
                        var multifield = tmp.firstElementChild;
                        that._elements.self.replaceWith(multifield);
                        channel.trigger("foundation-contentloaded");
                    }
                });
            }
        };
    })();

    /**
     * Initializes Children Editors as necessary on content loaded event
     */
    channel.on("foundation-contentloaded", function(event) {
        $(event.target).find(selectors.self).each(function() {
            // prevent multiple initialization
            if ($(this).data("childrenEditor") === undefined) {
                new ChildrenEditor({
                    el: this
                });
            }
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
