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
;(function ($, ns, channel, window) {
    "use strict";

    var ui = $(window).adaptTo("foundation-ui");

    /**
     * The toolbar item for editing the carousel
     */
    ns.edit.ToolbarActions.EDIT_CAROUSEL = new ns.ui.ToolbarAction({
        name      : "EDIT_CAROUSEL",
        text      : Granite.I18n.get("Edit Carousel Items"),
        icon      : "imageCarousel",
        order     : "first",
        execute   : function (editable, selectableParents, target) {
            var popover = new Coral.Popover().set({
                alignAt: Coral.Overlay.align.LEFT_BOTTOM,
                alignMy: Coral.Overlay.align.LEFT_TOP,
                content: {
                    innerHTML: ""
                },
                target: target[0],
                open: true
            });
            popover.on("coral-overlay:close", function() {
                $(popover).remove();
            });

            getCarouselItems(popover, editable.path);

            $(popover).addClass("cq-carousel-dropdown").appendTo(document.body);

            // Do not close the toolbar
            return false;
        },
        condition : function (editable) {
            // TODO: improve with super type
            return "core/wcm/sandbox/components/carousel/v1/carousel" === editable.type;
        },
        isNonMulti: true
    });

    function getCarouselItems(popover, path) {
        ui.wait(popover);
        $.ajax({
            url: path + ".model.json"
        }).done(function (data) {
            if (data && data.items) {
                var items = new Array(data.items.length);
                for (var i = 0; i < data.items.length; i++) {
                    var title = getTitle(data.items[i]);
                    if (!title) {
                        title = "#" + (i + 1);
                    }
                    items[i] = {
                        title: title
                    };
                }
                populateCarouselItems(popover, items);
            }
        });
    }

    function getTitle(item) {
        var title;
        if (item) {
            if (item["jcr:title"]) {
                title = item["jcr:title"];
            } else if (typeof item == "object") {
                for (var property in item) {
                    if (item.hasOwnProperty(property)) {
                        title = getTitle(item[property]);
                        if (title) {
                            break;
                        }
                    }
                }
            }
        }
        return title;
    }

    function populateCarouselItems(popover, items) {
        var table = $('<table is="coral-table" selectable orderable></table>');
        for (var i = 0; i < items.length; i++) {
            table.append($('<tr is="coral-table-row"><td is="coral-table-cell">' + items[i]["title"] + '</td>' +
                '<td is="coral-table-cell">' +
                '<button is="coral-button" type="button" variant="minimal" icon="reorder" coral-table-roworder></button>' +
                '<button is="coral-button" type="button" variant="minimal" icon="delete"></button>' +
                '</td></tr>'));
        }
        table.append($('<tr is="coral-table-row"><td is="coral-table-cell" colspan="2"><button is="coral-button" type="button" variant="minimal" icon="add"></button></td></tr>'));
        $(table).appendTo(popover.content);
        ui.clearWait();
    }
}(jQuery, Granite.author, jQuery(document), this));