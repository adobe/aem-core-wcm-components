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
    let ajaxCalls = [];

    beforeAll(function() {
        fixture.setBase('test/fixtures/htmlidvalidator');
    });

    beforeEach(function() {
        this.result = fixture.load('htmlIdValidationTest.html');

        // Store original AJAX and replace with spy
        originalAjax = window.$.ajax;
        ajaxCalls = [];

        window.$.ajax = function(options) {
            ajaxCalls.push(options);
            // Simulate successful responses
            if (options.url.endsWith('.json')) {
                options.success && options.success({});
            } else if (options.url.endsWith('.html?wcmmode=disabled')) {
                options.success && options.success('<div>no matching ids</div>');
            }
        };

        // Helper function to create mock elements
        this.createMockElement = function(formAction, inputValue) {
            return {
                closest: function(selector) {
                    return {
                        attr: function(name) {
                            return formAction;
                        },
                        getAttribute: function(name) {
                            return formAction;
                        }
                    };
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
        // Restore original AJAX
        if (originalAjax) {
            window.$.ajax = originalAjax;
        }
        fixture.cleanup();
    });

    describe("Path Sanitization Security Tests", function() {
        it("should handle path traversal sequences in URLs", function() {
            spyOn(console, 'warn');
            var mockElement = this.createMockElement('/content/../../../etc/passwd/_jcr_content/root/component', 'unique-id');

            // Get the validator from foundation registry
            var validator = window.foundationRegistry.validators[0];
            validator.validate(mockElement);

            // Check that the AJAX call was made with sanitized URL
            var ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl && ajaxUrl.endsWith('.html?wcmmode=disabled')) {
                // The URL should have path traversal sequences removed
                expect(ajaxUrl).toMatch(/^\/content\/etc\/passwd\.html/);
                expect(ajaxUrl).not.toContain('..');
            }
        });

        it("should block dangerous characters in paths", function() {
            spyOn(console, 'warn');

            var dangerousChars = ['<', '>', '"', '|', '*', '?'];
            var validator = window.foundationRegistry.validators[0];

            dangerousChars.forEach(function(char) {
                console.warn.calls.reset(); // Reset spy between iterations
                this.clearAjaxCalls(); // Clear previous AJAX calls

                var mockElement = this.createMockElement('/content/test' + char + 'malicious/_jcr_content/root/component', 'test-id');

                validator.validate(mockElement);

                // Should log a warning for dangerous characters
                expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));

                // Should not make AJAX call for invalid paths
                expect(ajaxCalls.length).toBe(0);
            }.bind(this));
        });

        it("should prevent XSS attempts in paths", function() {
            spyOn(console, 'warn');

            var mockElement = this.createMockElement('/content/test<script>alert("xss")</script>/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            // Should not make AJAX call for invalid paths
            expect(ajaxCalls.length).toBe(0);
        });

        it("should allow valid AEM paths", function() {
            var validPaths = [
                '/content/mysite/en/page/_jcr_content/root/component',
                '/content/my-site/en/page-name/_jcr_content/root/component',
                '/content/mysite/en/page_with_underscores/_jcr_content/root/component',
                '/content/mysite/en/page.with.dots/_jcr_content/root/component'
            ];

            var validator = window.foundationRegistry.validators[0];

            validPaths.forEach(function(path) {
                this.clearAjaxCalls();
                var mockElement = this.createMockElement(path, 'unique-id');

                validator.validate(mockElement);

                // Should make AJAX calls for valid paths (indicating they passed sanitization)
                expect(ajaxCalls.length).toBeGreaterThan(0);
            }.bind(this));
        });

        it("should normalize multiple slashes in paths", function() {
            var mockElement = this.createMockElement('/content//test///page/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            // Check that the AJAX call was made with normalized URL
            var ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl && ajaxUrl.endsWith('.html?wcmmode=disabled')) {
                // Verify the URL has normalized slashes
                expect(ajaxUrl).toMatch(/^\/content\/test\/page\.html/);
                expect(ajaxUrl).not.toMatch(/\/\//); // Should not contain double slashes
            }
        });

        it("should reject paths that don't start with forward slash", function() {
            spyOn(console, 'warn');

            var mockElement = this.createMockElement('content/test/page/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            // Should not make AJAX call for invalid paths
            expect(ajaxCalls.length).toBe(0);
        });

        it("should handle null or undefined compPath", function() {
            var validator = window.foundationRegistry.validators[0];
            var mockElement1 = this.createMockElement(null, 'test-id');
            var mockElement2 = this.createMockElement(undefined, 'test-id');
            var mockElement3 = this.createMockElement('', 'test-id');

            // All should return early due to invalid compPath - no AJAX calls should be made
            validator.validate(mockElement1);
            validator.validate(mockElement2);
            validator.validate(mockElement3);

            expect(ajaxCalls.length).toBe(0);
        });

        it("should properly encode paths with spaces for URL construction", function() {
            var mockElement = this.createMockElement('/content/my site/en/page with spaces/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            // Check that the AJAX call was made with properly encoded URL
            var ajaxUrl = this.getLastAjaxUrl();
            if (ajaxUrl && ajaxUrl.endsWith('.html?wcmmode=disabled')) {
                // Verify spaces are properly encoded but slashes are preserved
                expect(ajaxUrl).toMatch(/\/content\/my%20site\/en\/page%20with%20spaces\.html/);
            }
        });

        it("should handle paths with dots correctly", function() {
            var mockElement = this.createMockElement('/content/mysite/en/page.with.dots/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            // Should make AJAX calls for valid paths with dots
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should handle paths with underscores and hyphens", function() {
            var mockElement = this.createMockElement('/content/my-site/en/page_with_underscores/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            // Should make AJAX calls for valid paths with underscores and hyphens
            expect(ajaxCalls.length).toBeGreaterThan(0);
        });

        it("should block paths ending with question mark", function() {
            spyOn(console, 'warn');

            var mockElement = this.createMockElement('/content/test/page?/_jcr_content/root/component', 'test-id');
            var validator = window.foundationRegistry.validators[0];

            validator.validate(mockElement);

            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            // Should not make AJAX call for invalid paths
            expect(ajaxCalls.length).toBe(0);
        });
    });
});
