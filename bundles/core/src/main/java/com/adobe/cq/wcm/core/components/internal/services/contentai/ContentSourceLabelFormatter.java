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
package com.adobe.cq.wcm.core.components.internal.services.contentai;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Formats author-dialog labels for Content AI content source options.
 */
public final class ContentSourceLabelFormatter {

    public static final int DESCRIPTION_LABEL_MAX_LENGTH = 80;

    private static final String DESCRIPTION_SEPARATOR = " — ";

    private ContentSourceLabelFormatter() {
    }

    /**
     * @param name the content source name
     * @param id   optional fallback identifier
     * @return the index name to persist and send to Content AI
     */
    @NotNull
    public static String resolveIndexName(@Nullable String name, @Nullable String id) {
        if (StringUtils.isNotBlank(name)) {
            return name.trim();
        }
        if (StringUtils.isNotBlank(id)) {
            return id.trim();
        }
        return "";
    }

    /**
     * @param indexName   the resolved index name
     * @param description optional description from the list API
     * @return the author-visible dropdown label
     */
    @NotNull
    public static String formatLabel(@NotNull String indexName, @Nullable String description) {
        if (StringUtils.isBlank(description)) {
            return indexName;
        }
        String trimmed = description.trim();
        if (trimmed.length() <= DESCRIPTION_LABEL_MAX_LENGTH) {
            return indexName + DESCRIPTION_SEPARATOR + trimmed;
        }
        return indexName + DESCRIPTION_SEPARATOR
            + trimmed.substring(0, DESCRIPTION_LABEL_MAX_LENGTH) + "...";
    }
}
