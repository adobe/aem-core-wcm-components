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
;(function (h, $) {

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var image = window.CQ.CoreComponentsIT.v1.Image;

    /**
     * v1 specifics
     */
    var titleSelector = "span.cmp-image--title";
    var tcExecuteBeforeTest = image.tcExecuteBeforeTest(c.rtImage);
    var tcExecuteAfterTest = image.tcExecuteAfterTest();

    /**
     * The main test suite for Image Component
     */
    new h.TestSuite('Core Components - Image', {path: '/apps/core/wcm/tests/test-suites/core-components-suites/Image.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(image.tcAddImage(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcAddAltText(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetLink(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetCaption(titleSelector, tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetCaptionAsPopup(tcExecuteBeforeTest, tcExecuteAfterTest))
        .addTestCase(image.tcSetImageAsDecorative(tcExecuteBeforeTest, tcExecuteAfterTest))

    ;
}(hobs, jQuery));
