/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2017 Adobe Systems Incorporated
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

    function getExpandAction1() {
        var action;
        if (ns && ns.ui) {
            return new ns.ui.ToolbarAction({
                name: "EXPAND_CAROUSEL1",
                text: Granite.I18n.get("Expand/Collapse Carousel Items"),
                icon: "chevronUpDown",
                execute: function (editable) {
                    // TODO: improve the selector handling
                    var path = editable.slingPath;
                    var isExpanded = path.indexOf(".expanded1.html") > 0;
                    var newPath;
                    if (isExpanded) {
                        newPath = path.slice(0,path.indexOf(".expanded1.html")) + ".html";
                    } else {
                        newPath = path.slice(0,path.indexOf(".html")) + ".expanded1.html";
                    }
                    editable.slingPath = newPath;
                    ns.edit.EditableActions.REFRESH.execute(editable, editable.config);
                    return false;
                },
                condition: function (editable) {
                    // TODO: improve with super type
                    return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
                },
                isNonMulti: true
            });
        }
        return action;
    }

    function getExpandAction2() {
        var action;
        if (ns && ns.ui) {
            return new ns.ui.ToolbarAction({
                name: "EXPAND_CAROUSEL2",
                text: Granite.I18n.get("Expand/Collapse Carousel Items"),
                icon: "chevronUpDown",
                execute: function (editable) {
                    // TODO: improve the selector handling
                    var path = editable.slingPath;
                    var isExpanded = path.indexOf(".expanded2.html") > 0;
                    var newPath;
                    if (isExpanded) {
                        newPath = path.slice(0,path.indexOf(".expanded2.html")) + ".html";
                    } else {
                        newPath = path.slice(0,path.indexOf(".html")) + ".expanded2.html";
                    }
                    editable.slingPath = newPath;
                    ns.edit.EditableActions.REFRESH.execute(editable, editable.config);
                    return false;
                },
                condition: function (editable) {
                    // TODO: improve with super type
                    return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
                },
                isNonMulti: true
            });
        }
        return action;
    }

    function getExpandAction3() {
        var action;
        if (ns && ns.ui) {
            return new ns.ui.ToolbarAction({
                name: "EXPAND_CAROUSEL3",
                text: Granite.I18n.get("Expand/Collapse Carousel Items"),
                icon: "chevronUpDown",
                execute: function (editable) {
                    // TODO: improve the selector handling
                    var path = editable.slingPath;
                    var isExpanded = path.indexOf(".expanded3.html") > 0;
                    var newPath;
                    if (isExpanded) {
                        newPath = path.slice(0,path.indexOf(".expanded3.html")) + ".html";
                    } else {
                        newPath = path.slice(0,path.indexOf(".html")) + ".expanded3.html";
                    }
                    editable.slingPath = newPath;
                    ns.edit.EditableActions.REFRESH.execute(editable, editable.config);
                    return false;
                },
                condition: function (editable) {
                    // TODO: improve with super type
                    return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
                },
                isNonMulti: true
            });
        }
        return action;
    }

    channel.on("cq-layer-activated", function (event) {
        if (event.layer === "Preview") {
            // TODO: for all carousel components on the page:
            // display the editable as collapsed (remove the "expanded" selector in the editable path)
        }
    });

    channel.on("cq-editor-loaded", function (event) {
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL1", getExpandAction1());
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL2", getExpandAction2());
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL3", getExpandAction3());
        ns.editables.forEach(function (editable) {
            $(editable).on("beforeedit", function(event) {
                console.log("jck");
            });
        });
    });

    channel.on("cq-overlay-click.selection", function(event) {
        console.log("jck");
    });

    if (ns && ns.EditorFrame && ns.EditorFrame.editableToolbar) {
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL1", getExpandAction1());
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL2", getExpandAction2());
        ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL3", getExpandAction3());
    } else {
        channel.on("cq-editor-loaded", function (event) {
            if (event.layer === "Edit") {
                ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL1", getExpandAction1());
                ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL2", getExpandAction2());
                ns.EditorFrame.editableToolbar.registerAction("EXPAND_CAROUSEL3", getExpandAction3());
            }
        });
    }

}(jQuery, Granite.author, jQuery(document), this));
