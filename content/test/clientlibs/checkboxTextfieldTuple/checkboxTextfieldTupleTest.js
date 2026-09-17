/*******************************************************************************
 * Copyright 2026 Adobe
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
function createFieldAdapter(element) {
    return {
        setDisabled: function(disabled) {
            element.disabled = disabled;
        }
    };
}

describe("CheckboxTextfieldTuple readonly submit exclusion", function() {
    let CheckboxTextfieldTuple;

    beforeAll(function() {
        fixture.setBase("test/fixtures/checkboxTextfieldTuple");
        CheckboxTextfieldTuple = globalThis.__CHECKBOX_TEXTFIELD_TUPLE_TEST_API.CheckboxTextfieldTuple;
    });

    beforeEach(function() {
        fixture.load("checkboxTextfieldTupleTest.html");
    });

    afterEach(function() {
        fixture.cleanup();
    });

    function createTupleState(inherited) {
        const textfield = fixture.el.querySelector('input[name="./jcr:title"]');
        const tuple = Object.create(CheckboxTextfieldTuple.prototype);
        tuple._useReadOnlyWhenDisabled = true;
        tuple._isRichText = false;
        tuple._checkboxFoundation = {
            getValue: function() {
                return inherited ? "true" : "false";
            }
        };
        tuple._textfield = textfield;
        tuple._textfieldFoundation = createFieldAdapter(textfield);
        return tuple;
    }

    it("keeps inherited title readonly and focusable", function() {
        const textfield = fixture.el.querySelector('input[name="./jcr:title"]');
        const tuple = Object.create(CheckboxTextfieldTuple.prototype);
        tuple._isRichText = false;
        tuple._useReadOnlyWhenDisabled = true;
        tuple._textfield = textfield;
        tuple._textfieldFoundation = createFieldAdapter(textfield);

        tuple._disableTextfield(true);

        expect(textfield.readOnly).toBe(true);
        expect(textfield.disabled).toBe(false);
        expect(textfield.getAttribute("aria-readonly")).toBe("true");
    });

    it("excludes readonly inherited title from submit by temporarily disabling the field", function() {
        const tuple = createTupleState(true);
        tuple._textfield.readOnly = true;
        spyOn(tuple._textfieldFoundation, "setDisabled").and.callThrough();

        tuple._excludeReadOnlyTextfieldFromSubmit();

        expect(tuple._textfieldFoundation.setDisabled).toHaveBeenCalledWith(true);
        expect(tuple._textfield.readOnly).toBe(true);
    });

    it("does not exclude editable title values from submit", function() {
        const tuple = createTupleState(false);
        tuple._textfield.readOnly = false;
        spyOn(tuple._textfieldFoundation, "setDisabled");

        tuple._excludeReadOnlyTextfieldFromSubmit();

        expect(tuple._textfieldFoundation.setDisabled).not.toHaveBeenCalled();
    });
});
