/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Accordion} Sling Model used for the {@code /apps/core/wcm/components/accordion} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
@ConsumerType
public interface Accordion extends Container {

    /**
     * Returns the expanded item.
     *
     * @return The expanded item
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getExpandedItem() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the heading type.
     *
     * @return the heading type
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getHeadingType() {
        throw new UnsupportedOperationException();
    }
}
