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

    var deletedChildren = [];
    var orderedChildren = [];
    // TODO: check why below selector does not get triggered
    //    var REMOVE_BUTTON_SELECTOR = ".childreneditor button[handle='remove']";
    var REMOVE_BUTTON_SELECTOR = "button[handle='remove']";
    var ADD_BUTTON_SELECTOR = ".childreneditor button[coral-multifield-add]";
    var EDITOR_SELECTOR = ".childreneditor";
    var EDITOR_URL_SELECTOR = "childreneditor";
    var CHILD_SELECTOR = "coral-multifield-item";
    var CONTAINER_PATH_DATA_ATTR = "containerPath";
    var TITLE_PROP_NAME = "jcr:title";

    // Remove child
    $(document).on("click", REMOVE_BUTTON_SELECTOR, function(event) {
        var $button = $(this);
        var childName = $button.closest(CHILD_SELECTOR).data("name");
        deletedChildren.push(childName);
    });

    // Add a new child
    $(document).on("click", ADD_BUTTON_SELECTOR, function(event) {
        var $button = $(this);
        // We need one more frame to make sure the item renders the template in the DOM
        Coral.commons.nextFrame(function() {
            $button.trigger("foundation-contentloaded");
            var $editor = $button.closest(EDITOR_SELECTOR);
            var $child = $editor.find(CHILD_SELECTOR).last();
            var childName = "item_" + Date.now();
            var inputName = "./" + childName + "/" + TITLE_PROP_NAME;
            $child.data("name", childName);
            var $input = $child.find("input");
            $input.attr("name", inputName);
        }.bind(this));
    });

    // Trigger POST request to add, remove, re-order children nodes
    function processChildren($editor) {

        // Process re-ordered items
        $editor.find(CHILD_SELECTOR).each(function() {
            var $child = $(this);
            var childName = $child.data("name");
            orderedChildren.push(childName);
        });

        var containerPath = $editor.data(CONTAINER_PATH_DATA_ATTR);
        var url = containerPath + "." + EDITOR_URL_SELECTOR + ".html";
        $.ajax({
            type: "POST",
            url: url,
            data: {
                "deletedChildren": deletedChildren,
                "orderedChildren": orderedChildren
            }
        }
        );

        deletedChildren = [];
        orderedChildren = [];
    }

    // Submit hook to process the children
    $(window).adaptTo("foundation-registry").register("foundation.form.submit", {
        selector: "*",
        handler: function(formEl) {
            var $editor = $(formEl).find(EDITOR_SELECTOR);
            return {
                post: function() {
                    processChildren($editor);
                }
            };
        }
    });

}(jQuery, Granite.author, jQuery(document), this));
