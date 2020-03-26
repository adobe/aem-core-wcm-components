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

import java.util.Calendar;
import java.util.Set;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code CloudConfig} Sling Model used for the Cloud Configuration view.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.14.0
 */
@ConsumerType
public interface CloudConfig {

    /**
     * @return title of the cloud configuration resource
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return last modification date of the cloud configuration resource
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default Calendar getLastModifiedDate() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return {@code true} if the cloud configuration resource has children, otherwise {@code false}
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default boolean hasChildren() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return {@code true} if current resource is a folder, otherwise {@code false}
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default boolean isFolder() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return return set of allowed actions
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default Set<String> getActionsRels() {
        throw new UnsupportedOperationException();
    }
}
