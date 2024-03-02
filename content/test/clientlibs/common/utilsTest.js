/*******************************************************************************
 * Copyright 2022 Adobe
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
describe("Common Utils suite", function() {
    beforeAll(function() {
        fixture.setBase('test/fixtures/common')
    })
    beforeEach(function() {
        this.result = fixture.load('utilsTest.html');
    });
    afterEach(function() {
        fixture.cleanup()
    });

    it("test readData", function() {
        let options = CMP.utils.readData(fixture.el.firstElementChild, "image");
        expect(options.src).toBe(
            "https://s7g10.scene7.com/is/image/AEMSitesInternal/IMG_0006?qlt=82&wid=%7B.width%7D&ts=1666811582657&dpr=off");
        expect(options.widths).toBe("600,800,1000,1200,1600")
    });

    it("setup properties", function() {
        let options = CMP.utils.readData(fixture.el.firstElementChild, "image");
        let properties = CMP.utils.setupProperties(options, {
            "widths": {
                "default": [],
                "transform": function(value) {
                    var widths = [];
                    value.split(",").forEach(function(item) {
                        item = parseFloat(item);
                        if (!isNaN(item)) {
                            widths.push(item);
                        }
                    });
                    return widths;
                }
            }
        });

        expect(properties.widths).toEqual([600, 800, 1000, 1200, 1600])
    });
});