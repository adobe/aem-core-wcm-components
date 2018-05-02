/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

/**
 * Tests for the core text component
 */
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var imageV1 = window.CQ.CoreComponentsIT.Image.v1;
    var imageV2 = window.CQ.CoreComponentsIT.Image.v2;

    /**
     * v2 specifics
     */
    var titleSelector = ".cmp-image__title";
    var tcExecuteBeforeTest = imageV1.tcExecuteBeforeTest(c.rtImage_v2, "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest = imageV1.tcExecuteAfterTest();

    /**
     * The main test suite for Image Component
     */
    new h.TestSuite("Image v2", { path: "/apps/core/wcm/test-suites/Image/v2/Image.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false })

        .addTestCase(imageV2.tcAddImage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(imageV2.tcAddAltTextAndTitle(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(imageV1.tcSetLink(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(imageV2.tcDisableCaptionAsPopup(titleSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(imageV2.tcSetImageAsDecorative(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(c.tcCheckProxiedClientLibrary("/core/wcm/components/image/v2/image/clientlibs/site.js"));

}(hobs, jQuery));
