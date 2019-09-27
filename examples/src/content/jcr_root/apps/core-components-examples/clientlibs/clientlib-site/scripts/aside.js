/*
 *  Copyright 2018 Adobe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
(function() {
    "use strict";

    var selectors = {
        self: '.cmp-examples-structure__aside',
        showMenu: '[data-cmp-examples-is="showMenu"]'
    };

    var cssClasses = {
        open: 'cmp-examples-structure__aside--open',
        mask: {
            self: 'cmp-examples-mask',
            open: 'cmp-examples-mask--open'
        }
    };

    var mask;

    ready(function() {
        var showMenuActions = document.querySelectorAll(selectors.showMenu);
        var aside = document.querySelector(selectors.self);
        mask = document.createElement('div');
        mask.classList.add(cssClasses.mask.self);
        document.body.appendChild(mask);

        if (aside) {
            for (var i = 0; i < showMenuActions.length; ++i) {
                showMenuActions[i].addEventListener('click', function(event) {
                    event.stopPropagation();
                    aside.classList.add(cssClasses.open);
                    mask.classList.add(cssClasses.mask.open);
                });
            }

            mask.addEventListener('click', function (event) {
                aside.classList.remove(cssClasses.open);
                mask.classList.remove(cssClasses.mask.open);
            }, false);

            aside.addEventListener('click', function (event) {
                event.stopPropagation();
            }, false);
        }
    });

    function ready(fn) {
        if (document.attachEvent ? document.readyState === "complete" : document.readyState !== "loading"){
            fn();
        } else {
            document.addEventListener('DOMContentLoaded', fn);
        }
    }

})();
