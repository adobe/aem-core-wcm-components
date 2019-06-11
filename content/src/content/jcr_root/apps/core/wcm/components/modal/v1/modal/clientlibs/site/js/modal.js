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

var popupModal = tingle;
(function(popupModal, window) {
    "use strict";
    var config = {
        cssClass: ["custom-modal"],
        onClose: function() {
            window.location.hash = "";
        }
    };

    window.modelLib.setLibrary(popupModal.modal);

    function getDataContentUrl() {
        var getUrlModalId = getModalOpenBehavior();
        if (getUrlModalId) {
            var modalIdEl = document.getElementById(getUrlModalId);
            if (modalIdEl) {
                return modalIdEl.getAttribute("data-content-url");
            }
        }
    }

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

    function openModalBasedOnHash(event) {
        event.preventDefault();

        if (window.location.hash !== "" && window.location.hash !== "#") {
            var modalContentUrl = getDataContentUrl();
            if (modalContentUrl) {
                fetchData(modalContentUrl);
            }
        }
    }

    function fetchData(url) {
        var xhttp;
        xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                window.modelLib.initializeModel(config, this.responseText);
            }
        };
        xhttp.open("GET", url, true);
        xhttp.send();
    }

    window.addEventListener("hashchange", openModalBasedOnHash);

    document.addEventListener("DOMContentLoaded", function() {
        var modalContentUrl = getDataContentUrl();
        if (modalContentUrl) {
            fetchData(modalContentUrl);
        }
    });

}(popupModal, window));
