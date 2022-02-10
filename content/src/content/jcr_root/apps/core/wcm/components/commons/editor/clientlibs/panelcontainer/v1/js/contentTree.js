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
(function($, ns, channel) {
    "use strict";

    var selectors = {
        contentTree: ".editor-ContentTree coral-tree"
    };

    /**
     * Handling to ensure the {@link Granite.author.ui.ContentTree} selection logic functions
     * with Panel Containers.
     */

    /**
     * Disable/enable {@link Granite.author.ui.Overlay}s for Panel Container items on navigate.
     * The overlays of the non-active Panel Container items are disabled.
     * Necessary for preventing the default Content Tree selection logic from triggering a click on
     * overlays that aren't visible in the Content Frame.
     *
     * @param {jQuery.Event} event The panel navigation event Object
     */
    channel.on("cmp-panelcontainer-navigated", function(event) {
        var editables = ns.editables.find(event.id);
        if (editables.length) {
            updatePanelContainerOverlayState(editables[0]);
        }
    });

    /**
     * Ensure Panel Container child Editable overlay disabled states are updated
     * when the Editor is loaded.
     *
     * @param {jQuery.Event} event The editor loaded event Object
     */
    channel.on("cq-editor-loaded", function(event) {
        var editables = ns.editables;

        for (var i = 0; i < editables.length; i++) {
            var editable = editables[i];

            if (CQ.CoreComponents.panelcontainer.v1.utils.isPanelContainer(editable)) {
                updatePanelContainerOverlayState(editable);
            }
        }
    });

    /**
     * Bind content tree change events to replace the default selection logic of the
     * {@link Granite.author.ui.ContentTree} for Panel Container items.
     *
     * @param {jQuery.Event} event The sidepanel tab switched event Object
     */
    channel.on("cq-sidepanel-tab-switched", function(event) {
        Coral.commons.nextFrame(function() {
            var $contentTree = $(selectors.contentTree);

            if ($contentTree.length) {
                var contentTree = $contentTree[0];

                contentTree.off("coral-tree:change.panelcontainer").on("coral-tree:change.panelcontainer", function() {
                    var selectedItem = contentTree.selectedItem;

                    if (selectedItem) {
                        var editable = ns.editables.find(selectedItem.value)[0];
                        var editableParent = editable.getParent();
                        var panelContainer;
                        var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(editableParent);
                        if (panelContainerType) {
                            panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                                path: editableParent.path,
                                panelContainerType: panelContainerType,
                                el: CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editableParent)
                            });

                            var index = Array.prototype.indexOf.call(selectedItem.parentElement.children, selectedItem);

                            channel.one("cmp-panelcontainer-navigated", function() {
                                editable.overlay.dom.focus();
                                ns.selection.deselectAll();
                                ns.selection.deactivateCurrent();
                                ns.selection.select(editable);
                                ns.selection.activate(editable);

                                var active = ns.selection.getCurrentActive();

                                // If an editable has received the focus
                                if (active) {
                                    channel.trigger($.Event("cq-interaction-focus", {
                                        editable: active
                                    }));
                                }
                            });

                            panelContainer.navigate(index);
                        }
                    }
                });
            }
        });
    });

    /**
     * Toggles Panel Container items' overlay disabled state based on the current active index
     *
     * @param {Granite.author.Editable} editable The Panel Container {@link Granite.author.Editable}
     */
    function updatePanelContainerOverlayState(editable) {
        var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(editable);
        var panelContainer;

        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable)
            });
        }

        var activeIndex = panelContainer.getActiveIndex();
        var items = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerItems(editable);

        for (var i = 0; i < items.length; i++) {
            if (items[i].overlay) {
                items[i].overlay.setDisabled((activeIndex !== i));
            }
        }
    }

}(jQuery, Granite.author, jQuery(document)));
