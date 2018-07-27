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

    CQ.CoreComponents.panelcontainer.AFTER_CHILD_INSERT = function(childEditable) {
        var editable = ns.editables.getParent(childEditable);
        var path = childEditable.path;
        var panelContainer;
        var isSelected = editable.overlay.isSelected();

        var panelContainerType = CQ.CoreComponents.panelcontainer.utils.getPanelContainerType(editable);
        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: editable.dom
            });
        }

        ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
            var children = [];
            var index = 0;

            if (isSelected) {
                editable.overlay.setSelected(true);
            }

            if (editable.isContainer()) {
                children = editable.getChildren().filter(isDisplayable);
            }

            for (var i = 0; i < children.length; i++) {
                if (children[i].path === path) {
                    index = i;
                    break;
                }
            }

            panelContainer.navigate(index);
        });
    };

    CQ.CoreComponents.panelcontainer.AFTER_CHILD_DELETE = function(childEditable) {
        var editable = ns.editables.getParent(childEditable);
        var panelContainer;

        var panelContainerType = CQ.CoreComponents.panelcontainer.utils.getPanelContainerType(editable);
        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: editable.dom
            });
        }

        var index = panelContainer.getActiveIndex() - 1;

        ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
            if (!(index < 0)) {
                panelContainer.navigate(index);
            }
        });
    };

    /**
     * Test whether an [Editable]{@link Granite.author.Editable} is displayable in the panel popover.
     * Ignore [Inspectables]{@link Granite.author.Inspectable} and Placeholders.
     *
     * // TODO - make reusable, it's also in PanelSelector, could move to PanelContainer utils
     *
     * @param {Granite.author.Editable} editable The [Editable]{@link Granite.author.Editable} to test
     * @returns {Boolean} Whether the [Editable]{@link Granite.author.Editable} is displayed in the panel popover, or not
     */
    function isDisplayable(editable) {
        return (editable instanceof ns.Editable &&
        (editable.isContainer() || (editable.hasActionsAvailable() && !editable.isNewSection())));
    }

}(jQuery, Granite.author, jQuery(document), this));
