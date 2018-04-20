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
/*global jQuery*/
(function ($) {
    'use strict';

    window.CQ = window.CQ || {};
    window.CQ.CoreComponents = window.CQ.CoreComponents || {};
    window.CQ.CoreComponents.CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple || {};

    /**
     * Creates a tuple consisting of a checkbox and a text field located in the same dialog.
     *
     * @param {Element} dialog the dialog where the two elements are found
     * @param {String} checkboxSelector the selector for the checkbox
     * @param {String} textfieldSelector the selector for the text field
     * @constructor
     */
    var CheckboxTextfieldTuple = window.CQ.CoreComponents.CheckboxTextfieldTuple.v1 = function (dialog, checkboxSelector, textfieldSelector) {
        var self                  = this;
        self.ATTR_PREVIOUS_VALUE  = 'data-previous-value';
        self.ATTR_SEEDED_VALUE    = 'data-seeded-value';
        self._dialog              = dialog;
        self._checkbox            = dialog.querySelector(checkboxSelector);
        self._checkboxSelector    = checkboxSelector;
        self._checkboxFoundation  = $(self._checkbox).adaptTo('foundation-field');
        self._textfield           = dialog.querySelector(textfieldSelector);
        self._textfieldSelector   = textfieldSelector;
        self._textfieldFoundation = $(self._textfield).adaptTo('foundation-field');
        if (self._checkbox && self._checkboxFoundation) {
            self._checkbox.setAttribute(self.ATTR_PREVIOUS_VALUE, self._checkboxFoundation.getValue());
            self._checkbox.addEventListener('change', function () {
                self.update();
            });
            $(window).adaptTo('foundation-registry').register('foundation.adapters', {
                type    : 'foundation-toggleable',
                selector: self._checkboxSelector,
                adapter : function () {
                    return {
                        isOpen: function () {
                            return !self._checkboxFoundation.isDisabled();
                        },
                        show  : function () {
                            self._checkboxFoundation.setDisabled(false);
                            $(self._checkbox).parent().show();
                            self.update();
                        },
                        hide  : function () {
                            self._checkboxFoundation.setDisabled(true);
                            $(self._checkbox).parent().hide();
                            var previousValue = self._textfield.getAttribute(self.ATTR_PREVIOUS_VALUE);
                            if (fileReference && previousValue !== undefined && previousValue !== null) {
                                self._textfieldFoundation.setValue(previousValue);
                            }
                        }
                    };
                }
            });
        }
        if (self._textfield) {
            self._textfield.setAttribute(self.ATTR_PREVIOUS_VALUE, self._textfield.value);
            $(window).adaptTo('foundation-registry').register('foundation.adapters', {
                type    : 'foundation-toggleable',
                selector: self._textfieldSelector,
                adapter : function () {
                    return {
                        isOpen: function () {
                            return !self._textfieldFoundation.isDisabled();
                        },
                        show  : function () {
                            self._textfieldFoundation.setDisabled(false);
                            $(self._textfield).parent().show();
                        },
                        hide  : function () {
                            self._textfieldFoundation.setDisabled(true);
                            $(self._textfield).parent().hide();
                        }
                    };
                }
            });
        }
    };

    /**
     * Updates the tuple using the following logic:
     *
     * 1. if the checkbox is checked, the value of the text field will be replaced with the value of the
     *     {@link #ATTR_SEEDED_VALUE} data attribute from the text field;
     * 2. if the checkbox is unchecked, the value of the text field will be changed to the value of the {@link #ATTR_PREVIOUS_VALUE} data
     *     attribute on the text field, if this exists
     *
     * The text field will be disabled when the checkbox is checked, or enabled if the checkbox is not checked.
     */
    CheckboxTextfieldTuple.prototype.update = function () {
        if (this._checkboxFoundation && this._textfieldFoundation && this._textfield) {
            if (this._checkboxFoundation.getValue() === 'true') {
                this._textfieldFoundation.setValue(this._textfield.getAttribute(this.ATTR_SEEDED_VALUE));
                this._textfieldFoundation.setDisabled(true);
            } else {
                var previousValue = this._textfield.getAttribute(this.ATTR_PREVIOUS_VALUE);
                if (previousValue !== undefined && previousValue !== null && previousValue.trim() !== "") {
                    this._textfieldFoundation.setValue(previousValue);
                }
                this._textfieldFoundation.setDisabled(false);
            }
        }
    };

    /**
     * Seeds a value in the {@link #ATTR_SEEDED_VALUE} data attribute of the checkbox. If the value is empty then the data attribute is
     * removed.
     *
     * @param {String} [value] the value to seed
     */
    CheckboxTextfieldTuple.prototype.seedTextValue = function (value) {
        if (this._textfield) {
            if (value !== undefined && value !== null) {
                this._textfield.setAttribute(this.ATTR_SEEDED_VALUE, value);
            } else {
                this._textfield.removeAttribute(this.ATTR_SEEDED_VALUE);
            }
        }
    };

    /**
     * Resets the tuple: it un-checks the checkbox, removes the seeded value and sets the text field value to the previously known value.
     *
     * @see {@link CheckboxTextfieldTuple#update}
     * @see {@link CheckboxTextfieldTuple#seedTextValue}
     */
    CheckboxTextfieldTuple.prototype.reset = function () {
        if (this._checkboxFoundation && this._textfield && this._textfieldFoundation) {
            this._checkboxFoundation.setValue(false);
            this._textfield.removeAttribute(this.ATTR_SEEDED_VALUE);
            var previousValue = this._textfield.getAttribute(this.ATTR_PREVIOUS_VALUE);
            if (previousValue !== undefined && previousValue !== null) {
                this._textfieldFoundation.setValue(previousValue);
            }
            this._textfieldFoundation.setDisabled(false);
        }
    };

    /**
     * Sets the checkbox to its initial checked state.
     */
    CheckboxTextfieldTuple.prototype.reinitCheckbox = function () {
        if (this._checkbox && this._checkboxFoundation) {
            var previousValue = this._checkbox.getAttribute(this.ATTR_PREVIOUS_VALUE);
            if (previousValue !== undefined && previousValue !== null) {
                this._checkboxFoundation.setValue(previousValue);
                this.update();
            }
        }
    };

    /**
     * Hides the checkbox field, depending on the <code>hide</code> value.
     * @param {Boolean} [hide] when set to <code>true</code> the checkbox will be hidden
     */
    CheckboxTextfieldTuple.prototype.hideCheckbox = function (hide) {
        var checkbox = $(this._checkboxSelector).adaptTo('foundation-toggleable');
        if (checkbox) {
            if (hide) {
                checkbox.hide();
            } else {
                checkbox.show();
            }
        }
    };

    /**
     * Hides the text field, depending on the <code>hide</code> value.
     * @param {Boolean} [hide] when set to <code>true</code> the text will be hidden
     */
    CheckboxTextfieldTuple.prototype.hideTextfield = function (hide) {
        var textfield = $(this._textfieldSelector).adaptTo('foundation-toggleable');
        if (textfield) {
            if (hide) {
                textfield.hide();
            } else {
                textfield.show();
            }
        }
    };

})(jQuery);
