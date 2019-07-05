/*
 *  Copyright 2016 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

 window.digitalData = window.digitalData || {};
    window.digitalData.page = window.digitalData.page || {};
    window.digitalData.user = window.digitalData.user || {};
    window.digitalData = {
        page: {
            pageInfo: {
                pageName: '',
                pageURL: window.location.href,
                pageRef: document.referrer // Referring URL
            	},
            category:{
            	primaryCategory: '', //Main Section as per the Site Hierarchy
                subCategory: '', //Site Section 1 as per the Site Hierarchy
                pageType: ''
             	},
            attributes:{
            	locale: navigator.language, // Locale of the site
                domain: window.location.hostname, // Domain of the site
                language: '', //Language where the user has visted(en, etc)
                country: navigator.language.split("-")[1] //Country where the user has visted(US, etc)
            	}
        },
        user: {
            userAgent: navigator.userAgent
        }
    }

    // DATALAYERS UPDATE SCRIPT
    var getSearchField = document.querySelector(".cmp-search__input");
    var digitalData = window.digitalData;
    var pageName,
      pageType,
      primaryCategory,
      subCategory,
      pageLanguage,
      getSearchVal;

    // GET INPUT SERACH DATA ENTER BY USER
    function updateDigitalDataBySearch() {
      getSearchVal = getSearchField.value.toLowerCase();
      if (getSearchVal == "") {
        return false;
      } else {
        searchDigitalData = {
          search: {
            searchType: "",
            typedSearchTerm: getSearchVal,
            autoSearchFlag: "",
            typedSearchCount: "",
            internalSearchTerm: "",
            searchResults: ""
          }
        };
        Object.assign(digitalData, searchDigitalData);
        console.log(digitalData);
      }
    }

    // CAPTURE PAGE DETAILS LIKE META TAGS AND TITLE
    var capturePageDetails = () => {
      pageName = document.title;
      pageType = getMetaContent("template");
      primaryCategory = getMetaContent("primaryCategory");
      subCategory = getMetaContent("secondaryCategory");
      pageLanguage = document
        .getElementsByTagName("html")[0]
        .getAttribute("lang");
    };

    // GET PAGE META TAG VALUE
    function getMetaContent(name) {
      name = document.getElementsByTagName("meta")[name];
      if (name != undefined) {
        name = name.getAttribute("content");
        if (name != undefined) {
          return name;
        }
      }
      return null;
    }
    // UPDATE WINDOW.DIGITALDATA VALUES
    var pushValDigitalData = () => {
      var digitalData = window.digitalData;
      for (var key in digitalData) {
        if (digitalData.hasOwnProperty(key)) {
          var obj = digitalData[key];
          for (var prop in obj) {
            if (obj.hasOwnProperty(prop)) {
              if (obj[prop].pageName == "" || obj[prop].pageName) {
                obj[prop].pageName = pageName;
              }
              if (
                obj[prop].primaryCategory == "" ||
                obj[prop].primaryCategory
              ) {
                obj[prop].primaryCategory = primaryCategory;
              }
              if (obj[prop].subCategory == "" || obj[prop].subCategory) {
                obj[prop].subCategory = subCategory;
              }
              if (obj[prop].pageType == "" || obj[prop].pageType) {
                obj[prop].pageType = pageType;
              }
              if (obj[prop].language == "" || obj[prop].language) {
                obj[prop].language = pageLanguage;
              }
              if (
                obj[prop].typedSearchTerm == "" ||
                obj[prop].typedSearchTerm
              ) {
                obj[prop].typedSearchTerm = getSearchVal;
              }
            }
          }
        }
      }
    };

    document.addEventListener("click", function(event) {
      if (event.target.classList.contains("cmp-search__results")) {
        updateDigitalDataBySearch();
      } else {
        capturePageDetails();
        pushValDigitalData();
      }
    });
    document.addEventListener("DOMContentLoaded", function() {
      capturePageDetails();
      pushValDigitalData();
    });
