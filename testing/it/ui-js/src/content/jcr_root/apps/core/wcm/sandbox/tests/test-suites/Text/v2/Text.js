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
    var textV1 = window.CQ.CoreComponentsIT.Text.v1;
    var selectors = {
        editor: '.text.aem-GridColumn p',
        rendered: '.cmp-text > p'
    };

    /**
     * v1 specifics
     */
    var tcExecuteBeforeTest = textV1.tcExecuteBeforeTest(c.rtText_v2, "core/wcm/sandbox/tests/components/test-page-v2")
    var tcExecuteAfterTest = textV1.tcExecuteAfterTest();

    /**
     * The main test suite for Text Component
     */
    new h.TestSuite('Text v2', {path: '/apps/core/wcm/sandbox/test-suites/core-components-it/v2/Text.js',
        execBefore:c.tcExecuteBeforeTestSuite,
        execInNewWindow : false})

        .addTestCase(textV1.tcSetTextValueUsingInlineEditor(selectors, tcExecuteBeforeTest, tcExecuteAfterTest));
}(hobs, jQuery));
