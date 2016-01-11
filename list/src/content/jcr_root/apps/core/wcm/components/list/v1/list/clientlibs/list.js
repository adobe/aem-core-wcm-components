/*******************************************************************************
 * Copyright 2015 Adobe Systems Incorporated
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
CQ.WCM = CQ.WCM || {};

CQ.WCM.List = CQ.WCM.List || function () {
    "use strict";

    var self = {};

    var currentState = null;

    var _extractObject = function(queryString) {
        var params = queryString.split("&");
        var paramObj = {

        };

        for (var idx = 0 ; idx < params.length ; idx++) {
            var param = params[idx];
            var paramInfo = param.split("=");
            if (paramInfo.length >= 2) {
                paramObj[paramInfo[0]] = paramInfo[1];
            }
        }

        return paramObj;
    }

    var _combineQueryStrings = function(queryString1, queryString2) {
        var paramObj1 = _extractObject(queryString1);
        var paramObj2 = _extractObject(queryString2);

        for (var param in paramObj2) {
            paramObj1[param] = paramObj2[param];
        }

        var combinedQueryString = "";

        for (var param in paramObj1) {
            if (combinedQueryString != "") {
                combinedQueryString += "&";
            }
            combinedQueryString += param + "=" + paramObj1[param];
        }

        return combinedQueryString;
    };

    self.linkClick = function(element, listId) {
        var goToLink = element.href;

        var urlRegex = /https?:\/\/[^\\/]*(.*)/gi;
        var matches = urlRegex.exec(goToLink);
        var queryString = "";

        if (matches
            && matches.length > 0) {
            var linkPath = matches[1];
            var queryIdx = linkPath.indexOf("?");
            if (queryIdx >= 0) {
                queryString = linkPath.substring(queryIdx + 1);
            }
        }

        // do XHR to load list
        var  xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if (xhr.readyState == 4
                && xhr.status == 200
                && xhr.responseText != "") {
                var targetList = document.getElementById(listId);
                if (targetList) {

                    if (!window.history.state) {
                        window.history.replaceState({
                            listId: listId,
                            content: targetList.outerHTML
                        }, null, window.location.href);
                    }

                    if (currentState
                            && currentState.listId != listId) {
                        currentState["extraListId"] = listId;
                        currentState["extraContent"] = targetList.outerHTML;
                        window.history.replaceState(currentState, null, window.location.href);
                    }

                    targetList.outerHTML = xhr.responseText;

                    // focus first item of the new list
                    var newTargetList = document.getElementById(listId);
                    var firstListItem = newTargetList.getElementsByTagName("li")[0];
                    var anchors = firstListItem.getElementsByTagName("a");
                    if (anchors.length === 0) {
                        // fall back: no anchor in first list item; focus li itself
                        firstListItem.setAttribute("tabindex", "0");
                        firstListItem.focus();
                    } else {
                        // default: select anchor in first list item
                        anchors[0].focus();
                    }

                    var stateContent = xhr.responseText;
                    var currentUrl = window.location.href;
                    var currentQueryString = "";
                    var queryIdx = currentUrl.indexOf("?");
                    if (queryIdx >= 0) {
                        currentQueryString = currentUrl.substring(queryIdx + 1);
                        currentUrl = currentUrl.substring(0, queryIdx);
                    }

                    var useQueryString = _combineQueryStrings(currentQueryString, queryString);

                    currentState = {
                        listId: listId,
                        content: stateContent
                    };

                    window.history.pushState(currentState, null, currentUrl + "?" + useQueryString);
                }
            }
        }
        xhr.open("GET", goToLink, true);
        xhr.send();

        return false;
    };

    var _updateList = function (listId, content) {
        var targetList = document.getElementById(listId);
        if (targetList) {
            targetList.outerHTML = content;
        }
    }

    self.handlePopStateListNavigation = function(event) {
        if (event.state) {
            _updateList(event.state.listId, event.state.content);

            if (event.state.extraListId) {
                _updateList(event.state.extraListId, event.state.extraContent);
            }
        }
    };

    self.bindHistoryPopStateEvent = function() {
        if (window.addEventListener) {
            window.addEventListener("popstate", CQ.WCM.List.handlePopStateListNavigation);
        } else if (window.attachEvent) {
            window.attachEvent("popstate", CQ.WCM.List.handlePopStateListNavigation);
        }
    };

    return self;
}();

CQ.WCM.List.bindHistoryPopStateEvent();
