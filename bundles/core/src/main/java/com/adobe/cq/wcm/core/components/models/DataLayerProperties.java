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
     * Indicates if the component will populate the dataLayer
     *
     * @return {@code true} if the model will populate the dataLayer
     */
    @JsonIgnore
    default boolean isDataLayerEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a resource's asset (ex {@link Image})
     *
     * @return {@code Resource} of the asset
     */
    @JsonIgnore
    default Resource getAssetResource() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's ID
     *
     * @return string ID
     */
    @JsonIgnore
    default String getDataLayerId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's type
     *
     * @return type
     */
    @JsonIgnore
    default String getDataLayerType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's link (ex {@link Image})
     *
     * @return src
     */
    @JsonIgnore
    default String getDataLayerTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's text where it applies (ex {@link Text})
     *
     * @return text
     */
    @JsonIgnore
    default String getDataLayerText() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's tags where it applies (ex {@link Page})
     *
     * @return tags array
     */
    @JsonIgnore
    default String[] getDataLayerTags() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a model's link where a URL is present
     *
     * @return link URL
     */
    @JsonIgnore
    default String getDataLayerLinkUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Method used to return a page's template path
     *
     * @return JCR template path
     */
    @JsonIgnore
    default String getDataLayerTemplatePath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get a model's language where it applies (ex {@link Page}
     *
     * @return language
     */
    @JsonIgnore
    default String getDataLayerLanguage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get an {@link Carousel} or {@link Tabs} active item IDs
     *
     * @return item ID
     */
    @JsonIgnore
    default String[] getDataLayerShownItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * Used to generate a JSON string of the dataLayer representation of the model
     *
     * @return JSON string
     */
    @JsonIgnore
    default String getDataLayerString() {
        throw new UnsupportedOperationException();
    }

    /**
     * Used to generate a Map of the dataLayer representation of the model
     *
     * @return dataLayer Map
     */
    default Map<String, ?> getDataLayerJson() {
        throw new UnsupportedOperationException();
    }
}
