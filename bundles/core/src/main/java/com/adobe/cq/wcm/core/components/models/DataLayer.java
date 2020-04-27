/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.internal.jackson.DataLayerSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A base interface to be extended by components that need to enable dataLayer population.
 */
@ConsumerType
@JsonSerialize(using = DataLayerSerializer.class)
public interface DataLayer {

    /**
     * Checks if the data layer is enabled
     *
     * @return {@code true} if the model will populate the dataLayer
     */
    @JsonIgnore
    default boolean isEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's asset
     *
     * @return {@code Resource} of the asset
     */
    default Resource getAssetResource() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's ID used in the data layer
     *
     * @return string ID
     */
    default String getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's type used in the data layer
     *
     * @return type
     */
    default String getType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's title used in the data layer
     *
     * @return src
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's description used in the data layer
     *
     * @return description
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's last modified date using ISO 8601 standard
     *
     * @return lastModifiedDate
     */
    default String getLastModifiedDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's text used in the data layer
     *
     * @return text
     */
    default String getText() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's tags used in the data layer
     *
     * @return tags array
     */
    default String[] getTags() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's URL used in the data layer
     *
     * @return link URL
     */
    default String getUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's link URL used in the data layer
     *
     * @return link URL
     */
    default String getLinkUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the page template used in the data layer
     *
     * @return JCR template path
     */
    default String getTemplatePath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the page language used in the data layer
     *
     * @return language
     */
    default String getLanguage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the shown items of the Carousel, Accordion or Tabs used in the data layer
     *
     * @return item ID
     */
    default String[] getShownItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the JSON string of the resource's properties used in the data layer
     *
     * @return JSON string
     */
    @JsonIgnore
    default String getString() {
        throw new UnsupportedOperationException();
    }

}
