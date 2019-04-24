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

    function getModalOpenBehavior() {
        var hashVal = window.location.hash;
        var getUrlModal = hashVal.substr(1).split("?")[0];
        var isModalOn = document.querySelectorAll("div[data-modal-show='true']");
        if ((getUrlModal !== "" && isModalOn.length === 0) || (getUrlModal !== "" && isModalOn.length >= 0)) {
            return getUrlModal;
        } else if ((getUrlModal === "" && isModalOn.length > 0)) {
            return isModalOn[0].id;
        }
    }

    function fetchData(url) {
        var xhttp;
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                initializeModal(this.responseText);
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();
    }

    function initializeModal(html) {
        var modalOpen = new window.ModalLib({
            onClose: function() {
                window.location.hash = "";
            }
        });
        modalOpen.open();
        modalOpen.setContent(html);
    }

    function openModalBasedOnHash(event) {
        event.preventDefault();
        var getUrlModalID;

        if (window.location.hash !== "") {
            getUrlModalID = getModalOpenBehavior();
            if (getUrlModalID) {
                var modalContentUrl = document.getElementById(getUrlModalID).getAttribute("data-content-url");
                fetchData(modalContentUrl, initializeModal);
            }
        }

    }
    window.addEventListener("hashchange", openModalBasedOnHash);

    document.addEventListener("DOMContentLoaded", function() {
        var getUrlModalID = getModalOpenBehavior();
        if (getUrlModalID) {
            var modalContentUrl = document.getElementById(getUrlModalID).getAttribute("data-content-url");
            fetchData(modalContentUrl);
        }

    });

}());
