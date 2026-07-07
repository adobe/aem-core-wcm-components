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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the {@code ContentAISupportedSearch} Sling Model used for the
 * {@code /apps/core/wcm/components/contentaisearch} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 1.0.0
 */
public interface ContentAISupportedSearch extends Component {

    String PN_CONTENT_SOURCE = "contentSource";
    String PN_RESULTS_SIZE = "resultsSize";
    String PN_GENSEARCH_ENABLED_BY_DEFAULT = "genSearchEnabledByDefault";
    String PN_PLACEHOLDER = "placeholder";
    String PN_DISCLAIMER_TEXT = "disclaimerText";

    /**
     * @return the name of the Content AI content source this component queries.
     */
    @NotNull
    default String getContentSource() {
        return "";
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
