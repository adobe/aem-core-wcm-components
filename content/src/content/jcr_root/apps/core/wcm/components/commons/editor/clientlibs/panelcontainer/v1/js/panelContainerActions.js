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
(function($, ns) {
    "use strict";

    /**
     * Handles an afterChildInsert listener for a Panel Container component.
     * Refreshes the component and navigates to the correct active item.
     *
     * @param {Granite.author.Editable} childEditable The inserted child [Editable]{@link Granite.author.Editable}
     */
    CQ.CoreComponents.panelcontainer.v1.AFTER_CHILD_INSERT = function(childEditable) {
        var editable = ns.editables.getParent(childEditable);
        var path = childEditable.path;
        var panelContainer;
        var isSelected = editable.overlay.isSelected();

        var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(editable);
        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable)
            });
        }

        ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
            var index = 0;

            if (isSelected) {
                editable.overlay.setSelected(true);
            }

            var children = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerItems(editable);

            for (var i = 0; i < children.length; i++) {
                if (children[i].path === path) {
                    index = i;
                    break;
                }
            }

            // update the Panel Container element following refresh
            var element = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable);
            panelContainer.setElement(element);

            panelContainer.navigate(index);
        });
    };

    /**
     * Handles an afterChildDelete listener for a Panel Container component.
     * Refreshes the component and navigates to the correct active item.
     *
     * @param {Granite.author.Editable} childEditable The deleted child [Editable]{@link Granite.author.Editable}
     */
    CQ.CoreComponents.panelcontainer.v1.AFTER_CHILD_DELETE = function(childEditable) {
        var editable = ns.editables.getParent(childEditable);
        var panelContainer;

        var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(editable);
        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable)
            });
        }

        var index = panelContainer.getActiveIndex() - 1;

        ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
            if (!(index < 0)) {
                // update the Panel Container element following refresh
                var element = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable);
                panelContainer.setElement(element);

                panelContainer.navigate(index);
            }
        });
    };

    /**
     * Handles an afterChildMove listener for a Panel Container component.
     * Refreshes the component and navigates to the correct active item.
     */
    CQ.CoreComponents.panelcontainer.v1.AFTER_CHILD_MOVE = function() {
        var editable = this;
        var panelContainer;

        var panelContainerType = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerType(editable);
        if (panelContainerType) {
            panelContainer = new CQ.CoreComponents.panelcontainer.v1.PanelContainer({
                path: editable.path,
                panelContainerType: panelContainerType,
                el: CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable)
            });
        }

        var index = panelContainer.getActiveIndex() - 1;

        ns.edit.EditableActions.REFRESH.execute(editable).done(function() {
            if (!(index < 0)) {
                // update the Panel Container element following refresh
                var element = CQ.CoreComponents.panelcontainer.v1.utils.getPanelContainerHTMLElement(editable);
                panelContainer.setElement(element);

                panelContainer.navigate(index);
            }
        });
    };

}(jQuery, Granite.author));
