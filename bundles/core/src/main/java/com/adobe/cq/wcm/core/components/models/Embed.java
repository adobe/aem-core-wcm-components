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

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;

/**
 * Defines the {@code Embed} Sling Model used for the {@code /apps/core/wcm/components/embed} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.10.0
 */
@ConsumerType
public interface Embed extends Component {

    /**
     * Enumeration of the supported embed input types.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    enum Type {
        /**
         * Embed type for input from a URL.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.10.0
         */
        URL("url"),

        /**
         * Embed type for input from an embeddable.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.10.0
         */
        EMBEDDABLE("embeddable"),

        /**
         * Embed type for input from a HTML snippet.
         *
         * @since com.adobe.cq.wcm.core.components.models 12.10.0
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
         * @since com.adobe.cq.wcm.core.components.models 12.10.0
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
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_DESIGN_URL_DISABLED = "urlDisabled";

    /**
     * Name of the configuration policy property that indicates whether the embeddable input type is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_DESIGN_EMBEDDABLES_DISABLED = "embeddablesDisabled";

    /**
     * Name of the configuration policy property that indicates whether the html input type is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_DESIGN_HTML_DISABLED = "htmlDisabled";

    /**
     * Name of the configuration policy property that defines the embeddables that are allowed to be selected
     * by an author.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_DESIGN_ALLOWED_EMBEDDABLES = "allowedEmbeddables";

    /**
     * Name of the resource property that defines the embed input {@link Type}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_TYPE = "type";

    /**
     * Name of the resource property that defines the URL of an embeddable item.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_URL = "url";

    /**
     * Name of the resource property that defines an embeddable HTML snippet.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_HTML = "html";

    /**
     * Name of the resource property that defines the resource type of an embeddable.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String PN_EMBEDDABLE_RESOURCE_TYPE = "embeddableResourceType";

    /**
     * Resource type of an embeddable.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    String RT_EMBEDDABLE_V1 = "core/wcm/components/embed/v1/embed/embeddable";

    /**
     * Returns the embed input {@link Type}.
     *
     * @return the embed input {@link Type}, or {@code null} if no type is found
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    @Nullable
    default Type getType() {
        return null;
    }

    /**
     * Returns the URL of an embeddable item.
     *
     * @return The URL of the embeddable item, if type is {@link Type#URL}
     *         and the url type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    @Nullable
    default String getUrl() {
        return null;
    }

    /**
     * Returns the result from the processor that can process the given URL.
     *
     * @return The result from the processor that can process the given URL, {@code null} if processing is not possible
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    @Nullable
    default UrlProcessor.Result getResult() {
        return null;
    }

    /**
     * Returns the resource type of the embeddable.
     *
     * @return The resource type of the embeddable, if type is {@link Type#EMBEDDABLE}
     *         and the embeddable type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    @Nullable
    default String getEmbeddableResourceType() {
        return null;
    }

    /**
     * Returns an embeddable HTML snippet.
     *
     * @return An embeddable HTML {@link String}, if type is {@link Type#HTML}
     *         and the html type is not disabled via policy configuration, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.10.0
     */
    @Nullable
    default String getHtml() {
        return null;
    }

}
