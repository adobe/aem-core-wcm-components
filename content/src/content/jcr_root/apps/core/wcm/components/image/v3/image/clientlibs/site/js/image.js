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
    window.CMP.image.v3 = (function() {
        var IS = "image";

        var selectors = {
            self: "[data-cmp-hook-image='imageV3']"
        };

        var properties = {
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
            "dmimage": {
                "default": false,
                "transform": function(value) {
                    return !(value === null || typeof value === "undefined");
                }
            },
            "src": {
                "transform": function(value) {
                    return decodeURIComponent(value);
                }
            },
            "smartcroprendition": ""
        };

        function Image(config) {
            var that = this;
            /**
             * Init the image if the image is from dynamic media
             * @param {HTMLElement} component the image component
             */
            that.initImage = function(component) {
                var options = CMP.utils.readData(component, IS);
                that._properties = CMP.utils.setupProperties(options, properties);
                if (that._properties.dmimage) {
                    CMP.image.dynamicMedia.setDMAttributes(component, that._properties);
                }
            };
            that.initImage(config.element);
        }

        return {
            init: function() {
                var elements = document.querySelectorAll(selectors.self);
                for (var i = 0; i < elements.length; i++) {
                    new Image({ element: elements[i], options: CMP.utils.readData(elements[i], IS) });
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
                                        new Image({ element: element, options: CMP.utils.readData(element, IS) });
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
        };
    }());
    var documentReady = document.readyState !== 'loading' ? Promise.resolve() : new Promise(r => document.addEventListener('DOMContentLoaded', r));
    var utilsReady = (window.CMP && window.CMP.utils) ? Promise.resolve() : new Promise(r => document.addEventListener('core.wcm.components.commons.site.utils.loaded', r));
    var dynamicMediaReady = (window.CMP && window.CMP.image && window.CMP.image.dynamicMedia) ? Promise.resolve() : new Promise(r => document.addEventListener('core.wcm.components.commons.site.image.dynamic-media.loaded', r));

    Promise.all([documentReady, utilsReady, dynamicMediaReady]).then(window.CMP.image.v3.init);
}(window.document));
