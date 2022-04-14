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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.datalayer.jackson.ComponentDataModelSerializer;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A base interface to be extended by components that need to provide access to common properties.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
@ConsumerType
public interface Component extends ComponentExporter {

    /**
     * Name of the resource property that indicates the HTML id for the component.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_ID = "id";

    /**
     * Returns the HTML id of the the component's root element
     *
     * @return HTML id of the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default String getId() {
        return null;
    }

    /**
     * Returns the data layer information associated with the component
     *
     * @return {@link ComponentData} object associated with the component
     *
     * @since com.adobe.cq.wcm.core.components.models 12.12.0
     */
    @Nullable
    @JsonProperty("dataLayer")
    @JsonSerialize(using = ComponentDataModelSerializer.class)
    default ComponentData getData() {
        return null;
    }
    
    /**
     * Returns the style system information associated with the component
     *
     * @return CSS classes selected by the content author delimited using a SPACE character
     *
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    @Nullable
    @JsonProperty("appliedCssClassNames")
    default String getAppliedCssClasses() {
        return null;
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }

}
