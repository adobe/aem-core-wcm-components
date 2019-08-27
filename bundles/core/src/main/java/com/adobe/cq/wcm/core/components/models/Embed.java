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
package com.adobe.cq.wcm.core.components.models;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code Embed} Sling Model used for the {@code /apps/core/wcm/components/embed} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.9.0
 */
@ConsumerType
public interface Embed extends ComponentExporter {

    /**
     * Enumeration of the supported embed input types.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    enum Type {
        /**
         * Embed type for input from a URL.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        URL("url"),

        /**
         * Embed type for input from an embeddable.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        EMBEDDABLE("embeddable"),

        /**
         * Embed type for input from a HTML snippet.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        HTML("html");

        private String value;

        Type(String value) {
            this.value = value;
        }

        /**
         * Given a {@link String} {@code value}, this method returns the enum's value that corresponds to
         * the provided string representation. If no representation is found, {@code null} will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, {@code null} otherwise
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        public static Type fromString(String value) {
            for (Type type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * Name of the configuration policy property that indicates whether the url input type is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_DESIGN_URL_DISABLED = "urlDisabled";

    /**
     * Name of the configuration policy property that indicates whether the embeddable input type is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_DESIGN_EMBEDDABLES_DISABLED = "embeddablesDisabled";

    /**
     * Name of the configuration policy property that indicates whether the html input type is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_DESIGN_HTML_DISABLED = "htmlDisabled";

    /**
     * Name of the configuration policy property that defines the embeddables that are allowed to be selected
     * by an author.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_DESIGN_ALLOWED_EMBEDDABLES = "allowedEmbeddables";

    /**
     * Name of the resource property that defines the embed input {@link Type}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_TYPE = "type";

    /**
     * Name of the resource property that defines the URL of an embeddable item.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_URL = "url";

    /**
     * Name of the resource property that defines an embeddable HTML snippet.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_HTML = "html";

    /**
     * Name of the resource property that defines the resource type of an embeddable.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_EMBEDDABLE_RESOURCE_TYPE = "embeddableResourceType";

    /**
     * Resource type of an embeddable.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String RT_EMBEDDABLE = "core/wcm/components/embed/embeddable";

    /**
     * Returns the embed input {@link Type}.
     *
     * @return the embed input {@link Type}, or {@code null} if no type is found
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @Nullable
    default Type getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the URL of an embeddable item.
     *
     * @return The URL of the embeddable item, if type is {@link Type#URL}
     *         and the url type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @Nullable
    default String getUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the result from the processor that can process the given URL.
     *
     * @return The result from the processor that can process the given URL, {@code null} if processing is not possible
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @Nullable
    default Embed.UrlProcessor.Result getResult() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource type of the embeddable.
     *
     * @return The resource type of the embeddable, if type is {@link Type#EMBEDDABLE}
     *         and the embeddable type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @Nullable
    default String getEmbeddableResourceType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an embeddable HTML snippet.
     *
     * @return An embeddable HTML {@link String}, if type is {@link Type#HTML}
     *         and the html type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @Nullable
    default String getHtml() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Interface that defines a generic processor for a given URL
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    interface UrlProcessor {

        /**
         * Returns the result of processing the given URL, {@code null} if processing is not possible or failed.
         *
         * @param url The URL to process
         * @return The {@link Result} of processing, {@code null} if processing is not possible or failed.
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        default Result process(String url) {
            throw new UnsupportedOperationException();
        }

        /**
         * @since com.adobe.cq.wcm.core.components.models 12.9.0
         */
        interface Result {

            /**
             * Returns the name of the processor that was able to process the URL.
             *
             * @return Name of the processor.
             * @since com.adobe.cq.wcm.core.components.models 12.9.0
             */
            default String getProcessor() {
                throw new UnsupportedOperationException();
            }

            /**
             * Returns the data from the processor that was able to process the URL.
             *
             * @return Data from the processor that was able to process the URL.
             * @since com.adobe.cq.wcm.core.components.models 12.9.0
             */
            default Map<String, Object> getOptions() {
                throw new UnsupportedOperationException();
            }
        }

    }
}
