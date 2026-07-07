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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
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
    public static final boolean PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT = true;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    @Default(values = "")
    private String contentSource;

    @ValueMapValue
    @Default(intValues = PROP_RESULTS_SIZE_DEFAULT)
    private int resultsSize;

    @ValueMapValue
    @Default(booleanValues = PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT)
    private boolean genSearchEnabledByDefault;

    @ValueMapValue
    @Default(values = "")
    private String placeholder;

    @ValueMapValue
    @Default(values = "")
    private String disclaimerText;

    private final Map<String, String> i18nMessagesMap = new HashMap<>();

    @PostConstruct
    private void initModel() {
        // no-op for now; kept for parity with SearchImpl's initModel pattern and future extension
    }

    @NotNull
    @Override
    public String getContentSource() {
        return contentSource;
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @Override
    public boolean isGenSearchEnabledByDefault() {
        return genSearchEnabledByDefault;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String getDisclaimerText() {
        return disclaimerText;
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
        Locale pageLocale = currentPage.getLanguage(false);
        ResourceBundle resourceBundle = request.getResourceBundle(pageLocale);
        I18n i18n = new I18n(resourceBundle);
        i18nMessagesMap.put("Search", i18n.get("Search"));
        i18nMessagesMap.put("Clear", i18n.get("Clear"));
        i18nMessagesMap.put("AI-generated responses may be inaccurate. Verify important information.",
            i18n.get("AI-generated responses may be inaccurate. Verify important information."));
        try {
            return new ObjectMapper().writeValueAsString(i18nMessagesMap);
        } catch (Exception e) {
            return "{}";
        }
    }
}
