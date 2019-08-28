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
(function($) {
    "use strict";

    var BUTTON_NAME = ".cmp-form-button__editor-name";
    var BUTTON_VALUE = ".cmp-form-button__editor-value";
    var PROP_ERROR_MESSAGE = "error-message";

    $.validator.register({
        selector: BUTTON_NAME,
        validate: function(el) {
            var valueInput = el.closest("form").find(BUTTON_VALUE);
            if (valueInput.val() !== "") {
                if (el.val() === "") {
                    return el.data(PROP_ERROR_MESSAGE);
                }
            }
        }
    });

})(jQuery);
