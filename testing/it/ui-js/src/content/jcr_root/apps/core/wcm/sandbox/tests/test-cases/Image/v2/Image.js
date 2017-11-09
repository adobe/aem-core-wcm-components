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
window.CQ.CoreComponentsIT.Image.v2 = window.CQ.CoreComponentsIT.Image.v2 || {};

(function (h, $) {

    'use strict';
    var c              = window.CQ.CoreComponentsIT.commons,
        image          = window.CQ.CoreComponentsIT.Image.v2,
        testImagePath  = '/content/dam/core-components/core-comp-test-image.jpg',
        assetEditPath = '/mnt/overlay/dam/gui/content/assets/metadataeditor.external.html?item=' + testImagePath,
        altText        = 'Return to Arkham',
        captionText    = 'The Last Guardian',
        originalDamTitle       = 'Beach house',
        originalDamDescription = 'House on a beach with blue sky';

    image.tcAltAndTitleFromDAM = function (tcExecuteBeforeTest, tcExecuteAfterTest) {
        var assetEditor,
            initialContext;
        return new h.TestCase('Alt and Caption from DAM', {
            execBefore: tcExecuteBeforeTest,
            execAfter : cleanUp(tcExecuteAfterTest)
        })
            .execTestCase(c.tcOpenConfigureDialog('cmpPath'))
            .execFct(function (opts, done) {
                initialContext = h.context().window;
                c.openSidePanel(done);
            })
            .cui.dragdrop('coral-card.cq-draggable[data-path="' + testImagePath + '"]', 'coral-fileupload[name="./file"')
            .fillInput('input[name="./alt"]', altText)
            .fillInput('input[name="./jcr:title"]', captionText)
            .click('input[type="checkbox"][name="./displayPopupTitle"]')
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function () {
                return h.find('div.cmp-image img[alt="' + altText + '"][title="' + captionText + '"]', '#ContentFrame').size() === 1;
            })
            .execTestCase(c.tcOpenConfigureDialog('cmpPath'))
            .click('input[type="checkbox"][name="./altValueFromDAM"]')
            .click('input[type="checkbox"][name="./titleValueFromDAM"]')
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function () {
                return h.find('div.cmp-image img[alt="' + originalDamDescription + '"][title="' + originalDamTitle + '"]', '#ContentFrame').size() === 1;
            })
            .execTestCase(c.tcOpenConfigureDialog('cmpPath'))
            .execTestCase(
                editAsset(assetEditor, assetEditPath, 'Test title', 'Test description')
            ).execFct(function () {
                h.setContext(initialContext);
            })
            .asserts.isTrue(function () {
                return h.find('input[name="./alt"][data-disabled-value="Test description"]').size() === 1;
            })
            .asserts.isTrue(function () {
                return h.find('input[name="./jcr:title"][data-disabled-value="Test title"]').size() === 1;
            })
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function () {
                return h.find('div.cmp-image img[alt="Test description"][title="Test title"]', '#ContentFrame').size() === 1;
            })
            .execTestCase(c.tcOpenConfigureDialog('cmpPath'))
            .click('input[type="checkbox"][name="./altValueFromDAM"]')
            .click('input[type="checkbox"][name="./titleValueFromDAM"]')
            .execTestCase(c.tcSaveConfigureDialog)
            .asserts.isTrue(function () {
                return h.find('div.cmp-image img[alt="' + altText + '"][title="' + captionText + '"]', '#ContentFrame').size() === 1;
            });
    };

    function editAsset(assetEditor, assetEditPath, title, description) {
        return new h.TestCase('Edit asset properties').openWindow(assetEditPath, null, {success: function (window) {
                assetEditor = window;
                h.setContext(window);
            }})
            .fillInput('[name="./jcr:content/metadata/dc:title"]', title)
            .fillInput('[name="./jcr:content/metadata/dc:description"]', description)
            .click('#shell-propertiespage-doneactivator')
            .execFct(function () {
                assetEditor.close();
            });
    }

    function cleanUp(tcExecuteAfterTest) {
        var initialContext,
            assetEditor;
        return new TestCase('Clean Up', {
            execAfter: tcExecuteAfterTest
        }).execFct(function () {
            initialContext = h.context().window;
        }).execTestCase(editAsset(assetEditor, assetEditPath, originalDamTitle, originalDamDescription))
            .execFct(function () {
                h.setContext(initialContext);
            });
    }

}(hobs, jQuery));
