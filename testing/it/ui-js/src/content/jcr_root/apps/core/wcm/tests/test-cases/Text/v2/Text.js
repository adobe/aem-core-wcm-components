/*******************************************************************************
 * Copyright 2016 Adobe
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

window.CQ.CoreComponentsIT.Text.v2 = window.CQ.CoreComponentsIT.Text.v2 || {}

/**
 * Tests for the core text component
 */
;(function(h, $) {
    "use strict";

    // shortcuts
    var c = window.CQ.CoreComponentsIT.commons;
    var text = window.CQ.CoreComponentsIT.Text.v2;

    var testXSS = 'Hello World! <img ="/" onerror="alert(String.fromCharCode(88,83,83))"></img>';
    var textXSSProtectedHTL = "Hello World! <img>";
    var textXSSProtectedRTE = "Hello World! <img />";

    hobs.config.pacing_delay = 250;

    /**
     * Test: Check if text is XSS protected
     */
    text.tcCheckTextWithXSSProtection = function(selectors, tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Check text with XSS protection", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            // open dialog
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            // set text
            .fillInput("[name='./text']", testXSS)
            // save dialog
            .execTestCase(c.tcSaveConfigureDialog)

            // switch to the content frame
            .config.changeContext(c.getContentFrame)
            // check if the text is rendered with XSS protection
            .assert.isTrue(
                function() {
                    var actualValue = h.find(selectors.editorConf).html();
                    return actualValue.trim() === textXSSProtectedHTL;
                })
            // switch back to edit frame
            .config.resetContext()

            // check if the text is rendered with XSS protection
            .assert.isTrue(
                function() {
                    jQuery.ajax({
                        url: h.param("cmpPath")() + ".json",
                        method: "GET"
                    })
                        .done(function(data) {
                            h.param("textJson", data.text);
                        });
                    if (h.param("textJson")() === textXSSProtectedRTE) {
                        return true;
                    } else {
                        return false;
                    }
                });
    };

    hobs.config.pacing_delay = 0;

}(hobs, jQuery));
