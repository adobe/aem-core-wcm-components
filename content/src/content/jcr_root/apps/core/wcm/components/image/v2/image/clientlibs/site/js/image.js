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

    var NS = "cmp";
    var IS = "image";

    var EMPTY_PIXEL = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7";
    var LAZY_THRESHOLD_DEFAULT = 0;
    var SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";
    var SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY = "width={width}";
    var SRC_URI_TEMPLATE_DPR_VAR = "{dpr}";

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]',
        image: '[data-cmp-hook-image="image"]',
        map: '[data-cmp-hook-image="map"]',
        area: '[data-cmp-hook-image="area"]'
    };

    var lazyLoader = {
        "cssClass": "cmp-image__image--is-loading",
        "style": {
            "height": 0,
            "padding-bottom": "" // will be replaced with % ratio
        }
    };

    var properties = {
        /**
         * An array of alternative image widths (in pixels).
         * Used to replace a {.width} variable in the src property with an optimal width if a URI template is provided.
         *
         * @memberof Image
         * @type {Number[]}
         * @default []
         */
        "widths": {
            "default": [],
            "transform": function(value) {
                var widths = [];
                value.split(",").forEach(function(item) {
                    item = parseFloat(item);
                    if (!isNaN(item)) {
                        widths.push(item);
                    }
                });
                return widths;
            }
        },
        /**
         * Indicates whether the image should be rendered lazily.
         *
         * @memberof Image
         * @type {Boolean}
         * @default false
         */
        "lazy": {
            "default": false,
            "transform": function(value) {
                return !(value === null || typeof value === "undefined");
            }
        },
        /**
         * Indicates image is DynamicMedia image.
         *
         * @memberof Image
         * @type {Boolean}
         * @default false
         */
        "dmimage": {
            "default": false,
            "transform": function(value) {
                return !(value === null || typeof value === "undefined");
            }
        },
        /**
         * The lazy threshold.
         * This is the number of pixels, in advance of becoming visible, when an lazy-loading image should begin
         * to load.
         *
         * @memberof Image
         * @type {Number}
         * @default 0
         */
        "lazythreshold": {
            "default": 0,
            "transform": function(value) {
                var val =  parseInt(value);
                if (isNaN(val)) {
                    return LAZY_THRESHOLD_DEFAULT;
                }
                return val;
            }
        },
        /**
         * The image source.
         *
         * Can be a simple image source, or a URI template representation that
         * can be variable expanded - useful for building an image configuration with an alternative width.
         * e.g. '/path/image.coreimg{.width}.jpeg/1506620954214.jpeg'
         *
         * @memberof Image
         * @type {String}
         */
        "src": {
            "transform": function(value) {
                return decodeURIComponent(value);
            }
        }
    };

    var devicePixelRatio = window.devicePixelRatio || 1;

    function readData(element) {
        var data = element.dataset;
        var options = [];
        var capitalized = IS;
        capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
        var reserved = ["is", "hook" + capitalized];

        for (var key in data) {
            if (Object.prototype.hasOwnProperty.call(data, key)) {
                var value = data[key];

                if (key.indexOf(NS) === 0) {
                    key = key.slice(NS.length);
                    key = key.charAt(0).toLowerCase() + key.substring(1);

                    if (reserved.indexOf(key) === -1) {
                        options[key] = value;
                    }
                }
            }
        }
        return options;
    }

    function Image(config) {
        var that = this;

        var smartCrops = {};

        var useAssetDelivery = false;
        var srcUriTemplateWidthVar = SRC_URI_TEMPLATE_WIDTH_VAR;

        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            // check if asset delivery is used
            if (config.options.src && config.options.src.indexOf(SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY) >= 0) {
                useAssetDelivery = true;
                srcUriTemplateWidthVar = SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY;
            }

            setupProperties(config.options);
            cacheElements(config.element);
            // check image is DM asset; if true try to make req=set
            if (config.options.src && Object.prototype.hasOwnProperty.call(config.options, "dmimage") && (config.options["smartcroprendition"] === "SmartCrop:Auto")) {
                var request = new XMLHttpRequest();
                var urlTemplatePart = decodeURIComponent(config.options.src).split(srcUriTemplateWidthVar)[0];
                var url = urlTemplatePart + (urlTemplatePart.indexOf("?") < 0 ? "?" : "") + "req=set,json";

                request.open("GET", url, false);
                request.onload = function() {
                    if (request.status >= 200 && request.status < 400) {
                        // success status
                        var responseText = request.responseText;
                        var rePayload = new RegExp(/^(?:\/\*jsonp\*\/)?\s*([^()]+)\(([\s\S]+),\s*"[0-9]*"\);?$/gmi);
                        var rePayloadJSON = new RegExp(/^{[\s\S]*}$/gmi);
                        var resPayload = rePayload.exec(responseText);
                        var payload;
                        if (resPayload) {
                            var payloadStr = resPayload[2];
                            if (rePayloadJSON.test(payloadStr)) {
                                payload = JSON.parse(payloadStr);
                            }

                        }
                        // check "relation" - only in case of smartcrop preset
                        if (payload && payload.set.relation && payload.set.relation.length > 0) {
                            for (var i = 0; i < payload.set.relation.length; i++) {
                                smartCrops[parseInt(payload.set.relation[i].userdata.SmartCropWidth)] =
                                    ":" + payload.set.relation[i].userdata.SmartCropDef;
                            }
                        }
                    } else {
                        // error status
                    }
                };
                request.send();
            }

            if (!that._elements.noscript) {
                return;
            }

            that._elements.container = that._elements.link ? that._elements.link : that._elements.self;

            unwrapNoScript();

            if (that._properties.lazy) {
                addLazyLoader();
            }

            if (that._elements.map) {
                that._elements.image.addEventListener("load", onLoad);
            }

            window.addEventListener("resize", onWindowResize);
            ["focus", "click", "load", "transitionend", "animationend", "scroll"].forEach(function(name) {
                document.addEventListener(name, that.update);
            });

            that._elements.image.addEventListener("cmp-image-redraw", that.update);
            that.update();
        }

        function loadImage() {
            var hasWidths = (that._properties.widths && that._properties.widths.length > 0) || Object.keys(smartCrops).length > 0;
            var replacement;
            if (Object.keys(smartCrops).length > 0) {
                var optimalWidth = getOptimalWidth(Object.keys(smartCrops), false);
                replacement = smartCrops[optimalWidth];
            } else {
                replacement = hasWidths ? (that._properties.dmimage ? "" : ".") + getOptimalWidth(that._properties.widths, true) : "";
            }
            if (useAssetDelivery) {
                replacement = replacement !== "" ? ("width=" + replacement.substring(1)) : "";
            }
            var url = that._properties.src.replace(srcUriTemplateWidthVar, replacement);
            url = url.replace(SRC_URI_TEMPLATE_DPR_VAR, devicePixelRatio);

            var imgSrcAttribute = that._elements.image.getAttribute("src");

            if (url !== imgSrcAttribute) {
                if (imgSrcAttribute === null || imgSrcAttribute === EMPTY_PIXEL) {
                    that._elements.image.setAttribute("src", url);
                } else {
                    var urlTemplateParts = that._properties.src.split(srcUriTemplateWidthVar);
                    // check if image src was dynamically swapped meanwhile (e.g. by Target)
                    var isImageRefSame = imgSrcAttribute.startsWith(urlTemplateParts[0]);
                    if (isImageRefSame && urlTemplateParts.length > 1) {
                        isImageRefSame = imgSrcAttribute.endsWith(urlTemplateParts[urlTemplateParts.length - 1]);
                    }
                    if (isImageRefSame) {
                        that._elements.image.setAttribute("src", url);
                        if (!hasWidths) {
                            window.removeEventListener("scroll", that.update);
                        }
                    }
                }
            }
            if (that._lazyLoaderShowing) {
                that._elements.image.addEventListener("load", removeLazyLoader);
            }
        }

        function getOptimalWidth(widths, useDevicePixelRatio) {
            var container = that._elements.self;
            var containerWidth = container.clientWidth;
            while (containerWidth === 0 && container.parentNode) {
                container = container.parentNode;
                containerWidth = container.clientWidth;
            }

            var dpr = useDevicePixelRatio ? devicePixelRatio : 1;
            var optimalWidth = containerWidth * dpr;
            var len = widths.length;
            var key = 0;

            while ((key < len - 1) && (widths[key] < optimalWidth)) {
                key++;
            }

            return widths[key].toString();
        }

        function addLazyLoader() {
            var width = that._elements.image.getAttribute("width");
            var height = that._elements.image.getAttribute("height");

            if (width && height) {
                var ratio = (height / width) * 100;
                var styles = lazyLoader.style;

                styles["padding-bottom"] = ratio + "%";

                for (var s in styles) {
                    if (Object.prototype.hasOwnProperty.call(styles, s)) {
                        that._elements.image.style[s] = styles[s];
                    }
                }
            }
            that._elements.image.setAttribute("src", EMPTY_PIXEL);
            that._elements.image.classList.add(lazyLoader.cssClass);
            that._lazyLoaderShowing = true;
        }

        function unwrapNoScript() {
            var markup = decodeNoscript(that._elements.noscript.textContent.trim());
            var parser = new DOMParser();

            // temporary document avoids requesting the image before removing its src
            var temporaryDocument = parser.parseFromString(markup, "text/html");
            var imageElement = temporaryDocument.querySelector(selectors.image);
            imageElement.removeAttribute("src");
            that._elements.container.insertBefore(imageElement, that._elements.noscript);

            var mapElement = temporaryDocument.querySelector(selectors.map);
            if (mapElement) {
                that._elements.container.insertBefore(mapElement, that._elements.noscript);
            }

            that._elements.noscript.parentNode.removeChild(that._elements.noscript);
            if (that._elements.container.matches(selectors.image)) {
                that._elements.image = that._elements.container;
            } else {
                that._elements.image = that._elements.container.querySelector(selectors.image);
            }

            that._elements.map = that._elements.container.querySelector(selectors.map);
            that._elements.areas = that._elements.container.querySelectorAll(selectors.area);
        }

        function removeLazyLoader() {
            that._elements.image.classList.remove(lazyLoader.cssClass);
            for (var property in lazyLoader.style) {
                if (Object.prototype.hasOwnProperty.call(lazyLoader.style, property)) {
                    that._elements.image.style[property] = "";
                }
            }
            that._elements.image.removeEventListener("load", removeLazyLoader);
            that._lazyLoaderShowing = false;
        }

        function isLazyVisible() {
            if (that._elements.container.offsetParent === null) {
                return false;
            }

            var wt = window.pageYOffset;
            var wb = wt + document.documentElement.clientHeight;
            var et = that._elements.container.getBoundingClientRect().top + wt;
            var eb = et + that._elements.container.clientHeight;

            return eb >= wt - that._properties.lazythreshold && et <= wb + that._properties.lazythreshold;
        }

        function resizeAreas() {
            if (that._elements.areas && that._elements.areas.length > 0) {
                for (var i = 0; i < that._elements.areas.length; i++) {
                    var width = that._elements.image.width;
                    var height = that._elements.image.height;

                    if (width && height) {
                        var relcoords = that._elements.areas[i].dataset.cmpRelcoords;
                        if (relcoords) {
                            var relativeCoordinates = relcoords.split(",");
                            var coordinates = new Array(relativeCoordinates.length);

                            for (var j = 0; j < coordinates.length; j++) {
                                if (j % 2 === 0) {
                                    coordinates[j] = parseInt(relativeCoordinates[j] * width);
                                } else {
                                    coordinates[j] = parseInt(relativeCoordinates[j] * height);
                                }
                            }

                            that._elements.areas[i].coords = coordinates;
                        }
                    }
                }
            }
        }

        function cacheElements(wrapper) {
            that._elements = {};
            that._elements.self = wrapper;
            var hooks = that._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");

            for (var i = 0; i < hooks.length; i++) {
                var hook = hooks[i];
                var capitalized = IS;
                capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
                var key = hook.dataset[NS + "Hook" + capitalized];
                that._elements[key] = hook;
            }
        }

        function setupProperties(options) {
            that._properties = {};

            for (var key in properties) {
                if (Object.prototype.hasOwnProperty.call(properties, key)) {
                    var property = properties[key];
                    if (options && options[key] != null) {
                        if (property && typeof property.transform === "function") {
                            that._properties[key] = property.transform(options[key]);
                        } else {
                            that._properties[key] = options[key];
                        }
                    } else {
                        that._properties[key] = properties[key]["default"];
                    }
                }
            }
        }

        function onWindowResize() {
            that.update();
            resizeAreas();
        }

        function onLoad() {
            resizeAreas();
        }

        that.update = function() {
            if (that._properties.lazy) {
                if (isLazyVisible()) {
                    loadImage();
                }
            } else {
                loadImage();
            }
        };

        if (config && config.element) {
            init(config);
        }
    }

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Image({ element: elements[i], options: readData(elements[i]) });
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
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new Image({ element: element, options: readData(element) });
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
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }

    /*
        on drag & drop of the component into a parsys, noscript's content will be escaped multiple times by the editor which creates
        the DOM for editing; the HTML parser cannot be used here due to the multiple escaping
     */
    function decodeNoscript(text) {
        text = text.replace(/&(amp;)*lt;/g, "<");
        text = text.replace(/&(amp;)*gt;/g, ">");
        return text;
    }

})();
