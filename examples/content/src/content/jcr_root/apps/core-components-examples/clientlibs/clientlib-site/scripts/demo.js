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
/*global jQuery, PR, html_beautify */
(function($, Prettify, htmlBeautify) {
    "use strict";

    var selectors = {
        self: '[data-cmp-examples-is="demo"]',
        info: '[data-cmp-examples-hook-demo="info"]',
        json: '[data-cmp-examples-hook-demo="json"]',
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

    document.addEventListener('DOMContentLoaded', function() {
        var deferreds = [];

        document.querySelectorAll(selectors.self).forEach(function(demo) {
            var hideCode = demo.querySelector(selectors.hideCode);
            var showCode = demo.querySelector(selectors.showCode);
            var copyCode = demo.querySelector(selectors.copyCode);
            var info = demo.querySelector(selectors.info);
            var json = demo.querySelector(selectors.json);
            var markup = demo.querySelector(selectors.markup);

            if (json) {
                deferreds.push($.getJSON(json.dataset.cmpSrc + '.model.json', function(data) {
                    json.innerText = JSON.stringify(data, null, 2);
                }));
            }

            if (markup) {
                markup.innerHTML = escapeHtml(htmlBeautify(markup.innerHTML, { 'preserve_newlines': false, 'indent_size': 2 }));
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
        $.when.apply($, deferreds).then(function() {
            Prettify.prettyPrint();
        });
    });
}(jQuery, PR, html_beautify));
