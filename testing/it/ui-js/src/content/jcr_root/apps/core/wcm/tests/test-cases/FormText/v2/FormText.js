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

window.CQ.CoreComponentsIT.FormText.v2 = window.CQ.CoreComponentsIT.FormText.v2 || {}

/**
 * Tests for the core form text component
 */
;(function(h, $) {
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var formTextV1 = window.CQ.CoreComponentsIT.FormText.v1;
    var formTextV2 = window.CQ.CoreComponentsIT.FormText.v2;

    // samples
    var elementName = "Luigi";
    var requiredMessage = "Attack ships on fire off the shoulder of Orion";
    var constraintMessage = requiredMessage;

    /**
     * Test : required
     *
     * 1. Open the edit dialog
     * 2. Configure as required and add a required message, save
     * 3. Verify the text input is set to required
     * 4. Switch the same component to type textarea, save
     * 5. Verify the textarea is set to required
     * 6. Verify the required message is set
     */
    formTextV2.setRequired = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Required", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // 1
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2
            .execTestCase(formTextV1.setMandatoryFields)
            .execTestCase(c.tcSwitchConfigTab(("Constraints")))
            .click("input[type='checkbox'][name='./required'")
            .fillInput("textarea[name='./requiredMessage']", requiredMessage)
            .execTestCase(c.tcSaveConfigureDialog)

            // 3
            .asserts.isTrue(function() {
                return h.find("input[type='text'][name='" + elementName + "'][required]", "#ContentFrame").size() === 1;
            })

            // 4
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))
            .execTestCase(formTextV1.setInputType("textarea"))
            .execTestCase(c.tcSaveConfigureDialog)

            // 5
            .asserts.isTrue(function() {
                return h.find("textarea[name='" + elementName + "'][required]", "#ContentFrame").size() === 1;
            })

            // 6
            .asserts.isTrue(function() {
                return h.find(".cmp-form-text[data-cmp-required-message='" + requiredMessage + "']", "#ContentFrame").size() === 1;
            });
    };

    /**
     * Test : constraint message
     *
     * 1. Open the edit dialog
     * 2. Configure as type email and add a constraint message, save
     * 3. Verify the constraint message is set
     */
    formTextV2.setConstraintMessage = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Set Constraint Message", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest })

            // 1
            .execTestCase(c.tcOpenConfigureDialog("cmpPath"))

            // 2
            .execTestCase(formTextV1.setMandatoryFields)
            .execTestCase(formTextV1.setInputType("email"))
            .execTestCase(c.tcSwitchConfigTab(("Constraints")))
            .fillInput("textarea[name='./constraintMessage']", constraintMessage)
            .execTestCase(c.tcSaveConfigureDialog)

            // 3
            .asserts.isTrue(function() {
                return h.find(".cmp-form-text[data-cmp-constraint-message='" + constraintMessage + "']",
                    "#ContentFrame").size() === 1;
            });
    };

}(hobs, jQuery));
