/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

(function($) {
    "use strict";
    var foundationReg = $(window).adaptTo("foundation-registry");
    foundationReg.register("foundation.validation.validator", {
        selector: 'input[data-foundation-validation="dialog-id-field-validation"]',
        validate: function(el) {
            var regexPattern = /^[a-zA-Z0-9&,.()'!/?:;-]+$/;
            var errorMessage = "Only contain letters, numbers and special characters [&,.()'!/?:;-]!";
            if (el.value !== "") {
                var result = el.value.match(regexPattern);
                if (result === null) {
                    return errorMessage;
                }
            }
            
        }
    });
}(jQuery));
