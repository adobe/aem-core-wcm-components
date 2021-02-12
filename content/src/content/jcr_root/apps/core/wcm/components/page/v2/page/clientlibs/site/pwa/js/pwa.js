/*
 *  Copyright 2021 Adobe
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

(function(document) {
    "use strict";

    // Check that service workers are supported
    if (!("serviceWorker" in navigator)) {
        return;
    }

    var newServiceWorker = null;
    var SW_PATH = "cq:sw_path";
    var toastMessage = document.getElementsByClassName("cmp-page__toastmessagehide")[0];
    var pwaMetaData = document.getElementsByName(SW_PATH)[0];
    if (!pwaMetaData) {
        return;
    }
    var serviceWorker = pwaMetaData.getAttribute("content");
    var refreshing = false;

    function showUpdate() {
        if (!toastMessage) {
            return;
        }

        // The click event on the pop up notification
        toastMessage.addEventListener("click", function() {
            newServiceWorker.postMessage({ action: "skipWaiting" });
        });

        toastMessage.innerText = "A new version of this app is available. Click this message to reload.";
        if (window.CQ && window.CQ.I18n) {
            toastMessage.innerText = window.CQ.I18n.getMessage("A new version of this app is available. Click this message to reload.");
        }
        toastMessage.className = "cmp-page__toastmessageshow";
    }

    function onLoad() {
        navigator.serviceWorker.register(serviceWorker).then(function(registration) {
            registration.addEventListener("updatefound", function() {
                // An updated service worker is available
                newServiceWorker = registration.installing;
                newServiceWorker.addEventListener("statechange", function() {
                    // Has service worker state changed?
                    if ((newServiceWorker.state === "installed") && navigator.serviceWorker.controller) {
                        showUpdate(); // There is a new service worker available, show the notification
                    }
                });
            });
        });

        navigator.serviceWorker.addEventListener("controllerchange", onControllerChange);
    }

    function onControllerChange() {
        if (refreshing) {
            return;
        }
        window.location.reload();
        refreshing = true;
    }

    // Use the window load event to keep the page load performant
    window.addEventListener("load", onLoad);
}(document));
