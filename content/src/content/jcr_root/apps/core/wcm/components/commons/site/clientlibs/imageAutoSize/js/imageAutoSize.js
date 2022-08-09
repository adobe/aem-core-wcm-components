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
        var autoSizes = {
            elements: []
        };

        var selectors = {
            self: ".cmp-image.autosize"
        };

        /**
         * delay function execution
         * @param {Function} func the function which should get delayed
         * @returns {(function(): void)|*} the wrapped function
         */
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

        /**
         * set the size attribute to the image element to the provided width in pixel
         * @param {Element} image the image element
         * @param {Number} width the auto width
         */
        var setSizeAttribute = function(image, width) {
            image._autoWidth = width;
            var widthStr = width.toString() + "px";
            image.setAttribute("sizes", widthStr);
        };

        /**
         * get the width from the parent element and set it to the image size
         * @param {Element} image the image element
         * @param {Element} component the image component
         */
        var setImageSizeToParentWidth = function(image, component) {
            var parent = component.parentNode;
            if (parent) {
                var width = CMP.image.dynamicMedia.getWidth(component, parent);
                if (width && width !== image._autoWidth) {
                    setSizeAttribute(image, width);
                }
            }
        };

        /**
         * unwrap the image element from the noscript element
         * @param {HTMLElement} component the noscript element
         */
        var replaceNoScript = function(component) {
            var dmImage = component.matches("[data-cmp-dmimage]");
            var noscript = component.querySelector("noscript");
            if (noscript) {
                var tmp = document.createElement("div");
                tmp.innerHTML = noscript.textContent || noscript.innerHTML;
                var image = tmp.firstElementChild;
                if (dmImage) {
                    CMP.image.dynamicMedia.setDMAttributes(component, image);
                }
                setImageSizeToParentWidth(image, noscript);
                autoSizes.elements.push(image);
                noscript.replaceWith(image);
            }
        };

        var checkElements = function() {
            var elemLength = autoSizes.elements.length;
            for (var i = 0; i < elemLength; i++) {
                var image = autoSizes.elements[i];
                setImageSizeToParentWidth(image, image);
            }
        };

        return {
            init: function() {
                var componentList = document.querySelectorAll(selectors.self);
                var listLength = componentList.length;
                for (var i = 0; i < listLength; i++) {
                    var component = componentList[i];
                    replaceNoScript(component);
                }
                addEventListener("resize", debounce(checkElements), true);
            }
        };

    }());

    document.addEventListener("DOMContentLoaded", window.CMP.image.autoSizes.init);

}(window.document));
