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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A base interface to be extended by components that need to enable dataLayer population.
 */
@ConsumerType
public interface DataLayerProperties {

    /**
     * Checks if the data layer is enabled
     *
     * @return {@code true} if the model will populate the dataLayer
     */
    @JsonIgnore
    default boolean isDataLayerEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's asset
     *
     * @return {@code Resource} of the asset
     */
    @JsonIgnore
    default Resource getAssetResource() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's ID used in the data layer
     *
     * @return string ID
     */
    @JsonIgnore
    default String getDataLayerId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's type used in the data layer
     *
     * @return type
     */
    @JsonIgnore
    default String getDataLayerType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's title used in the data layer
     *
     * @return src
     */
    @JsonIgnore
    default String getDataLayerTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's description used in the data layer
     *
     * @return description
     */
    @JsonIgnore
    default String getDataLayerDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's last modified date using ISO 8601 standard
     *
     * @return lastModifiedDate
     */
    @JsonIgnore
    default String getDataLayerLastModifiedDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's text used in the data layer
     *
     * @return text
     */
    @JsonIgnore
    default String getDataLayerText() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's tags used in the data layer
     *
     * @return tags array
     */
    @JsonIgnore
    default String[] getDataLayerTags() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the resource's URL used in the data layer
     *
     * @return link URL
     */
    @JsonIgnore
    default String getDataLayerLinkUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the page template used in the data layer
     *
     * @return JCR template path
     */
    @JsonIgnore
    default String getDataLayerTemplatePath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the page language used in the data layer
     *
     * @return language
     */
    @JsonIgnore
    default String getDataLayerLanguage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the shown items of the Carousel, Accordion or Tabs used in the data layer
     *
     * @return item ID
     */
    @JsonIgnore
    default String[] getDataLayerShownItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the JSON string of the resource's properties used in the data layer
     *
     * @return JSON string
     */
    @JsonIgnore
    default String getDataLayerString() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the Map of the resource's properties used in the data layer
     *
     * @return dataLayer Map
     */
    default Map<String, ?> getDataLayerJson() {
        throw new UnsupportedOperationException();
    }
}
