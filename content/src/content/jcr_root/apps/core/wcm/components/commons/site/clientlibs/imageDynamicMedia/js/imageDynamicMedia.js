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
    window.CMP.image.dynamicMedia = (function() {
        var autoSmartCrops = {};
        var SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";
        var SRC_URI_TEMPLATE_DPR_VAR = "{dpr}";
        var dpr = window.devicePixelRatio || 1;
        var config = {
            minWidth: 20
        };

        /**
         * get auto smart crops from dm
         * @param {String} src the src uri
         * @returns {{}} the smart crop json object
         */
        var getAutoSmartCrops = function(src) {
            var request = new XMLHttpRequest();
            var url = src.split(SRC_URI_TEMPLATE_WIDTH_VAR)[0] + "?req=set,json";
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
                            autoSmartCrops[parseInt(payload.set.relation[i].userdata.SmartCropWidth)] =
                                ":" + payload.set.relation[i].userdata.SmartCropDef;
                        }
                    }
                } else {
                    // error status
                }
            };
            request.send();
            return autoSmartCrops;
        };

        /**
         * Build and return the srcset value based on the available auto smart crops
         * @param {String} src the src uri
         * @returns {String} the srcset
         */
        var getSrcSet = function(src, smartCrops) {
            var srcset;
            var keys = Object.keys(smartCrops);
            if (keys.length > 0) {
                srcset = [];
                for (var key in autoSmartCrops) {
                    srcset.push(src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, smartCrops[key]) + " " + key + "w");
                }
            }
            return  srcset.join(",");
        };

        /**
         * Get the optimal width based on the available sizes
         * @param {[Number]} sizes the available sizes
         * @param {Number} width the element width
         * @returns {String} the optimal width
         */
        function getOptimalWidth(sizes, width) {
            var len = sizes.length;
            var key = 0;

            while ((key < len - 1) && (sizes[key] < width)) {
                key++;
            }

            return sizes[key] !== undefined ? sizes[key].toString() : width;
        }

        /**
         * Get the width of an element or parent element if the width is smaller than the minimum width
         * @param {HTMLElement} component the image component
         * @param {HTMLElement | Node} parent the parent element
         * @returns {Number} the width of the element
         */
        var getWidth = function(component, parent) {
            var width = component.offsetWidth;
            while (width < config.minWidth && parent && !component._autoWidth) {
                width =  parent.offsetWidth;
                parent = parent.parentNode;
            }
            return width;
        };

        /**
         * Set the src and srcset attribute for a Dynamic Media Image which auto smart crops enabled.
         * @param {HTMLElement} component the image component
         * @param {{}} properties the component properties
         */
        var setDMAttributes = function(component, properties) {
            var src = properties.src.replace(SRC_URI_TEMPLATE_DPR_VAR, dpr);
            var smartCrops = {};
            var width;
            if (properties["smartcroprendition"] === "SmartCrop:Auto") {
                smartCrops = getAutoSmartCrops(src);
            }
            var hasWidths = (properties.widths && properties.widths.length > 0) || Object.keys(smartCrops).length > 0;
            if (hasWidths) {
                var image = component.querySelector("img");
                var elemWidth = getWidth(component, component.parentNode);
                if (properties["smartcroprendition"] === "SmartCrop:Auto") {
                    image.setAttribute("srcset", CMP.image.dynamicMedia.getSrcSet(src, smartCrops));
                    width = getOptimalWidth(Object.keys(smartCrops, elemWidth));
                    image.setAttribute("src", CMP.image.dynamicMedia.getSrc(src, smartCrops[width]));
                } else {
                    width = getOptimalWidth(properties.widths, elemWidth);
                    image.setAttribute("src", CMP.image.dynamicMedia.getSrc(src, width));
                }
            }
        };

        /**
         * Get the src attribute based on the optimal width
         * @param {String} src the src uri
         * @param {String} width the element width
         * @returns {String} the final src attribute
         */
        var getSrc = function(src, width) {
            if (src.indexOf(SRC_URI_TEMPLATE_WIDTH_VAR) > -1) {
                src = src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, width);
            }
            return src;
        };

        document.dispatchEvent(new CustomEvent('core.wcm.components.commons.site.image.dynamic-media.loaded'));

        return {
            getAutoSmartCrops: getAutoSmartCrops,
            getSrcSet: getSrcSet,
            getSrc: getSrc,
            setDMAttributes: setDMAttributes,
            getWidth: getWidth
        };
    }());
}(window.document));
