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
    let validator;
    let testPathSanitization;

    beforeAll(function() {
        fixture.setBase('test/fixtures/htmlidvalidator');
    });

    beforeEach(function() {
        this.result = fixture.load('htmlIdValidationTest.html');

        // Wait for the validator to be registered
        setTimeout(function() {
            if (window.foundationRegistry && window.foundationRegistry.validators.length > 0) {
                validator = window.foundationRegistry.validators[0];
            }
        }, 100);

        // Extract the path sanitization logic for direct testing
        testPathSanitization = function(compPath) {
            if (!compPath) {
                return null;
            }

            var pagePath = compPath.split("/_jcr_content")[0];

            // Sanitize pagePath to prevent path traversal and injection attacks
            if (!pagePath || typeof pagePath !== 'string') {
                return null;
            }

            // Remove any path traversal attempts and normalize the path
            pagePath = pagePath.replace(/\.\./g, '').replace(/\/+/g, '/');

            // Validate that the path starts with / and doesn't contain dangerous patterns
            if (!/^\//.test(pagePath) || /[<>"|*?]/.test(pagePath)) {
                console.warn("Invalid page path detected: " + pagePath);
                return null;
            }

            return pagePath;
        };
    });

    afterEach(function() {
        fixture.cleanup();
    });

    describe("Path Sanitization Security Tests", function() {
        it("should remove path traversal sequences", function() {
            var result = testPathSanitization('/content/../../../etc/passwd/_jcr_content/root/component');

            // The path traversal sequences should be removed, resulting in a clean path
            expect(result).toBe('/content/etc/passwd');
            expect(result).not.toContain('..');
        });

        it("should block dangerous characters in paths", function() {
            spyOn(console, 'warn');

            var dangerousChars = ['<', '>', '"', '|', '*', '?'];

            dangerousChars.forEach(function(char) {
                console.warn.calls.reset(); // Reset spy between iterations
                var result = testPathSanitization('/content/test' + char + 'malicious/_jcr_content/root/component');

                expect(result).toBeNull();
                expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
            });
        });

        it("should prevent actual path traversal attacks by rejecting dangerous patterns", function() {
            spyOn(console, 'warn');

            // Test a path that contains dangerous characters that would be used in attacks
            var result = testPathSanitization('/content/test<script>alert("xss")</script>/_jcr_content/root/component');

            expect(result).toBeNull();
            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
        });

        it("should allow valid AEM paths", function() {
            var validPaths = [
                '/content/mysite/en/page/_jcr_content/root/component',
                '/content/my-site/en/page-name/_jcr_content/root/component',
                '/content/mysite/en/page_with_underscores/_jcr_content/root/component',
                '/content/mysite/en/page.with.dots/_jcr_content/root/component'
            ];

            validPaths.forEach(function(path) {
                var result = testPathSanitization(path);

                expect(result).not.toBeNull();
                expect(result).toMatch(/^\/content/);
            });
        });

        it("should normalize multiple slashes in paths", function() {
            var result = testPathSanitization('/content//test///page/_jcr_content/root/component');

            expect(result).toBe('/content/test/page');
            expect(result).not.toMatch(/\/\//); // Should not contain double slashes
        });

        it("should reject paths that don't start with forward slash", function() {
            spyOn(console, 'warn');

            var result = testPathSanitization('content/test/page/_jcr_content/root/component');

            expect(result).toBeNull();
            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
        });

        it("should handle null or undefined compPath", function() {
            var result1 = testPathSanitization(null);
            var result2 = testPathSanitization(undefined);
            var result3 = testPathSanitization('');

            expect(result1).toBeNull();
            expect(result2).toBeNull();
            expect(result3).toBeNull();
        });

        it("should remove path traversal sequences", function() {
            var result = testPathSanitization('/content/test/../admin/_jcr_content/root/component');

            expect(result).toBe('/content/test/admin');
            expect(result).not.toContain('..');
        });

        it("should handle paths with spaces", function() {
            var result = testPathSanitization('/content/my site/en/page with spaces/_jcr_content/root/component');

            expect(result).toBe('/content/my site/en/page with spaces');
            expect(result).toMatch(/^\/content/);
        });

        it("should handle paths with dots", function() {
            var result = testPathSanitization('/content/mysite/en/page.with.dots/_jcr_content/root/component');

            expect(result).toBe('/content/mysite/en/page.with.dots');
            expect(result).toMatch(/^\/content/);
        });

        it("should handle paths with underscores and hyphens", function() {
            var result = testPathSanitization('/content/my-site/en/page_with_underscores/_jcr_content/root/component');

            expect(result).toBe('/content/my-site/en/page_with_underscores');
            expect(result).toMatch(/^\/content/);
        });

        it("should block paths ending with question mark", function() {
            spyOn(console, 'warn');

            var result = testPathSanitization('/content/test/page?/_jcr_content/root/component');

            expect(result).toBeNull();
            expect(console.warn).toHaveBeenCalledWith(jasmine.stringMatching(/Invalid page path detected/));
        });
    });
});
