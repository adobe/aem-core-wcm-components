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
    var IS_OPTION = "formOption";
    var IS_DASH_OPTION = "form-option";

    var selectors = {
        self: "[data-" + NS + '-is="' + IS_OPTION + '"]'
    };

    var properties = {        
        /**
         * A validation message to display if no input is supplied, but input is expected for the field.
         *
         * @type {String}
         */
        requiredMessage: {
        }
    };

    function readDataOption(element) {
        var data = element.dataset;
        var options = [];
        var capitalized = IS_OPTION;
        capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
		console.log("capitalized -- " + capitalized);
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

    function FormOption(config) {
        if (config.element) {
            // prevents multiple initialization
            config.element.removeAttribute("data-" + NS + "-is");
        }

        this._cacheElements(config.element);
        this._setupProperties(config.options);
        if(this._elements.input.type !== 'checkbox'){
			this._elements.input.addEventListener("invalid", this._onInvalid.bind(this));
        	this._elements.input.addEventListener("input", this._onInput.bind(this));
        }


    }

    FormOption.prototype._onInvalid = function(event) {
        event.target.setCustomValidity("");
        if (event.target.validity.valueMissing) {			
            if (this._properties.requiredMessage) {                    
                event.target.setCustomValidity(this._properties.requiredMessage);
            }
        }

    };

    FormOption.prototype._onInput = function(event) {
        event.target.setCustomValidity("");
    };

    FormOption.prototype._cacheElements = function(wrapper) {
        this._elements = {};
        this._elements.self = wrapper;
        var hooks = this._elements.self.querySelectorAll("[data-" + NS + "-hook-" + IS_DASH_OPTION + "]");
        for (var i = 0; i < hooks.length; i++) {
            var hook = hooks[i];
            var capitalized = IS_OPTION;
            capitalized = capitalized.charAt(0).toUpperCase() + capitalized.slice(1);
            var key = hook.dataset[NS + "Hook" + capitalized];
            if(!this._elements.hasOwnProperty(key)){
                this._elements[key] = hook;}
        }
    };

    FormOption.prototype._setupProperties = function(options) {
        this._properties = {};

        for (var key in properties) {
            if (properties.hasOwnProperty(key)) {
                var property = properties[key];
                if (options && options[key] != null) {
                    if (property && typeof property.transform === "function") {
                        this._properties[key] = property.transform(options[key]);
                    } else {
                        this._properties[key] = options[key];
                    }
                } else {
                    this._properties[key] = properties[key]["default"];
                }
            }
        }
    };

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new FormOption({ element: elements[i], options: readDataOption(elements[i]) });
        }

        var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
        var body = document.querySelector("body");
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                // needed for IE
                var nodesArray = [].slice.call(mutation.addedNodes);
                if (nodesArray.length > 0) {
                    nodesArray.forEach(function(addedNode) {
                        if (addedNode.querySelectorAll) {
                            var elementsArray = [].slice.call(addedNode.querySelectorAll(selectors.self));
                            elementsArray.forEach(function(element) {
                                new FormOption({ element: element, options: readDataOption(element) });
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

})();