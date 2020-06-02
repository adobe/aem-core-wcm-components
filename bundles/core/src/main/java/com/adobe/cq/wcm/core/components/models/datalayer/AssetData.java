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
package com.adobe.cq.wcm.core.components.models.datalayer;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Interface defining data for assets.
 *
 * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface AssetData {

    /**
     * Returns the JCR id of the asset
     *
     * @return Asset JCR id
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("repo:id")
    default String getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the asset URL
     *
     * @return Asset URL
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("repo:path")
    default String getUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the asset format
     *
     * @return Asset format
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("@type")
    default String getFormat() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the asset tags
     *
     * @return Asset tags
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("xdm:tags")
    default String[] getTags() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the asset last modified date
     *
     * @return Asset last modified date
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("repo:modifyDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    default Date getLastModifiedDate() {
        throw new UnsupportedOperationException();
    }
}
