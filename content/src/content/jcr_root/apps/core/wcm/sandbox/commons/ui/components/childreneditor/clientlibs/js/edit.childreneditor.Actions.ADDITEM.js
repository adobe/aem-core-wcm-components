/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */
;(function ($, ns, channel, window) {
    "use strict";

    /*
    Below code has been copied from edit.ToolbarActions.INSERT.js [0] and tweaked to:
    - display the list of allowed components to choose from
    - return the resource type of the selected component

    TODO: once the concept and the UX is tested/validated, it should be improved

    [0] https://git.corp.adobe.com/CQ/ui-wcm-editor/blob/master/content/jcr_root/libs/cq/gui/components/authoring/editors/clientlibs/core/js/edit/ToolbarActions/edit.ToolbarActions.INSERT.js
     */

    var dialog = null,
        selectList = null,
        resourceType = null,
        $searchComponent = null;

    function filterComponent (allowedComponents) {
        var components = ns.components.allowedComponents.sort(ns.components.sortComponents),
            groups = {},
            keyword = $searchComponent[0].value,
            regExp = null;

        // rebuild the selectList entries
        selectList.groups.clear();
        selectList.items.clear();

        if (keyword !== undefined && keyword !== null) {
            keyword = keyword.trim();
        }
        else {
            keyword = "";
        }

        if (keyword.length > 0) {
            regExp = new RegExp(".*" + keyword + ".*", "i");
        }

        components.forEach(function(c) {
            var cfg = c.componentConfig;
            var g;

            if (keyword.length > 0) {
                var isKeywordFound = regExp.test(Granite.I18n.getVar(cfg.title));
            }

            if ( !(keyword.length > 0) || isKeywordFound) {

                var componentAbsolutePath = c.componentConfig.path,
                    componentRelativePath = componentAbsolutePath.replace(/^\/[a-z]+\//, "");

                // allowedComponents (coming from page design) could contain relative paths (sling search paths omitted)
                if (allowedComponents.indexOf(componentAbsolutePath) > -1 ||
                    allowedComponents.indexOf(componentRelativePath) > -1 ||
                    allowedComponents.indexOf("group:" + c.getGroup()) > -1) {
                    g = c.getGroup();

                    var group = document.createElement('coral-selectlist-group');
                    group.label = Granite.I18n.getVar(g);
                    groups[g] = groups[g] || group;

                    var item = document.createElement('coral-selectlist-item');
                    item.value = cfg.path;
                    item.innerHTML = Granite.I18n.getVar(cfg.title);

                    groups[g].items.add(item);
                }
            }
        });

        Object.keys(groups).forEach(function (g) {
            selectList.groups.add(groups[g]);
        });
    }

	function createInsertComponentDialog() {
	    if (!dialog) {
	        dialog = new Coral.Dialog().set({
	            closable: Coral.Dialog.closable.ON,
	            header: {
	                innerHTML: Granite.I18n.get('Insert New Component')
	            },
	            content: {
	                innerHTML: '<coral-search class="InsertComponentDialog-search" placeholder="' + Granite.I18n.get("Enter Keyword") + '"></coral-search><coral-selectlist class="InsertComponentDialog-list"></coral-selectlist>'
	            }
	        });

	        dialog.classList.add('InsertComponentDialog');
	        dialog.content.classList.add('InsertComponentDialog-components');
	        $searchComponent = $(dialog).find('.InsertComponentDialog-search');
	        document.body.appendChild(dialog);
	    } else {
	        $searchComponent[0].value = ""; //if dialog already initialized then remove the filter text
	    }
	}

	function bindEventToInsertComponentDialog (allowedComponents, editable) {
	    $searchComponent.off("keydown.insertComponent.coral-search");
	    $searchComponent.on("keydown.insertComponent.coral-search", $.debounce(150, function(event) {
	        filterComponent(allowedComponents);
	    }));

      // clear search input handling
      $searchComponent[0].off("coral-search:clear").on("coral-search:clear", function() {
          if ($searchComponent[0].value.trim().length) {
              $searchComponent[0].value = "";
              filterComponent(allowedComponents);
          }
      });

	    selectList.off('coral-selectlist:change').on('coral-selectlist:change', function(event) {
	        selectList.off('coral-selectlist:change');

	        dialog.hide();

	        var component = ns.components.find(event.detail.selection.value);

	        if (component.length > 0) {
                resourceType = component[0].getResourceType();
	        }
	    });
	}

    ns.edit.childreneditor = ns.edit.childreneditor || {};
    ns.edit.childreneditor.Actions = ns.edit.childreneditor.Actions || {};

    ns.edit.childreneditor.Actions.ADDITEM = ns.edit.childreneditor.Actions.ADDITEM || {};

    ns.edit.childreneditor.Actions.ADDITEM.getResourceType = function() {
        return resourceType;
    };

    ns.edit.childreneditor.Actions.ADDITEM.execute = function(editable) {
        var parent = ns.editables.getParent(editable),
            allowedComponents = ns.components.computeAllowedComponents(parent, ns.pageDesign); // TODO Review allowedComponents

        createInsertComponentDialog();

        Coral.commons.ready(dialog, function() {
            selectList = $(dialog).find('coral-selectlist')[0];
            filterComponent(allowedComponents);
            bindEventToInsertComponentDialog(allowedComponents, editable);

            dialog.show();
        });
    };

}(jQuery, Granite.author, jQuery(document), this));
