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
        componentGroup: '.cmp-examples-container--component-group',
        componentTeaser: '.cmp-examples-teaser--component',
        noResults: '.cmp-examples-text--no-results'
    };

    var search;
    var componentGroups;
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

        componentGroups.forEach(function(group) {
            var groupVisible = false;
            var groupTeasers = group.querySelectorAll(selectors.componentTeaser);
            groupTeasers = [].slice.call(groupTeasers);

            for (var i = 0; i < groupTeasers.length; i++) {
                if (groupTeasers[i].style.display === "") {
                    groupVisible = true;
                    break;
                }
            }

            if (groupVisible) {
                group.style.display = "";
            } else {
                group.style.display = "none";
            }
        });

        if (noResults) {
            noResults.style.display = (total === count) ? "" : "none";
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        search = document.querySelector(selectors.self);
        if (search) {
            componentGroups = document.querySelectorAll(selectors.componentGroup);
            componentGroups = [].slice.call(componentGroups);
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
