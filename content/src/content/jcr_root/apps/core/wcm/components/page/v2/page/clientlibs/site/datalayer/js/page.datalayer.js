/*******************************************************************************
 * Copyright 2020 Adobe
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

    var dataLayer = window.dataLayer = window.dataLayer || [];

    // Add the page data to the data layer
    var dataLayerJson = document.getElementsByTagName("BODY")[0].getAttribute("data-cmp-page-data-layer");
    var pageData = JSON.parse(dataLayerJson);
    dataLayer.push({
        data: {
            page: pageData
        }
    });

})();
