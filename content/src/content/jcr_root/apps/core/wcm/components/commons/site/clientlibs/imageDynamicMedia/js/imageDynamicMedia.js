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
(function() {
    "use strict";

    window.CMP = window.CMP || {};
    window.CMP.image = window.CMP.image || {};
    window.CMP.image.dynamicMedia = (function() {
        var autoSmartCrops = {};
        var SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";

        var getAutoSmartCrops = function(src) {
            var request = new XMLHttpRequest();
            var url = decodeURIComponent(src).split(SRC_URI_TEMPLATE_WIDTH_VAR)[0] + "?req=set,json";

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

        var getSrcSet = function(src) {
            var srcset;
            if (Object.keys(autoSmartCrops).length === 0) {
                getAutoSmartCrops(src);
            }
            var keys = Object.keys(autoSmartCrops);
            if (keys.length > 0) {
                srcset = [];
                for (var key in autoSmartCrops) {
                    srcset.push(src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, autoSmartCrops[key]) + " " + key + "w");
                }
            }
            return  srcset.join(",");
        };

        function getOptimalWidth(sizes, width) {
            var len = sizes.length;
            var key = 0;

            while ((key < len - 1) && (sizes[key] < width)) {
                key++;
            }

            return sizes[key].toString();
        }

        var getSrc = function(src, elemWidth) {
            if (Object.keys(autoSmartCrops).length === 0) {
                getAutoSmartCrops(src);
            }
            var keys = Object.keys(autoSmartCrops);
            return src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, autoSmartCrops[getOptimalWidth(keys, elemWidth)]);
        };

        return {
            getAutoSmartCrops: getAutoSmartCrops,
            getSrcSet: getSrcSet,
            getSrc: getSrc
        };
    }());
}());
