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

import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Version;

import com.adobe.granite.license.ProductInfo;
import com.adobe.granite.license.ProductInfoProvider;

/**
 * Detects whether the runtime is AEM as a Cloud Service using {@link ProductInfo}.
 */
public final class AemCloudPlatformDetector {

    /**
     * Minimum major version for AEM as a Cloud Service release train versioning.
     * Cloud author instances report calendar-year based versions (e.g. 2026.x, 2030.x).
     */
    static final int CLOUD_RELEASE_VERSION_MAJOR_MIN = 2020;

    /**
     * Minimum classic semver version reported by AEM as a Cloud Service publish tier.
     */
    static final Version MIN_CLOUD_CLASSIC_VERSION = new Version("6.6.0");

    /**
     * First classic AEM 6.5 service pack (SP20) at which Content AI Search is unsupported on non-Cloud AEM
     * (on-premise / Adobe Managed Services). SP0-SP19 remain supported.
     */
    static final Version MIN_UNSUPPORTED_CLASSIC_65_VERSION = new Version("6.5.20");

    private AemCloudPlatformDetector() {
    }

    /**
     * @param productInfoProvider Granite product info service
     * @return {@code true} when the runtime is AEM as a Cloud Service
     */
    public static boolean isCloudPlatform(@Nullable ProductInfoProvider productInfoProvider) {
        Version version = getVersion(productInfoProvider);
        if (version == null) {
            return false;
        }
        if (version.getMajor() >= CLOUD_RELEASE_VERSION_MAJOR_MIN) {
            return true;
        }
        return version.compareTo(MIN_CLOUD_CLASSIC_VERSION) >= 0;
    }

    /**
     * @param productInfoProvider Granite product info service
     * @return {@code true} when the runtime is classic AEM 6.5 at service pack 20 or later (SP20+ is unsupported
     *         for Content AI Search on non-Cloud AEM; earlier 6.5 service packs and AEM as a Cloud Service are
     *         unaffected)
     */
    public static boolean isUnsupportedClassic65ServicePack(@Nullable ProductInfoProvider productInfoProvider) {
        if (isCloudPlatform(productInfoProvider)) {
            return false;
        }
        Version version = getVersion(productInfoProvider);
        if (version == null) {
            return false;
        }
        return version.getMajor() == 6 && version.getMinor() == 5
            && version.compareTo(MIN_UNSUPPORTED_CLASSIC_65_VERSION) >= 0;
    }

    @Nullable
    private static Version getVersion(@Nullable ProductInfoProvider productInfoProvider) {
        if (productInfoProvider == null) {
            return null;
        }
        ProductInfo productInfo = productInfoProvider.getProductInfo();
        if (productInfo == null) {
            return null;
        }
        return productInfo.getVersion();
    }
}
