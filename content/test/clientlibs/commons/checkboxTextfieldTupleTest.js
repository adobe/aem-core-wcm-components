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
function makeCheckboxFoundationMock() {
    return {
        getValue: function() { return "false"; },
        setValue: function() {},
        isDisabled: function() { return false; },
        setDisabled: function() {}
    };
}

function makeTextfieldFoundationMock() {
    return {
        getValue: function() { return ""; },
        setValue: jasmine.createSpy("textfield.setValue"),
        isDisabled: function() { return false; },
        setDisabled: function() {}
    };
}

function getRegisteredCheckboxAdapter() {
    return globalThis.foundationRegistry._adapters.find(function(a) {
        return a.type === "foundation-toggleable" && a.selector.includes("titleValueFromDAM");
    });
}

describe("CheckboxTextfieldTuple suite", function() {
    let container;
    let checkboxEl;
    let checkboxFoundationMock;

    beforeEach(function() {
        container = document.createElement("div");
        container.innerHTML =
            "<div>" +
            '<coral-checkbox name="./titleValueFromDAM" value="true">' +
            '<input type="checkbox" handle="input" />' +
            "</coral-checkbox>" +
            "</div>";
        document.body.appendChild(container);

        checkboxEl = container.querySelector('coral-checkbox[name="./titleValueFromDAM"]');
        checkboxFoundationMock = makeCheckboxFoundationMock();
        checkboxEl.__foundationField = checkboxFoundationMock;
    });

    afterEach(function() {
        container.remove();
        globalThis.foundationRegistry._adapters.length = 0;
    });

    describe("checkbox foundation-toggleable adapter", function() {
        it("hide() should not throw when textfield is absent (SITES-46718 regression)", function() {
            // Image v3 design dialog: titleValueFromDAM checkbox present, jcr:title input absent → _textfield = null
            const tuple = new CQ.CoreComponents.CheckboxTextfieldTuple.v1(
                container,
                'coral-checkbox[name="./titleValueFromDAM"]',
                'input[name="./jcr:title"]'
            );
            expect(tuple._textfield).toBeNull();

            const adapterConfig = getRegisteredCheckboxAdapter();
            expect(adapterConfig).toBeDefined();
            expect(function() { adapterConfig.adapter().hide(); }).not.toThrow();
        });

        it("hide() should restore previous textfield value when textfield is present", function() {
            const titleInput = document.createElement("input");
            titleInput.setAttribute("name", "./jcr:title");
            titleInput.value = "Previous Title";  // constructor reads .value to initialise data-previous-value
            container.querySelector("div").appendChild(titleInput);

            const textfieldMock = makeTextfieldFoundationMock();
            titleInput.__foundationField = textfieldMock;

            const tuple = new CQ.CoreComponents.CheckboxTextfieldTuple.v1(
                container,
                'coral-checkbox[name="./titleValueFromDAM"]',
                'input[name="./jcr:title"]'
            );
            expect(tuple._textfield).not.toBeNull();

            getRegisteredCheckboxAdapter().adapter().hide();
            expect(textfieldMock.setValue).toHaveBeenCalledWith("Previous Title");
        });

        it("show() should re-enable the checkbox", function() {
            const titleInput = document.createElement("input");
            titleInput.setAttribute("name", "./jcr:title");
            container.querySelector("div").appendChild(titleInput);

            const textfieldMock = makeTextfieldFoundationMock();
            titleInput.__foundationField = textfieldMock;

            const setDisabledSpy = jasmine.createSpy("checkbox.setDisabled");
            checkboxFoundationMock.setDisabled = setDisabledSpy;

            const tuple = new CQ.CoreComponents.CheckboxTextfieldTuple.v1(
                container,
                'coral-checkbox[name="./titleValueFromDAM"]',
                'input[name="./jcr:title"]'
            );
            expect(tuple._textfield).not.toBeNull();

            getRegisteredCheckboxAdapter().adapter().show();
            expect(setDisabledSpy).toHaveBeenCalledWith(false);
        });
    });
});
