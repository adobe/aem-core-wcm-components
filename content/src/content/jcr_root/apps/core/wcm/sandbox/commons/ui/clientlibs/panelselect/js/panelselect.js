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
(function($, ns, channel, window) {
    "use strict";

    /**
     * Toolbar action that presents a UI for selecting and reordering child
     * items of a toggleable panel container component for display.
     */
    var panelSelect = new ns.ui.ToolbarAction({
        name: "PANEL_SELECT",
        text: Granite.I18n.get("Select panel"),
        icon: "multipleCheck",
        order: "after CONFIGURE",
        execute: function(editable, param, target) {
            new CQ.CoreComponents.PanelSelector({
                "editable": editable,
                "target": target[0]
            });

            // do not close the toolbar
            return false;
        },
        condition: function(editable) {
            var isPanelContainer = false;
            if (CQ && CQ.CoreComponents && CQ.CoreComponents.panelcontainer && CQ.CoreComponents.panelcontainer.utils) {
                isPanelContainer = CQ.CoreComponents.panelcontainer.utils.isPanelContainer(editable);
            }

            var children = CQ.CoreComponents.panelcontainer.utils.getPanelContainerItems(editable);

            return (children.length > 1 && isPanelContainer);
        },
        isNonMulti: true
    });

    channel.on("cq-layer-activated", function(event) {
        if (event.layer === "Edit") {
            ns.EditorFrame.editableToolbar.registerAction("PANEL_SELECT", panelSelect);
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
