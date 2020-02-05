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

    function sendPageUpdateToLaunch(event, oldState, newState) {
        console.log("Data sent to launch: data recorded for page " + Object.values(newState.component.page)[0].id);
    }

    function sendImageClickToLaunch(event, oldState, newState) {
        console.log("Data sent to launch: click recorded for image " + event.info.path);
    }

    dataLayer.push({
        on: 'datalayer:change',
        handler: sendPageUpdateToLaunch
    });

    dataLayer.push({
        on: 'image clicked',
        handler: sendImageClickToLaunch
    });

}());
