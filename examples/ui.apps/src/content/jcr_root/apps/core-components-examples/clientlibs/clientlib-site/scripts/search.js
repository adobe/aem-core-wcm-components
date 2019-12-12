/*******************************************************************************
 * Copyright 2019 Adobe
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

    var selectors = {
        self: '[name="cmp-examples-component-search"]',
        componentTeaser: '.cmp-examples-teaser--component',
        noResults: '.cmp-examples-text--no-results'
    };

    var search;
    var componentTeasers;
    var noResults;

    function updateResults(token) {
        var total = componentTeasers.length;
        var count = 0;
        componentTeasers.forEach(function(teaser) {
            var teaserText = teaser.textContent || teaser.innerText;
            if (teaserText.toUpperCase().indexOf(token.toUpperCase()) > -1) {
                teaser.style.display = "";
            } else {
                teaser.style.display = "none";
                count++;
            }
        });

        if (noResults) {
            noResults.style.display = (total === count) ? "" : "none";
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        search = document.querySelector(selectors.self);
        if (search) {
            componentTeasers = document.querySelectorAll(selectors.componentTeaser);
            componentTeasers = [].slice.call(componentTeasers);
            noResults = document.querySelector(selectors.noResults);

            if (noResults) {
                noResults.style.display = "none";
            }

            search.addEventListener('input', function(event) {
                updateResults(search.value);
            })
        }
    });
}());
