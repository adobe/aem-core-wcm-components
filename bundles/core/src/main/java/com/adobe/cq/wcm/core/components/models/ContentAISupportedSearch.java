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
package com.adobe.cq.wcm.core.components.models;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;

/**
 * Defines the {@code ContentAISupportedSearch} Sling Model used for the
 * {@code /apps/core/wcm/components/contentaisearch} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 1.0.0
 */
public interface ContentAISupportedSearch extends Component {

    String PN_CONTENT_SOURCE = "contentSource";
    String PN_CONTENT_SOURCE_TYPE = "contentSourceType";
    String PN_CONTENT_SOURCES = "contentSources";
    String PN_PRIMARY_CONTENT_SOURCE = "primaryContentSource";
    String PN_RESULTS_SIZE = "resultsSize";
    String PN_GENSEARCH_ENABLED_BY_DEFAULT = "genSearchEnabledByDefault";
    String PN_GENSEARCH_TOGGLE_VISIBLE = "genSearchToggleVisible";
    String PN_GENSEARCH_ERROR_FALLBACK = "genSearchErrorFallback";
    String PN_PLACEHOLDER = "placeholder";
    String PN_DISCLAIMER_TEXT = "disclaimerText";

    String GENSEARCH_ERROR_FALLBACK_RESULTS_ONLY = "RESULTS_ONLY";
    String GENSEARCH_ERROR_FALLBACK_SHOW_ERROR = "SHOW_ERROR";
    String GENSEARCH_ERROR_FALLBACK_SHOW_ERROR_MESSAGE = "SHOW_ERROR_MESSAGE";

    /**
     * @return the primary Content AI content source name (first configured source).
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    @NotNull
    default String getContentSource() {
        return getPrimaryContentSource();
    }

    /**
     * @return the configured Content AI content source type.
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    @NotNull
    default String getContentSourceType() {
        return ContentAIClient.DEFAULT_CONTENT_SOURCE_TYPE;
    }

    /**
     * @return the configured Content AI content source names.
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    @NotNull
    default List<String> getContentSources() {
        return Collections.emptyList();
    }

    /**
     * @return the primary content source used for generative search.
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    @NotNull
    default String getPrimaryContentSource() {
        List<String> sources = getContentSources();
        return sources.isEmpty() ? "" : sources.get(0);
    }

    /**
     * @return the maximum number of results to fetch from the results list.
     */
    default int getResultsSize() {
        return 0;
    }

    /**
     * @return whether the generative-summary toggle should default to on.
     */
    default boolean isGenSearchEnabledByDefault() {
        return true;
    }

    /**
     * @return whether the visitor-facing generative search toggle is rendered.
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    default boolean isGenSearchToggleVisible() {
        return true;
    }

    /**
     * @return the visitor-facing fallback when generative search fails.
     * @since com.adobe.cq.wcm.core.components.models 12.32.0
     */
    @NotNull
    default String getGenSearchErrorFallback() {
        return GENSEARCH_ERROR_FALLBACK_RESULTS_ONLY;
    }

    /**
     * @return the placeholder text for the search input, or {@code null} if not configured.
     */
    @Nullable
    default String getPlaceholder() {
        return null;
    }

    /**
     * @return the disclaimer text shown below the generative summary, or {@code null} to use the default i18n string.
     */
    @Nullable
    default String getDisclaimerText() {
        return null;
    }

    /**
     * @return a JSON string of localized messages for client-side use.
     */
    @NotNull
    default String getI18nMessages() {
        return "{}";
    }
}
