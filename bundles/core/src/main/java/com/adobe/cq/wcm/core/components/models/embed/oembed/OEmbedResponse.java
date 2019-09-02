/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.models.embed.oembed;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the oEmbed HTTP response object.
 *
 * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
 */
@ConsumerType
public interface OEmbedResponse {

    /**
     * Enumeration of oEmbed response formats
     */
    enum Format {
        /**
         * JSON response format.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        JSON("json"),

        /**
         * XML response format.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        XML("xml");

        private String value;

        Format(String value) {
            this.value = value;
        }

        public static Format fromString(String value) {
            for (Format format : values()) {
                if (format.value.equals(value)) {
                    return format;
                }
            }
            return JSON;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Enumeration of oEmbed response types
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    enum Type {
        /**
         * oEmbed type for photos.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        PHOTO("photo"),

        /**
         * oEmbed type for video players.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        VIDEO("video"),

        /**
         * oEmbed type for generic embed data (such as title or author name), does not provide url or html parameters.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        LINK("link"),

        /**
         * oEmbed type for rich HTML content that does not fall under any other type.
         *
         * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
         */
        RICH("rich");

        private String value;

        Type(String value) {
            this.value = value;
        }

        public static Type fromString(String value) {
            for (Type type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return RICH;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * The resource type.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @NotNull
    default String getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * The oEmbed version number.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @NotNull
    default String getVersion() {
        throw new UnsupportedOperationException();
    }

    /**
     * A text title, describing the resource.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * The name of the author/owner of the resource.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getAuthorName() {
        throw new UnsupportedOperationException();
    }

    /**
     * A URL for the author/owner of the resource.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getAuthorUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * The name of the resource provider.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getProviderName() {
        throw new UnsupportedOperationException();
    }

    /**
     * The url of the resource provider.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getProviderUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * The suggested cache lifetime for this resource, in seconds.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default Long getCacheAge() {
        throw new UnsupportedOperationException();
    }

    /**
     * A URL to a thumbnail image representing the resource.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getThumbnailUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * The width of the optional thumbnail.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getThumbnailWidth() {
        throw new UnsupportedOperationException();
    }

    /**
     * The height of the optional thumbnail.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getThumbnailHeight() {
        throw new UnsupportedOperationException();
    }

    /**
     * The width in pixels required to display the HTML.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getWidth() {
        throw new UnsupportedOperationException();
    }

    /**
     * The height in pixels required to display the HTML.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getHeight() {
        throw new UnsupportedOperationException();
    }

    /**
     * The HTML required to display the resource.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getHtml() {
        throw new UnsupportedOperationException();
    }

    /**
     * The source URL of the image.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    @Nullable
    default String getUrl() {
        throw new UnsupportedOperationException();
    }
}
