/*******************************************************************************
 * Copyright 2016 Adobe
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
(function() {
    "use strict";

    var devicePixelRatio = window.devicePixelRatio || 1;

    function SmartImage(noScriptElement, options) {
        var that = this;
        var showsLazyLoader = false;
        var image;
        var container;
        var anchor;
        var dropContainer;
        var initDone = false;

        that.defaults = {
            loadHidden: false,
            imageSelector: "img",
            containerSelector: ".cmp-image",
            sourceAttribute: "src",
            lazyEnabled: true,
            lazyThreshold: 0,
            lazyEmptyPixel: "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
            lazyLoaderClass: "loading",
            lazyLoaderStyle: {
                "height": 0,
                "padding-bottom": "" // will get replaced with ratio in %
            }
        };

        function init() {
            var tmp = document.createElement("div");
            tmp.innerHTML = decodeNoScript(noScriptElement.textContent.trim());
            var imageElement = tmp.firstElementChild;
            var source = imageElement.getAttribute(options.sourceAttribute);
            imageElement.removeAttribute(options.sourceAttribute);
            imageElement.setAttribute("data-src-disabled", source);
            container.insertBefore(imageElement, noScriptElement);
            noScriptElement.remove();

            if (container.matches(options.imageSelector)) {
                image = container;
            } else {
                image = imageElement;
            }

            that.container = container;
            that.options = options;
            that.image = image;
            if (options.lazyEnabled) {
                addLazyLoader();
            }
            window.addEventListener("scroll", that.update);
            window.addEventListener("resize", that.update);
            window.addEventListener("update", that.update);
            image.addEventListener("cmp-image-redraw", that.update);
            that.update();
        }

        function loadImage() {
            if (options.smartSizes && options.smartImages && options.smartSizes.length > 0) {
                if (options.smartSizes.length === options.smartImages.length) {
                    var containerWidth = 0;

                    if (container.tagName.toLowerCase() === "a") {
                        containerWidth = container.parentElement.clientWidth;
                    } else {
                        containerWidth = container.clientWidth;
                    }
                    var optimalSize = containerWidth * devicePixelRatio;
                    var len = options.smartSizes.length;
                    var key = 0;

                    while ((key < len - 1) && (options.smartSizes[key] < optimalSize)) {
                        key++;
                    }

                    if (image.getAttribute(options.sourceAttribute) !== options.smartImages[key]) {
                        image.setAttribute(options.sourceAttribute, options.smartImages[key]);
                        image.removeAttribute("data-src-disabled");
                        window.removeEventListener("scroll", that.update);
                    }
                }
            } else {
                if (!initDone) {
                    image.setAttribute(options.sourceAttribute, image.getAttribute("data-src-disabled"));
                    image.removeAttribute("data-src-disabled");
                    window.removeEventListener("scroll", that.update);
                    initDone = true;
                }
            }

            if (showsLazyLoader) {
                image.addEventListener("load", removeLazyLoader);
            }
        }

        function addLazyLoader() {
            var width = image.getAttribute("width");
            var height = image.getAttribute("height");
            if (width && height) {
                var ratio = (height / width) * 100;
                var styles = options.lazyLoaderStyle;
                styles["padding-bottom"] = ratio + "%";
                for (var s in styles) {
                    if (Object.prototype.hasOwnProperty.call(styles, s)) {
                        image.style[s] = styles[s];
                    }
                }
            }
            image.setAttribute(options.sourceAttribute, options.lazyEmptyPixel);
            image.classList.add(options.lazyLoaderClass);
            showsLazyLoader = true;
        }

        function removeLazyLoader() {
            image.classList.remove(options.lazyLoaderClass);
            for (var property in options.lazyLoaderStyle) {
                if (Object.prototype.hasOwnProperty.call(options.lazyLoaderStyle, property)) {
                    image.style[property] = "";
                }
            }
            image.removeEventListener("load", removeLazyLoader);
            showsLazyLoader = false;
        }

        function isLazyVisible() {

            if (container.offsetParent === null) {
                return false;
            }

            var wt = window.pageYOffset;
            var wb = wt + document.documentElement.clientHeight;
            var et = container.getBoundingClientRect().top + wt;
            var eb = et + container.clientHeight;

            return eb >= wt - options.lazyThreshold && et <= wb + options.lazyThreshold;
        }

        that.update = function() {
            if (options.lazyEnabled) {
                if (isLazyVisible() || options.loadHidden) {
                    loadImage();
                }
            } else {
                loadImage();
            }
        };

        options = Object.assign(that.defaults, options);

        container = noScriptElement.closest(options.containerSelector);
        if (container) {
            dropContainer = noScriptElement.closest(".cq-dd-image");
            if (dropContainer) {
                container = dropContainer;
            }
            anchor = container.querySelector(".cmp-image--link");
            if (anchor !== null) {
                container = anchor;
            }
            init();
        }
    }

    document.addEventListener("DOMContentLoaded", function() {

        var imageElements = document.querySelectorAll("noscript[data-cmp-image]");
        var images        = [];
        for (var index = 0; index < imageElements.length; index++) {
            var noScriptElement = imageElements[index];
            var imageOptions    = noScriptElement.dataset.cmpImage;
            noScriptElement.removeAttribute("data-cmp-image");
            images.push(new SmartImage(noScriptElement, JSON.parse(imageOptions)));
        }
        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body             = document.querySelector("body");
        var observer         = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var noScriptArray = [].slice.call(addedNode.querySelectorAll("noscript[data-cmp-image]"));
                            noScriptArray.forEach(function(noScriptElement) {
                                var imageOptions = JSON.parse(noScriptElement.dataset.cmpImage);
                                noScriptElement.removeAttribute("data-cmp-image");
                                images.push(new SmartImage(noScriptElement, imageOptions));
                            });
                        }
                    });
                }
            });
        });

        observer.observe(body, {
            subtree: true,
            childList: true,
            characterData: true
        });
    });

    /*
         on drag & drop of the component into a parsys, noscript's content will be escaped multiple times by the editor which creates
         the DOM for editing; the HTML parser cannot be used here due to the multiple escaping
     */
    function decodeNoScript(text) {
        text = text.replace(/&(amp;)*lt;/g, "<");
        text = text.replace(/&(amp;)*gt;/g, ">");
        return text;
    }
})();
