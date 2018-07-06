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
(function($, ns, channel, window) {
    "use strict";

    var deletedItems = [];

    // Remove item
    $(document).on("coral-collection:remove", "coral-multifield", function(event) {
        var $item = $(event.detail.item);
        var path = $item.data("path");
        deletedItems.push(path);
    });

    // Add item
    $(document).on("coral-collection:add", "coral-multifield", function(event) {
        if (this === event.target) {
            // We need one more frame to make sure the item renders the template in the DOM
            Coral.commons.nextFrame(function() {
                $(this).trigger("foundation-contentloaded");
                var $item = $(event.detail.item);

                var parentPath = $item.closest("coral-multifield").data("parentPath");

                var itemName = "item_" + Date.now();
                var inputName = "./" + itemName + "/jcr:title";
                var itemPath = parentPath + "/" + itemName;
                $item.attr("data-path", itemPath);
                var $input = $item.find("input");
                $input.attr("name", inputName);
            }.bind(this));
        }
    });

    function processRequests() {
        var requests = [];

        // Process removed items
        for (var i = 0; i < deletedItems.length; i++) {
            var itemPath = deletedItems[i];
            requests.push(
                {
                    type: "POST",
                    url: itemPath,
                    data: { ":operation": "delete" }
                }
            );
        }
        deletedItems = [];

        // Process re-ordered items
        $("coral-multifield-item").each(function(idx) {
            var $item = $(this);
            var itemPath = $item.data("path");
            requests.push(
                {
                    type: "POST",
                    url: itemPath,
                    data: { ":order": idx }
                }
            );
        });

        var chain = $.when();
        requests.forEach(function(request) {
            chain = chain.then(function() {
                return $.ajax(request);
            });
        });

    }

    $(window).adaptTo("foundation-registry").register("foundation.form.submit", {
        selector: "*",
        handler: function(formEl) {

            /*
            */

            return {
                post: function() {
                    processRequests();
                }
            };
        }
    });


}(jQuery, Granite.author, jQuery(document), this));
