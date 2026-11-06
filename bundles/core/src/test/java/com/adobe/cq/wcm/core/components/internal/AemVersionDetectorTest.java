/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AemVersionDetectorTest {

    private final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @Test
    void isCloudPlatform_falseOnAem65() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        assertFalse(AemVersionDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_trueOnCloudPublish() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        assertTrue(AemVersionDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_trueOnCloudAuthorReleaseTrain() {
        mockProductInfoProvider.setVersion(new Version("2026.2.24288"));
        assertTrue(AemVersionDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_falseWhenProviderMissing() {
        assertFalse(AemVersionDetector.isCloudPlatform(null));
    }

    @Test
    void isCloudPlatform_falseWhenProductInfoMissing() {
        assertFalse(AemVersionDetector.isCloudPlatform(() -> null));
    }

    @Test
    void isCloudPlatform_falseWhenVersionMissing() {
        com.adobe.granite.license.ProductInfo productInfo = mock(com.adobe.granite.license.ProductInfo.class);
        when(productInfo.getVersion()).thenReturn(null);
        assertFalse(AemVersionDetector.isCloudPlatform(() -> productInfo));
    }

    @Test
    void isGenSearchSupportedPlatform_trueOnCloudPublish() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        assertTrue(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_trueOnCloudAuthorReleaseTrain() {
        mockProductInfoProvider.setVersion(new Version("2026.2.24288"));
        assertTrue(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_trueOnBrandedLtsQualifier() {
        mockProductInfoProvider.setVersion(new Version("6.5.2.LTS"));
        assertTrue(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_trueOnBrandedLtsQualifierLowercase() {
        mockProductInfoProvider.setVersion(new Version("6.5.21.lts"));
        assertTrue(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @ParameterizedTest
    @ValueSource(strings = {"6.5.0", "6.5.25", "6.5.27"}) // GA, latest known SP, and a future SP alike
    void isGenSearchSupportedPlatform_falseOnAnyNonLtsClassic65MicroVersion(String version) {
        mockProductInfoProvider.setVersion(new Version(version));
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_falseOnQualifierThatMerelyContainsLts() {
        // Exact match only - a qualifier that merely contains "LTS" as a substring (e.g. a hypothetical internal
        // build tag) must not be mistaken for the real LTS branding.
        mockProductInfoProvider.setVersion(new Version("6.5.5.NOTLTSBUILD"));
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_falseOnOtherClassicVersion() {
        mockProductInfoProvider.setVersion(new Version("6.4.8"));
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(mockProductInfoProvider));
    }

    @Test
    void isGenSearchSupportedPlatform_failsClosedWhenProviderMissing() {
        // Fail-closed (hidden) rather than fail-open, when the platform can't be determined at all.
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(null));
    }

    @Test
    void isGenSearchSupportedPlatform_failsClosedWhenProductInfoMissing() {
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(() -> null));
    }

    @Test
    void isGenSearchSupportedPlatform_failsClosedWhenVersionMissing() {
        com.adobe.granite.license.ProductInfo productInfo = mock(com.adobe.granite.license.ProductInfo.class);
        when(productInfo.getVersion()).thenReturn(null);
        assertFalse(AemVersionDetector.isGenSearchSupportedPlatform(() -> productInfo));
    }
}
