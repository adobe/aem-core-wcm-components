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

window.CQ.CoreComponentsIT.Page.v2 = window.CQ.CoreComponentsIT.Page.v2 || {};

/**
 * Tests for the core page component.
 */
(function(h, $) {
    "use strict";

    // shortcut
    var c = window.CQ.CoreComponentsIT.commons;
    var pageV1 = window.CQ.CoreComponentsIT.Page.v1;
    var pageV2 = window.CQ.CoreComponentsIT.Page.v2;

    var configuration = "/conf/core-components";

    /**
     * Test: Check the Advanced Configuration option of a page properties.
     */
    pageV2.tcAdvancedConfigurationPageProperties = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Advanced Configuration page property", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            // open the new page in the sites
            .navigateTo("/sites.html%testPagePath%")

            .execTestCase(pageV1.openPageProperties)

            /* insert information for 'Settings' */

            // open the Advanced tab
            .click("coral-tab-label:contains('Advanced')", { before: 1000 })
            // check if the "Advanced" option was selected
            .assert.isTrue(function() {
                return h.find("coral-tab.is-selected coral-tab-label:contains('Advanced')").size() === 1;
            })

            // test the configuration settings

            // set the configuration
            .fillInput("foundation-autocomplete[name='./cq:conf'] input[is='coral-textfield']", configuration, { after: 2000 })
            .click("button[value='" + configuration + "']", { after: 2000 })

            /*  Check if the configuration is saved */

            // save the configuration and open again the page property
            .click("coral-buttongroup button:contains('Save & Close')", { expectNav: true })
            .execTestCase(pageV1.openPageProperties)
            .click("coral-tab-label:contains('Advanced')", { after: 2000 })

            // check the configuration
            .assert.isTrue(function(opts) {
                return h.find("input[name='./cq:conf'] span:contains('" + configuration + "')");
            });
    };

    /**
     * Test: Check the Blueprint options of a page properties.
     */
    pageV2.tcBlueprintPageProperties = function(tcExecuteBeforeTest, tcExecuteAfterTest) {
        return new h.TestCase("Blueprint for a page", {
            execBefore: tcExecuteBeforeTest,
            execAfter: tcExecuteAfterTest
        })

            // create the live copy page, store page path in 'testLiveCopyPagePath'
            .execFct(function(opts, done) {
                c.createLiveCopy(h.param("testPagePath")(opts), c.rootPage, "page_" + Date.now(), "page_" + Date.now(), "testLiveCopyPagePath", done);
            })

            // open the new page in the sites
            .navigateTo("/sites.html%testPagePath%")

            .execTestCase(pageV1.openPageProperties)

            .click("coral-tab-label:contains('Blueprint')", { delay: 1000 })
            // check if the "Blueprint" option was selected
            .assert.isTrue(function() {
                return h.find("coral-tab.is-selected coral-tab-label:contains('Blueprint')").size() === 1;
            })

            .click("coral-anchorbutton-label:contains('Rollout')", { before: 2000, expectNav: true })
            // check if the page is selected
            .assert.isTrue(function() {
                return h.find("coral-checkbox.select-rollout[checked]").size() === 2;
            })
            // check the Rollout page and all sub pages
            .click("coral-checkbox.coral-Form-field")
            // save the configuration
            .ifElse(function() {
                return h.find("coral-dialog#aem-sites-rollout-schedule-dialog").size() > 0;
            }, new hobs.TestCase("Close schedule rollout modal and submit")
                .click(".cq-dialog-actions .cq-dialog-submit")
                .click("button.schedule-rollout-done", { expectedNav: true }),
            new hobs.TestCase("Submit")
                .click(".cq-dialog-actions .cq-dialog-submit", { expectNav: true })
            )
            // delete the test page we created for the live copy
            .execFct(function(opts, done) {
                c.deletePage(h.param("testLiveCopyPagePath")(opts), done);
            });
    };

}(hobs, jQuery));
