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
(function($, ns, components, channel) {
    "use strict";

    /**
     * An Smart Crop Dialog represents a Component Dialog opened on an Editable.
     *
     * @class
     * @alias Granite.author.edit.Dialog
     * @extends Granite.author.ui.Dialog
     *
     * @param {Granite.author.Editable} editable The Editable on which the Dialog will be opened
     */
    components.image.v3.smartCropDialog = function(editable) {
        this.editable = editable;
    };

    ns.util.inherits(components.image.v3.smartCropDialog, ns.ui.Dialog);

    components.image.v3.smartCropDialog.prototype.getConfig = function() {
        return {
            src: "/mnt/overlay/core/wcm/components/image/v3/image/smartCropDialog.html" + this.editable.path,
            loadingMode: this.editable.config.dialogLoadingMode,
            layout: this.editable.config.dialogLayout || "auto"
        };
    };

    components.image.v3.smartCropDialog.prototype.getRequestData = function() {
        return {
            resourceType: this.editable.type
        };
    };

    /**
     * Success handler.
     * @function Granite.author.edit.Dialog#onSuccess
     *
     * @fires Document#cq-persistence-after-update
     *
     * @param {jQuery} currentDialog The jQuery element representing the current dialog
     * @param {jQuery} currentFloatingDialog The jQuery element representing the current floating dialog
     */
    components.image.v3.smartCropDialog.prototype.onSuccess = function(currentDialog, currentFloatingDialog) {
        var self = this;
        var properties = {};

        if (currentFloatingDialog) {
            var propertiesArray = currentFloatingDialog.serializeArray();

            propertiesArray.forEach(function(propertyNameValue) {
                properties[propertyNameValue.name] = propertyNameValue.value;
            });
        }

        channel.trigger("cq-persistence-after-update", [this.editable,  properties]);

        var history = ns.history.Manager.getHistory();
        if (history) {
            history.clear();
        }

        // refresh the editable and recreate its overlay
        ns.edit.EditableActions.REFRESH.execute(this.editable)
            .then(function() {
                ns.selection.select(self.editable);
                self.editable.afterEdit();

                var editableParent = ns.editables.getParent(self.editable);
                editableParent && editableParent.afterChildEdit(self.editable);
            });
    };

    components.image.v3.smartCropDialog.prototype.onFocus = function() {
        if (ns.EditorFrame.editableToolbar && ns.EditorFrame.editableToolbar.close) {
            ns.EditorFrame.editableToolbar.close();
        }
    };

    components.image.v3.smartCropDialog.prototype.onOpen = function() {
        ns.history.Manager.setBlocked(true);

    };

    components.image.v3.smartCropDialog.prototype.onClose = function() {
        if (this.editable && this.editable.overlay && this.editable.overlay.dom) {
            this.editable.overlay.dom.focus();
        }
        ns.history.Manager.setBlocked(false);
    };
}(jQuery, Granite.author, window.CQ.CoreComponents, jQuery(document)));
