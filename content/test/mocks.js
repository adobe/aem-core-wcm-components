/*******************************************************************************
 * Copyright 2026 Adobe
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
class JQueryArray extends Array {
    constructor(...args) {
        super(...args);
        this._events = {};
    }

    find(selector) {
        const elem = this[0] ? this[0].querySelector(selector) : null;
        return elem ? new JQueryArray(elem) : new JQueryArray();
    }

    attr(name) {
        return this[0] ? this[0].getAttribute(name) : null;
    }

    closest(selector) {
        return this;
    }

    val() {
        return this[0] ? this[0].value : '';
    }

    on(event, handler) {
        if (!this._events[event]) {
            this._events[event] = [];
        }
        this._events[event].push(handler);
        return this;
    }

    trigger(event) {
        const eventName = typeof event === 'string' ? event : event.type;
        const handlers = this._events[eventName] || [];
        for (const handler of handlers) {
            handler(event);
        }
        return this;
    }

    off(event) {
        if (event) {
            delete this._events[event];
        } else {
            this._events = {};
        }
        return this;
    }
}

function jQuery(obj) {
    if (obj?.__jqWrapped) {
        return obj.__jqWrapped;
    }

    const result = new JQueryArray(obj);

    if (obj === document) {
        if (!jQuery._docChannel) {
            jQuery._docChannel = result;
        }
        return jQuery._docChannel;
    }

    if (obj === window) {
        result.adaptTo = function(type) {
            if (type === 'foundation-registry') {
                return window.foundationRegistry;
            }
            return null;
        };
    }

    return result;
}

jQuery.getJSON = function(url) {
    return new Promise(function(resolve, reject) {
        if (jQuery._getJSONHandler) {
            setTimeout(function() {
                jQuery._getJSONHandler(url, resolve, reject);
            }, 0);
        }
    });
};

jQuery.ajax = function(options) {
    return new Promise(function(resolve, reject) {
        if (jQuery._ajaxHandler) {
            setTimeout(function() {
                jQuery._ajaxHandler(options, resolve, reject);
            }, 0);
        }
    });
};

// Add $ as alias for jQuery
window.$ = jQuery;

Granite = {
    author: {
        util: {
            mixin: function (dest, src) {
                for (var prop in src) {
                    if (src.hasOwnProperty(prop)) {
                        dest[prop] = src[prop];
                    }
                }
            },
            createClass: function (classDefinition) {
                var methods = {};

                if (!classDefinition.constructor) {
                    classDefinition.constructor = function () {
                    };
                }

                for (var prop in classDefinition) {
                    if (classDefinition.hasOwnProperty(prop) && prop !== "constructor") {
                        methods[prop] = classDefinition[prop];
                    }
                }

                Granite.author.util.mixin(classDefinition.constructor.prototype, methods);

                return classDefinition.constructor;
            }
        },
        editor: {
            register: function (type, editor) {
                this[type] = editor;
            }
        },
        ContentFrame: {
            contentWindow: null
        },
        CFM: {
            Fragments: {
                mappings: new Map(),
                adaptToFragment: function (dom) {
                    const fragmentElem = dom.querySelector('[data-cmp-contentfragment-path]');
                    if (fragmentElem) {
                        const fragmentPath = fragmentElem.getAttribute('data-cmp-contentfragment-path');
                        return Granite.author.CFM.Fragments.mappings.get(fragmentPath);
                    } else {
                        return null;
                    }
                }
            }
        }
    },
    Toggles: {
        enabled: false,
        isEnabled: function (feature) {
            return Granite.Toggles.enabled;
        }
    },
    HTTP: {
        externalize: function (url) {
            return url;
        }
    },
    I18n: {
        get: function(message) {
            return message;
        }
    }
};

// Mock foundation registry
window.foundationRegistry = {
    validators: [],
    register: function(type, config) {
        if (type === 'foundation.validation.validator') {
            this.validators.push(config);
        }
    }
};

// Enhance jQuery with adaptTo method for foundation registry
jQuery.fn = jQuery.prototype;
jQuery.fn.adaptTo = function(type) {
    if (type === 'foundation-registry') {
        return window.foundationRegistry;
    }
    return null;
};

// Make sure $ also has the adaptTo method
jQuery.adaptTo = function(type) {
    if (type === 'foundation-registry') {
        return window.foundationRegistry;
    }
    return null;
};

// Mock window.adaptTo as well
window.adaptTo = function(type) {
    if (type === 'foundation-registry') {
        return window.foundationRegistry;
    }
    return null;
};

// Mock document object
window.document = window.document || {
    addEventListener: function() {},
    querySelector: function() { return null; },
    querySelectorAll: function() { return []; }
};
