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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.AemCloudPlatformDetector;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearchV2;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.granite.license.ProductInfoProvider;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ContentAISupportedSearchV2.class, ContentAISupportedSearch.class, ComponentExporter.class},
    resourceType = ContentAISupportedSearchV2Impl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentAISupportedSearchV2Impl extends AbstractComponentImpl implements ContentAISupportedSearchV2 {

    protected static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v2/contentaisearch";

    public static final int PROP_RESULTS_SIZE_DEFAULT = 12;
    public static final String PROP_RESULTS_LAYOUT_DEFAULT = ContentAISupportedSearch.RESULTS_LAYOUT_CARD;

    @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
    private ProductInfoProvider productInfoProvider;

    @ValueMapValue
    @Default(values = "")
    private String contentSource;

    @ValueMapValue(name = PN_CONTENT_SOURCE_TYPE)
    @Default(values = ContentAISupportedSearch.DEFAULT_CONTENT_SOURCE_TYPE)
    private String contentSourceType;

    @ValueMapValue(name = PN_CONTENT_SOURCES, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] contentSources;

    @ValueMapValue(name = PN_PRIMARY_CONTENT_SOURCE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = "")
    private String primaryContentSource;

    @ValueMapValue
    @Default(intValues = PROP_RESULTS_SIZE_DEFAULT)
    private int resultsSize;

    @ValueMapValue(name = PN_RESULTS_LAYOUT)
    @Default(values = PROP_RESULTS_LAYOUT_DEFAULT)
    private String resultsLayout;

    @ValueMapValue(name = PN_AI_SEARCH_MODE_ENABLED, injectionStrategy = InjectionStrategy.OPTIONAL)
    private Boolean aiSearchModeEnabledProperty;

    @ValueMapValue(name = PN_GENSEARCH_ERROR_RETRY_VISIBLE)
    @Default(booleanValues = true)
    private boolean genSearchErrorRetryVisible;

    @ValueMapValue
    @Default(values = "")
    private String placeholder;

    @ValueMapValue
    @Default(values = "")
    private String disclaimerText;

    private List<String> resolvedContentSources = Collections.emptyList();
    private String resolvedPrimaryContentSource = "";
    private boolean aiSearchModeEnabled;

    @PostConstruct
    private void initModel() {
        resolvedContentSources = resolveContentSources();
        resolvedPrimaryContentSource = resolvePrimaryContentSource(resolvedContentSources);
        aiSearchModeEnabled = resolveAiSearchModeEnabled();
    }

    private boolean resolveAiSearchModeEnabled() {
        if (!AemCloudPlatformDetector.isCloudPlatform(productInfoProvider)) {
            return false;
        }
        return aiSearchModeEnabledProperty == null || aiSearchModeEnabledProperty.booleanValue();
    }

    @NotNull
    @Override
    public String getContentSource() {
        return resolvedPrimaryContentSource;
    }

    @NotNull
    @Override
    public String getContentSourceType() {
        return StringUtils.defaultIfBlank(contentSourceType, ContentAISupportedSearch.DEFAULT_CONTENT_SOURCE_TYPE);
    }

    @NotNull
    @Override
    public List<String> getContentSources() {
        return resolvedContentSources;
    }

    @NotNull
    @Override
    public String getPrimaryContentSource() {
        return resolvedPrimaryContentSource;
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @NotNull
    @Override
    public String getResultsLayout() {
        if (ContentAISupportedSearch.RESULTS_LAYOUT_LIST.equals(resultsLayout)) {
            return ContentAISupportedSearch.RESULTS_LAYOUT_LIST;
        }
        return ContentAISupportedSearch.RESULTS_LAYOUT_CARD;
    }

    @Override
    public boolean isAiSearchModeEnabled() {
        return aiSearchModeEnabled;
    }

    @Override
    public boolean isGenSearchErrorRetryVisible() {
        return genSearchErrorRetryVisible;
    }

    @Override
    public String getPlaceholder() {
        return StringUtils.isBlank(placeholder) ? null : placeholder;
    }

    @Override
    public String getDisclaimerText() {
        return StringUtils.isBlank(disclaimerText) ? null : disclaimerText;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @NotNull
    private List<String> resolveContentSources() {
        List<String> sources = new ArrayList<>();
        if (contentSources != null) {
            sources.addAll(Arrays.stream(contentSources)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toList()));
        }
        if (sources.isEmpty() && StringUtils.isNotBlank(contentSource)) {
            sources.add(contentSource.trim());
        }
        return Collections.unmodifiableList(sources);
    }

    @NotNull
    private String resolvePrimaryContentSource(@NotNull List<String> sources) {
        if (StringUtils.isNotBlank(primaryContentSource)) {
            return primaryContentSource.trim();
        }
        if (!sources.isEmpty()) {
            return sources.get(0);
        }
        return "";
    }
}
