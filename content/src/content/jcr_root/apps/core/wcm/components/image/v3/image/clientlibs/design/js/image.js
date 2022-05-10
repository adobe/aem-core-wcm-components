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

    var enableAssetDeliveryServiceCheckbox = "coral-checkbox[name='./enableAssetDeliveryService']";
    var enableModernImageFormatsCheckbox = "coral-checkbox[name='./enableModernImageFormats']";

    $(document).on("dialog-loaded", function(e) {
        $(enableAssetDeliveryServiceCheckbox).on("change", function() {
            toggleMIFCheckbox();
        });

        toggleMIFCheckbox();
    });

    function toggleMIFCheckbox() {
        var enableModernImageFormatsCheckboxField = $(enableModernImageFormatsCheckbox).adaptTo("foundation-field");
        if ($(enableAssetDeliveryServiceCheckbox)[0].checked) {
            enableModernImageFormatsCheckboxField.setDisabled(false);
        } else {
            $(enableModernImageFormatsCheckbox).attr("checked", false);
            enableModernImageFormatsCheckboxField.setValue(false);
            enableModernImageFormatsCheckboxField.setDisabled(true);
        }
    }

})(jQuery);
