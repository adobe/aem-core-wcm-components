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

    var newServiceWorker = null;

    function showUpdate() {
        var toastMessage = document.getElementsByClassName("cmp-page__toastmessagehide")[0];
        if (toastMessage) {
            toastMessage.className = "cmp-page__toastmessageshow";
        }
    }

    // The click event on the pop up notification
    var toastMessage = document.getElementsByClassName("cmp-page__toastmessagehide")[0];
    if (toastMessage) {
        toastMessage.addEventListener("click", function() {
            newServiceWorker.postMessage({ action: "skipWaiting" });
        });
        if (window.CQ && window.CQ.I18n) {
            toastMessage.innerText = window.CQ.I18n.getMessage("A new version of this app is available. Click this message to reload.");
        } else {
            toastMessage.innerText = "A new version of this app is available. Click this message to reload.";
        }
    }

    // Check that service workers are supported
    if ("serviceWorker" in navigator) {
        // Use the window load event to keep the page load performant
        window.addEventListener("load", function() {
            var pwaMetaData = document.getElementsByName("cq:sw_path")[0];
            var serviceWorker = pwaMetaData.getAttribute("content");
            navigator.serviceWorker.register(serviceWorker).then(function(registration) {
                registration.addEventListener("updatefound", function() {
                    // An updated service worker is available
                    newServiceWorker = registration.installing;
                    newServiceWorker.addEventListener("statechange", function() {
                        // Has service worker state changed?
                        switch (newServiceWorker.state) {
                            case "installed":
                                // There is a new service worker available, show the notification
                                if (navigator.serviceWorker.controller) {
                                    showUpdate();
                                }
                                break;
                            default: break;
                        }
                    });
                });
            });

            var refreshing;
            navigator.serviceWorker.addEventListener("controllerchange", function() {
                if (refreshing) {
                    return;
                }
                window.location.reload();
                refreshing = true;
            });
        });
    }
}(document));
