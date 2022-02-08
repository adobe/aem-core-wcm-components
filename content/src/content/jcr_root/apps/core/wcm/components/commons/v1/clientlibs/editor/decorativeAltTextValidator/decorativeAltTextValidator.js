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
(function(window, $) {
    "use strict";

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.DecorativeAltTextValidator = window.CQ.CoreComponents.DecorativeAltTextValidator || {};

    /**
     * Creates a validator for an alt text which is required if decorative checkbox is not checked
     *
     * @param {String} decorativeCheckboxSelector the selector for the decorative checkbox in the dialog
     * @param {String} altTextFieldSelector the selector for the alt text in the dialog
     */
    window.CQ.CoreComponents.DecorativeAltTextValidator.v1 = function(decorativeCheckboxSelector, altTextFieldSelector) {

        let self = this;
        self._decorativeCheckboxSelector = decorativeCheckboxSelector;
        self._altTextFieldSelector = altTextFieldSelector;

        $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
            selector: self._altTextFieldSelector,
            validate: function(el) {
                var $el = $(el);
                var decorative = $el.closest("form").find(self._decorativeCheckboxSelector).adaptTo("foundation-field");
                if (decorative) {
                    if (el.value.length === 0 && !decorative.checked) {
                        return Granite.I18n.get("Error: Please fill out this field.");
                    }
                }
            }
        });
        $(document).on("change", self._decorativeCheckboxSelector, function(e) {
            var $altText = $(self._altTextFieldSelector);
            var validation = $altText.adaptTo("foundation-validation");
            if (validation) {
                validation.checkValidity();
                validation.updateUI();
            }
        });
    };

})(window, jQuery);