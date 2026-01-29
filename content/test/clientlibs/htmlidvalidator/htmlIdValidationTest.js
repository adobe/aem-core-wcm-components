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
describe("HTML ID Validator Path Sanitization", function() {
    let originalAjax;
    let originalIsEnabled;
    let ajaxCalls = [];

    beforeAll(function() {
        fixture.setBase('test/fixtures/htmlidvalidator');
    });

    beforeEach(function() {
        this.result = fixture.load('htmlIdValidationTest.html');

        // Store original AJAX and replace with spy
        originalAjax = window.$.ajax;
        originalIsEnabled = window.Granite.Toggles.isEnabled;
        ajaxCalls = [];

        window.$.ajax = function(options) {
            ajaxCalls.push(options);
            // Simulate successful responses
            if (options.url.endsWith('.json')) {
                options.success?.({});
            } else if (options.url.endsWith('.html?wcmmode=disabled')) {
                options.success?.('<div>no matching ids</div>');
            }
        };

        // Helper function to enable/disable the CT_SANITIZE_ENCODE_PATH toggle
        this.setToggleEnabled = function(enabled) {
            window.Granite.Toggles.isEnabled = function(feature) {
                if (feature === "CT_SITES-33116") {
                    return enabled;
                }
                return false;
            };
        };

        // Helper function to create mock elements
        this.createMockElement = function(formAction, inputValue) {
            const mockForm = {
                attr: function(name) {
                    return formAction;
                },
                getAttribute: function(name) {
                    return formAction;
                }
            };

            return {
                closest: function(selector) {
                    return mockForm;
                },
                val: function() {
                    return inputValue || 'test-id';
                },
                getAttribute: function(name) {
                    return formAction;
                }
            };
        };

        // Helper function to get the last AJAX call URL
        this.getLastAjaxUrl = function() {
            return ajaxCalls.length > 0 ? ajaxCalls[ajaxCalls.length - 1].url : null;
        };

        // Helper function to clear AJAX calls
        this.clearAjaxCalls = function() {
            ajaxCalls = [];
        };
    });

    afterEach(function() {
        // Restore original AJAX and toggle
        if (originalAjax) {
            window.$.ajax = originalAjax;
        }
        if (originalIsEnabled) {
            window.Granite.Toggles.isEnabled = originalIsEnabled;
        }
        fixture.cleanup();
    });

    describe("Path Sanitization Security Tests", function() {
        it("should handle path traversal sequences in URLs", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('/content/../../../etc/passwd/_jcr_content/root/component', 'unique-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED - should sanitize path traversal
            this.setToggleEnabled(true);
            validator.validate(mockElement);
            let ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                expect(ajaxUrl).toMatch(/^\/content\/etc\/passwd\.html/);
                expect(ajaxUrl).not.toContain('..');
            }

            // Test with toggle DISABLED - should encode but not sanitize
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement);
            ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                // Legacy behavior: .. is encoded as %2E%2E
                expect(ajaxUrl).toContain('%2E%2E');
            }
        });

        it("should block dangerous characters in paths", function() {
            spyOn(console, 'warn');

            const dangerousChars = ['<', '>', '"', '|', '*', '?'];
            const validator = window.foundationRegistry.validators[0];

            dangerousChars.forEach(function(char) {
                // Test with toggle ENABLED - should block dangerous characters
                console.warn.calls.reset();
                this.clearAjaxCalls();
                this.setToggleEnabled(true);

                const mockElement = this.createMockElement('/content/test' + char + 'malicious/_jcr_content/root/component', 'test-id');
                validator.validate(mockElement);

                expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
                expect(ajaxCalls.length).toBe(0);

                // Test with toggle DISABLED - should encode but not block
                console.warn.calls.reset();
                this.clearAjaxCalls();
                this.setToggleEnabled(false);

                validator.validate(mockElement);

                expect(console.warn).not.toHaveBeenCalled();
                expect(ajaxCalls.length).toBeGreaterThan(0);
            }.bind(this));
        });

        it("should prevent XSS attempts in paths", function() {
            spyOn(console, 'warn');

            const mockElement = this.createMockElement('/content/test<script>alert("xss")</script>/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED - should block XSS attempts
            this.setToggleEnabled(true);
            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            expect(ajaxCalls.length).toBe(0);

            // Test with toggle DISABLED - should encode but not block
            console.warn.calls.reset();
            this.clearAjaxCalls();
            this.setToggleEnabled(false);

            validator.validate(mockElement);

            expect(console.warn).not.toHaveBeenCalled();
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should allow valid AEM paths", function() {
            const validPaths = [
                '/content/mysite/en/page/_jcr_content/root/component',
                '/content/my-site/en/page-name/_jcr_content/root/component',
                '/content/mysite/en/page_with_underscores/_jcr_content/root/component',
                '/content/mysite/en/page.with.dots/_jcr_content/root/component'
            ];

            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            validPaths.forEach(function(path) {
                this.clearAjaxCalls();
                const mockElement = this.createMockElement(path, 'unique-id');
                validator.validate(mockElement);
                expect(ajaxCalls.length).toBeGreaterThan(0);
            }.bind(this));

            // Test with toggle DISABLED
            this.setToggleEnabled(false);
            validPaths.forEach(function(path) {
                this.clearAjaxCalls();
                const mockElement = this.createMockElement(path, 'unique-id');
                validator.validate(mockElement);
                expect(ajaxCalls.length).toBeGreaterThan(0);
            }.bind(this));
        });

        it("should normalize multiple slashes in paths", function() {
            const mockElement = this.createMockElement('/content//test///page/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED - should normalize slashes
            this.setToggleEnabled(true);
            validator.validate(mockElement);
            let ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                expect(ajaxUrl).toMatch(/^\/content\/test\/page\.html/);
                expect(ajaxUrl).not.toMatch(/\/\//);
            }

            // Test with toggle DISABLED - should NOT normalize slashes
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement);
            ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                // Legacy behavior: slashes are encoded but not normalized
                expect(ajaxUrl).toContain('%2F%2F');
            }
        });

        it("should reject paths that don't start with forward slash", function() {
            spyOn(console, 'warn');

            const mockElement = this.createMockElement('content/test/page/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED - should reject
            this.setToggleEnabled(true);
            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            expect(ajaxCalls.length).toBe(0);

            // Test with toggle DISABLED - should allow (legacy behavior)
            console.warn.calls.reset();
            this.clearAjaxCalls();
            this.setToggleEnabled(false);

            validator.validate(mockElement);

            expect(console.warn).not.toHaveBeenCalled();
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should handle null or undefined compPath", function() {
            const validator = window.foundationRegistry.validators[0];
            const mockElement1 = this.createMockElement(null, 'test-id');
            const mockElement2 = this.createMockElement(undefined, 'test-id');
            const mockElement3 = this.createMockElement('', 'test-id');

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            validator.validate(mockElement1);
            validator.validate(mockElement2);
            validator.validate(mockElement3);
            expect(ajaxCalls.length).toBe(0);

            // Test with toggle DISABLED - same behavior
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement1);
            validator.validate(mockElement2);
            validator.validate(mockElement3);
            expect(ajaxCalls.length).toBe(0);
        });

        it("should properly encode paths with spaces for URL construction", function() {
            const mockElement = this.createMockElement('/content/my site/en/page with spaces/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            validator.validate(mockElement);
            let ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                expect(ajaxUrl).toMatch(/\/content\/my%20site\/en\/page%20with%20spaces\.html/);
            }

            // Test with toggle DISABLED - same encoding behavior
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement);
            ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl?.endsWith('.html?wcmmode=disabled')) {
                expect(ajaxUrl).toMatch(/\/content\/my%20site\/en\/page%20with%20spaces\.html/);
            }
        });

        it("should handle paths with dots correctly", function() {
            const mockElement = this.createMockElement('/content/mysite/en/page.with.dots/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);

            // Test with toggle DISABLED
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should handle paths with underscores and hyphens", function() {
            const mockElement = this.createMockElement('/content/my-site/en/page_with_underscores/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED
            this.setToggleEnabled(true);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);

            // Test with toggle DISABLED
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should block paths ending with question mark", function() {
            spyOn(console, 'warn');

            const mockElement = this.createMockElement('/content/test/page?/_jcr_content/root/component', 'test-id');
            const validator = window.foundationRegistry.validators[0];

            // Test with toggle ENABLED - should block
            this.setToggleEnabled(true);
            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            expect(ajaxCalls.length).toBe(0);

            // Test with toggle DISABLED - should allow (legacy behavior)
            console.warn.calls.reset();
            this.clearAjaxCalls();
            this.setToggleEnabled(false);

            validator.validate(mockElement);

            expect(console.warn).not.toHaveBeenCalled();
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });
    });
});
