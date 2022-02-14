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
        searchResultsStatus: '.cmp-examples-search-results-status'
    };

    var search;
    var componentGroups;
    var componentTeasers;

    function updateResults(token) {
        var count = 0;
        var foundComponentTeasers = 0;

        componentTeasers.forEach(function(teaser) {
            var teaserText = teaser.textContent || teaser.innerText;
            if (teaserText.toUpperCase().indexOf(token.toUpperCase()) > -1) {
                teaser.style.display = "";
                foundComponentTeasers += 1;
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

        updateSearchResultsStatusMessage(token, foundComponentTeasers);
    }

    // useful for Accessibility, helping users with low vision and users with cognitive disabilities to identify the change in results
    function updateSearchResultsStatusMessage(token, foundComponentTeasers) {
        if (!token) {
            document.querySelector(selectors.searchResultsStatus).innerHTML = "";
        } else {
            var searchResultsFoundMessage = `<p aria-live="polite" role="status">${foundComponentTeasers} ${foundComponentTeasers === 1 ?
                "component" : "components"} found based on your search.</p>`;
            var searchResultsNotFoundMessage = `<p aria-live="polite" role="status">No components found. Please try a different search.</p>`;
            document.querySelector(selectors.searchResultsStatus).innerHTML = foundComponentTeasers > 0 ?
                searchResultsFoundMessage : searchResultsNotFoundMessage;
        }
    }

    function createSearchResultsStatusElement() {
        var searchInput = document.querySelector(selectors.self);
        var infoMessageContainer = document.createElement("div");
        infoMessageContainer.setAttribute("class", "cmp-examples-search-results-status");
        infoMessageContainer.setAttribute("aria-live", "polite");
        infoMessageContainer.setAttribute("role", "status");
        searchInput.parentNode.insertBefore(infoMessageContainer , searchInput.parentNode.firstChild);
    }

    document.addEventListener('DOMContentLoaded', function() {
        search = document.querySelector(selectors.self);
        if (search) {
            createSearchResultsStatusElement();
            componentGroups = document.querySelectorAll(selectors.componentGroup);
            componentGroups = [].slice.call(componentGroups);
            componentTeasers = document.querySelectorAll(selectors.componentTeaser);
            componentTeasers = [].slice.call(componentTeasers);

            search.addEventListener('input', function(event) {
                updateResults(search.value);
            })
        }
    });
}());
