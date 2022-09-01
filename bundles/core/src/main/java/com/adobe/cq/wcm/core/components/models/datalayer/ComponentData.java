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

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A base interface to be extended by components that need to enable data layer integration.
 *
 * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
 */
@ConsumerType
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ComponentData {

    /**
     * Returns the component's ID
     *
     * @return string ID
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonIgnore
    @JsonProperty("@id")
    default String getId() {
        return null;
    }

    /**
     * Returns the component's type used in the data layer
     *
     * @return type
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("@type")
    default String getType() {
        return null;
    }

    /**
     * Returns the component's last modified date using ISO 8601 standard
     *
     * @return lastModifiedDate
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("repo:modifyDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    default Date getLastModifiedDate() {
        return null;
    }


    /**
     * Returns the component's parent ID
     * 
     * @return The component's parent ID
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    default String getParentId() {
        return null;
    }

    /**
     * Returns the component's title used in the data layer
     *
     * @return src
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("dc:title")
    default String getTitle() {
        return null;
    }

    /**
     * Returns the component's description used in the data layer
     *
     * @return description
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("dc:description")
    default String getDescription() {
        return null;
    }

    /**
     * Returns the component's text used in the data layer
     *
     * @return text
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("xdm:text")
    default String getText() {
        return null;
    }

    /**
     * Returns the component's link URL used in the data layer
     *
     * @return link URL
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("xdm:linkURL")
    default String getLinkUrl() {
        return null;
    }

    /**
     * Returns the JSON string of the component's properties used in the data layer
     *
     * @return JSON string
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonIgnore
    @Nullable
    default String getJson() {
        return null;
    }

}
