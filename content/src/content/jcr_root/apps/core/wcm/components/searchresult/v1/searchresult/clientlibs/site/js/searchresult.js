/*******************************************************************************
* Copyright 2017 Adobe Systems Incorporated
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
    "use strict";
    var RESULTS_JSON = "searchresults.json";
    var PARAM_RESULTS_OFFSET = "resultsOffset";
    var $getFacetFilterCheckbox = document.getElementsByName("facet");
    var currentPageUrl = window.location.href;
    var getPageURL = currentPageUrl.substring(0, currentPageUrl.lastIndexOf("."));
    var getQueryParam = window.location.search;
    var getRelativePath = document.querySelector(".search__field--view").dataset.cmpRelativepath;
    var $getLoadMoreBtn = document.querySelector(".search__results--footer button");
    var getCategory = new Array();
    var resultSize = 0;
    
    // Load More Button Click Function
    $getLoadMoreBtn.addEventListener("click", function(event) {
        resultSize = resultSize + parseInt(document.querySelector(".search__field--view").dataset.cmpResultsSize);
        console.log(resultSize)
        fetchDataNew();
    })
    
    //CATEGORIES CLICK EVENT
    $getFacetFilterCheckbox.forEach(function(getFacetFilterCheckbox) {
        getFacetFilterCheckbox.addEventListener("click", function(event) {
            getCategory = $(
                'input[type="checkbox"]:checked'
            )
            .map(function() {
                return this.value;
            })
            .get();
            getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
        });
    });
    
    // On page load function
    function onDocumentReady() {
        fetchDataNew();
    }
    
    //FETCH DATA API CALL
    function fetchDataNew(getCategory) {
        let apiURL = getDataURL(getCategory);
        console.log(apiURL);
        fetch(apiURL)
        .then(function(response) {
            return response.json();
        })
        .then(function(json) {
            return displayDataOnPage(json);
        });
    }
    
    //FETCH DATA URL CREATION
    function getDataURL(getCategory) {
        var getDefaultDisplayOfSearchResults = getCategory ? 0 : resultSize;
        var fetchAPIURL =
            getPageURL +
                "." +
                    RESULTS_JSON + getRelativePath+
                        getQueryParam +
                            "&" +
                                PARAM_RESULTS_OFFSET +
                                    "=" +
                                        getDefaultDisplayOfSearchResults;
        var fetchAPIURLNew = getCategory
        ? fetchAPIURL + "&tag=" + getCategory
        : fetchAPIURL;
        return fetchAPIURLNew;
    }
    
    //DISPLAY DATA ON PAGE LOAD
    function displayDataOnPage(data) {
        var items = [];
        resultSize === 0 ? $(".search__field--view").empty() : '';
        $.each(data, function (key, val) {
            items.push(
                "<li id='" +
                key +
                "'><img src='https://dummyimage.com/600x400/000/fff' class='search__img--card' /><h3>" +
                val.title +
                "</h3><span>" +
                val.lastModified +
                "</span> | <span>" +
                val.path +
                "</span> | <span>" +
                val.url +
                "</span><p>" +
                val.description +
                "</p></li>"
            );
        });
        $("<ul/>", {
            class: "my-new-list",
            html: items.join("")
        }).appendTo(".search__field--view");
    }
    
    document.addEventListener("DOMContentLoaded", onDocumentReady);
})();