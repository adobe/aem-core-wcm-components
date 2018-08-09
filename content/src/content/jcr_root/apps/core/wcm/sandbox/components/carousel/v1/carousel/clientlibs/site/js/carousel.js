/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
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
/* global Granite */
(function() {
    "use strict";

    var NS = "cmp";
    var IS = "carousel";

    var selectors = {
        self: "[data-" +  NS + '-is="' + IS + '"]'
    };

    function Carousel(config) {
        var that = this;

        if (config && config.element) {
            init(config);
        }

        function init(config) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");

            cacheElements(config.element);
            that._active = 0;

            if (that._elements.item) {
                refreshActive();
                initControls();
            }

            if (Granite && Granite.author) {
                new Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
                    if (message.data && message.data.type === "cmp-carousel" && message.data.id === that._elements.self.dataset["cmpPanelcontainerId"]) {
                        if (message.data.operation === "navigate") {
                            navigate(message.data.index);
                        }
                    }
                });
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
                if (that._elements[key]) {
                    if (!Array.isArray(that._elements[key])) {
                        var tmp = that._elements[key];
                        that._elements[key] = [tmp];
                    }
                    that._elements[key].push(hook);
                } else {
                    that._elements[key] = hook;
                }
            }
        }

        function initControls() {
            var prev = that._elements["prev"];
            if (prev) {
                prev.addEventListener("click", function() {
                    if (that._active > 0) {
                        that._active = that._active - 1;
                    } else {
                        that._active = that._elements["item"].length - 1;
                    }
                    refreshActive();
                });
            }

            var next = that._elements["next"];
            if (next) {
                next.addEventListener("click", function() {
                    if (that._active < that._elements["item"].length - 1) {
                        that._active = that._active + 1;
                    } else {
                        that._active = 0;
                    }
                    refreshActive();
                });
            }

            var indicators = that._elements["indicator"];
            if (indicators) {
                for (var i = 0; i < indicators.length; i++) {
                    indicators[i].addEventListener("click", function(event) {
                        navigate(event.target.dataset["slide"]);
                    });
                }
            }
        }

        function refreshActive() {
            var items = that._elements["item"];
            var indicators = that._elements["indicator"];

            if (items) {
                if (Array.isArray(items)) {
                    for (var i = 0; i < items.length; i++) {
                        if (i === parseInt(that._active)) {
                            items[i].classList.add("cmp-carousel__item--active");
                            indicators[i].classList.add("cmp-carousel__indicator--active");
                        } else {
                            items[i].classList.remove("cmp-carousel__item--active");
                            indicators[i].classList.remove("cmp-carousel__indicator--active");
                        }
                    }
                } else {
                    // only one item
                    items.classList.add("cmp-carousel__item--active");
                    indicators.classList.add("cmp-carousel__indicator--active");
                }
            }
        }

        function navigate(index) {
            that._active = index;
            refreshActive();
        }
    }

    function readData(element) {
        var data = element.dataset;
        var options = [];
        var capitalized = IS;
        capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
        var reserved = ["is", "hook" + capitalized];

        for (var key in data) {
            if (data.hasOwnProperty(key)) {
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

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new Carousel({ element: elements[i], options: readData(elements[i]) });
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
                                new Carousel({ element: element, options: readData(element) });
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
        document.addEventListener("DOMContentLoaded", onDocumentReady());
    }

}());
