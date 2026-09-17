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

/**
 * Defines the {@code ContentAISupportedSearchV2} Sling Model used for the
 * {@code /apps/core/wcm/components/contentaisearch/v2} component.
 *
 * <p>Extends {@link ContentAISupportedSearch} rather than sharing it directly: v2 replaces the
 * visitor-facing generative-summary <em>checkbox toggle</em> with two mutually-exclusive tabs
 * (Search Results / AI Mode), which removes {@code genSearchEnabledByDefault} entirely and
 * repurposes {@code genSearchToggleVisible}/{@code genSearchErrorFallback} into differently-shaped
 * properties below — a bigger contract change than this project's more common v1/v2
 * shared-interface-with-additive-default-methods pattern is meant for.</p>
 *
 * @since com.adobe.cq.wcm.core.components.models 12.33.0
 */
public interface ContentAISupportedSearchV2 extends ContentAISupportedSearch {

    String PN_AI_SEARCH_MODE_ENABLED = "aiSearchModeEnabled";
    String PN_GENSEARCH_ERROR_RETRY_VISIBLE = "genSearchErrorRetryVisible";

    /**
     * @return whether the "AI Mode" tab is rendered at all. When {@code false}, the component
     *         shows only a plain Search Results view with no tab bar and never calls the
     *         generative-search endpoint. Only available on AEM as a Cloud Service; always
     *         {@code false} on classic AEM regardless of author configuration (same platform
     *         gating as v1's {@code genSearchToggleVisible}).
     */
    default boolean isAiSearchModeEnabled() {
        return true;
    }

    /**
     * @return whether a retry button is shown alongside the AI Mode tab's error message when
     *         generative search fails. The error message itself is always shown on failure;
     *         this only controls the retry button.
     */
    default boolean isGenSearchErrorRetryVisible() {
        return true;
    }
}
