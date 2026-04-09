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
            if (type === 'foundation-ui') {
                return { prompt: function() {} };
            }
            return null;
        };
    }

    return result;
}

jQuery.getJSON = function(url) {
    let _resolve, _reject;
    const deferred = {
        then: function(onResolve, onReject) { // NOSONAR - mimicking jQuery deferred API
            _resolve = onResolve;
            _reject = onReject;
            return deferred;
        }
    };
    if (jQuery._getJSONHandler) {
        setTimeout(function() {
            jQuery._getJSONHandler(url, _resolve, _reject);
        }, 0);
    }
    return deferred;
};

/**
 * Minimal {@code $.get} for string URLs (editDialog HTML fetch) or settings objects (delegates to {@link jQuery.ajax}).
 * String form uses optional {@code jQuery._getHandler(url, deliverHtml)} for tests.
 */
jQuery.get = function(url, data, success, dataType) {
    if (typeof url === 'object' && url !== null) {
        return jQuery.ajax(Object.assign({ type: 'GET' }, url));
    }
    var state = { doneCb: null, alwaysCb: null, scheduled: false };
    function schedule() {
        if (state.scheduled) {
            return;
        }
        state.scheduled = true;
        setTimeout(function() {
            var html = '';
            if (jQuery._getHandler) {
                jQuery._getHandler(url, function(h) {
                    html = (h !== undefined && h !== null) ? h : '';
                });
            }
            if (state.doneCb) {
                state.doneCb(html);
            }
            if (state.alwaysCb) {
                state.alwaysCb();
            }
        }, 0);
    }
    return {
        done: function(cb) {
            state.doneCb = cb;
            schedule();
            return this;
        },
        always: function(cb) {
            state.alwaysCb = cb;
            schedule();
            return this;
        },
        fail: function() {
            return this;
        },
        then: function(onSucc, onFail) {
            this.done(function() {
                if (onSucc) {
                    onSucc.apply(null, arguments);
                }
            });
            return this;
        }
    };
};

/**
 * Resolves when all arguments with {@code .done} or {@code .then} have completed (used by editDialog).
 */
jQuery.when = function() {
    var args = Array.prototype.slice.call(arguments);
    if (args.length === 0) {
        return {
            done: function(cb) {
                setTimeout(cb, 0);
                return this;
            },
            then: function(ok) {
                this.done(ok || function() {});
                return this;
            }
        };
    }
    var remaining = args.length;
    var results = new Array(args.length);
    var pendingDone = [];

    function flush() {
        if (remaining !== 0) {
            return;
        }
        var copy = pendingDone.slice();
        pendingDone.length = 0;
        copy.forEach(function(cb) {
            cb.apply(null, results);
        });
    }

    args.forEach(function(arg, i) {
        function one(v) {
            results[i] = v;
            remaining--;
            flush();
        }
        if (arg && typeof arg.done === 'function') {
            arg.done(one);
        } else if (arg && typeof arg.then === 'function') {
            arg.then(one, function() {});
        } else {
            one(arg);
        }
    });

    return {
        done: function(cb) {
            if (remaining === 0) {
                setTimeout(function() {
                    cb.apply(null, results);
                }, 0);
            } else {
                pendingDone.push(cb);
            }
            return this;
        },
        then: function(onOk, onFail) {
            this.done(function() {
                if (onOk) {
                    onOk.apply(null, arguments);
                }
            });
            return this;
        }
    };
};

jQuery.ajax = function(options) {
    let _resolve, _reject;
    const deferred = {
        then: function(onResolve, onReject) { // NOSONAR - mimicking jQuery deferred API
            _resolve = onResolve;
            _reject = onReject;
            return deferred;
        }
    };
    if (jQuery._ajaxHandler) {
        setTimeout(function() {
            jQuery._ajaxHandler(options, _resolve, _reject);
        }, 0);
    }
    return deferred;
};

jQuery.Deferred = function() {
    var callbacks = [];
    var resolved = false;
    var resolvedArgs = [];
    var deferred = {
        resolve: function() {
            resolved = true;
            resolvedArgs = Array.prototype.slice.call(arguments);
            var cbs = callbacks.slice();
            callbacks = [];
            cbs.forEach(function(cb) {
                cb.apply(null, resolvedArgs);
            });
            return deferred;
        },
        done: function(callback) {
            if (resolved) {
                callback.apply(null, resolvedArgs);
            } else {
                callbacks.push(callback);
            }
            return deferred;
        },
        promise: function() {
            return deferred;
        }
    };
    return deferred;
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

window.Coral = {
    commons: {
        ready: function(el, callback) {
            callback(el);
        }
    },
    Select: {
        Item: function() {
            this.content = { textContent: "" };
            this.value = "";
            this.selected = false;
        }
    }
};

// Mock document object
window.document = window.document || {
    addEventListener: function() {},
    querySelector: function() { return null; },
    querySelectorAll: function() { return []; }
};
