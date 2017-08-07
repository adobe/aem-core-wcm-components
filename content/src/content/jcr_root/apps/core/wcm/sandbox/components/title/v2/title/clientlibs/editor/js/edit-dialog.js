/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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
/**
 * The edit dialog of the Core Title component:
 * - displays all the sizes if no sizes have been defined in the policy
 * - hides all the sizes if only one size has been defined in the policy
 * - displays all the sizes defined in the policy if there are at least two
 */
(function ($, Granite, ns, $document) {

    var SIZES_SELECTOR          = "coral-select.core-title-sizes",
        DEFAULT_SIZES_SELECTOR  = "coral-select.core-title-sizes-default";

    // Hide/display the size dropdown
    $document.on("foundation-contentloaded", function (e) {
        Coral.commons.ready($(SIZES_SELECTOR, DEFAULT_SIZES_SELECTOR), function(component) {
            var select = $(SIZES_SELECTOR).get(0);
            var defaultSelect = $(DEFAULT_SIZES_SELECTOR).get(0);
            if (select === null || select === undefined || defaultSelect === null || defaultSelect === undefined) {
                return;
            }
            var itemsCount = select.items.getAll().length;
            if (itemsCount == 0) {
                // display all the sizes
                $(select).parent().remove();
            } else if (itemsCount == 1) {
                // don't display anything
                $(select).parent().remove();
                $(defaultSelect).parent().remove();
            } else {
                // display the values defined in the design policy
                $(defaultSelect).parent().remove();
            }
        });
    });

}(jQuery, Granite, Granite.author, jQuery(document)));