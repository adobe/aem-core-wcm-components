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

;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    // the path to the policies
    c.policyPath_sandbox = "/conf/core-components/settings/wcm/policies/core-component/components/sandbox";
    // the policy assignment path
    c.policyAssignmentPath_sandbox = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content/root/responsivegrid/core-component/components/sandbox";
    // proxy components path
    c.proxyPath_sandbox = "/apps/core-component/components/sandbox/";

    // core component resource types
    // carousel component
    c.rtCarousel_v1 = "core/wcm/sandbox/components/carousel/v1/carousel";
    // tabs component
    c.rtTabs_v1 = "core/wcm/sandbox/components/tabs/v1/tabs";


}(hobs, jQuery));
