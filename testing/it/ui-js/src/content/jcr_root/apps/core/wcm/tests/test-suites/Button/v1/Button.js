/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
;(function(h, $) { // eslint-disable-line no-extra-semi
    "use strict";

    var c = window.CQ.CoreComponentsIT.commons;
    var button = window.CQ.CoreComponentsIT.Button.v1;
    var selectors = {
        editDialog: {
            text: "[name='./jcr:title']",
            link: "[name='./link']",
            icon: "[name='./icon']"
        },
        button: {
            self: ".cmp-button",
            text: ".cmp-button__text",
            icon: ".cmp-button__icon"
        }
    };

    var tcExecuteBeforeTest = button.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtButton_v1,
        "core/wcm/tests/components/test-page-v2");
    var tcExecuteAfterTest  = button.tcExecuteAfterTest(c.tcExecuteAfterTest, c.policyPath, c.policyAssignmentPath);

    new h.TestSuite("Button v1", {
        path: "/apps/core/wcm/tests/core-components-it/Button/v1/Button.js",
        execBefore: c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    })
        .addTestCase(button.tcSetText(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(button.tcSetLink(tcExecuteBeforeTest, tcExecuteAfterTest, selectors))
        .addTestCase(button.tcSetIcon(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));
}(hobs, jQuery));
