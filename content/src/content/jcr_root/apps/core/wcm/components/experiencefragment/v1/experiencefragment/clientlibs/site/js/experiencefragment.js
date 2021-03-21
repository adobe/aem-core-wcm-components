/*******************************************************************************
 * Copyright 2021 Adobe
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

    $(".cmp-experiencefragment").each(function () {
        var ADOBE_TARGET = adobe.target;
        var $remoteOfferEnabled = $(this).data("enable-remote-offer");
        var $remoteOfferId = $(this).data("remote-offer-id");

        if ($remoteOfferEnabled !== undefined && $remoteOfferEnabled && $remoteOfferId
            && typeof ADOBE_TARGET !== "undefined" && ADOBE_TARGET) {
            ADOBE_TARGET.getOffer({
                mbox: $remoteOfferId,
                success: function (offer) {
                    ADOBE_TARGET.applyOffer({
                        mbox: $remoteOfferId,
                        selector: "#" + $remoteOfferId,
                        offer: offer
                    });
                },
                error: function (error) {
                    var el = document.getElementById($remoteOfferId);
                    el.style.visibility = "visible";
                }
            });
        }
    });

})();
