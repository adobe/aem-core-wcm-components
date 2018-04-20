/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
/*global jQuery*/
(function ($) {
    'use strict';

    var dialogContentSelector = '.cmp-teaser__editor',
        titleCheckboxSelector = 'coral-checkbox[name="./titleValueFromPage"]',
        titleTextfieldSelector = 'input[name="./jcr:title"]',
        descriptionCheckboxSelector = 'coral-checkbox[name="./descriptionValueFromPage"]',
        descriptionTextfieldSelector = 'input[name="./jcr:description"]',
        linkURLSelector = '[name="./linkURL"]',
        CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1,
        titleTuple,
        descriptionTuple,
        linkURL = undefined;

    $(document).on('dialog-loaded', function (e) {
        var $dialog        = e.dialog,
            $dialogContent = $dialog.find(dialogContentSelector),
            dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;

        if (dialogContent) {
            titleTuple = new CheckboxTextfieldTuple(dialogContent, titleCheckboxSelector, titleTextfieldSelector);
            descriptionTuple = new CheckboxTextfieldTuple(dialogContent, descriptionCheckboxSelector, descriptionTextfieldSelector);

            var $linkURLField = $dialogContent.find(linkURLSelector);
            linkURL = $linkURLField.adaptTo('foundation-field').getValue();
            $linkURLField.on("change", function() {
                linkURL = $linkURLField.adaptTo('foundation-field').getValue();
                retrievePageInfo();
            });

            retrievePageInfo();
        }
    });

    function retrievePageInfo() {
        if (linkURL) {
            return $.ajax({
                url: linkURL + '/_jcr_content.json'
            }).done(function (data) {
                if (data) {
                    titleTuple.seedTextValue(data['jcr:title']);
                    titleTuple.update();
                    descriptionTuple.seedTextValue(data['jcr:description']);
                    descriptionTuple.update();
                }
            });
        } else {
            titleTuple.update();
            descriptionTuple.update();
        }
    }
})(jQuery);