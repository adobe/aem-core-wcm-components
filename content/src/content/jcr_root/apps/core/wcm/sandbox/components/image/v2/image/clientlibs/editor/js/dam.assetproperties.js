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
/*global jQuery, Coral*/
(function ($) {
    'use strict';

    var dialog,
        altTuple,
        captionTuple,
        cqFileUpload,
        cqFileUploadEdit;

    $(document).on('dialog-loaded', function (e) {
        dialog           = e.dialog[0];
        altTuple         = new CheckboxTextfieldTuple(dialog, 'coral-checkbox[name="./altValueFromDAM"]', 'input[name="./alt"]');
        captionTuple     =
            new CheckboxTextfieldTuple(dialog, 'coral-checkbox[name="./titleValueFromDAM"]', 'input[name="./jcr:title"]');
        cqFileUpload     = dialog.querySelector('.cq-FileUpload');
        cqFileUploadEdit = dialog.querySelector('.cq-FileUpload-edit');
        if (cqFileUpload) {
            $(cqFileUpload).on('assetselected', function (e) {
                retrieveDAMInfo(e.path);
            });
            $(cqFileUpload).on('click', '[coral-fileupload-clear]', function () {
                altTuple.reset();
                captionTuple.reset();
            });
            $(cqFileUpload).on('coral-fileupload:fileadded', function () {
                altTuple.reset();
                captionTuple.reset();
            });
        }
        if (cqFileUploadEdit) {
            var fileReference = cqFileUploadEdit.getAttribute('data-cq-fileupload-filereference');
            if (fileReference) {
                retrieveDAMInfo(fileReference);
                $(window).on('focus', function () {
                    retrieveDAMInfo(fileReference);
                });
            }
        }
    });

    $(document).on('dialog-beforeclose', function () {
        $(window).off('focus');
    });

    function retrieveDAMInfo(fileReference) {
        return $.ajax({
            url: fileReference + '/_jcr_content/metadata.json'
        }).done(function (data) {
            if (data) {
                if (altTuple) {
                    var description = data['dc:description'];
                    if(description === undefined || description.trim() === '') {
                        description = data['dc:title'];
                    }
                    altTuple.seedTextValue(description);
                    altTuple.update();
                }
                if (captionTuple) {
                    var title = data['dc:title'];
                    captionTuple.seedTextValue(title);
                    captionTuple.update();
                }
            }
        });
    }

    /**
     * Creates a tuple consisting of a checkbox and a text field located in the same dialog.
     *
     * @param {Element} dialog the dialog where the two elements are found
     * @param {String} checkboxSelector the selector for the checkbox
     * @param {String} textfieldSelector the selector for the text field
     * @constructor
     */
    var CheckboxTextfieldTuple = function (dialog, checkboxSelector, textfieldSelector) {
        var self                 = this;
        self.ATTR_PREVIOUS_VALUE = 'data-previous-value';
        self.ATTR_DISABLED_VALUE = 'data-disabled-value';
        self._dialog             = dialog;
        self._checkboxSelector   = checkboxSelector;
        self._textfieldSelector  = textfieldSelector;
        self._checkbox           = dialog.querySelector(checkboxSelector);
        self._textfield          = dialog.querySelector(textfieldSelector);
        if (self._checkbox) {
            Coral.commons.ready(self._checkbox, function (field) {
                self._checkbox = field;
                self._checkbox.on('change', function () {
                    self.update();
                });
            });
        }
        if (self._textfield) {
            Coral.commons.ready(self._textfield, function (field) {
                self._textfield = field;
                self._textfield.setAttribute(self.ATTR_PREVIOUS_VALUE, self._textfield.value);
            });
        }
    };

    /**
     * Updates the tuple using the following logic:
     *
     * 1. if the checkbox is checked, the value of the text field will be replaced with the value of the
     *     {@link #ATTR_DISABLED_VALUE} data attribute from the text field;
     * 2. if the checkbox is unchecked, the value of the text field will be changed to the value of the {@link #ATTR_PREVIOUS_VALUE} data
     *     attribute on the text field, if this exists
     *
     * The text field will be disabled when the checkbox is checked, or enabled if the checkbox is not checked.
     */
    CheckboxTextfieldTuple.prototype.update = function () {
        if (this._checkbox.checked) {
            this._textfield.value = this._textfield.getAttribute(this.ATTR_DISABLED_VALUE);
        } else {
            var previousValue = this._textfield.getAttribute(this.ATTR_PREVIOUS_VALUE);
            if (previousValue) {
                this._textfield.value = previousValue;
            }
        }
        this._textfield.disabled = this._checkbox.checked;
    };

    /**
     * Seeds a value in the {@link #ATTR_DISABLED_VALUE} data attribute of the checkbox. If the value is empty then the data attribute is
     * removed.
     *
     * @param {String} [value] the value to seed
     */
    CheckboxTextfieldTuple.prototype.seedTextValue = function (value) {
        if (value) {
            this._textfield.setAttribute(this.ATTR_DISABLED_VALUE, value);
        } else {
            this._textfield.removeAttribute(this.ATTR_DISABLED_VALUE);
        }
    };

    /**
     * Resets the tuple: it un-checks the checkbox, removes the seeded value and sets the text field value to the previously known value.
     *
     * @see {@link CheckboxTextfieldTuple#update}
     * @see {@link CheckboxTextfieldTuple#seedTextValue}
     */
    CheckboxTextfieldTuple.prototype.reset = function () {
        this._checkbox.checked = false;
        this._textfield.removeAttribute(this.ATTR_DISABLED_VALUE);
        var previousValue = this._textfield.getAttribute(this.ATTR_PREVIOUS_VALUE);
        if (previousValue || previousValue === '') {
            this._textfield.value = previousValue;
        }
        this._textfield.disabled = this._checkbox.checked;
    }
})(jQuery);
