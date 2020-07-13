/*###############################################################################
# Copyright 2020 Adobe
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
###############################################################################*/

(function ($, ns, channel, window) {

    "use strict";
    var placeholderClass = 'cq-placeholder';
    var newComponentClass = 'new';
    var sectionClass = 'section';
    var newComponentPlaceholderText = Granite.I18n.get('Drag components here');
    var placeholderDataElementClass = "placeholder-text-"



    /**
     * Indicates if the Inspectable has a placeholder element
     * @returns {false|String} Returns the placeholder string or false if no placeholder exists
     */
    ns.Inspectable.prototype.hasPlaceholder = function() {
        if( !this.onPage() ) {
            return false;
        }

        // New component placeholder (drag components here)
        if (this.dom.hasClass(newComponentClass)) {
             //return newComponentPlaceholderText;
            var editableEl = this.dom.parents(".cq-Editable-dom[data-placeholder-text],.cq-Editable-dom [data-placeholder-text]").first();
        if(editableEl.length > 0) {
            console.log(editableEl.data('placeholder-text'));
            var placeholderHint = editableEl.data('placeholder-text');
            var elClasses = this.dom.attr("class").split(" ");
            for(var i = 0; i < elClasses.length; i++) {
                var placeholderHintSelector = editableEl.data(`placeholderDataElementClass ${elClasses[i]}` );
                if(placeholderHintSelector) {
                    placeholderHint = placeholderHintSelector;
                    break;
                }
            }
            if(placeholderHint) {
                return Granite.I18n.get(placeholderHint);
            }
        }
        return newComponentPlaceholderText;
        }

        // Placeholder for empty component
        var placeholder;
        if (this.dom.hasClass(placeholderClass)) {
            /// The dom is directly marked as placeholder
            placeholder = this.dom;
        } else {
            // The dom isn't marked as placeholder, but it might contain a nested placeholder
            // when the inspectable is a drop target or is inplace editable
            if (this.config.editConfig &&
                (this.config.editConfig.dropTarget || this.config.editConfig.inplaceEditingConfig)) {

                var inspectable = this;
                placeholder = inspectable.dom
                    .find(`.${placeholderClass}`)
                    .filter(function() {
                        // Filter out nested placeholders that are part of another inspectable
                        return inspectable.dom.is($(this).closest(".cq-Editable-dom"));
                    });
            } else {
                // The inspectable can have a direct child as placeholder to allow user interaction
                // (e.g., "allowed components" in template editor)
                placeholder = this.dom.find(`> .${placeholderClass}`);
            }
        }

        return placeholder && placeholder.length ?
            Granite.I18n.getVar(placeholder.data("emptytext")) : false;
    };

    /**
     * Indicates if the Inspectable has some actions defined in its config (to be compatible with the Editable interface)
     * @returns {false} Returns always false as Inspectable don't offer any action executable on them
     */
    ns.Inspectable.prototype.hasAction = function () {
        return false;
    };

    /**
     * Indicates if the Inspectable has some actions available on it (to be compatible with the Editable interface)
     * @returns {boolean} Returns always true otherwise the Inspectable is ignored/considered disabled
     */
    ns.Inspectable.prototype.hasActionsAvailable = function() {
        return true;
    };

    /**
     * No operation (to be compatible with the Editable interface)
     */
    ns.Inspectable.prototype.updateConfig = function() {
    };


    /**
     * Outputs the current position of the Inspectable on the screen
     * @return {Object} Returns the position coordinates (top, left) and width/height values
     */
    ns.Inspectable.prototype.getArea = function() {
        if (!this.onPage() ) {
            return null;
        }

        if (!this.dom[0].getBoundingClientRect) {
            return {top: 0, left: 0, width: 0, height: 0};
        }

        var rect = this.dom[0].getBoundingClientRect();

        return {
            top: rect.top,
            left: rect.left,
            width: rect.width,
            height: rect.height
        };
    };

    /**
     * Indicates if the Inspectable is present on the current page
     * @return {boolean} Returns true if the Inspectable has a corresponding dom element
     */
    ns.Inspectable.prototype.onPage = function() {
        return this.dom !== null;
    };

    /**
     * Returns the name of the corresponding JCR content node
     * @return {String} Returns the name (last part) of the JCR content node
     */
    ns.Inspectable.prototype.getNodeName = function() {
        var p = this.path.split('/');

        return p[p.length - 1];
    };

    /**
     * Returns the name of the sling resource type of the corresponding component (which could be considered as the component name)
     * @return {String} Returns the name (last part) of the resource type (= component name)
     */
    ns.Inspectable.prototype.getResourceTypeName = function() {
        var p = this.type.split('/');

        return p[p.length - 1];
    };

    /**
     * Returns the JCR path of the parent Inspectable
     * @return {String} Returns the content path of the parent node
     */
    ns.Inspectable.prototype.getParentPath = function() {
        var p = this.path;

        return p.substr(0, p.lastIndexOf('/'));
    };

    /**
     * Returns the sling resource type of the parent Inspectable
     * @return {String} Returns the sling resource type of the parent Inspectable
     */
    ns.Inspectable.prototype.getParentResourceType = function() {
        var parent = ns.editables.getParent(this);

        return parent ?
            parent.type : undefined;
    };

    /**
     * Returns all the parents of the Inspectable
     * @return {Inspectable[]} Returns all the parents of the Inspectable
     */
    ns.Inspectable.prototype.getAllParents = function() {
        var parents = [],
            parent = ns.editables.getParent(this);

        while (parent) {
            parents.push(parent);
            parent = ns.editables.getParent(parent);
        }
        return parents;
    };

    /**
     * Returns the type name (= "Inspectable")
     * @return {String} Returns the type name of the Inspectable class
     */
    ns.Inspectable.prototype.getTypeName = function() {
        return 'Inspectable';
    };

    /**
     * Indicates if the Inspectable represents a "new section" component
     * @returns {boolean} Returns true if the Inspectable represents a "new section" component
     */
    ns.Inspectable.prototype.isNewSection = function() {
        var $dom = $(this.dom);
        return $dom.hasClass(newComponentClass) && $dom.hasClass(sectionClass);
    };

    /**
     * Indicates if the Inspectable is the root
     *
     * @returns {boolean}
     */
    ns.Inspectable.prototype.isRoot = function () {
        return this.isContainer() && !ns.editables.getParent(this);
    };

    /**
     * Indicates if the Inspectable is a "new section" of the root
     *
     * @returns {boolean}
     */
    ns.Inspectable.prototype.isRootNewSection = function () {
        var parent = ns.editables.getParent(this);
        return this.isNewSection() && parent && parent.isRoot();
    };

    /**
     * Returns true if the Inspectable is a container
     *
     * @memberOf Granite.author.Inspectable.prototype
     *
     * @returns {boolean} Returns true if the Inspectable is a container
     */
    ns.Inspectable.prototype.isContainer = function () {
        return this.config && this.config.isContainer;
    };

    /**
     * Indicates if the Inspectable exists under an authored template structure page
     * @returns {boolean}
     */
    ns.Inspectable.prototype.isStructure = function () {
        return !!(this.config && this.config.editConfig && this.config.editConfig.structure);
    };


    /**
     * Indicates if the Inspectable exists under an authored template structure page AND is locked
     * @returns {boolean} Returns true if the Inspectable is part of the structure of an authored template AND is locked
     */
    ns.Inspectable.prototype.isStructureLocked = function () {
        return !!(this.config && this.config.editConfig && this.config.editConfig.structureLocked);
    };

    /**
     * @deprecated Use {@link Granite.author.editables.add} instead
     */
    ns.Inspectable.prototype.store = function() {
        ns.editables.add(this);
        return this;
    };
    /**
     * @deprecated use {@link Granite.author.editables.remove} instead
     */
    ns.Inspectable.prototype.unstore = function() {
        ns.editables.remove(this);
        return this;
    };
    /**
     * @deprecated Use {@link Granite.author.editables.getParent} instead
     */
    ns.Inspectable.prototype.getParent = function() {
        return ns.editables.getParent(this);
    };
    /**
     *
     * @deprecated Use {@link Granite.author.editables.getChildren} instead
     */
    ns.Inspectable.prototype.getChildren = function() {
        return ns.editables.getChildren(this);
    };

    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.setSelected = function() {
        if (this.overlay) {
            this.overlay.setSelected();
        }

        return this;
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.setDisabled = function (condition) {
        if (this.overlay) {
            this.overlay.setDisabled(condition);
        }

        return this;
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.setUnselected = function() {
        if (this.overlay) {
            this.overlay.setSelected(false);
        }

        return this;
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.setActive = function() {
        if (this.overlay) {
            this.overlay.setActive();
        }

        return this;
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.setInactive = function() {
        if (this.overlay) {
            this.overlay.setActive(false);
        }

        return this;
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.isSelected = function() {
        return this.overlay.isSelected();
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.isActive = function() {
        return this.overlay.isActive();
    };
    /**
     * @deprecated Use Overlay API instead
     */
    ns.Inspectable.prototype.isDisabled = function () {
        return this.overlay.isDisabled();
    };

}(jQuery, Granite.author, jQuery(document)));
