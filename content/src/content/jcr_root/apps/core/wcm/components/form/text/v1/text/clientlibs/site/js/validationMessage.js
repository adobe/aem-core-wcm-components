/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
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
(function () {
    'use strict';

    function documentReady(fn) {
        if (document.readyState != 'loading'){
            fn();
        } else {
            document.addEventListener('DOMContentLoaded', fn);
        }
    }

    var INPUT_FIELD = '.cmp-form-field input',
        REQUIRED_MSG_ATTRIBUTE = 'data-cmp-required',
        CONSTRAINT_MSG_ATTRIBUTE = 'data-cmp-constraint';

    documentReady(function () {
        var inputFields = document.querySelectorAll(INPUT_FIELD), inputField, index;
        for (index = 0; index < inputFields.length; index++) {
            inputField = inputFields[index];
            inputField.addEventListener('invalid', function (e) {
                e.target.setCustomValidity("");
                if (e.target.validity.typeMismatch) {
                    if (inputField.hasAttribute(CONSTRAINT_MSG_ATTRIBUTE)) {
                        e.target.setCustomValidity(inputField.getAttribute(CONSTRAINT_MSG_ATTRIBUTE));
                    }
                } else if (e.target.validity.valueMissing) {
                    if (inputField.hasAttribute(REQUIRED_MSG_ATTRIBUTE)) {
                        e.target.setCustomValidity(inputField.getAttribute(REQUIRED_MSG_ATTRIBUTE));
                    }
                }
            });
            inputField.addEventListener('input', function (e) {
                e.target.setCustomValidity("");
            });
        }
    });

})();
