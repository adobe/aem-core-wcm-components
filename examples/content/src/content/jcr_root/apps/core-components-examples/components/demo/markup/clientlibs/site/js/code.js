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
/*global PR, html_beautify */
(function(PR) {
    "use strict";

    function escapeHtml(html) {
        var text = document.createTextNode(html);
        var p = document.createElement("p");
        p.appendChild(text);
        return p.innerHTML;
    }

    function getJSON(url, callback) {
        var request = new XMLHttpRequest();
        request.open('GET', url, true);
        request.onload = function() {
            if (request.status >= 200 && request.status < 400) {
                // Success!
                var data = JSON.parse(request.responseText);
                callback(data);
            } else {
                // We reached our target server, but it returned an error

            }
        };
        request.send();
    }

    document.addEventListener("DOMContentLoaded", function() {
        document.querySelectorAll("pre.cmp-demo__json code").forEach(function(block) {
            getJSON(block.dataset.cmpSrc + ".model.json", function(data) {
                block.innerHTML = JSON.stringify(data);
            });
        });
        document.querySelectorAll("pre.cmp-demo__markup code").forEach(function(block) {
            block.innerHTML = escapeHtml(html_beautify(block.innerHTML, { "preserve_newlines": false }));
        });
        PR.prettyPrint();
    });
}(PR));
