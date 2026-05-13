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

    if (obj === globalThis) {
        result.adaptTo = function(type) {
            if (type === 'foundation-registry') {
                return globalThis.foundationRegistry;
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
        return jQuery.ajax({ type: 'GET', ...url });
    }
    const state = { doneCb: null, alwaysCb: null, scheduled: false };
    function schedule() {
        if (state.scheduled) {
            return;
        }
        state.scheduled = true;
        setTimeout(function() {
            let html = '';
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
        then: function(onSucc, onFail) { // NOSONAR - mimicking jQuery deferred API
            this.done(function(...args) {
                if (onSucc) {
                    onSucc(...args);
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
    const args = Array.prototype.slice.call(arguments);
    if (args.length === 0) {
        return {
            done: function(cb) {
                setTimeout(cb, 0);
                return this;
            },
            then: function(ok) { // NOSONAR - mimicking jQuery deferred API
                this.done(ok || function() {});
                return this;
            }
        };
    }
    let remaining = args.length;
    const results = new Array(args.length);
    const pendingDone = [];

    function flush() {
        if (remaining !== 0) {
            return;
        }
        const copy = pendingDone.slice();
        pendingDone.length = 0;
        copy.forEach(function(cb) {
            cb(...results);
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
                    cb(...results);
                }, 0);
            } else {
                pendingDone.push(cb);
            }
            return this;
        },
        then: function(onOk, onFail) { // NOSONAR - mimicking jQuery deferred API
            this.done(function(...args) {
                if (onOk) {
                    onOk(...args);
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
    const callbacks = [];
    let resolved = false;
    let resolvedArgs = [];
    const deferred = {
        resolve: function() {
            resolved = true;
            resolvedArgs = Array.prototype.slice.call(arguments);
            const cbs = callbacks.slice();
            callbacks.length = 0;
            cbs.forEach(function(cb) {
                cb(...resolvedArgs);
            });
            return deferred;
        },
        done: function(callback) {
            if (resolved) {
                callback(...resolvedArgs);
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
globalThis.$ = jQuery;

Granite = {
    author: {
        util: {
            mixin: function (dest, src) {
                for (const prop in src) {
                    if (Object.hasOwn(src, prop)) {
                        dest[prop] = src[prop];
                    }
                }
            },
            createClass: function (classDefinition) {
                const methods = {};

                if (!classDefinition.constructor) {
                    classDefinition.constructor = function () {
                    };
                }

                for (const prop in classDefinition) {
                    if (Object.hasOwn(classDefinition, prop) && prop !== "constructor") {
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
globalThis.foundationRegistry = {
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
        return globalThis.foundationRegistry;
    }
    return null;
};

// Make sure $ also has the adaptTo method
jQuery.adaptTo = function(type) {
    if (type === 'foundation-registry') {
        return globalThis.foundationRegistry;
    }
    return null;
};

// Mock adaptTo on the global object as well
globalThis.adaptTo = function(type) {
    if (type === 'foundation-registry') {
        return globalThis.foundationRegistry;
    }
    return null;
};

globalThis.Coral = {
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
globalThis.document = globalThis.document || {
    addEventListener: function() {},
    querySelector: function() { return null; },
    querySelectorAll: function() { return []; }
};

globalThis.CQ = globalThis.CQ || {};
globalThis.CQ.CoreComponents = globalThis.CQ.CoreComponents || {};
if (!globalThis.CQ.CoreComponents.CheckboxTextfieldTuple) {
    globalThis.CQ.CoreComponents.CheckboxTextfieldTuple = {
        v1: function CheckboxTextfieldTupleStub() {
            this.hideCheckbox = function() {};
            this.reset = function() {};
            this.reinitCheckbox = function() {};
            this.hideTextfield = function() {};
            this.seedTextValue = function() {};
            this.update = function() {};
        }
    };
}

/** Filled by Image v2 or v3 editor image.js when present (Karma loads those scripts after mocks). */
globalThis.__IMAGE_V2_EDITOR_TEST_API = {};
globalThis.__IMAGE_V3_EDITOR_TEST_API = {};
/** Filled by Content Fragment v1 editor editDialog.js when present (Karma loads that script after mocks). */
globalThis.__CONTENTFRAGMENT_V1_DIALOG_TEST_API = {};
/** Filled by Content Fragment List v1 editor contentfragmentlist.js when present (Karma loads that script after mocks). */
globalThis.__CONTENTFRAGMENTLIST_V1_EDITOR_TEST_API = {};
