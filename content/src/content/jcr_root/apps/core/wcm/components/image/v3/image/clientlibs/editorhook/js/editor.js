/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
(function($, ns, channel, authorNs) {
    "use strict";
    ns.image.v3.actions.smartCrop = function() {
        const editable = this;
        authorNs.DialogFrame.openDialog(new ns.image.v3.smartCropDialog(editable));
    };

    /**
     * Represents the SMARTCROP action (opens a component [Dialog]{@link Granite.author.edit.Dialog}) that could be performed on an {@link Granite.author.Editable}
     *
     * @memberOf Granite.author.edit.ToolbarActions
     * @type Granite.author.ui.ToolbarAction
     * @alias SMARTCROP
     */
    authorNs.edit.ToolbarActions.SMARTCROP = new authorNs.ui.ToolbarAction({
        name: "SMARTCROP",
        icon: "cropLightning",
        text: Granite.I18n.get("Smart Crop"),
        order: "before CONFIGURE",
        execute: function openEditDialog(editable) {
            authorNs.DialogFrame.openDialog(new ns.image.v3.smartCropDialog(editable));
        },
        condition: function (editable) {
            return authorNs.pageInfoHelper.canModify() && editable.hasAction("SMARTCROP");
        },
        isNonMulti: true
    });

    channel.on("cq-layer-activated", function(e) {
        if (e.layer === "Edit") {
            if(authorNs.EditorFrame && authorNs.EditorFrame.editableToolbar) {
                authorNs.EditorFrame.editableToolbar.registerAction("SMARTCROP", authorNs.edit.ToolbarActions.SMARTCROP);
            }
        }
    });

})(jQuery, CQ.CoreComponents, jQuery(document), Granite.author);

