/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
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
    var RESULTS_JSON = "searchresults.json";
    var PARAM_RESULTS_OFFSET = "resultsOffset";
    var $getFacetFilterCheckbox = document.getElementsByName("facet");
    var currentPageUrl = window.location.href;
    var getPageURL = currentPageUrl.substring(0, currentPageUrl.lastIndexOf("."));
    var getQueryParam = window.location.search;
    var searchFieldListGroup = document.querySelector(".cmp-search-list__item-group");

    var searchField = document.querySelector(".search__field--view");

    var getRelativePath = searchField.dataset.cmpRelativePath;
    var getLoadMoreBtn = document.querySelector(".search__results--footer button");
    var searchResultEndMessage = document.getElementById("js-searchResults-endData");
    var $getSortAscDesVal = document.getElementById("js-sorting-des-asc");
    var $getSortDirVal = document.getElementById("js-sort-dir");

    var getCategory = new Array();
    var resultSize = 0;
    var LIST_GROUP;

    var getSortAscDesVal = getSortingVal($getSortAscDesVal);
    var getSortDirVal = getSortingVal($getSortDirVal);

    // GET SORTING DROP-DOWN VALUES
    function getSortingVal(val) {
        return Array.from(val.options)
            .filter(function(option) {
                return option.selected;
            })
            .map(function(option) {
                return option.value;
            });
    }

    // Load More Button Click Function
    getLoadMoreBtn.addEventListener("click", function(event) {
        resultSize = resultSize + parseInt(document.querySelector(".search__field--view").dataset.cmpResultsSize);
        getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
    });

    // CATEGORIES CLICK EVENT
    $getFacetFilterCheckbox.forEach(function(getFacetFilterCheckbox) {
        getFacetFilterCheckbox.addEventListener("click", function(event) {
            resultSize = 0;
            if (getFacetFilterCheckbox.checked) {
                getCategory.push(getFacetFilterCheckbox.value);
            } else {
                var NEW_LIST = getCategory.filter(function(item) {
                    return item !== getFacetFilterCheckbox.value;
                });
                getCategory = NEW_LIST;
            }
            getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
        });
    });

    // SORT BY CLICK EVENT AND PAGE LOAD
    $getSortAscDesVal.addEventListener("change", function(event) {
        resultSize = 0;
        getSortAscDesVal = getSortingVal($getSortAscDesVal);
        getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
    });

    $getSortDirVal.addEventListener("change", function(event) {
        resultSize = 0;
        getSortDirVal = getSortingVal($getSortDirVal.options);
        getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
    });

    // On page load function
    function onDocumentReady() {
        searchResultEndMessage.style.display = "none";
        getLoadMoreBtn.style.display = "none";
        fetchDataNew();
    }

    // FETCH DATA API CALL
    function fetchDataNew(getCategory) {
        var apiURL = getDataURL(getCategory);
        fetch(apiURL)
            .then(function(response) {
                return response.json();
            })
            .then(function(json) {
                return displayDataOnPage(json);
            });
    }

    // FETCH DATA URL CREATION
    function getDataURL(getCategory) {
        var fetchAPIURL = getPageURL + "." + RESULTS_JSON + getRelativePath + getQueryParam + "&" + PARAM_RESULTS_OFFSET + "=" + resultSize + "&orderby=" + getSortDirVal + "&sort=" + getSortAscDesVal;
        var fetchAPIURLNew = getCategory ? fetchAPIURL + "&tags=" + getCategory : fetchAPIURL;
        return fetchAPIURLNew;
    }

    // Check null value and replace with empty string
    function checkNull(inputValue) {
        var value = "";
        if (inputValue === null) {
            return value;
        } else {
            return inputValue;
        }
    }

    // DISPLAY DATA ON PAGE LOAD
    function displayDataOnPage(data) {

        if (resultSize === parseInt(0)) {
            searchFieldListGroup.innerHTML = "";
            LIST_GROUP = "";
        }
        var dataCount = Object.keys(data).length;
        if (dataCount !== 0) {
            searchResultEndMessage.style.display = "none";
            getLoadMoreBtn.style.display = "block";

        } else {
            searchResultEndMessage.style.display = "block";
            getLoadMoreBtn.style.display = "none";
        }

        for (var i = 0; i < dataCount; i++) {
            LIST_GROUP += "<li class='cmp-searchresult-item'><h3 class='cmp-searchresult-title'><a class='cmp-searchresult-link' href=" + checkNull(data[i].url) + ">" + checkNull(data[i].title) + "</a></h3><span class='cmp-searchresult-tags'>" + checkNull(data[i].tags) + "</span> <span class='cmp-searchresult-author'>" + checkNull(data[i].author) + "</span> | <span class='cmp-searchresult-date'>" + checkNull(data[i].formattedLastModifiedDate) + "</span> <p class='cmp-searchresult-description'>" + checkNull(data[i].description) + "</p></li>";
        }
        searchFieldListGroup.innerHTML = LIST_GROUP;

    }

    document.addEventListener("DOMContentLoaded", onDocumentReady);
})();
