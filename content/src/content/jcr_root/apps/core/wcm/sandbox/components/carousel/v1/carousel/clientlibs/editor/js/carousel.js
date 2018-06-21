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
/* global
 Granite, Coral
 */
;(function ($, ns, channel, window) {
    "use strict";

    channel.on("cq-editor-loaded", function (event) {
        //alert(1);
        ns.EditorFrame.editableToolbar.registerAction("EDIT_CAROUSEL", getEditAction());
    });

    if (ns && ns.EditorFrame && ns.EditorFrame.editableToolbar) {
        alert(2);
        ns.EditorFrame.editableToolbar.registerAction("EDIT_CAROUSEL", getEditAction());
    } else {
        //alert(3);
        channel.on("cq-editor-loaded", function (event) {
            if (event.layer === "Edit") {
                alert(4);
                ns.EditorFrame.editableToolbar.registerAction("EDIT_CAROUSEL", getEditAction());
            }
        });
    }

    function getEditAction() {
        if (ns && ns.ui) {
            return new ns.ui.ToolbarAction({
                name      : "EDIT_CAROUSEL",
                text      : Granite.I18n.get("Edit Carousel Items"),
                icon      : "edit",
                order     : "first",
                execute   : function (editable) {
                    //alert(5);
                    console.log(editable);
                    console.log(editable.getChildren());
                    console.log(ns.editables.getChildren(editable, true));
                },
                condition : function (editable) {
                    // TODO: improve with super type
                    return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
                },
                isNonMulti: true
            });
        }
    }

}(jQuery, Granite.author, jQuery(document), this));
