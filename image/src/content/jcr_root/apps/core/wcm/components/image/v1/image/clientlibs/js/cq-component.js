/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
(function ($) {
    "use strict";

    function cqComponent(elements, options) {
        options = $.extend({}, cqComponent.defaults, options);
        elements = elements || options.componentSelector;

        $(elements).each(function () {
            var componentElement = $(this),
                componentOptions = componentElement.data(options.dataAttribute),
                componentName = componentOptions.component;

            if (componentName in cqComponent.fn) {
                var component = new cqComponent.fn[componentName](componentElement, componentOptions);

                componentElement.data("component", component);
            }
        });
    }

    cqComponent.defaults = {
        componentSelector: "[data-component]",
        dataAttribute: "component"
    };

    cqComponent.fn = {};

    cqComponent.update = function () {
        $(window).trigger("update");
    };

    window.cqComponent = cqComponent;

    $(function () {
        window.cqComponent();
    });

})(jQuery);
