/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
(function () {
    'use strict';

    var devicePixelRatio = window.devicePixelRatio || 1;

    function SmartImage(noScriptElement, options) {
        var that = this,
            showsLazyLoader = false,
            image,
            container,
            anchor,
            dropContainer,
            updateMode,
            initDone = false;

            that.defaults = {
                loadHidden: false,
                imageSelector: 'img',
                containerSelector: '.cmp-image',
                sourceAttribute: 'src',
                lazyEnabled: true,
                lazyThreshold: 0,
                lazyEmptyPixel: 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7',
                lazyLoaderClass: 'loading',
                lazyLoaderStyle: {
                    'height': 0,
                    'padding-bottom': '' // will get replaced with ratio in %
                }
            };

        function init() {
            var tmp = document.createElement('div');
            tmp.innerHTML = decodeNoScript(noScriptElement.textContent.trim());
            var imageElement = tmp.firstElementChild;
            var source = imageElement.getAttribute(options.sourceAttribute);
            imageElement.removeAttribute(options.sourceAttribute);
            imageElement.setAttribute('data-src-disabled', source);
            noScriptElement.remove();
            container.insertBefore(imageElement, container.firstChild);

            if (container.matches(options.imageSelector)) {
                image = container;
            } else {
                image = container.querySelector(options.imageSelector);
            }

            that.container = container;
            that.options = options;
            that.image = image;
            initLazy();
            window.addEventListener('scroll', that.update);
            window.addEventListener('resize', that.update);
            window.addEventListener('update', that.update);
        }

        function initLazy() {
            if (options.lazyEnabled) {
                addLazyLoader();
                if (isLazyVisible()) {
                    initSmart();
                } else {
                    image.classList.add(options.lazyLoaderClass);
                    updateMode = 'lazy';
                    setTimeout(that.update, 200);
                }
            } else {
                initSmart();
            }
        }

        function initSmart() {
            if (initDone) {
                return;
            }

            if (options.smartSizes && options.smartImages && options.smartSizes.length > 0) {
                if (console && options.smartSizes.length !== options.smartImages.length) {
                    console.warn('The size of the smartSizes and of the smartImages arrays do not match!');
                } else {
                    updateMode = 'smart';
                    that.update();
                }
            } else if (options.loadHidden || container.offsetParent !== null) {
                image.setAttribute(options.sourceAttribute, image.getAttribute('data-src-disabled'));
                image.removeAttribute('data-src-disabled');
            }

            if (showsLazyLoader) {
                image.addEventListener('load', removeLazyLoader);
            }

            initDone = true;
        }

        function addLazyLoader() {
            var width = image.getAttribute('width'),
                height = image.getAttribute('height');

            if (width && height) {
                var ratio = (height / width) * 100,
                    styles = options.lazyLoaderStyle;

                styles['padding-bottom'] = ratio + '%';
                for (var s in styles) {
                    if (styles.hasOwnProperty(s)) {
                        image.style[s] = styles[s];
                    }
                }
            }

            image.setAttribute(options.sourceAttribute, options.lazyEmptyPixel);
            showsLazyLoader = true;
        }

        function removeLazyLoader() {
            image.classList.remove(options.lazyLoaderClass);
            for (var property in options.lazyLoaderStyle) {
                if (options.lazyLoaderStyle.hasOwnProperty(property)) {
                    image.style[property] = '';
                }
            }
            image.removeEventListener('load', removeLazyLoader);
            showsLazyLoader = false;
        }

        function isLazyVisible() {
            if (container.offsetParent === null) {
                return false;
            }

            var wt = window.pageYOffset,
                wb = wt + document.documentElement.clientHeight,
                et = container.getBoundingClientRect().top + wt,
                eb = et + container.clientHeight;

            return eb >= wt - options.lazyThreshold && et <= wb + options.lazyThreshold;
        }

        that.update = function () {
            if (updateMode === 'lazy') {
                if (isLazyVisible()) {
                    window.removeEventListener('scroll', that.update);
                    initSmart();
                }
            } else if (updateMode === 'smart' && (options.loadHidden || container.offsetParent !== null)) {
                var containerWidth = 0;
                if (container.tagName.toLowerCase() === 'a') {
                    containerWidth = container.parentElement.clientWidth;
                } else {
                    containerWidth = container.clientWidth;
                }
                var optimalSize = containerWidth * devicePixelRatio,
                    len = options.smartSizes.length,
                    key = 0;

                while ((key < len-1) && (options.smartSizes[key] < optimalSize)) {
                    key++;
                }

                if (image.getAttribute(options.sourceAttribute) !== options.smartImages[key]) {
                    image.setAttribute(options.sourceAttribute, options.smartImages[key]);
                }
                image.removeAttribute('data-src-disabled');
            }
        };

        options = Object.assign(that.defaults, options);
        
        container = noScriptElement.closest(options.containerSelector);
        if(container) {
            dropContainer = noScriptElement.closest('.cq-dd-image');
            if(dropContainer) {
                container = dropContainer;
            }
            anchor = container.querySelector('a.cmp-image--link');
            if(anchor !== null) {
                container = anchor;
            }
            init();
        }
    }

    var imageElements = document.querySelectorAll('[data-cmp-image]');
    var images = [];
    for (var index = 0; index < imageElements.length; index++) {
        var noScriptElement = imageElements[index];
        var imageOptions = noScriptElement.dataset.cmpImage;
        noScriptElement.removeAttribute('data-cmp-image');
        images.push(new SmartImage(noScriptElement, JSON.parse(imageOptions)));
    }
    var MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver;
    var body = document.querySelector('body');
    var observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            // needed for IE
            var nodesArray = [].slice.call(mutation.addedNodes);
            if (nodesArray.length > 0) {
                nodesArray.forEach(function (addedNode) {
                    if(addedNode.querySelectorAll) {
                        var noScriptArray = [].slice.call(addedNode.querySelectorAll('noscript[data-cmp-image]'));
                        noScriptArray.forEach(function (noScriptElement) {
                            var imageOptions = JSON.parse(noScriptElement.dataset.cmpImage);
                            noScriptElement.removeAttribute('data-cmp-image');
                            images.push(new SmartImage(noScriptElement, imageOptions));
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

    /*
         on drag & drop of the component into a parsys, noscript's content will be escaped multiple times by the editor which creates
         the DOM for editing; the HTML parser cannot be used here due to the multiple escaping
     */
    function decodeNoScript(text){
        text = text.replace(/&(amp;)*lt;/g, '<');
        text = text.replace(/&(amp;)*gt;/g, '>');
        return text;
    }
})();
