/*******************************************************************************
 * Copyright 2025 Adobe
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
describe("HTML ID validator path encoding and preview", function() {
    let originalAjax;
    let originalIsEnabled;
    let ajaxCalls = [];

    beforeAll(function() {
        fixture.setBase('test/fixtures/htmlidvalidator');
    });

    beforeEach(function() {
        this.result = fixture.load('htmlIdValidationTest.html');

        // Store original AJAX and replace with spy
        originalAjax = globalThis.$.ajax;
        originalIsEnabled = globalThis.Granite.Toggles.isEnabled;
        ajaxCalls = [];

        globalThis.$.ajax = function(options) {
            ajaxCalls.push(options);
            // Simulate successful responses
            if (options.url?.endsWith(".json")) {
                options.success?.({});
            } else if (options.url?.includes(".html") && options.url?.includes("wcmmode=disabled")) {
                options.success?.("<div>no matching ids</div>");
            }
        };

        this.toggle33116Enabled = true;
        this.toggle41942Enabled = true;

        // CT_SITES-33116: path encoding toggle. CT_SITES-41942: page preview URL resolution and fetched markup handling.
        this.setToggleEnabled = function(enabled33116) {
            this.toggle33116Enabled = enabled33116;
            globalThis.Granite.Toggles.isEnabled = function(feature) {
                if (feature === "CT_SITES-33116") {
                    return this.toggle33116Enabled;
                }
                if (feature === "CT_SITES-41942") {
                    return this.toggle41942Enabled;
                }
                return true;
            }.bind(this);
        }.bind(this);

        this.setToggle41942Enabled = function(enabled41942) {
            this.toggle41942Enabled = enabled41942;
            globalThis.Granite.Toggles.isEnabled = function(feature) {
                if (feature === "CT_SITES-33116") {
                    return this.toggle33116Enabled;
                }
                if (feature === "CT_SITES-41942") {
                    return this.toggle41942Enabled;
                }
                return true;
            }.bind(this);
        }.bind(this);

        // Helper function to create mock elements
        this.createMockElement = function(formAction, inputValue = "test-id") {
            const mockForm = {
                attr: function(name) {
                    return formAction;
                },
                getAttribute: function(name) {
                    return formAction;
                }
            };

            const resolvedVal = inputValue || "test-id";

            return {
                closest: function(selector) {
                    return mockForm;
                },
                val: function() {
                    return resolvedVal;
                },
                value: resolvedVal,
                getAttribute: function(name) {
                    return formAction;
                }
            };
        };

        // Helper function to get the last AJAX call URL
        this.getLastAjaxUrl = function() {
            return ajaxCalls.at(-1)?.url ?? null;
        };

        // Helper function to clear AJAX calls
        this.clearAjaxCalls = function() {
            ajaxCalls = [];
        };

        // Helper function to get the html-unique-id field validator (Karma exposes the instance via test API)
        this.getValidator = function() {
            const api = globalThis.__HTML_ID_VALIDATOR_EDITOR_TEST_API;
            if (typeof api?.getHtmlUniqueIdFieldValidator === "function") {
                return api.getHtmlUniqueIdFieldValidator();
            }
            const v = globalThis.foundationRegistry?.validators;
            if (v == null) {
                return undefined;
            }
            for (const entry of v) {
                if (entry?.selector === "[data-validation=html-unique-id-validator]") {
                    return entry;
                }
            }
            return v[0];
        };

        // Helper for CT_SITES-33116 off: legacy path resolution without normalising dot segments
        this.resetForDisabledToggle = function() {
            this.clearAjaxCalls();
            this.toggle33116Enabled = false;
            this.toggle41942Enabled = true;
            globalThis.Granite.Toggles.isEnabled = function(feature) {
                if (feature === "CT_SITES-33116") {
                    return this.toggle33116Enabled;
                }
                if (feature === "CT_SITES-41942") {
                    return this.toggle41942Enabled;
                }
                return true;
            }.bind(this);
        };

        // When path encoding is on and the segment is invalid, the validator logs and skips the HTML preview request
        this.expectInvalidPathSkipsPreview = function() {
            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            expect(ajaxCalls.length).toBe(0);
        };

        // When the path segment is accepted, no warning and a preview-related request is issued
        this.expectPreviewRequestSent = function() {
            expect(console.warn).not.toHaveBeenCalled();
            expect(ajaxCalls.length).toBeGreaterThan(0);
        };

        // Helper function to reset console.warn spy
        this.resetWarnSpy = function() {
            console.warn.calls?.reset();
        };

        // Helper function to validate and get the HTML URL
        this.validateAndGetHtmlUrl = function(mockElement) {
            this.getValidator().validate(mockElement);
            const url = this.getLastAjaxUrl();
            return url?.includes(".html") && url?.includes("wcmmode=disabled") ? url : null;
        };

        // Runs validation with path encoding on, then with CT_SITES-33116 off (legacy path handling)
        this.expectPathAllowedInBothStates = function(mockElement) {
            const validator = this.getValidator();

            this.setToggleEnabled(true);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);

            this.resetForDisabledToggle();
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);
        };

        // Invalid segment with encoding on skips preview; same form action with encoding off may still resolve a preview URL
        this.expectEncodingRejectsThenLegacyAcceptsPath = function(mockElement) {
            const validator = this.getValidator();

            this.setToggleEnabled(true);
            validator.validate(mockElement);
            this.expectInvalidPathSkipsPreview();

            this.resetWarnSpy();
            this.resetForDisabledToggle();
            validator.validate(mockElement);
            this.expectPreviewRequestSent();
        };

        this.setToggleEnabled(true);
    });

    afterEach(function() {
        // Restore original AJAX and toggle
        if (originalAjax) {
            globalThis.$.ajax = originalAjax;
        }
        if (originalIsEnabled) {
            globalThis.Granite.Toggles.isEnabled = originalIsEnabled;
        }
        fixture.cleanup();
    });

    describe("Path shape and encoding behaviour", function() {
        it("should normalise parent-directory segments when path encoding toggle is on", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('/content/../../../etc/passwd/_jcr_content/root/component', 'unique-id');

            this.setToggleEnabled(true);
            let ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toContain("/content/etc/passwd.html");
                expect(ajaxUrl).not.toContain("..");
            }

            // With path encoding off, dot segments are not normalised the same way
            this.resetForDisabledToggle();
            ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toContain("/etc/passwd.html");
                expect(ajaxUrl).not.toMatch(/\.\.\//);
            }
        });

        it("should block disallowed characters in paths", function() {
            spyOn(console, 'warn');

            const disallowedPathChars = ['<', '>', '"', '|', '*', '?'];

            disallowedPathChars.forEach(function(char) {
                this.resetWarnSpy();
                this.clearAjaxCalls();

                const mockElement = this.createMockElement('/content/test' + char + 'segment/_jcr_content/root/component', 'test-id');
                this.expectEncodingRejectsThenLegacyAcceptsPath(mockElement);
            }.bind(this));
        });

        it("should reject paths containing angle brackets or angle-bracketed segments in the path string", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('/content/test<tag>text</tag>/_jcr_content/root/component', 'test-id');
            this.expectEncodingRejectsThenLegacyAcceptsPath(mockElement);
        });

        it("should allow valid AEM paths", function() {
            const validPaths = [
                '/content/mysite/en/page/_jcr_content/root/component',
                '/content/my-site/en/page-name/_jcr_content/root/component',
                '/content/mysite/en/page_with_underscores/_jcr_content/root/component',
                '/content/mysite/en/page.with.dots/_jcr_content/root/component'
            ];

            validPaths.forEach(function(path) {
                this.clearAjaxCalls();
                const mockElement = this.createMockElement(path, 'unique-id');
                this.expectPathAllowedInBothStates(mockElement);
            }.bind(this));
        });

        it("should normalize multiple slashes in paths", function() {
            const mockElement = this.createMockElement('/content//test///page/_jcr_content/root/component', 'test-id');

            // Test with toggle ENABLED - should normalize slashes
            this.setToggleEnabled(true);
            let ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toMatch(/content\/test\/page\.html/);
                expect(ajaxUrl).not.toMatch(/content\/\/test/);
            }

            // Test with toggle DISABLED - should NOT normalize slashes
            this.resetForDisabledToggle();
            ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toContain("//test///");
            }
        });

        it("should reject paths that don't start with forward slash", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('content/test/page/_jcr_content/root/component', 'test-id');
            this.expectEncodingRejectsThenLegacyAcceptsPath(mockElement);
        });

        it("should handle null or undefined compPath", function() {
            const validator = this.getValidator();
            const mockElements = [
                this.createMockElement(null, 'test-id'),
                this.createMockElement(undefined, 'test-id'),
                this.createMockElement('', 'test-id')
            ];

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            mockElements.forEach(function(el) { validator.validate(el); });
            expect(ajaxCalls.length).toBe(0);

            // Test with toggle DISABLED - same behavior
            this.resetForDisabledToggle();
            mockElements.forEach(function(el) { validator.validate(el); });
            expect(ajaxCalls.length).toBe(0);
        });

        it("should properly encode paths with spaces for URL construction", function() {
            const mockElement = this.createMockElement('/content/my site/en/page with spaces/_jcr_content/root/component', 'test-id');
            const expectedPattern = /\/content\/my%20site\/en\/page%20with%20spaces\.html/;

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            let ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toMatch(expectedPattern);
            }

            // Test with toggle DISABLED - same encoding behavior
            this.resetForDisabledToggle();
            ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toMatch(expectedPattern);
            }
        });

        it("should handle paths with dots correctly", function() {
            const mockElement = this.createMockElement('/content/mysite/en/page.with.dots/_jcr_content/root/component', 'test-id');
            this.expectPathAllowedInBothStates(mockElement);
        });

        it("should handle paths with underscores and hyphens", function() {
            const mockElement = this.createMockElement('/content/my-site/en/page_with_underscores/_jcr_content/root/component', 'test-id');
            this.expectPathAllowedInBothStates(mockElement);
        });

        it("should block paths ending with question mark", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('/content/test/page?/_jcr_content/root/component', 'test-id');
            this.expectEncodingRejectsThenLegacyAcceptsPath(mockElement);
        });

        it("does not load page HTML when the form action omits the authored content path segment and preview helpers are on", function() {
            this.setToggleEnabled(false);
            this.setToggle41942Enabled(true);
            const mockElement = this.createMockElement("/content/usergenerated/demo/payload/resource", "id1");
            this.getValidator().validate(mockElement);
            const htmlCalls = ajaxCalls.filter(function(c) {
                return c?.url?.includes(".html") && c?.url?.includes("wcmmode=disabled");
            });
            expect(htmlCalls.length).toBe(0);
        });

        it("loads page HTML for actions without the authored content path segment when preview helpers are off", function() {
            this.setToggleEnabled(false);
            this.setToggle41942Enabled(false);
            const mockElement = this.createMockElement("/content/usergenerated/demo/payload/resource", "id1");
            this.getValidator().validate(mockElement);
            const htmlCalls = ajaxCalls.filter(function(c) {
                return c?.url?.includes(".html") && c?.url?.includes("wcmmode=disabled");
            });
            expect(htmlCalls.length).toBeGreaterThan(0);
        });

        it("accepts form actions that use jcr:content in the path when preview helpers are on", function() {
            this.setToggleEnabled(false);
            this.setToggle41942Enabled(true);
            const mockElement = this.createMockElement("/content/mysite/en/page/jcr:content/root/res", "id1");
            this.getValidator().validate(mockElement);
            const htmlCalls = ajaxCalls.filter(function(c) {
                return c?.url?.includes(".html") && c?.url?.includes("wcmmode=disabled");
            });
            expect(htmlCalls.length).toBeGreaterThan(0);
        });
    });
});
