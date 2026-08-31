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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Version;

import com.adobe.granite.license.ProductInfo;
import com.adobe.granite.license.ProductInfoProvider;

/**
 * Detects properties of the running AEM version/platform (Cloud Service vs. classic, and classic AEM 6.5 LTS
 * branding) using {@link ProductInfo}.
 */
public final class AemVersionDetector {

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
     * Version qualifier reported by properly provisioned/branded classic AEM 6.5 LTS instances (e.g.
     * {@code 6.5.2.LTS}). A classic 6.5.x version without this qualifier (e.g. a bare {@code 6.5.0}) is treated
     * as an unsupported/unbranded environment.
     */
    static final String CLASSIC_65_LTS_QUALIFIER = "LTS";

    private AemVersionDetector() {
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
     * @return {@code true} when the runtime is classic AEM 6.5 without the {@code LTS} version qualifier, at any
     *         release (GA, service pack, or otherwise) - Content AI Search is unsupported there. Properly branded
     *         AEM 6.5 LTS (qualifier present) is unaffected. Note: major/minor 6.5 can never also satisfy
     *         {@link #isCloudPlatform}, so no separate cloud check is needed here.
     */
    public static boolean isUnbrandedClassic65(@Nullable ProductInfoProvider productInfoProvider) {
        Version version = getVersion(productInfoProvider);
        if (version == null || version.getMajor() != 6 || version.getMinor() != 5) {
            return false;
        }
        return !StringUtils.equalsIgnoreCase(version.getQualifier(), CLASSIC_65_LTS_QUALIFIER);
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
