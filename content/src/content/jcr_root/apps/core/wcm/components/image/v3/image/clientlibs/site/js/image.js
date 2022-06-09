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

(function(window, document) {
    "use strict";

    var cmpImage;
    var imageCfg;

    (function() {
        var prop;

        var imageDefaults = {
            minSize: 40
        };

        imageCfg = window.cmpImageCfg || {};

        for (prop in imageDefaults) {
            if (!(prop in imageCfg)) {
                imageCfg[prop] = imageDefaults[prop];
            }
        }
    })();

    var setTimeout = window.setTimeout;
    var docElem = document.documentElement;
    var dpr = window.devicePixelRatio || 1;

    var SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";
    var SRC_URI_TEMPLATE_DPR_VAR = "{dpr}";

    var getWidth = function(elem, parent, width) {
        width = width || elem.offsetWidth;

        while (width < imageCfg.minSize && parent && !elem._autoWidth) {
            width = parent.offsetWidth;
            parent = parent.parentNode;
        }

        return width;
    };

    function getOptimalWidth(sizes, width) {
        var len = sizes.length;
        var key = 0;

        while ((key < len - 1) && (sizes[key] < width)) {
            key++;
        }

        return sizes[key].toString();
    }

    var triggerEvent = function(elem, name, detail) {
        var event = document.createEvent("Event");

        if (!detail) {
            detail = {};
        }
        detail.instance = cmpImage;
        event.initEvent(name, true, true);
        event.detail = detail;
        elem.dispatchEvent(event);
        return event;
    };

    var debounce = function(func) {
        var timeout;
        var timestamp;
        var wait = 99;
        var run = function() {
            timeout = null;
            func();
        };
        var later = function() {
            var last = Date.now() - timestamp;

            if (last < wait) {
                setTimeout(later, wait - last);
            } else {
                (requestIdleCallback || run)(run);
            }
        };

        return function() {
            timestamp = Date.now();

            if (!timeout) {
                timeout = setTimeout(later, wait);
            }
        };
    };

    var getAutoSmartCrops = function(src) {
        var autoSmartCrops = {};
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

    var image = (function() {
        var images;

        var setSizes = function(imgEl, width) {
            imgEl._autoWidth = width;
            width += "px";
            imgEl.setAttribute("sizes", width);
        };

        var setSizesAttribute = function(elem, dataAttr, width) {
            var event;
            var imgEl;

            imgEl = elem.querySelector("img");

            if (imgEl && imgEl.hasAttribute("srcset")) {
                var parent = imgEl.parentNode;

                if (parent) {
                    width = getWidth(imgEl, parent, width);
                    event = triggerEvent(imgEl, "beforesizes", { width: width, dataAttr: !!dataAttr });

                    if (!event.defaultPrevented) {
                        width = event.detail.width;

                        if (width && width !== imgEl._autoWidth) {
                            setSizes(imgEl, width);
                        }
                    }
                }
            }
        };

        var updateImageSizesAttribute = function() {
            images = cmpImage.elements;
            for (var i = 0; i < images.length; i++) {
                setSizesAttribute(images[i]);
            }
        };

        var debouncedUpdateImageSizesAttribute = debounce(updateImageSizesAttribute);

        return {
            _init: function() {
                cmpImage.elements = document.querySelectorAll('[data-cmp-is="image"]');
                addEventListener("resize", debouncedUpdateImageSizesAttribute);

                if (cmpImage.elements.length) {

                    updateImageSizesAttribute();
                }
            },
            setSizesAttribute: setSizesAttribute
        };
    })();

    var dmImage = (function() {
        var dmImages;

        var setSrcSet = function(elem) {
            var imgEl;
            var src;
            var srcset;
            var autoSmartCrops;

            src = elem.getAttribute("data-cmp-src");
            imgEl = elem.querySelector("img");
            if (src && imgEl) {
                src = src.replace(SRC_URI_TEMPLATE_DPR_VAR, dpr);
                if (elem.matches('[data-cmp-smartcroprendition="SmartCrop:Auto"]')) {
                    autoSmartCrops = getAutoSmartCrops(src);
                    var keys = Object.keys(autoSmartCrops);
                    if (keys.length > 0) {
                        srcset = [];
                        for (var key in autoSmartCrops) {
                            srcset.push(src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, autoSmartCrops[key]) + " " + key + "w");
                        }
                        imgEl.setAttribute("srcset", srcset.join(","));
                        src = src.replace(SRC_URI_TEMPLATE_WIDTH_VAR, autoSmartCrops[getOptimalWidth(keys, getWidth(imgEl))]);
                    }
                }
                imgEl.setAttribute("src", src);
            }
        };

        var updateDMElements = function() {
            var i;
            var len = dmImages.length;
            if (len) {
                i = 0;
                for (; i < len; i++) {
                    setSrcSet(dmImages[i]);
                }
            }
        };

        return {
            _init: function() {
                dmImages = document.querySelectorAll('[data-cmp-is="image"][data-cmp-dmimage]');
                if (dmImages.length) {
                    updateDMElements();
                }
            },
            update: updateDMElements,
            setSrcSet: setSrcSet
        };
    })();

    var init = function() {
        if (!init.i && document.querySelectorAll) {
            init.i = true;
            dmImage._init();
            image._init();
            var observer = new MutationObserver(function(mutations) {
                mutations.forEach(function(mutation) {
                    var nodesArray = [].slice.call(mutation.addedNodes);
                    if (nodesArray.length > 0) {
                        nodesArray.forEach(function(addedNode) {
                            if (addedNode.querySelectorAll) {
                                var images = addedNode.querySelectorAll('[data-cmp-is="image"]');
                                images.forEach(function(item) {
                                    if (item.matches("[data-cmp-dmimage]")) {
                                        dmImage.setSrcSet(item);
                                    }
                                    image.setSizesAttribute(item);
                                });
                            }
                        });
                    }
                });
            });
            observer.observe(docElem, { childList: true, subtree: true });
        }
    };

    setTimeout(function() {
        init();
    });

    cmpImage = {
        cfg: imageCfg,
        dmImage: dmImage,
        image: image,
        init: init
    };

    return cmpImage;
})(window, document);
