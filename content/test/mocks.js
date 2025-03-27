/*******************************************************************************
 * Copyright 2025 Adobe
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
    find(selector) {
        const elem = this[0].querySelector(selector)
        return elem ? new JQueryArray(elem) : new JQueryArray();
    }

    attr(name) {
        return this[0] ? this[0].getAttribute(name) : null;
    }
}

function jQuery(obj) {
    return new JQueryArray(obj);
}

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
};
