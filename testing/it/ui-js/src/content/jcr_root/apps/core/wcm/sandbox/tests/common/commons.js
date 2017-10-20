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

;(function(h, $) {

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;

    // the path to the policies
    c.policyPath_sandbox = "/conf/core-components/settings/wcm/policies/core-component/components/sandbox";
    // the policy assignment path
    c.policyAssignmentPath_sandbox = "/conf/core-components/settings/wcm/templates/core-components/policies/jcr:content/root/responsivegrid/core-component/components/sandbox";
    // proxy components path
    c.proxyPath_v2 = "/apps/core-component/components/sandbox/"

    // core component resource types
    // text component
    c.rtText_v2 = "core/wcm/sandbox/components/text/v2/text";
    // title component
    c.rtTitle_v2 = "core/wcm/sandbox/components/title/v2/title";
    // list component
    c.rtList_v2 = "core/wcm/sandbox/components/list/v2/list";
    // image component
    c.rtImage_v2 = "core/wcm/sandbox/components/image/v2/image";
    // breadcrumb component
    c.rtBreadcrumb_v2 = "core/wcm/sandbox/components/breadcrumb/v2/breadcrumb";
    // form container
    c.rtFormContainer_v2 = "core/wcm/sandbox/components/form/container/v2/container";
    // form button
    c.rtFormButton_v2 = "core/wcm/sandbox/components/form/button/v2/button";
    // form button
    c.rtFormText_v2 = "core/wcm/sandbox/components/form/text/v2/text";
    // form option
    c.rtFormOptions_v2 = "core/wcm/sandbox/components/form/options/v2/options";
    // form hidden
    c.rtFormHidden_v2 = "core/wcm/sandbox/components/form/hidden/v2/hidden";
    // navigation component
    c.rtNavigation_v1 = "core/wcm/sandbox/components/navigation/v1/navigation";
    // language navigation component
    c.rtLanguageNavigation_v1 = "core/wcm/sandbox/components/languagenavigation/v1/languagenavigation";
    // search component
    c.rtSearch_v1 = "core/wcm/sandbox/components/search/v1/search";
    // teaser component
    c.rtTeaser_v1 = "core/wcm/sandbox/components/teaser/v1/teaser";

}(hobs, jQuery));
