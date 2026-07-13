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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.osgi.framework.Version;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Search;
import com.adobe.granite.license.ProductInfo;
import com.adobe.granite.license.ProductInfoProvider;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Search model implementation for v3.
 */
@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Search.class, ComponentExporter.class},
       resourceType = SearchImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SearchImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.SearchImpl {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE = "core/wcm/components/search/v3/search";

    /**
     * Minimum major version for AEM as a Cloud Service release train versioning.
     * Cloud author instances report calendar-year based versions (e.g. 2026.x, 2030.x).
     */
    private static final int CLOUD_RELEASE_VERSION_MAJOR_MIN = 2020;

    /**
     * Minimum classic semver version reported by AEM as a Cloud Service publish tier.
     */
    private static final Version MIN_CLOUD_CLASSIC_VERSION = new Version("6.6.0");

    @OSGiService
    private ProductInfoProvider productInfoProvider;

    @ScriptVariable
    private Style currentStyle;

    private boolean hideAiSearchToggle;

    @PostConstruct
    private void initV3Model() {
        boolean hideByDefault = !isCloudPlatform();
        if (currentStyle != null) {
            hideAiSearchToggle = currentStyle.get(PN_HIDE_AI_SEARCH_TOGGLE, hideByDefault);
        } else {
            hideAiSearchToggle = hideByDefault;
        }
    }

    /**
     * Returns whether the runtime is AEM as a Cloud Service.
     * Cloud author reports calendar-year release train versions and publish may report classic semver.
     */
    private boolean isCloudPlatform() {
        if (productInfoProvider == null) {
            return false;
        }
        ProductInfo productInfo = productInfoProvider.getProductInfo();
        if (productInfo == null) {
            return false;
        }
        Version version = productInfo.getVersion();
        if (version == null) {
            return false;
        }
        if (version.getMajor() >= CLOUD_RELEASE_VERSION_MAJOR_MIN) {
            return true;
        }
        return version.compareTo(MIN_CLOUD_CLASSIC_VERSION) >= 0;
    }

    @Override
    @JsonIgnore
    public boolean hideAiSearchToggle() {
        return hideAiSearchToggle;
    }
}
