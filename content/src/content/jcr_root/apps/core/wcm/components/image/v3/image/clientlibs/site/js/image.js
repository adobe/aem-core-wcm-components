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
        var selectors = {
            self: "[data-cmp-hook-image='imageV3']"
        };

        /**
         * Init the image if the image is from dynamic media
         * @param {HTMLElement} component the image component
         */
        var initImage = function(component) {
            var dmImage = component.matches("[data-cmp-dmimage]");
            if (dmImage) {
                var image = component.querySelector("img");
                CMP.image.dynamicMedia.setDMAttributes(component, image);
            }
        };

        return {
            init: function() {
                var componentList = document.querySelectorAll(selectors.self);
                var listLength = componentList.length;
                for (var i = 0; i < listLength; i++) {
                    var component = componentList[i];
                    initImage(component);
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
                                        initImage(element);
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
    if (document.readyState !== "loading") {
        window.CMP.image.v3.init();
    } else {
        document.addEventListener("DOMContentLoaded", window.CMP.image.v3.init);
    }

}(window.document));
