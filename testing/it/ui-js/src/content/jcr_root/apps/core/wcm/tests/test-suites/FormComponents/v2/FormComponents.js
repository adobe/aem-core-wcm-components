/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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

;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formComponentsV1 = window.CQ.CoreComponentsIT.FormComponents.v1;

    /**
     * v2 specifics
     */
    var tcExecuteBeforeTest = formComponentsV1.tcExecuteBeforeTest(c.rtFormContainer_v2, c.rtFormText_v2, c.rtFormHidden_v2, c.rtFormOptions_v2, c.rtFormButton_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = formComponentsV1.tcExecuteAfterTest();

    /**
     * The main test suite.
     */
    new h.TestSuite("Form Components v2", { path: "/apps/core/wcm/test-suites/FormComponents/v2/FormComponents.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(formComponentsV1.storeContent(tcExecuteBeforeTest, tcExecuteAfterTest));

}(hobs, jQuery));
