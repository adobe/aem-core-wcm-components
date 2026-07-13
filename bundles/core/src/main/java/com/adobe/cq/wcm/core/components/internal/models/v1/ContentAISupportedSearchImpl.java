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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.granite.license.ProductInfoProvider;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ContentAISupportedSearch.class, ComponentExporter.class},
    resourceType = ContentAISupportedSearchImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentAISupportedSearchImpl extends AbstractComponentImpl implements ContentAISupportedSearch {

    protected static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/contentaisearch";

    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;
    public static final String PROP_RESULTS_LAYOUT_DEFAULT = ContentAISupportedSearch.RESULTS_LAYOUT_CARD;
    public static final boolean PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT = true;

    @OSGiService
    private ProductInfoProvider productInfoProvider;

    @ValueMapValue
    @Default(values = "")
    private String contentSource;

    @ValueMapValue(name = PN_CONTENT_SOURCE_TYPE)
    @Default(values = ContentAIClient.DEFAULT_CONTENT_SOURCE_TYPE)
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

    @ValueMapValue
    @Default(booleanValues = PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT)
    private boolean genSearchEnabledByDefault;

    @ValueMapValue(name = PN_GENSEARCH_TOGGLE_VISIBLE, injectionStrategy = InjectionStrategy.OPTIONAL)
    private Boolean genSearchToggleVisibleProperty;

    @ValueMapValue
    @Default(values = GENSEARCH_ERROR_FALLBACK_RESULTS_ONLY)
    private String genSearchErrorFallback;

    @ValueMapValue
    @Default(values = "")
    private String placeholder;

    @ValueMapValue
    @Default(values = "")
    private String disclaimerText;

    private List<String> resolvedContentSources = Collections.emptyList();
    private String resolvedPrimaryContentSource = "";
    private boolean genSearchToggleVisible;

    private final Map<String, String> i18nMessagesMap = new HashMap<>();

    @PostConstruct
    private void initModel() {
        resolvedContentSources = resolveContentSources();
        resolvedPrimaryContentSource = resolvePrimaryContentSource(resolvedContentSources);
        genSearchToggleVisible = resolveGenSearchToggleVisible();
    }

    private boolean resolveGenSearchToggleVisible() {
        if (!AemCloudPlatformDetector.isCloudPlatform(productInfoProvider)) {
            return false;
        }
        return genSearchToggleVisibleProperty == null || genSearchToggleVisibleProperty.booleanValue();
    }

    @NotNull
    @Override
    public String getContentSource() {
        return resolvedPrimaryContentSource;
    }

    @NotNull
    @Override
    public String getContentSourceType() {
        return StringUtils.defaultIfBlank(contentSourceType, ContentAIClient.DEFAULT_CONTENT_SOURCE_TYPE);
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
    public boolean isGenSearchEnabledByDefault() {
        return genSearchEnabledByDefault;
    }

    @Override
    public boolean isGenSearchToggleVisible() {
        return genSearchToggleVisible;
    }

    @NotNull
    @Override
    public String getGenSearchErrorFallback() {
        return StringUtils.defaultIfBlank(genSearchErrorFallback, GENSEARCH_ERROR_FALLBACK_RESULTS_ONLY);
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

    @JsonIgnore
    @NotNull
    @Override
    public String getI18nMessages() {
        Page page = getCurrentPage();
        Locale pageLocale = page != null ? page.getLanguage(false) : request.getLocale();
        ResourceBundle resourceBundle = request.getResourceBundle(pageLocale);
        I18n i18n = new I18n(resourceBundle);
        i18nMessagesMap.put("Search", i18n.get("Search"));
        i18nMessagesMap.put("Clear", i18n.get("Clear"));
        i18nMessagesMap.put("AI-generated responses may be inaccurate. Verify important information.",
            i18n.get("AI-generated responses may be inaccurate. Verify important information."));
        i18nMessagesMap.put("Generative answer", i18n.get("Generative answer"));
        i18nMessagesMap.put("Generating answer...", i18n.get("Generating answer..."));
        i18nMessagesMap.put("Powered by Content AI", i18n.get("Powered by Content AI"));
        i18nMessagesMap.put("Sources", i18n.get("Sources"));
        i18nMessagesMap.put("Results per page", i18n.get("Results per page"));
        i18nMessagesMap.put("Search results", i18n.get("Search results"));
        i18nMessagesMap.put("Cards", i18n.get("Cards"));
        i18nMessagesMap.put("List", i18n.get("List"));
        i18nMessagesMap.put("Results layout", i18n.get("Results layout"));
        i18nMessagesMap.put("Previous", i18n.get("Previous"));
        i18nMessagesMap.put("Next", i18n.get("Next"));
        i18nMessagesMap.put("Page {0} of {1}", i18n.get("Page {0} of {1}"));
        i18nMessagesMap.put("Showing {0}-{1} of {2}", i18n.get("Showing {0}-{1} of {2}"));
        try {
            return new ObjectMapper().writeValueAsString(i18nMessagesMap);
        } catch (Exception e) {
            return "{}";
        }
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
