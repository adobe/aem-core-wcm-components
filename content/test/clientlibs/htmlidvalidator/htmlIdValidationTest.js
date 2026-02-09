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

        // Helper function to get the validator
        this.getValidator = function() {
            return window.foundationRegistry.validators[0];
        };

        // Helper function to reset state for toggle disabled test
        this.resetForDisabledToggle = function() {
            this.clearAjaxCalls();
            this.setToggleEnabled(false);
        };

        // Helper function to assert path was blocked (toggle enabled behavior)
        this.expectPathBlocked = function() {
            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            expect(ajaxCalls.length).toBe(0);
        };

        // Helper function to assert path was allowed (toggle disabled/legacy behavior)
        this.expectPathAllowed = function() {
            expect(console.warn).not.toHaveBeenCalled();
            expect(ajaxCalls.length).toBeGreaterThan(0);
        };

        // Helper function to reset console.warn spy
        this.resetWarnSpy = function() {
            if (console.warn.calls) {
                console.warn.calls.reset();
            }
        };

        // Helper function to validate and get the HTML URL
        this.validateAndGetHtmlUrl = function(mockElement) {
            this.getValidator().validate(mockElement);
            const url = this.getLastAjaxUrl();
            return url?.endsWith('.html?wcmmode=disabled') ? url : null;
        };

        // Helper function to test path is allowed in both toggle states
        this.expectPathAllowedInBothStates = function(mockElement) {
            const validator = this.getValidator();

            this.setToggleEnabled(true);
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);

            this.resetForDisabledToggle();
            validator.validate(mockElement);
            expect(ajaxCalls.length).toBeGreaterThan(0);
        };

        // Helper function to test path is blocked when enabled, allowed when disabled
        this.expectBlockedWhenEnabledAllowedWhenDisabled = function(mockElement) {
            const validator = this.getValidator();

            this.setToggleEnabled(true);
            validator.validate(mockElement);
            this.expectPathBlocked();

            this.resetWarnSpy();
            this.resetForDisabledToggle();
            validator.validate(mockElement);
            this.expectPathAllowed();
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

            // Test with toggle ENABLED - should sanitize path traversal
            this.setToggleEnabled(true);
            let ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toMatch(/^\/content\/etc\/passwd\.html/);
                expect(ajaxUrl).not.toContain('..');
            }

            // Test with toggle DISABLED - should encode but not sanitize
            this.resetForDisabledToggle();
            ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toContain('%2E%2E');
            }
        });

        it("should block dangerous characters in paths", function() {
            spyOn(console, 'warn');

            const dangerousChars = ['<', '>', '"', '|', '*', '?'];

            dangerousChars.forEach(function(char) {
                this.resetWarnSpy();
                this.clearAjaxCalls();

                const mockElement = this.createMockElement('/content/test' + char + 'malicious/_jcr_content/root/component', 'test-id');
                this.expectBlockedWhenEnabledAllowedWhenDisabled(mockElement);
            }.bind(this));
        });

        it("should prevent XSS attempts in paths", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('/content/test<script>alert("xss")</script>/_jcr_content/root/component', 'test-id');
            this.expectBlockedWhenEnabledAllowedWhenDisabled(mockElement);
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
                expect(ajaxUrl).toMatch(/^\/content\/test\/page\.html/);
                expect(ajaxUrl).not.toMatch(/\/\//);
            }

            // Test with toggle DISABLED - should NOT normalize slashes
            this.resetForDisabledToggle();
            ajaxUrl = this.validateAndGetHtmlUrl(mockElement);
            if (ajaxUrl) {
                expect(ajaxUrl).toContain('%2F%2F');
            }
        });

        it("should reject paths that don't start with forward slash", function() {
            spyOn(console, 'warn');
            const mockElement = this.createMockElement('content/test/page/_jcr_content/root/component', 'test-id');
            this.expectBlockedWhenEnabledAllowedWhenDisabled(mockElement);
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
            this.expectBlockedWhenEnabledAllowedWhenDisabled(mockElement);
        });
    });
});
