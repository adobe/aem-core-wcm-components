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

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Interface for a single item in a container.
 * Every container item has at least a resource.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.27.0
 */
@ConsumerType
public interface ContainerItem {

    /**
     * Gets the resource for this container item.
     *
     * @return The resource for this container item.
     * @since com.adobe.cq.wcm.core.components.models 12.27.0
     */
    @Nullable
    @JsonIgnore
    default Resource getResource() {
        return null;
    }

    /**
     * Gets the name of this container item.
     *
     * @return The name of this container item.
     * @since com.adobe.cq.wcm.core.components.models 12.27.0
     */
    @NotNull
    default String getName() {
        return "";
    }

    /**
     * Returns the path of this container item.
     *
     * @return the container item path or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.28.0
     */
    @Nullable
    default String getPath() {
        return null;
    }

}
