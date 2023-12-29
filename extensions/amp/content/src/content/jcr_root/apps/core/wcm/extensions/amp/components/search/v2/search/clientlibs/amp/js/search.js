/*******************************************************************************
 * Copyright 2022 Adobe
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
    window.onload = updateSearchResultsStatusMessageElement;

    var selectors = {
        searchComponent: ".cmp-search",
        searchInput: ".cmp-search__input",
        searchResultsContainer: ".i-amphtml-autocomplete-results",
        itemInSearchResults: ".i-amphtml-autocomplete-item",
        searchResultsStatusMessage: ".cmp_search__info"
    };

    function updateSearchResultsStatusMessageElement() {
        var observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                var mutatedSearchComponent = mutation.target.closest(selectors.searchComponent);
                if (mutatedSearchComponent) {
                    var searchResultsContainer = mutatedSearchComponent.querySelector(selectors.searchResultsContainer);
                    var searchInput = mutatedSearchComponent.querySelector(selectors.searchInput);
                    var searchResultsStatusMessage = mutatedSearchComponent.querySelector(selectors.searchResultsStatusMessage);
                    if (searchResultsContainer && searchInput && searchResultsStatusMessage) {
                        if (!searchInput.value || searchResultsContainer.hasAttribute("hidden")) {
                            searchResultsStatusMessage.innerText = "";
                        } else {
                            var numberOfResults = searchResultsContainer.querySelectorAll(selectors.itemInSearchResults);
                            var searchResultsFoundMessage = numberOfResults.length === 1 ? numberOfResults.length + " result" : numberOfResults.length + " results";
                            var searchResultsNotFoundMessage = "No results";
                            searchResultsStatusMessage.innerText = numberOfResults.length ? searchResultsFoundMessage : searchResultsNotFoundMessage;
                        }
                    }
                }
            });
        });
        var searchComponents = document.querySelectorAll(selectors.searchComponent);
        if (searchComponents.length) {
            for (var i = 0; i < searchComponents.length; i++) {
                var searchResultsContainer = searchComponents[i].querySelector(selectors.searchResultsContainer);
                observer.observe(searchResultsContainer, {
                    attributes: true,
                    attributeFilter: ["hidden"],
                    subtree: true,
                    childList: true
                });
            }
        }
    }
})();
