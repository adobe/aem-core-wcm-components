/*******************************************************************************
 * Copyright 2022 Adobe
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
(function(document) {
    "use strict";

    window.CMP = window.CMP || {};
    window.CMP.image = window.CMP.image || {};
    window.CMP.image.autoSizes = (function() {
        var SRC_URI_TEMPLATE_DPR_VAR = "{dpr}";
        var dpr = window.devicePixelRatio || 1;
        var autoSizes = {
            elements: []
        };

        var selectors = {
            self: ".cmp-image.autosize"
        };

        var config = {
            minWidth: 20
        };

        var debounce = function(func) {
            var wait = 99;
            var timeout;
            var args;
            var context;
            var timestamp;

            return function() {
                context = this;
                args = [].slice.call(arguments, 0);
                timestamp = new Date();

                var later = function() {
                    var last = (new Date()) - timestamp;
                    if (last < wait) {
                        timeout = setTimeout(later, wait - last);
                    } else {
                        timeout = null;
                        func.apply(context, args);
                    }
                };

                if (!timeout) {
                    timeout = setTimeout(later, wait);
                }
            };
        };

        var getWidth = function(component, parent) {
            var width = component.offsetWidth;
            while (width < config.minWidth && parent && !component._autoWidth) {
                width =  parent.offsetWidth;
                parent = parent.parentNode;
            }
            return width;
        };

        var sizeComponent = function(component, width) {
            component._autoWidth = width;
            width += "px";
            component.setAttribute("sizes", width);
        };

        var getSizedComponent = function(image, component, width) {
            var parent = component.parentNode;
            if (parent) {
                width = getWidth(component, parent);
                if (width && width !== image._autoWidth) {
                    sizeComponent(image, width);
                }
            }
        };

        var setDMAttributes = function(component, image) {
            var src = component.getAttribute("data-cmp-src");
            if (src) {
                if (component.matches('[data-cmp-smartcroprendition="SmartCrop:Auto"]')) {
                    src = src.replace(SRC_URI_TEMPLATE_DPR_VAR, dpr);
                    image.setAttribute("srcset", CMP.image.dynamicMedia.getSrcSet(src));
                }
                image.setAttribute("src", CMP.image.dynamicMedia.getSrc(src, getWidth(component, component.parentNode)));
            }
        };

        var replaceNoScript = function(component) {
            var dmImage = component.matches("[data-cmp-dmimage]");
            var noscript = component.querySelector("noscript");
            if (noscript) {
                var tmp = document.createElement("div");
                tmp.innerHTML = noscript.textContent || noscript.innerHTML;
                var image = tmp.firstElementChild;
                if (dmImage) {
                    setDMAttributes(component, image);
                }
                autoSizes.elements.push(image);
                getSizedComponent(image, noscript);
                noscript.replaceWith(image);
            }
        };

        var checkElements = function() {
            var elemLength = autoSizes.elements.length;
            for (var i = 0; i < elemLength; i++) {
                var image = autoSizes.elements[i];
                getSizedComponent(image, image);
            }
        };

        return {
            init: function() {
                var componentList = document.querySelectorAll(selectors.self);
                var listLength = componentList.length;
                for (var i = 0; i < listLength; i++) {
                    var component = componentList[i];
                    replaceNoScript(component);
                    component.classList.remove("autosize");
                }
                addEventListener("resize", debounce(checkElements), true);
            }
        };

    }());

    document.addEventListener("DOMContentLoaded", window.CMP.image.autoSizes.init);

}(window.document));
