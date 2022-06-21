/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
(function($) {
    "use strict";

    var selectors = {
        dialogContent: ".cmp-button__editor",
        linkField: 'input[name="./link"]',
        linkURLField: 'foundation-autocomplete[name="./linkURL"]'
    };
    var $linkField;
    var $linkURLField;
    var link;
    var linkURL;

    $(document).on("dialog-loaded", function(event) {
        var $dialog = event.dialog;
        var $dialogContent = $dialog.find(selectors.dialogContent);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            $linkField = $dialogContent.find(selectors.linkField);
            $linkURLField = $dialogContent.find(selectors.linkURLField);
            displayLinkProperty();
        }
    });

    /**
     * Displays the linkURL or link property if available and removes the link property when the dialog is submitted.
     */
    function displayLinkProperty() {
        if ($linkField && $linkURLField && $linkURLField.adaptTo("foundation-field")) {
            link = $linkField.val();
            linkURL = $linkURLField.adaptTo("foundation-field").getValue();
            // if the 'link' property is set and the 'linkURL' is not set: display its value in the 'linkURL' field
            if (!linkURL && link) {
                $linkURLField.adaptTo("foundation-field").setValue(link);
            }
            // remove the 'link' property in the JCR repository
            link = $linkField.val("");
        }
    }

})(jQuery);
