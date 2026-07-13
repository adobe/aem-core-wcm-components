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
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AemCloudPlatformDetectorTest {

    private final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @Test
    void isCloudPlatform_falseOnAem65() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        assertFalse(AemCloudPlatformDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_trueOnCloudPublish() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        assertTrue(AemCloudPlatformDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_trueOnCloudAuthorReleaseTrain() {
        mockProductInfoProvider.setVersion(new Version("2026.2.24288"));
        assertTrue(AemCloudPlatformDetector.isCloudPlatform(mockProductInfoProvider));
    }

    @Test
    void isCloudPlatform_falseWhenProviderMissing() {
        assertFalse(AemCloudPlatformDetector.isCloudPlatform(null));
    }

    @Test
    void isCloudPlatform_falseWhenProductInfoMissing() {
        assertFalse(AemCloudPlatformDetector.isCloudPlatform(() -> null));
    }

    @Test
    void isCloudPlatform_falseWhenVersionMissing() {
        com.adobe.granite.license.ProductInfo productInfo = mock(com.adobe.granite.license.ProductInfo.class);
        when(productInfo.getVersion()).thenReturn(null);
        assertFalse(AemCloudPlatformDetector.isCloudPlatform(() -> productInfo));
    }
}
