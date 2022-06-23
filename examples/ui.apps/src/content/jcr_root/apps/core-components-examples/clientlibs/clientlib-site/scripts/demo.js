/*******************************************************************************
 * Copyright 2018 Adobe
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
/*global PR, html_beautify */
(function(Promise, Prettify, htmlBeautify) {
    "use strict";

    var selectors = {
        self: '[data-cmp-examples-is="demo"]',
        info: '[data-cmp-examples-hook-demo="info"]',
        json: '[data-cmp-examples-hook-demo="json"]',
        jsonLink: '[data-cmp-examples-hook-demo="jsonLink"]',
        markup: '[data-cmp-examples-hook-demo="markup"]',
        hideCode: '[data-cmp-examples-hook-demo="hideCode"]',
        showCode: '[data-cmp-examples-hook-demo="showCode"]',
        copyCode: '[data-cmp-examples-hook-demo="copyCode"]',
        activePre: '.cmp-tabs__tabpanel--active pre'
    };

    function escapeHtml(html) {
        var text = document.createTextNode(html);
        var p = document.createElement('p');
        p.appendChild(text);
        return p.innerHTML;
    }

    // From https://developers.google.com/web/fundamentals/primers/promises#promisifying_xmlhttprequest
    // Code samples are licensed under the Apache 2.0 License
    function get(url) {
        // Return a new promise.
        return new Promise(function(resolve, reject) {
            // Do the usual XHR stuff
            var req = new XMLHttpRequest();
            req.open('GET', url);

            req.onload = function() {
                // This is called even on 404 etc
                // so check the status
                if (req.status == 200) {
                    // Resolve the promise with the response text
                    resolve(req.response);
                }
                else {
                    // Otherwise reject with the status text
                    // which will hopefully be a meaningful error
                    reject(Error(req.statusText));
                }
            };

            // Handle network errors
            req.onerror = function() {
                reject(Error("Network Error"));
            };

            // Make the request
            req.send();
        });
    }

    // From https://developers.google.com/web/fundamentals/primers/promises#promisifying_xmlhttprequest
    // Code samples are licensed under the Apache 2.0 License
    function getJSON(url) {
        return get(url).then(JSON.parse);
    }

    function ready(fn) {
        if (document.attachEvent ? document.readyState === "complete" : document.readyState !== "loading"){
            fn();
        } else {
            document.addEventListener('DOMContentLoaded', fn);
        }
    }

    ready(function() {
        var deferreds = [];

        var demos = document.querySelectorAll(selectors.self);
        demos = [].slice.call(demos);
        demos.forEach(function(demo) {
            var hideCode = demo.querySelector(selectors.hideCode);
            var showCode = demo.querySelector(selectors.showCode);
            var copyCode = demo.querySelector(selectors.copyCode);
            var info = demo.querySelector(selectors.info);
            var json = demo.querySelector(selectors.json);
            var jsonLink = demo.querySelector(selectors.jsonLink);
            var jsonSrc = "";
            var markup = demo.querySelector(selectors.markup);

            if (jsonLink) {
                jsonSrc = jsonLink.href;

                // a link to the model JSON is presented initially in the markup so that the content
                // can be scraped when exporting a static version of the library
                if (jsonLink.parentNode) {
                    jsonLink.parentNode.removeChild(jsonLink);
                }
            }

            if (json) {
                deferreds.push(getJSON(jsonSrc).then(function(data) {
                    json.innerText = JSON.stringify(data, null, 2);
                }));
            }

            if (markup) {
                try {
                    markup.innerHTML = escapeHtml(htmlBeautify(markup.innerHTML, { 'preserve_newlines': false, 'indent_size': 2 }));
                } catch (err) {
                    markup.innerHTML = escapeHtml(markup.innerHTML);
                }
            }

            if (hideCode) {
                hideCode.addEventListener('click', function() {
                    info.classList.remove('cmp-examples-demo__info--open');
                    hideCode.disabled = true;
                    showCode.disabled = false;
                    copyCode.disabled = true;
                });
            }

            if (showCode) {
                showCode.addEventListener('click', function() {
                    info.classList.add('cmp-examples-demo__info--open');
                    hideCode.disabled = false;
                    showCode.disabled = true;
                    copyCode.disabled = false;
                });
            }

            if (copyCode) {
                copyCode.addEventListener('click', function(event) {
                    var activePre = demo.querySelector(selectors.activePre);

                    if (activePre) {
                        var tempTextarea = document.createElement('textarea');
                        tempTextarea.value = activePre.innerText;
                        document.body.appendChild(tempTextarea);
                        tempTextarea.select();

                        window.CmpExamples.Notification.show('Copied to clipboard', window.CmpExamples.Notification.state.SUCCESS);

                        try {
                            document.execCommand('copy');
                        } catch(error) {
                            window.CmpExamples.Notification.show('Unable to copy to clipboard', window.CmpExamples.Notification.state.ERROR);
                        }

                        document.body.removeChild(tempTextarea);
                    }
                });
            }
        });

        // Prettify once all JSON requests have completed
        Promise.all(deferreds).then(function() {
            Prettify.prettyPrint();
        });
    });
}(Promise, PR, html_beautify));
