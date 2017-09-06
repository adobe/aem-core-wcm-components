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
    var c      = window.CQ.CoreComponentsIT.commons,
        teaser = window.CQ.CoreComponentsIT.Teaser.v1,
        selectors = {
            component: {
                self: '.cmp-teaser',
                image: '.cmp-teaser__image',
                title: '.cmp-teaser__title',
                description: '.cmp-teaser__description',
                link: '.cmp-teaser__link'
            },
            editDialog: {
                assetDrag: function (imagePath) {
                    return 'coral-card.cq-draggable[data-path="' + imagePath + '"]';
                },
                assetDrop: 'coral-fileupload[name="./file"',
                linkURL: 'foundation-autocomplete[name="./linkURL"]',
                linkText: 'input[name="./linkText"]',
                title: 'input[name="./jcr:title"]',
                description: 'input[name="./jcr:description"]'
            }
        };

    var tcExecuteBeforeTest = teaser.tcExecuteBeforeTest(c.tcExecuteBeforeTest, c.rtTeaser_v1,
        'core/wcm/sandbox/tests/components/test-page-v2');
    var tcExecuteAfterTest  = teaser.tcExecuteAfterTest(c.tcExecuteAfterTest);

    new h.TestSuite('Teaser v1', {
        path           : '/apps/core/wcm/sandbox/tests/core-components-it/v1/Teaser.js',
        execBefore     : c.tcExecuteBeforeTestSuite,
        execInNewWindow: false
    }).addTestCase(teaser.testFullyConfiguredTeaser(tcExecuteBeforeTest, tcExecuteAfterTest, selectors));

}(hobs, jQuery));
