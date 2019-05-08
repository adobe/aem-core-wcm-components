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
/* Getting Page URL  Modal ID */

var getValOnPage = document.createElement("div");
getValOnPage.setAttribute("id", "data-modal-content");
document.body.appendChild(getValOnPage);
var getUrlModalID;

(function() {
    "use strict";

    function getModalOpenBehavior() {
        var getUrlModal = window.location.hash.substr(1).split("?")[0];
        var isModalOn = document.querySelectorAll("div[data-modal-show='true']");
        if ((getUrlModal !== "" && isModalOn.length === 0) || (getUrlModal !== "" && isModalOn.length >= 0)) {
            return (
                getUrlModalID = getUrlModal
            );
        } else if ((getUrlModal === "" && isModalOn.length > 0)) {
            var isModalOnVal = isModalOn[0].id;
            return (
                getUrlModalID = isModalOnVal
            );
        } else if ((getUrlModal !== "" && isModalOn.length === 0)) {
            return (
                getUrlModalID = getUrlModal
            );
        }
    }

    function fetchData(url, insertModalContent) {
        var xhttp;
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                insertModalContent(this.responseText);
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();
    }

    function initializeModal(xhttp) {
        document.getElementById("data-modal-content").innerHTML = xhttp;
        var modalOpen = new tingle.modal({
            onClose: function() {
                window.location.hash = "";
            }
        });
        modalOpen.open();
        modalOpen.setContent(document.getElementById("data-modal-content").innerHTML);
    }

    function openModalBasedOnHash(event) {
        event.preventDefault();
        if (window.location.hash !== "") {
            getUrlModalID = getModalOpenBehavior();
            if (getUrlModalID) {
                var modalContentUrl = document.getElementById(getUrlModalID).getAttribute("data-content-url");
                fetchData(modalContentUrl, initializeModal);
                document.getElementById("data-modal-content").style.display = "none";
            }
        }

    }
    window.addEventListener("hashchange", openModalBasedOnHash);

    document.addEventListener("DOMContentLoaded", function(event) {
        event.preventDefault();
        getUrlModalID = getModalOpenBehavior();
        if (getUrlModalID) {
            var modalContentUrl = document.getElementById(getUrlModalID).getAttribute("data-content-url");
            fetchData(modalContentUrl, initializeModal);
            document.getElementById("data-modal-content").style.display = "none";

        }

    });
}());
