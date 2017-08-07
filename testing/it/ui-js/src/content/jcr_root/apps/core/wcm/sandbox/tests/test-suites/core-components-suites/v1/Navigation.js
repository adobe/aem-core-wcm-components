/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
/* globals hobs,jQuery */
;(function (h, $) {

    'use strict';
    var c                                    = window.CQ.CoreComponentsIT.commons,
        navigation                           = window.CQ.CoreComponentsIT.v1.Navigation;

    var tcExecuteBeforeTest = navigation.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtNavigation_v1,
        'core/wcm/sandbox/tests/components/test-page-v2');
    var tcExecuteAfterTest  = navigation.tcExecuteAfterTest(c.tcExecuteAfterTest);

    new h.TestSuite('Core Components - Navigation (v1 sandbox)', {
        path           : '/apps/core/wcm/sandbox/tests/core-components-it/v1/Navigation.js',
        execBefore     : c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(navigation.testDefaultConfiguration(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(navigation.testChangeMaxDepthLevel(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(navigation.testMaxDepthAndStartLevel(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
